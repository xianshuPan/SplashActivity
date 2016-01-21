package com.hylg.igolf.ui.friend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.tsz.afinal.FinalBitmap;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.FriendHotItem;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.FriendAttentionAdd;
import com.hylg.igolf.cs.request.FriendCommentsAdd;
import com.hylg.igolf.cs.request.FriendPraiseAdd;
import com.hylg.igolf.cs.loader.AsyncImageLoader;
import com.hylg.igolf.cs.loader.GetTipsDetialLoader;
import com.hylg.igolf.cs.loader.AsyncImageLoader.ImageCallback;
import com.hylg.igolf.cs.loader.GetTipsDetialLoader.GetTipsDetialCallback;
import com.hylg.igolf.cs.request.GetFriendCommentsList;
import com.hylg.igolf.cs.request.GetFriendPraiserList;
import com.hylg.igolf.ui.member.SystemBarUtils;
import com.hylg.igolf.ui.view.ListviewBottomRefresh;
import com.hylg.igolf.ui.view.LoadFail;
import com.hylg.igolf.ui.view.MyGridView;
import com.hylg.igolf.ui.view.ShareMenu;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;


public class FriendTipsDetailActivity extends FragmentActivity implements OnClickListener {
	
	private final String 				TAG 						= "FriendNewTipsCountActivity";
	
	private ImageButton  				mBack 						= null;
	
	public String 						sn							= "",
										attention_sn                = "",
										tosn                        = "",
										name                        = "",
										avatar                      = "",
										toname                      = "",
										tipsId                      = "";
	
	
	public ImageView 					avatarImage ;
	public TextView 					userName ;
	public TextView 					addTime ;
	public TextView 					contents ;
	public ImageView 					attention ;
	public ImageView 					delete ;

	public RelativeLayout 				commentsCountLinear,praisersCountLinear;
	public TextView 					commentsCountTxt, praisersCountTxt;


	public LinearLayout 				shareLinear,praiseLinear;
	public TextView 					commentTxt ;
	public ImageView 					praiseImage ;

	
	public MyGridView 					images;
	public ImageView 					image;
	public LinearLayout 				commentListLinear,praiseListLinear;
	public ListviewBottomRefresh 		commentsList,praiserList;
	private LoadFail                    commentsLoadFail,praiserLoadFail;
	private FriendItemCommentsAdapter   commentsAdapter;
	private FriendItemPraiserAdapter    praiserAdapter;

	/*点赞人*/
	public ArrayList<HashMap<String, String>> praises = null;
	/*评论*/
	public ArrayList<HashMap<String, String>> comments = null;
	
	private GetTipsDetialLoader         reqLoader;     
	private FriendHotItem               item;
	
	/*添加评论*/
	private PopupWindow                 mCommentAddPop              = null;
	private View                        mCommentsPopView            = null;
	private EditText					mCommentsEdit               = null;
	private TextView                    mCommentsAddText            = null;
	
	private InputMethodManager          mInputManager               = null;
	
	/* 
	 * 保存新增的评论
	 * */
	private HashMap<String, String>     mCurrentComments            = new HashMap<String, String>();

	private int                         commentPageNumber,praisePageNumber,pageSize;

	private FriendTipsDetailActivity    mContext = null;
	
	public static void startFriendTipsDetailActivity(Activity context,Bundle data) {

		Intent intent = new Intent(context, FriendTipsDetailActivity.class);
		intent.putExtra("Data", data);
		context.startActivityForResult(intent, Const.REQUST_CODE_SIGNATURE_MY);
		context.overridePendingTransition(R.anim.ac_slide_right_in,R.anim.ac_slide_left_out);
		
	}
	
	public static void startFriendTipsDetailActivity(Fragment context,Bundle data) {

		Intent intent = new Intent(context.getActivity(), FriendTipsDetailActivity.class);
		intent.putExtra("Data", data);
		context.startActivityForResult(intent, Const.REQUST_CODE_SIGNATURE_MY);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.friend_tips_detial);
		
