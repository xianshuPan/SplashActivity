package com.hylg.igolf.ui.friend;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.ui.member.MemDetailActivityNew;
import com.hylg.igolf.ui.view.CircleImageView;
import com.hylg.igolf.utils.DownLoadImageTool;
import com.hylg.igolf.utils.GlobalData;
import com.hylg.igolf.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendItemPraiserAdapter extends BaseAdapter {

	private final String 					TAG = "CoachCommentsAdapter";


	private Activity 						context;

	// data list
	public ArrayList<HashMap<String,String>> 	list;

	private int 							starSize;
	private GlobalData 						gd;


	public FriendItemPraiserAdapter(Activity context, ArrayList<HashMap<String, String>> list) {
		this.context = context;
		this.list = list;
		starSize = (int) context.getResources().getDimension(R.dimen.golfers_li_rate_star_size);
		gd = MainApp.getInstance().getGlobalData();
	}

	public void refreshListInfo(ArrayList<HashMap<String,String>> list) {
		this.list.clear();
		this.list = list;
		notifyDataSetChanged();
	}
	
	public void appendListInfo(ArrayList<HashMap<String,String>> list) {
		for(int i=0, size=list.size(); i<size; i++) {
			this.list.add(list.get(i));
		}
		notifyDataSetChanged();
	}

	public void addNewItem(HashMap<String,String> item) {

		if (list != null) {

			boolean result = false;
			for (int i=0, size=list.size(); i<size; i++) {

				if (list.get(i).get("sn").equalsIgnoreCase(item.get("sn"))) {

					list.remove(i);
					result = true;
					break;
				}

			}

			if (!result) {

				list.add(0,item);
			}
		}
		notifyDataSetChanged();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHodler holder;
		if(null == convertView) {
			convertView = View.inflate(context, R.layout.friend_praiser_item, null);
			
			holder = new ViewHodler();
			
			holder.avatarIv = (CircleImageView) convertView.findViewById(R.id.coach_comments_item_avatar);
			holder.nickNameTxt = (TextView) convertView.findViewById(R.id.coach_comments_item_nickname);


			convertView.setTag(holder);
			
		} else {
			holder = (ViewHodler) convertView.getTag();
		}
		final HashMap<String,String> data = list.get(position);
		
		convertView.setBackgroundResource(R.drawable.list_item_even_bkg);

		DownLoadImageTool.getInstance(context).displayImage(Utils.getAvatarURLString(data.get("sn")), holder.avatarIv, null);
		//Utils.loadAvatar(context,data.get("sn"),holder.avatarIv);
		
		holder.nickNameTxt.setText(data.get("name"));

		convertView.setOnClickListener(new OnItemChildClickListener(position));

		return convertView;
	}

	private class ViewHodler {
		
		protected CircleImageView avatarIv;
		
		protected TextView nickNameTxt;

	}

	private class OnItemChildClickListener implements View.OnClickListener {
		private int position;

		public OnItemChildClickListener(int pos) {
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
