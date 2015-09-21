package com.hylg.igolf.wxapi;

import javax.xml.transform.Source;

import com.hylg.igolf.DebugTools;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * show the pay result satisfy the micro message pay ,
 * 
 * the class name must be "WXPayEntryActivity"
 * 
 * and the class must be in the "package name.wxapi" package
 * 
 * else can not response the pay result
 * 
 * */

public class WXPayEntryActivity extends Activity implements 
															IWXAPIEventHandler
															{
	
	private static final String 			TAG 				= "WXPayEntryActivity";
	
	private final int                       UsageRecord         = 1,
											ChargeRecord		= 2,
											ToCashRecord        = 3;
    
    /*ͷ���ķ���,��ҳ,����*/
	//private LinearLayout 					mBackLinear			= null, 
											//mHomeLinear			= null;
	private TextView						//mTitleText          = null,
			
											/*order success show*/
											mOrderMoneyTxt      = null,
											mOrderNoTxt      	= null,
											mOrderListTxt      	= null,
											mOrderPersonalTxt   = null,
											
											/*charge success show*/
											mChargeMoneyTxt		= null,
											mChargeNoTxt		= null,
											mChargeContinuTxt	= null,
											
											/*order pay or charge pay fail show*/
											mFailTileTxt        = null,
											mFailNoTxt          = null,
											mFailContinuTxt     = null;
	
    /*query the order state result */
	private RelativeLayout                  mOrderSuccessRelative   = null,
											mChargeSuccessRelative  = null,
											mFailRelative       	= null;
	
	/*progress bar*/
	//private ProgressWheel 					mProgress			= null;
	
	/*tencent pay api*/
	private IWXAPI 							api					= null;
	
	
	private String 							strMoney            = "",
											mSource             = "",
											mOrderID 			= "",
											mOrderNo 			= "",
											mAmount 			= "";	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setContentView(R.layout.pay_result);
        
       // initUI();
        
    }
 

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		boolean result = api.handleIntent(getIntent(), this);
		DebugTools.getDebug().debug_v(TAG, "onNewIntent_handleIntent------->>>"+result);
	}
	
	@Override
	public void onRestart() {
		DebugTools.getDebug().debug_v(TAG, "onRestart..");
		super.onRestart();
	}
	
	@Override
	public void onStart() {
		DebugTools.getDebug().debug_v(TAG, "onStart..");
		super.onStart();
	}
	
	@Override
	public void onResume() {
		DebugTools.getDebug().debug_v(TAG, "onResume..");
		//handler.getInstance().setHandleEvent(this);
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		DebugTools.getDebug().debug_v(TAG, "onPause..");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//handler.getInstance().deleteHandleEvent(this);
		DebugTools.getDebug().debug_v(TAG, "onDestroy..");
	}

	
	@Override
	public void onReq(BaseReq req) {
		
		
	}

	@Override
	public void onResp(BaseResp resp) {

		
	}


}