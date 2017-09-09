package com.holub.asynch;

/* 이 클래스는 Taming Java Threads에서 설명한 com.asynch.Condition 클래스를
 * 단순화한 것이다. 이 클래스를 사용하여 어떤 조건이 true가 되기를 기다려라.
 * <PRE>
 * ConditionVariable hellFreezesOver = new ConditionVariable( false );
 *
 * Threads 1:
 *     hellFreezesOver.waitForTrue();
 *
 * Threads 2:
 *     hellFreezesOver.set( true );
 * </PRE>
 * Unlike <code>wait()</code>와 달리 조건 변수의 상태가 true라면 실행이
 * 중단되지 않을 것이다. 변수를 false 상태로 바꾸어 다른 스레드 변수가 true가
 * 되길 기다리도록 하고 싶다면 <code>set( false )</code>를
 * 호출하면 된다.
 *  */

public class ConditionVariable
{
    private volatile boolean isTrue;

    public ConditionVariable( boolean isTrue )
    {
        this.isTrue = isTrue;
    }

    public synchronized boolean isTrue()
    {
        return isTrue;
    }

    public synchronized void set( boolean how )
    {
        if( (isTrue = how) == true )
            notifyAll();
    }

    public final synchronized void waitForTrue() throws InterruptedException
    {
        while( !isTrue )
            wait();
    }
}
