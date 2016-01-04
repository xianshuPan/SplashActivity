
package com.hylg.igolf.ui.friend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.R;
import com.hylg.igolf.imagepicker.Config;
import com.hylg.igolf.utils.FileTool;
import com.xc.lib.view.SDK16;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.util.FloatMath;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CameraActivity extends Activity implements OnClickListener ,OnItemClickListener{

	private final String 							TAG = "CameraActivity";
	
	private final int 								Type_Counting_Int = 0,
													Type_Saving_Count_Int = 1;
	
	/*相机预览*/
	private FrameLayout 							mCameraPreViewFrame;
	private SurfaceView 							mCameraPreViewSurface;
	private ImageView                               mCameraImage;
	
	private Camera 									mCamera;
	private Camera.Parameters 						mCameraParas = null;
	private int                                     mMaxZoomInt = 0,
													mZoomInt = 0;
	private boolean                                 mIsZoom = false;
	
	private PictureCallback 						mPicture;
	
	private File 									pictureFile ;
	
	private CameraPreview 							mPreview;
	
	
	private ImageView                               mCurrentImage;
	
	
	private TextView                                mCountText ;
	
	/*接受数据*/
	private Intent 									mIntent ;
	
	/**/
	private String 									mRequestType ,
													mTimeStampStr ,
													mCurrentPathStr;
	
	private boolean 								mstop,
													mbIsCounting = false,
													mIsEditListShowing = false;
	
													//倒计时的
	private int                                     miCountSecond = 0,
			
													/*连拍间隔时间*/
													miCameraSleepTime = 50,
			
													//保存图片的张数
													miCountPictrue = 1;
	
	/*待保存的图片数据*/
	private List<byte[]>                            mQueueData = null;
	
	
	/*相机的相关操作*/
	private LinearLayout                            mBackLinear = null,
													mCameraFlashLinear = null,
													mCameraChangeLinear = null,
													mCameraStyleLinear = null,
													mCameraMoreLinear = null;
	
	private ImageView								mCameraBackImage  = null,
													mTakePictureImage = null,
													mCameraStyleImage = null,
													mCameraFlashImage = null;
	
	// 0表示后置，1表示前置  
    private int 									cameraPosition = 1,
    												mCameraOrientationLandscape = 1,
    												mTakePictureCount = Config.SELECT_MAX_NUM;
    
    private SurfaceTexture                          mHolder = null;
    
    private ListView                                mCameraMoreList = null;
    
    private int                                     mode = 0,
    												current_x,
    												current_y,
    												start_x,
    												start_y;
    
    private float                                   beforeLenght,
    												afterLenght;
    
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.camera);
		
		/*检查是否有相机*/
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			
			Toast.makeText(this, R.string.no_camera, Toast.LENGTH_SHORT).show();
			this.onDestroy();
		}
		
		/*初始化UI控件几设计时间监听器*/
		
		initUI();
		
	}
	
	@Override
	protected void onResume() {
		//DebugTools.getDebug().debug_v(TAG, "onResume");
		
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		//DebugTools.getDebug().debug_v(TAG, "onDestroy" );
		
		//if (mCamera != null) {
			
			//mCamera.stopPreview();
		    //mCamera.release();
		//}
		
		if (mQueueData != null) {
			
			mQueueData.clear();
			
			mQueueData = null;
		}
		
		releaseCamera();
		
		super.onDestroy();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		
		  if(newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
		  {
			  //TODO 某些操作 
			  mCameraOrientationLandscape = 1;
			  
		  } else if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			  
			  mCameraOrientationLandscape = 0;
		  }
		  
		  DebugTools.getDebug().debug_v(TAG, "mCameraOrientationLandscape------->>>"+mCameraOrientationLandscape);
		
		super.onConfigurationChanged(newConfig);
	}
	
	public boolean onKeyDown(int keyCode , KeyEvent event) {
		
		if ((keyCode == KeyEvent.KEYCODE_BACK) && 
			(event.getAction() == KeyEvent.ACTION_DOWN)) {
			
			//startActivity( new Intent(this, FriendMessageNewActivity.class));
			
			this.finish();
		}
		
		return super.onKeyDown(keyCode, event);
	}

	/*onclick�¼��Ĵ���*/
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if (v.getId() == mTakePictureImage.getId()) {
			
			hideEditList();
			
			mstop = true;
			mTakePictureImage.setEnabled(false);
			mCountText.setVisibility(View.VISIBLE);
			
			/*
			 * 开启倒计时线程，倒计时结束后拍照
			 * 注拍单张不用倒计时;
			 * */
			
			if (mTakePictureCount == Config.SELECT_MAX_NUM) {
				
				new CountingTimeThread().start();
				
			} else {
				
				mTakePictureImage.setEnabled(false);
				//mCountText.setVisibility(View.GONE);
				/*倒计时结束，开始拍照*/
				mCamera.takePicture(null, null, mPicture);
				mTimeStampStr = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date());
			}
			
		
		} else if (v.getId() == mCameraBackImage.getId()) {
			
			hideEditList();
			this.finish();
			
		} else if (mCameraFlashLinear.getId() == v.getId()) {
			
			hideEditList();
			String mode = mCameraParas.getFlashMode();
			if (mode.equalsIgnoreCase(Parameters.FLASH_MODE_TORCH)) {
				
				mCameraParas.setFlashMode(Parameters.FLASH_MODE_OFF);
				mCameraFlashImage.setBackgroundResource(R.drawable.camera_flash_close);
				
			} else if (mode.equalsIgnoreCase(Parameters.FLASH_MODE_OFF)) {
				
				mCameraParas.setFlashMode(Parameters.FLASH_MODE_AUTO);
				mCameraFlashImage.setBackgroundResource(R.drawable.camera_flash_auto);
				
			}  else if (mode.equalsIgnoreCase(Parameters.FLASH_MODE_AUTO)) {
				
				mCameraParas.setFlashMode(Parameters.FLASH_MODE_TORCH);
				mCameraFlashImage.setBackgroundResource(R.drawable.camera_flash_open);
			}
			
			setStartPreview();
			
		} else if (v.getId() == mCameraChangeLinear.getId()) {
			
			hideEditList();
			
			int cameraCount ;
            CameraInfo cameraInfo = new CameraInfo();  
            cameraCount = Camera.getNumberOfCameras();// 得到摄像头的个数  
  
            DebugTools.getDebug().debug_v(TAG, "cameraCount------->>>"+cameraCount);
            for (int i = 0; i < cameraCount; i++) {  
            	
                Camera.getCameraInfo(i, cameraInfo);// 得到每一个摄像头的信息  
                if (cameraPosition == 1) {  
                    // 现在是后置，变更为前置  
                    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {  
                        /**  
                         * 记得释放camera，方便其他应用调用  
                         */  
                        releaseCamera();  
                        // 打开当前选中的摄像头  
                        mCamera = Camera.open(i);  
                        
                        // 通过surfaceview显示取景画面  
                        setStartPreview();  
                        cameraPosition = 0;  
                        break;  
                    }  
                } else {  
                    // 现在是前置， 变更为后置  
                    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {  
                        /**  
                         * 记得释放camera，方便其他应用调用  
                         */  
                        releaseCamera();  
                        mCamera = Camera.open(i);  
                        setStartPreview();  
                        cameraPosition = 1;  
                        break;  
                    }  
                }  
  
            }  
            
		} else if (v.getId() == mCameraStyleLinear.getId()) {
			
			hideEditList();
			if (mTakePictureCount == Config.SELECT_MAX_NUM) {
				
				mTakePictureCount = 1;
				mCameraStyleImage.setBackgroundResource(R.drawable.camera_style);
				
			} else if (mTakePictureCount == 1) {
				
				mTakePictureCount = Config.SELECT_MAX_NUM;
				mCameraStyleImage.setBackgroundResource(R.drawable.camera_style_nine);
			}
			
		} else if (v.getId() == mCameraMoreLinear.getId()) {
			
			if (mIsEditListShowing) {
				
				hideEditList();
				
			} else {
				
				showEditList();
			}
		}
	}
	
	/*����UI����ص��¼�������*/
	private void initUI() {
		
		mCameraPreViewFrame 			= (FrameLayout) findViewById(R.id.tools_camera_previewFrame);
		//mCameraImage                    = (ImageView) findViewById(R.id.tools_camera_preview_image);
		//mCameraPreViewSurface           = (SurfaceView) findViewById(R.id.tools_camera_preview_surface);
		mCountText                      = (TextView) findViewById(R.id.tools_camera_count_text);
		mCurrentImage 					= (ImageView) findViewById(R.id.tools_camera_current_image);
		mCameraBackImage                = (ImageView) findViewById(R.id.camera_back_image);
		
		mBackLinear 					= (LinearLayout) findViewById(R.id.camera_back_linear);
		mCameraFlashLinear 				= (LinearLayout) findViewById(R.id.camera_flash_linear);
		mCameraChangeLinear 			= (LinearLayout) findViewById(R.id.camera_change_linear);
		mCameraStyleLinear 				= (LinearLayout) findViewById(R.id.camera_style_linear);
		mCameraMoreLinear 				= (LinearLayout) findViewById(R.id.camera_more_linear);
		mTakePictureImage 				= (ImageView) findViewById(R.id.camera_tak_picture_image);
		mCameraStyleImage				= (ImageView) findViewById(R.id.camera_style_image);
		mCameraFlashImage				= (ImageView) findViewById(R.id.camera_flash_image);
		mCameraMoreList					= (ListView) findViewById(R.id.camera_more_List);
		mQueueData                      = new ArrayList<byte []>();
		
		/*������Ϊ�գ�ֱ�ӽ���*/
		mCamera 	= getCameraInstance();
		
		if (mCamera == null) {
			
			this.finish();
			
			return;
		}
		
//		PackageManager pm = this.getPackageManager();  
//	    boolean isSupportMultiTouch = pm.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH); 
//	    DebugTools.getDebug().debug_v("camera", "是否支持多点触控-----》》》》"+isSupportMultiTouch);  
		
		mCameraParas = mCamera.getParameters();
		mIsZoom = mCameraParas.isZoomSupported();
		mMaxZoomInt = mCameraParas.getMaxZoom(); 
		mZoomInt = mCameraParas.getZoom();
		mCameraParas.setZoom(1);
		mCameraParas.setFlashMode(Parameters.FLASH_MODE_OFF);
		
		List<Size> previewSizes = mCameraParas.getSupportedPreviewSizes(); 
    	for(int i=0; i<previewSizes.size(); i++) {  
            Size size = previewSizes.get(i); 
            
            DebugTools.getDebug().debug_v("camera", "initCamera:摄像头支持的previewSizes: width = "+size.width+"height = "+size.height);  
        }  
    	
    	//mCameraParas.setPreviewSize(1280, 720);
    	//mCameraParas.setPictureSize(1280, 720);
    	
    	mCameraParas.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
    	mCameraParas.setJpegQuality(100);
    	mCameraParas.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
    	
		//mCameraParas.set("orientation", "portrait");
		//mCameraParas.setRotation(90);
    	
    	//mCameraParas.set("rotation", 90);
		mPreview = new CameraPreview(this);
		
		mCameraPreViewFrame.addView(mPreview);
		//mCameraPreViewFrame.setOnTouchListener(new MyOnTouchListener());
		//mCameraImage.setOnTouchListener(new MyOnTouchListener());
		
		
		//mHolder = mCameraPreViewSurface.getHolder();
		//mHolder.addCallback(mPreview);
		
		mTakePictureImage.setOnClickListener(this);
		mTakePictureImage.setOnTouchListener(new MyOnTouchListener());
		//mCurrentImage.setOnClickListener(this);
		mBackLinear.setOnClickListener(this);
		mCameraFlashLinear.setOnClickListener(this);
		mCameraChangeLinear.setOnClickListener(this);
		mCameraStyleLinear.setOnClickListener(this);
		mCameraMoreLinear.setOnClickListener(this);
		mCameraMoreList.setOnItemClickListener(this);
		mCameraBackImage.setOnClickListener(this);
		
		mCameraMoreList.setAdapter(new ArrayAdapter<String>(this,  android.R.layout.simple_list_item_1,getData() ));
		
		//String currentImagePath = getCurrentImagePath();
		//DownLoadImageTool.getInstance(this).displayImage("file:///"+currentImagePath, mCurrentImage);
		
		Intent data = getIntent();
		if (data != null && data.getIntExtra("Model", -1) > 0) {
			
			mTakePictureCount = 1;
			mCameraStyleImage.setBackgroundResource(R.drawable.camera_style);
		}
		
		mPicture = new PictureCallback() {
			
			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				// TODO Auto-generated method stub
				
				//mCountText.setVisibility(View.GONE);
	        	mbIsCounting = false;
	        	miCountSecond = 0;
	        	
	        	long start = System.currentTimeMillis();
	        	DebugTools.getDebug().debug_v(TAG, "start_take_pictrue"+start);
	        	mCamera.startPreview();
	        	
	        	long end = System.currentTimeMillis();
	        	DebugTools.getDebug().debug_v(TAG, "take_pictrue_one_time"+(end-start));
	        	
	        	mQueueData.add(data);
	        	
	        	/*图片数量*/
	        	mCountText.setText(String.valueOf(miCountPictrue));
	        	miCountPictrue++;
	        	
				if (miCountPictrue <= mTakePictureCount) {
					
					if (miCameraSleepTime > 0) {
						
						try {
							Thread.sleep(miCameraSleepTime);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					mCamera.takePicture(null, null, mPicture);
					
				} else {
					
					miCountPictrue = 0;
					mTakePictureImage.setEnabled(false);
					
					/*开启保存图片线程*/
					DebugTools.getDebug().debug_v(TAG, "------》》》l;l;l;l;l;l;l;l;l;l;l;l;开启保存线程");
					mCurrentImage.setVisibility(View.VISIBLE);
					new SavingPicThread().start();
				}
				
				DebugTools.getDebug().debug_v(TAG, "����ͼƬ����ʱ�䣺"+System.currentTimeMillis());
			}
		    
		};
	}
	
	 /*为了使图片按钮按下和弹起状态不同，采用过滤颜色的方法.按下的时候让图片颜色变淡*/  
    public class MyOnTouchListener implements OnTouchListener{  
  
        public final  float[] BT_SELECTED= new float[]  
                { 2, 0, 0, 0, 2,  
            0, 2, 0, 0, 2,  
            0, 0, 2, 0, 2,  
            0, 0, 0, 1, 0 };                  
  
        public final float[] BT_NOT_SELECTED=new float[]  
                { 1, 0, 0, 0, 0,  
            0, 1, 0, 0, 0,  
            0, 0, 1, 0, 0,  
            0, 0, 0, 1, 0 };  
        
        
        public boolean onTouch(View v, MotionEvent event) {  
            // TODO Auto-generated method stub  
        	
        	if (v.getId() == mTakePictureImage.getId()) {
        		
        		 if(event.getAction() == MotionEvent.ACTION_DOWN){  
                     v.getBackground().setColorFilter(new ColorMatrixColorFilter(BT_SELECTED));  
                     v.setBackground(v.getBackground());  
                 }  
                 else if(event.getAction() == MotionEvent.ACTION_UP){  
                     v.getBackground().setColorFilter(new ColorMatrixColorFilter(BT_NOT_SELECTED));  
                     v.setBackground(v.getBackground());  
                       
                 }  
        		 
        	} 
        		
//        	if (v.getId() == mCameraImage.getId()) {
//        		
//        		switch (event.getAction() & MotionEvent.ACTION_MASK) {
//        		
//                case MotionEvent.ACTION_DOWN:  
//                	
//                	DebugTools.getDebug().debug_v(TAG, "mCameraPreViewFrame------>>>ACTION_DOWN");
//                    onTouchDown(event);  
//                    break;  
//                    
//                // 多点触摸  
//                case MotionEvent.ACTION_POINTER_DOWN: 
//                	
//                	DebugTools.getDebug().debug_v(TAG, "mCameraPreViewFrame------>>>ACTION_POINTER_DOWN");
//                    onPointerDown(event);  
//                    break;  
//          
//                case MotionEvent.ACTION_MOVE:  
//                	
//                	DebugTools.getDebug().debug_v(TAG, "mCameraPreViewFrame------>>>ACTION_MOVE");
//                    onTouchMove(event);  
//                    break;  
//                case MotionEvent.ACTION_UP:  
//                	
//                	DebugTools.getDebug().debug_v(TAG, "mCameraPreViewFrame------>>>ACTION_UP");
//                    mode = MODE.NONE;  
//                    break;  
//          
//                // 多点松开  
//                case MotionEvent.ACTION_POINTER_UP:  
//                	
//                	DebugTools.getDebug().debug_v(TAG, "mCameraPreViewFrame------>>>ACTION_POINTER_UP");
//                    mode = MODE.NONE;  
//                    /** 执行缩放还原 **/  
////                    if (isScaleAnim) {  
////                        doScaleAnim();  
////                    }  
//                    break;  
//                }  
        	//}
           
            return false;  
        }  
  
    }  
    
    void onTouchDown(MotionEvent event) {  
        mode = MODE.DRAG;  
  
        start_x = (int) event.getX();  
        start_y = (int) event.getY();  
  
    }  
    
    /** 两个手指 只能放大缩小 **/  
    void onPointerDown(MotionEvent event) {  
        if (event.getPointerCount() == 2) {  
            mode = MODE.ZOOM;  
            beforeLenght = getDistance(event);// 获取两点的距离  
        }  
    }  
    
    /** 移动的处理 **/  
    void onTouchMove(MotionEvent event) {  
        /** 处理拖动 **/  
        if (mode == MODE.DRAG) {  
      
            current_x = (int) event.getX();  
            current_y = (int) event.getY();  
      
        }  
        /** 处理缩放 **/  
        else if (mode == MODE.ZOOM) {  
      
            afterLenght = getDistance(event);// 获取两点的距离  
      
            float gapLenght = afterLenght - beforeLenght;// 变化的长度  
      
            if (Math.abs(gapLenght) > 1f) {  
                //scale_temp = afterLenght / beforeLenght;// 求的缩放的比例  
      
                setScale(gapLenght);  
      
                beforeLenght = afterLenght;  
            }  
        }  
      
    }  
    
    
    private float getDistance(MotionEvent event) {  
        float x = event.getX(0) - event.getX(1);  
        float y = event.getY(0) - event.getY(1);  
        return FloatMath.sqrt(x * x + y * y);  
    }  
    
    private void setScale(float x) {
    	
    	if (x > 0) {
    		
    		mZoomInt++;
    		
    	} else {
    		
    		mZoomInt--;
    	}
    	
    	if (mIsZoom && mZoomInt > 0 && mZoomInt < mMaxZoomInt) {
    		
    		 mCameraParas.setZoom(mZoomInt);
    	}
    	
    	setStartPreview();
    }
	
	 private ArrayList<String> getData()  
	 {  
		 ArrayList<String> list = new ArrayList<String>();
		 String [] more = getResources().getStringArray(R.array.camera_more_time);
		 
		 for (int i =0 ; i<more.length ;i++) {
			 
			 list.add(more[i]);
		 }
	     return list;  
	 }  
	
	  private void showEditList() {
			if (mIsEditListShowing) {
				return;
			}
			
			//hideFilterList();
			
			TranslateAnimation translateAnimation = new TranslateAnimation(
					TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
					TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
					TranslateAnimation.RELATIVE_TO_SELF, 1.0f,
					TranslateAnimation.RELATIVE_TO_SELF, 0.0f);
			translateAnimation.setInterpolator(new DecelerateInterpolator(2.0f));
			AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
			AnimationSet animation = new AnimationSet(false);
			animation.addAnimation(translateAnimation);
			animation.addAnimation(alphaAnimation);
			animation.setDuration(500);
			mCameraMoreList.startAnimation(animation);

			//This is needed because some older android
			//versions contain a bug where AnimationListeners
			//are not called in Animations
			mCameraMoreList.setVisibility(View.VISIBLE);
			
			mIsEditListShowing = true;
		}
		
		private void hideEditList() {
			if (!mIsEditListShowing) {
				return;
			}
			
			TranslateAnimation translateAnimation = new TranslateAnimation(
					TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
					TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
					TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
					TranslateAnimation.RELATIVE_TO_SELF, 1.0f);
			translateAnimation.setInterpolator(new DecelerateInterpolator(2.0f));
			AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
			AnimationSet animation = new AnimationSet(false);
			animation.addAnimation(translateAnimation);
			animation.addAnimation(alphaAnimation);
			animation.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
					// Do nothing
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// Do nothing
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					mCameraMoreList.setVisibility(View.INVISIBLE);
				}
			});
			animation.setDuration(500);
			mCameraMoreList.startAnimation(animation);
			
			mIsEditListShowing = false;
		}
	
	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance
	    }
	    catch (Exception e){
	        // Camera is not available (in use or does not exist)
	    }
	    return c; // returns null if camera is unavailable
	}
	
	
