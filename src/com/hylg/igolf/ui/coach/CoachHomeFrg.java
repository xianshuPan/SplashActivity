package com.hylg.igolf.ui.coach;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.CoachItem;
import com.hylg.igolf.cs.loader.AsyncImageLoader;
import com.hylg.igolf.cs.loader.GetLastAppCoachLoader;
import com.hylg.igolf.cs.loader.GetLastAppCoachLoader.GetLastAppCoachCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.Utils;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class CoachHomeFrg extends Fragment implements OnClickListener{
	
	private static final String 				TAG = "CoachHomeFrg";
	
	private ImageView 							mHobbyImage = null,
												mProfessionalImage = null;
	
	/*
	 * 业余教练和专业教练的价格
	 * */
	private TextView                            mHobbyPirceTxt = null,
												mProfessionalPriceTxt = null;
	
	private ImageView 							mCustomerImage = null;
	
	/*
	 * 最近约过的教练
	 * */
	private View                                mInvitedCoachLinear = null;
	protected ImageView 						avatarIv;
	protected ImageView 						sexIv;
	protected ImageView 						typeIv;

	protected TextView 							handicapiTv;
	protected TextView 							nicknameTv;
	private RatingBar                           star;
	protected TextView 							teachTimeTv;
	protected TextView 							ageTv;
	protected TextView 							specialTv;
	protected TextView 							distanceTv;
	protected TextView 							distanceTimeTv;
	
	
	private GetLastAppCoachLoader               mGetLastAppCoachLoader = null;
	
	private TextView                            mApplyCoachInfoTxt = null,mMyTeachingTxt= null;
	
	private long 								id = 0;

	private CoachItem                           coachItem = null;
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
		DebugTools.getDebug().debug_v(TAG, " --- onCreate");
		super.onCreate(savedInstanceState);
	}
    
    @Override
   	public void onViewCreated(View view,Bundle savedInstanceState) {
   		DebugTools.getDebug().debug_v(TAG, " --- onViewCreated");

   		super.onViewCreated(view, savedInstanceState);
   	}
  
    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){  
        super.onCreate(savedInstanceState);  
        
        View view = inflater.inflate(R.layout.coach_frg_home, null);
  
        initView(view);  
        
        return view;
    }  
    
    @Override
	public void onResume() {
		DebugTools.getDebug().debug_v(TAG, "onResume..");
		
		
		super.onResume();
	}
    
    @Override 
    public void onPause () {
    	
    	DebugTools.getDebug().debug_v(TAG, "onPause..");
    	
    	super.onPause();
    }
    
    /*
     * 初始化UI
     * */
    private void initView (View view) {
    	
    	mHobbyImage = (ImageView) view.findViewById(R.id.coach_frg_home_hobby_image);
		mProfessionalImage = (ImageView) view.findViewById(R.id.coach_frg_home_professional_image);
    	mHobbyPirceTxt = (TextView) view.findViewById(R.id.coach_frg_home_hobby_price_text);
		mProfessionalPriceTxt = (TextView) view.findViewById(R.id.coach_frg_home_professional_price_text);
		mInvitedCoachLinear =  view.findViewById(R.id.coach_frg_home_invited_coacher_linear);
		mInvitedCoachLinear.setOnClickListener(this);

		avatarIv = (ImageView) mInvitedCoachLinear.findViewById(R.id.coach_item_avatar);
		typeIv = (ImageView) mInvitedCoachLinear.findViewById(R.id.coach_item_type_image);
		handicapiTv = (TextView) mInvitedCoachLinear.findViewById(R.id.coach_item_handicapIndex_text);

		nicknameTv = (TextView) mInvitedCoachLinear.findViewById(R.id.coach_item_nickname);
		teachTimeTv = (TextView) mInvitedCoachLinear.findViewById(R.id.coach_item_teach_times_text);
		ageTv = (TextView) mInvitedCoachLinear.findViewById(R.id.coach_item_teach_years_text);
		specialTv = (TextView) mInvitedCoachLinear.findViewById(R.id.coach_item_special_text);
		distanceTv = (TextView) mInvitedCoachLinear.findViewById(R.id.coach_item_distance_text);
		distanceTimeTv = (TextView) mInvitedCoachLinear.findViewById(R.id.coach_item_distance_time_text);
		star = (RatingBar) mInvitedCoachLinear.findViewById(R.id.coach_item_rating);

		mApplyCoachInfoTxt = (TextView) view.findViewById(R.id.coach_frg_home_apply_coach_text);
		mMyTeachingTxt = (TextView) view.findViewById(R.id.coach_frg_home_my_teaching_text);
		
		mHobbyImage.setOnClickListener(this);
		mApplyCoachInfoTxt.setOnClickListener(this);
		mMyTeachingTxt.setOnClickListener(this);
		mProfessionalImage.setOnClickListener(this);
		id = MainApp.getInstance().getCustomer().id;

		mApplyCoachInfoTxt.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		
		/*
		 * 获取用户的最近约国的教练
		 * */
		mGetLastAppCoachLoader = new GetLastAppCoachLoader(getActivity(), id,MainApp.getInstance().getGlobalData().getLat(),
				MainApp.getInstance().getGlobalData().getLng(), new GetLastAppCoachCallback() {
			
			@Override
			public void callBack(int retId, String msg, CoachItem item) {
				// TODO Auto-generated method stub

				if (retId == BaseRequest.REQ_RET_OK) {

					coachItem = item;

					if (mGetLastAppCoachLoader.request.hobby_price > 0) {

						mHobbyPirceTxt.setText(String.valueOf(mGetLastAppCoachLoader.request.hobby_price)+getResources().getString(R.string.str_coach_price_unit));
					}

					if (mGetLastAppCoachLoader.request.professional_price > 0) {

						mProfessionalPriceTxt.setText(String.valueOf(mGetLastAppCoachLoader.request.professional_price)+getResources().getString(R.string.str_coach_price_unit));
					}


					if (coachItem == null || coachItem.sn == null || coachItem.sn.length() <= 0) {

						return;
					}

					mInvitedCoachLinear.setBackgroundResource(R.drawable.list_item_even_bkg);
					mInvitedCoachLinear.setVisibility(View.VISIBLE);
					if (item.type == Const.PROFESSIONAL_COACH) {

						typeIv.setBackgroundResource(R.drawable.professional);

					} else {

						typeIv.setBackgroundResource(R.drawable.white_bg);
					}

					handicapiTv.setText(Utils.getDoubleString(getActivity(), item.handicapIndex));
					nicknameTv.setText(item.nickname);
					teachTimeTv.setText(String.valueOf(item.teachTimes));
					ageTv.setText(String.valueOf(item.teachYear));
					specialTv.setText(item.special);
					star.setRating(item.rate);

					if (item.distance <= 1) {

						distanceTv.setText("附近");
					} else {

						distanceTv.setText(String.valueOf(item.distance)+"km");
					}

					distanceTimeTv.setText(Utils.handTime(item.distanceTime));

					loadAvatar(item.sn, item.avatar);

				}

			}
		});
		mGetLastAppCoachLoader.requestData();
		
//		mCustomerImage = (ImageView) view.findViewById(R.id.friend_frg_camera_customer_image);
//		mCustomerImage.setOnClickListener(this);
    }

	/*
	 * 加载头像
	 * */
	private void loadAvatar(String sn,String filename) {
		Drawable avatar = AsyncImageLoader.getInstance().getAvatar(getActivity(), sn, filename,
				(int) getResources().getDimension(R.dimen.avatar_detail_size));
		if(null != avatar) {
			avatarIv.setImageDrawable(avatar);
		} else {
			avatarIv.setImageResource(R.drawable.avatar_loading);
			AsyncImageLoader.getInstance().loadAvatar(getActivity(), sn, filename,
					new AsyncImageLoader.ImageCallback() {
						@Override
						public void imageLoaded(Drawable imageDrawable) {
							if(null != imageDrawable && null != avatarIv) {
								avatarIv.setImageDrawable(imageDrawable);
							}
						}
					});
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {

			
			case R.id.coach_frg_home_apply_coach_text:
			
				Intent intent1 = new Intent(getActivity(), CoachApplyInfoActivity.class);
				startActivity(intent1);
			
				break;
			
			case R.id.coach_frg_home_hobby_image:
					
				CoachListActivity.startCoachList(getActivity(), 1);
				break;
			
			case R.id.coach_frg_home_professional_image:
			
				CoachListActivity.startCoachList(getActivity(), 0);
				break;

			case R.id.coach_frg_home_invited_coacher_linear :

				CoachInfoDetailActivity.startCoachInfoDetail(getActivity(), coachItem);
				getActivity().overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);

				break;

			case R.id.coach_frg_home_my_teaching_text :

				Intent intent2 = new Intent(getActivity(), CoachMyTeachingActivity.class);
				startActivity(intent2);
				getActivity().overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);

				break;
		}
	}
    
}
