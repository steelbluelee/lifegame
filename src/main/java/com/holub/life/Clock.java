package com.holub.life;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.Timer;  // java.awt.Timer를 오버라이딩
import java.util.TimerTask;
import com.holub.ui.MenuSite;
import com.holub.tools.Publisher;


public class Clock {
    private Timer clock = new Timer();
    private TimerTask tick = null;

    // Clock은 모든 것이 static인 Singleton으로 실체화할 수 없다.
    // Clock은 자신을 위한 메뉴를 추가하는데,
    // 이는 메뉴바가 생성되기 전에는 할 수 없는 작업이기 때문이다.

    private Clock() {
        createMenus();
    }

    private static Clock instance;

    public synchronized static Clock instance() {
        if ( instance == null )
            instance = new Clock();
        return instance;
    }

    /** Clock을 시작함
     * @param millisecondsBetweenTicks 시계침 사이의 간격
     * 밀리 초 단위이다. 0 값은
     * Clock을 멈추어야 함을 의미한다.
     */

    public void startTicking( int millisecondsBetweenTicks ) {
        if ( tick != null ) {
            tick.cancel();
            tick = null;
        }

        if ( millisecondsBetweenTicks > 0 ) {
            tick = new TimerTask() {
                public void run() { tick(); }
            };
            clock.scheduleAtFixedRate( tick, 0, millisecondsBetweenTicks );
        }
    }

    public void stop() {
        startTicking(0);
    }

    private void createMenus() {
        // 리스너를 셋업하여 "Exit"을 제외한
        // 모든 이벤트를 처리하도록 한다.

        ActionListener modifier =
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String name = ((JMenuItem)e.getSource()).getName();
                    char toDo = name.charAt(0);

                    if (toDo == 'T') {
                        tick();
                    } else {
                        startTicking( toDo == 'A' ? 500 : // 매우 느림
                                toDo == 'S' ? 150 : // 느림
                                toDo == 'M' ? 70  : // 중간
                                toDo == 'F' ? 30  : 0 ); // 빠름
                    }
                }
            };

        MenuSite.addLine(this, "Go", "Halt", modifier);
        MenuSite.addLine(this, "Go", "Tick (Single Stop)", modifier);
        MenuSite.addLine(this, "Go", "Agonizing", modifier);
        MenuSite.addLine(this, "Go", "Slow", modifier);
        MenuSite.addLine(this, "Go", "Medium", modifier);
        MenuSite.addLine(this, "Go", "Fast", modifier);
    }

    private Publisher publisher = new Publisher();

    public void addClockListener(Listener observer) {
        publisher.subscribe(observer);
    }

    public interface Listener {
        void tick();
    }

    public void tick() {
        publisher.publish(
            new Publisher.Distributor() {
                public void deliverTo(Object subscriber) {
                    ((Listener)subscriber).tick();
                }
            }
        );
    }
} 
