package com.hylg.igolf.ui.friend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.tsz.afinal.FinalBitmap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.PopupWindow.OnDismissListener;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.FriendHotItem;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.FriendAttentionAdd;
import com.hylg.igolf.cs.request.FriendCommentsAdd;
import com.hylg.igolf.cs.request.FriendPraiseAdd;
import com.hylg.igolf.cs.request.FriendTipsDelete;
import com.hylg.igolf.ui.member.MemDetailActivityNew;
import com.hylg.igolf.ui.view.FlowLayout;
import com.hylg.igolf.ui.view.MyGridView;
import com.hylg.igolf.ui.view.NoLineClickSpan;
import com.hylg.igolf.ui.view.RefreshView;
import com.hylg.igolf.ui.view.ShareMenu;
import com.hylg.igolf.ui.widget.IgBaseAdapter;
import com.hylg.igolf.ui.widget.XRTextView;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

public class FriendCircleAdapter extends IgBaseAdapter implements OnClickListener {
	
	private final String                TAG                         = "FriendCircleAdapter";
	
	public ArrayList<FriendHotItem> 	list;
	
	private Activity 					mContext;
	
	private String 						sn							= "",
										tipid                		= "",
										attention_sn                = "",
										name                        = "",
										avatar                      = "",
										tosn                        = "",
										toname                      = "";
	
	/*
	 * 当前点击的item 的index
	 * */
	private int                         mCurrentPositionInt         = 0;
	
	/*添加评论*/
	private PopupWindow                 mCommentAddPop              = null;
	private View                        mCommentsPopView            = null;
	private EditText					mCommentsEdit               = null;
	private TextView                    mCommentsAddText            = null;
	
	/*是否需要显示删除按钮*/
	private boolean                     mIsShowDelete               = false;
	private InputMethodManager          mInputManager               = null;
	
	
	private ListView                    mList                       = null;
	private RefreshView					mRefreshView                = null;
	
	/* 新增的评论
	 * */
	private HashMap<String, String>     mCurrentComments            = new HashMap<String, String>();
	
	public FriendCircleAdapter(Activity activity) {

		list = new ArrayList<FriendHotItem>();
		
		mContext = activity;
		
		init();
	}
	
	public FriendCircleAdapter(Activity activity,ArrayList<FriendHotItem> list,ListView listView,RefreshView refreshView ,boolean isShowDelete) {

		mContext = activity;
		this.list = list;
		mList = listView;
		mRefreshView = refreshView;
		mIsShowDelete = isShowDelete;
		init();
	}
	
