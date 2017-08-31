package com.holub.life;

import java.awt.*;
import javax.swing.*;
import com.holub.ui.MenuSite;

public final class Life extends JFrame
{
    private static JComponent universe;

    public static void main (String[] args) {
        new Life();
    }

    private Life()
    {
        super( "The Game of Life. "
                + "&copy;2003 Allen I. Holub <http://www.holub.com");

        /* 서브컴포넌트가 메뉴를 추가하기 때문에
         * Life를 생성하며 MenuSite를 프레임에 장착해야 한다. */
        MenuSite.establish( this );

        setDefaultCloseOperation( EXIT_ON_CLOSE );
        getContentPane().setLayout( new BorderLayout() );
        getContentPane().add( Universe.instance(), BorderLayout.CENTER );

        pack();
        setVisible( true );
    }


    private static final long serialVersionUID = 1L;
}
