package com.hylg.igolf.ui.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.R;

import android.content.Context;
import android.support.v4.view.ViewPager;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.view.View.OnTouchListener;


public class RefreshView extends LinearLayout implements OnTouchListener {
	
	private static final String 		TAG = "EhecdRefreshView";
	
	public final static int 			RELEASE_To_REFRESH = 0;  
	public final static int 			PULL_To_REFRESH = 1;  
	public final static int 			REFRESHING = 2;  
	public final static int 			DONE = 3;  
	//public final static int 			LOADING = 4;  
	
	/*ʵ�ʵ�padding�ľ����������ƫ�ƾ���ı���  */ 
	private final static int 			RATIO = 3;  
	private LayoutInflater 				inflater;  
 	private LinearLayout 				headView;  
 	private LinearLayout 				footView;  
 	private TextView 					tipsTextview;  
 	private TextView 					footTipsTextview;  
	private TextView 					lastUpdatedTextView;  
	private ImageView 					arrowImageView;  
	private ProgressBar 				progressBar;  
	private ProgressBar 				footProgressBar; 
    private RotateAnimation 			animation;  
    private RotateAnimation 			reverseAnimation; 
    
    
    private RelativeLayout              mHeadRelative ;
    private ViewPager             		mTabHost ;
    
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
    public boolean 						isBottomRefresh = false,
    									loadOnce = false;
    
    
    private ListView                     mRefreshView = null;
    
    private ISizeChanged				 mSizeChanged = null;
    
    private Context                      mContext = null;
    
    private boolean 					 isUpMove = false;                    
    
    public interface OnRefreshListener {
		public void onRefreshData();
	}
	
	public interface OnLoadMoreListener {
		public void onLoadMore();
	}
    
    public interface ISizeChanged {
    	
    	public void sizeChanged(int w, int h, int oldw, int oldh);
    }
    
    public void setSizeChanged(ISizeChanged m) {
    	 
    	if (m == null) {
    		 return;
    	}
    	mSizeChanged = m;
    }

