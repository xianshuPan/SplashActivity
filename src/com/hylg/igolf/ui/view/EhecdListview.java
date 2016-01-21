package com.hylg.igolf.ui.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class EhecdListview extends ListView implements OnScrollListener {
	
	private static final String 		TAG = "EhecdListview";
	
	public final static int 			RELEASE_To_REFRESH = 0;  
	public final static int 			PULL_To_REFRESH = 1;  
	public final static int 			REFRESHING = 2;  
	public final static int 			DONE = 3;  
	public final static int 			LOADING = 4;  
	
	/*ʵ�ʵ�padding�ľ����������ƫ�ƾ���ı���  */ 
	private final static int 			RATIO = 3;  
	private LayoutInflater 				inflater;  
 	private LinearLayout 				headView;  
 	private LinearLayout 				footView;
	private View                        footBottomView;
 	private TextView 					tipsTextview;  
 	private TextView 					footTipsTextview;  
	private TextView 					lastUpdatedTextView;  
	private ImageView 					arrowImageView;  
	private ProgressBar 				progressBar;  
	private ProgressBar 				footProgressBar; 
    private RotateAnimation 			animation;  
    private RotateAnimation 			reverseAnimation; 
    
    /*���ڱ�֤startY��ֵ��һ�������
     * touch�¼���ֻ����¼һ�� */  
    private boolean 					isRecored;  
    private int 						headContentWidth;  
    private int 						headContentHeight;  
    private int 						startY;  
    public int 							firstItemIndex;
    private int 						lastItemIndex;  
    public int 							visibleItemCount;
    public int 							totaltItemIndex;  
    public int 							state;  
    private boolean 					isBack;  
    private OnRefreshListener 			refreshListener;  
    private OnLoadMoreListener 			loadMoreListener;  
    private boolean 					isRefreshable;  
    
    /*��һ����������ʾ���ǲ��ǵײ�ˢ��*/
    public boolean 						isBottomRefresh = false,isBottomShow;

	public EhecdListview(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView(context);
	}
	
	public EhecdListview(Context context,AttributeSet set) {
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
		headView = (LinearLayout)inflater.inflate(R.layout.ehecd_listview_header, null);
		arrowImageView = (ImageView)headView.findViewById(R.id.refresh_list_header_pull_down);
		progressBar = (ProgressBar)headView.findViewById(R.id.refresh_list_header_progressbar);
		tipsTextview = (TextView)headView.findViewById(R.id.refresh_list_header_text);
		lastUpdatedTextView = (TextView)headView.findViewById(R.id.refresh_list_header_last_update);
		
		footView = (LinearLayout)inflater.inflate(R.layout.ehecd_listview_footer, null);
		footBottomView = footView.findViewById(R.id.refresh_list_footer_bottom_view);
		footProgressBar  = (ProgressBar)footView.findViewById(R.id.refresh_list_footer_progressbar);
		footTipsTextview = (TextView)footView.findViewById(R.id.refresh_list_footer_text);
		footTipsTextview.setText(R.string.list_footer_load_more);
		
		measureView(headView);  
		DebugTools.getDebug().debug_v("headWide", "======="+headView.getWidth());
		DebugTools.getDebug().debug_v("headheight", "======="+headView.getHeight());
		measureView(footView); 
		DebugTools.getDebug().debug_v("footerWide", "======="+footView.getWidth());
		DebugTools.getDebug().debug_v("footerheight", "======="+footView.getHeight());
		headContentHeight = headView.getMeasuredHeight();
		headContentWidth = footView.getMeasuredHeight();
		headView.setPadding(0, -1*headContentHeight, 0, 0);
		headView.invalidate();
		//footView.setPadding(0, 0, 0, headContentWidth);
		footView.invalidate();
		
		addHeaderView(headView, null, false);
		addFooterView(footView,null,false);
		requestFocus();
		setOnScrollListener(this);
		
		animation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(400);
		animation.setFillAfter(true);
		
		reverseAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(250);
		reverseAnimation.setFillAfter(true);
		
		state = DONE;
		isRefreshable = false;
		
	}
	
	public void measureView(View child)
	{
		ViewGroup.LayoutParams p = child.getLayoutParams();  
		if (p == null) {  
			p = new ViewGroup.LayoutParams(  ViewGroup.LayoutParams.WRAP_CONTENT,  ViewGroup.LayoutParams.WRAP_CONTENT);  
		}  
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 , p.width);
		int lpHeight = p.height;  
		int childHeightSpec;  
		if (lpHeight > 0) {  
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);  
		} else {  
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);  
		}  
		child.measure(childWidthSpec, childHeightSpec);  

	}
	
	public interface OnRefreshListener
	{
		void onRefresh();
	}
	
	public interface OnLoadMoreListener
	{
		void onLoadMore();
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
		
		//�������ײ���ʱ����״̬����ˢ�µ�ʱ���Զ�ˢ�£�
//		if (arg1 == OnScrollListener.SCROLL_STATE_IDLE && 
//				arg0.getLastVisiblePosition() >= (arg0.getCount() - 1)	&&
//				state != REFRESHING ) {
//			
//			isBottomRefresh = true;
//			state = REFRESHING;
//			changeHeaderViewByState();
//			
//			
//			//onRefresh();
//			
//			//onLoadMore();
//			//DebugTools.getDebug().debug_v("���ײ�ˢ��", "=======");
//		}
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
		if(isRefreshable)
		{
			switch(event.getAction())
			{
			case MotionEvent.ACTION_DOWN:
				if (firstItemIndex == 0 && !isRecored) {
					isRecored = true;
					startY = (int)event.getY();
				}
				break;
				
			case MotionEvent.ACTION_UP:
				if(state != LOADING && state != REFRESHING) {
					if (state == DONE) {
						//ʲô������
					}
					if (state == PULL_To_REFRESH) {
						state=DONE;
						changeHeaderViewByState();
					}
					if (state == RELEASE_To_REFRESH) {
						state = REFRESHING;
						changeHeaderViewByState();
						onRefresh();
					}
					
					/*���������ײ���ʱ�򣬻�Ҫ��һ��item��index ������0 ��
					 * ��item�������Ѿ��������ֻ���Ļ����չʾ������Ž��еײ�ˢ��
					 * 
					 */
					if (lastItemIndex == totaltItemIndex && firstItemIndex != 0) {
						state = REFRESHING;
						changeHeaderViewByState();
						onLoadMore();
						System.out.println("+++++++++++++++++++++"+lastItemIndex);
					}
				}
				isRecored = false;
				isBack = false;
				break;
			case MotionEvent.ACTION_MOVE:
				int tempY = (int)event.getY();
				if (!isRecored && firstItemIndex == 0) {
					isRecored = true;
					startY = tempY;
				}
				if (state != REFRESHING && state != LOADING && isRecored)
				{
					if (state == RELEASE_To_REFRESH) {
						setSelection(0);
						// �������ˣ��Ƶ�����Ļ�㹻�ڸ�head�ĳ̶ȣ����ǻ�û���Ƶ�ȫ���ڸǵĵز�  
						if (((tempY - startY) / RATIO < headContentHeight) && (tempY - startY) > 0) {  
							state = PULL_To_REFRESH;  
							changeHeaderViewByState();  
							Log.v(TAG, "���ɿ�ˢ��״̬ת�䵽����ˢ��״̬");  
						 } else if (tempY - startY <= 0) {  
							 state = DONE;  
						     changeHeaderViewByState();  
						     Log.v(TAG, "���ɿ�ˢ��״̬ת�䵽done״̬");  
						  } else { 
						    	// �������ˣ����߻�û�����Ƶ���Ļ�����ڸ�head�ĵز�  
						        // ���ý����ر�Ĳ�����ֻ�ø���paddingTop��ֵ������  
	                  }  

				}
					if (state == PULL_To_REFRESH)
					{
						setSelection(0);  
						 
						// ���������Խ���RELEASE_TO_REFRESH��״̬  
						if ((tempY - startY) / RATIO >= headContentHeight) {  
						 state = RELEASE_To_REFRESH;  
						 isBack = true;  
						 changeHeaderViewByState();  
						 Log.v(TAG, "��done��������ˢ��״̬ת�䵽�ɿ�ˢ��");  
						  }  
						 // ���Ƶ�����  
						 else if (tempY - startY <= 0) {  
						 state = DONE;  
						 changeHeaderViewByState();  
						 Log.v(TAG, "��DOne��������ˢ��״̬ת�䵽done״̬");  
						}  
					}
					 // done״̬��  
					 if (state == DONE) {  
						 if (tempY - startY > 0) {  
							 state = PULL_To_REFRESH;  
							 changeHeaderViewByState();  
						 }  
					 }  
					// ����headView��size  
					 if (state == PULL_To_REFRESH) {  
						 headView.setPadding(0, -1 * headContentHeight  + (tempY - startY) / RATIO, 0, 0);  
					 }  
					  // ����headView��paddingTop  
					 if (state == RELEASE_To_REFRESH) {  
						 headView.setPadding(0, (tempY - startY) / RATIO - headContentHeight, 0, 0);  
					                 }  
				}
				break;
			}
		}
		return super.onTouchEvent(event);
	 //return true;
	}
	
	// ��״̬�ı�ʱ�򣬵��ø÷������Ը��½���  
	public void changeHeaderViewByState() {
	        switch (state) {  
	        case RELEASE_To_REFRESH:  
	             arrowImageView.setVisibility(View.VISIBLE);  
	             progressBar.setVisibility(View.GONE);  
	             tipsTextview.setVisibility(View.VISIBLE);  
	             lastUpdatedTextView.setVisibility(View.VISIBLE);  
	   
	             arrowImageView.clearAnimation();  
	             arrowImageView.startAnimation(animation);  
	   
	             tipsTextview.setText(R.string.put_off_refresh);  
	   
	             Log.v(TAG, "��ǰ״̬���ɿ�ˢ��");  
	             break;  
	         case PULL_To_REFRESH:  
	             progressBar.setVisibility(View.GONE);  
	             tipsTextview.setVisibility(View.VISIBLE);  
	             lastUpdatedTextView.setVisibility(View.VISIBLE);  
	             arrowImageView.clearAnimation();  
	             arrowImageView.setVisibility(View.VISIBLE);  
	             // ����RELEASE_To_REFRESH״̬ת������  
	             if (isBack) {  
	                 isBack = false;  
	                arrowImageView.clearAnimation();  
	                arrowImageView.startAnimation(reverseAnimation);  
	   
	                 tipsTextview.setText(R.string.push_down_refresh);  
	             } else {  
	                 tipsTextview.setText(R.string.push_down_refresh);  
	             }  
	            Log.v(TAG, "��ǰ״̬������ˢ��");  
	            break;  
	   
	         case REFRESHING:  
	   
	            headView.setPadding(0, 0, 0, 0);  
	   
	             progressBar.setVisibility(View.VISIBLE);  
	             arrowImageView.clearAnimation();  
	             arrowImageView.setVisibility(View.GONE);  
	             tipsTextview.setText(R.string.refreshing);  
	             lastUpdatedTextView.setVisibility(View.VISIBLE);  
	             
	             footProgressBar.setVisibility(View.VISIBLE);
	             footTipsTextview.setText(R.string.loading);
	             
	             Log.v(TAG, "��ǰ״̬,����ˢ��...");  
	             break;  
	         case DONE:  
	             headView.setPadding(0, -1 * headContentHeight, 0, 0);  
	  
	             progressBar.setVisibility(View.GONE);  
	             arrowImageView.clearAnimation();  
	             arrowImageView.setImageResource(R.drawable.refresh_list_pull_down);  
	             tipsTextview.setText(R.string.push_down_refresh);  
	             lastUpdatedTextView.setVisibility(View.VISIBLE);  

	             footProgressBar.setVisibility(View.INVISIBLE);
	             footTipsTextview.setVisibility(View.VISIBLE);
	             footTipsTextview.setText(R.string.list_footer_load_more);
	             Log.v(TAG, "��ǰ״̬��done");  
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

	     }  
	     
	private void onRefresh() {  
		if (refreshListener != null) {  
			refreshListener.onRefresh();  
	    }  
	}  
	
	private void onLoadMore() {  
		if (loadMoreListener != null) {  
			loadMoreListener.onLoadMore();  
	    }  
	}  
	     
	public void setOnRefreshListener(OnRefreshListener refreshListener)
	{
	    this.refreshListener = refreshListener;
	    isRefreshable = true;
	}
	
	public void setOnLoadMoreListener(OnLoadMoreListener refreshListener)
	{
	    this.loadMoreListener = refreshListener;
	    isRefreshable = true;
	}
	     
	public void onRefreshComplete()
	{
	    isBottomRefresh = false;
	    state = DONE;
	    SimpleDateFormat format=new SimpleDateFormat("yyyy:MM:dd  HH:mm");  
	    String date=format.format(new Date());  
	    lastUpdatedTextView.setText("上次更新:" + date);
	    changeHeaderViewByState();  
	}
	     
	public void setAdapter(BaseAdapter adapter)
	{
	    SimpleDateFormat format=new SimpleDateFormat("yyyy:MM:dd  HH:mm");  
	    String date=format.format(new Date());  
	    lastUpdatedTextView.setText("上次更新: " + date);
	    super.setAdapter(adapter);  
	}
}
