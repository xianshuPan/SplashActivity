package com.hylg.igolf;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.hylg.igolf.cs.data.FriendHotItem;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.FriendMessageNew;
import com.hylg.igolf.cs.request.FriendMessageNewPictureUpload;
import com.hylg.igolf.imagepicker.Config;
import com.hylg.igolf.utils.FileUtils;
import com.hylg.igolf.utils.Utils;

public class HdService extends Service {

	private final String                        TAG		= "ShoppingMallService";
	/*
	 * 
	 */
	private ExecutorService 			 		ses 		= null;
	
	private FriendHotItem						mFriendMessageNewItem   = null;
	
	private AsyncTask 							mUploadFriendTips;
	
	private String 								updateFilePath = null,
												down_url;
	
	private HomeWatcher 						mHomeWatcher;
												
	
	private static final int 					TIMEOUT = 10 * 1000;
	private static final int 					DOWN_OK = 1;
	private static final int 					DOWN_ERROR = 0;
	private static final int 					DOWN_STEP = 2;
	
	private NotificationManager                 notificationManager = null;
	
	private Notification 						notification = null;
	
	private RemoteViews 						rViews;

	@Override
	public void onDestroy() {
		super.onDestroy();
		DebugTools.getDebug().debug_v(TAG, "Service------>>>onDestroy");
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		
		DebugTools.getDebug().debug_v(TAG, "Service------>>>onBind");
		return null;
	}

