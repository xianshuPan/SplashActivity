package com.hylg.igolf.ui.customer;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import android.widget.ImageView.ScaleType;

import cn.jpush.android.api.JPushInterface;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.CoachItem;
import com.hylg.igolf.cs.data.Customer;
import com.hylg.igolf.cs.data.Member;
import com.hylg.igolf.cs.loader.AsyncImageLoader;
import com.hylg.igolf.cs.loader.AsyncImageLoader.ImageCallback;
import com.hylg.igolf.cs.loader.GetMemberloader;
import com.hylg.igolf.cs.loader.GetMemberloader.GetMemberCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.ReturnCode;
import com.hylg.igolf.cs.request.UpdateAlbum;
import com.hylg.igolf.ui.account.LoginActivity;
import com.hylg.igolf.ui.common.AlbumPagerActivity;
import com.hylg.igolf.ui.common.AlbumPagerActivity.OnAlbumSetAvatarListener;
import com.hylg.igolf.ui.common.ImageSelectActivity;
import com.hylg.igolf.ui.common.ImageSelectActivity.onImageSelectListener;
import com.hylg.igolf.utils.*;

public class CustomerHomeFrg extends Fragment implements View.OnClickListener,onImageSelectListener,
	OnAlbumSetAvatarListener{
	private static final String TAG = "CustomerHomeFrg";
	private static CustomerHomeFrg customerFrg = null;
	private TextView signatureTxtExpand;
	private Customer customer;
	private GlobalData gd;
	private ImageView customerAvatar,sex,addAlbumView;
	private View addAlbumSpace;
	private TextView age,region,yearsExp,handicapi,best,matches,heat,rank,act;
	private static ImageView msgAlertIv;
	private GetMemberloader reqLoader = null;
	private LinearLayout albumLayout;
	private Uri mUri;
//	private Bitmap avatarBmp = null;
	private int ablumSpace;
	
	public static  CustomerHomeFrg getInstance() {
		if(null == customerFrg) {
			customerFrg = new CustomerHomeFrg();
		}
		return customerFrg;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		Bundle args = getArguments();cusinfo_logout
		customer = MainApp.getInstance().getCustomer();
		gd = MainApp.getInstance().getGlobalData();
		ablumSpace = getResources().getDimensionPixelSize(R.dimen.detail_ablum_pad);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.customer_frg_home, container, false);
		view.findViewById(R.id.cusinfo_logout).setOnClickListener(this);
		view.findViewById(R.id.cusinfo_myinfo_ll).setOnClickListener(this);
		view.findViewById(R.id.cusinfo_msg_ll).setOnClickListener(this);
		view.findViewById(R.id.cusinfo_history_ll).setOnClickListener(this);
		view.findViewById(R.id.cusinfo_about_ll).setOnClickListener(this);
		view.findViewById(R.id.cusinfo_expand_btn).setOnClickListener(this);
		
		signatureTxtExpand = (TextView) view.findViewById(R.id.cusinfo_signature_txt_expand);
		String sign = customer.signature.trim();
		if(sign.length() > 0) {
			
			signatureTxtExpand.setText(sign);
			
		}else{
			
			signatureTxtExpand.setText(R.string.str_comm_def_signature);
		}
		TextView nickName = (TextView) view.findViewById(R.id.cusinfo_nickname_txt);
		nickName.setText(customer.nickname);
		sex = (ImageView) view.findViewById(R.id.cusinfo_sex_img);
		if(Const.SEX_MALE == customer.sex) {
			sex.setImageResource(R.drawable.ic_male);
		} else {
			sex.setImageResource(R.drawable.ic_female);
		}
		LinearLayout rate = (LinearLayout) view.findViewById(R.id.cusinfo_rate_ll);
		Utils.setLevel(getActivity(), rate, (int) getResources().getDimension(R.dimen.personal_detail_rate_star_size), customer.rate);
		age = (TextView) view.findViewById(R.id.cusinfo_age_txt);
		age.setText(String.format(getResources().getString(R.string.str_member_age_wrap), customer.age));
		region = (TextView) view.findViewById(R.id.cusinfo_region_txt);
		region.setText(gd.getRegionName(customer.city));
		
		handicapi = (TextView) view.findViewById(R.id.cusinfo_handicapi_txt);
		handicapi.setText(Utils.getDoubleString(getActivity(), customer.handicapIndex));
		best = (TextView) view.findViewById(R.id.cusinfo_best_txt);
		best.setText(Utils.getIntString(getActivity(), customer.best));
		matches = (TextView) view.findViewById(R.id.cusinfo_matches_txt);
		matches.setText(String.valueOf(customer.matches));
		
		/**/
		yearsExp = (TextView) view.findViewById(R.id.cusinfo_yearsexp_txt);
		yearsExp.setText(customer.yearsExpStr);
		rank = (TextView) view.findViewById(R.id.cusinfo_cityrank_txt);
		rank.setText(Utils.getCityRankString(getActivity(), customer.rank));
		heat = (TextView) view.findViewById(R.id.cusinfo_heat_txt);
		heat.setText(String.valueOf(customer.heat));
		act = (TextView) view.findViewById(R.id.cusinfo_activity_txt);
		act.setText(String.valueOf(customer.activity));
		customerAvatar = (ImageView) view.findViewById(R.id.customer_avatar);
		msgAlertIv = (ImageView) view.findViewById(R.id.cusinfo_msg_img);
		
		/*是否显示msg 的*/
		if(MainApp.getInstance().getGlobalData().msgNumSys == 0){
			Utils.setGone(msgAlertIv);
		}else{
			Utils.setVisible(msgAlertIv);
		}
		
		/*加载头像*/
		loadAvatar(customer.sn,customer.avatar,customerAvatar);
		
		DebugTools.getDebug().debug_v("customer.avatar", "---->>>>"+customer.avatar);
		
		albumLayout = (LinearLayout) view.findViewById(R.id.cusinfo_myinfo_photo_ll);
		addAlbumView = (ImageView) view.findViewById(R.id.customer_album_add);
		addAlbumView.setOnClickListener(this);
		addAlbumSpace = view.findViewById(R.id.cusinfo_album_add_space);
		initAlbum();
		return view;
	}
	
	private LinearLayout getAlbumLl(boolean leftSpace) {
		LinearLayout ll = new LinearLayout(getActivity());
		ll.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
		if(leftSpace) {
			ll.setPadding(ablumSpace, ablumSpace, 0, ablumSpace);
		} else {
			ll.setPadding(0, ablumSpace, 0, ablumSpace);
		}
		ll.setGravity(Gravity.CENTER);
		return ll;
	}
	
	private ImageView getAlbumIv() {
		ImageView imageView = new ImageView(getActivity());
		imageView.setScaleType(ScaleType.CENTER_CROP);
		int size = (int) getResources().getDimension(R.dimen.detail_ablum_size);
		imageView.setLayoutParams(new LayoutParams(size, size));
		return imageView;
	}
	
	private void initAlbum(){
		refreshAlbumAdd();
		albumLayout.removeAllViews();
		Utils.logh(TAG, "album size:"+ customer.album.size());
		for(int i=0, size=customer.album.size(); i<size; i++){
			LinearLayout ll = getAlbumLl(i != 0);
			ll.addView(addAlbumImage(i));
			albumLayout.addView(ll);
		}
		
	}
	
	private ImageView addAlbumImage(final int position) {
		final ImageView imageView = getAlbumIv();
		String albumName = customer.album.get(position);
		
		DebugTools.getDebug().debug_v("albumName", "----->>>>>"+albumName);
		
		Drawable album = AsyncImageLoader.getInstance().getAlbum(getActivity(), customer.sn, albumName,Const.ALBUM_THUMBNAIL);
		Utils.logh(TAG, "addAlbumImage imageView: " + imageView + "  album: " + album);
		
		
		
		if(null != album) {
			imageView.setImageDrawable(album);
		} else {
			imageView.setImageResource(R.drawable.avatar_loading);
			AsyncImageLoader.getInstance().loadAlbum(getActivity(), customer.sn, albumName, Const.ALBUM_THUMBNAIL,
					new ImageCallback() {
						@Override
						public void imageLoaded(Drawable imageDrawable) {
							if(null != imageDrawable ) {
								Utils.logh(TAG, "imageLoaded " + imageView);
								imageView.setImageDrawable(imageDrawable);
							}
						}
				});
		}
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				imageBrower(position);
			}
		});
		return imageView;
	}
	
	private void imageBrower(int position) {
		ArrayList<String> album = customer.album;
		String[] urls = new String[album.size()];
		int index = 0;
		for(String name : album) {
			urls[index] = name;
			index ++;
		}
		AlbumPagerActivity.startAlbumPagerCustomer(this, position, urls, customer.sn);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		refreshDataAysnc();
	}
	
	private void clearLoader() {
		if(isLoading()) {
			GetMemberloader loader = reqLoader;
			loader.stopTask(true);
			Utils.logh(TAG, "clearLoader reqLoader: " + reqLoader);
		}
	}
	
	private boolean isLoading() {
		return (null != reqLoader && reqLoader.isRunning());
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		clearLoader();
	}

	private void refreshDataAysnc() {
		if(!Utils.isConnected(getActivity())){
			return;
		}
		// 正在请求，不重复请求
		if(isLoading()) {
			return ;
		}
		clearLoader();
		reqLoader = new GetMemberloader(getActivity(), customer.sn,customer.sn, new GetMemberCallback() {
			
			@Override
			public void callBack(int retId, String msg, Member member,CoachItem coach_item) {
				switch (retId) {
					case ReturnCode.REQ_RET_OK:
						handicapi.setText(Utils.getDoubleString(getActivity(), customer.handicapIndex));
						best.setText(Utils.getIntString(getActivity(), customer.best));
						heat.setText(String.valueOf(customer.heat));
						rank.setText(Utils.getCityRankString(getActivity(), customer.rank));
						act.setText(String.valueOf(customer.activity));
						matches.setText(String.valueOf(customer.matches));
						break;
					default:
						break;
				}
			}
		});
		reqLoader.requestData();
	}
	
	private void loadAvatar(String sn,String filename,final ImageView iv){
		Drawable avatar = AsyncImageLoader.getInstance().getAvatar(getActivity(), sn, filename,
				(int) getResources().getDimension(R.dimen.avatar_detail_size));
//		if(null != avatar) {
//			iv.setImageDrawable(avatar);
//		} else {
			iv.setImageResource(R.drawable.avatar_loading);
			AsyncImageLoader.getInstance().loadAvatar(getActivity(), sn, filename,
					new ImageCallback() {
						@Override
						public void imageLoaded(Drawable imageDrawable) {
							if(null != imageDrawable) {
								iv.setImageDrawable(imageDrawable);
							}
						}
				});
		//}
	}
	
	@Override
	public void onClick(View v) {
		Intent intent ;
		switch(v.getId()) {
			case R.id.cusinfo_logout:
				LoginActivity.backWithPhone(getActivity(), MainApp.getInstance().getCustomer().phone);
				ExitToLogin.getInstance().exitToLogin();
				JPushInterface.stopPush(getActivity());
				break;
			case R.id.cusinfo_myinfo_ll:
				intent = new Intent(getActivity(),ModifyInfoActivity.class);
				startActivityForResult(intent, Const.REQUST_CODE_MODIFY_MY_INFO);
				getActivity().overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;
			case R.id.cusinfo_msg_ll:
				intent = new Intent(getActivity(),SysMsgActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;
			case R.id.cusinfo_history_ll:
				intent = new Intent(getActivity(),InviteHistoryActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;
			case R.id.cusinfo_about_ll:
				intent = new Intent(getActivity(),AboutIgolfActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;
			case R.id.cusinfo_expand_btn:
				ModifySignatureActivity.startModifySignatureActivity(CustomerHomeFrg.this, customer.signature);
				getActivity().overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;
			case R.id.customer_album_add:
				ImageSelectActivity.startImageSelect(CustomerHomeFrg.this);
				break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Utils.logh(TAG, " ::: requestCode: " + requestCode + " resultCode: " + resultCode + " intent: " + intent);
		if(resultCode != Activity.RESULT_OK) {
			super.onActivityResult(requestCode, resultCode, intent);
			return ;
		}
		if(Const.REQUST_CODE_SIGNATURE_MY == requestCode) {
			String sign = customer.signature.trim();
			Utils.logh(TAG, "sign: " + sign);
			if(sign.length() > 0) {
				signatureTxtExpand.setText(sign);
			}else{
				signatureTxtExpand.setText(R.string.str_comm_def_signature);
			}
			return ;
		}
		if(Const.REQUST_CODE_MODIFY_MY_INFO == requestCode && null != intent){
		    boolean changedFlag = intent.getBooleanExtra(Const.BUNDLE_KEY_MY_INFO_CHANGED, false);
		    if(changedFlag){
		    	loadAvatar(customer.sn,customer.avatar,customerAvatar);
		    	signatureTxtExpand.setText(customer.signature);
		    	yearsExp.setText(customer.yearsExpStr);
			    if(Const.SEX_MALE == customer.sex) {
					sex.setImageResource(R.drawable.ic_male);
				} else {
					sex.setImageResource(R.drawable.ic_female);
				}
			    region.setText(gd.getRegionName(customer.city));
			    age.setText(String.format(getResources().getString(R.string.str_member_age_wrap), customer.age));
		    }
		}
		if(Const.REQUST_CODE_PHOTE_TAKE_PHOTO == requestCode) {
//			startPhotoCrop(mUri);
			try {
				addAlbum(new File(new URI(mUri.toString())));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			return ;

		} else if(Const.REQUST_CODE_PHOTE_GALLERY == requestCode) {
//			startPhotoCrop(intent.getData());
			Uri uri = intent.getData();
			String img_path = FileUtils.getMediaImagePath(getActivity(), uri);
			addAlbum(new File(img_path));
			return ;

		} else if(Const.REQUST_CODE_PHOTE_CROP == requestCode) {
			if(null != intent) {
				Bundle b = intent.getExtras();
				if(null != b) {
					Bitmap photo = b.getParcelable("data");
					if(null != photo) {
						doAddAlbum(photo,customer.sn);
						return ;
					} else {
						Log.w(TAG, "photo null ! ");
					}
				} else {
					Log.w(TAG, "intent.getExtras() null ! ");
				}
			}
		}
	}
	
	private void addAlbum(File file) {
		int uploadAlbumSize = getResources().getInteger(R.integer.upload_ablum_size);
		int angle = FileUtils.getExifOrientation(file.getAbsolutePath());
		Bitmap bitmap = FileUtils.getSmallBitmap(file.getAbsolutePath(), uploadAlbumSize, uploadAlbumSize);
		Utils.logh(TAG, "addAlbum size: " + uploadAlbumSize + " bitmap: " + bitmap.getWidth() + " x " + bitmap.getHeight() + " angle: " + angle
				+ "\n   " + file.getAbsolutePath());
		if(angle != 0) {
			bitmap = FileUtils.getExifBitmap(bitmap, angle);
			Utils.logh(TAG, "exif bitmap " + bitmap.getWidth() + " x " + bitmap.getHeight());
		}
		doAddAlbum(bitmap, customer.sn);
	}
	
	@Override
	public void onCameraTypeSelect() {
		mUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
				new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss", Locale.getDefault())
					.format(new Date(System.currentTimeMillis())) + ".jpg"));
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
		startActivityForResult(intent, Const.REQUST_CODE_PHOTE_TAKE_PHOTO);
	}

	@Override
	public void onGalleryTypeSelect() {
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(Media.EXTERNAL_CONTENT_URI, "image/*");
		startActivityForResult(intent, Const.REQUST_CODE_PHOTE_GALLERY);
	}
	
	private void doAddAlbum(final Bitmap bitmap, final String sn) {
		if(!Utils.isConnected(getActivity())){
			return;
		}

		WaitDialog.showWaitDialog(getActivity(), R.string.str_loading_add_album);
		new AsyncTask<Object, Object, Integer>() {
			UpdateAlbum request = new UpdateAlbum(getActivity(), bitmap, sn);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(BaseRequest.REQ_RET_OK == result) {
					addNewAlbum(request.getImageName());
				} else {
					Toast.makeText(getActivity(),
							request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
	private void addNewAlbum(String new_album) {
		Utils.logh(TAG, "addNewAlbum new_album: " + new_album + " old size: " + customer.album.size());
		customer.album.add(new_album);
		refreshAlbumAdd();
		LinearLayout ll = getAlbumLl(true);
		ll.addView(addAlbumImage(customer.album.size() - 1));
		albumLayout.addView(ll);
		Toast.makeText(getActivity(), R.string.str_toast_update_album_success, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onAvatarReset(String sn, String avatar, Bitmap bitmap) {
		Utils.logh(TAG, "onAvatarReset customer sn: " + customer.sn + " sn: " + sn + " avatar:  " + avatar);
		if(!customer.sn.equals(sn)) {
			Log.w(TAG, "onAvatarReset sn do not fit !!!");
			return ;
		}
		AsyncImageLoader.getInstance().clearOverDueCache(sn, 
				FileUtils.getAvatarPathBySn(getActivity(), sn, MainApp.getInstance().getCustomer().avatar));
		MainApp.getInstance().getCustomer().avatar = avatar;
		SharedPref.setString(SharedPref.SPK_AVATAR, avatar, getActivity());
		customerAvatar.setImageBitmap(bitmap);
	}

	@Override
	public void onAlbumDelete(String sn, String album) {
		Utils.logh(TAG, "onAlbumDelete sn: " + sn + " album:  " + album);
		if(!customer.sn.equals(sn)) {
			Log.w(TAG, "onAlbumDelete sn do not fit !!!");
			return ;
		}
		int pos = -1;
		for(String item : customer.album) {
			pos ++;
			if(item.equals(album)) {
				customer.album.remove(item);
				break;
			}
		}
		Utils.logh(TAG, "delete : " + pos);
		albumLayout.removeViewAt(pos);
		refreshAlbumAdd();
		// 异步删除
		AsyncImageLoader.getInstance().deleteAlbum(getActivity(), sn, album);
	}
	
	private void refreshAlbumAdd() {
		int len = getResources().getInteger(R.integer.album_max_length);
		int size = customer.album.size();
		if(size <= 0) {
			Utils.setVisibleGone(addAlbumView, addAlbumSpace);
		} else if(size < len) {
			Utils.setVisible(addAlbumView, addAlbumSpace);
		} else {
			Utils.setGone(addAlbumView, addAlbumSpace);
		}
	}
	
	public static void updateMsgAlert(int count) {
		Utils.logh(TAG, "updateMsgAlert count: " + count);
		if(count > 0) {
			Utils.setVisible(msgAlertIv);
		} else {
			Utils.setGone(msgAlertIv);
		}
	}
}
