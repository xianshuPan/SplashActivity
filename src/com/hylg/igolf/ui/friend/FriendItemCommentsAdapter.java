package com.hylg.igolf.ui.friend;

import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.Customer;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.FriendCommentDelete;
import com.hylg.igolf.ui.member.MemDetailActivityNew;
import com.hylg.igolf.ui.view.CircleImageView;
import com.hylg.igolf.utils.DownLoadImageTool;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendItemCommentsAdapter extends BaseAdapter {
	
	private final String 					TAG = "CoachCommentsAdapter";
	
	
	private FriendTipsDetailActivity 		context;
	
	// data list
	private ArrayList<HashMap<String,String>> 	list;

	private int 							starSize;
	private Customer 						gd;

	private Handler mHandler ;
	
	
	public FriendItemCommentsAdapter(FriendTipsDetailActivity context, ArrayList<HashMap<String,String>> list,Handler handler) {
		this.context = context;
		this.list = list;
		starSize = (int) context.getResources().getDimension(R.dimen.golfers_li_rate_star_size);
		gd = MainApp.getInstance().getCustomer();
		mHandler = handler;
	}

	public void refreshListInfo(ArrayList<HashMap<String,String>> list) {
		this.list.clear();
		this.list = list;
		notifyDataSetChanged();
	}

	public void addNewItem(HashMap<String,String> item) {

		if (list != null) {

			list.add(0,item);
		}
		notifyDataSetChanged();
	}
	
	public void appendListInfo(ArrayList<HashMap<String,String>> list) {
		for(int i=0, size=list.size(); i<size; i++) {
			this.list.add(list.get(i));
		}
		notifyDataSetChanged();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHodler holder;
		if(null == convertView) {
			convertView = View.inflate(context, R.layout.friend_comments_item, null);
			
			holder = new ViewHodler();
			
			holder.avatarIv = (CircleImageView) convertView.findViewById(R.id.coach_comments_item_avatar);
			holder.nickNameTxt = (TextView) convertView.findViewById(R.id.coach_comments_item_nickname);
			holder.contentTxt = (TextView) convertView.findViewById(R.id.coach_comments_item_content_text);
			holder.timeTxt = (TextView) convertView.findViewById(R.id.coach_comments_item_time_text);

			convertView.setTag(holder);
			
		} else {
			holder = (ViewHodler) convertView.getTag();
		}
		HashMap<String,String> data = list.get(position);
		
		convertView.setBackgroundResource(R.drawable.list_item_even_bkg);

		DownLoadImageTool.getInstance(context).displayImage(Utils.getAvatarURLString(data.get("sn")), holder.avatarIv, null);
		//Utils.loadAvatar(context,data.get("sn"),holder.avatarIv);
		
		holder.nickNameTxt.setText(data.get("name"));

		if (data.get("toname") != null && data.get("toname").length() > 0) {

			holder.nickNameTxt.setText(data.get("name")+"回复:"+data.get("toname"));
		}

		holder.contentTxt.setText(data.get("content"));
		holder.timeTxt.setText(Utils.StringTimeToString(data.get("commentstime")));

		holder.avatarIv.setOnClickListener(new OnAvatarClickListener(position));

		convertView.setOnClickListener(new OnItemClickListener(position));


		return convertView;
	}

	private class ViewHodler {
		
		protected CircleImageView avatarIv;
		
		protected TextView nickNameTxt;
		protected TextView contentTxt;
		protected TextView timeTxt;

	}

	private class OnAvatarClickListener implements View.OnClickListener {
		private int position;

		public OnAvatarClickListener(int pos) {
			position = pos;
		}

		@Override
		public void onClick(View v) {
			String memSn = list.get(position).get("sn");
			// 自己不跳转
			if(memSn.endsWith(MainApp.getInstance().getCustomer().sn)) {
				return ;
			}
			MemDetailActivityNew.startMemDetailActivity(context, memSn);
		}
	}

	private class OnItemClickListener implements View.OnClickListener {
		private int position;

		public OnItemClickListener(int pos) {
			position = pos;
		}

		@Override
		public void onClick(View v) {
			String commentSnStr = list.get(position).get("sn");
			String commentIdStr = list.get(position).get("id");
			String commentNameStr = list.get(position).get("name");

						/*如果是自己则不能回复*/
			if(commentSnStr != null && !commentSnStr.equalsIgnoreCase(gd.sn)) {

				context.tosn = commentSnStr;
				context.toname = commentNameStr;
				context.showPopupWindow();
			}
			else {

				showDeletePopWin(commentIdStr);
			}
		}
	}

	

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int i) {
		return 0;
	}


	private PopupWindow mDesPopWin;
	private void showDeletePopWin(final String commentId) {
		if(null != mDesPopWin && mDesPopWin.isShowing()) {
			return ;
		}
		RelativeLayout sv = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.friend_circle_delete_pop, null);
		TextView delete_text = (TextView)sv.findViewById(R.id.delete_comment_txt);
		TextView cancel_text = (TextView)sv.findViewById(R.id.delete_comment_cancel_txt);
		mDesPopWin = new PopupWindow(sv, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
		mDesPopWin.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(android.R.color.transparent)));
		mDesPopWin.setAnimationStyle(android.R.style.Animation_Dialog);

		delete_text.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				deleteSelefComment(commentId);
			}
		});
		sv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismissExchgPopwin();
			}
		});
		cancel_text.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismissExchgPopwin();
			}
		});
		mDesPopWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				if (null != mDesPopWin) {
					Utils.logh(TAG, "onDismiss ");
					mDesPopWin = null;
				}
			}
		});

		mDesPopWin.showAtLocation(sv, Gravity.CENTER, 0, 0);
	}

	private void dismissExchgPopwin() {
		if(null != mDesPopWin && mDesPopWin.isShowing()) {
			mDesPopWin.dismiss();
//			mDesPopWin = null;
		}
	}

	/*
	 * 删除
	 * */
	private void deleteSelefComment(final String commentId) {

		/**/
		WaitDialog.showWaitDialog(context, R.string.str_loading_waiting);
		new AsyncTask<Object, Object, Integer>() {

			final FriendCommentDelete request = new FriendCommentDelete(context,commentId,gd.sn);
			@Override
			protected Integer doInBackground(Object... params) {

				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);

				dismissExchgPopwin();
				if(BaseRequest.REQ_RET_OK == result) {

					if (list != null && list.size() > 0) {

						int count = list.size();

						for (int i = 0;i < count;i++) {

							if (commentId.equalsIgnoreCase(list.get(i).get("id"))) {

								list.remove(i);
								break;

							}
						}

					}

					Message msg = mHandler.obtainMessage();
					msg.what = FriendTipsDetailActivity.DELETE_COMMENT_SUCC;
					mHandler.sendMessage(msg);
					notifyDataSetChanged();

				} else {

					Toast.makeText(context, request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
}
