package com.hylg.igolf.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.protocol.HTTP;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.ui.friend.publish.GlobalContext;

import cn.gl.lib.utils.BaseUtils;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;


public class Utils extends BaseUtils {

	public static boolean TEST_MODE = true;
	
	static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * note net status
	 */
	private static boolean connection;
	public static boolean isConnected(Context ctx) {
		if(!connection) {
			Toast.makeText(ctx, R.string.str_toast_disconnect, Toast.LENGTH_SHORT).show();
		}
		return connection;
	}
    public static boolean ConnectionCheck(Context ctx) {
    	connection = false;
    	ConnectivityManager conMan = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
    	if(null == conMan) {
    		Log.w("", "xxx get ConnectivityManager fail xxx ");
    		return false;
    	}
    	NetworkInfo anwi = conMan.getActiveNetworkInfo();
    	if(null == anwi) {
    		Log.w("", "xxx NetworkInfo fail xxx ");
    		return false;
    	}
    	if(!anwi.isConnected()) {
    		Log.w("", "xxx NetworkInfo isConnected fail xxx ");
    		return false;
    	}
    	connection = true;
    	logh("", "ConnectionCheck: connection true");
    	return true;
    }

	public enum NetWorkType {
		none, mobile, wifi
	}
	public static NetWorkType getNetworkType() {

		ConnectivityManager connMgr = (ConnectivityManager) MainApp.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		if (networkInfo != null) {
			switch (networkInfo.getType()) {
				case ConnectivityManager.TYPE_MOBILE:
					return NetWorkType.mobile;
				case ConnectivityManager.TYPE_WIFI:
					return NetWorkType.wifi;
			}
		}

		return NetWorkType.none;
	}

	public static void setLevel(Context context, LinearLayout ll, int size, double level) {
		int lev = (int) (level * 10);
		int on = lev / 10;
		boolean half = lev % 10 > 0;
		int off = on;
		ll.removeAllViews();
		ImageView star;
		for(int i=0; i<on; i++) {
			star = new ImageView(context);
			star.setScaleType(ScaleType.FIT_CENTER);
			star.setImageResource(R.drawable.icon_star_on);
			star.setLayoutParams(new LayoutParams(size, size));
			ll.addView(star);
		}
		if(half) {
			star = new ImageView(context);
			star.setScaleType(ScaleType.FIT_CENTER);
			star.setImageResource(R.drawable.icon_star_half);
			star.setLayoutParams(new LayoutParams(size, size));
			ll.addView(star);
			off ++;
		}
		for(int j=off; j<5; j++) {
			star = new ImageView(context);
			star.setScaleType(ScaleType.FIT_CENTER);
			star.setImageResource(R.drawable.icon_star_off);
			star.setLayoutParams(new LayoutParams(size, size));
			ll.addView(star);				
		}
		setVisible(ll);
	}
	
	public static String getDoubleString(Context context, double value) {
		if(value == Double.MAX_VALUE) {
			return context.getString(R.string.str_no_value);
		}
		if(value < 0) {
			return String.format(context.getString(R.string.str_negative_handicap), String.valueOf(value));
		}
			return String.format(context.getString(R.string.str_positive_handicap), String.valueOf(value));
	}
	
	public static String getIntString(Context context, int value) {
		if(value == Integer.MAX_VALUE) {
			return context.getString(R.string.str_no_value);
		}
		if(value < 0) {
			return String.format(context.getString(R.string.str_negative_handicap), String.valueOf(value));
		}
		return String.format(context.getString(R.string.str_positive_handicap), String.valueOf(value));
	}
	
	public static String getCityRankString(Context context, int value) {
		if(value == Integer.MAX_VALUE) {
			return context.getString(R.string.str_no_value);
		}
		return String.valueOf(value);
	}

	public static String getAvatarURLString(String sn) {
		if(sn == null || sn.length() <= 0 ) {
			return "";
		}
		return BaseRequest.SERVER_IP+"/gams/person/"+sn+"/avatar/original/"+sn+".jpg";
	}
	
