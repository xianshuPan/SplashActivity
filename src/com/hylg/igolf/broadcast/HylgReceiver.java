package com.hylg.igolf.broadcast;

import com.hylg.igolf.utils.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

public class HylgReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
//		Bundle bundle = intent.getExtras();
		Utils.logh("HylgReceiver", ">>>>>>>>>>>>> action: " + action);
		if(ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
			Utils.ConnectionCheck(context);
		}
	}

}
