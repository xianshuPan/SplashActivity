package com.hylg.igolf.ui.golfers.adapter;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.GolferInfo;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.FriendAttentionAdd;
import com.hylg.igolf.ui.widget.IgBaseAdapter;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.GlobalData;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

import java.util.ArrayList;

public class GolfersAdapter extends IgBaseAdapter {
	private Activity context;
	// click index
	public final static int GOLFERS_INDEX_ITEM = 0;
	public final static int GOLFERS_INDEX_AVATAR = 1;
	public final static int GOLFERS_INDEX_INVITE = 2;
	// data list
	private ArrayList<GolferInfo> list;
	// hadler to transfer data by message
	private Handler mHandle;
	// bundle data key: list, search result.
	public final static String BUNDLE_KEY_GOLFERS_LIST = "golfersList";
	public final static String BUNDLE_KEY_GOLFERS_SEARCH = "golfersSearch";
	private String bundleKey;
	private int starSize;
	private GlobalData gd;

	private boolean mIsRecommand = false;
	
	public GolfersAdapter(Activity context, Handler handle, String bundleKey, ArrayList<GolferInfo> list,boolean isRecommand) {
		this.context = context;
		mHandle = handle;
		this.bundleKey = bundleKey;
		this.list = list;
		starSize = (int) context.getResources().getDimension(R.dimen.golfers_li_rate_star_size);

		mIsRecommand = isRecommand;
		gd = MainApp.getInstance().getGlobalData();
	}

	public void refreshListInfo(ArrayList<GolferInfo> list) {
		this.list.clear();
		this.list = list;
		notifyDataSetChanged();
	}
	
	public void appendListInfo(ArrayList<GolferInfo> list) {
		for(int i=0, size=list.size(); i<size; i++) {
			this.list.add(list.get(i));
		}
		notifyDataSetChanged();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHodler holder;
		if(null == convertView) {
			convertView = View.inflate(context, R.layout.golfers_list_item, null);
			holder = new ViewHodler();
			holder.inviteBtn = (Button) convertView.findViewById(R.id.golfers_li_invite_btn);
			holder.attentionBtn = (Button) convertView.findViewById(R.id.golfers_li_attention_btn);
			holder.avatarIv = (ImageView) convertView.findViewById(R.id.golfers_li_avatar);
			holder.sexIv = (ImageView) convertView.findViewById(R.id.golfers_li_sex);
			holder.nicknameTv = (TextView) convertView.findViewById(R.id.golfers_li_nickname);
			holder.handicapiTv = (TextView) convertView.findViewById(R.id.golfers_li_handicapi);
			holder.regionTv = (TextView) convertView.findViewById(R.id.golfers_li_region);
			holder.rateLl = (LinearLayout) convertView.findViewById(R.id.golfers_li_rate_ll);
			holder.yearsExpTv = (TextView) convertView.findViewById(R.id.golfers_li_yearsExp);
			holder.industryTv = (TextView) convertView.findViewById(R.id.golfers_li_industry);
			convertView.setTag(holder);
		} else {

			holder = (ViewHodler) convertView.getTag();
		}
		GolferInfo data = list.get(position);
		
//		if(position % 2 == 0) {
			convertView.setBackgroundResource(R.drawable.list_item_even_bkg);
//		} else {
//			convertView.setBackgroundResource(R.drawable.list_item_odd_bkg);
//		}

		if (mIsRecommand) {

			holder.attentionBtn.setVisibility(View.VISIBLE);

			if(list.get(position).attention == 0) {

				holder.attentionBtn.setText(R.string.str_friend_attention);//color_tab_green
				holder.attentionBtn.setTextColor(context.getResources().getColor(R.color.color_tab_green));
				holder.attentionBtn.setBackgroundResource(R.drawable.attent_color);

			}
			else if (list.get(position).attention == 1) {

				holder.attentionBtn.setText(R.string.str_friend_attented);
				holder.attentionBtn.setTextColor(context.getResources().getColor(R.color.color_white));
				holder.attentionBtn.setBackgroundResource(R.drawable.attented_color);
			}
		}
		else {

			holder.attentionBtn.setVisibility(View.GONE);
		}
		convertView.setOnClickListener(new OnItemChildClickListener(GOLFERS_INDEX_ITEM, position));
		holder.inviteBtn.setOnClickListener(new OnItemChildClickListener(GOLFERS_INDEX_INVITE, position));
		holder.avatarIv.setOnClickListener(new OnItemChildClickListener(GOLFERS_INDEX_AVATAR, position));
		holder.attentionBtn.setOnClickListener(new OnItemAttentionClickListener(position));
		
		loadAvatar(context, data.sn, data.avatar, holder.avatarIv);

		if(Const.SEX_MALE == data.sex) {
			holder.sexIv.setImageResource(R.drawable.man);
		} else {
			holder.sexIv.setImageResource(R.drawable.woman);
		}
		holder.nicknameTv.setText(data.nickname);
		holder.handicapiTv.setText(Utils.getDoubleString(context, data.handicapIndex));
		holder.regionTv.setText(gd.getRegionName(data.region));
		//Utils.setLevel(context, holder.rateLl, starSize, data.rate);
		holder.yearsExpTv.setText(String.format(context.getString(R.string.str_golfers_li_yearsexp), data.yearsExp));
		holder.industryTv.setText(gd.getIndustryName(data.industry));
		return convertView;
	}

	private class ViewHodler {
		protected Button inviteBtn;
		protected Button attentionBtn;
		protected ImageView avatarIv;
		protected ImageView sexIv;
		protected TextView handicapiTv;
		protected TextView nicknameTv;
		protected TextView regionTv;
		protected LinearLayout rateLl;
		protected TextView industryTv;
		protected TextView yearsExpTv;
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


	private class OnItemAttentionClickListener implements View.OnClickListener {
		private int position;

		public OnItemAttentionClickListener( int position) {

			this.position = position;
		}

		@Override
		public void onClick(View v) {

			attention(position);
		}

	}

	/*
	 * 添加关注
	 * */
	private void attention(final int position) {

		/*添加或取消关注*/
		WaitDialog.showWaitDialog(context, R.string.str_loading_waiting);
		new AsyncTask<Object, Object, Integer>() {

			FriendAttentionAdd request = new FriendAttentionAdd(context,MainApp.getInstance().getCustomer().sn,list.get(position).sn,list.get(position).attention);
			@Override
			protected Integer doInBackground(Object... params) {

				return request.connectUrlGet();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);

				if(BaseRequest.REQ_RET_OK == result) {

					/*已经关注过*/
					int attention = list.get(position).attention;

					list.get(position).attention = attention == 1 ? 0 : 1;

					notifyDataSetChanged();

				} else {

					Toast.makeText(context,request.getFailMsg(),Toast.LENGTH_SHORT).show();
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
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
