package com.hylg.igolf.ui.coach;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.CoachItem;
import com.hylg.igolf.cs.loader.GetLastAppCoachLoader;
import com.hylg.igolf.cs.loader.GetLastAppCoachLoader.GetLastAppCoachCallback;
import com.hylg.igolf.ui.customer.CustomerHomeActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
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
	
	
	private GetLastAppCoachLoader               mGetLastAppCoachLoader = null;
	
	private TextView                            mApplyCoachInfoTxt = null;
	
	private long 								id = 0;
    
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
		
		mApplyCoachInfoTxt = (TextView) view.findViewById(R.id.coach_frg_home_apply_coach_text);
		
		mHobbyImage.setOnClickListener(this);
		mApplyCoachInfoTxt.setOnClickListener(this);
		mProfessionalImage.setOnClickListener(this);
		id = MainApp.getInstance().getCustomer().id;
		
		/*
		 * 获取用户的最近约国的教练
		 * */
		mGetLastAppCoachLoader = new GetLastAppCoachLoader(getActivity(), id, 34.11, 104.11, new GetLastAppCoachCallback() {
			
			@Override
			public void callBack(int retId, String msg, CoachItem item) {
				// TODO Auto-generated method stub
				
			}
		});
		mGetLastAppCoachLoader.requestData();
		
		mCustomerImage = (ImageView) view.findViewById(R.id.friend_frg_camera_customer_image); 
		mCustomerImage.setOnClickListener(this);
    }

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		
		case R.id.friend_frg_camera_customer_image:
			
			Intent intent = new Intent(getActivity(), CustomerHomeActivity.class);
			startActivity(intent);
			break;
			
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
		}
	}
    
}
