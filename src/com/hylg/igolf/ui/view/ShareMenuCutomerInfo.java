package com.hylg.igolf.ui.view;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.utils.Base64;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class ShareMenuCutomerInfo implements OnClickListener {

	public final String 					TAG 									= "ShareMenu";

	/*ͷ���ķ���,��ҳ,����*/
	protected RelativeLayout 				mMainRelative                           = null,
											mMicroBlogRelative						= null,
											mMicroMessageFriendRelative				= null,
											mMicroMessageFriendCircleRelative		= null,
											mQQRelative								= null,
											mQQZoneRelative							= null;

	protected TextView						mCancleText          					= null;

	private PopupWindow						mMainMenuPop        					= null;

	private View 							mPopupWindowView						= null;

	private Activity                        mContext                    			= null;

	private View                            mView                                   = null;


	private Bitmap 							bitmap = null;

	private String                          imageUrl = "http://121.199.22.44:8080/gams/person/common/about_logo.png";

	private String                          targetUrl = "";

	String 									content = "从爱高尔夫分享,我的个人信息";

	public ShareMenuCutomerInfo(Activity context, View view,String sn) {
		
	
		mView = view;
		mContext = context;
		
		initPopupWindow();
		
		String param= "sn="+sn;
		
		DebugTools.getDebug().debug_v("getShareURL", "----->>>>"+param);
		
		try {
			targetUrl = BaseRequest.SERVER_PATH+"share/shareSelfInfo?param="+URLEncoder.encode(Base64.encode(param.getBytes()),HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		getBitmap();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		mMainMenuPop.dismiss();
		DebugTools.getDebug().debug_v(TAG, "super----->>>onclicked");
		if (arg0.getId() == mMainRelative.getId()) {

			mMainMenuPop.dismiss();
		}
		else if (arg0.getId() == mMicroBlogRelative.getId()) {
			
			/*新浪分享*/
			IWeiboShareAPI mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(mContext, "3633766045");
			mWeiboShareAPI.registerApp();
			
		    WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
		    TextObject textObject = new TextObject();
	        textObject.text = "分享到新浪微博 [ "+targetUrl+" ]";
	        
	        ImageObject imageObject = new ImageObject();
	       // Bitmap thumb = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.avatar_loading);
	        
	        bmpToByteArray();
	        imageObject.setImageObject(bitmap);
	        //imageObject.setImageObject(thumb);
	        
	        weiboMessage.textObject = textObject;
	        weiboMessage.imageObject = imageObject;
	        
	        // 2. 初始化从第三方到微博的消息请求
	        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
	        // 用transaction唯一标识一个请求
	        request.transaction = String.valueOf(System.currentTimeMillis());
	        request.multiMessage = weiboMessage;
	        
	       boolean result =  mWeiboShareAPI.sendRequest(mContext, request);
	       
	       //DebugTools.getDebug().debug_v("新浪微博分享结果", "------>>>"+result);
			
			
		} else if (arg0.getId() == mMicroMessageFriendRelative.getId()) {
			
			new microMessageThread(0).start();
			
		} else if (arg0.getId() == mMicroMessageFriendCircleRelative.getId()) {
			
			new microMessageThread(1).start();

		} else if (mQQZoneRelative.getId() == arg0.getId()) {
			
			Tencent mTencent = Tencent.createInstance("1104649528", mContext);
			
			final Bundle params = new Bundle();  


	        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, content);  
	        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, targetUrl);  
	        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "分享");  

	        ArrayList<String> sdf = new ArrayList<String>();
			sdf.add(imageUrl);
	        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, sdf);
	            
	        params.putString(QzoneShare.SHARE_TO_QQ_APP_NAME, "爱高尔夫");  
	        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT); 
			 
	        mTencent.shareToQzone(mContext, params, new IUiListener() {

				@Override
				public void onError(UiError arg0) {
					// TODO Auto-generated method stub

					DebugTools.getDebug().debug_v("UiError", "----->>>" + arg0);
				}

				@Override
				public void onComplete(Object arg0) {
					// TODO Auto-generated method stub
					DebugTools.getDebug().debug_v("UiError", "----->>>" + arg0);
				}

				@Override
				public void onCancel() {
					// TODO Auto-generated method stub
					DebugTools.getDebug().debug_v("UiError", "----->>>????????");
				}
			});
	        
			
		}  else if (arg0.getId() == mQQRelative.getId()) {
			
			int shareType = QQShare.SHARE_TO_QQ_TYPE_DEFAULT;
			Tencent mTencent = Tencent.createInstance("1104649528", mContext);
			
			final Bundle params = new Bundle();  

	        params.putString(QQShare.SHARE_TO_QQ_TITLE, content);  
	        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl);  
	        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "分享");   

	        ArrayList<String> sdf = new ArrayList<String>();
	        sdf.add(imageUrl);
	        
	        //params.putStringArrayList(QQShare.SHARE_TO_QQ_IMAGE_URL, sdf);
	        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
	            
	        //params.putString(shareType == QQShare.SHARE_TO_QQ_TYPE_DEFAULT ? QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL : QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);  
	        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "爱高尔夫");  
	        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, shareType); 
	            
