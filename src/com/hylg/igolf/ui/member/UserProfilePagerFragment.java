package com.hylg.igolf.ui.member;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.ui.customer.ViewInject;
import com.hylg.igolf.ui.friend.FriendAttentionFrg;
import com.hylg.igolf.ui.friend.FriendHotFrg;
import com.hylg.igolf.ui.friend.FriendLocalFrg;
import com.hylg.igolf.ui.friend.FriendNewFrg;
import com.hylg.igolf.ui.friend.publish.WeiBoUser;
import com.hylg.igolf.ui.view.PagerSlidingTabStrip;
import com.hylg.igolf.ui.view.ZoomOutPageTransformer;
import com.hylg.igolf.ui.widget.ProfileScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户资料Pager<br/>
 * tab1:详情<br/>
 * tab2:微博<br/>
 * tab3:原创微博<br/>
 * tab4:相册<br/>
 *
 * Created by wangdan on 15-2-10.
 */
public class UserProfilePagerFragment extends Fragment implements View.OnClickListener
                                                      {

    public static void launch(FragmentActivity from, WeiBoUser user) {
        UserProfileActivity.launch(from, user);
    }

    public static Fragment newInstance() {
        UserProfilePagerFragment fragment = new UserProfilePagerFragment();

        Bundle args = new Bundle();
        args.putSerializable("user", MainApp.getInstance().getUser());
        fragment.setArguments(args);

        return fragment;
    }

    public static Fragment newInstance(WeiBoUser searchResult) {
        UserProfilePagerFragment fragment = new UserProfilePagerFragment();

        Bundle args = new Bundle();
        args.putSerializable("user", searchResult);
        args.putInt("profile_index", 1);
        fragment.setArguments(args);

        return fragment;
    }


    @ViewInject(id = R.id.rootScrolView)
    ProfileScrollView rootScrolView;
    // 封面
    @ViewInject(id = R.id.imgCover)
    ImageView imgCover;
    @ViewInject(id = R.id.txtName)
    // 名字
            TextView txtName;
    // 头像
    @ViewInject(id = R.id.imgPhoto)
    ImageView imgPhoto;
    // 认证类别
    @ViewInject(id = R.id.imgVerified)
    ImageView imgVerified;
    // 性别
    @ViewInject(id = R.id.imgGender)
    ImageView imgGender;
    // 粉丝数
    @ViewInject(id = R.id.txtFollowersCounter, click = "onClick")
    TextView txtFollowersCounter;
    // 关注数
    @ViewInject(id = R.id.txtFriendsCounter, click = "onClick")
    TextView txtFriendsCounter;
    // 简介fv
    @ViewInject(id = R.id.txtDesc)
    TextView txtDesc;

    @ViewInject(id = R.id.slidingTabs)
    private PagerSlidingTabStrip mTabsIndicater;

    @ViewInject(id = R.id.pager)
    private ViewPager viewPager;

    private WeiBoUser mUserBean;

    private List<android.support.v4.app.Fragment> fragmentList;

     @Override
    public void onCreate(Bundle savedInstanceState) {
        mUserBean = savedInstanceState == null ? (WeiBoUser) getArguments().getSerializable("user")
                                              : (WeiBoUser) savedInstanceState.getSerializable("user");


        super.onCreate(savedInstanceState);
    }

     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
         super.onCreate(savedInstanceState);

         View view = inflater.inflate(R.layout.as_ui_profile_pager, null);
         //setContentView(R.layout.friend_frg_home1);

         initTabHost(view);

         initViewPager(view);

         return view;
     }

     private void initTabHost(View view) {
         fragmentList = new ArrayList<android.support.v4.app.Fragment>();


         FriendHotFrg friendHot = new FriendHotFrg();

         FriendNewFrg friendNewFrg = new FriendNewFrg();
         friendNewFrg.setTabHost(viewPager);
         fragmentList.add(friendNewFrg);
         fragmentList.add(friendHot);
         fragmentList.add(new FriendLocalFrg());
         fragmentList.add(new FriendAttentionFrg());

     }

      private void initViewPager(View view) {

          mTabsIndicater = (PagerSlidingTabStrip)view.findViewById(R.id.slidingTabs);

          viewPager = (ViewPager) view.findViewById(R.id.pager);
          viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

              @Override
              public void onPageScrollStateChanged(int arg0) {
                  // TODO Auto-generated method stub

              }

              @Override
              public void onPageScrolled(int arg0, float arg1, int arg2) {
                  // TODO Auto-generated method stub

              }

              @Override
              public void onPageSelected(int arg0) {

                  //tabHost.setCurrentTab(arg0);
              }

          });

          viewPager.setAdapter(new FragmentViewPagerAdapter(getFragmentManager()));
          viewPager.setPageTransformer(true, new ZoomOutPageTransformer());

          mTabsIndicater.setViewPager(viewPager);

          ((FriendNewFrg)fragmentList.get(0)).setTabHost(viewPager);

          setTabsValue();
      }


      @Override
        public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("user", mUserBean);
        }

         public class FragmentViewPagerAdapter extends FragmentPagerAdapter {

             private final String[] TITLES = { "最新", "热门","本地","关注"};

             public FragmentViewPagerAdapter(FragmentManager fm) {
                 super(fm);
                                                                  // TODO Auto-generated constructor stub
             }

             @Override
             public CharSequence getPageTitle(int position) {
                 return TITLES[position];
             }

             @Override
             public android.support.v4.app.Fragment getItem(int arg0) {
                 // TODO Auto-generated method stub
                 return fragmentList.get(arg0);
             }

             @Override
             public int getCount() {
                 // TODO Auto-generated method stub
                 return fragmentList.size();
             }

         }


       private void setProfile() {
        // 封面
//        ImageConfig coverConfig = new ImageConfig();
//        coverConfig.setLoadfaildRes(R.drawable.bg_banner_dialog);
//        coverConfig.setLoadingRes(R.drawable.bg_banner_dialog);
//        coverConfig.setDisplayer(new DefaultDisplayer());
//        BitmapLoader.getInstance().display(this, mUserBean.getCover_image_phone(), imgCover, coverConfig);
//        // 名字
//        // fuck 2014-09-04 当名字过长大于8个字时，截取部分文字
//        int maxLength = AisenUtils.getStrLength("一二三四五六七八九十");
//        if (AisenUtils.getStrLength(mUserBean.getName()) > maxLength) {
//            StringBuffer sb = new StringBuffer();
//            int index = 0;
//            while (AisenUtils.getStrLength(sb.toString()) < maxLength) {
//                if (index >= mUserBean.getName().length())
//                    break;
//
//                sb.append(mUserBean.getName().charAt(index));
//                index++;
//            }
//            sb.append("...");
//            txtName.setText(sb.toString());
//        }
//        else {
//            txtName.setText(mUserBean.getScreen_name());
//        }
//        // 头像
//        //BitmapLoader.getInstance().display(this, AisenUtils.getUserPhoto(mUserBean), imgPhoto, ImageConfigUtils.getLargePhotoConfig());
//        // 性别
//        imgGender.setVisibility(View.VISIBLE);
//        if ("m".equals(mUserBean.getGender()))
//            imgGender.setImageResource(R.drawable.man);
//        else if ("f".equals(mUserBean.getGender()))
//            imgGender.setImageResource(R.drawable.woman);
//        else
//            imgGender.setVisibility(View.GONE);
//        // 认证
//        //AisenUtils.setImageVerified(imgVerified, mUserBean);
//        // 关注数
//        txtFriendsCounter.setText(String.format(getString(R.string.profile_friends), AisenUtils.getCounter(mUserBean.getFriends_count())));
//        // 粉丝数
//        txtFollowersCounter.setText(String.format(getString(R.string.profile_followers), AisenUtils.getCounter(mUserBean.getFollowers_count())));
//        // 简介
//        txtDesc.setText(mUserBean.getDescription());
//        // 简介
//        if (!TextUtils.isEmpty(mUserBean.getDescription()))
//            txtDesc.setText(mUserBean.getDescription());
//        else
//            txtDesc.setText(getString(R.string.profile_des_none));
    }
     private void setTabsValue() {

        DisplayMetrics dm = getResources().getDisplayMetrics();

         // 设置Tab是自动填充满屏幕的
         mTabsIndicater.setShouldExpand(true);
         // 设置Tab的分割线是透明的
         mTabsIndicater.setDividerColor(Color.TRANSPARENT);
         // 设置Tab底部线的高度
         mTabsIndicater.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, dm));
         // 设置Tab Indicator的高度
         mTabsIndicater.setIndicatorHeight((int) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 3, dm));
         // 设置Tab标题文字的大小
         mTabsIndicater.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, dm));
         mTabsIndicater.setTextColor(getResources().getColor(R.color.color_friend_item_praiser_name));
         // 设置Tab Indicator的颜色
         //mTabsIndicater.setIndicatorColor(Color.parseColor("#000"));
         // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
         //mTabsIndicater.setSelectedTextColor(Color.parseColor("#45c01a"));
         // 取消点击Tab时的背景色
         mTabsIndicater.setTabBackground(0);
     }

                                                          @Override
    public void onClick(View v) {
        // 关注
        if (v.getId() == R.id.txtFriendsCounter) {
           // FriendshipTabsFragment.launch(getActivity(), mUserBean, 0);
        }
        // 粉丝
        else if (v.getId() == R.id.txtFollowersCounter) {

            //FriendshipTabsFragment.launch(getActivity(), mUserBean, 1);
        }
    }




    Handler mHandler = new Handler();

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        //inflater.inflate(R.menu.menu_profile_pager, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

