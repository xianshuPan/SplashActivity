package com.hylg.igolf.ui.widget;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.R;
import com.hylg.igolf.ui.friend.publish.WeiBoUser;


/**
 * Created by wangdan on 15-2-11.
 */
public class ContactScrollView extends ScrollView {

    public static final String TAG = "ProfileScrollView";

    public ContactScrollView(Context context) {
        super(context);
    }

    public ContactScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContactScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private static final int INVALID_POINTER = -1;


    private View refreshView;
    private int mActivePointerId = INVALID_POINTER;
    private float mInitialMotionY;
    private int action_size,headHeight;

    private FragmentActivity activity;
    private WeiBoUser mUser;

    private float downY,moveY;

    private float downX,moveX;

    private boolean in = false;


    private ListView unRegisterList;


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        activity = (FragmentActivity) getContext();

        unRegisterList = (ListView)activity.findViewById(R.id.contact_list);

    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

    }

    public void setAbsListView(View refreshView) {
        this.refreshView = refreshView;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);

        if (refreshView != null) {
            boolean canChildScrollUp = ViewCompat.canScrollVertically(refreshView, -1);
//            Logger.d(TAG, String.format("canChildScrollUp = %s", String.valueOf(canChildScrollUp)));
            if (canChildScrollUp) {
                return false;
            }
        }

        switch (action) {
        case MotionEvent.ACTION_DOWN:
            mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
            final float initialMotionY = getMotionEventY(ev, mActivePointerId);
            if (initialMotionY == -1) {
                return false;
            }
            mInitialMotionY = initialMotionY;
            downY = ev.getY();
            downX = ev.getX();
            break;
        case MotionEvent.ACTION_MOVE:

            in = true;
           // postInvalidate();

            if (mActivePointerId == INVALID_POINTER) {
//                Logger.e(TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
                return false;
            }
            moveY = ev.getY();
            moveX = ev.getX();
            final float y = getMotionEventY(ev, mActivePointerId);
            if (y == -1) {
                return false;
            }
            final float yDiff = y - mInitialMotionY;

            DebugTools.getDebug().debug_v("scrollview_yDiff","----->>>"+yDiff);
            if (yDiff < 0) {
                if (getChildAt(0).getMeasuredHeight() <= getHeight() + getScrollY()) {
                    return false;
                }
            }
            else {

                if(unRegisterList != null) {

                    if(unRegisterList.getFirstVisiblePosition() > 0){
                   // if (unRegisterList.getChildAt(0) != null && unRegisterList.getChildAt(0).getVisibility() == View.VISIBLE) {
                        return false;
                    }
                }
            }

//            float ads = getScrollY();
//            DebugTools.getDebug().debug_v("ProfileScrollView","ads----->>>"+ads);
//            if(ads == 0 &&( moveY-downY) >  0) {
//
//                return false;
//            }
//
//            if (Math.abs(moveY-downY) < Math.abs(moveX-downX)) {
//
//                return false;
//            }

            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            mActivePointerId = INVALID_POINTER;
            break;
        }

        return super.onInterceptTouchEvent(ev);
    }

//    @Override
//    public boolean onTouchEvent (MotionEvent ev){
//
//        super.onTouchEvent(ev);
//    }



    private float getMotionEventY(MotionEvent ev, int activePointerId) {
        final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        if (index < 0) {
            return -1;
        }
        return MotionEventCompat.getY(ev, index);
    }

    public void setUser(WeiBoUser user) {
        this.mUser = user;
    }

}
