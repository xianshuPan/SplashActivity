package com.hylg.igolf.ui.member;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Browser;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;

import com.hylg.igolf.R;
import com.hylg.igolf.ui.customer.ViewInject;
import com.hylg.igolf.ui.friend.publish.WeiBoUser;


/**
 * 用户搜索界面
 * 
 * @author wangdan
 *
 */
public class UserProfileActivity extends FragmentActivity {

	public static void launch(FragmentActivity from, String screenName) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("org.aisen.weibo.sina.userinfo://@%s", screenName)));
        intent.putExtra(Browser.EXTRA_APPLICATION_ID, from.getPackageName());
        from.startActivity(intent);
	}

    public static void launch(FragmentActivity from, WeiBoUser user) {
        Intent intent = new Intent(from, UserProfileActivity.class);
        intent.putExtra("user", user);
        from.startActivity(intent);
    }
	
	@ViewInject(id = R.id.layContent)
	View layoutContent;



    @ViewInject(id = R.id.viewToolbar)
	View viewToolbar;
	
	private boolean searchFailed = false;
	
	private String screenName;

    private WeiBoUser mUser;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
       // AisenUtils.setStatusBar(this);

		super.onCreate(savedInstanceState);

		setContentView(R.layout.as_ui_profile_activity);

       // getSupportActionBar().setDisplayShowHomeEnabled(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
//            layToolbar.setPadding(layToolbar.getPaddingLeft(),
//                                        layToolbar.getPaddingTop() + SystemBarUtils.getStatusBarHeight(this),
//                                        layToolbar.getPaddingRight(),
//                                        layToolbar.getPaddingBottom());
//
//            viewToolbar.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
//                    getResources().getDimensionPixelSize(R.dimen.abc_action_bar_default_height_material) + SystemBarUtils.getStatusBarHeight(this)));
        }

        if (savedInstanceState == null && getIntent() != null) {
            mUser = (WeiBoUser) getIntent().getSerializableExtra("user");
        }
        else {
            if (savedInstanceState != null)
                mUser = (WeiBoUser) savedInstanceState.getSerializable("user");
        }

//        if (mUser != null) {
//           getSupportFragmentManager().beginTransaction()
//                    .add(R.id.layContent, UserProfilePagerFragment.newInstance(mUser), "user_profile")
//                    .commit();
//            return;
//        }

//		searchFailed = savedInstanceState == null ? false : savedInstanceState.getBoolean("searchFailed");
//		screenName = savedInstanceState == null ? null : savedInstanceState.getString("screenName");
//
//		//txtLoadFailed.setText(R.string.error_pic_load_faild);
//
//		if (savedInstanceState == null) {
//			//getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            //getSupportActionBar().setTitle(R.string.title_user_profile);
//
//			Uri data = getIntent().getData();
//			if (data != null) {
//				String d = data.toString();
//				int index = d.lastIndexOf("/");
//				String userName = d.substring(index + 1);
//				if (userName.indexOf("@") == 0)
//					userName = userName.substring(1);
//
//				screenName = userName;
//
//				//reload(null);
//			} else {
//				finish();
//				return;
//			}
//		}
//
//		if (searchFailed) {
//			//reload(null);
//		}


	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putString("screenName", screenName);
		outState.putBoolean("searchFailed", searchFailed);
	}

	@Override
	protected void onResume () {


//		if (mUser != null) {
//			getSupportFragmentManager().beginTransaction()
//					.add(R.id.layContent, UserProfilePagerFragment.newInstance(mUser), "user_profile")
//					.commit();
//			return;
//		}
		super.onResume();

		if (mUser != null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.layContent, UserProfilePagerFragment.newInstance(mUser), "user_profile")
					.commit();
			return;
		}
	}

}