//	        int mExtarFlag  = QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN;
//	        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, mExtarFlag);  
			 
	        mTencent.shareToQQ(mContext, params, new IUiListener() {
				
				@Override
				public void onError(UiError arg0) {
					// TODO Auto-generated method stub
					
					DebugTools.getDebug().debug_v("UiError", "----->>>"+arg0);
				}
				
				@Override
				public void onComplete(Object arg0) {
					// TODO Auto-generated method stub
					DebugTools.getDebug().debug_v("UiError", "----->>>"+arg0);
				}
				
				@Override
				public void onCancel() {
					// TODO Auto-generated method stub
					DebugTools.getDebug().debug_v("UiError", "----->>>????????");
				}
			});
	        
			
		} else if (arg0.getId() == mCancleText.getId()) {
			
			mMainMenuPop.dismiss();
		}
	}
	
	private class microMessageThread extends Thread {
		
		private int Type = 0;
		
		public microMessageThread (int type ) {
			
			Type = type;
		}
		
		@Override
		public void run() {
			
			IWXAPI  api = WXAPIFactory.createWXAPI(mContext, "wx14da9bc6378845fe",true);
			api.registerApp("wx14da9bc6378845fe");
		 	WXWebpageObject webpage = new WXWebpageObject();  
	        webpage.webpageUrl = targetUrl;  
	        WXMediaMessage msg = new WXMediaMessage(webpage);  

	        msg.title = content;  
	        //msg.description = "sadfsdf";  
	           
	        if(bitmap == null){  
	            Toast.makeText(mContext, "图片不能为空", Toast.LENGTH_SHORT).show();  
	        }else{  
	            msg.thumbData = bmpToByteArray();  
	        }  
	          
	        SendMessageToWX.Req req = new SendMessageToWX.Req();  
	        req.transaction = buildTransaction("webpage");  
	        req.message = msg;  
	        req.scene = Type;  
	        api.sendReq(req);  
		}
	}
	
	 private String buildTransaction(final String type) {  
	        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();  
	 }  
	 
	 private byte[] bmpToByteArray () {
		 
		 ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
		 
		 int quality = 100;

		 byte[] b = null;

		 if (bitmap != null) {

			 bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayBitmapStream);

			 while (byteArrayBitmapStream.toByteArray().length > 30 * 1024) {

				 quality -= 10;
				 byteArrayBitmapStream.reset();
				 bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayBitmapStream);

			 }

			 b = byteArrayBitmapStream.toByteArray();

			 bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);

		 }

		 return b;
	 }
	
	
	/**  
     * ��ʼ��popupwindow  
     */  
    private void initPopupWindow(){  
        initPopupWindowView();  
        //��ʼ��popupwindow������ʾview�����ø�view�Ŀ��/�߶�  
        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        
        int height = mContext.getResources().getDisplayMetrics().heightPixels;
        
        mMainMenuPop = new PopupWindow(mPopupWindowView,width,height);
        mMainMenuPop.setFocusable(true);  
        mMainMenuPop.setOutsideTouchable(true);  
        
        // �����Ϊ�˵��������Back��Ҳ��ʹ����ʧ�����Ҳ�����Ӱ����ı�����ʹ�ø÷����������֮�⣬�ſɹرմ���  
        mMainMenuPop.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_gray));  

        //���ý��롢��������Ч��  
        //mMainMenuPop.setAnimationStyle(R.style.popupwindow);  
        mMainMenuPop.update();  
        //popupWindow����dismissʱ������������setOutsideTouchable(true)�����view֮��/����back�ĵط�Ҳ�ᴥ��  
        mMainMenuPop.setOnDismissListener(new OnDismissListener() {  
              
            @Override  
            public void onDismiss() {  
                // TODO Auto-generated method stub  
 
            }  
        });  
    }  
  
    /**  
     * ��ʼ��popupwindowView,����view�е�textview����¼�  
     */  
    private void initPopupWindowView(){
  
        mPopupWindowView 					= mContext.getLayoutInflater().inflate(R.layout.share_menu, null);
		mMainRelative                       = (RelativeLayout) mPopupWindowView.findViewById(R.id.main_layout);
		mMicroBlogRelative					= (RelativeLayout) mPopupWindowView.findViewById(R.id.share_menu_micro_blog_relative);
		mMicroMessageFriendRelative			= (RelativeLayout) mPopupWindowView.findViewById(R.id.share_menu_micro_message_friend_relative); 
		mMicroMessageFriendCircleRelative	= (RelativeLayout) mPopupWindowView.findViewById(R.id.share_menu_micro_message_friend_circle_relative); 
		mQQRelative							= (RelativeLayout) mPopupWindowView.findViewById(R.id.share_menu_QQ_relative); 
		mQQZoneRelative						= (RelativeLayout) mPopupWindowView.findViewById(R.id.share_menu_QQ_Zone_relative); 
		mCancleText							= (TextView) mPopupWindowView.findViewById(R.id.share_menu_cancel_text);

		mMainRelative.setOnClickListener(this);
		mCancleText.setOnClickListener(this);
		mMicroBlogRelative.setOnClickListener(this);
		mMicroMessageFriendRelative.setOnClickListener(this);
		mMicroMessageFriendCircleRelative.setOnClickListener(this);
		mQQRelative.setOnClickListener(this);
		mQQZoneRelative.setOnClickListener(this); 
        
    }  
	
	 /**��ʾpopupwindow*/  
    public void showPopupWindow(){  
  
        if(!mMainMenuPop.isShowing()) {  
        	
        	mMainMenuPop.setAnimationStyle(R.style.PopupAnimation);
        	mMainMenuPop.showAtLocation(mView, Gravity.BOTTOM, 0, 0);   
        	
        } else{  
        	
        	mMainMenuPop.dismiss();  
        }  
    }  
    
    private void getBitmap () {


		bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.about_logo);
    	
    }
}
