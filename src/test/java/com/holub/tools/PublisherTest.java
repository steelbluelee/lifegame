package com.holub.tools;

import org.junit.*;
import static org.assertj.core.api.Assertions.*;

public class PublisherTest {

    interface Observer {
        void notify(String arg);
    }

    class Notifier {
        private Publisher publisher = new Publisher();

        public void addObserver(Observer l) {
            publisher.subscribe(l);
        }
        
        public void removeObserver(Observer l) {
            publisher.cancelSubscription(l);
        }

        public void fire(final String arg) {
            publisher.publish(
                    new Publisher.Distributor() {
                        public void deliverTo(Object subscriber) {
                            ((Observer)subscriber).notify(arg);
                        }
                    }
            );
        }
    }

    Notifier source = new Notifier();
    StringBuffer actualResults = new StringBuffer();
    StringBuffer expectedResults = new StringBuffer();
    Observer listener1 =
                    new Observer() {
                        public void notify(String arg) {
                            actualResults.append("1[" + arg + "]");
                        }
                    };
    Observer listener2 =
                    new Observer() {
                        public void notify(String arg) {
                            actualResults.append("2[" + arg + "]");
                        }
                    };

    @Before public void setUp() {
        source.addObserver(listener1);
        source.addObserver(listener2);
    }

    @Test
    public void test_publish() {
        source.fire("a");
        source.fire("b");

        expectedResults.append("2[a]");
        expectedResults.append("1[a]");
        expectedResults.append("2[b]");
        expectedResults.append("1[b]");

        assertThat(actualResults.toString()).isEqualTo(expectedResults.toString());
    }

    @Test
    public void test_cancelSubscription() {
        source.removeObserver(listener1);

        source.fire("c");

        expectedResults.append("2[c]");

        assertThat(actualResults.toString()).isEqualTo(expectedResults.toString());
    }

    @Test(expected=java.util.NoSuchElementException.class)
    public void test_cancelSubscription_with_not_existing_observer_throw_() {
        source.removeObserver(listener1);
        source.removeObserver(listener1);
    }
} 
