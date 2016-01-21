package com.hylg.igolf.ui.coach;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import cn.gl.lib.view.RoundedImageView;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.CoachItem;
import com.hylg.igolf.ui.widget.IgBaseAdapter;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.GlobalData;
import com.hylg.igolf.utils.Utils;

public class CoachListAdapter extends IgBaseAdapter {
	
	private final String 					TAG = "CoachListAdapter";
	
	// click index
	public final static int 				GOLFERS_INDEX_ITEM = 0;
	public final static int 				GOLFERS_INDEX_AVATAR = 1;
	public final static int 				GOLFERS_INDEX_INVITE = 2;
	
	private Activity 						context;
	
	// data list
	private ArrayList<CoachItem> 			list;
	// hadler to transfer data by message
	private Handler 						mHandle;
	// bundle data key: list, search result.
	public final static String 				BUNDLE_KEY_COACH_LIST = "coachList";
	public final static String 				BUNDLE_KEY_COACH_SEARCH = "coachSearch";
	private String 							bundleKey;
	private int 							starSize;
	private GlobalData 						gd;

	
	
	public CoachListAdapter(Activity context, Handler handle, String bundleKey, ArrayList<CoachItem> list) {
		this.context = context;
		mHandle = handle;
		this.bundleKey = bundleKey;
		this.list = list;
		starSize = (int) context.getResources().getDimension(R.dimen.golfers_li_rate_star_size);
		gd = MainApp.getInstance().getGlobalData();
	}

	public void refreshListInfo(ArrayList<CoachItem> list) {
		this.list.clear();
		this.list = list;
		notifyDataSetChanged();
	}
	
	public void appendListInfo(ArrayList<CoachItem> list) {
		for(int i=0, size=list.size(); i<size; i++) {
			this.list.add(list.get(i));
		}
		notifyDataSetChanged();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHodler holder;
		if(null == convertView) {
			convertView = View.inflate(context, R.layout.coach_list_item, null);
			holder = new ViewHodler();
			
			holder.avatarIv = (ImageView) convertView.findViewById(R.id.coach_item_avatar);
			holder.typeIv = (ImageView) convertView.findViewById(R.id.coach_item_type_image);
			holder.sexIv = (ImageView) convertView.findViewById(R.id.coach_item_sex_image);
			holder.handicapiTv = (TextView) convertView.findViewById(R.id.coach_item_handicapIndex_text);
			holder.star = (RatingBar) convertView.findViewById(R.id.coach_item_rating);
			holder.nicknameTv = (TextView) convertView.findViewById(R.id.coach_item_nickname);
			holder.teachTimeTv = (TextView) convertView.findViewById(R.id.coach_item_teach_times_text);
			holder.ageTv = (TextView) convertView.findViewById(R.id.coach_item_teach_years_text);
			holder.specialTv = (TextView) convertView.findViewById(R.id.coach_item_special_text);
			holder.distanceTv = (TextView) convertView.findViewById(R.id.coach_item_distance_text);
			holder.distanceTimeTv = (TextView) convertView.findViewById(R.id.coach_item_distance_time_text);

			convertView.setTag(holder);
			
		} else {
			
			holder = (ViewHodler) convertView.getTag();
		}
		CoachItem data = list.get(position);
		
		convertView.setBackgroundResource(R.drawable.list_item_even_bkg);
		
		CoachItem item = list.get(position);
		
		if (item.type == Const.PROFESSIONAL_COACH) {
			
			holder.typeIv.setBackgroundResource(R.drawable.professional);
			
		} else {
			
			holder.typeIv.setBackgroundResource(R.drawable.white_bg);
		}

		if (item.sex == Const.SEX_MALE) {

			holder.sexIv.setBackgroundResource(R.drawable.man);

		} else {

			holder.sexIv.setBackgroundResource(R.drawable.woman);
		}
		
		holder.handicapiTv.setText(Utils.getDoubleString(context, item.handicapIndex));
		holder.nicknameTv.setText(item.nickname);
		holder.teachTimeTv.setText(String.valueOf(item.teachTimes));
		holder.ageTv.setText(String.valueOf(item.teachYear));
		holder.specialTv.setText(item.special);

		if (item.distance <= 1) {

			holder.distanceTv.setText("附近");
		}
		else if (item.distance > 2000) {

			holder.distanceTv.setText(MainApp.getInstance().getGlobalData().getRegionName(item.city));
		}
		else {

			holder.distanceTv.setText(String.valueOf(item.distance)+"km");
		}
		//holder.distanceTv.setText(String.valueOf(item.distance)+"km");
		holder.distanceTimeTv.setText(Utils.handTime(item.distanceTime));
		holder.star.setRating(item.rate);
		convertView.setOnClickListener(new OnItemChildClickListener(GOLFERS_INDEX_ITEM, position));
		holder.avatarIv.setOnClickListener(new OnItemChildClickListener(GOLFERS_INDEX_AVATAR, position));
		
		loadAvatar(context, data.sn, data.avatar, holder.avatarIv);


		return convertView;
	}

	private class ViewHodler {
		
		protected ImageView avatarIv;
		protected ImageView sexIv;
		protected ImageView typeIv;

		protected RatingBar star ;
		
		protected TextView handicapiTv;
		protected TextView nicknameTv;
		
		protected TextView teachTimeTv;
		protected TextView ageTv;
		protected TextView specialTv;
		protected TextView distanceTv;
		protected TextView distanceTimeTv;
	}
	
	private class OnItemChildClickListener implements View.OnClickListener {
		private int clickIndex;
		private int position;
		
		public OnItemChildClickListener(int clickIndex, int position) {
			this.clickIndex = clickIndex;
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			Message msg = new Message();
			msg.what = clickIndex;
//			msg.arg1 = position;
			Bundle b = new Bundle();
			b.putSerializable(bundleKey, list.get(position));
			msg.setData(b);
			mHandle.sendMessage(msg);
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
}