		mBack =  (ImageButton)  findViewById(R.id.friend_tips_detial_back);
		
		mBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mContext.finish();
			}
		});
		
		avatarImage = (ImageView) findViewById(R.id.friend_tips_detial_user_headImage);
		userName = (TextView) findViewById(R.id.friend_tips_detial_user_nameText);
		addTime = (TextView) findViewById(R.id.friend_tips_detial_add_timeText);
		contents = (TextView) findViewById(R.id.friend_tips_detial_content_Text);
		attention = (ImageView) findViewById(R.id.friend_tips_detial_attention_image);
		delete = (ImageView) findViewById(R.id.friend_tips_detial_delete_image);

		commentsCountLinear = (RelativeLayout) findViewById(R.id.friend_tips_detial_commentsCount_linear);
		commentsCountLinear.setSelected(true);
		praisersCountLinear = (RelativeLayout) findViewById(R.id.friend_tips_detial_praiserCount_linear);
		commentsCountTxt = (TextView) findViewById(R.id.friend_tips_detial_commentsCount_txt);
		praisersCountTxt = (TextView) findViewById(R.id.friend_tips_detial_praiserCount_text);

		commentListLinear = (LinearLayout) findViewById(R.id.friend_tips_detial_comments_list_linear);
		praiseListLinear = (LinearLayout) findViewById(R.id.friend_tips_detial_praiser_list_linear);
		commentsList = (ListviewBottomRefresh)findViewById(R.id.friend_tips_detial_comments_list);
		praiserList = (ListviewBottomRefresh)findViewById(R.id.friend_tips_detial_praise_list);
		commentsLoadFail = new LoadFail(this,(RelativeLayout) findViewById(R.id.friend_tips_detial_comments_load_fail));
		praiserLoadFail = new LoadFail(this,(RelativeLayout) findViewById(R.id.friend_tips_detial_praise_load_fail));

		shareLinear = (LinearLayout) findViewById(R.id.friend_tips_detial_share_linear);
		commentTxt = (TextView) findViewById(R.id.friend_tips_detial_comment_text);
		praiseImage = (ImageView) findViewById(R.id.friend_tips_detial_praise_image);
		praiseLinear = (LinearLayout) findViewById(R.id.friend_tips_detial_praise_linear);
		images = (MyGridView) findViewById(R.id.friend_tips_detial_image_content);

		commentsList.setShowFootBottom(true);
		commentsList.setOnRefreshListener(new ListviewBottomRefresh.OnRefreshListener() {
			@Override
			public void onRefresh() {

				loadMoreCommentsData();
			}
		});
		commentsCountLinear.setOnClickListener(this);
		praisersCountLinear.setOnClickListener(this);

//		commentsList.setOnLoadMoreListener(new EhecdListview.OnLoadMoreListener() {
//			@Override
//			public void onLoadMore() {
//
//				loadMoreCommentsData();
//			}
//		});
		praiserList.setShowFootBottom(true);
		praiserList.setOnRefreshListener(new ListviewBottomRefresh.OnRefreshListener() {
			@Override
			public void onRefresh() {

				loadMorePraiseData();
			}
		});
