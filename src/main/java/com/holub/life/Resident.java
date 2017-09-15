package com.holub.life;

import java.awt.*;
import javax.swing.*;
// java.awt.Color에 정의되어 있지 않은 다양한 상수를 정의하고 있다.
import com.holub.ui.Colors;
import com.holub.life.Cell;
import com.holub.life.Storable;
import com.holub.life.Direction;
import com.holub.life.Neighborhood;
import com.holub.life.Universe;

public final class Resident implements Cell
{
    private static final Color BORDER_COLOR = Colors.DARK_YELLOW;
    private static final Color LIVE_COLOR = Color.RED;
    private static final Color DEAD_COLOR = Colors.LIGHT_YELLOW;

    private boolean amAlive = false;
    private boolean willBeAlive = false;

    private boolean isStable()
    {
        return amAlive == willBeAlive;
    }

    /* 다음 상태를 계산한다.
     * @return true 셀이 안정적이지 않을 때(이는 transition() 호출시
     * 상태를 변경하게 될 것이다.) */
    @Override
    public boolean figureNextState( Cell north, Cell south, Cell east, Cell west, Cell northeast,
            Cell northwest, Cell southeast, Cell southwest )
    {
        verify( north, "north" );
        verify( south, "south" );
        verify( east, "east" );
        verify( west, "west" );
        verify( northeast, "northeast" );
        verify( northwest, "northwest" );
        verify( southeast, "southeast" );
        verify( southwest, "southwest" );

        int neighbors = 0;

        if( north.isAlive() ) ++neighbors;
        if( south.isAlive() ) ++neighbors;
        if( east.isAlive() ) ++neighbors;
        if( west.isAlive() ) ++neighbors;
        if( northeast.isAlive() ) ++neighbors;
        if( northwest.isAlive() ) ++neighbors;
        if( southeast.isAlive() ) ++neighbors;
        if( southwest.isAlive() ) ++neighbors;

        willBeAlive = (neighbors == 3 || (amAlive && neighbors == 2));
        return !isStable();
    }

    private void verify( Cell c, String direction )
    {
        assert ((c instanceof Resident) || (c == Cell.DUMMY)) : "incorrect type for " + direction
                + "+ " + c.getClass().getName();
    }

    /* 이 셀(Resident)은 모두 자신의 엣지(edge)에 있다. 그러므로
     * (0,0)을 제외한 다른 위치를 요청하는 것은 내부 에러를 발생시킨다.
     * 이들의 너비는 1이기 때문이다. */
    @Override
    public Cell edge( int row, int column )
    {
        assert row == 0 && column == 0;
        return this;
    }

    @Override
    public boolean transition()
    {
        boolean changed = isStable();
        amAlive = willBeAlive;
        return changed;
    }

    @Override
    public void redraw( Graphics g, Rectangle here, boolean drawAll )
    {
        g = g.create();
        g.setColor( amAlive ? LIVE_COLOR : DEAD_COLOR );
        g.fillRect( here.x + 1, here.y + 1, here.width - 1, here.height - 1);

        /* 그리드의 오른쪽과 아래쪽에는 선을 그리지 않는다.
         * 자신과 인접해 있는 셀이 해당 부분에 선을 그려주기 때문이다.
         * 가장 오른쪽과 가장 밑쪽 셀을 위한 특별 케이스는 처리하지 않는다. */

        g.setColor( BORDER_COLOR );
        g.drawLine( here.x, here.y, here.x, here.y + here.height );
        g.drawLine( here.x, here.y, here.x + here.width, here.y );
        g.dispose();
    }

    @Override
    public void userClicked( Point here, Rectangle surface )
    {
        amAlive = !amAlive;

    }

    @Override
    public void clear()
    {
        amAlive = willBeAlive = false;

    }

    @Override
    public boolean isAlive()
    {
        return amAlive;
    }

    @Override
    public Cell create()
    {
        return new Resident();
    }

    @Override
    public int widthInCells()
    {
        return 1;
    }

    @Override
    public Direction isDisruptiveTo()
    {
        return isStable() ? Direction.NONE : Direction.ALL;
    }

    @Override
    public boolean transfer( Storable blob, Point upperLeft, boolean doLoad )
    {
        Memento memento = (Memento) blob;
        if( doLoad )
        {
            if( amAlive = willBeAlive = memento.isAlive( upperLeft ) )
                return true;
        } else if( amAlive ) // 살아있는 셀만 저장한다.
            memento.markAsAlive( upperLeft );

        return false;
    }

    @Override
    public Storable createMemento()
    {
        throw new UnsupportedOperationException( "May not create memento of a unitary cell" );
    }
}
