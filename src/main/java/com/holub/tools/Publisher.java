package com.holub.tools;

public class Publisher {

    public interface Distributor {
        public void deliverTo(Object subscriber);
    }

    public class Node {
        public final Object subscriber;
        public final Node next;

        private Node(Object subscriber, Node next) {
            this.subscriber = subscriber;
            this.next = next;
        }

        public Node remove(Object target) {
            if (target == subscriber)
                return next;

            if (next == null)
                throw new java.util.NoSuchElementException(target.toString());

            return new Node(subscriber, next.remove(target));
        }

        public void accept(Distributor deliveryAgnet) {
            deliveryAgnet.deliverTo(subscriber);
        }
    } 

    private volatile Node subscribers = null;

    public void publish(Distributor deliveryAgnet) {
        for (Node cursor = subscribers; cursor != null; cursor = cursor.next) {
            cursor.accept(deliveryAgnet);
        }
    }

    public void subscribe(Object subscriber) {
        subscribers = new Node(subscriber, subscribers);
    }
    
    public void cancelSubscription(Object subscriber) {
        subscribers = subscribers.remove(subscriber);
    }
} 
