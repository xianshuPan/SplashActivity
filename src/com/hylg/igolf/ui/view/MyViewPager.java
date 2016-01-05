package com.hylg.igolf.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by hongdoutouzi on 15/12/10.
 */
public class MyViewPager extends ViewPager {

    private boolean scrollble=true;

    public MyViewPager(Context context){
    super(context);
}

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        if (!scrollble) {
//            return true;
//        }
//        return super.onTouchEvent(ev);
//    }

    @Override
    public void scrollTo(int x, int y){
        if (scrollble){
            super.scrollTo(x, y);
        }
    }


    public boolean isScrollble() {
        return scrollble;
    }

    public void setScrollble(boolean scrollble) {
        this.scrollble = scrollble;
    }
}
