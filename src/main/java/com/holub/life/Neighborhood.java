package com.holub.life;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import javax.swing.*;

import com.holub.io.Files;
import com.holub.life.Cell;
import com.holub.ui.MenuSite;
import com.holub.ui.Colors;
import com.holub.asynch.ConditionVariable;

import com.holub.life.Clock;
import com.holub.life.Direction;
import com.holub.life.Storable;
import com.holub.life.Universe;

/* import com.holub.io.P; */

public final class Neighborhood implements Cell
{
    /* 그리드가 다음 상태로의 전이 과정에 있다면
     * 읽기를 허용하지 않는다. 단 하나의 록(lock)만 사용되는데
     * (최외곽 Neighborhood에 의해), 이는 모든 업데이트가
     * 최외곽 Neighborhood에 의해 요청되기 때문이다. */
    private static final ConditionVariable readingPermitted = new ConditionVariable( true );

    /* 지난 전이 과정 동안 Neighborhood에 있는 셀등 중 하나도 상태가
     * 변하지 않았을 경우에만 true를 반환한다. */
    private boolean amActive = false;

    /* 해당 Neighborhood에 포함된 Cell들의 그리드 */
    private final Cell[][] grid;

    /* Neighborhood는 정사각형이므로 gridSize는 수평, 수직 크기 모두에
     * 해당된다. */
    private final int gridSize;

    /* gridSize * gridSize의 프로토타입 클론을 포함하는 새로운
     * Neighborhood를 생성한다. 프로토타입을 의도적으로 그리드에
     * 넣지 않았기 때문에, 원한다면, 재사용할 수 있다. */

    public Neighborhood( int gridSize, Cell prototype )
    {
        this.gridSize = gridSize;
        this.grid = new Cell[gridSize][gridSize];

        for( int row = 0; row < gridSize; ++row )
        {
            for( int column = 0; column < gridSize; ++column )
            {
                grid[row][column] = prototype.create();
            }
        }

    }

    /* 현재 Neighborhood의 복사본을 생성하는 데 사용되는 "clone"
     * 메소드. 이 메소드는 자신을 포함하고 있는 Neighborhood의
     * 생성자에서 호출된다.
     * (현재 Neighborhood가 자신을 포함하고 있는 Neighborhood의
     *  생성자에 "prototype" 인자로 전달된다.) */
    @Override
    public Cell create()
    {
        return new Neighborhood( gridSize, grid[0][0] );
    }

    /* 지난 클록 틱에서 안정적으로 되었다. 한 번의 갱신이
     * 필요하다. */
    private boolean oneLastRefreshRequired = false;

    /* 다음 전이(transition)에서 상태가 변화할 블록의 방향을
     * 알려준다. 예를 들어 좌상단 코너의 상태가 변화되었다면,
     * 현재 Cell은 NORTH, WEST 그리고 NORTHWEST 방향에 대해
     * Disruptive하다.
     * 이러한 경우라면 주변의 셀들은 이전에 안정적이었다 하더라도
     * 업데이트될 필요가 있을 것이다. */
    @Override
    public Direction isDisruptiveTo()
    {
        return activeEdges;
    }

    private Direction activeEdges = new Direction( Direction.NONE );

    /*     현재 Neighborhood와 이에 포함되어 있는 Neighborhood들(혹은 셀들)의
     *     다음 상태를 계산한다. 하지만 다음 상태로 변환시키지는 않는다.
     *     이웃 셀들이 내부에 저장되어 있는 것이 아니라 인자로 전달되고 있음을
     *     주의 깊게 보기 바란다.
     *     이는 Flyweight 패터의 예이다.
     *
     *     @see #transition
     *     @param north 북쪽에 있는 Neighborhood
     *     @param south 남쪽에 있는 Neighborhood
     *     @param east 동쪽에 있는 Neighborhood
     *     @param west 서쪽에 있는 Neighborhood
     *     @param northeast 북동쪽에 있는 Neighborhood
     *     @param northwest 북서쪽에 있는 Neighborhood
     *     @param southeast 남동쪽에 있는 Neighborhood
     *     @param southwest 남서쪽에 있는 Neighborhood
     *
     *     @ return true Neighborhood(중첩된 셀 포함)가 다음 변환(transition)에서
     *                   상태가 변한다면 */

