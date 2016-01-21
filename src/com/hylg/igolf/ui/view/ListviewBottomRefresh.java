package com.hylg.igolf.ui.view;


import com.hylg.igolf.DebugTools;
import com.hylg.igolf.R;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ListviewBottomRefresh extends ListView implements OnScrollListener{
	
	private static final String 		TAG = "EhecdListviewBottomRefresh";

	private static final int INVALID_POINTER = -1;
	
	public final static int 			REFRESHING = 2;  
	public final static int 			DONE = 3;

	private int mActivePointerId = INVALID_POINTER;
	private float mInitialMotionY;
	
	/*ʵ�ʵ�padding�ľ����������ƫ�ƾ���ı���  */ 
	private LayoutInflater 				inflater;  
 	private LinearLayout 				footView;  
 	private TextView 					footTipsTextview;   
	private ProgressBar 				footProgressBar;
	private View                        footBottomView;
    
    /*���ڱ�֤startY��ֵ��һ�������
     * touch�¼���ֻ����¼һ�� */  
    public int 							firstItemIndex;
    private int 						lastItemIndex;  
    public int 							visibleItemCount;
    public int 							totaltItemIndex;  
    public int 							state;  
    private OnRefreshListener 			refreshListener;  
    private boolean 					isRefreshable;  
    
    /*��һ����������ʾ���ǲ��ǵײ�ˢ��*/
    public boolean 						isBottomRefresh = false,isBottomShow = false;
    
    private int 						mScrollY;

	public ListviewBottomRefresh(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView(context);
	}
	
	public ListviewBottomRefresh(Context context,AttributeSet set) {
		super(context,set);
		// TODO Auto-generated constructor stub
		initView(context);
	}

	public void setShowFootBottom(boolean is_bottom_show) {

		isBottomShow = is_bottom_show;

	}
	
	void initView(Context context)
	{
		inflater = LayoutInflater.from(context);
		
		footView = (LinearLayout)inflater.inflate(R.layout.ehecd_listview_footer, null);
		footProgressBar  = (ProgressBar)footView.findViewById(R.id.refresh_list_footer_progressbar);
		footTipsTextview = (TextView)footView.findViewById(R.id.refresh_list_footer_text);
		footBottomView = footView.findViewById(R.id.refresh_list_footer_bottom_view);
		
		//footProgressBar.setVisibility(View.GONE);
		//footTipsTextview.setVisibility(View.GONE);
		
		measureView(footView); 
		//DebugTools.getDebug().debug_v("footerWide", "======="+footView.getWidth());
		//DebugTools.getDebug().debug_v("footerheight", "======="+footView.getHeight());
		footView.invalidate();
		
		addFooterView(footView,null,false);
		requestFocus();
		setOnScrollListener(this);
		
		state = DONE;
		isRefreshable = false;
	}
	
	public void measureView(View child) {
		
		ViewGroup.LayoutParams p = child.getLayoutParams();  
		
		if (p == null) {  
			
			p = new ViewGroup.LayoutParams(  ViewGroup.LayoutParams.WRAP_CONTENT,  ViewGroup.LayoutParams.WRAP_CONTENT);  
		}  
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);  
		int lpHeight = p.height;  
		int childHeightSpec;  
		if (lpHeight > 0) {  
			
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);  
			
		} else {  
			
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
			
		}  
		child.measure(childWidthSpec, childHeightSpec);  

	}
	
