package com.hylg.igolf.ui.widget;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.ui.member.MemDetailActivityNew;
import com.hylg.igolf.utils.DownLoadImageTool;
import com.hylg.igolf.utils.SharedPref;
import com.hylg.igolf.utils.Utils;

public abstract class IgBaseAdapter extends BaseAdapter {

	protected boolean isAvatarClickable = true ;

	/**
	 * 获取默认大小头像
	 * @param context
	 * @param sn
	 * @param name
	 * @param iv
	 */
	protected void loadAvatar(Activity context, String sn, String name, ImageView iv) {
		loadAvatar(context, sn, name, iv, false, (int) context.getResources().getDimension(R.dimen.avatar_detail_size));
	}

	protected void loadAvatar(Activity context, String sn, ImageView iv) {
		loadAvatar(context, sn, sn+".jpg", iv, false, (int) context.getResources().getDimension(R.dimen.avatar_detail_size));
	}

	public void setIsAvatarClickable (boolean clickable) {

		isAvatarClickable = clickable;
	}
	
	/**
	 * 获取设置size大小头像
	 * @param context
	 * @param sn
	 * @param name
	 * @param iv
	 * @param size
	 */
	protected void loadAvatar(Activity context, String sn, String name, ImageView iv, int size) {
		loadAvatar(context, sn, name, iv, false, size);
	}
	
	protected void loadAvatar(final Activity context, final String sn, String name, final ImageView iv, boolean myInvite, int size) {
		// 头像存在，且不是自己的头像，设置点击查看详情
		if(null != name && !name.isEmpty() && !sn.equals(MainApp.getInstance().getCustomer().sn)) {
			iv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					if (isAvatarClickable) {

						MemDetailActivityNew.startMemDetailActivity(context, sn);
						context.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
					}

				}
			});
		} else {
			iv.setOnClickListener(null);
			// 在我的约球列表中，我发起的大厅约球，没有申请时，返回了自己的信息，应显示默认图标
			if(myInvite) {
				iv.setImageResource(R.drawable.avatar_no_golfer);
				return ;
			}
		}
		//Drawable avatar = AsyncImageLoader.getInstance().getAvatar(context, sn, name, size);

		if (sn.equals(MainApp.getInstance().getCustomer().sn)) {

			String prefAvatar = SharedPref.getString(SharedPref.SPK_AVATAR, context);
			//avatar = AsyncImageLoader.getInstance().getAvatar(context, sn, prefAvatar, size);
		}

//		if(null != avatar) {
//			iv.setImageDrawable(avatar);
//		} else {
//			iv.setImageResource(R.drawable.avatar_loading);
//			AsyncImageLoader.getInstance().loadAvatar(context, sn, name,
//					new ImageCallback() {
//						@Override
//						public void imageLoaded(Drawable imageDrawable) {
//							if(null != imageDrawable && null != iv) {
//								iv.setImageDrawable(imageDrawable);
//								notifyDataSetChanged();
//							}
//						}
//				});
//		}

		DownLoadImageTool.getInstance(context).displayImage(Utils.getAvatarURLString(sn),iv,null);

	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
}
