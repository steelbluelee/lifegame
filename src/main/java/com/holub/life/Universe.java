package com.holub.life;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import com.holub.io.Files;
import com.holub.ui.MenuSite;

import com.holub.life.Cell;
import com.holub.life.Storable;
import com.holub.life.Clock;
import com.holub.life.Neighborhood;
import com.holub.life.Resident;

/* Universe는 스윙의 이벤트 모델과 Life 클래스들 사이에 있는
 * Mediator이다. 이는 또한 Universe.instance()를 통해서만 접근할
 * 수 있는 Singleton이기도 하다. 이 클래스는 모든 스윙 이벤트
 * 요청을 받아 변경한 후, 최외곽의 Neighborhood에 요청을 보낸다.
 * 이는 또한 중첩(Composite) Neighborhood를 생성한다. */

public class Universe extends JPanel
{
    private final Cell outermostCell;
    private static final Universe theInstance = new Universe();

    /* Cell에 포함되어 있는 Neighborhood의 디폴트 높이와 너비.
     * 이 값이 너무 크면 프로그램의 실행 속도가 느려질 것이다.
     * 전체 블록을 하나의 단위로 업데이트하기 때문에 해야 할 일이 보다
     * 많아지기 때문이다. 반면 값이 너무 작으면 체크해야 하는 블록이 너무
     * 많아진다. 8은 적당한 숫자일 것이다. */
    private static final int DEFAULT_GRID_SIZE = 8;

    /* 가장 작은 '원자(atomic)' 셀의 크기, 즉 Resident 객체의 크기이다.
     * 이 크기는 Resident 외부에 있다.(이 값은 Resident의 "저 자신을 그려라"
     * 라는 메소드에 인자로 전달된다.) */
    private static final int DEFAULT_CELL_SIZE = 8;

    /* 생성자가 private이기 대문에 Universe는 Universe.instance() 메소드를
     * 통해서만 생성할 수 있다. 단순한 Singleton 패턴 실체화이다. */

    private Universe()
    {
        /* '유니버스'를 구성하는 중첩 Cell들을 생성한다.
         * 현재 구현의 버고로 인해 그리드의 전체 크기가 스크린의 크기에 비해
         * 지나치게 큰 경우 프로그램이 정상적으로 실행되는 것을 보기
         * 힘들 것이다. */

        outermostCell = new Neighborhood (
                DEFAULT_GRID_SIZE,
                new Neighborhood (
                    DEFAULT_GRID_SIZE,
                    new Resident()
                    )
                );
    }
    public static Universe instance()
    {
        return theInstance;
    }

    private static final long serialVersionUID = 1L;
}
