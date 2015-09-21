package com.hylg.igolf.ui.common;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hylg.igolf.R;
import com.hylg.igolf.cs.loader.AsyncImageLoader;
import com.hylg.igolf.cs.loader.AsyncImageLoader.ImageCallback;
import com.hylg.igolf.ui.view.photoview.PhotoViewAttacher;
import com.hylg.igolf.ui.view.photoview.PhotoViewAttacher.OnPhotoTapListener;
import com.hylg.igolf.utils.FileUtils;
import com.hylg.igolf.utils.Utils;

public class ScorcardDetailFragment extends Fragment {
	private final static String TAG = "ScorcardDetailFragment";

	private String mImageUrl;
	private ImageView mImageView;
	private ProgressBar progressBar;
	private String appSn, userSn;
	private PhotoViewAttacher mAttacher;

	public static ScorcardDetailFragment newInstance(String imageUrl, String appSn, String userSn) {
		final ScorcardDetailFragment f = new ScorcardDetailFragment();

		final Bundle args = new Bundle();
		args.putString("url", imageUrl);
		args.putString("appSn", appSn);
		args.putString("userSn", userSn);
		f.setArguments(args);

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments() != null) {
			mImageUrl = getArguments().getString("url");
			userSn = getArguments().getString("userSn");
			appSn = getArguments().getString("appSn");
		} else {
			if(Utils.LOG_H) {
				throw new java.lang.IllegalArgumentException("ImageDetailFragment parameter invalide !");
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
		mImageView = (ImageView) v.findViewById(R.id.image);
		progressBar = (ProgressBar) v.findViewById(R.id.loading);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if(FileUtils.getCusPicName(mImageUrl).isEmpty()) {
			Utils.logh(TAG, " >>>>>> is empty");
			if(Utils.LOG_H) {
				Toast.makeText(getActivity(), "sever appSn is null !", Toast.LENGTH_SHORT).show();
			}
			return ;
		}
		Drawable scoreCard = AsyncImageLoader.getInstance().getScorecardDetail(getActivity(), appSn, userSn, mImageUrl);
		Utils.logh(TAG, "----++++----" + mImageUrl + " scoreCard: " + scoreCard);
		if(null != scoreCard) {
			Utils.setVisibleGone(mImageView, progressBar);
			mImageView.setImageDrawable(scoreCard);
			createAttacher();
		} else { // 不存在，加载
			Utils.setVisible(progressBar);
			// 正在加载，延时获取
			if(AsyncImageLoader.getInstance().isScorecardLoading(getActivity(), appSn, userSn, mImageUrl)) {
				Utils.logh(TAG, "loading .....");
				handler.sendEmptyMessageDelayed(MSG_RELOAD, RELOAD_DELAY);
			} else {
				// 异步加载
				AsyncImageLoader.getInstance().loadScorecard(getActivity(), appSn, userSn, mImageUrl, 
						new ImageCallback() {
							@Override
							public void imageLoaded(Drawable imageDrawable) {
								Utils.logh(TAG, "----loadScorecard imageLoaded---- mImageView " + mImageView + " imageDrawable: " + imageDrawable);
								Utils.setVisibleGone(mImageView, progressBar);
								if(null != imageDrawable) {
									if(null != mImageView) {
										mImageView.setImageDrawable(imageDrawable);
										createAttacher();
									}
								} else {
									Toast.makeText(getActivity(), R.string.str_toast_image_load_fail, Toast.LENGTH_SHORT).show();
								}
								if(null != handler && handler.hasMessages(MSG_RELOAD)) {
									handler.removeMessages(MSG_RELOAD);
								}
							}
					});
			}
		}
		
	}
	
	private final static int MSG_RELOAD = 1; // 正在加载，延时获取，避免加载过程中退出，进入，回来无法显示
	private final static long RELOAD_DELAY = 3 * 1000;
	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			if(msg.what == MSG_RELOAD) {
				if(AsyncImageLoader.getInstance().isScorecardLoading(getActivity(), appSn, userSn, mImageUrl)) {
					// 仍在加载，继续等待
					handler.sendEmptyMessageDelayed(MSG_RELOAD, RELOAD_DELAY);
					Utils.logh(TAG, "MSG_RELOAD loading .....");
				} else {
					if(null != handler && handler.hasMessages(MSG_RELOAD)) {
						handler.removeMessages(MSG_RELOAD);
					}
					// 加载完成，获取加载信息
					Drawable album = AsyncImageLoader.getInstance().getScorecardDetail(getActivity(), appSn, userSn, mImageUrl);
					Utils.setVisibleGone(mImageView, progressBar);
					Utils.logh(TAG, "MSG_RELOAD over .....");
					if(null != album) {
						mImageView.setImageDrawable(album);
						createAttacher();
					} else {
						// 加载失败
					}
				}
			}
			return false;
		}
	});
	
	/**
	 * 创建Attacher，成功后才创建，避免加载后，小图显示到左上角
	 */
	private void createAttacher() {
		mAttacher = new PhotoViewAttacher(mImageView);
		mAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {
			@Override
			public void onPhotoTap(View arg0, float arg1, float arg2) {
				getActivity().finish();
			}
		});
	}
}
