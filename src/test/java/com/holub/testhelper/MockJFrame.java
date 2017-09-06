package com.holub.testhelper;

import java.awt.event.*;
import javax.swing.*;

public class MockJFrame extends JFrame
{
    public MockJFrame()
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
