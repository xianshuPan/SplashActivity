package com.hylg.igolf.imagepicker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.R;
import com.hylg.igolf.imagepicker.ImageGridAdapter.TextCallback;
import com.hylg.igolf.ui.friend.CameraActivity;
import com.hylg.igolf.ui.friend.FriendMessageNewActivity;
import com.hylg.igolf.utils.FileTool;
import com.xc.lib.activity.BaseActivity;
import com.xc.lib.xutils.bitmap.callback.BitmapLoadFrom;

public class ImageGridActivity extends BaseActivity implements OnClickListener,OnItemClickListener {
	
	public static final String TAG = "ImageGridActivity";
	
	public static final String EXTRA_IMAGE_LIST = "imagelist";

	private List<ImageItem> dataList;
	private GridView gridView;
	private ImageGridAdapter adapter;
	private AlbumHelper helper;
	private TextView okButton,mSelectedImageBucketNameTxt;
	
	private List<ImageBucket> photoList;
	
	
	private PopupWindow	mPhotoPop   = null;
	private ListView    mPhotoList  = null;
	
	private ImageBucketAdapter phptoAdapter;
	
	private RelativeLayout mSelectPhotoRelative;
	
	private boolean mIsEditListShowing = false;
	
	private ImageView	mBackImage = null;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeText(ImageGridActivity.this,
						"最多选择" + Config.SELECT_MAX_NUM + "张图片", Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//helper = AlbumHelper.getHelper();
		
		helper = new AlbumHelper();
		helper.init(getApplicationContext());
		
		photoList = helper.getImagesBucketList(true);

		//dataList = (List<ImageItem>) getIntent().getSerializableExtra(EXTRA_IMAGE_LIST);
		
		initViews();
		
		for(int i = 0 ; i < photoList.size(); i++) {
			
			ImageBucket temp = photoList.get(i);
			
			if (temp.bucketName.equalsIgnoreCase(Config.ALL_IMAGE)) {
				
				dataList = temp.imageList;
				
				mSelectedImageBucketNameTxt.setText(temp.bucketName);
				
				adapter = new ImageGridAdapter(this, dataList, mHandler,temp.bucketName);
				gridView.setAdapter(adapter);
			}
		}
		
		//copyCamera1() ;
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		//helper = AlbumHelper.getHelper();
		
		photoList = helper.getImagesBucketList(true);

		//dataList = (List<ImageItem>) getIntent().getSerializableExtra(EXTRA_IMAGE_LIST);
		for(int i = 0 ; i < photoList.size(); i++) {
			
			ImageBucket temp = photoList.get(i);
			
			if (temp.bucketName.equalsIgnoreCase(Config.ALL_IMAGE)) {
				
				mSelectedImageBucketNameTxt.setText(temp.bucketName);
				dataList = temp.imageList;
				
				adapter = new ImageGridAdapter(this, dataList, mHandler,temp.bucketName);
				gridView.setAdapter(adapter);
			}
		}
		
		phptoAdapter.notifyDataSetChanged();

	}
	
	

	private void initViews() {
		setContentView(R.layout.image_picker_ac_home);
		
		mBackImage = getRateView(R.id.image_picker_head_back, true);
		mBackImage.setOnClickListener(this);
		
		okButton = getRateView(R.id.image_picker_done_text, true);
		okButton.setOnClickListener(this);
		
		mSelectedImageBucketNameTxt = getRateView(R.id.image_picker_photo_select_text, true);
		
		phptoAdapter = new ImageBucketAdapter(this, photoList);
		
		gridView = getRateView(R.id.gridview, true);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
		adapter = new ImageGridAdapter(this, dataList, mHandler,photoList.get(0).bucketName);
		gridView.setAdapter(adapter);
		
		adapter.setTextCallback(new TextCallback() {
			public void onListen(int count) {
				// int num = 9 - adapter.map.values().size();
				okButton.setText("完成" + "(" + count + " / " + Config.SELECT_MAX
						+ ")");
			}
		});
		
		mSelectPhotoRelative =  getRateView(R.id.image_picker_photo_select_relative, true);
		mSelectPhotoRelative.setOnClickListener(this);
		
		//initPopupWindow();
		
		mPhotoList = getRateView(R.id.image_picker_photo_List, true);
		mPhotoList.setAdapter(phptoAdapter);
		mPhotoList.setOnItemClickListener(this);

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
		/*animation.setAnimationListener(new AnimationListener() {			
			@Override
			public void onAnimationStart(Animation animation) {
				mEditListView.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// Do nothing
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// Do nothing
			}
		});*/
		animation.setDuration(500);
		mPhotoList.startAnimation(animation);

		//This is needed because some older android
		//versions contain a bug where AnimationListeners
		//are not called in Animations
		mPhotoList.setVisibility(View.VISIBLE);
		
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
				mPhotoList.setVisibility(View.INVISIBLE);
			}
		});
		animation.setDuration(500);
		mPhotoList.startAnimation(animation);
		
		mIsEditListShowing = false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		case R.id.image_picker_head_back :
			
			this.finish();
			
			break;
		
		case R.id.image_picker_done_text:
