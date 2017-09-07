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

        ActionListener mockMenuListener = Mockito.mock(ActionListener.class);
        Mockito.doNothing().when(mockMenuListener).actionPerformed(Mockito.isA(ActionEvent.class));
        ActionEvent mockActionEvent = Mockito.mock(ActionEvent.class);

        MenuSite.addLine(mockFrame, "Go", "Test", mockMenuListener);

        ((JMenuItem) (mockFrame.getJMenuBar().getMenu(0).getMenuComponent(0))).getActionListeners()[0]
                .actionPerformed(mockActionEvent);

        Mockito.verify(mockMenuListener).actionPerformed(Mockito.isA(ActionEvent.class));

        MenuSite.unestablish();
    }

    @Test(expected=java.lang.NullPointerException.class)
    public void test_shouldBeNullAfterRemoveMenus()
    {
        ActionListener mockMenuListener = Mockito.mock(ActionListener.class);

        MenuSite.addLine(mockFrame, "Go", "Test", mockMenuListener);
        MenuSite.removeMyMenus(mockFrame);

        ((JMenuItem) mockFrame.getJMenuBar().getMenu(0).getMenuComponent(0)).getText();
    }

}
