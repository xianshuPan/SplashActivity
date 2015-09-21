package com.hylg.igolf.ui.friend;

import java.util.ArrayList;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.FriendHotItem;
import com.hylg.igolf.cs.loader.GetFriendHotListLoader;
import com.hylg.igolf.cs.loader.GetFriendHotListLoader.GetFriendHotListCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.imagepicker.Config;
import com.hylg.igolf.ui.view.LoadFail;
import com.hylg.igolf.ui.view.RefreshView;
import com.hylg.igolf.ui.view.LoadFail.onRetryClickListener;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

import android.content.Context;
import android.content.IntentFilter;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;

public class FriendHotActivity extends FragmentActivity {
	
	private final String              TAG                          = "FriendHotFrg";
//	OnBackListener mListener;

//	public interface OnBackListener {
//		public void backEvent();
//	}
	
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (OnBackListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
//        }
//    }
	
	private RelativeLayout				mRefreshView                = null;
	
	private ListView                    mList                       = null;
	
	private FriendHotAdapter			mFriendHotAdapter			= null;
	
	private GetFriendHotListLoader 		reqLoader 					= null;
	
	private LoadFail 					loadFail;
	
	private String 						sn							= "";
	private int 						startPage					= 0, 
										pageSize					= 0;
	
	private LayoutInflater              mLayoutInflater             = null;
	
	
	/*添加评论*/
	
	private View                        mCommentsPopView            = null;
	
	
	private View                        arg0View,ParentView ;
	int inputHeight,windowHeight =0;
	private RefreshView              	mheadRelative = null;
	
	KeyboardView asdfads;
	
	InputMethodManager manager;
	
	private EditText replyEdit;// 回复框
	
	
	private PopupWindow mCommentPop1;
	private PopupWindow editWindow;// 回复window

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.friend_frg_hot12);
		
		loadFail = new LoadFail(this);
		
		loadFail.setOnRetryClickListener(retryListener);
		
		sn = MainApp.getInstance().getCustomer().sn;
		startPage = MainApp.getInstance().getGlobalData().startPage;
		pageSize = MainApp.getInstance().getGlobalData().pageSize;
		
		onCreateView();
		