//        MenuItem createItem = menu.findItem(R.id.create);
//        MenuItem destoryItem = menu.findItem(R.id.destory);
//        MenuItem followerDestoryItem = menu.findItem(R.id.followDestory);
//
//        if (mUserBean == null || AppContext.getUser().getIdstr().equals(mUserBean.getIdstr())) {
//            createItem.setVisible(false);
//            destoryItem.setVisible(false);
//            followerDestoryItem.setVisible(false);
//        }
//        else {
//            createItem.setVisible(mFriendship != null && !mFriendship.getSource().getFollowing());
//            destoryItem.setVisible(mFriendship != null && mFriendship.getSource().getFollowing());
//            followerDestoryItem.setVisible(mFriendship != null && mFriendship.getTarget().getFollowing());
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 关注
//        if (item.getItemId() == R.id.create) {
//            BizFragment.getBizFragment(this).createFriendship(mUserBean, this);
//        }
//        // 取消关注
//        else if (item.getItemId() == R.id.destory) {
//            BizFragment.getBizFragment(this).destoryFriendship(mUserBean, this);
//        }
//        // 移除粉丝
//        else if (item.getItemId() == R.id.followDestory) {
//            BizFragment.getBizFragment(this).destoryFollower(mUserBean, this);
//        }

        return super.onOptionsItemSelected(item);
    }




    public interface IUserProfileRefresh {

        public void refreshProfile();

    }

}
