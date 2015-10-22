package com.hylg.igolf.jpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import cn.jpush.android.api.JPushInterface;

import com.hylg.igolf.ui.MainActivity;
import com.hylg.igolf.ui.SplashActivity;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.SharedPref;
import com.hylg.igolf.utils.Utils;

public class JpushReceiver extends BroadcastReceiver {
	private static final String TAG = "JPush";

	@Override
	public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
		Utils.logh(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
		
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Utils.logh(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...
                        
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        	Utils.logh(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
        	processCustomMessage(context, bundle);
        
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Utils.logh(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Utils.logh(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
            context.sendBroadcast(new Intent(Const.IG_ACTION_NEW_ALERTS));
            context.sendBroadcast(new Intent(Const.IG_ACTION_MY_INVITE_JPUSH_NOTIFY));
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Utils.logh(TAG, "[MyReceiver] 用户点击打开了通知");
            clickBundle = bundle;
            // 在前台，发送切换到大厅页面广播
            if(MainActivity.isMainForeground()) {
            	context.sendBroadcast(new Intent(Const.IG_ACTION_FORE_JPUSH_NOTIFY).putExtras(bundle));
            	return ;
            }
            // 首先检测是否被强制关闭过，以启动MainActivity，或SplashActivity
            int ppid = SharedPref.getInt(SharedPref.PREFS_KEY_PID, context);
            final int pid = Process.myPid();
            Utils.logh(TAG, "ppid: " + ppid + " cur pid: " + pid);
            if(0 != ppid && ppid != pid) {
            	SplashActivity.startSplashActivityFromReceiver(context, bundle);
            } else {
            	MainActivity.startMainActivityFromReceiver(context, bundle);
//            	context.sendBroadcast(new Intent(Const.IG_ACTION_BACK_JPUSH_NOTIFY).putExtras(bundle));
            }
        	
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Utils.logh(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
        	
        } else {
        	Utils.logh(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
	}
	
	private static Bundle clickBundle = null;
	public static Bundle clickBundle() {
		return clickBundle;
	}
	public static void clearClickBundle() {
		clickBundle = null;
	}
	
	//send msg to MainActivity
	private void processCustomMessage(Context context, Bundle bundle) {
//		if (MainActivity.isForeground) {
//			String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
//			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
//			Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
//			msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
//			if (!ExampleUtil.isEmpty(extras)) {
//				try {
//					JSONObject extraJson = new JSONObject(extras);
//					if (null != extraJson && extraJson.length() > 0) {
//						msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
//					}
//				} catch (Exception e) {
//
//				}
//
//			}
//			context.sendBroadcast(msgIntent);
//		}
	}
	
	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}
}
