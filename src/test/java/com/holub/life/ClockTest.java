package com.holub.life;

import org.junit.*;
import org.powermock.modules.junit4.PowerMockRunner;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import com.holub.ui.MenuSite;

@RunWith(PowerMockRunner.class)
public class ClockTest {
    Clock clock = Clock.instance();

    @Test
    @PrepareForTest(MenuSite.class)
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
