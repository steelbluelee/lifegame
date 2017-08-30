package com.holub.ui;

import com.holub.life.Clock;
import java.awt.event.*;

public class MenuSite {
    private MenuSite(){}

    private static MenuSite instance;
    public synchronized static MenuSite instance() {
        if (instance == null) {
            instance = new MenuSite();
        }
        return instance;
    }

    public MenuSite addLine(Clock arg1, String arg2, String arg3, ActionListener arg4) {
        return this;
    }

} 