//		praiserList.setOnLoadMoreListener(new EhecdListview.OnLoadMoreListener() {
//			@Override
//			public void onLoadMore() {
//
//				loadMorePraiseData();
//			}
//		});


		mInputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		sn = MainApp.getInstance().getCustomer().sn;
		commentPageNumber = 1;
		praisePageNumber = 1;
		pageSize = MainApp.getInstance().getGlobalData().pageSize;
		name = MainApp.getInstance().getCustomer().nickname;
		avatar = MainApp.getInstance().getCustomer().avatar;
		
		mCommentsPopView = mContext.getLayoutInflater().inflate(R.layout.friend_add_comments_view, null, false);
		mCommentsEdit = (EditText) mCommentsPopView.findViewById(R.id.friend_comments_input_edit);
		mCommentsAddText = (TextView) mCommentsPopView.findViewById(R.id.friend_comments_sent_text);
		
		mCommentsAddText.setOnClickListener(this);
		mCommentsEdit.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
				/*根据是否输入判断，发送按钮是否可以点击*/
				if (arg0.length() > 0) {
					
					mCommentsAddText.setEnabled(true);
					mCommentsAddText.setTextColor(mContext.getResources().getColor(R.color.color_white));
					
				} else {
					
					mCommentsAddText.setTextColor(mContext.getResources().getColor(R.color.gray));
					mCommentsAddText.setEnabled(false);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}
		});

		int wight = mContext.getResources().getDisplayMetrics().widthPixels;
		
		mCommentAddPop = new PopupWindow(mCommentsPopView,wight,ViewGroup.LayoutParams.WRAP_CONTENT);  
		mCommentAddPop.setFocusable(true);  
		mCommentAddPop.setOutsideTouchable(true);  
		mCommentAddPop.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
		mCommentAddPop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); 
		mCommentAddPop.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.white_bg));  
		mCommentAddPop.setOnDismissListener(new OnDismissListener() {  
	              
	            @Override  
	            public void onDismiss() {  
	                // TODO Auto-generated method stub  
	            	mInputManager.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
	            }  
	    });  
		
		Intent data = getIntent();
		if ( data != null && data.getBundleExtra("Data") != null && 
			 data.getBundleExtra("Data").getString("TipsId") != null) {
			
			tipsId = data.getBundleExtra("Data").getString("TipsId");
		}
		
		initDataAysnc();
	}
	
	@Override
	protected void onResume () {
		
		DebugTools.getDebug().debug_v(TAG, "sn??/、、？/????" + sn);
		
		super.onResume();
	}
	
	
	/*初始化数据请求*/
	private void initDataAysnc() {
		if(!Utils.isConnected(mContext)) {
			return ;
		}
		WaitDialog.showWaitDialog(mContext, R.string.str_loading_msg);
		clearLoader();
		/*sn 暂时等于1*/
		reqLoader = new GetTipsDetialLoader(mContext, sn, tipsId, new GetTipsDetialCallback() {
			
			@Override
			public void callBack(int retId, String msg, FriendHotItem item) {
				// TODO Auto-generated method stub
				
				if(BaseRequest.REQ_RET_F_NO_DATA == retId ) {
					if(msg.trim().length() == 0) {
						msg = getString(R.string.str_friend_no_data);
					}
					// display reload page
					//loadFail.displayNoDataRetry(msg);
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
					
				} else if (BaseRequest.REQ_RET_OK == retId) {
					
					//loadFail.displayNone();
					initListView(item);
					//listFooter.refreshFooterView(inviteList.size(), pageSize);
					MainApp.getInstance().getGlobalData().setHasStartNewInvite(false);
					
				} else {
					
					//loadFail.displayFail(msg);
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
				}
				WaitDialog.dismissWaitDialog();
				reqLoader = null;	
			}
		});
		
		reqLoader.requestData();
	}
	
	
	private void loadAvatar(String sn,String filename,final ImageView iv){
		Drawable avatar = AsyncImageLoader.getInstance().getAvatar(mContext, sn, filename,
				(int) getResources().getDimension(R.dimen.avatar_detail_size));
		if(null != avatar) {
			iv.setImageDrawable(avatar);
		} else {
			iv.setImageResource(R.drawable.avatar_loading);
			AsyncImageLoader.getInstance().loadAvatar(mContext, sn, filename,
					new ImageCallback() {
						@Override
						public void imageLoaded(Drawable imageDrawable) {
							if(null != imageDrawable && null != iv) {
								iv.setImageDrawable(imageDrawable);
							}
						}
				});
		}
	}
	

	private void clearLoader() {
		if(isLoading()) {
			GetTipsDetialLoader loader = reqLoader;
			loader.stopTask(true);
			loader = null;
		}
	}
	
	private boolean isLoading() {
		return (null != reqLoader && reqLoader.isRunning());
	}


	/*加载更多数据*/
	private void refreshCommentsData() {

		if(!Utils.isConnected(this)) {

			commentsList.onRefreshComplete();
			return ;
		}
		//WaitDialog.showWaitDialog(getActivity(), R.string.str_loading_msg);
		clearLoader();

		commentPageNumber = 1;

		/*sn 暂时等于1*/
		final GetFriendCommentsList request = new GetFriendCommentsList(this,sn,commentPageNumber,pageSize,item.tipid);
		new AsyncTask<Object, Object, Integer>() {
			@Override
			protected Integer doInBackground(Object... params) {

				return request.connectUrl();
			}

			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);

				commentsList.onRefreshComplete();
				if (BaseRequest.REQ_RET_OK == result ) {

					if (commentsAdapter == null) {

						commentsAdapter = new FriendItemCommentsAdapter(mContext,request.getFriendCommentsList());

					}
					else {

						commentsAdapter.refreshListInfo(request.getFriendCommentsList());
					}

				}
				else {

					Toast.makeText(mContext, request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}

			}
		}.execute(null,null,null);
	}

	/*加载更多数据*/
	private void refreshPraiseData() {

		if(!Utils.isConnected(this)) {

			praiserList.onRefreshComplete();
			return ;
		}
		//WaitDialog.showWaitDialog(getActivity(), R.string.str_loading_msg);
		clearLoader();

		commentPageNumber=1;

		/*sn 暂时等于1*/
		final GetFriendPraiserList request = new GetFriendPraiserList(this,sn,commentPageNumber,pageSize,item.tipid);
		new AsyncTask<Object, Object, Integer>() {
			@Override
			protected Integer doInBackground(Object... params) {

				return request.connectUrl();
			}

			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);

				praiserList.onRefreshComplete();
				if (BaseRequest.REQ_RET_OK == result ) {

					if (praiserAdapter == null) {

						praiserAdapter = new FriendItemPraiserAdapter(mContext,request.getFriendCommentsList());

					}
					else {

						praiserAdapter.refreshListInfo(request.getFriendCommentsList());
					}

				}
				else {

					Toast.makeText(mContext, request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}

			}
		}.execute(null,null,null);
	}

	/*加载更多数据*/
	private void loadMoreCommentsData() {

		if(!Utils.isConnected(this)) {

			commentsList.onRefreshComplete();
			return ;
		}
		//WaitDialog.showWaitDialog(getActivity(), R.string.str_loading_msg);
		clearLoader();

		commentPageNumber++;

		/*sn 暂时等于1*/
		final GetFriendCommentsList request = new GetFriendCommentsList(this,sn,commentPageNumber,pageSize,item.tipid);
		new AsyncTask<Object, Object, Integer>() {
			@Override
			protected Integer doInBackground(Object... params) {

				return request.connectUrlGet();
			}

			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);

				commentsList.onRefreshComplete();
				if (BaseRequest.REQ_RET_OK == result ) {

					if (commentsAdapter == null) {

						commentsAdapter = new FriendItemCommentsAdapter(mContext,request.getFriendCommentsList());

					}
					else {

						commentsAdapter.appendListInfo(request.getFriendCommentsList());
					}

				}
				else {

					Toast.makeText(mContext, request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}

			}
		}.execute(null,null,null);
	}

	/*加载更多数据*/
	private void loadMorePraiseData() {

		if(!Utils.isConnected(this)) {

			praiserList.onRefreshComplete();
			return ;
		}
		//WaitDialog.showWaitDialog(getActivity(), R.string.str_loading_msg);
		clearLoader();

		praisePageNumber++;

		/*sn 暂时等于1*/
		final GetFriendPraiserList request = new GetFriendPraiserList(this,sn,praisePageNumber,pageSize,item.tipid);
		new AsyncTask<Object, Object, Integer>() {
			@Override
			protected Integer doInBackground(Object... params) {

				return request.connectUrlGet();
			}

			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);

				praiserList.onRefreshComplete();
				if (BaseRequest.REQ_RET_OK == result ) {

					if (praiserAdapter == null) {

						praiserAdapter = new FriendItemPraiserAdapter(mContext,request.getFriendCommentsList());

					}
					else {

						praiserAdapter.appendListInfo(request.getFriendCommentsList());
					}

				}
				else {

					Toast.makeText(mContext, request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}

			}
		}.execute(null,null,null);
	}
	
	
	private void initListView(FriendHotItem item1) {
		
		this.item = item1;

		final String sn_tips = item.sn;
		
		/*加载头像*/
		loadAvatar(item.sn,item.avatar,avatarImage);
		
		if (item.attention == 0) {
			
			attention.setImageResource(R.drawable.attent_color);
			
		} else if (item.attention == 1) {
			
			attention.setImageResource(R.drawable.attented_color);
		}
		attention.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String snStr = sn_tips;
				attention_sn = snStr;
				
				/*添加关注*/
				attention();
			}
		});
		
		if (item.praise == 0) {
			
			praiseImage.setImageResource(R.drawable.good);
			
		} else if (item.praise == 1) {

			praiseImage.setImageResource(R.drawable.good_clicked);
		}
		

		praiseLinear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				/*点赞*/
				praise();
			}
		});
		
		/*有没有图片数据*/
		ArrayList<Photos> listPhoto = getPhotos(item);
		
		shareLinear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// ;

				ShareMenu shareMenu = new ShareMenu(mContext, shareLinear, item);
				shareMenu.showPopupWindow();

			}
		});
		
		if (listPhoto != null && listPhoto.size() > 0) {
			
			if (listPhoto.size() == 1) {
				
				//LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				//images.setLayoutParams(lp);
				images.setNumColumns(1);
				
			} else if (listPhoto.size() == 2 || listPhoto.size() == 4) {
				
				images.setNumColumns(2);
				//LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(160, LayoutParams.WRAP_CONTENT);
				//images.setLayoutParams(lp);
				
			} else {
				
				//LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				//images.setLayoutParams(lp);
				images.setNumColumns(3);
			}
			
			images.setAdapter(new ImageAdapter(mContext, listPhoto, false));
			images.setVisibility(View.VISIBLE);
			
		} else {
			
			images.setVisibility(View.GONE);
		}

		commentTxt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg01) {
				// TODO Auto-generated method stub
				
				/*弹出输入框*/
				toname = "";
				tosn = "";
				mCommentsEdit.setHint("");
				showPopupWindow();
			}
		});

		userName.setText(item.name);
		
		String contentStr = item.content;
		if (contentStr != null && contentStr.length() > 0) {
			
			contents.setVisibility(View.VISIBLE);
			contents.setText(contentStr);
			
		} else {
			
			contents.setVisibility(View.GONE);
		}
		
		addTime.setText(Utils.handTime(item.releaseTime));

		if (item1 != null && item1.comments != null && item1.comments.size() > 0) {

			commentsAdapter = new FriendItemCommentsAdapter(this,item1.comments);
			commentsList.setAdapter(commentsAdapter);
			commentsList.onRefreshComplete();
			commentsLoadFail.displayNone();
		}
		else {

			commentsLoadFail.displayNoData("暂无评论");
		}

		if (item1 != null && item1.praises != null && item1.praises.size() > 0) {

			praiserAdapter = new FriendItemPraiserAdapter(this,item1.praises);
			praiserList.setAdapter(praiserAdapter);
			praiserList.onRefreshComplete();
			praiserLoadFail.displayNone();

		}
		else {

			praiserLoadFail.displayNoData("暂无点赞");
		}

		int height = getResources().getDisplayMetrics().heightPixels;

		int statusBar = 0;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
			statusBar = SystemBarUtils.getStatusBarHeight(this);

			// statusBar = 100;
		}

		int subHeight = findViewById(R.id.friend_tips_detial_commentsCount_relative).getHeight()+findViewById(R.id.friend_tips_detail_head).getHeight()+statusBar;
		commentListLinear.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				height - subHeight));
		praiseListLinear.setLayoutParams(new LinearLayout.LayoutParams(
				 LinearLayout.LayoutParams.MATCH_PARENT,
				 height - subHeight));

		if (item.commentsCount >= 0) {

			commentsCountTxt.setText(String.valueOf(item.commentsCount));
		}

		if (item.commentsCount >= 0) {

			praisersCountTxt.setText(String.valueOf(item.praiseCount));
		}

		
	}
	
	 /*发布的图片*/
    public class ImageAdapter extends BaseAdapter {
    	private Context context;
    	private List paths;
    	private boolean localtion;// true是本地图片 false 是网络图片
    	private FinalBitmap finalBit;
    	
    	private ArrayList<String> maxPaths = new ArrayList<String>();

    	public ImageAdapter(Context context, ArrayList<Photos> paths, boolean localtion) {
    		this.context = context;
    		this.paths = paths;
    		this.localtion = localtion;
    		finalBit = FinalBitmap.create(context);
    		
    		for (Photos ph :paths) {
    			
    			if (ph.max != null && ph.max.length() > 0) {
    				
    				maxPaths.add(maxPaths.size(), ph.max);
    			} else {
    				
    				maxPaths.add(maxPaths.size(), ph.localStr);
    			}
    			
    		}
    	}


    	public void setData(List<String> paths) {
    		this.paths = paths;
    	}

    	@Override
    	public int getCount() {
    		return paths == null ? 0 : paths.size();
    	}

    	@Override
    	public Object getItem(int position) {
    		return paths == null ? null : paths.get(position);
    	}

    	@Override
    	public long getItemId(int position) {
    		return position;
    	}

    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
    		
    		final int index = position;
    		
    		Holder holder = new Holder();
    		if (convertView == null) {
    			
    			if (paths.size() == 1) { 
    				
    				convertView = LayoutInflater.from(context).inflate(R.layout.friend_images_one, null);
    				
    			} else if (paths.size() == 2 || paths.size() == 4) {
    				
    				convertView = LayoutInflater.from(context).inflate(R.layout.friend_images_two, null);
    				
    			} else {
    				
    				convertView = LayoutInflater.from(context).inflate(R.layout.friend_images, null);
    			}
    			
    			holder.imageView = (ImageView) convertView.findViewById(R.id.image);
    			
    			convertView.setTag(holder);
    			
    		} else {
    			
    			holder = (Holder) convertView.getTag();
    		}
//    		holder.imageView.setImageResource(R.drawable.aaa);
    		Photos ph = (Photos) paths.get(position);
    		
    		//DebugTools.getDebug().debug_v(TAG, "ph.max----->>>"+ph.max);
    		
    		holder.imageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					int indexInt = index;
					Intent data = new Intent(mContext, PhotoViewActivity.class);
					data.putStringArrayListExtra("Photos", maxPaths);
					data.putExtra("Index", indexInt);
					mContext.startActivity(data);
				}
			});
    		
    		if (ph.min != null && ph.min.length() > 0) {
    			
    			if (paths.size() == 1) {
    				
    				finalBit.display(holder.imageView, ph.max);
    				//DownLoadImageTool.getInstance(getActivity()).displayImage(ph.max, holder.imageView);
    				
    			} else {
    				
    				finalBit.display(holder.imageView, ph.min);
    				//DownLoadImageTool.getInstance(getActivity()).displayImage(ph.min, holder.imageView);
    				
    				//finalBit.
    			}
    			
    		} else if (ph.localStr != null) {
    			
    			finalBit.display(holder.imageView, "file:///"+ph.localStr);
    			//DownLoadImageTool.getInstance(getActivity()).displayImage("file:///"+ph.localStr, holder.imageView);
    		}
    		
    		
    		//DownLoadImageTool.getInstance(getActivity()).displayImage(ph.max, holder.imageView);
    		
    		return convertView;
    	}

    	private class Holder {
    		public ImageView imageView;
    	}
    }
	
	/*
	 * 添加关注
	 * */
	private void attention() {
		
		/*添加或取消关注*/
		WaitDialog.showWaitDialog(mContext, R.string.str_loading_waiting);
		new AsyncTask<Object, Object, Integer>() {
		
			FriendAttentionAdd request = new FriendAttentionAdd(mContext,sn,attention_sn,item.attention);
			@Override
			protected Integer doInBackground(Object... params) {

				return request.connectUrlGet();
			}
			
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				
				if(BaseRequest.REQ_RET_OK == result) {
					
					/*已经关注过*/
					int attention = item.attention;
					item.attention = attention == 1 ? 0 : 1;
					
				} else {
					
					
					
				}
				
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}

	
	/*
	 * 对帖子进行点赞
	 * */
	private void praise() {
		
		/*添加和取消点赞*/
		WaitDialog.showWaitDialog(mContext, R.string.str_loading_waiting);
		new AsyncTask<Object, Object, Integer>() {
		
			int praise = item.praise;
			
			FriendPraiseAdd request = new FriendPraiseAdd(mContext,sn,name,item.tipid,praise);
			@Override
			protected Integer doInBackground(Object... params) {

				return request.connectUrlGet();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				
				if(BaseRequest.REQ_RET_OK == result) {
					
					/*已经点过赞*/
					int praise = item.praise;
			
					item.praise = praise == 0 ? 1 : 0;

					if (item.praise == 0) {

						praiseImage.setImageResource(R.drawable.good);

					} else if (item.praise == 1) {

						praiseImage.setImageResource(R.drawable.good_clicked);
					}

					HashMap<String, String> reviewItemHash = new HashMap<String, String>();
					
//					if (praise == 0) {
//
//						//int size = item.praises.size();
//
//						reviewItemHash.put("id", request.praiseId);
//						reviewItemHash.put("tipid", tipid);
//						reviewItemHash.put("sn", sn);
//						reviewItemHash.put("name", name);
//
//						item.praises.add(0,reviewItemHash);
//
//					} else {
//
//						for(int i = 0; i < item.praises.size(); i++) {
//
//							String snStr = item.praises.get(i).get("sn");
//							if (snStr != null && snStr.equalsIgnoreCase(sn)) {
//
//								item.praises.remove(i);
//							}
//
//						}
//					}
					
					//initListView(item);

					reviewItemHash.put("id", request.praiseId);
					reviewItemHash.put("tipid", item.tipid);
					reviewItemHash.put("sn", sn);
					reviewItemHash.put("name", name);

					if (praiserAdapter == null) {

						item.praises.add(0,reviewItemHash);
						praiserAdapter = new FriendItemPraiserAdapter(mContext,item.praises);
						praiserList.setAdapter(praiserAdapter);
						praiserLoadFail.displayNone();
					}
					else {

						praiserAdapter.addNewItem(reviewItemHash);
					}

					praisersCountTxt.setText(String.valueOf(praiserAdapter.list.size()));
					
				} else {

					Toast.makeText(mContext, request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
	/*
     * 弹出评论对话框
     * */  
    public void showPopupWindow() {

 
        if(!mCommentAddPop.isShowing()) {  
        	
        	mInputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        	
        	mCommentAddPop.showAtLocation(commentListLinear, Gravity.BOTTOM, 0, 0);
        		
        	if (toname != null && toname.length() > 0) {
        		
        		mCommentsEdit.setHint("回复:"+toname);
        	}
        	
        
        } else {  
        	
        	mCommentAddPop.dismiss();  
        }  
    }
    
    /**
	 * 处理图片数据
	 *
	 */
	private ArrayList<Photos> getPhotos(FriendHotItem item) {

		ArrayList<Photos> phs = new ArrayList<Photos>();
		
		try {
			
			String photo = item.imageURL;
			
			String[] photos = photo.split(",");
			
			if (photo != null && photo.length() > 0 && !photo.equalsIgnoreCase("null")&& photos != null && photos.length > 0) {
				
				int size = photos.length;
				Photos ph;
				for (int i = 0; i < size; i++) {
					String name = photos[i];
					ph = new Photos();
					ph.min = BaseRequest.TipsPic_Thum_PATH+name;
					ph.max = BaseRequest.TipsPic_Original_PATH+name;
					phs.add(ph);
				}
				return phs;
			}
			
			/*解析本地的图片*/
			if (item.localImageURL != null && item.localImageURL.size() > 0) {
				
				int size = item.localImageURL.size();
				Photos ph;
				for (int i = 0; i < size; i++) {
					ph = new Photos();
					ph.localStr = item.localImageURL.get(i);
					DebugTools.getDebug().debug_v(TAG,"ph.localStr----->>>"+ph.localStr);
					
					phs.add(ph);
				}
				return phs;
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if (arg0.getId() == mCommentsAddText.getId()) {
			
			if(!Utils.isConnected(mContext)){
				return;
			}
			
			/**/
			String commentsStrInput = mCommentsEdit.getText().toString();
			String commentsStr = commentsStrInput.replaceAll("[^0-9a-zA-Z\u4e00-\u9fa5.，,。？“”]+", "");
			
			mCurrentComments.put("tipid", item.tipid);
			mCurrentComments.put("sn", sn);
			mCurrentComments.put("name", name);
			mCurrentComments.put("avatar", avatar);
			mCurrentComments.put("content", commentsStr);
			mCurrentComments.put("toname", toname);
			mCurrentComments.put("tosn", tosn);
			mCurrentComments.put("commentstime", String.valueOf(System.currentTimeMillis()));
			
			/*提交评论*/
			WaitDialog.showWaitDialog(mContext, R.string.str_loading_add_comment);
			new AsyncTask<Object, Object, Integer>() {
			
				FriendCommentsAdd request = new FriendCommentsAdd(mContext, mCurrentComments.get("sn"), mCurrentComments.get("name") ,
						mCurrentComments.get("avatar") ,mCurrentComments.get("tipid"),mCurrentComments.get("tosn"),
						mCurrentComments.get("toname"),mCurrentComments.get("content"));
				@Override
				protected Integer doInBackground(Object... params) {

					return request.connectUrlGet();
				}
				@Override
				protected void onPostExecute(Integer result) {
					super.onPostExecute(result);
					
					if(BaseRequest.REQ_RET_OK == result) {
						
						/*一旦点击了发送按钮，就应该把输入框清空*/
						mCommentsEdit.setText("");
						mCommentAddPop.dismiss();
						
						//initListView(item);
						if (commentsAdapter == null) {

							item.comments.add(0, mCurrentComments);
							commentsAdapter = new FriendItemCommentsAdapter(mContext,item.comments);
							commentsList.setAdapter(commentsAdapter);
							commentsLoadFail.displayNone();
						}
						else {

							commentsAdapter.addNewItem(mCurrentComments);
						}


						
					} else {

						Toast.makeText(mContext, request.getFailMsg(), Toast.LENGTH_SHORT).show();
						
					}
					WaitDialog.dismissWaitDialog();
				}
			}.execute(null, null, null);
			
		}
		else if (arg0.getId() == commentsCountLinear.getId()) {

			commentsCountLinear.setSelected(true);
			commentListLinear.setVisibility(View.VISIBLE);

			praisersCountLinear.setSelected(false);
			praiseListLinear.setVisibility(View.GONE);

		}
		else if (arg0.getId() == praisersCountLinear.getId()) {

			commentsCountLinear.setSelected(false);
			commentListLinear.setVisibility(View.GONE);

			praisersCountLinear.setSelected(true);
			praiseListLinear.setVisibility(View.VISIBLE);

		}
	}
}
