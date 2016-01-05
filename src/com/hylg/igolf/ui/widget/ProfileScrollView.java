package com.hylg.igolf.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Debug;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.R;
import com.hylg.igolf.ui.friend.publish.WeiBoUser;
import com.hylg.igolf.ui.member.SystemBarUtils;


/**
 * Created by wangdan on 15-2-11.
 */
public class ProfileScrollView extends ScrollView {

    public static final String TAG = "ProfileScrollView";

    public ProfileScrollView(Context context) {
        super(context);
    }

    public ProfileScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProfileScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private static final int INVALID_POINTER = -1;

    private LinearLayout child;
    //private View layTop;
    private View layTabStrip;
    private ViewPager viewPager;
    private View viewToolbar;
    private View bottem_linear;

    private View refreshView;
    private int mActivePointerId = INVALID_POINTER;
    private float mInitialMotionY;
    private int action_size,headHeight;

    private FragmentActivity activity;
    private WeiBoUser mUser;

    private float downY,moveY;

    private float downX,moveX;

    private boolean in = false;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (getChildCount() > 0) {
            if (getChildAt(0).getHeight() > 0 && child == null) {
                child = (LinearLayout) getChildAt(0);
            }
        }

       // int themeColor = Utils.resolveColor(getContext(), R.attr.colorPrimary, Color.BLUE);

        int themeColor = Color.BLUE;

        if (child != null && child.getHeight() > 0 && layTabStrip == null) {
            layTabStrip = child.getChildAt(1);
            //layTabStrip.setBackgroundColor(themeColor);
            viewPager = (ViewPager) child.getChildAt(2);

            activity = (FragmentActivity) getContext();
            //layTop = activity.findViewById(R.id.layTop);
            //layTop.setBackgroundColor(Color.RED);
            viewToolbar = activity.findViewById(R.id.mem_info_head_nick_text);
//            if (viewToolbar != null)
//                viewToolbar.setBackgroundColor(Color.GREEN);
            bottem_linear = activity.findViewById(R.id.mem_info_bottem_relative);
            //activity.findViewById(R.id.viewBgDes).setBackgroundColor(Color.BLACK);

            action_size = activity.getResources().getDimensionPixelSize(R.dimen.div_line_width);
            int statusBar = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                statusBar = SystemBarUtils.getStatusBarHeight(activity);

               // statusBar = 100;
            }

            DebugTools.getDebug().debug_v(TAG,"statusBar---------->>>>"+statusBar);
//            Logger.d(TAG, String.format("topview height = %d, stripView height = %d, toolbar height = %d",
//                    child.getChildAt(0).getHeight(), layTabStrip.getHeight(), action_size));

            int height = activity.getResources().getDisplayMetrics().heightPixels;

            int sdsdf= activity.findViewById(R.id.mem_info_ac_detail_new_head).getHeight();
            DebugTools.getDebug().debug_v(TAG, "height---------->>>>" + height);

            headHeight = (layTabStrip.getHeight()+statusBar+sdsdf);

            viewPager.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        height - headHeight));

        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (viewToolbar != null)
            viewToolbar.setAlpha(Math.abs(t * 1.0f / (headHeight - action_size)));

        if (bottem_linear != null)
            bottem_linear.setAlpha(1-Math.abs(t * 1.0f / (headHeight - action_size)));

        // 设置显示Actionbar的title
        if (activity != null) {
            if (viewToolbar != null && viewToolbar.getAlpha() >= 0.75f) {
                //activity.getSupportActionBar().setTitle(mUser.getScreen_name());
                viewToolbar.setVisibility(View.VISIBLE);
            }
            else {
                //activity.getSupportActionBar().setTitle("");
                viewToolbar.setVisibility(View.GONE);
            }

            if (bottem_linear != null && bottem_linear.getAlpha() >= 0.1f) {
                //activity.getSupportActionBar().setTitle(mUser.getScreen_name());
                bottem_linear.setVisibility(View.VISIBLE);
            }
            else {
                //activity.getSupportActionBar().setTitle("");
                bottem_linear.setVisibility(View.GONE);
            }
        }
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
            if (yDiff < 0) {
                if (getChildAt(0).getMeasuredHeight() <= getHeight() + getScrollY()) {
                    return false;
                }

            }

            float ads = getScrollY();
            DebugTools.getDebug().debug_v("ProfileScrollView","ads----->>>"+ads);
            if(ads == 0 &&( moveY-downY) >  0) {

                return false;
            }

            if (Math.abs(moveY-downY) < Math.abs(moveX-downX)) {

                return false;
            }

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
