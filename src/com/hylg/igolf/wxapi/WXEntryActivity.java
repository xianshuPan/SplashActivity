/*
 * 官网地站:http://www.mob.com
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2013年 mob.com. All rights reserved.
 */

package com.hylg.igolf.wxapi;

import com.hylg.igolf.DebugTools;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

/** 微信客户端回调activity示例 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
	
	public final String 					TAG 				= "WXEntryActivity";

	private IWXAPI 							api					= null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Tools.getInstance().setActivityNoTitle(this);
		DebugTools.getDebug().debug_v(TAG, "onCreate..");
		api = WXAPIFactory.createWXAPI(this, "wx14da9bc6378845fe");
		
		boolean result = api.handleIntent(getIntent(), this);
		DebugTools.getDebug().debug_v(TAG, "onreat_handleIntent------->>>"+result);
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//setContentView(R.layout.order_pay);
		//initUI();
		
	}
	
	@Override
	protected void onNewIntent (Intent intent) {
		
		DebugTools.getDebug().debug_v(TAG, "onNewIntent..");
		setIntent(intent);
		boolean result = api.handleIntent(getIntent(), this);
		DebugTools.getDebug().debug_v(TAG, "onNewIntent_handleIntent------->>>"+result);
		super.onNewIntent(intent);
	}

	@Override
	public void onReq(BaseReq arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResp(BaseResp arg0) {
		// TODO Auto-generated method stub
		
		DebugTools.getDebug().debug_v("微信返回结果", "----->>>"+arg0);
		
		this.finish();
	}

}
