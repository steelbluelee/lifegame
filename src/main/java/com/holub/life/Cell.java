package com.holub.life;

import java.awt.*;

import com.holub.life.Storable;

public interface Cell
{
    /* 인자로 넘어온 이웃들을 기준으로 해당 셀의
     * 다음 상태를 계산한다.
     * @return 셀이 불안전(상태를 바꿈)하면 true를 반환한다. */
    boolean figureNextState( Cell north, Cell south, Cell east, Cell west, Cell northeast,
            Cell northwest, Cell southeast, Cell southwest );

    /* 중첩 셀에서 지정된 곳에 위치해 있는 특정 셀에
     * 접근한다.
     * @param row 요청된 행
     *             전체 행의 크기보다 작아야 한다.
     * @param column 요청된 열
     *             역시 전체 크기보다 작아야 한다.
     * @return 요청된 행과 열에 위치해 있는 셀 */
    Cell edge( int row, int column );

    /* 가장 최근 호출된 {@link #figureNextState}가 계산한 상태로
     * 전이한다.
     * @return true 전이 과정 동안 상태가 변화 */
    boolean transition();

    /* 필요하다면 인자로 넘어온 Graphics 객체 위에 Rectangle
     * 인자 객체를 이용하여 자신을 다시 그린다.
     * 이 메소드는 조건적으로 사용되며,
     * 어떤 셀이 리프레시될 필요가 없다면(예를 들어 상태가 변하지 않았을 경우)
     * 호출되지 않는다.
     * @param g이 그래픽 객체를 사용하여 다시 그린다.
     * @param here 현재 셀의 경계를 기술하는 사각형
     * @param drawAll true이며 전체 복합 셀을 다시 그린다. false이면
     * 다시 그릴 필요가 있는 서브셀들만 그리게 된다. */
    void redraw( Graphics g, Rectangle here, boolean drawAll );

    /* 사용자가 마우스를 클릭했을 때 호출된다.
     * @param here 클릭한 위치를 현재 셀의 경계를 기준으로 보았을 때의
     * 상대 좌표 */
    void userClicked( Point here, Rectangle surface );

    // 현재 셀, 혹은 서브셀이 살아 있다면, true를 반환한다.
    boolean isAlive();

    // 셀의 너비를 반환한다.
    int widthInCells();

    // 자신과 내용상 동일한 객체를 새로 생성하여 반환한다.
    Cell create();

    /* 이 셀의 변화로 인해 상태가 변화가 될 셀의 방향을 나타내는
     * Direction 객체를 반환한다.
     * @return 변화가 일어난 엣지(edge) 혹은 엣지들(edges)을 나타내는
     *     Direction 객체
     *  */
    Direction isDisruptiveTo();

    // 셀과 모든 서브셀을 '죽은' 상태로 셋팅한다.
    void clear();

    /* Cell.Memento 인터페이스는 향후 복구를 위해
     * 셀과 모든 서브셀의 상태를 저장한다. */
    interface Memento extends Storable
    {
        /* 메멘토를 생성할 때 셀이 살아 있음을 표시한다. */
        void markAsAlive( Point location );

        /* 메멘토로부터 셀을 복구시킬 때
         * 셀이 살아 있음을 알려 준다. */
        boolean isAlive( Point location );
    }

    /* 이 메소드는 내부적으로 메멘토로부터 셀의 상태를 저장하거나
     * 복구하기 위해 사용된다.
     * @return true 셀이 transfer에 의해 수정되었을 때 */
    boolean transfer( Storable memento, Point upperLeftCorner, boolean doLoad );

    /* transfer() 메소드의 'doLoad' 인자로 넘겨주는 값 */
    public static boolean STORE = false;

    /* 이 메소드는 최외곽 셀에서 사용한다.
     * 최외곽 셀이 아닌 내부 셀에서는 사용하지 않기 때문에
     * 유니버스에 있는 최외곽 셀만이 구현하면 된다.
     * 최외곽 셀이 아닌 다른 셀들은 이 메소드가 호출되었을 때
     * UnsupportedOperationException을 던져야 한다. */
    Storable createMemento();

    /* DUMMY 싱글톤은 항상 죽어 있는(그러므로 안정된) 셀을 표현한다.
     * 이는 격자의 엣지(edge)로 사용되며 싱글톤이다.
     * Dummy 클래스는 private 이어야 하지만, 아래에 선언되어 있는
     * public DUMMY 필드를 통해 접근할 수 있다. 나는 이 클래스가
     * private 이길 바리지만, JLS(열자 주: Java Language Specification)는
     * 인터페이스가 private 멤버를 갖는 것을 허용하지 않는다. */

    public static final Cell DUMMY = new Cell()
    {
        public boolean figureNextState( Cell n, Cell s, Cell e, Cell w, Cell ne, Cell nw, Cell se,
                Cell sw )
        {
            return true;
        }

        public Cell edge( int r, int c )
        {
            return this;
        }

        public boolean isAlive()
        {
            return false;
        }

        public Cell create()
        {
            return this;
        }

        public Direction isDisruptiveTo()
        {
            return Direction.NONE;
        }

        public void clear()
        {
        }

        public int widthInCells()
        {
            return 0;
        }

        public boolean transition()
        {
            return false;
        }

        public void userClicked( Point h, Rectangle s )
        {
        }

        public void redraw( Graphics g, Rectangle here, boolean drawAll )
        {
        }

        public boolean transfer( Storable m, Point ul, boolean load )
        {
            return false;
        }

        public Storable createMemento()
        {
            throw new UnsupportedOperationException( "Cannot create memento of dummy block" );
        }
    };
}