//			ArrayList<String> list = new ArrayList<String>();
//			Collection<String> c = adapter.map.values();
//			Iterator<String> it = c.iterator();
//			for (; it.hasNext();) {
//				list.add(it.next());
//			}
//
//			for (int i = 0; i < list.size(); i++) {
//				if (Config.drr.size() < Config.SELECT_MAX_NUM) {
//					Config.drr.add(list.get(i));
//				}
//			}
			
			startActivity( new Intent(this, FriendMessageNewActivity.class));
			
			
			finish();
			break;
			
		case R.id.image_picker_photo_select_relative:
			
			DebugTools.getDebug().debug_v(TAG, "image_picker_photo_select_relative........cliked");
			//showPopupWindow();
			if (mIsEditListShowing) {
				hideEditList();
			} else {
				showEditList();
			}
			
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if (arg0.getId() == mPhotoList.getId()) {
			
			dataList = photoList.get(arg2).imageList;
			adapter = new ImageGridAdapter(this, dataList, mHandler,photoList.get(arg2).bucketName);
			gridView.setAdapter(adapter);
			
			mSelectedImageBucketNameTxt.setText(photoList.get(arg2).bucketName);
			hideEditList();
		}
		
	}
		
	@Override
	protected void onActivityResult (int requestCode,int resultCode,Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_OK) {
			
			if (requestCode == 101) {
				
				Bitmap bitmap;
				
				String mTimeStampStr = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date());
				File pictrueFile = FileTool.getInstance(this).getInternalOutputMediaFile(0,mTimeStampStr);
				
//				if (data != null && data.getData() != null) {
//					
//					Uri uri = data.getData();
//					
//					try {
//						bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//					} catch (FileNotFoundException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					
//					
//				} else if (data != null && data.getExtras() != null) {
//					
//					
//					Bundle data1 = data.getExtras();
//					
//					bitmap = (Bitmap)data1.get("data");
//				}
				
				if (Config.mTakePictrueFile != null) {
					
					bitmap = BitmapFactory.decodeFile(Config.mTakePictrueFile.getAbsolutePath());
				} else {
					
					return;
				}
				

				if (bitmap != null && pictrueFile != null) {
					
					FileOutputStream fos;
					try {
						fos = new FileOutputStream(pictrueFile);
						
//						int bitMapCount = bitmap.getByteCount();
//
//						if (bitMapCount < 200*1024) {
//
//							//fos.write(bitmap.);
//							bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//
//						} else if (bitMapCount > 200*1024 && bitMapCount  < 1000*1024) {
//
//							bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
//
//						} else if (bitMapCount  > 1000*1024 && bitMapCount < 3000*1024) {
//
//							bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
//
//						} else if (bitMapCount  > 3000*1024) {
//
//							bitmap.compress(Bitmap.CompressFormat.JPEG, 20, fos);
//						}


						ByteArrayOutputStream out = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);

						float zoom = (float)Math.sqrt(64 * 1024 / (float)out.toByteArray().length);

						Matrix matrix = new Matrix();
						matrix.setScale(zoom, zoom);

						Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

						result.compress(Bitmap.CompressFormat.JPEG, 85, out);

						while(out.toByteArray().length > 64 * 1024){
							System.out.println(out.toByteArray().length);
							matrix.setScale(0.9f, 0.9f);
							result = Bitmap.createBitmap(result, 0, 0, result.getWidth(), result.getHeight(), matrix, true);
							out.reset();
							result.compress(Bitmap.CompressFormat.JPEG, 85, out);
						}

						out.writeTo(fos);
						
						if (Config.drr.size() < Config.SELECT_MAX_NUM) {
			        		
			        		Config.drr.add(pictrueFile.getAbsolutePath());
			        	}
						
						startActivity(new Intent(this, FriendMessageNewActivity.class));
						
						Config.mTakePictrueFile.delete();
						Config.mTakePictrueFile = null;
						
						this.finish();
						
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {

						e.printStackTrace();
					}


				}

				try {
					MediaStore.Images.Media.insertImage(this.getContentResolver(),
							bitmap, pictrueFile.getName(),
                            pictrueFile.getName());
				} catch (Exception e) {
					e.printStackTrace();
				}
				Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				Uri uri = Uri.fromFile(pictrueFile);
				intent.setData(uri);
				this.sendBroadcast(intent);

				//sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
			}
		}

		
	}
}