//	/** A basic Camera preview class */
//	public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
//
//	    public CameraPreview(Context context) {
//			super(context);
//			
//			mHolder = getHolder();
//			mHolder.addCallback(this);
//			// TODO Auto-generated constructor stub
//		}
//
//		public void surfaceCreated(SurfaceHolder holder) {
//	        // The Surface has been created, now tell the camera where to draw the preview.
//	    	
//	    	/*��ݻ�ȡ�����������������*/
//	    	
//	        try {
//	        	
//	        	setStartPreview();
//	            //mCamera.startPreview();
//	        } catch (NullPointerException e) {
//	        	
//	        	releaseCamera();
//	        	//DebugTools.getDebug().debug_v(TAG, "Error setting camera preview: " + e.getMessage());
//	        } catch (Exception e) {
//	        	mCamera.release();
//	        	//DebugTools.getDebug().debug_v(TAG, "Error setting camera preview: " + e.getMessage());
//	        }
//	    }
//
//	    public void surfaceDestroyed(SurfaceHolder holder) {
//	        // empty. Take care of releasing the Camera preview in your activity.
//	    	releaseCamera();
//	    }
//
//	    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
//	        // If your preview can change or rotate, take care of those events here.
//	        // Make sure to stop the preview before resizing or reformatting it.
//
//	        if (mHolder.getSurface() == null){
//	          // preview surface does not exist
//	          return;
//	        }
//
//	        // stop preview before making changes
//	        try {
//	        	
//	            mCamera.stopPreview();
//	            
//	        } catch (Exception e){
//	          // ignore: tried to stop a non-existent preview
//	        }
//
//	        // set preview size and make any resize, rotate or
//	        // reformatting changes here
//
//	        // start preview with new settings
//	        try {
//	           // mCamera.setPreviewDisplay(mHolder);
//	        	
//	        	setStartPreview();
//	            
//	            if(mstop){
//	            	
//	            	mCamera.stopPreview();
//	            	
//	            } else {
//	            	
//	            	 mCamera.startPreview();
//	            }
//	           
//
//	        } catch (Exception e){
//	            //DebugTools.getDebug().debug_v(TAG, "Error starting camera preview: " + e.getMessage());
//	        }
//	    }
//	}
	
	/** A basic Camera preview class */
	public class CameraPreview extends TextureView implements TextureView.SurfaceTextureListener{
		
	   // private SurfaceHolder mHolder;

	    public CameraPreview(Context context) {
	        super(context);
	        this.setSurfaceTextureListener(this);  
	    }

		@Override
		public void onSurfaceTextureAvailable(SurfaceTexture arg0, int arg1,
				int arg2) {
			// TODO Auto-generated method stub
			
			mHolder = arg0;
			setStartPreview();
	    	
	    	DebugTools.getDebug().debug_v(TAG, "onSurfaceTextureAvailable" );
		}

		@Override
		public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
			// TODO Auto-generated method stub
			//mCamera.stopPreview();
		    //mCamera.release();
			releaseCamera();
		    
		    DebugTools.getDebug().debug_v(TAG, "onSurfaceTextureDestroyed" );
			return false;
		}

		@Override
		public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int arg1,
				int arg2) {
			// TODO Auto-generated method stub
			
			 //DebugTools.getDebug().debug_v(TAG, "onSurfaceTextureSizeChanged" );
		}

		@Override
		public void onSurfaceTextureUpdated(SurfaceTexture arg0) {
			// TODO Auto-generated method stub
			
			 //DebugTools.getDebug().debug_v(TAG, "onSurfaceTextureUpdated" );
		}
	}
	
	 /**  
     * 设置camera显示取景画面,并预览  
     * @param camera  
     */  
    private void setStartPreview(){  
        try {  

	    	mCamera.setParameters(mCameraParas);
	    	
	    	/*顺时针旋转90du*/
	    	mCamera.setDisplayOrientation(90);
	    	
	    	try {
				mCamera.setPreviewTexture(mHolder);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    	mCamera.startPreview();
        	
            //camera.setPreviewTexture(holder);  
            //camera.startPreview();  
        } catch (Exception e) {  
           // Log.d(TAG, "Error starting camera preview: " + e.getMessage());  
        }  
    }  
	
	 /**  
     * 释放mCamera  
     */  
    private void releaseCamera() {  
        if (mCamera != null) {  
            mCamera.setPreviewCallback(null);  
            mCamera.stopPreview();// 停掉原来摄像头的预览  
            mCamera.release();  
            mCamera = null;  
        }  
    }  
	
	/*倒计时线程*/
	class CountingTimeThread extends Thread
	{
		public void run()
		{
			try {
					while(!mbIsCounting) {
						Thread.sleep(1000);
						miCountSecond++;
						
						Message msg = handler.obtainMessage();
						
						msg.what = Type_Counting_Int;
						msg.arg1 = miCountSecond;
						
						handler.sendMessage(msg);
						if (miCountSecond >= 5) {
							
							mbIsCounting = true;
						}
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	
	
	
	/*获取最后拍照的目录的路径*/
	private String getCurrentImagePath () {
		
		String result = "";
		
		File file1 = new File("/sdcard/igolfPictrue");
		
		if (file1.exists() && file1.isDirectory() ) {
			
			File[] files = file1.listFiles();
			
			int length1 = files.length;
			
			if (length1 > 0) {
				
				File file2  = new File(files[length1-1].getAbsolutePath());
				
				mCurrentPathStr = file2.getAbsolutePath();
				
				if (file2.exists() && file2.isDirectory()) {
					
					File[] files2 = file2.listFiles();
					
					int length2 = files2.length;
					
					if (length2 > 0) {
					
					
						File file3  = new File(files2[length2-1].getAbsolutePath());
						result = file3.getAbsolutePath();
					}
				}
			}
		}
		
		return result ;
	}

	
	/*保存图片线程*/
	class SavingPicThread extends Thread
	{
		public void run()
		{
			if (mQueueData == null || mQueueData.size() < 0) {
				
				return ;
			}
			
			/*从队列中去取出图片的数据保存*/
			long start = System.currentTimeMillis();
			DebugTools.getDebug().debug_v(TAG, "开始保存图片数据:::::---0-0-0-0-0-0-0-0-0-"+start);
			
			FileOutputStream fos = null;
		    Bitmap bitmap = null;
		     
		    Bitmap rotateBitmap ;
		    
		    BitmapFactory.Options option = new Options();
		    option.inSampleSize = 4;
			
			//Toast.makeText(CameraActivity.this, "开始保存图片数据", Toast.LENGTH_SHORT).show();
			for (int i =0 ; i <  mQueueData.size() ;i++) {
				
				if (mQueueData.get(i) == null) {
					
					return;
				}
				
				/*保存图片文件*/
				
				
				DebugTools.getDebug().debug_v(TAG, "循环内，，，，，，开始保存图片数据"+System.currentTimeMillis());
				
				pictureFile = FileTool.getInstance(CameraActivity.this).getInternalOutputMediaFile(0,mTimeStampStr);
				
		        /*д���ļ�*/
		      
		        try {
		        	
		        	/*data��ֵ����100*1024*/
		        	fos = new FileOutputStream(pictureFile);
		        	
			        //fos.write((byte[])mQueueData.get(i));
			        // fos.close();
			        
		        	if (Config.drr.size() < Config.SELECT_MAX_NUM) {
		        		
		        		Config.drr.add(pictureFile.getAbsolutePath());
		        	}
			       
		        	if (mQueueData.get(i).length > 2000000) {
		        		
		        		option.inSampleSize = 4;
		        	} else {
		        		
		        		option.inSampleSize = 2;
		        	}
	       		   bitmap = BitmapFactory.decodeByteArray(mQueueData.get(i), 0, mQueueData.get(i).length,option); 
	       		   
		        	//bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_gray);
	       		
	       		   //fos.write(mQueueData.get(i));
	       		   
	       		   Matrix matrix = new Matrix(); 
	       		   matrix.reset(); 
	       		   matrix.postRotate(90);

	       		   /*保存一张，就更新UI */
	       		   Message msg = handler.obtainMessage();
	       		   msg.what = Type_Saving_Count_Int;
	       		   msg.arg1 = i;
	       		   handler.sendMessage(msg);
	       		   
	       		   /*获取旋转的图片*/
	       		   
	       		  rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	       		  
	       		  if (rotateBitmap.getWidth() > 1280) {
	       			   
	       			rotateBitmap.compress(Bitmap.CompressFormat.JPEG, 20, fos);
	       			 
	       		   } else {
	       			   
	       			rotateBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
	       		   }
	       		   
		        		
		           DebugTools.getDebug().debug_v(TAG, "循环内，，，，，，结束保存图像数据"+System.currentTimeMillis());
		            
		        } catch (FileNotFoundException e) {
		        	
		        	//DebugTools.getDebug().debug_v(TAG, "File not found: " + e.getMessage());
		        	
		        } catch (IOException e) {
		        	
		        	//DebugTools.getDebug().debug_v(TAG, "Error accessing file: " + e.getMessage());
		        }
		        finally {
		        	
		        	try {
		        		
		        		//bitmap.recycle();
		        		//rotateBitmap.recycle();
						fos.close();
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        	if (bitmap != null) {
		        		//bitmap.recycle();
		        	}
		        	
		        }/*finally����*/
		        
		        
			}
			
			long end = System.currentTimeMillis();
			DebugTools.getDebug().debug_v(TAG, "保存图片数据耗时；"+(end-start));
			
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
		if (mCameraMoreList.getId() == arg0.getId()) {
			
			hideEditList();
			switch (arg2) {
			
			case 0:
				miCameraSleepTime = 50;
			break;
			case 1:
				miCameraSleepTime = 100;
			break;
			case 2:
				miCameraSleepTime = 150;
			break;
			
			}
		}
	}
	

	/*message handler*/
	Handler handler = new Handler()
	{
		 public void handleMessage(Message msg) 
		 {
				/*处理倒计时线程发送过来的消息*/
				if (msg.what == Type_Counting_Int) {
					
					mCountText.setText(String.valueOf(msg.arg1));
					
					if (msg.arg1 == 5) {
						
						mCountText.setText("GO!");
						mTakePictureImage.setEnabled(false);
						//mCountText.setVisibility(View.GONE);
						/*倒计时结束，开始拍照*/
						mCamera.takePicture(null, null, mPicture);
						mTimeStampStr = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date());
					}
				}
				
				/*处理保存线程发送过来的消息*/
				if (msg.what == Type_Saving_Count_Int) {
					
					Intent intent = new Intent(); //新建一个Intent，用来发广播  
					Uri uri = Uri.fromFile(pictureFile); //新建一个Uri，使用file的地址  
					intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE); //设置intent的action，这条action表示要将数据添加进系统的媒体资源库  
					intent.setData(uri); //指定存入资源库的数据，就是我们照片文件的地址  
					sendBroadcast(intent); //发布广播。使用MainActivity.this.sendBroadcast(intent);也可以  
					//sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
					DebugTools.getDebug().debug_v(TAG, "保存图片张数："+msg.arg1);
					
					mCountText.setText(R.string.str_loading_save_pictrue);
					
					switch (msg.arg1) {
					
//					case 0:
//						mCountText.setBackgroundResource(R.drawable.progress_1);
//					break;
//
//					case 1:
//						mCountText.setBackgroundResource(R.drawable.progress_2);
//					break;
//					case 2:
//						mCountText.setBackgroundResource(R.drawable.progress_3);
//					break;
//
//					case 3:
//						mCountText.setBackgroundResource(R.drawable.progress_4);
//					break;
//
//					case 4:
//						mCountText.setBackgroundResource(R.drawable.progress_5);
//					break;
//
//					case 5:
//						mCountText.setBackgroundResource(R.drawable.progress_6);
//					break;
//
//					case 6:
//						mCountText.setBackgroundResource(R.drawable.progress_7);
//					break;
//
//					case 7:
//						mCountText.setBackgroundResource(R.drawable.progress_8);
//					break;
//
//					case 8:
//						mCountText.setBackgroundResource(R.drawable.progress_9);
//					break;
					
					default :
					}
					
					
					if (msg.arg1 == (mTakePictureCount-1)) {
						
						mCountText.setVisibility(View.VISIBLE);
						mCountText.setText(R.string.str_image_picker_selected_done);
						
						//String currentImagePath = getCurrentImagePath();
						//mCurrentPathStr = "/sdcard/igolfPictrue"+ File.separator +mTimeStampStr;
						
						//mQueueData = null;
						
						//mTakePictureImage.setEnabled(true);
						
						startActivity(new Intent(CameraActivity.this, FriendMessageNewActivity.class));
						
						CameraActivity.this.finish();
					}
					
				}
		 }
	};
    
}