	/* 根据输入的时间显示是好久
	 * 刚刚 、几分钟以前、 几天以前
	 * */
	public static String getDateString(Context context, String value) {
		String result = "";
		if(value == null || value.length() <= 0) {
			return result;
		}
		
		Calendar now = Calendar.getInstance();
		Calendar input = Calendar.getInstance();
		
		long asdf = Long.valueOf(value);
		
		input.setTime(new Date(asdf));
		
		int nowMINUTE = now.get(Calendar.MINUTE);
		
		int nowHOUR = now.get(Calendar.HOUR);
		if (now.get(Calendar.AM_PM) == Calendar.PM) {
			
			nowHOUR = nowHOUR+ 12;
		}
		
		int nowDATE = now.get(Calendar.DATE);
		int nowMONTH = now.get(Calendar.MONTH)+1;
		int nowYEAR = now.get(Calendar.YEAR);
		
		int inputMINUTE = input.get(Calendar.MINUTE);
		int inputHOUR = input.get(Calendar.HOUR);
		
		if (input.get(Calendar.AM_PM) == Calendar.PM) {
			
			inputHOUR = inputHOUR+12;
		}
		
		int inputDATE = input.get(Calendar.DATE);
		int inputMONTH = input.get(Calendar.MONTH)+1;
		int inputYEAR = input.get(Calendar.YEAR);
		
		DebugTools.getDebug().debug_v("release_time", "----->>>"+inputYEAR+"年"+inputMONTH+"月"+inputDATE+"日"+inputHOUR+"时"+inputMINUTE+"分");
		
		
		if (nowMINUTE - inputMINUTE <= 1 && 
			nowHOUR - inputHOUR <= 0 &&
			nowDATE - inputDATE <= 0 &&
			nowMONTH - inputMONTH <= 0 &&
			nowYEAR - inputYEAR <= 0) {
			
			result = "刚刚";
		}
		
		if (nowMINUTE - inputMINUTE > 1 && 
			nowHOUR - inputHOUR <= 0 &&
			nowDATE - inputDATE <= 0 &&
			nowMONTH - inputMONTH <= 0 &&
			nowYEAR - inputYEAR <= 0) {
				
			result = (nowMINUTE - inputMINUTE)+"分钟以前";
		}
		
		if (nowHOUR - inputHOUR  > 1 &&
			nowDATE - inputDATE <= 0 &&
			nowMONTH - inputMONTH <= 0 &&
			nowYEAR - inputYEAR <= 0) {
					
			result = (nowHOUR - inputHOUR)+"小时以前";
		}
		
		if (nowDATE - inputDATE >1 &&
			nowMONTH - inputMONTH<= 0 &&
			nowYEAR - inputYEAR <= 0) {
						
			result = (nowDATE - inputDATE)+"天以前";
		}
		if (nowMONTH - inputMONTH > 1 &&
			nowYEAR - inputYEAR <= 0) {
							
			result = (nowMONTH - inputMONTH)+"月以前";
		}
		
		if (nowYEAR - inputYEAR >= 1) {
							
			int re = (nowYEAR-inputYEAR);
			
			DebugTools.getDebug().debug_v("tag", "__________>>>>>>>>>"+re);
			result = re +"年以前";
		}
		
		return result;
		
	}
	
	/**
	 * 处理时间
	 *
	 * @return
	 */
	public static String handTime(long time) {
		if (time <= 0) {
			return "";
		}
		try {
			//Date date = format.parse(time);
			long tm = System.currentTimeMillis();// 当前时间戳
			long d = (tm - time) / 1000;// 时间差距 单位秒
			
			
			if ((d / (60 * 60 * 24 * 30 *12)) > 0) {
				
				return d / (60 * 60 * 24 * 30 * 12) + "年前";
				
			} else if ((d / (60 * 60 * 24 * 30)) > 0) {
				
				return d / (60 * 60 * 24 * 30) + "个月前";
				
			} else if ((d / (60 * 60 * 24)) > 0) {
				
				return d / (60 * 60 * 24) + "天前";
				
			} else if ((d / (60 * 60)) > 0) {
				
				return d / (60 * 60) + "小时前";
				
			} else if ((d / 60) > 0) {
				
				return d / 60 + "分钟前";
				
			} else {
				// return d + "秒前";
				return "刚刚";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	
	public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return  dp * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp){
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
    
    /*
     * 把长整型的时间，转换成字符串
     * */
    public  static String longTimeToString (long time) {
    	
    	if (time <= 0) {
    		
    		return "";
    	}
    	
    	Date date = new Date(time);
    	
    	SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    	String result = sdf.format(date);

    	
    	return result;
    }

	/*
     * 把长整型的时间，转换成字符串
     * */
	public  static String longTime1000ToString (long time) {

		if (time <= 0) {

			return "";
		}

		time = time/1000;

		Date date = new Date(time);

		SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String result = sdf.format(date);

		return result;
	}
    
    public static String longTimePeriodToString (long progress) {
    	
    	if (progress <= 0) {
    		
    		return "无";
    	}
    	
    	long Hours = progress/3600;
          
    	long hours = progress % 3600;
          
    	long Minites = hours /60;
          
    	long minites = hours%60;
    	
    	return Hours+":" +Minites+":"+minites;
    }
    
    public static String transferIs2String(InputStream is) {
		
		if (is ==  null) {
			
			return "";
		}
		String str = null;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len ;
		try {
			while ((len = is.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			str = new String(outStream.toByteArray(), HTTP.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return str;
	}

	public static Calendar getCalendar (String time) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date_start = null;

		Calendar result;

		try {
			date_start = sdf.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Calendar now = Calendar.getInstance();

		Calendar toCalendar = Calendar.getInstance();
		toCalendar.setTime(date_start);

		int count = toCalendar.compareTo(now);

		if (count == 0) {

			int hour1 = toCalendar.get(Calendar.HOUR_OF_DAY);
			int hour_now= now.get(Calendar.HOUR_OF_DAY);

			if ((hour1-hour_now )> 2) {

				result =  toCalendar;

			} else {

				toCalendar.set(Calendar.HOUR_OF_DAY,toCalendar.get(Calendar.HOUR_OF_DAY)+2);

				result = toCalendar;
			}

		} else if (count < 0) {

			if (now.get(Calendar.MINUTE) > 30) {

				now.set(Calendar.HOUR_OF_DAY,now.get(Calendar.HOUR_OF_DAY)+3);
				now.set(Calendar.MINUTE,0);

			} else {

				now.set(Calendar.HOUR_OF_DAY,now.get(Calendar.HOUR_OF_DAY)+2);
				now.set(Calendar.MINUTE,30);
			}

			result =  now;

		} else {

			result =  toCalendar;
		}

		return result;
	}
}
