package com.holub.life;

public class Direction
{
    private int map = BITS_NONE;

    private static final int BITS_NORTH = 0x0001;
    private static final int BITS_SOUTH = 0x0002;
    private static final int BITS_EAST = 0x0004;
    private static final int BITS_WEST = 0x0008;
    private static final int BITS_NORTHEAST = 0x0010;
    private static final int BITS_NORTHWEST = 0x0020;
    private static final int BITS_SOUTHEAST = 0x0040;
    private static final int BITS_SOUTHWEST = 0x0080;
    private static final int BITS_ALL = 0x00ff;
    private static final int BITS_NONE = 0x0000;
    /*
     *     다양한 방향들. 우리는 그리드의 엣지에 대해 논의하고 있으므로
     *     NORTH | WEST와 NORTHWEST는 다른 방향이다.
     *     NORTH는 NORTH 엣지에 있는 무언가가 활성화되어 있다는 것을 의미하며,
     *     WEST는 WEST 엣지에 이는 것이 활성화되어 있는 것을 의미한다. 그리고
     *     NORTHWEST는 NORTHWEST 코너에 있는 셀이 활성화되어 있다는 의미이다.
     *     만약 NORTHWEST 코너가 활성화되어 있다면, north와 west 엣지 역시
     *     활성화되어 있을 것이다. 하지만 그 역은 성립하지 않는다. */

    public static final Direction NORTH = new Immutable( BITS_NORTH );
    public static final Direction SOUTH = new Immutable( BITS_SOUTH );
    public static final Direction EAST = new Immutable( BITS_EAST );
    public static final Direction WEST = new Immutable( BITS_WEST );
    public static final Direction NORTHEAST = new Immutable( BITS_NORTHEAST );
    public static final Direction NORTHWEST = new Immutable( BITS_NORTHWEST );
    public static final Direction SOUTHEAST = new Immutable( BITS_SOUTHEAST );
    public static final Direction SOUTHWEST = new Immutable( BITS_SOUTHWEST );
    public static final Direction ALL = new Immutable( BITS_ALL );
    public static final Direction NONE = new Immutable( BITS_NONE );

    public Direction()
    {
    }

    public Direction( Direction d )
    {
        map = d.map;
    }

    public Direction( int bits )
    {
        map = bits;
    }

    public boolean equals( Direction d )
    {
        return d.map == map;
    }

    public void clear()
    {
        map = BITS_NONE;
    }

    public void add( Direction d )
    {
        map |= d.map;
    }

    public boolean has( Direction d )
    {
        return the( d );
    }

    public boolean the( Direction d )
    {
        return ((map & d.map) == d.map);
    }

    private static final class Immutable extends Direction
    {
        private static final String message =
            "May not modify Direction constant (Direction.NORTH, etc)";

        private Immutable( int bits)
        {
            super( bits );
        }

        public void clear()
        {
            throw new UnsupportedOperationException( message );
        }
        
        public void add( Direction d )
        {
            throw new UnsupportedOperationException( message );
        }
    }
}
