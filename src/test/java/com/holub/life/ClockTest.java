package com.holub.life;

import org.junit.*;
import org.mockito.Mockito;

public class ClockTest
{
    Clock clock = Clock.instance();

    @Test
    public void test_tick()
    {
        Clock.Listener mockClockLintenen1 = Mockito.mock(Clock.Listener.class);
        Clock.Listener mockClockLintenen2 = Mockito.mock(Clock.Listener.class);

        Clock clock = Clock.instance();

        clock.addClockListener(mockClockLintenen1);
        clock.addClockListener(mockClockLintenen2);

        clock.tick();

        Mockito.verify(mockClockLintenen1).tick();
        Mockito.verify(mockClockLintenen2).tick();
    }

    @Test
    public void test_startTicking()
    {
        Clock.Listener mockClockLintenen1 = Mockito.mock(Clock.Listener.class);
        Clock.Listener mockClockLintenen2 = Mockito.mock(Clock.Listener.class);

        Clock clock = Clock.instance();

        clock.addClockListener(mockClockLintenen1);
        clock.addClockListener(mockClockLintenen2);

        clock.startTicking(5);

        try {
            Thread.sleep(20);
        } catch(Exception e){
            e.printStackTrace();
        }
        
        clock.stop();

        try {
            Thread.sleep(15);
        } catch(Exception e){
            e.printStackTrace();
        }

        Mockito.verify(mockClockLintenen1, Mockito.atLeast(3)).tick();
        Mockito.verify(mockClockLintenen1, Mockito.atMost(5)).tick();
        Mockito.verify(mockClockLintenen2, Mockito.atLeast(3)).tick();
        Mockito.verify(mockClockLintenen2, Mockito.atMost(5)).tick();
    }
} 
