package com.hylg.igolf.cs.request;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;

import org.json.JSONArray;

import org.json.JSONObject;

import android.content.Context;
import android.os.Environment;
import android.os.Process;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.cs.data.Customer;
import com.hylg.igolf.utils.Base64;
import com.hylg.igolf.utils.GlobalData;
import com.hylg.igolf.utils.SharedPref;


public class LoginUser extends BaseRequest {
	private String param;

	private Context mContext;

	public LoginUser(Context context, String phone, String password) {
		super(context);

		mContext = context;
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_PHONE); s.append(KV_CONN); s.append(phone);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PWD); s.append(KV_CONN); s.append(Base64.encode(password.getBytes()));
		param = s.toString();
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("loginUser", param);
	}

	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);

			DebugTools.getDebug().debug_v("login", "login----->>>>"+jo);

			int rn = jo.optInt(RET_NUM, REQ_RET_FAIL);
			if(REQ_RET_OK != rn) {
				failMsg = jo.getString(RET_MSG);
				return rn;
			}

			GlobalData gd = MainApp.getInstance().getGlobalData();
			gd.msgNumSys = jo.getInt(RET_MSG_NUM_SYS);
			gd.msgNumInvite = jo.getInt(RET_MSG_NUM_INVITE);

			Customer customer = MainApp.getInstance().getCustomer();
			JSONObject cusObj = new JSONObject(jo.getString(RET_CUSTOMER));
			customer.id = cusObj.getLong(RET_ID);
			customer.is_coach = jo.getInt("isCoach");
			customer.sn = cusObj.getString(RET_SN);
			customer.nickname = cusObj.getString(RET_NICKNAME);
			customer.avatar = cusObj.getString(RET_AVATAR);
			customer.phone = cusObj.getString(RET_PHONE);
			customer.addressName = cusObj.optString(RET_ADDRESS_NAME);
			customer.sex = cusObj.getInt(RET_SEX);
			customer.yearsExpStr = cusObj.getString(RET_YEAR_EXP_STR);
			customer.state = cusObj.getString(RET_STATE);
			customer.city = cusObj.getString(RET_CITY);
			customer.industry = cusObj.getString(RET_INDUSTRY);
			customer.signature = cusObj.optString(RET_SIGNATURE,"");
			customer.rate = cusObj.getDouble(RET_RATE);
			customer.ratedCount = cusObj.getInt(RET_RATE_COUNT);
			customer.heat = cusObj.getInt(RET_HEAT);
			customer.activity = cusObj.getInt(RET_ACTIVITY);
			customer.rank = cusObj.optInt(RET_RANK,Integer.MAX_VALUE);
			customer.matches = cusObj.getInt(RET_MATCHES);
			customer.winNum = cusObj.getInt(RET_WIN_NUM);
			customer.handicapIndex = cusObj.optDouble(RET_HANDICAP_INDEX, Double.MAX_VALUE);
			customer.best = cusObj.optInt(RET_BEST, Integer.MAX_VALUE);
//			customer.album = ;
			JSONArray albums = cusObj.getJSONArray(RET_ALBUM);
			customer.album.clear();
			for(int i=0, len=albums.length(); i<len; i++) {
				customer.album.add(albums.getString(i));
			}
			customer.age = cusObj.getInt(RET_AGE_STR);
			
			// 登录成功，保存pid。推送启动时，检测是否被强制关闭过

			File file = mContext.getFilesDir();

			String path = file.getAbsolutePath()+File.separator+"user.txt";

			File file2 = new File(path);

			if (!file2.exists()) {

				file2.createNewFile();
			}

			FileOutputStream fos = new FileOutputStream(file2);

			ObjectOutputStream dos = new ObjectOutputStream(fos);

			dos.writeObject(customer);

			SharedPref.setInt(SharedPref.PREFS_KEY_PID, Process.myPid(), context);
			
		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
