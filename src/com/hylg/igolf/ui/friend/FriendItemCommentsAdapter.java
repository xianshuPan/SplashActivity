package com.hylg.igolf.ui.friend;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.CoachComemntsItem;
import com.hylg.igolf.cs.data.Customer;
import com.hylg.igolf.ui.member.MemDetailActivityNew;
import com.hylg.igolf.ui.view.CircleImageView;
import com.hylg.igolf.ui.widget.IgBaseAdapter;
import com.hylg.igolf.utils.DownLoadImageTool;
import com.hylg.igolf.utils.GlobalData;
import com.hylg.igolf.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendItemCommentsAdapter extends BaseAdapter {
	
	private final String 					TAG = "CoachCommentsAdapter";
	
	
	private FriendTipsDetailActivity 		context;
	
	// data list
	private ArrayList<HashMap<String,String>> 	list;

	private int 							starSize;
	private Customer 						gd;
	
	
	public FriendItemCommentsAdapter(FriendTipsDetailActivity context, ArrayList<HashMap<String,String>> list) {
		this.context = context;
		this.list = list;
		starSize = (int) context.getResources().getDimension(R.dimen.golfers_li_rate_star_size);
		gd = MainApp.getInstance().getCustomer();
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
			String commentNameStr = list.get(position).get("name");

						/*如果是自己则不能回复*/
			if(commentSnStr != null && !commentSnStr.equalsIgnoreCase(gd.sn)) {

				context.tosn = commentSnStr;
				context.toname = commentNameStr;
				context.showPopupWindow();
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
}
