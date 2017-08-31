package com.holub.life;

import javax.swing.*;

public class Universe extends JPanel
{
    private static final Universe theInstance = new Universe();

    public static Universe instance()
    {
        return theInstance;
    }

    private static final long serialVersionUID = 1L;
}