	/*
	 * 初始化变量
	 * */
	private void init() {
		
		mInputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		sn = MainApp.getInstance().getCustomer().sn;
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
	
	public void initListInfo(ArrayList<FriendHotItem> list1) {
		this.list.clear();
		addData(list1);
		notifyDataSetChanged();
	}
	
	public void refreshListInfo(ArrayList<FriendHotItem> list1) {
		this.list.clear();
		addData(list1);
		notifyDataSetChanged();
	}
	
	public void appendListInfo(ArrayList<FriendHotItem> list1) {
		addData(list1);
		notifyDataSetChanged();
	}
	
	private void addData (ArrayList<FriendHotItem> list1) {
		
		if (list1 == null) {
			
			return;
		}
		
		for(int i=0, size=list1.size(); i<size; i++) {
			this.list.add(list1.get(i));
		}
	}
	
	public void appendFriendHotItem(FriendHotItem item) {
		
		if (list != null) {
			
			if (list.get(0).releaseTime != item.releaseTime) {
				
				list.add(0, item);
			}
			
		}
		
		notifyDataSetChanged();
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		
		final int index = arg0;
		
		ViewHolder holder;
		holder = null;

		if (arg1 ==  null) {
			
			arg1 = mContext.getLayoutInflater().inflate(R.layout.friend_frg_hot_item, null);
			
			holder = new ViewHolder();
			
			holder.avatarImage = (ImageView) arg1.findViewById(R.id.user_headImage);
			holder.userName = (TextView)arg1.findViewById(R.id.user_nameText);
			holder.addTime = (TextView) arg1.findViewById(R.id.add_timeText);
			holder.contents = (TextView) arg1.findViewById(R.id.content_Text);
			holder.attention = (ImageView) arg1.findViewById(R.id.attention_image);
			holder.delete = (ImageView) arg1.findViewById(R.id.delete_image);
			holder.share = (ImageView) arg1.findViewById(R.id.share_image);
			holder.praise = (ImageView) arg1.findViewById(R.id.good_image);
			holder.moreComments = (TextView) arg1.findViewById(R.id.more_comments_text);
			
			//holder.praisersText = (TextView) arg1.findViewById(R.id.good_user_nameText);
			
			holder.praisersLinear = (FlowLayout) arg1.findViewById(R.id.good_user_name_linear);
			holder.praisersRelative = (RelativeLayout) arg1.findViewById(R.id.praisers_relative);
			holder.images = (MyGridView) arg1.findViewById(R.id.image_content);
			holder.commensLinear = (LinearLayout)arg1.findViewById(R.id.comments_linear);
			holder.comments = (ImageView)arg1.findViewById(R.id.comment_image);
			
			if (mIsShowDelete) {
				
				holder.delete.setVisibility(View.VISIBLE);
			}
			
			
			arg1.setTag(holder);
			
		} else {
			
			holder = (ViewHolder) arg1.getTag();
		}
		
		loadAvatar(mContext, list.get(arg0).sn, list.get(arg0).avatar, holder.avatarImage);
		
		/*在我的栏目中，帖子就没有关注了，改为删除按钮*/
		final String tipsid = list.get(arg0).tipid;
		holder.delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub


				int indexInt = index;
				mCurrentPositionInt = indexInt;
				
				/*删除帖子*/
				String tipidStr = tipsid;
				tipid = tipidStr;
				
				AlertDialog.Builder dialog = new Builder(mContext);
				dialog.setMessage(R.string.str_delete_friend);
				dialog.setPositiveButton(R.string.str_photo_commit, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						deleteFriend();
					}
				});
				dialog.setNegativeButton(R.string.str_photo_cancel, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						//CartActivity.this.finish();
					}
				});
				dialog.show();

			}
		});
		
		final String sn_tips = list.get(arg0).sn;
		if (list.get(arg0).attention == 0) {
			
			holder.attention.setImageResource(R.drawable.attent_color);
			
		} else if (list.get(arg0).attention == 1) {
			
			holder.attention.setImageResource(R.drawable.attented_color);
		}
		holder.attention.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String snStr = sn_tips;
				attention_sn = snStr;
				
				int indexInt = index;
				mCurrentPositionInt = indexInt;
				
				/*添加关注*/
				attention();
			}
		});
		
		/*是否有人点赞,点赞人的名字都要能够点击，*/
		
		holder.praisersLinear.removeAllViews();
		if (list.get(arg0).praises != null && list.get(arg0).praises.size() > 0) {
		
			ImageView praiserStartImage = new ImageView(mContext);
			LayoutParams adf = new LayoutParams(30, 30);
			praiserStartImage.setLayoutParams(adf);
			praiserStartImage.setImageResource(R.drawable.good_icon);
			holder.praisersLinear.addView(praiserStartImage);
			
			for (int i = 0; i < list.get(arg0).praises.size() ; i++) {
				
				StringBuffer praisers = new StringBuffer();
				String praiser = list.get(arg0).praises.get(i).get("name");
				praisers.append(praiser);
				
				if (i <= list.get(arg0).praises.size() -2) {
					
					praisers.append("、");
				}
				
				final String praiseSn = list.get(arg0).praises.get(i).get("sn");
				
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
				
				holder.praisersLinear.addView(praiserName);
			}
			
			
			TextView praiserEndName = new TextView(mContext);
			
			praiserEndName.setText(R.string.str_friend_praised);
			praiserEndName.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			holder.praisersLinear.addView(praiserEndName);
			
				
			holder.praisersRelative.setVisibility(View.VISIBLE);
			//holder.praisersText.setText(result);
			
		/*没有人点赞*/
		} else {
			
			holder.praisersRelative.setVisibility(View.GONE);
		}
		
		if (list.get(arg0).praise == 0) {
			
			holder.praise.setImageResource(R.drawable.good);
			
		} else if (list.get(arg0).praise == 1) {
			
			holder.praise.setImageResource(R.drawable.good_clicked);
		}
		
		
		//final String tipsid = list.get(arg0).tipid;
		holder.praise.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				int indexInt = index;
				mCurrentPositionInt = indexInt;
				
				/*点赞*/
				String tipidStr = tipsid;
				tipid = tipidStr;
				praise();
			}
		});
		
		/*有没有图片数据*/
		ArrayList<Photos> listPhoto = getPhotos(list.get(arg0));
		
		final FriendHotItem friendHotItem = list.get(arg0);
		holder.share.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// ;
				
				FriendHotItem item = friendHotItem;
								 	
				ShareMenu shareMenu = new ShareMenu(mContext, mList,item);
				shareMenu.showPopupWindow();
				
			}
		});
		
		if (listPhoto != null && listPhoto.size() > 0) {
			
			if (listPhoto.size() == 1) {
				
				//LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				//holder.images.setLayoutParams(lp);
				holder.images.setNumColumns(1);
				
			} else if (listPhoto.size() == 2 || listPhoto.size() == 4) {
				
				holder.images.setNumColumns(2);
				//LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(160, LayoutParams.WRAP_CONTENT);
				//holder.images.setLayoutParams(lp);
				
			} else {
				
				//LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				//holder.images.setLayoutParams(lp);
				holder.images.setNumColumns(3);
			}
			
			holder.images.setAdapter(new ImageAdapter(mContext, listPhoto, false));
			holder.images.setVisibility(View.VISIBLE);
			
		} else {
			
			holder.images.setVisibility(View.GONE);
		}
		
		final View mCommentsView1 = holder.commensLinear;
		holder.comments.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg01) {
				// TODO Auto-generated method stub
				int indexInt = index;
				mCurrentPositionInt = indexInt;
				View temp = mCommentsView1;
				
				/*弹出输入框*/
				tosn = "";
				toname = "";
				mCommentsEdit.setHint("");
				showPopupWindow(temp);
			}
		});
		
		/*构造评论列表*/
		holder.commensLinear.removeAllViews();
		if (list.get(arg0).comments != null && list.get(arg0).comments.size() > 0) {
			
			int count = 0;
			
			if (list.get(arg0).comments.size() > 10) {
				
				count = 10;
				holder.moreComments.setVisibility(View.VISIBLE);
				
			} else {
				
				count =list.get(arg0).comments.size();
				holder.moreComments.setVisibility(View.GONE);
			}
			
			for (int i = 0; i < count ; i++) {
				
				LinearLayout commensLinearItem = (LinearLayout) mContext.getLayoutInflater().inflate(R.layout.friend_frg_hot_item_comment, null);
				
				XRTextView commentUserName = (XRTextView)commensLinearItem.findViewById(R.id.comment_user_name_text);
				
				final String commentSn = list.get(arg0).comments.get(i).get("sn");
				final String commentToSn = list.get(arg0).comments.get(i).get("tosn");
				final String commentName = list.get(arg0).comments.get(i).get("name");
				
				String name = list.get(arg0).comments.get(i).get("name");
				String commentContent = list.get(arg0).comments.get(i).get("content");
				
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
			        
			       
			        String toname1 = list.get(arg0).comments.get(i).get("toname");
			        
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
			    commentUserName.setLinkTextLenth(name.length(), toname1.length());
			    commentUserName.setMovementMethod(LinkMovementMethod.getInstance());  
			    commentUserName.setFocusable(false);  
			    commentUserName.setClickable(false);  
			    commentUserName.setLongClickable(false);  
			    commentUserName.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						
						int indexInt = index;
						mCurrentPositionInt = indexInt;
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
				
				holder.commensLinear.addView(commensLinearItem);
				
			}
			
		/*没有评论*/
		} else {
			
			holder.moreComments.setVisibility(View.GONE);
		}
		
		holder.userName.setText(list.get(arg0).name);
		
		String contentStr = list.get(arg0).content;
		if (contentStr != null && contentStr.length() > 0) {
			
			holder.contents.setVisibility(View.VISIBLE);
			holder.contents.setText(contentStr);
			
		} else {
			
			holder.contents.setVisibility(View.GONE);
		}
		
		holder.addTime.setText(Utils.handTime(list.get(arg0).releaseTime));
		holder.moreComments.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				Bundle data = new Bundle();
				
				int index1 = index;
				String TipsId = list.get(index1).tipid;
				data.putString("TipsId", TipsId);
				
				FriendTipsDetailActivity.startFriendTipsDetailActivity(mContext, data);
			}
		});
		
		arg1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				                                                                                                                                  
				Bundle data = new Bundle();
				
				int index1 = index;
				String TipsId = list.get(index1).tipid;
				data.putString("TipsId", TipsId);
				
				FriendTipsDetailActivity.startFriendTipsDetailActivity(mContext, data);
				
			}
		});
		
		
		return  arg1;
	}
	
	
	

	private static class ViewHolder {

		public ImageView avatarImage ;
		public TextView userName ;
		public TextView addTime ;
		public TextView contents ;
		public ImageView attention ;
		public ImageView delete ;
		public ImageView share;
		public ImageView praise ;
		public TextView moreComments ;
		//public TextView praisersText ;
		public FlowLayout praisersLinear;
		
		public RelativeLayout praisersRelative ;
		
		public MyGridView images;
		public ImageView image;
		public LinearLayout commensLinear;
		public ImageView comments;
	}
	
	/*
	 * 对帖子进行点赞
	 * */
	private void praise() {
		
		/*添加和取消点赞*/
		WaitDialog.showWaitDialog(mContext, R.string.str_loading_waiting);
		new AsyncTask<Object, Object, Integer>() {
		
			int praise = list.get(mCurrentPositionInt).praise;
			
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
					int praise = list.get(mCurrentPositionInt).praise;
			
					list.get(mCurrentPositionInt).praise = praise == 0 ? 1 : 0;
					
					if (praise == 0) {
						
						int size = list.get(mCurrentPositionInt).praises.size();
						HashMap<String, String> reviewItemHash = new HashMap<String, String>();
						reviewItemHash.put("id", request.praiseId);	
						reviewItemHash.put("tipid", tipid);
						reviewItemHash.put("sn", sn);
						reviewItemHash.put("name", name);	
						list.get(mCurrentPositionInt).praises.add(size,reviewItemHash);
						
					} else {
						
						for(int i = 0; i < list.get(mCurrentPositionInt).praises.size(); i++) {
							
							String snStr = list.get(mCurrentPositionInt).praises.get(i).get("sn");
							if (snStr != null && snStr.equalsIgnoreCase(sn)) {
								
								list.get(mCurrentPositionInt).praises.remove(i);
							}
							
						}
					}
					
					notifyDataSetChanged();
					
				} else {
					

				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
	/*
	 * 添加关注
	 * */
	private void attention() {
		
		/*添加或取消关注*/
		WaitDialog.showWaitDialog(mContext, R.string.str_loading_waiting);
		new AsyncTask<Object, Object, Integer>() {
		
			FriendAttentionAdd request = new FriendAttentionAdd(mContext,sn,attention_sn,list.get(mCurrentPositionInt).attention);
			@Override
			protected Integer doInBackground(Object... params) {

				return request.connectUrlGet();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				
				if(BaseRequest.REQ_RET_OK == result) {
					
					/*已经关注过*/
					int attention = list.get(mCurrentPositionInt).attention;
					
					list.get(mCurrentPositionInt).attention = attention == 1 ? 0 : 1;
					
					notifyDataSetChanged();
					
				} else {
					

				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
	/*
	 * 删除朋友圈帖子
	 * */
	private void deleteFriend() {
		
		/**/
		WaitDialog.showWaitDialog(mContext, R.string.str_loading_waiting);
		new AsyncTask<Object, Object, Integer>() {
			
			FriendTipsDelete request = new FriendTipsDelete(mContext,tipid);
			@Override
			protected Integer doInBackground(Object... params) {

				return request.connectUrlGet();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				
				if(BaseRequest.REQ_RET_OK == result) {
					
					if (list != null && mCurrentPositionInt < list.size()) {
						
						list.remove(mCurrentPositionInt);
					}
					
					notifyDataSetChanged();
					
				} else {
					

				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
	
	/**
	 * 处理图片数据
	 *
	 * @return
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
			if (item.localImageURL != null && item.localImageURL.size() > 0 ) {

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
     * 弹出评论对话框
     * */  
    private void showPopupWindow(View temp) {  
    	
		int[] positon = {0,0};
		
		temp.getLocationOnScreen(positon);
		
		int scrollInt = positon[1]+temp.getHeight();
 
        if(!mCommentAddPop.isShowing()) {  
        	
        	mInputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        	
        	if (mRefreshView == null) {
        		
        		mCommentAddPop.showAtLocation(mList, Gravity.BOTTOM, 0, 0); 
        		
        	} else {
        		
        		mCommentAddPop.showAtLocation(mRefreshView, Gravity.BOTTOM, 0, 0); 
        	}
        	 
        	mList.smoothScrollBy(scrollInt-600, 200);
        	
        	if (toname != null && toname.length() > 0) {
        		
        		mCommentsEdit.setHint("回复:"+toname);
        	}

        } else {  
        	
        	mCommentAddPop.dismiss();  
        }  
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
			
			mCurrentComments.put("tipid", list.get(mCurrentPositionInt).tipid);
			mCurrentComments.put("sn", sn);
			mCurrentComments.put("name", name);
			mCurrentComments.put("avatar", avatar);
			mCurrentComments.put("content", commentsStr);
			mCurrentComments.put("toname", toname);
			mCurrentComments.put("tosn", tosn);
			
			/*提交评论*/
			WaitDialog.showWaitDialog(mContext, R.string.str_loading_add_comment);
			new AsyncTask<Object, Object, Integer>() {
			
				FriendCommentsAdd request = new FriendCommentsAdd(mContext, mCurrentComments.get("sn"), 
						mCurrentComments.get("name") ,mCurrentComments.get("avatar") ,mCurrentComments.get("tipid"),
						mCurrentComments.get("tosn"), mCurrentComments.get("toname"),mCurrentComments.get("content"));
				@Override
				protected Integer doInBackground(Object... params) {

					return request.connectUrlGet();
				}
				@Override
				protected void onPostExecute(Integer result) {
					super.onPostExecute(result);
					
					if(BaseRequest.REQ_RET_OK == result) {
						
						int size = list.get(mCurrentPositionInt).comments.size();
						
						/*不能直接天剑mCurrentComments，要新添加hashmap*/
						HashMap<String, String> hash = new HashMap<String, String>();
						hash.put("tipid", mCurrentComments.get("tipid"));
						hash.put("sn", mCurrentComments.get("sn"));
						hash.put("name", mCurrentComments.get("name"));
						hash.put("content", mCurrentComments.get("content"));
						hash.put("toname", mCurrentComments.get("toname"));
						hash.put("tosn", mCurrentComments.get("tosn"));
						
						list.get(mCurrentPositionInt).comments.add(size,hash);
						notifyDataSetChanged();
						
						/*一旦点击了发送按钮，就应该把输入框清空*/
						mCommentsEdit.setText("");
						mCommentAddPop.dismiss();
						
					} else {
						
						
						
					}
					WaitDialog.dismissWaitDialog();
				}
			}.execute(null, null, null);
			
		} 
			
	}
}
