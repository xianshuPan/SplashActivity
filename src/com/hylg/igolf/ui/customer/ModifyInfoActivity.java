package com.hylg.igolf.ui.customer;

import java.io.File;
import java.text.SimpleDateFormat;
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
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.Customer;
import com.hylg.igolf.cs.loader.AsyncImageLoader;
import com.hylg.igolf.cs.loader.AsyncImageLoader.ImageCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.ModifyCustomer;
import com.hylg.igolf.cs.request.RequestParam;
import com.hylg.igolf.cs.request.UpdateAvatar;
import com.hylg.igolf.ui.common.AgeSelectActivity;
import com.hylg.igolf.ui.common.AgeSelectActivity.onAgeSelectListener;
import com.hylg.igolf.ui.common.ImageSelectActivity;
import com.hylg.igolf.ui.common.ImageSelectActivity.onImageSelectListener;
import com.hylg.igolf.ui.common.IndustrySelectActivity;
import com.hylg.igolf.ui.common.IndustrySelectActivity.onIndustrySelectListener;
import com.hylg.igolf.ui.common.RegionSelectActivity;
import com.hylg.igolf.ui.common.RegionSelectActivity.onRegionSelectListener;
import com.hylg.igolf.ui.common.SexSelectActivity;
import com.hylg.igolf.ui.common.SexSelectActivity.onSexSelectListener;
import com.hylg.igolf.ui.common.YearsExpSelectActivity;
import com.hylg.igolf.ui.common.YearsExpSelectActivity.onYearsExpSelectListener;
import com.hylg.igolf.ui.reqparam.ModifyCustomerReqParam;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.ExitToLogin;
import com.hylg.igolf.utils.FileUtils;
import com.hylg.igolf.utils.GlobalData;
import com.hylg.igolf.utils.SharedPref;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

public class ModifyInfoActivity extends FragmentActivity implements OnClickListener,onSexSelectListener,
											onRegionSelectListener,onIndustrySelectListener,
											onAgeSelectListener,onYearsExpSelectListener, 
											onImageSelectListener{
	private static final String TAG = "ModifyInfoActivity";
	private TextView phoneView,ageView,sexView,yearView,regionView,industryView,signatureView;
	private ModifyCustomerReqParam reqParam;
	private GlobalData goGlobalData;
	private ImageView avatarView;
	private Uri mUri;
	private Bitmap avatarBmp = null;
	private Customer customer;
	private boolean changedFlag = false;

	public static void startModifyInfoActivity(Activity context) {

		Intent intent = new Intent(context, ModifyInfoActivity.class);
		context.startActivity(intent);
		context.overridePendingTransition(R.anim.ac_slide_right_in,R.anim.ac_slide_left_out);
	}
	
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitToLogin.getInstance().addActivity(this);
		setContentView(R.layout.customer_ac_modify_info);
		getViews();
		initMyInfoData();
	}
	