		onViewCreated();
		
	}

	public View onCreateView() {
		
		mLayoutInflater = this.getLayoutInflater();

		//View parentView = mLayoutInflater.inflate(R.layout.friend_frg_hot12, null, false);
		
		mRefreshView   = (RelativeLayout) findViewById(R.id.friend_frg_hot_refresh);
		
		mList          = (ListView) findViewById(R.id.friend_frg_hot_listview);
		//mheadRelative  = (RefreshView) parentView.findViewById(R.id.friend_frg_hot_refresh);
		
		mCommentsPopView = mLayoutInflater.inflate(R.layout.friend_add_comments_view, null);
		
		LinearLayout.LayoutParams asdf = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 50);
		
		windowHeight = getResources().getDisplayMetrics().heightPixels;
		
		asdf.setMargins(0, windowHeight-inputHeight, 0, 0);
		asdfads = new KeyboardView(this, null);
		inputHeight = asdfads.getHeight();
		DebugTools.getDebug().debug_v(TAG, "____......."+inputHeight);
		//mRefreshView.addView(mCommentsPopView, asdf);
		
		//replyEdit = (EditText) mCommentsPopView.findViewById(R.id.friend_comments_input_edit);
		
		mCommentPop1 = new PopupWindow(mCommentsPopView,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT,true);  
		editWindow 	= new PopupWindow(mCommentsPopView, LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT, true);
		
		mCommentPop1.setBackgroundDrawable(getResources().getDrawable(R.color.color_white)); 
		editWindow.setBackgroundDrawable(getResources().getDrawable(R.color.color_white));
		
		// 以下两句不能颠倒
		mCommentPop1.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
		editWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
		
		
		mCommentPop1.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		editWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		
		// 以下两句不能颠倒
		
		
		
		return mRefreshView;
		// return super.onCreateView(, container, savedInstanceState);
	}
	

	public void onViewCreated() {
		
		DebugTools.getDebug().debug_v(TAG, ">>>>>>>onViewCreated");
		
//		mRefreshView.setOnRefreshListener(new OnRefreshListener() {
//			
//			@Override
//			public void onRefresh() {
//				// TODO Auto-generated method stub
//				
//			}
//		});

//		if(null != mFriendHotAdapter) {
//			
//			mList.setAdapter(mFriendHotAdapter);
//			//Utils.logh(TAG, "exist myInviteAdapter " + myInviteAdapter);
//			
//		} else {
			
			initDataAysnc();
//			loadFail.displayFail("加载失败！");
		//}
		
		// 注册监听
		IntentFilter filter = new IntentFilter();
		filter.addAction(Const.IG_ACTION_MY_INVITE_JPUSH_NOTIFY);
		//this.registerReceiver(mReceiver, filter);
	}
	
	private void clearLoader() {
		if(isLoading()) {
			GetFriendHotListLoader loader = reqLoader;
			loader.stopTask(true);
			loader = null;
		}
	}
	
	private boolean isLoading() {
		return (null != reqLoader && reqLoader.isRunning());
	}
	
	/*初始化数据请求*/
	private void initDataAysnc() {
		if(!Utils.isConnected(this)) {
			return ;
		}
		WaitDialog.showWaitDialog(this, R.string.str_loading_msg);
		clearLoader();
		
		/*sn 暂时等于1*/
		reqLoader = new GetFriendHotListLoader(this,sn, startPage, pageSize,"",Config.FRIEND_HOT,
			new GetFriendHotListCallback() {
			
			@Override
			public void callBack(int retId, String msg, ArrayList<FriendHotItem> hotList) {
				// TODO Auto-generated method stub
				//mRefreshView.onRefreshComplete();
				
				if(BaseRequest.REQ_RET_F_NO_DATA == retId || hotList.size() == 0) {
					if(msg.trim().length() == 0) {
						msg = getString(R.string.str_hall_invite_mine_no_hint);
					}
					// display reload page
					loadFail.displayNoDataRetry(msg);
					Toast.makeText(FriendHotActivity.this, msg, Toast.LENGTH_SHORT).show();
					MainApp.getInstance().getGlobalData().setHasStartNewInvite(false);
					
				} else if(BaseRequest.REQ_RET_OK == retId) {
					
					loadFail.displayNone();
					
					DebugTools.getDebug().debug_v(TAG, "-----????initListView");
					initListView(hotList);
					
					//listFooter.refreshFooterView(inviteList.size(), pageSize);
					MainApp.getInstance().getGlobalData().setHasStartNewInvite(false);
				} else {
					loadFail.displayFail(msg);
					Toast.makeText(FriendHotActivity.this, msg, Toast.LENGTH_SHORT).show();
				}
				WaitDialog.dismissWaitDialog();
				reqLoader = null;
			}
		});
		reqLoader.requestData();
	}
	
	
	private onRetryClickListener retryListener = new onRetryClickListener() {
		@Override
		public void onRetryClick() {
			//Utils.logh(TAG, "onRetryClick ... ");
			mFriendHotAdapter = null;
			initDataAysnc();
		}
	};
	
	
	private void initListView(ArrayList<FriendHotItem> inviteList) {
		mFriendHotAdapter = new FriendHotAdapter(inviteList);
		mList.setAdapter(mFriendHotAdapter);
		//Utils.logh(TAG, "initListView myInviteAdapter " + myInviteAdapter);
	}
	
	/*朋友圈数据适配器*/
	private class FriendHotAdapter extends BaseAdapter {
		
		private ArrayList<FriendHotItem> list;
		
		public FriendHotAdapter(ArrayList<FriendHotItem> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			
			if (arg1 ==  null) {
				
				arg1 = mLayoutInflater.inflate(R.layout.friend_frg_hot_item, null);
			}
			
			TextView userName = (TextView)arg1.findViewById(R.id.user_nameText);
			
			TextView addTime = (TextView) arg1.findViewById(R.id.add_timeText);
			TextView contents = (TextView) arg1.findViewById(R.id.content_Text);
			
			final LinearLayout commensLinear = (LinearLayout)arg1.findViewById(R.id.comments_linear);
			final ImageView comments = (ImageView)arg1.findViewById(R.id.comment_image);
			comments.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					
					showPopupWindow(commensLinear);
				}
			});
			
			/*构造评论列表*/
			commensLinear.removeAllViews();
			if (list.get(arg0).comments != null && list.get(arg0).comments.size() > 0) {
				
				for (int i = 0; i < list.get(arg0).comments.size() ; i++) {
					
					LinearLayout commensLinearItem = (LinearLayout) mLayoutInflater.inflate(R.layout.friend_frg_hot_item_comment, null);
					
					TextView commentUserName = (TextView)commensLinearItem.findViewById(R.id.comment_user_name_text);
					TextView commentContents = (TextView)commensLinearItem.findViewById(R.id.comment_contents_text);
					
					commentUserName.setText(list.get(arg0).comments.get(i).get("name"));
					commentContents.setText(list.get(arg0).comments.get(i).get("content"));
					
					commensLinear.addView(commensLinearItem);
					
				}
			}
			
			userName.setText(list.get(arg0).name);
			contents.setText(list.get(arg0).content);
			addTime.setText(Utils.getDateString(FriendHotActivity.this,String.valueOf(list.get(arg0).releaseTime)));
			
			arg0View = arg1;
			return  arg1;
		}
		
		
	}
	
	
	 /**��ʾpopupwindow*/  
    private void showPopupWindow(View view){  
 
        	RelativeLayout topLayout;
        	topLayout = (RelativeLayout) mLayoutInflater.inflate(R.layout.friend_frg_hot12, null);
     	
        	
        	mCommentPop1.showAtLocation(topLayout, Gravity.BOTTOM, 0, 0);  
        	manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    		manager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        	
    		mCommentPop1.setOnDismissListener(new OnDismissListener() {  
	              
	            @Override  
	            public void onDismiss() {  
	                // TODO Auto-generated method stub  
	            	manager.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
	            }  
	        });  
        	
    }  
}