    @Override
    public boolean figureNextState( Cell north, Cell south, Cell east, Cell west, Cell northeast,
            Cell northwest, Cell southeast, Cell southwest )
    {
        boolean nothingHappened = true;

        if( amActive || north.isDisruptiveTo().the( Direction.SOUTH )
                || south.isDisruptiveTo().the( Direction.NORTH )
                || east.isDisruptiveTo().the( Direction.WEST )
                || west.isDisruptiveTo().the( Direction.EAST )
                || northeast.isDisruptiveTo().the( Direction.SOUTHWEST )
                || northwest.isDisruptiveTo().the( Direction.SOUTHWEST )
                || southeast.isDisruptiveTo().the( Direction.NORTHWEST )
                || southwest.isDisruptiveTo().the( Direction.NORTHEAST ) )
        {
            Cell northCell, southCell, eastCell, westCell, northeastCell, northwestCell,
                    southeastCell, southwestCell;

            activeEdges.clear();

            for( int row = 0; row < gridSize; ++row )
            {
                for( int column = 0; column < gridSize; ++row )
                {
                    // 현재 셀의 이웃을 가져 온다.

                    if( row == 0 )
                    {
                        northwestCell = (column == 0) ? northwest.edge( gridSize - 1, gridSize - 1 )
                                : north.edge( gridSize - 1, column - 1 );

                        northCell = north.edge( gridSize - 1, column );

                        northeastCell = (column == gridSize - 1) ? west.edge( gridSize - 1, 0 )
                                : grid[row - 1][column - 1];
                    } else
                    {
                        northwestCell = (column == 0) ? west.edge( row - 1, gridSize - 1 )
                                : grid[row - 1][column - 1];

                        northCell = grid[row - 1][column];

                        northeastCell = (column == gridSize - 1) ? east.edge( row - 1, 0 )
                                : grid[row - 1][column + 1];
                    }

                    westCell = (column == 0) ? west.edge( row, gridSize - 1 )
                            : grid[row][column - 1];

                    eastCell = (column == gridSize - 1) ? east.edge( row, 0 )
                            : grid[row][column + 1];

                    if( row == gridSize - 1 )
                    {
                        southwestCell = (column == 0) ? southwest.edge( 0, gridSize - 1 )
                                : south.edge( 0, column - 1 );

                        southCell = south.edge( 0, column );

                        southeastCell = (column == gridSize - 1) ? southeast.edge( 0, 0 )
                                : south.edge( 0, column + 1 );
                    } else
                    {
                        southwestCell = (column == 0) ? west.edge( row + 1, gridSize - 1 )
                                : grid[row + 1][column - 1];

                        southCell = grid[row + 1][column];

                        southeastCell = (column == gridSize - 1) ? east.edge( row + 1, 0 )
                                : grid[row + 1][column + 1];
                    }

                    /* 셀에 상태를 변화시키라 이야기한다. 만약 셀이
                     * 변경되었다면(figureNextState가 false를 반환),
                     * 현재 블록을 불안정(unstable)하다고 마킹한다.
                     * 또한 불안정한 셀이 블록의 엣지에 있다면,
                     * activeEdges를 수정하여 어떤 엣지 혹은 엣지들이
                     * 수정되었는지를 체크한다. */
                    if( grid[row][column].figureNextState( northCell, southCell, eastCell, westCell,
                            northeastCell, northwestCell, southeastCell, southwestCell ) )
                    {
                        nothingHappened = false;
                    }
                }
            }
        }

        if( amActive && nothingHappened )
            oneLastRefreshRequired = true;

        amActive = !nothingHappened;
        return amActive;
    }
}