//	public static void startSysMsgDetailActivity(Context context, SysMsgInfo sysMsgInfo) {
//		Intent intent = new Intent(context, SysMsgDetailActivity.class);
//		
//		context.startActivity(intent);
//	}
	
	private void getViews(){
		phoneView = (TextView) findViewById(R.id.customer_my_info_phone_selection);
		yearView = (TextView) findViewById(R.id.customer_my_info_yearsexp_selection);
		sexView = (TextView) findViewById(R.id.customer_my_info_sex_selection);
		ageView = (TextView) findViewById(R.id.customer_my_info_age_selection);
		regionView = (TextView) findViewById(R.id.customer_my_info_region_selection);
		industryView = (TextView) findViewById(R.id.customer_my_info_industry_selection);
		signatureView = (TextView) findViewById(R.id.customer_my_info_signature_selection);
		avatarView = (ImageView) findViewById(R.id.customer_my_info_avtar);
		findViewById(R.id.customer_my_info_phone_ll).setOnClickListener(this);
		findViewById(R.id.customer_my_info_password_ll).setOnClickListener(this);
		findViewById(R.id.customer_my_info_sex_ll).setOnClickListener(this);
		findViewById(R.id.customer_my_info_yearsexp_ll).setOnClickListener(this);
		findViewById(R.id.customer_my_info_region_ll).setOnClickListener(this);
		findViewById(R.id.customer_my_info_industry_ll).setOnClickListener(this);
		findViewById(R.id.customer_my_info_age_ll).setOnClickListener(this);
		findViewById(R.id.customer_my_info_topbar_back).setOnClickListener(this);
		findViewById(R.id.customer_my_info_signature_ll).setOnClickListener(this);
		findViewById(R.id.customer_my_info_avtar_ll).setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.customer_my_info_topbar_back:
				finishWithAnim();
				break;
			case R.id.customer_my_info_sex_ll:
				SexSelectActivity.startSexSelect(this, false, reqParam.sex);
				break;
			case R.id.customer_my_info_yearsexp_ll:
				YearsExpSelectActivity.startYearsExpSelect(this, reqParam.yearsExp);
				break;
			case R.id.customer_my_info_region_ll:
				RegionSelectActivity.startRegionSelect(this, RegionSelectActivity.REGION_TYPE_SET_INFO, reqParam.city);
				break;
			case R.id.customer_my_info_industry_ll:
				IndustrySelectActivity.startIndustrySelect(this, false, reqParam.industry);
				break;
			case R.id.customer_my_info_age_ll:
				AgeSelectActivity.startAgeSelect(this, reqParam.age);
				break;
			case R.id.customer_my_info_phone_ll:
				VerifyPwdActivity.startVerifyPwdActivity(this);
				overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;
			case R.id.customer_my_info_password_ll:
				ModifyPwdActivity.startModifyPwdActivity(this);
				overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;
			case R.id.customer_my_info_signature_ll:
				ModifySignatureActivity.startModifySignatureActivity(this,customer.signature);
				overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;
			case R.id.customer_my_info_avtar_ll:
				ImageSelectActivity.startImageSelect(this);
				break;
		}
	}
	
	
	private void initMyInfoData() {
		customer = MainApp.getInstance().getCustomer();
		loadAvatar(customer.sn,customer.avatar);
		goGlobalData = MainApp.getInstance().getGlobalData();
		sexView.setText(goGlobalData.getSexName(customer.sex));
		ageView.setText(String.format(getString(R.string.str_account_age_wrap), customer.age));
		phoneView.setText(customer.phone);
		regionView.setText(goGlobalData.getRegionName(customer.city));
		String sign = customer.signature.trim();
		if(sign.length() > 0) {
			
			signatureView.setText(sign);
			
		}else{
			
			signatureView.setText(R.string.str_comm_def_signature);
		}
		industryView.setText(goGlobalData.getIndustryName(customer.industry));
		yearView.setText(String.format(getString(R.string.str_account_yearsexp_wrap), Integer.parseInt(customer.yearsExpStr)));
		reqParam = new ModifyCustomerReqParam();
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
	
	
	private void finishWithAnim() {
		if(changedFlag) {
			Intent intent = new Intent();
			intent.putExtra(Const.BUNDLE_KEY_MY_INFO_CHANGED, true);
			setResult(RESULT_OK, intent);
		}
		finish();
		overridePendingTransition(R.anim.ac_slide_left_in, R.anim.ac_slide_right_out);
	}
	
	protected void toastShort(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
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

	@Override
	public void onYearsExpSelect(int newYearsExp) {
		doModifyCustomerInfo(RequestParam.PARAM_REQ_YEARSEXP_STR,String.valueOf(newYearsExp),
				yearView,String.format(getString(R.string.str_account_yearsexp_wrap), newYearsExp));
		customer.yearsExpStr = String.valueOf(newYearsExp);
	}

	@Override
	public void onAgeSelect(int newAge) {
		doModifyCustomerInfo(RequestParam.PARAM_REQ_AGE,String.valueOf(newAge),
				ageView,String.format(getString(R.string.str_account_age_wrap), newAge));
		customer.age = newAge;
	}

	@Override
	public void onIndustrySelect(String newIndustry) {
		doModifyCustomerInfo(RequestParam.PARAM_REQ_INDUSTRY,newIndustry,
				industryView,goGlobalData.getIndustryName(newIndustry));
		customer.industry = newIndustry;
	}

	@Override
	public void onRegionSelect(String newRegion) {
		doModifyCustomerInfo(RequestParam.PARAM_REQ_CITY,newRegion,
				regionView,goGlobalData.getRegionName(newRegion));
		customer.city = newRegion;
	}

	@Override
	public void onSexSelect(int newSex) {
		doModifyCustomerInfo(RequestParam.PARAM_REQ_SEX,String.valueOf(newSex),
				sexView,goGlobalData.getSexName(newSex));
		customer.sex = newSex;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Utils.logh(TAG, " ::: requestCode: " + requestCode + " resultCode: " + resultCode + " intent: " + intent);
		if(Const.REQUST_CODE_PHOTE_TAKE_PHOTO == requestCode) {
			if(RESULT_OK == resultCode) {
				startPhotoCrop(mUri);
				return ;
			}
		} else if(Const.REQUST_CODE_PHOTE_GALLERY == requestCode) {
			if(RESULT_OK == resultCode && null != intent) {
				startPhotoCrop(intent.getData());
				return ;
			}
		} else if(Const.REQUST_CODE_PHOTE_CROP == requestCode) {
			if(RESULT_OK == resultCode && null != intent) {
				Bundle b = intent.getExtras();
				if(null != b) {
					Bitmap photo = b.getParcelable("data");
					if(null != photo) {
						avatarBmp = photo;
						doModifyAvatar(avatarBmp, customer.sn);
						return ;
					} else {
						Log.w(TAG, "photo null ! ");
					}
				} else {
					Log.w(TAG, "intent.getExtras() null ! ");
				}
			}
		}else if(Const.REQUST_CODE_SIGNATURE_MY == requestCode){
			String sign = customer.signature.trim();
			if(sign.length() > 0) {
				signatureView.setText(sign);
			}else{
				signatureView.setText(R.string.str_comm_def_signature);
			}
		    changedFlag = true;
		}
		super.onActivityResult(requestCode, resultCode, intent);
	}
	
	private void doModifyCustomerInfo(final String key,final String value,final TextView textView,final String textValue){
		WaitDialog.showWaitDialog(this,
				R.string.str_loading_modify_msg);
		if(!Utils.isConnected(this)){
			return;
		}
		new AsyncTask<Object, Object, Integer>() {
			ModifyCustomer request = new ModifyCustomer(ModifyInfoActivity.this, customer.sn, key, value);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(BaseRequest.REQ_RET_OK == result) {
					textView.setText(textValue);
					changedFlag = true;
				} else {
					Toast.makeText(ModifyInfoActivity.this,request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
	private void startPhotoCrop(Uri uri) {
		int width = getResources().getInteger(R.integer.upload_avatar_size);
		int height = getResources().getInteger(R.integer.upload_avatar_size);

		Utils.logh(TAG, "startPhotoCrop width: " + width + " height: " + height);
		
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", width);
		intent.putExtra("aspectY", height);
		intent.putExtra("outputX", width);
		intent.putExtra("outputY", height);
		intent.putExtra("return-data", true);

		startActivityForResult(intent, Const.REQUST_CODE_PHOTE_CROP);
	}
	
	private void doModifyAvatar(final Bitmap bitmap, final String sn) {
		if(!Utils.isConnected(this)){
			return;
		}
		WaitDialog.showWaitDialog(this,
				R.string.str_loading_modify_msg);
		new AsyncTask<Object, Object, Integer>() {
			UpdateAvatar request = new UpdateAvatar(ModifyInfoActivity.this, bitmap, sn);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(BaseRequest.REQ_RET_OK == result) {
					AsyncImageLoader.getInstance().clearOverDueCache(sn, 
							FileUtils.getAvatarPathBySn(ModifyInfoActivity.this, sn, customer.avatar));
//					loadNewAvatar(sn);
					String avatar = request.getImageName();
					customer.avatar = avatar;
					SharedPref.setString(SharedPref.SPK_AVATAR, avatar, ModifyInfoActivity.this);
//					loadAvatar(customer.sn, avatar);//修改成功,重新加载头像,同步客户端内存图片
					avatarView.setImageBitmap(bitmap);
					changedFlag = true;
				} else {
					Toast.makeText(ModifyInfoActivity.this,
							request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(KeyEvent.KEYCODE_BACK == keyCode) {
			finishWithAnim();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
