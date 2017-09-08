package com.holub.life;

import java.awt.*;

/* import com.holub.life.Storable; */

public interface Cell
{
    /* 사용자가 마우스를 클릭했을 때 호출된다.
     * @param here 클릭한 위치를 현재 셀의 경계를 기준으로 보았을 때의
     * 상대 좌표 */
    void userClicked( Point here, Rectangle surface );

    // 셀의 너비를 반환한다.
    int widthInCells();

    // 셀과 모든 서브셀을 '죽은' 상태로 셋팅한다.

    void clear();


}
