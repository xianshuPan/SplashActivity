package com.hylg.igolf.ui.view;


import com.hylg.igolf.R;

import android.content.Context;
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
	
	public final static int 			REFRESHING = 2;  
	public final static int 			DONE = 3;  
	
	/*ʵ�ʵ�padding�ľ����������ƫ�ƾ���ı���  */ 
	private LayoutInflater 				inflater;  
 	private LinearLayout 				footView;  
 	private TextView 					footTipsTextview;   
	private ProgressBar 				footProgressBar; 
    
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
    public boolean 						isBottomRefresh = false;
    
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
	
	void initView(Context context)
	{
		inflater = LayoutInflater.from(context);
		
		footView = (LinearLayout)inflater.inflate(R.layout.ehecd_listview_footer, null);
		footProgressBar  = (ProgressBar)footView.findViewById(R.id.refresh_list_footer_progressbar);
		footTipsTextview = (TextView)footView.findViewById(R.id.refresh_list_footer_text);
		
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
		public void onRefresh();
	}
	
	public interface OnChangeMarginListener
	{
		public void OnChangeMargin();
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
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event)
	{
		 return super.onInterceptTouchEvent(event);
		//return false;
	}
	
	public boolean ondispatchTouchEvent(MotionEvent event)
	{
		return super.dispatchTouchEvent(event);	
		//return false;
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
	             Log.v(TAG, "��ǰ״̬,����ˢ��...");  
	             break;  
	             
	         case DONE:  
	             
	             footProgressBar.setVisibility(View.INVISIBLE);
	             //footView.setVisibility(View.GONE);
	             
	             //removeFooterView(footView);
	             //footTipsTextview.setVisibility(View.GONE);
	             footTipsTextview.setText(R.string.list_footer_load_more);
	             Log.v(TAG, "��ǰ״̬��done");  
	            break;  
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
