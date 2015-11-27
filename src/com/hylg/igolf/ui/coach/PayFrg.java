package com.hylg.igolf.ui.coach;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hylg.igolf.R;
import com.hylg.igolf.utils.Const;

import android.os.Handler;

public class PayFrg extends Fragment implements View.OnClickListener {

	private static final String 					TAG = "PayFrg";


	private TextView                                mAmountTxt,mPayTxt;

	private ImageButton                             mBack;

	private RelativeLayout                          mWeChatRelative,mAlipayRelative,mUnionPayRelative;
	private ImageView                               mWeChatImage,mAlipayImage,mUnionPayImage;

	private String                                  mSelectPayTypeStr = "alipay";

	private Handler                                 mHandler=null;

	private double                                  mAmount = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	public PayFrg(Handler handler,double amount) {

		mHandler = handler;

		mAmount = amount;

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.pay_menu, container, false);

		mBack = (ImageButton) view.findViewById(R.id.pay_back);
		mPayTxt = (TextView) view.findViewById(R.id.pay_text);
		mAmountTxt = (TextView) view.findViewById(R.id.pay_amount_text);

		mWeChatRelative = (RelativeLayout) view.findViewById(R.id.pay_wechat_relative);
		mAlipayRelative = (RelativeLayout) view.findViewById(R.id.pay_alipay_relative);
		mUnionPayRelative = (RelativeLayout) view.findViewById(R.id.pay_union_relative);
		mWeChatImage = (ImageView)view.findViewById(R.id.pay_wechat_select_image);
		mAlipayImage = (ImageView)view.findViewById(R.id.pay_alipay_select_image);
		mUnionPayImage = (ImageView)view.findViewById(R.id.pay_union_select_image);

		mPayTxt.setOnClickListener(this);
		mWeChatRelative.setOnClickListener(this);
		mAlipayRelative.setOnClickListener(this);
		mUnionPayRelative.setOnClickListener(this);
		mBack.setOnClickListener(this);

		if (mAmount >= 0) {

			mAmountTxt.setText(String.valueOf(mAmount));
		}

		return view;
	}
	
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		
		// 注册监听
		IntentFilter filter = new IntentFilter();
		filter.addAction(Const.IG_ACTION_MY_INVITE_JPUSH_NOTIFY);
	}
	
	
	@Override
	public void onResume() {
	
		super.onResume();
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();


	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {

			case R.id.pay_back :

					getActivity().getSupportFragmentManager().popBackStack();

				break;

			case R.id.pay_text:

				if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {

					getActivity().getSupportFragmentManager().popBackStack();
				}

				if (mHandler != null) {

					Message msg = mHandler.obtainMessage();

					msg.what = Const.PAY_TYPE_SELECT;
					Bundle data = new Bundle();
					data.putString("Type", mSelectPayTypeStr);
					msg.setData(data);
					mHandler.sendMessage(msg);
				}

				break;

			case R.id.pay_alipay_relative :

				mSelectPayTypeStr = "alipay";
				mAlipayImage.setImageResource(R.drawable.selected);
				mWeChatImage.setImageResource(R.drawable.select);
				mUnionPayImage.setImageResource(R.drawable.select);

				break;

			case R.id.pay_wechat_relative :

				mSelectPayTypeStr = "wx";
				mAlipayImage.setImageResource(R.drawable.select);
				mWeChatImage.setImageResource(R.drawable.selected);
				mUnionPayImage.setImageResource(R.drawable.select);

				break;

			case R.id.pay_union_relative :

				mSelectPayTypeStr = "upacp";
				mAlipayImage.setImageResource(R.drawable.select);
				mWeChatImage.setImageResource(R.drawable.select);
				mUnionPayImage.setImageResource(R.drawable.selected);

				break;

		}
	}

//	@Override
//	public void onAttach(Activity activity) {
//		super.onAttach(activity);
//		// 注册监听
//		IntentFilter filter = new IntentFilter();
//		filter.addAction(Const.IG_ACTION_MY_INVITE_JPUSH_NOTIFY);
//		activity.registerReceiver(mReceiver, filter);
//	}
//
//	@Override
//	public void onDetach() {
//		getActivity().unregisterReceiver(mReceiver);
//		super.onDetach();
//	}

}
