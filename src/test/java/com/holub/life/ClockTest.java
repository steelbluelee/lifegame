package com.holub.life;

import org.junit.*;
import org.mockito.Mockito;

import com.holub.ui.MenuSite;
import com.holub.testhelper.*;

public class ClockTest
{
    Clock clock;

    Clock.Listener mockClockListener1 = Mockito.mock(Clock.Listener.class);
    Clock.Listener mockClockListener2 = Mockito.mock(Clock.Listener.class);

    @BeforeClass
    public static void initGlobalResources()
    {
        MenuSite.establish(new MockJFrame());
    }

    @Before
    public void setUp()
    {
        clock = Clock.instance();
        clock.addClockListener(mockClockListener1);
        clock.addClockListener(mockClockListener2);

    }

    @AfterClass
    public static void releaseGlocalResources()
    {
        MenuSite.unestablish();
    }

    @Test
    public void test_tick()
    {
        clock.tick();

        Mockito.verify(mockClockListener1).tick();
        Mockito.verify(mockClockListener2).tick();
    }

    @Test
    public void test_startTicking()
    {
        clock.startTicking(5);

        try
        {
            Thread.sleep(20);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        clock.stop();

        try
        {
            Thread.sleep(15);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        Mockito.verify(mockClockListener1, Mockito.atLeast(3)).tick();
        Mockito.verify(mockClockListener1, Mockito.atMost(5)).tick();
        Mockito.verify(mockClockListener2, Mockito.atLeast(3)).tick();
        Mockito.verify(mockClockListener2, Mockito.atMost(5)).tick();
    }
}