	public RefreshView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView(context);
	}
	
	public RefreshView(Context context,AttributeSet set) {
		super(context,set);
		// TODO Auto-generated constructor stub
		initView(context);
	}
	
	void initView(Context context)
	{
		
		mContext = context;
		
		inflater = LayoutInflater.from(context);
		headView = (LinearLayout)inflater.inflate(R.layout.ehecd_listview_header, null);
		arrowImageView = (ImageView)headView.findViewById(R.id.refresh_list_header_pull_down);
		progressBar = (ProgressBar)headView.findViewById(R.id.refresh_list_header_progressbar);
		tipsTextview = (TextView)headView.findViewById(R.id.refresh_list_header_text);
		lastUpdatedTextView = (TextView)headView.findViewById(R.id.refresh_list_header_last_update);
		
		footView = (LinearLayout)inflater.inflate(R.layout.ehecd_listview_footer, null);
		footProgressBar  = (ProgressBar)footView.findViewById(R.id.refresh_list_footer_progressbar);
		footTipsTextview = (TextView)footView.findViewById(R.id.refresh_list_footer_text);
		footTipsTextview.setText(R.string.push_down_load_more);
		
		measureView(headView);  
		DebugTools.getDebug().debug_v("headWide", "======="+headView.getWidth());
		DebugTools.getDebug().debug_v("headheight", "======="+headView.getHeight());
		measureView(footView); 
		DebugTools.getDebug().debug_v("footerWide", "======="+footView.getWidth());
		DebugTools.getDebug().debug_v("footerheight", "======="+footView.getHeight());
		headContentHeight = headView.getMeasuredHeight();
		headContentWidth = footView.getMeasuredWidth();
		headView.setPadding(0, -1*headContentHeight, 0, 0);
		headView.invalidate();
		//footView.setPadding(0, 0, 0, headContentWidth);
		footView.invalidate();
		
		addView(headView,0);
		
		//addView(footView);
		//addFooterView(footView,null,false);
		requestFocus();
		//setOnScrollListener(this);

		//setOnTouchListener(this);
		
		animation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, 
						RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(400);
		animation.setFillAfter(true);
		
		reverseAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, 
							   RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(250);
		reverseAnimation.setFillAfter(true);
		
		state = DONE;
		isRefreshable = false;
		
	}
	
	 /** 
     * ����һЩ�ؼ��Եĳ�ʼ�����������磺������ͷ����ƫ�ƽ������أ���ListViewע��touch�¼��� 
     */  
    @Override  
    protected void onLayout(boolean changed, int l, int t, int r, int b) {  
        super.onLayout(changed, l, t, r, b);  
        if (changed && !loadOnce) {  
           // hideHeaderHeight = -header.getHeight();  
           // headerLayoutParams = (MarginLayoutParams) header.getLayoutParams();  
           // headerLayoutParams.topMargin = hideHeaderHeight;  
        	
        	mRefreshView = (ListView) getChildAt(1);  
        	mRefreshView.setOnTouchListener(this); 
        	
        	//mRefreshView.addFooterView(footView);
        	
        	LinearLayout.LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        	lp.setMargins(0, 0, 0, -20);
        	//addView(footView,2);       	
        	//mRefreshView.addView(footView);
        	
        	mRefreshView.addFooterView(footView,null,false);
    
        	mRefreshView.setOnScrollListener(new OnScrollListener() {
				
				@Override
				public void onScrollStateChanged(AbsListView arg0, int arg1) {
					// TODO Auto-generated method stub
					
					if (arg1 == OnScrollListener.SCROLL_STATE_IDLE && 
						arg0.getLastVisiblePosition() >= (arg0.getCount() - 1)	&&
						state != REFRESHING ) {
						
						state = REFRESHING;
						
						isBottomRefresh = true;
						
						changeHeaderViewByState();
						onLoadMore();
					}
				}
				
				@Override
				public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
					// TODO Auto-generated method stub
					firstItemIndex = arg1;
					lastItemIndex = arg1+arg2;
					visibleItemCount = arg2;
					totaltItemIndex = arg3;
				}
			});
        	
            loadOnce = true;  
        }  
    }  
    
    @Override  
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {  
        super.onSizeChanged(w, h, oldw, oldh);  

    }  
	
	public void measureView(View child)
	{
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
	
	
	public interface OnChangeMarginListener
	{
		public void OnChangeMargin();
	}
	
//	@Override
//	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
//		// TODO Auto-generated method stub
//		firstItemIndex = arg1;
//		lastItemIndex = arg1+arg2;
//		visibleItemCount = arg2;
//		totaltItemIndex = arg3;
//		
//		//�������ײ���ʱ����״̬����ˢ�µ�ʱ���Զ�ˢ�£�
//		if (lastItemIndex == totaltItemIndex && visibleItemCount != totaltItemIndex && state != REFRESHING && state != LOADING) {
//			isBottomRefresh = true;
//			state = REFRESHING;
//			changeHeaderViewByState();
//			onRefresh();
//			//DebugTools.getDebug().debug_v("���ײ�ˢ��", "=======");
//		}
//	}
//
//	@Override
//	public void onScrollStateChanged(AbsListView arg0, int arg1) {
//		// TODO Auto-generated method stub
//		
//	}
	
//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent event)
//	{
//		 return super.onInterceptTouchEvent(event);
//		//return false;
//	}
//	
//	public boolean ondispatchTouchEvent(MotionEvent event)
//	{
//		return super.dispatchTouchEvent(event);	
//		//return false;
//	}
	
//	@Override
//	public boolean onTouchEvent(MotionEvent event)
//	{
//		if(isRefreshable)
//		{
//			switch(event.getAction())
//			{
//			case MotionEvent.ACTION_DOWN:
//				if (firstItemIndex == 0 && !isRecored) {
//					isRecored = true;
//					startY = (int)event.getY();
//				}
//				break;
//				
//			case MotionEvent.ACTION_UP:
//				//if(state != LOADING && state != REFRESHING) {
//				if(state != REFRESHING) {
//					if (state == DONE) {
//						//ʲô������
//					}
//					if (state == PULL_To_REFRESH) {
//						state=DONE;
//						changeHeaderViewByState();
//					}
//					if (state == RELEASE_To_REFRESH) {
//						state = REFRESHING;
//						changeHeaderViewByState();
//						onRefresh();
//					}
//					
//					/*���������ײ���ʱ�򣬻�Ҫ��һ��item��index ������0 ��
//					 * ��item�������Ѿ��������ֻ���Ļ����չʾ������Ž��еײ�ˢ��
//					 * 
//					 */
//					if (lastItemIndex == totaltItemIndex && firstItemIndex != 0) {
//						state = REFRESHING;
//						changeHeaderViewByState();
//						onRefresh();
//						System.out.println("+++++++++++++++++++++"+lastItemIndex);
//					}
//				}
//				isRecored = false;
//				isBack = false;
//				break;
//			case MotionEvent.ACTION_MOVE:
//				int tempY = (int)event.getY();
//				if (!isRecored && firstItemIndex == 0) {
//					isRecored = true;
//					startY = tempY;
//				}
//				//if (state != REFRESHING && state != LOADING && isRecored)
//				
//				if (state != REFRESHING  && isRecored)
//				{
//					if (state == RELEASE_To_REFRESH) {
//						//setSelection(0);
//						// �������ˣ��Ƶ�����Ļ�㹻�ڸ�head�ĳ̶ȣ����ǻ�û���Ƶ�ȫ���ڸǵĵز�  
//						if (((tempY - startY) / RATIO < headContentHeight) && (tempY - startY) > 0) {  
//							state = PULL_To_REFRESH;  
//							changeHeaderViewByState();  
//							Log.v(TAG, "���ɿ�ˢ��״̬ת�䵽����ˢ��״̬");  
//						 } else if (tempY - startY <= 0) {  
//							 state = DONE;  
//						     changeHeaderViewByState();  
//						     Log.v(TAG, "���ɿ�ˢ��״̬ת�䵽done״̬");  
//						  } else { 
//						    	// �������ˣ����߻�û�����Ƶ���Ļ�����ڸ�head�ĵز�  
//						        // ���ý����ر�Ĳ�����ֻ�ø���paddingTop��ֵ������  
//	                  }  
//
//				}
//					if (state == PULL_To_REFRESH)
//					{
//						//setSelection(0);  
//						 
//						// ���������Խ���RELEASE_TO_REFRESH��״̬  
//						if ((tempY - startY) / RATIO >= headContentHeight) {  
//						 state = RELEASE_To_REFRESH;  
//						 isBack = true;  
//						 changeHeaderViewByState();  
//						 Log.v(TAG, "��done��������ˢ��״̬ת�䵽�ɿ�ˢ��");  
//						  }  
//						 // ���Ƶ�����  
//						 else if (tempY - startY <= 0) {  
//						 state = DONE;  
//						 changeHeaderViewByState();  
//						 Log.v(TAG, "��DOne��������ˢ��״̬ת�䵽done״̬");  
//						}  
//					}
//					 // done״̬��  
//					 if (state == DONE) {  
//						 if (tempY - startY > 0) {  
//							 state = PULL_To_REFRESH;  
//							 changeHeaderViewByState();  
//						 }  
//					 }  
//					// ����headView��size  
//					 if (state == PULL_To_REFRESH) {  
//						 headView.setPadding(0, -1 * headContentHeight  + (tempY - startY) / RATIO, 0, 0);  
//					 }  
//					  // ����headView��paddingTop  
//					 if (state == RELEASE_To_REFRESH) {  
//						 headView.setPadding(0, (tempY - startY) / RATIO - headContentHeight, 0, 0);  
//					                 }  
//				}
//				break;
//			}
//		}
//		return super.onTouchEvent(event);
//	 //return true;
//	}
	
	// ��״̬�ı�ʱ�򣬵��ø÷������Ը��½���  
	private void changeHeaderViewByState() {  
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
	   
	        	 if (!isBottomRefresh) {
	        		 
	        		 headView.setPadding(0, 0, 0, 0);  
		             progressBar.setVisibility(View.VISIBLE);  
		             arrowImageView.clearAnimation();  
		             arrowImageView.setVisibility(View.GONE);  
		             tipsTextview.setText(R.string.refreshing_cn);  
		             lastUpdatedTextView.setVisibility(View.VISIBLE);  
	        	 }
	             
	             
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
	             footTipsTextview.setText(R.string.push_down_load_more);
	             Log.v(TAG, "��ǰ״̬��done");  
	            break;  
	        }  
	     }  
	     
	private void onRefresh() {  
		if (refreshListener != null) {  
			refreshListener.onRefreshData();  
	    }  
	}  
	     
	public void setOnRefreshListener(OnRefreshListener refreshListener1)
	{
	    this.refreshListener = refreshListener1;
	    isRefreshable = true;
	}
	
	private void onLoadMore() {  
		if (loadMoreListener != null) {  
			loadMoreListener.onLoadMore();  
	    }  
	}  
	     
	public void setOnLoadMoreListener(OnLoadMoreListener loadMoreListener)
	{
	    this.loadMoreListener = loadMoreListener;
	    isRefreshable = true;
	}
	     
	public void onRefreshComplete()
	{
	    isBottomRefresh = false;
	    state = DONE;
	    SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd HH:mm");  
	    String date=format.format(new Date());  
	    lastUpdatedTextView.setText("上次更新" + date);  
	    changeHeaderViewByState();  
	}
	     
	public void setAdapter(BaseAdapter adapter)
	{
	    //SimpleDateFormat format=new SimpleDateFormat("yyyy��MM��dd��  HH:mm");  
	   // String date=format.format(new Date());  
	   // lastUpdatedTextView.setText("������ " + date);  
	   // super.setAdapter(adapter);  
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		if(isRefreshable)
		{
			float mRefreshViewScrollX = mRefreshView.getAlpha();
			
			//DebugTools.getDebug().debug_v(TAG, "mRefreshViewScrollX------>>>>>>>"+mRefreshViewScrollX);
			
			if (firstItemIndex > 0) {
				
				return false;
			}
			
			switch(event.getAction())
			{
			case MotionEvent.ACTION_DOWN:
				if (!isRecored) {
					isRecored = true;
					startY = (int)event.getY();
				}
				break;
				
			case MotionEvent.ACTION_UP:
				//if(state != LOADING && state != REFRESHING) {
				arg0.performClick();
				
				int upY = (int)event.getY();
				if( state != REFRESHING) {
					
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
//					if ((upY > startY) && (upY - startY)> 20 ) {
//						
//						if (mHeadRelative != null && isUpMove ) {
//							
//							//Animation anim1 = AnimationUtils.loadAnimation(mContext, R.anim.popup_enter);
//							
//							TranslateAnimation anim1 = new TranslateAnimation(0, 0, 0, mHeadRelative.getHeight());
//							anim1.setDuration(500);
//							anim1.setAnimationListener(new AnimationListener() {
//								
//								@Override
//								public void onAnimationStart(Animation arg0) {
//									// TODO Auto-generated method stub
//									
//								}
//								
//								@Override
//								public void onAnimationRepeat(Animation arg0) {
//									// TODO Auto-generated method stub
//									
//								}
//								
//								@Override
//								public void onAnimationEnd(Animation arg0) {
//									// TODO Auto-generated method stub
//									///mHeadRelative.setVisibility(View.VISIBLE);
//									
////									RelativeLayout.LayoutParams para = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
////									para.setMargins(0, mHeadRelative.getHeight(), 0, 0);
////									mTabHost.setLayoutParams(para);
//									
//									isUpMove = false;
//									
//									//mHeadRelative.clearAnimation();
//									//mTabHost.clearAnimation();
//									//mTabHost.invalidate();
//								}
//							});
//							
//							Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.popup_enter_up);
//							
//							TranslateAnimation anim2 = new TranslateAnimation(0, 0, -mHeadRelative.getHeight(), 0);
//							anim2.setDuration(500);
//							
//							ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(mHeadRelative, "translationY", -mHeadRelative.getHeight(), 0);  
//							scaleYAnimator.setDuration(500);  
//							
//							scaleYAnimator.start();
//							
//							ObjectAnimator scaleYAnimator1 = ObjectAnimator.ofFloat(mTabHost, "translationY", -mHeadRelative.getHeight(), 0);  
//							scaleYAnimator1.setDuration(500);  
//							
//							scaleYAnimator1.start();
//							
//							//mHeadRelative.startAnimation(anim2);
//							
//							//mTabHost.startAnimation(anim1);
//							
//						}
//						
//					} else if ((startY-upY)> 5) {
//						
//						if (mHeadRelative != null && !isUpMove ) {
//							
//							//Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.popup_exit);
//							TranslateAnimation anim = new TranslateAnimation(0, 0, 0,-mHeadRelative.getHeight() );
//							anim.setDuration(500);
//							anim.setAnimationListener(new AnimationListener() {
//								
//								@Override
//								public void onAnimationStart(Animation arg0) {
//									// TODO Auto-generated method stub
//									
//								}
//								
//								@Override
//								public void onAnimationRepeat(Animation arg0) {
//									// TODO Auto-generated method stub
//									
//								}
//								
//								@Override
//								public void onAnimationEnd(Animation arg0) {
//									// TODO Auto-generated method stub
//									//mHeadRelative.setVisibility(View.GONE);
//									
////									RelativeLayout.LayoutParams para = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
////									//para.setMargins(0, mHeadRelative.getHeight(), 0, 0);
////									mTabHost.setLayoutParams(para);
//									
//									isUpMove = true;
//									
//									mHeadRelative.clearAnimation();
//									mTabHost.clearAnimation();
//									mTabHost.invalidate();
//								}
//							});
//							
//							Animation anim1 = AnimationUtils.loadAnimation(mContext, R.anim.popup_exit_up);
//							
//							ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(mHeadRelative, "translationY", 0, -mHeadRelative.getHeight());  
//							scaleYAnimator.setDuration(500);  
//							
//							scaleYAnimator.start();
//							
//							ObjectAnimator scaleYAnimator1 = ObjectAnimator.ofFloat(mTabHost, "translationY", 0, -mHeadRelative.getHeight());  
//							scaleYAnimator1.setDuration(500);  
//							scaleYAnimator1.start();
//							
//							//mHeadRelative.startAnimation(anim);
//							
//							//mTabHost.startAnimation(anim);
//						}
//					}
				}
				isRecored = false;
				isBack = false;
				break;
			case MotionEvent.ACTION_MOVE:
				int tempY = (int)event.getY();
				if (!isRecored ) {
					isRecored = true;
					startY = tempY;
				}
				
				
				//if (state != REFRESHING && state != LOADING && isRecored)
				if (state != REFRESHING  && isRecored )
				{
					if (state == RELEASE_To_REFRESH) {
						//setSelection(0);
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
							//setSelection(0);  
							 
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
	
	
	public void setHeadRelative (RelativeLayout relative) {
		
		mHeadRelative = relative;
	}
	
	public void setTabHost (ViewPager tabHost) {
		
		mTabHost = tabHost;
	}
}
