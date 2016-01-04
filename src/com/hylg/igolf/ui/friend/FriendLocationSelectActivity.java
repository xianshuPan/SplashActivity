package com.hylg.igolf.ui.friend;

import java.util.ArrayList;
import java.util.List;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.overlay.PoiOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import com.hylg.igolf.DebugTools;
import com.hylg.igolf.R;
import com.hylg.igolf.imagepicker.Config;
import com.hylg.igolf.ui.view.EditTextWithDeleteButton;
import com.hylg.igolf.ui.view.EditTextWithDeleteButton.TextChangedListener;
import com.hylg.igolf.ui.view.ListviewBottomRefresh;
import com.hylg.igolf.ui.view.ListviewBottomRefresh.OnRefreshListener;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FriendLocationSelectActivity extends Activity implements  
															LocationSource,
															AMapLocationListener,
															TextChangedListener,
															OnPoiSearchListener,
															OnRefreshListener,
															OnItemClickListener,
															OnClickListener{
	
	
	private final String 						TAG 					= "FriendMessageNewActivity";
	
	/*后退和确定按钮*/
	private ImageView 							mBackImage 				= null;
	
	/*输入搜索关键字*/
	private  EditTextWithDeleteButton  			mContentsEdit 			= null;
	
	/*显示当前位置*/
	private TextView                            mCurrentLoctNameTxt     = null,
												mCurrentLoctAddrTxt     = null;
	
	private ListviewBottomRefresh               mLocationList           = null;
	private poiResultAdapter                    mAdapter                = null;
	
	
	/*高德地图相关控件*/
	private MapView 							mapView					= null;
    private AMap 								aMap					= null;
    private OnLocationChangedListener 			mListener;
	private LocationManagerProxy 				mAMapLocationManager;
	private LatLonPoint							mLocationPoint          = null;
	
	
	/*定位获取到地址信息*/
	private String 								mLocationCityStr        = null,
												mLocationProvinceStr    = null,
												mLocationRegionStr      = null,
												mLocationAliasStr       = null,
	                                            mLocationAddrStr        = null;
	
	/*关键字搜索*/
	private PoiResult 							mPoiResult; // poi返回的结果
	private int 								mCurrentPage = 0;// 当前页面，从0开始计数
	private PoiSearch.Query 					mQuery;// Poi查询条件类
	private PoiSearch 							mPoiSearch;// POI搜索
	
	private PoiOverlay 							mPoiOverlay;// poi图层
	private List<PoiItem> 						mPoiItems;// poi数据
	
	private String                              mKeyWordsStr            = null;
	
	
	private RelativeLayout                      mCurrentLocationRelative = null,
												mSelectNoLocationRelative = null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.friend_ac_location_select);
		
		initView (savedInstanceState) ;
	}
	
	private void initView(Bundle savedInstanceState) {
		
		mBackImage 						= (ImageView) findViewById(R.id.location_select_head_back);
		
		mContentsEdit 					= (EditTextWithDeleteButton) findViewById(R.id.location_select_input_edit);
		
		mCurrentLoctNameTxt     		= (TextView) findViewById(R.id.location_select_current_name_text);
		mCurrentLoctAddrTxt     		= (TextView) findViewById(R.id.location_select_current_address_text);
		
		mLocationList                   = (ListviewBottomRefresh) findViewById(R.id.location_select_list);
		
		mapView 						= (MapView) findViewById(R.id.location_select_amap);
		
		mCurrentLocationRelative 		= (RelativeLayout) findViewById(R.id.location_select_current_relative);
		mSelectNoLocationRelative       = (RelativeLayout) findViewById(R.id.location_select_no_relative);
		
        mapView.onCreate(savedInstanceState);// 必须要写
        
        aMap = mapView.getMap();
        mPoiItems = new ArrayList<PoiItem>();
        mAdapter = new poiResultAdapter();
        mLocationList.setAdapter(mAdapter);
		mBackImage.setOnClickListener(this);
        mLocationList.setOnItemClickListener(this);
        mLocationList.setOnRefreshListener(this);
        mContentsEdit.addTextChangedListener(this);
        mCurrentLocationRelative.setOnClickListener(this);
        mSelectNoLocationRelative.setOnClickListener(this);
        
        MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.location_marker));// 设置小蓝点的图标
		//myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
		//myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
		//myLocationStyle.anchor(int,int)//设置小蓝点的锚点
		//myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
		aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);
        
	}
	
	
	/**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);
        
        mapView.onResume();
    }
 
    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        
        
        /*必须要调用次方法*/
        deactivate();
    }
     
    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
 
    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    
    

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationChanged(AMapLocation aLocation) {
		// TODO Auto-generated method stub
		if (mListener != null && aLocation != null) {
			
			mListener.onLocationChanged(aLocation);// 显示系统小蓝点
			
			mLocationCityStr = aLocation.getCity();
			mLocationAddrStr = aLocation.getAddress();
			
			mCurrentLoctNameTxt.setText(mLocationCityStr);
			mCurrentLoctAddrTxt.setText(mLocationAddrStr);
			
			Config.mLocationProvinceStr = aLocation.getProvince();
			Config.mLocationCityStr = aLocation.getCity();
			Config.mLocationRegionStr = aLocation.getDistrict();
			
			Config.mLocationAddrStr = aLocation.getAddress();
			Config.mLocationAliasStr = aLocation.getPoiName();
			
			mLocationPoint = new LatLonPoint(aLocation.getLatitude(), aLocation.getLongitude());
		}
		
	}

	@Override
	public void activate(OnLocationChangedListener listener) {
		// TODO Auto-generated method stub
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			
			/*
			 * mAMapLocManager.setGpsEnable(false);
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
			 * API定位采用GPS和网络混合定位方式
			 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
			 */
			mAMapLocationManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 2000, 10, this);
		}
	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destroy();
		}
		mAMapLocationManager = null;
	}

	
	/*一下三个方法是，关键字输入框的输入变化*/
	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		mKeyWordsStr = arg0.toString();
		
		/*清空数据*/
		if (mPoiItems != null) {
			
			mPoiItems.clear();
		}
		
		doSearchQuery();
		
	}

	
	/*关键字搜索结果回调*/
	@Override
	public void onPoiItemDetailSearched(PoiItemDetail arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
		// TODO Auto-generated method stub
		
		mLocationList.onRefreshComplete();
		
		if (rCode == 0) {
			if (result != null && result.getQuery() != null) {// 搜索poi的结果
				if (result.getQuery().equals(mQuery)) {// 是否是同一条
					mPoiResult = result;
					//mPoiItems = mPoiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
					
					DebugTools.getDebug().debug_v(TAG, "onPoiSearched>>>>>>>>>>>");
					
					ArrayList<PoiItem> tempList = mPoiResult.getPois();
					
					DebugTools.getDebug().debug_v(TAG, "tempList----->>>>>"+tempList);
					
					int size ;
					
					if (tempList != null) {
						
						size = tempList.size();
						for (int i = 0; i <size ; i++ ) {
							
							mPoiItems.add(tempList.get(i));
						}
					}
					
					//mPoiItems = mPoiResult.getPois();
					
					DebugTools.getDebug().debug_v(TAG, "mPoiItems----->>>>>"+mPoiItems);
					
					mAdapter.notifyDataSetChanged();
					//mLocationList.setAdapter(mAdapter);
					
					
					List<SuggestionCity> suggestionCities = mPoiResult.getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
					if (mPoiItems != null && mPoiItems.size() > 0) {
						aMap.clear();// 清理之前的图标
						mPoiOverlay = new PoiOverlay(aMap, mPoiItems);
						mPoiOverlay.removeFromMap();
						mPoiOverlay.addToMap();
						mPoiOverlay.zoomToSpan();

						//nextButton.setClickable(true);// 设置下一页可点
					} else if (suggestionCities != null
							&& suggestionCities.size() > 0) {
						
						//showSuggestCity(suggestionCities);
						
					} else {
						//ToastUtil.show(PoiAroundSearchActivity.this,R.string.no_result);
						
					}
				}
			} else {
				//ToastUtil.show(PoiAroundSearchActivity.this, R.string.no_result);
			}
		} else if (rCode == 27) {
			
			//ToastUtil.show(PoiAroundSearchActivity.this, R.string.error_network);
		} else if (rCode == 32) {
			
			//ToastUtil.show(PoiAroundSearchActivity.this, R.string.error_key);
		} else {
			
			//ToastUtil.show(PoiAroundSearchActivity.this,getString(R.string.error_other) + rCode);
		}
	}
	
	
	/**
	 * 开始进行poi搜索
	 */
	protected void doSearchQuery() {
		//showProgressDialog();// 显示进度框
		
		if (mLocationPoint == null) {
			
			return;
		}
		
		mCurrentPage = 0;
		mQuery = new PoiSearch.Query(mKeyWordsStr, "", mLocationCityStr);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
		mQuery.setPageSize(10);// 设置每页最多返回多少条poiitem
		mQuery.setPageNum(mCurrentPage);// 设置查第一页
		
		mPoiSearch = new PoiSearch(this, mQuery);
		mPoiSearch.setOnPoiSearchListener(this);
		mPoiSearch.setBound(new SearchBound(mLocationPoint, 2000, true));
		mPoiSearch.searchPOIAsyn();
		
	}
	
	class poiResultAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mPoiItems.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			
			if (arg1 == null) {
				
				arg1 = getLayoutInflater().inflate(R.layout.friend_location_select_item, null);
			}
			
			TextView locationName = (TextView)arg1.findViewById(R.id.location_item_name_text);
			TextView locationAddress = (TextView)arg1.findViewById(R.id.location_item_address_text);
			
			locationName.setText(mPoiItems.get(arg0).getTitle());
			locationAddress.setText(mPoiItems.get(arg0).getSnippet());
			
			return arg1;
			
		}
		
		
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		if (mQuery != null && mPoiSearch != null) {
			//if (mPoiResult.getPageCount() - 1 > mCurrentPage) {
				mCurrentPage++;
				mQuery.setPageNum(mCurrentPage);// 设置查后一页
				mPoiSearch.searchPOIAsyn();
				
			//} else {
				
				//ToastUtil.show(PoiKeywordSearchActivity.this,R.string.no_result);
			//}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
		if (mLocationList.getId() == arg0.getId()) {
			
			Config.mLocationAliasStr = mPoiItems.get(arg2).getTitle();
			Config.mLocationAddrStr = mPoiItems.get(arg2).getSnippet();
			this.finish();
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if (arg0.getId() == mCurrentLocationRelative.getId()) {
			
			this.finish();
			
		}
		else if (arg0.getId() == mSelectNoLocationRelative.getId()) {
			
			Config.mLocationAliasStr = "";
			this.finish();
		}
		else if (arg0.getId() == mBackImage.getId()) {

			this.finish();
		}
	}
}
