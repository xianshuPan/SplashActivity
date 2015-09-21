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
import com.hylg.igolf.ui.member.MemDetailActivityNew;
import com.hylg.igolf.ui.view.FlowLayout;
import com.hylg.igolf.ui.view.MyGridView;
import com.hylg.igolf.ui.view.NoLineClickSpan;
import com.hylg.igolf.ui.view.ShareMenu;
import com.hylg.igolf.ui.widget.XRTextView;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
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
	
	private FragmentActivity 			mContext 					= null;
	
	private String 						sn							= "",
										tipid                		= "",
										attention_sn                = "",
										tosn                        = "",
										name                        = "",
										toname                      = "",
										tipsId                      = "";
	
	
	public ImageView 					avatarImage ;
	public TextView 					userName ;
	public TextView 					addTime ;
	public TextView 					contents ;
	public ImageView 					attention ;
	public ImageView 					delete ;
	public ImageView 					share;
	public ImageView 					praise ;

	/*
	 * 显示点赞的人
	 * */
	public FlowLayout 					praisersLinear;
	
	public RelativeLayout 				praisersRelative ;
	
	public MyGridView 					images;
	public ImageView 					image;
	public LinearLayout 				commensLinear;
	public ImageView 					comments;
	
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
	
	public static void startFriendTipsDetailActivity(Activity context,Bundle data) {

		Intent intent = new Intent(context, FriendTipsDetailActivity.class);
		intent.putExtra("Data", data);
		context.startActivityForResult(intent, Const.REQUST_CODE_SIGNATURE_MY);
		
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
		share = (ImageView) findViewById(R.id.friend_tips_detial_share_image);
		praise = (ImageView) findViewById(R.id.friend_tips_detial_good_image);
		praisersLinear = (FlowLayout) findViewById(R.id.friend_tips_detial_good_user_name_linear);
		praisersRelative = (RelativeLayout) findViewById(R.id.friend_tips_detial_praisers_relative);
		images = (MyGridView) findViewById(R.id.friend_tips_detial_image_content);
		commensLinear = (LinearLayout)findViewById(R.id.friend_tips_detial_comments_linear);
		comments = (ImageView)findViewById(R.id.friend_tips_detial_comment_image);
		
		sn = MainApp.getInstance().getCustomer().sn;
		
		mInputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		sn = MainApp.getInstance().getCustomer().sn;
		name = MainApp.getInstance().getCustomer().nickname;
		
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
		
		DebugTools.getDebug().debug_v(TAG, "sn??/、、？/????"+sn);
		
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
		
		/*是否有人点赞,点赞人的名字都要能够点击，*/
		
		praisersLinear.removeAllViews();
		if (item.praises != null && item.praises.size() > 0) {
		
			ImageView praiserStartImage = new ImageView(mContext);
			LayoutParams adf = new LayoutParams(30, 30);
			praiserStartImage.setLayoutParams(adf);
			praiserStartImage.setImageResource(R.drawable.good_icon);
			praisersLinear.addView(praiserStartImage);
			
			for (int i = 0; i < item.praises.size() ; i++) {
				
				StringBuffer praisers = new StringBuffer();
				String praiser = item.praises.get(i).get("name");
				praisers.append(praiser);
				
				if (i <= item.praises.size() -2) {
					
					praisers.append("、");
				}
				
				final String praiseSn = item.praises.get(i).get("sn");
				
				TextView praiserName = new TextView(mContext);
				
				praiserName.setText(praisers);
				praiserName.setTextColor(mContext.getResources().getColor(R.color.color_friend_item_praiser_name));
				praiserName.setBackgroundResource(R.drawable.praiser_selection);
				praiserName.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				praiserName.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						String praisSnStr = praiseSn;
						
						if (praisSnStr != null && !praisSnStr.equalsIgnoreCase(sn)) {
							
							MemDetailActivityNew.startMemDetailActivity(mContext, praisSnStr);
							mContext.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
						}
						
					}
				});
				
				praisersLinear.addView(praiserName);
			}
			
			
			TextView praiserEndName = new TextView(mContext);
			
			praiserEndName.setText(R.string.str_friend_praised);
			praiserEndName.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			praisersLinear.addView(praiserEndName);
			
				
			praisersRelative.setVisibility(View.VISIBLE);
			//praisersText.setText(result);
			
		/*没有人点赞*/
		} else {
			
			praisersRelative.setVisibility(View.GONE);
		}
		
		if (item.praise == 0) {
			
			praise.setImageResource(R.drawable.good);
			
		} else if (item.praise == 1) {
			
			praise.setImageResource(R.drawable.good_clicked);
		}
		
		
		final String tipsid = item.tipid;
		praise.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				/*点赞*/
				String tipidStr = tipsid;
				tipid = tipidStr;
				praise();
			}
		});
		
		/*有没有图片数据*/
		ArrayList<Photos> listPhoto = getPhotos(item);
		
		share.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// ;
								 	
				ShareMenu shareMenu = new ShareMenu(mContext, commensLinear,item);
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
		
		final View mCommentsView1 = commensLinear;
		comments.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg01) {
				// TODO Auto-generated method stub
				View temp = mCommentsView1;
				
				/*弹出输入框*/
				toname = "";
				mCommentsEdit.setHint("");
				showPopupWindow(temp);
			}
		});
		
		/*构造评论列表*/
		commensLinear.removeAllViews();
		if (item.comments != null && item.comments.size() > 0) {
			
			for (int i = 0; i < item.comments.size() ; i++) {
				
				LinearLayout commensLinearItem = (LinearLayout) mContext.getLayoutInflater().inflate(R.layout.friend_frg_hot_item_comment, null);
				
				XRTextView commentUserName = (XRTextView)commensLinearItem.findViewById(R.id.comment_user_name_text);
				
				final String commentSn = item.comments.get(i).get("sn");
				final String commentToSn = item.comments.get(i).get("tosn");
				final String commentName = item.comments.get(i).get("name");
				
				String name = item.comments.get(i).get("name");
				
				String commentContent = item.comments.get(i).get("content");
				
				ClickableSpan clickableSpan = new NoLineClickSpan() {  
			            @Override  
			            public void onClick(View widget) {  
			                if (widget instanceof TextView) {  
			                	String praisSnStr = commentSn;
								
								if (praisSnStr != null && !praisSnStr.equalsIgnoreCase(sn)) {
									
									MemDetailActivityNew.startMemDetailActivity(mContext, praisSnStr);
									mContext.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
								}
			                }  
			            }  
			        };  
			        
			        ClickableSpan clickableSpan1 = new NoLineClickSpan() {  
			            @Override  
			            public void onClick(View widget) {  
			                if (widget instanceof TextView) {  
			                	String praisSnStr = commentToSn;
								
								if (praisSnStr != null && !praisSnStr.equalsIgnoreCase(sn)) {
									
									MemDetailActivityNew.startMemDetailActivity(mContext, praisSnStr);
									mContext.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
								} 
			                }  
			            }  
			        };  
			        
			       
			        String toname1 = item.comments.get(i).get("toname");
			        
			        SpannableStringBuilder sp = new SpannableStringBuilder(commentName+":"+commentContent); 
			        
			        /*判断是否是回复评论*/
					if (toname1 != null && toname1.length() > 0) {
						
						sp = new SpannableStringBuilder(commentName+"回复:"+toname1+commentContent);  
					}
			        
				    sp.setSpan(clickableSpan, 0, commentName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); 
			        
				    if (toname1 != null && toname1.length() > 0) {
						
						 sp.setSpan(clickableSpan1, commentName.length()+3, commentName.length()+3+toname1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  
					}
			       
			    commentUserName.setText(sp);  
			    // commentUserName.setLinkTextColor(Color.parseColor("#110000"));  
			    commentUserName.setLinkTextLenth(name.length(), toname1.length());
			    commentUserName.setMovementMethod(LinkMovementMethod.getInstance());  
			    commentUserName.setFocusable(false);  
			    commentUserName.setClickable(false);  
			    commentUserName.setLongClickable(false);  
			        
			        
			    commentUserName.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						
						View temp = mCommentsView1;
						
						String commentSnStr = commentSn;
						String commentNameStr = commentName;
						
						/*如果是自己则不能回复*/
						if(commentSnStr != null && !commentSnStr.equalsIgnoreCase(sn)) {
	
							tosn = commentSnStr;
							toname = commentNameStr;
							showPopupWindow(temp);
						}
						
						DebugTools.getDebug().debug_v(TAG, "commentSnStr--------->>>>>>>"+commentSnStr);
						DebugTools.getDebug().debug_v(TAG, "sn--------->>>>>>>"+sn);
					}
				});
				
				commensLinear.addView(commensLinearItem);
				
			}
		}
		
		userName.setText(item.name);
		
		String contentStr = item.content;
		if (contentStr != null && contentStr.length() > 0) {
			
			contents.setVisibility(View.VISIBLE);
			contents.setText(contentStr);
			
		} else {
			
			contents.setVisibility(View.GONE);
		}
		
		addTime.setText(Utils.handTime(item.releaseTime));
		
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
			
			FriendPraiseAdd request = new FriendPraiseAdd(mContext,sn,name,tipid,praise);
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
					
					if (praise == 0) {
						
						int size = item.praises.size();
						HashMap<String, String> reviewItemHash = new HashMap<String, String>();
						reviewItemHash.put("id", request.praiseId);	
						reviewItemHash.put("tipid", tipid);
						reviewItemHash.put("sn", sn);
						reviewItemHash.put("name", name);	
						item.praises.add(size,reviewItemHash);
						
					} else {
						
						for(int i = 0; i < item.praises.size(); i++) {
							
							String snStr = item.praises.get(i).get("sn");
							if (snStr != null && snStr.equalsIgnoreCase(sn)) {
								
								item.praises.remove(i);
							}
							
						}
					}
					
					initListView(item);
					
				} else {
					

				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
	/*
     * 弹出评论对话框
     * */  
    private void showPopupWindow(View temp) {  
    	
		int[] positon = {0,0};
		
		temp.getLocationOnScreen(positon);
 
        if(!mCommentAddPop.isShowing()) {  
        	
        	mInputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        	
        	mCommentAddPop.showAtLocation(commensLinear, Gravity.BOTTOM, 0, 0); 
        		
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
	 * @param photo
	 * @return
	 */
	private ArrayList<Photos> getPhotos(FriendHotItem item) {

		ArrayList<Photos> phs = new ArrayList<Photos>();
		
		try {
			
			String photo = item.imageURL;
			
			String[] photos = photo.split(",");
			
			if (photo != null && photo.length() > 0 && photos != null && photos.length > 0) {
				
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
			mCurrentComments.put("content", commentsStr);
			mCurrentComments.put("toname", toname);
			mCurrentComments.put("tosn", tosn);
			
			/*提交评论*/
			WaitDialog.showWaitDialog(mContext, R.string.str_loading_add_comment);
			new AsyncTask<Object, Object, Integer>() {
			
				FriendCommentsAdd request = new FriendCommentsAdd(mContext, mCurrentComments.get("sn"), 
						mCurrentComments.get("name") ,mCurrentComments.get("tipid"),mCurrentComments.get("tosn"),
						mCurrentComments.get("toname"),mCurrentComments.get("content"));
				@Override
				protected Integer doInBackground(Object... params) {

					return request.connectUrlGet();
				}
				@Override
				protected void onPostExecute(Integer result) {
					super.onPostExecute(result);
					
					if(BaseRequest.REQ_RET_OK == result) {
						
						int size = item.comments.size();
						
						/*不能直接天剑mCurrentComments，要新添加hashmap*/
						HashMap<String, String> hash = new HashMap<String, String>();
						hash.put("tipid", mCurrentComments.get("tipid"));
						hash.put("sn", mCurrentComments.get("sn"));
						hash.put("name", mCurrentComments.get("name"));
						hash.put("content", mCurrentComments.get("content"));
						hash.put("toname", mCurrentComments.get("toname"));
						hash.put("tosn", mCurrentComments.get("tosn"));
						
						item.comments.add(size,hash);
						
						/*一旦点击了发送按钮，就应该把输入框清空*/
						mCommentsEdit.setText("");
						
						initListView(item);
						
					} else {
						
						
						
					}
					WaitDialog.dismissWaitDialog();
				}
			}.execute(null, null, null);
			
		} 
	}
}
