package com.holub.ui;

import java.awt.*;

/* Colors 인터페이스는 다양한 색을 정의하는 심벌 상수를 정의할 뿐이다.
 * 상수의 이름은 자기 서술적이다. */

public interface Colors
{
    static final Color DARK_RED = new Color( 0x99, 0x00, 0x00 );
    static final Color MEDIUM_RED = new Color( 0xcc, 0x00, 0x00 );
    static final Color LIGHT_RED = new Color( 0xff, 0x00, 0x00 );

    static final Color DARK_ORANGE = new Color( 0xff, 0x66, 0x00 );
    static final Color MEDIUM_ORANGE = new Color( 0xff, 0x99, 0x00 );
    static final Color LIGHT_ORANGE = new Color( 0xff, 0xcc, 0x00 );
    static final Color ORANGE = new Color( 0xff, 0x99, 0x00 );

    static final Color OCHRE = new Color( 0xcc, 0x99, 0x00 );
    static final Color DARK_YELLOW = new Color( 0xff, 0xff, 0x00 );
    static final Color MEDIUM_YELLOW = new Color( 0xff, 0xff, 0x99 );
    static final Color LIGHT_YELLOW = new Color( 0xff, 0xff, 0xdd );

    static final Color DARK_GREEN = new Color( 0x00, 0x66, 0x00 );
    static final Color MEDIUM_GREEN = new Color( 0x00, 0x99, 0x00 );
    static final Color LIGHT_GREEN = new Color( 0x00, 0xff, 0x00 );
    static final Color GREEN = MEDIUM_GREEN;

    static final Color DARK_BLUE = new Color( 0x00, 0x00, 0x99 );
    static final Color MEDIUM_BLUE = new Color( 0x00, 0x00, 0xcc );
    static final Color LIGHT_BLUE = new Color( 0x00, 0x00, 0xff );

    static final Color DARK_PURPLE = new Color( 0x99, 0x00, 0x99 );
    static final Color MEDIUM_PURPLE = new Color( 0xcc, 0x00, 0xff );
    static final Color LIGHT_PURPLE = new Color( 0xcc, 0x99, 0xff );
    static final Color PURPLE = MEDIUM_PURPLE;
}
