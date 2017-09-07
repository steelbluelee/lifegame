package com.holub.ui;

import org.junit.*;
import org.mockito.Mockito;
import static org.assertj.core.api.Assertions.*;
/* import org.mockito.*;
 * import java.awt.*; */
import java.awt.event.*;
import javax.swing.*;

import com.holub.testhelper.*;

public class MenuSiteTest
{
    JFrame mockFrame;
    ActionListener mockMenuListener = Mockito.mock(ActionListener.class);

    @Before
    public void setUp()
    {
        mockFrame = new MockJFrame();
        MenuSite.establish(mockFrame);

    }

    @After
    public void tearDrop()
    {
        MenuSite.unestablish();
    }

    @Test
    public void test_addLine()
    {
        Mockito.doNothing().when(mockMenuListener).actionPerformed(Mockito.isA(ActionEvent.class));
        ActionEvent mockActionEvent = Mockito.mock(ActionEvent.class);

        MenuSite.addLine(mockFrame, "Go", "Test", mockMenuListener);

        ((JMenuItem) (mockFrame.getJMenuBar().getMenu(0).getMenuComponent(0))).getActionListeners()[0]
                .actionPerformed(mockActionEvent);

        Mockito.verify(mockMenuListener).actionPerformed(Mockito.isA(ActionEvent.class));
    }

    @Test(expected = java.lang.NullPointerException.class)
    public void test_shouldBeNullAfterRemoveMenus()
    {
        MenuSite.addLine(mockFrame, "Go", "Test", mockMenuListener);
        MenuSite.removeMyMenus(mockFrame);

        ((JMenuItem) mockFrame.getJMenuBar().getMenu(0).getMenuComponent(0)).getText();
    }

    @Test
    public void test_HelpShouldBeTheLastMenu()
    {
        MenuSite.addLine(mockFrame, "Help", "Help", mockMenuListener);
        MenuSite.addLine(mockFrame, "Go", "Test", mockMenuListener);

        assertThat(((JMenuItem) mockFrame.getJMenuBar().getMenu(1).getMenuComponent(0)).getText()).isEqualTo("Help");
    }
}
