package com.hylg.igolf.ui.member;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.Member;
import com.hylg.igolf.cs.loader.AsyncImageLoader;
import com.hylg.igolf.cs.loader.AsyncImageLoader.ImageCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.GetMember;
import com.hylg.igolf.ui.common.AlbumPagerActivity;
import com.hylg.igolf.ui.hall.StartInviteStsActivity;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.GlobalData;
import com.hylg.igolf.utils.Utils;

public class MemDetailActivity extends Activity implements OnClickListener{
	
	private static final String TAG = "MemDetailActivity";
	private TextView nicknameView,ageView,regionView,yearsexpView,
		cityrankView,heatView,activityView,signatureView,bestView,
		handicapiView,totalMatchesView,fightView,scoreView;
	private ImageView sexImgView,avatarView;
	private LinearLayout ratell;
	private Button callBtn,inviteBtn;
	private String memSn;
	private static final String BUNDLE_KEY_MEM_SN = "memSn";
	private GlobalData gd;
	private String phoneNumber,scroeMsg,fightMsg;
	private LinearLayout albumLayout;
	private int ablumSpace;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mem_ac_detail);
		getViews();
		getMemberInfo();
		ablumSpace = getResources().getDimensionPixelSize(R.dimen.detail_ablum_pad);
	}
	
	public static void startMemDetailActivity(Context context,String memSn) {
		Intent intent = new Intent(context, MemDetailActivity.class);
		intent.putExtra(BUNDLE_KEY_MEM_SN, memSn);
		context.startActivity(intent);
	}
	
	private void getViews(){
		nicknameView = (TextView) findViewById(R.id.meminfo_nickname_txt);
		ageView = (TextView) findViewById(R.id.meminfo_age_txt);
		regionView = (TextView) findViewById(R.id.meminfo_region_txt);
		ratell = (LinearLayout) findViewById(R.id.meminfo_rate_ll);
		sexImgView = (ImageView) findViewById(R.id.meminfo_sex_img);
		avatarView = (ImageView) findViewById(R.id.member_avatar);
		yearsexpView = (TextView) findViewById(R.id.meminfo_yearsexp_txt);
		cityrankView = (TextView) findViewById(R.id.meminfo_cityrank_txt);
		heatView = (TextView) findViewById(R.id.meminfo_heat_txt);
		activityView = (TextView) findViewById(R.id.meminfo_activity_txt);
		signatureView = (TextView) findViewById(R.id.meminfo_signature_txt);
		bestView = (TextView) findViewById(R.id.meminfo_best_txt);
		handicapiView = (TextView) findViewById(R.id.meminfo_handicapi_txt);
		totalMatchesView = (TextView) findViewById(R.id.meminfo_matches_txt);
		fightView = (TextView) findViewById(R.id.member_detail_fightMsg_selection);
		scoreView = (TextView) findViewById(R.id.member_detail_scoreMsg_selection);
		callBtn = (Button) findViewById(R.id.member_detail_oper_btn_call);
		inviteBtn = (Button) findViewById(R.id.member_detail_oper_btn_invite);
		callBtn.setOnClickListener(this);
		inviteBtn.setOnClickListener(this);
		findViewById(R.id.member_detail_topbar_back).setOnClickListener(this);
		findViewById(R.id.member_detail_fightMsg_ll).setOnClickListener(this);
		findViewById(R.id.member_detail_scoreMsg_ll).setOnClickListener(this);
		findViewById(R.id.member_detail_topbar_back).setOnClickListener(this);
		gd = MainApp.getInstance().getGlobalData();
		this.memSn = getIntent().getExtras().getString(BUNDLE_KEY_MEM_SN);
		albumLayout = (LinearLayout) findViewById(R.id.member_detail_photo_ll);
	}
	
	private void getMemberInfo(){
		new AsyncTask<Object, Object, Integer>() {
			GetMember request = new GetMember(MemDetailActivity.this, MainApp.getInstance().getCustomer().sn, memSn);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(BaseRequest.REQ_RET_OK == result) {
					initMemberInfo(request.getMember());
				} else {
					Toast.makeText(MemDetailActivity.this,
							request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
			}
		}.execute(null, null, null);
	}
	
	private void initMemberInfo(Member member){
		loadAvatar(member.sn,member.avatar);
		ageView.setText(String.format(getResources().getString(R.string.str_member_age_wrap), member.age));
		regionView.setText(gd.getRegionName(member.city));
		nicknameView.setText(member.nickname);
		Utils.setLevel(MemDetailActivity.this, ratell, (int) getResources().getDimension(R.dimen.personal_detail_rate_star_size), member.rate);
		yearsexpView.setText(member.yearsExpStr);
		cityrankView.setText(Utils.getCityRankString(this, member.rank));
		heatView.setText(String.valueOf(member.heat));
		activityView.setText(String.valueOf(member.activity));
		bestView.setText(Utils.getIntString(this, member.best));
		handicapiView.setText(Utils.getDoubleString(this, member.handicapIndex));
		totalMatchesView.setText(String.valueOf(member.matches));
		if(Const.SEX_MALE == member.sex) {
			sexImgView.setImageResource(R.drawable.ic_male);
		} else {
			sexImgView.setImageResource(R.drawable.ic_female);
		}
		if(member.fightMsg.isEmpty()){
			fightView.setText(R.string.str_member_fight_record_none);
		}else{
			fightView.setText(member.fightMsg);
		}
		if(member.signature.isEmpty()){
			signatureView.setText(R.string.str_comm_def_signature);
		}else{
			signatureView.setText(member.signature);
		}
		scoreView.setText(member.scoreMsg);
		phoneNumber = member.phone;
		scroeMsg = member.scoreMsg;
		fightMsg = member.fightMsg;
		initAlbum(member);
	}
	
	private void initAlbum(final Member member) {
		for(int i=0, size=member.album.size(); i<size; i++){
			final int position = i;
			String albumName = member.album.get(position);
			Drawable album = AsyncImageLoader.getInstance().getAlbum(this, member.sn, albumName,0);
			final ImageView imageView = getAlbumIv();
			LinearLayout ll = getAlbumLl(i+1 != size);
			if(null != album) {
				imageView.setImageDrawable(album);
			} else {
				imageView.setImageResource(R.drawable.avatar_loading);
				AsyncImageLoader.getInstance().loadAlbum(this, member.sn, albumName,0,
						new ImageCallback() {
							@Override
							public void imageLoaded(Drawable imageDrawable) {
								if(null != imageDrawable && null != imageView) {
									Utils.logh(TAG, "imageLoaded");
									imageView.setImageDrawable(imageDrawable);
								}
							}
					});
			}
			imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					imageBrower(position, member);
				}
			});
			ll.addView(imageView);
			albumLayout.addView(ll);
		}
	}

	private LinearLayout getAlbumLl(boolean rightSpace) {
		LinearLayout ll = new LinearLayout(this);
		ll.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
		if(rightSpace) {
			ll.setPadding(0, ablumSpace, ablumSpace, ablumSpace);
		} else {
			ll.setPadding(0, ablumSpace, 0, ablumSpace);
		}
		ll.setGravity(Gravity.CENTER);
		return ll;
	}
	
	private ImageView getAlbumIv() {
		ImageView imageView = new ImageView(this);
		imageView.setScaleType(ScaleType.CENTER_CROP);
		int size = (int) getResources().getDimension(R.dimen.detail_ablum_size);
		imageView.setLayoutParams(new LayoutParams(size, size));
		return imageView;
	}
	
	private void imageBrower(int position, Member member) {
		ArrayList<String> album = member.album;
		String[] urls = new String[album.size()];
		int index = 0;
		for(String name : album) {
			urls[index] = name;
			index ++;
		}
		AlbumPagerActivity.startAlbumPagerMember(this, position, urls, member.sn);
	}
	
	private void loadAvatar(String sn,String filename){
		Drawable avatar = AsyncImageLoader.getInstance().getAvatar(this, sn, filename,
				(int) getResources().getDimension(R.dimen.avatar_detail_size));
		if(null != avatar) {
			
			avatarView.setImageDrawable(avatar);
			
		} else {
			avatarView.setImageResource(R.drawable.avatar_loading);
			AsyncImageLoader.getInstance().loadAvatar(this, sn, filename,
					new ImageCallback() {
						@Override
						public void imageLoaded(Drawable imageDrawable) {
							if(null != imageDrawable && null != avatarView) {
								avatarView.setImageDrawable(imageDrawable);
							}
						}
				});
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.member_detail_oper_btn_call:
				if(phoneNumber.isEmpty()){
					Toast.makeText(MemDetailActivity.this, R.string.str_member_not_call, Toast.LENGTH_SHORT).show();
				}else{
					Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:" + phoneNumber));
					startActivity(intent);
				}
				break;
	
			case R.id.member_detail_oper_btn_invite:
				StartInviteStsActivity.startOpenInvite(MemDetailActivity.this, memSn);
				overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;
			case R.id.member_detail_topbar_back:
				finishWithAnim();
				break;
			case R.id.member_detail_fightMsg_ll:
				MemFightRecordActivity.startMemFightRecordActivity(MemDetailActivity.this, fightMsg,memSn);
				overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;
			case R.id.member_detail_scoreMsg_ll:
				MemScoreHistoryActivity.startMemScoreHistoryActivity(MemDetailActivity.this, scroeMsg,memSn);
				overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(KeyEvent.KEYCODE_BACK == keyCode) {
			finishWithAnim();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void finishWithAnim() {
		finish();
		overridePendingTransition(R.anim.ac_slide_left_in, R.anim.ac_slide_right_out);
		
	}
}
