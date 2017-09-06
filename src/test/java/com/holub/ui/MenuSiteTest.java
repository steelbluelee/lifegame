package com.holub.ui;

import org.junit.*;
import org.mockito.Mockito;
import org.mockito.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MenuSiteTest
{
    class MockFrame extends JFrame
    {
        MockFrame()
        {
            setSize(400, 200);
            addWindowListener(new WindowAdapter()
            {
                public void windowClosing(WindowEvent e)
                {
                    System.exit(1);
                }
            });
        }

        private static final long serialVersionUID = 1L;
    }

    @Test
    public void test_addLine()
    {
        JFrame mockFrame = new MockFrame();
        MenuSite.establish( mockFrame );
        ActionListener mockMenuListener = Mockito.mock( ActionListener.class );
        Mockito.doNothing()
            .when( mockMenuListener )
            .actionPerformed( Mockito.isA( ActionEvent.class ) );
        ActionEvent mockActionEvent = Mockito.mock( ActionEvent.class );

        MenuSite.addLine( mockFrame, "Go", "Test", mockMenuListener);

       ((JMenuItem) (mockFrame.getJMenuBar().getMenu( 0 ).getMenuComponent( 0 )))
           .getActionListeners()[0]
           .actionPerformed( mockActionEvent );

        Mockito.verify(mockMenuListener)
            .actionPerformed( Mockito.isA( ActionEvent.class ) );

        MenuSite.unestablish();
    }
}
