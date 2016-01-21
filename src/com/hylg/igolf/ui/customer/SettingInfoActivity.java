package com.hylg.igolf.ui.customer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.MyFolloweInfo;
import com.hylg.igolf.cs.loader.AsyncImageLoader;
import com.hylg.igolf.cs.loader.GetMyFollowerListLoader;
import com.hylg.igolf.cs.loader.GetMyFollowerListLoader.GetMyFollowerListCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.FriendAttentionAdd;
import com.hylg.igolf.ui.account.AgreementActivityActivity;
import com.hylg.igolf.ui.account.FeedBackActivity;
import com.hylg.igolf.ui.account.LoginActivity;
import com.hylg.igolf.ui.view.EhecdListview;
import com.hylg.igolf.ui.view.EhecdListview.OnLoadMoreListener;
import com.hylg.igolf.ui.view.EhecdListview.OnRefreshListener;
import com.hylg.igolf.ui.widget.IgBaseAdapter;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.DownLoadImageTool;
import com.hylg.igolf.utils.ExitToLogin;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

import java.util.ArrayList;

import cn.jpush.android.api.JPushInterface;

public class SettingInfoActivity extends FragmentActivity implements  OnClickListener{
	
	private final String 				TAG 						= "MyFollowerActivity";

	private static String               SN_KEY                      = "mem_sn";
	
	private ImageButton  				mBack 						= null;

	private FragmentActivity 			mContext 					= null;

	private ImageView                   mAvatar                     = null;

	private RelativeLayout              mBaseInfoRelative           = null;

	private TextView                    mRebindPhone                = null,
										mResetPsw                	= null,
										mFeedBack                	= null,
										mAbout                		= null,
										mAgreement                	= null,
										mHotline                	= null,
										mExit                		= null;




	public static void startSettingInfoActivity(Activity context) {

		Intent intent = new Intent(context, SettingInfoActivity.class);

		context.startActivity(intent);
		context.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}
	
	public static void startSettingInfoActivity(Fragment context) {

		Intent intent = new Intent(context.getActivity(), SettingInfoActivity.class);
		context.startActivity(intent);
		context.getActivity().overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting_info_ac);
		
		mBack =  (ImageButton)  findViewById(R.id.setting_back);
		mBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mContext.finish();
			}
		});

		mAvatar = (ImageView) findViewById(R.id.setting_avatar_image);
		mBaseInfoRelative = (RelativeLayout)findViewById(R.id.setting_base_info_relative);

		mRebindPhone = (TextView) findViewById(R.id.setting_rebind_phone_content_text);
		mResetPsw = (TextView) findViewById(R.id.setting_rebset_psw_content_text);
		mFeedBack = (TextView) findViewById(R.id.setting_feedback_content_text);
		mAbout = (TextView) findViewById(R.id.setting_about_content_text);
		mAgreement = (TextView) findViewById(R.id.setting_agreement_content_text);
		mHotline  = (TextView) findViewById(R.id.setting_hotline_content_text);
		mExit  = (TextView) findViewById(R.id.setting_exit_text);

		mBaseInfoRelative.setOnClickListener(this);
		mRebindPhone.setOnClickListener(this);
		mResetPsw.setOnClickListener(this);
		mFeedBack.setOnClickListener(this);
		mAbout.setOnClickListener(this);
		mAgreement.setOnClickListener(this);
		mHotline.setOnClickListener(this);
		mExit.setOnClickListener(this);

		String phone = MainApp.getInstance().getCustomer().phone;

		int is_coach = MainApp.getInstance().getCustomer().is_coach;

		mRebindPhone.setText(Utils.getHintPhone(phone));


		/*
		* if the user is coach ,can not update selfinfo in setting ,just in coach apply page
		* */
		if (is_coach == 0 ) {

			findViewById(R.id.setting_base_info_relative).setVisibility(View.GONE);
		}
		
	}
	
	@Override
	protected void onResume () {

		String sn = MainApp.getInstance().getCustomer().sn;
		//DownLoadImageTool.getInstance(this).displayImage(Utils.getAvatarURLString(sn),mAvatar,null);

		Utils.loadAvatar(mContext,sn,mAvatar);
		super.onResume();
	}



	@Override
	public void onClick(View view) {

		switch (view.getId()) {

			case R.id.setting_base_info_relative :

				ModifyInfoActivity.startModifyInfoActivity(this);
				break;

			case R.id.setting_rebind_phone_content_text :

				RebindPhoneActivity.startRebindPhoneActivity(this);
				break;

			case R.id.setting_rebset_psw_content_text :

				ModifyPwdActivity.startModifyPwdActivity(this);
				break;

			case R.id.setting_feedback_content_text :

				//RebindPhoneActivity.startRebindPhoneActivity(this);
				FeedBackActivity.StartFeedBackActivity(this);
				break;

			case R.id.setting_about_content_text :

				AboutIgolfActivity.startAboutIgolfActivity(this);
				break;

			case R.id.setting_agreement_content_text :

				AgreementActivityActivity.StartAgreementActivityActivity(this);
				break;

			case R.id.setting_hotline_content_text :

				Intent data = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + getResources().getString(R.string.str_customer_service_phone)));
				startActivity(data);
				break;

			case R.id.setting_exit_text:

				LoginActivity.backWithPhone(mContext, MainApp.getInstance().getCustomer().phone);
				ExitToLogin.getInstance().exitToLogin();
				JPushInterface.stopPush(mContext);

				MainApp.getInstance().getGlobalData().setBalance(0.0);
				MainApp.getInstance().getGlobalData().setCardNo("");
				MainApp.getInstance().getGlobalData().setBankName("");

				this.finish();

				break;


		}

	}
}
