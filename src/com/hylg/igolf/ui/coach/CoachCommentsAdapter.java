package com.hylg.igolf.ui.coach;

import java.util.ArrayList;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import cn.gl.lib.view.RoundedImageView;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.CoachComemntsItem;
import com.hylg.igolf.cs.data.CoachItem;
import com.hylg.igolf.ui.view.CircleImageView;
import com.hylg.igolf.ui.widget.IgBaseAdapter;
import com.hylg.igolf.utils.GlobalData;
import com.hylg.igolf.utils.Utils;
import com.xc.lib.utils.Tools;

public class CoachCommentsAdapter extends IgBaseAdapter {
	
	private final String 					TAG = "CoachCommentsAdapter";
	
	
	private Activity 						context;
	
	// data list
	private ArrayList<CoachComemntsItem> 	list;

	private int 							starSize;
	private GlobalData 						gd;
	
	
	public CoachCommentsAdapter(Activity context, ArrayList<CoachComemntsItem> list) {
		this.context = context;
		this.list = list;
		starSize = (int) context.getResources().getDimension(R.dimen.golfers_li_rate_star_size);
		gd = MainApp.getInstance().getGlobalData();
	}

	public void refreshListInfo(ArrayList<CoachComemntsItem> list) {
		this.list.clear();
		this.list = list;
		notifyDataSetChanged();
	}
	
	public void appendListInfo(ArrayList<CoachComemntsItem> list) {
		for(int i=0, size=list.size(); i<size; i++) {
			this.list.add(list.get(i));
		}
		notifyDataSetChanged();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHodler holder;
		if(null == convertView) {
			convertView = View.inflate(context, R.layout.coach_comments_item, null);
			
			holder = new ViewHodler();
			
			holder.avatarIv = (CircleImageView) convertView.findViewById(R.id.coach_comments_item_avatar);
			holder.nickNameTxt = (TextView) convertView.findViewById(R.id.coach_comments_item_nickname);
			holder.contentTxt = (TextView) convertView.findViewById(R.id.coach_comments_item_content_text);
			holder.timeTxt = (TextView) convertView.findViewById(R.id.coach_comments_item_time_text);
			holder.rating = (RatingBar) convertView.findViewById(R.id.coach_comments_item_rating);

			convertView.setTag(holder);
			
		} else {
			holder = (ViewHodler) convertView.getTag();
		}
		CoachComemntsItem data = list.get(position);
		
		convertView.setBackgroundResource(R.drawable.list_item_even_bkg);

		loadAvatar(context, data.student_sn, data.student_avatar, holder.avatarIv);
		
		holder.nickNameTxt.setText(data.student_nick_name);
		holder.contentTxt.setText(data.content);
		holder.timeTxt.setText(Utils.longTimeToString(data.commentTime));
		holder.rating.setRating(data.star);

		return convertView;
	}

	private class ViewHodler {
		
		protected CircleImageView avatarIv;
		
		protected TextView nickNameTxt;
		protected TextView contentTxt;
		protected TextView timeTxt;
		
		protected RatingBar rating;
		

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