	@Override
	public void onCreate() {

		DebugTools.getDebug().debug_v(TAG, "Service------>>>onCreate");
		
		notificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
		
		notification = new Notification();
		notification.icon = R.drawable.ic_launcher;
		notification.tickerText = getText(R.string.app_name);
		
		// 加载Notification的布局文件
		rViews = new RemoteViews(getPackageName(), R.layout.down_load_progress);
		  // 设置下载进度条
		rViews.setProgressBar(R.id.downloadFile_pb, 100, 0, false);
		rViews.setTextViewText(R.id.downloadFileName, "爱高尔夫");
		rViews.setTextViewText(R.id.downloadP, "0%");
		
		notification.tickerText = "爱高尔夫";
		
		//registerReceiver(new HelloBroadReciever(), new IntentFilter("com.hylg.igolf.stop"));
		
		ses = Executors.newScheduledThreadPool(5);
		
		mHomeWatcher = new HomeWatcher(this);  
	    mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
			
			@Override
			public void onHomePressed() {
//				// TODO Auto-generated method stub
//				
//				DebugTools.getDebug().debug_v(TAG, "开启后台上传！");
//				
//				if (Config.mFriendMessageNewItem != null) {
//					
//					mUploadFriendTips = new AsyncTask<Object, Object, Integer>() {
//						
//						FriendMessageNew request = new FriendMessageNew(HdService.this,Config.mFriendMessageNewItem);
//						@Override
//						protected Integer doInBackground(Object... params) {
//		
//								return request.connectUrl();
//							
//						}
//						
//						@Override
//						protected void onPostExecute(Integer result) {
//							super.onPostExecute(result);
//							
//							if(BaseRequest.REQ_RET_OK == result) {
//								
//								Config.mFriendMessageNewItem = null;
//								DebugTools.getDebug().debug_v(TAG, "后台上传朋友圈成功！");
//								
//							} else {
//								
//		
//							}
//						}
//					};
//					
//					mUploadFriendTips.execute(null, null, null);
//					
//					new AsyncTask<Object, Object, Integer>() {
//						
//						FriendMessageNewPictureUpload request = new FriendMessageNewPictureUpload(HdService.this);
//						@Override
//						protected Integer doInBackground(Object... params) {
//		
//								return request.connectUrl();
//							
//						}
//						
//						@Override
//						protected void onPostExecute(Integer result) {
//							super.onPostExecute(result);
//							
//							if(BaseRequest.REQ_RET_OK == result) {
//								
//								Config.mFriendMessageNewItem = null;
//								DebugTools.getDebug().debug_v(TAG, "后台上传图片成功！");
//								
//							} else {
//								
//		
//							}
//						}
//					}.execute(null,null,null);
//					
//				}
//				
			}
			
			@Override
			public void onHomeLongPressed() {
				// TODO Auto-generated method stub
				
			}
		});  
	   //  mHomeWatcher.startWatch();  
		
		super.onCreate();
		
	}
	
	  // 回调接口  
    public interface OnHomePressedListener {  
        public void onHomePressed();  
        public void onHomeLongPressed();  
    }  
	
	/** 
	 * Home键监听封装 
	 */  
	public class HomeWatcher {  
	  
	    static final String TAG = "HomeWatcher";  
	    private Context mContext;  
	    private IntentFilter mFilter;  
	    private OnHomePressedListener mListener;  
	    private InnerRecevier mRecevier;  
	  
	  
	    public HomeWatcher(Context context) {  
	        mContext = context;  
	        mRecevier = new InnerRecevier();  
	        mFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);  
	    }  
	  
	    /** 
	     * 设置监听 
	     * @param listener 
	     */  
	    public void setOnHomePressedListener(OnHomePressedListener listener) {  
	        mListener = listener;  
	    }  
	  
	    /** 
	     * 开始监听，注册广播 
	     */  
	    public void startWatch() {  
	        if (mRecevier != null) {  
	            mContext.registerReceiver(mRecevier, mFilter);  
	        }  
	    }  
	  
	    /** 
	     * 停止监听，注销广播 
	     */  
	    public void stopWatch() {  
	        if (mRecevier != null) {  
	            mContext.unregisterReceiver(mRecevier);  
	        }  
	    }  
	  
	    /** 
	     * 广播接收者 
	     */  
	    class InnerRecevier extends BroadcastReceiver {  
	        final String SYSTEM_DIALOG_REASON_KEY = "reason";  
	        final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";  
	        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";  
	        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";  
	  
	        @Override  
	        public void onReceive(Context context, Intent intent) {  
	            String action = intent.getAction();  
	            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {  
	                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);  
	                if (reason != null) {  
	                    DebugTools.getDebug().debug_v(TAG, "action:" + action + ",reason:" + reason);  
	                    if (mListener != null) {  
	                        if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {  
	                            // 短按home键  
	                            mListener.onHomePressed();  
	                        } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {  
	                            // 长按home键  
	                            mListener.onHomeLongPressed();  
	                        }  
	                    }  
	                }  
	            }  
	        }  
	    }  
	}  
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		DebugTools.getDebug().debug_v(TAG, "Service------>>>onStartCommand");
		
		DebugTools.getDebug().debug_v(TAG, "开启后台上传！");
		
		if (intent != null && intent.getSerializableExtra("Message") != null &&
			Config.mFriendMessageNewItem != null) {
			
			mUploadFriendTips = new AsyncTask<Object, Object, Integer>() {
				
				FriendMessageNew request = new FriendMessageNew(HdService.this,Config.mFriendMessageNewItem);
				
				@Override
				protected Integer doInBackground(Object... params) {

						return request.connectUrl();
					
				}
				
				@Override
				protected void onPostExecute(Integer result) {
					super.onPostExecute(result);
					
					if(BaseRequest.REQ_RET_OK == result) {
						
						Config.mFriendMessageNewItem = null;
						DebugTools.getDebug().debug_v(TAG, "后台上传朋友圈成功！");
						
					} else {
						

					}
				}
			};
			
			mUploadFriendTips.execute(null, null, null);
			

			/*开启上传朋友圈图片的线程*/
			FriendMessageNewPictureUpload request1 = new FriendMessageNewPictureUpload(HdService.this);
			request1.start();
			
			
		/*开启下载 爱高尔夫的apk 线程 */
		} else if (intent != null && intent.getStringExtra("DownLoadUrl") != null) {
			
			down_url = intent.getStringExtra("DownLoadUrl");
			updateFilePath = createFile("igolf.apk");
			
			doDownloadApk(updateFilePath);
		}
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	private String createFile(String name) {
		String path = FileUtils.getApkPath(this);
		if(null == path) {
			path = FileUtils.getFilePathName(getFilesDir().getAbsolutePath(), FileUtils.CACHE_DIR_APK);
		}
		Utils.logh(TAG, "createFile path : " + path);
		File updateDir = new File(path);
		if (!updateDir.exists()) {
			updateDir.mkdirs();
		}

		File updateFile = new File(FileUtils.getFilePathName(path, name));
		if(updateFile.exists()) {
			updateFile.delete();
		}
		try {
			updateFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return updateFile.getAbsolutePath();
	}
	
	public void doDownloadApk(final String filePath) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean error = false;
				final Message message = new Message();
				try {
					if (downloadUpdateFile(down_url, filePath) > 0) {
						message.what = DOWN_OK;
						handler.sendMessage(message);
						error = false;
					} else {
						error = true;
					}
				} catch (IOException e) {
					e.printStackTrace();
					error = true;
				}
				if(error) {
					File file = new File(filePath);
					if(null != file && file.exists()) {
						file.delete();
					}
					message.what = DOWN_ERROR;
					handler.sendMessage(message);
					//startNewActivity();
				}
			}
		}).start();
	}

	/*
	 * 开启下载线程
	 * */
	public long downloadUpdateFile(String down_url, String file)
			throws IOException {
		Utils.logh(TAG, "downloadUpdateFile down_url: " + down_url + " file: " + file);
		URL url;
		try {
			url = new URL(down_url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return 0;
		}

		int down_step = 1;
		int totalSize;
		int downloadCount = 0;
		int updateCount = 0;
		InputStream inputStream;
		OutputStream outputStream;

		HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
		httpURLConnection.setConnectTimeout(TIMEOUT);
		httpURLConnection.setReadTimeout(TIMEOUT);
		// get the total size of file
		totalSize = httpURLConnection.getContentLength();
		if (httpURLConnection.getResponseCode() == 404) {
			return 0;
		}
		inputStream = httpURLConnection.getInputStream();
		outputStream = new FileOutputStream(file, false);// overlay if file
															// exist
		byte buffer[] = new byte[1024];
		int readsize = 0;
		while ((readsize = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, readsize);
			downloadCount += readsize;
			if (updateCount == 0 ||
					(downloadCount * 100 / totalSize - down_step) >= updateCount) {
				updateCount += down_step;
				final Message message = new Message();
				message.what = DOWN_STEP;
				message.arg1 = updateCount;
				handler.sendMessage(message);
				Utils.logh(TAG, "updateCount: " + updateCount);
			}

		}
		if (httpURLConnection != null) {
			httpURLConnection.disconnect();
		}
		inputStream.close();
		outputStream.close();

		return downloadCount;

	}

	private void installFile() {
		
		DebugTools.getDebug().debug_v("安装文件路径", "------》》》"+updateFilePath);
		File f = new File(updateFilePath);
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(f),
				"application/vnd.android.package-archive");
		startActivity(intent);
//		startActivity(intent);
	}
	
	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
				case DOWN_STEP:
					
				rViews.setProgressBar(R.id.downloadFile_pb, 100,(int) msg.arg1,false);
				rViews.setTextViewText(R.id.downloadP, msg.arg1+"%");
				notification.contentView = rViews;
				notificationManager.notify(R.id.about_version, notification);	   
				DebugTools.getDebug().debug_v("apk下载进度", "------>>>>"+msg.arg1);

					break;
				case DOWN_OK:
					Utils.logh(TAG, "DOWN_OK");
					notificationManager.cancel(R.id.about_version);
					installFile();
					
					break;
				case DOWN_ERROR:
					Utils.logh(TAG, "DOWN_ERROR");
					break;
			}
			return false;
		}
		
	});
}