//	 @Override  
//	 protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
//	        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,  
//	                MeasureSpec.AT_MOST);  
//	        super.onMeasure(widthMeasureSpec, expandSpec);  
//	 }  
	
	public interface OnRefreshListener
	{
		void onRefresh();
	}
	
	public interface OnChangeMarginListener
	{
		void OnChangeMargin();
	}
	
	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		firstItemIndex = arg1;
		lastItemIndex = arg1+arg2;
		visibleItemCount = arg2;
		totaltItemIndex = arg3;
		//DebugTools.getDebug().debug_v("����ˢ��", "===="+lastItemIndex);
		//�������ײ���ʱ����״̬����ˢ�µ�ʱ���Զ�ˢ�£�
		if (lastItemIndex == totaltItemIndex  && state != REFRESHING && 
			visibleItemCount != totaltItemIndex) {
			
//			footProgressBar.setVisibility(View.VISIBLE);
//			footTipsTextview.setVisibility(View.VISIBLE);
//			state = REFRESHING;
//			changeHeaderViewByState();
//			onRefresh();
		}
		
		if (arg1 == OnScrollListener.SCROLL_STATE_IDLE && 
			arg0.getLastVisiblePosition() >= (arg0.getCount() - 1)	&&
			state != REFRESHING ) {
//				
//			footProgressBar.setVisibility(View.VISIBLE);
//			footTipsTextview.setVisibility(View.VISIBLE);
//			state = REFRESHING;
//			changeHeaderViewByState();
//			onRefresh();
		}

	}
	
	 protected int getListViewScrollY() {
		 View topChild = getChildAt(0);
		 return topChild == null ? 0 : getFirstVisiblePosition() * topChild.getHeight() - topChild.getTop();
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	
	public boolean ondispatchTouchEvent(MotionEvent event)
	{
		return super.dispatchTouchEvent(event);	
		//return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = MotionEventCompat.getActionMasked(ev);


		switch (action) {
			case MotionEvent.ACTION_DOWN:
				mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
				final float initialMotionY = getMotionEventY(ev, mActivePointerId);
				if (initialMotionY == -1) {
					return false;
				}
				mInitialMotionY = initialMotionY;

				break;
			case MotionEvent.ACTION_MOVE:


				if (mActivePointerId == INVALID_POINTER) {
//                Logger.e(TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
					return false;
				}

				final float y = getMotionEventY(ev, mActivePointerId);
				if (y == -1) {
					return false;
				}
				final float yDiff = y - mInitialMotionY;

				DebugTools.getDebug().debug_v("listview_yDiff","----->>>"+yDiff);
				if (yDiff > 0) {
//					if (getChildAt(0).getMeasuredHeight() <= getHeight() + getScrollY()) {
//						return false;
//					}

					if ( firstItemIndex > 0) {
						return true;

					}

				}



				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				mActivePointerId = INVALID_POINTER;
				break;
		}

		return super.onInterceptTouchEvent(ev);
	}

	private float getMotionEventY(MotionEvent ev, int activePointerId) {
		final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
		if (index < 0) {
			return -1;
		}
		return MotionEventCompat.getY(ev, index);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		performClick();
		if (isRefreshable)
		{
			switch(event.getAction())
			{
			case MotionEvent.ACTION_DOWN:
				
				return true;
				
			case MotionEvent.ACTION_UP:
				if (state != REFRESHING) {
					
					/*
					 * ���������ײ���ʱ�򣬻�Ҫ��һ��item��index ������0 ��
					 * ��item�������Ѿ��������ֻ���Ļ����չʾ������Ž��еײ�ˢ��
					 * 
					 */
					if (lastItemIndex == totaltItemIndex && firstItemIndex != 0) {
						isBottomRefresh = true;
						
						footProgressBar.setVisibility(View.VISIBLE);
						footTipsTextview.setVisibility(View.VISIBLE);
						state = REFRESHING;
						changeHeaderViewByState();
						onRefresh();
						
					}
				}
				break;
			case MotionEvent.ACTION_MOVE:
				
				break;
			}
		}
		return super.onTouchEvent(event);
	}
	
	// ��״̬�ı�ʱ�򣬵��ø÷������Ը��½���  
	private void changeHeaderViewByState() {  
	        switch (state) {  
	   
	         case REFRESHING:  
	             
	        	 //addFooterView(footView);
	        	 footView.setVisibility(View.VISIBLE);
	             footProgressBar.setVisibility(View.VISIBLE);
	             footTipsTextview.setVisibility(View.VISIBLE);
	             footTipsTextview.setText(R.string.list_footer_loading);

	             break;  
	             
	         case DONE:  
	             
	             footProgressBar.setVisibility(View.INVISIBLE);
	             //footView.setVisibility(View.GONE);
	             
	             //removeFooterView(footView);
	             //footTipsTextview.setVisibility(View.GONE);
	             footTipsTextview.setText(R.string.list_footer_load_more);

	            break;  
	        }


		if (getAdapter() != null) {

			int count = getAdapter().getCount();
			if (count < 10) {

				footView.setVisibility(View.GONE);
			}
			else {

				footView.setVisibility(View.VISIBLE);
				if (isBottomShow) {

					footBottomView.setVisibility(View.VISIBLE);
				}
				else {

					footBottomView.setVisibility(View.GONE);
				}
			}

		}
		else {

			footView.setVisibility(View.GONE);
		}
	}

	private void onRefresh() {  
		if (refreshListener != null) {  
			refreshListener.onRefresh();  
	    }  
	}  
	     
	public void setOnRefreshListener(OnRefreshListener refreshListener)
	{
	    this.refreshListener = refreshListener;
	    isRefreshable = true;
	}
	     
	public void onRefreshComplete()
	{
	    isBottomRefresh = false;
	    state = DONE;
	    changeHeaderViewByState();  
	}
	     
	public void setAdapter(BaseAdapter adapter)
	{
	    super.setAdapter(adapter);  
	}
}
