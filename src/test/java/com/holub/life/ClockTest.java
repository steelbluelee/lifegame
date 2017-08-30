package com.holub.life;

import org.junit.*;
import org.mockito.Mockito;

public class ClockTest {
    Clock clock = Clock.instance();

    @Test
    public void test_tick() {

        Clock.Listener mockClockLintenen1 = Mockito.mock(Clock.Listener.class);
        Clock.Listener mockClockLintenen2 = Mockito.mock(Clock.Listener.class);

        Clock clock = Clock.instance();

        clock.addClockListener(mockClockLintenen1);
        clock.addClockListener(mockClockLintenen2);

        clock.tick();

        Mockito.verify(mockClockLintenen1).tick();
        Mockito.verify(mockClockLintenen2).tick();
    }
} 
