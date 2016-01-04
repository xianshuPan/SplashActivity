package com.hylg.igolf.ui.friend;import java.util.ArrayList;import java.util.List;import net.tsz.afinal.FinalBitmap;import com.amap.api.location.AMapLocation;import com.amap.api.location.AMapLocationListener;import com.amap.api.location.LocationManagerProxy;import com.amap.api.location.LocationProviderProxy;import com.hylg.igolf.DebugTools;import com.hylg.igolf.MainApp;import com.hylg.igolf.R;import com.hylg.igolf.cs.data.FriendHotItem;import com.hylg.igolf.cs.request.FriendMessageNew;import com.hylg.igolf.imagepicker.Config;import com.hylg.igolf.imagepicker.ImageGridActivity;import com.hylg.igolf.ui.friend.publish.Params;import com.hylg.igolf.ui.friend.publish.PublishBean;import com.hylg.igolf.ui.friend.publish.PublishDB;import com.hylg.igolf.ui.friend.publish.PublishService;import com.hylg.igolf.ui.friend.publish.PublishType;import com.hylg.igolf.utils.Utils;import com.tencent.mm.sdk.openapi.IWXAPI;import com.tencent.mm.sdk.openapi.WXAPIFactory;import com.tencent.tauth.Tencent;import com.xc.lib.layout.LayoutUtils;import android.app.Activity;import android.app.AlertDialog;import android.content.Context;import android.content.DialogInterface;import android.content.Intent;import android.location.Location;import android.os.Bundle;import android.text.Editable;import android.text.TextWatcher;import android.view.KeyEvent;import android.view.LayoutInflater;import android.view.View;import android.view.ViewGroup;import android.view.View.OnClickListener;import android.view.Window;import android.widget.AdapterView;import android.widget.AdapterView.OnItemClickListener;import android.widget.BaseAdapter;import android.widget.EditText;import android.widget.GridView;import android.widget.ImageView;import android.widget.RelativeLayout;import android.widget.TextView;import android.widget.Toast;public class FriendMessageNewActivity extends Activity implements OnClickListener , 																 OnItemClickListener{			private final String 						TAG 					= "FriendMessageNewActivity";	private int                           		MAX_LENGTH_EDIT         = 0;	/*后退和确定按钮*/	private ImageView 							mBackImage 				= null;		private TextView  							mDistrText 				= null,												mErrorTxt               = null,												mContentSurplusTxt      = null;		/*填写刺客想法*/	private  EditText 							mContentsEdit 			= null;		/*选中的图片的*/	private GridView 							mSelectedImgGrid 		= null;		private AddImageGridAdapter 				mAdapter 				= null;		/*选中的位置商家信息*/	private RelativeLayout 						mSelectedLocationRelative = null;		private TextView  							mSelectedLocationText 	= null;		/*分享里面的，微信、微博、qq*/	private ImageView 							mMicroMessageImage		= null,													mMicroBlogImage			= null,												mTencentQQImage			= null;		private boolean                             mMicroMessageSelected   = true,												mMicroBlogSelected		= true,												mTencentQQSelected		= false;		private FriendMessageNew					reqLoader               = null;			private String                              sn                      = "",												name                    = "",												content                 = "";		private IWXAPI                              api                     = null;		private Tencent                             mTencent                = null;			private FriendHotItem						mFriendMessageNewItem   = null;		//private IWeiboShareAPI 						mWeiboShareAPI;		/*高德定位操作*/	private LocationManagerProxy 				mLocationManagerProxy;	private myAMapLocationListener      		mAMapLocationListener;		private String 								mLocationCityStr  		= "",												mLocationProvinceStr    = "",												mLocationRegionStr      = "",												mLocationAddrStr        = "";	private PublishBean 						mBean;	private Activity                            mContext;	public static void startFriendMessageNewActivity(Activity from, PublishBean bean) {		Intent intent = new Intent(from, FriendMessageNewActivity.class);		intent.putExtra("type", PublishType.status.toString());		if (bean != null)			intent.putExtra("bean", bean);		from.startActivity(intent);	}	@Override	protected void onCreate(Bundle savedInstanceState) {		super.onCreate(savedInstanceState);		DebugTools.getDebug().debug_v(TAG,"----->>>onCreat");		requestWindowFeature(Window.FEATURE_NO_TITLE);		setContentView(R.layout.friend_ac_message_new);				initView() ;	}		private void initView() {				mBackImage 						= (ImageView) findViewById(R.id.image_picker_head_back);				mDistrText 						= (TextView) findViewById(R.id.image_picker_distr_text);		mErrorTxt                       = (TextView) findViewById(R.id.friend_message_new_txtErrorHint);		mContentsEdit 					= (EditText) findViewById(R.id.friend_message_new_contents_edit);		mContentSurplusTxt              = (TextView) findViewById(R.id.friend_message_new_txtContentSurplus);		mSelectedImgGrid 				= (GridView) findViewById(R.id.friend_message_new_gridview);				mSelectedLocationRelative 		= (RelativeLayout) findViewById(R.id.friend_message_new_location_relative);		mSelectedLocationText 			= (TextView) findViewById(R.id.friend_message_new_location_text);				mMicroMessageImage 				= (ImageView) findViewById(R.id.friend_message_new_micro_messsage_image);		mMicroBlogImage 				= (ImageView) findViewById(R.id.friend_message_new_micro_blog_image);		mTencentQQImage 				= (ImageView) findViewById(R.id.friend_message_new_tencent_qq_image);		mContext = this;		MAX_LENGTH_EDIT = mContext.getResources().getInteger(R.integer.edit_max_length140);		mSelectedLocationRelative.setOnClickListener(this);		mDistrText.setOnClickListener(this);		mBackImage.setOnClickListener(this);		mMicroMessageImage.setOnClickListener(this);		mMicroBlogImage.setOnClickListener(this);		mTencentQQImage.setOnClickListener(this);		mSelectedImgGrid.setOnItemClickListener(this);		mContentsEdit.addTextChangedListener(editContentWatcher);				sn = MainApp.getInstance().getCustomer().sn;		name = MainApp.getInstance().getCustomer().nickname;		api = WXAPIFactory.createWXAPI(this, "wx14da9bc6378845fe");		mFriendMessageNewItem = new FriendHotItem();				//mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, "3633766045");		//mWeiboShareAPI.registerApp();		mTencent = Tencent.createInstance("eDdC9iNdZzWeZb7C", this.getApplicationContext());		mBean = (PublishBean) getIntent().getSerializableExtra("bean");		if (mBean == null) {			mBean = newPublishBean();			mBean.setDelay(0);			mBean.setTiming(0);			mBean.sn = sn;			mBean.avatar = MainApp.getInstance().getCustomer().avatar;			mBean.name = name;			mBean.setStatus(PublishBean.PublishStatus.create);		}		else {			mContentsEdit.setText(mBean.getText());			mContentsEdit.setSelection(mContentsEdit.length());			mSelectedLocationText.setText(mBean.alias);			if (mBean.getPics() != null && mBean.getPics().length > 0) {				if (Config.drr == null)					Config.drr = new ArrayList<String>();				for (int i=0;i<mBean.getPics().length;i++) {					Config.drr.add(i,mBean.getPics()[i]);				}			}		}		mAdapter = new AddImageGridAdapter(this, Config.drr,mSelectedImgGrid);		mSelectedImgGrid.setAdapter(mAdapter);	}	PublishBean newPublishBean() {		PublishBean bean = new PublishBean();		bean.setStatus(PublishBean.PublishStatus.create);		bean.setType(PublishType.status);		Params params = new Params();		// 默认所有人可见		params.addParameter("visible", "0");		bean.setParams(params);		return bean;	}	/**	 * 微博内容监听，刷新提示信息	 */	private TextWatcher editContentWatcher = new TextWatcher() {		@Override		public void onTextChanged(CharSequence s, int start, int before, int count) {			// 设置长度提示			String content = mContentsEdit.getText().toString();			mContentSurplusTxt.setText(content.length() + "/"+MAX_LENGTH_EDIT);			mBean.setText(content);		}		@Override		public void beforeTextChanged(CharSequence s, int start, int count, int after) {		}		@Override		public void afterTextChanged(Editable s) {		}	};	private void update() {		mAdapter.setData(Config.drr);		Config.SELECT_MAX = Config.SELECT_MAX_NUM - Config.drr.size();				mSelectedLocationText.setText(mBean.alias);	}	@Override	protected void onDestroy() {		super.onDestroy();		// Config.bitmapdatas.clear();		Config.drr.clear();		DebugTools.getDebug().debug_v(TAG, "----->>>onDestroy");		//Config.mLocationAliasStr = getResources().getString(R.string.str_friend_location_select);		Config.mLocationAliasStr = "";		//Config.mLocationProvinceStr = "";		//Config.mLocationCityStr = "";		//Config.mLocationRegionStr = "";		//Config.mLocationAddrStr = "";	}	@Override	protected void onResume() {		super.onResume();		DebugTools.getDebug().debug_v(TAG, "----->>>onResume");		mLocationManagerProxy = LocationManagerProxy.getInstance(this);		mAMapLocationListener = new myAMapLocationListener();		 		//此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，		//注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请求		//在定位结束后，在合适的生命周期调用destroy()方法		//其中如果间隔时间为-1，则定位只定一次		mLocationManagerProxy.requestLocationData(				LocationProviderProxy.AMapNetwork, 60 * 1000, 15, mAMapLocationListener);	 		mLocationManagerProxy.setGpsEnable(false);		setPics();		if (Config.mLocationAliasStr != null && Config.mLocationAliasStr.length() > 0) {			mBean.alias = Config.mLocationAliasStr;		}		update();	}	private void setPics () {		if ((Config.drr == null || Config.drr.size() > 0)) {			String[] pics = new String[Config.drr.size()];			for (int i=0;i<Config.drr.size();i++) {				pics[i]=Config.drr.get(i);			}			mBean.setPics(pics);		}	}		@Override	protected void onPause() {		DebugTools.getDebug().debug_v(TAG, "----->>>onPause");		 if (mLocationManagerProxy != null) {			 mLocationManagerProxy.removeUpdates(mAMapLocationListener);			 mLocationManagerProxy.destroy();		  }		 		 mAMapLocationListener = null;		 mLocationManagerProxy = null;		super.onPause();	}		@Override	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		mTencent.onActivityResult(requestCode, resultCode, data);	}	@Override	public boolean onKeyDown(int keyCode , KeyEvent event) {		DebugTools.getDebug().debug_v(TAG, "----->>>onKeyDown");		if ((keyCode == KeyEvent.KEYCODE_BACK) &&				(event.getAction() == KeyEvent.ACTION_DOWN)) {			if (mBean.getPics() !=null && mBean.getPics().length > 0) {				askSaveToDraft();			}			else {				mContext.finish();			}		}		return super.onKeyDown(keyCode, event);	}	@Override	public void onClick(View arg0) {		// TODO Auto-generated method stub		if (arg0.getId() == mSelectedLocationRelative.getId()) {						startActivity(new Intent(this, FriendLocationSelectActivity.class));					} else if (arg0.getId() == mBackImage.getId()) {			if (mBean.getPics() !=null && mBean.getPics().length > 0) {				askSaveToDraft();			}			else {				mContext.finish();			}					} else if (arg0.getId() == mMicroMessageImage.getId()) {						mMicroMessageSelected = !mMicroMessageSelected;						if (mMicroMessageSelected) {								mMicroMessageImage.setImageResource(R.drawable.micro_message_selected);			} else  {								mMicroMessageImage.setImageResource(R.drawable.micro_message);			}					} else if (arg0.getId() == mMicroBlogImage.getId()) {						mMicroBlogSelected = !mMicroBlogSelected;						if (mMicroBlogSelected) {								mMicroBlogImage.setImageResource(R.drawable.micro_blog_selected);							} else  {								mMicroBlogImage.setImageResource(R.drawable.micro_blog);			}		}		else if (arg0.getId() == mTencentQQImage.getId()) {						mTencentQQSelected = !mTencentQQSelected;						if (mTencentQQSelected) {								mTencentQQImage.setImageResource(R.drawable.tencent_qq_selected);							} else  {								mTencentQQImage.setImageResource(R.drawable.tencent_qq);			}		}		else {			if (mDistrText.getId() == arg0.getId()) {				content = mContentsEdit.getText().toString();				DebugTools.getDebug().debug_e(TAG, "-------------------->>>" + content);				if ((Config.drr == null || Config.drr.size() <= 0)) {					Toast.makeText(this, "请添加美图", Toast.LENGTH_SHORT).show();					return;				}				/**/				if (!Utils.isConnected(this)) {					return;				}				mFriendMessageNewItem.avatar = MainApp.getInstance().getCustomer().avatar;				mFriendMessageNewItem.tipid = String.valueOf(System.currentTimeMillis());				mFriendMessageNewItem.sn = sn;				mFriendMessageNewItem.name = name;				mFriendMessageNewItem.content = content;				mFriendMessageNewItem.provence = mLocationProvinceStr;				mFriendMessageNewItem.city = mLocationCityStr;				mFriendMessageNewItem.alais = Config.mLocationAliasStr;				mFriendMessageNewItem.region = mLocationRegionStr;				mFriendMessageNewItem.detail = mLocationAddrStr;				mFriendMessageNewItem.imageURL = "";				mFriendMessageNewItem.releaseTime = System.currentTimeMillis();				mFriendMessageNewItem.localImageURL = new ArrayList<String>();				for (String str : Config.drr) {					mFriendMessageNewItem.localImageURL.add(str);				}				//Config.mFriendMessageNewItem = mFriendMessageNewItem;//				Intent data1 = new Intent(FriendMessageNewActivity.this, HdService.class);//				data1.putExtra("Message", mFriendMessageNewItem);//				startService(data1);				mBean.setTiming(System.currentTimeMillis());				PublishService.publish(this, mBean);				this.finish();			/*发布帖子*///			WaitDialog.showWaitDialog(this, R.string.str_loading_add_album);//			new AsyncTask<Object, Object, Integer>() {////				FriendMessageNew request = new FriendMessageNew(FriendMessageNewActivity.this,mFriendMessageNewItem);//				@Override//				protected Integer doInBackground(Object... params) {////					return request.connectUrl();//				}//				@Override//				protected void onPostExecute(Integer result) {//					super.onPostExecute(result);////					if(BaseRequest.REQ_RET_OK == result) {////						/*选择分享到微博*///						if (mMicroBlogSelected) {////							//实例化一个OnekeyShare对象//							OnekeyShare oks = new OnekeyShare();//							//分享内容的标题//							oks.setTitle("爱高尔夫");//							//标题对应的网址，如果没有可以不设置//							oks.setTitleUrl("http://www.sc-hdi.com/");//							//设置分享的文本内容//							oks.setText(content);//							//设置分享照片的本地路径，如果没有可以不设置//							if (Config.drr != null && Config.drr.size() > 0) {////								oks.setImagePath(Config.drr.get(0));//							}////							//微信和易信的分享的网络连接，如果没有可以不设置//							oks.setUrl("http://www.sc-hdi.com/");//							//程序的名称或者是站点名称//							oks.setSite("site");//							//程序的名称或者是站点名称的链接地址//							oks.setSiteUrl("http://www.sc-hdi.com/");//							//设置是否是直接分享//							oks.setSilent(true);//							oks.setPlatform("SinaWeibo");//							//显示//							oks.show(FriendMessageNewActivity.this);//						}////						/*选择分享到微信*///						if (mMicroMessageSelected) {////							//实例化一个OnekeyShare对象//							OnekeyShare oks = new OnekeyShare();//							//分享内容的标题//							oks.setTitle("爱高尔夫");//							//标题对应的网址，如果没有可以不设置//							oks.setTitleUrl("http://www.sc-hdi.com/");//							//设置分享的文本内容//							oks.setText(content);//							//设置分享照片的本地路径，如果没有可以不设置//							if (Config.drr != null && Config.drr.size() > 0) {////								oks.setImagePath(Config.drr.get(0));//							}////							//微信和易信的分享的网络连接，如果没有可以不设置//							oks.setUrl("http://www.sc-hdi.com/");//							//程序的名称或者是站点名称//							oks.setSite("site");//							//程序的名称或者是站点名称的链接地址//							oks.setSiteUrl("http://www.sc-hdi.com/");//							//设置是否是直接分享//							oks.setSilent(true);//							oks.setPlatform("WechatMoments");//							//显示//							oks.show(FriendMessageNewActivity.this);//						}////						/*没有选择分享到微信，选择了分享到 qq 空间*///						if (!mMicroMessageSelected && mTencentQQSelected) {////							//实例化一个OnekeyShare对象//							OnekeyShare oks = new OnekeyShare();//							//分享内容的标题//							oks.setTitle("爱高尔夫");//							//标题对应的网址，如果没有可以不设置//							oks.setTitleUrl("http://www.sc-hdi.com/");//							//设置分享的文本内容//							oks.setText(content);//							//设置分享照片的本地路径，如果没有可以不设置//							if (Config.drr != null && Config.drr.size() > 0) {////								oks.setImagePath(Config.drr.get(0));//							}////							//微信和易信的分享的网络连接，如果没有可以不设置//							oks.setUrl("http://www.sc-hdi.com/");//							//程序的名称或者是站点名称//							oks.setSite("site");//							//程序的名称或者是站点名称的链接地址//							oks.setSiteUrl("http://www.sc-hdi.com/");//							//设置是否是直接分享//							oks.setSilent(true);//							oks.setPlatform("QZone");//							//显示//							oks.show(FriendMessageNewActivity.this);//						}////////					} else {//////					}////					WaitDialog.dismissWaitDialog();//					FriendMessageNewActivity.this.finish();//				}//			}.execute(null, null, null);			}		}	}	private void askSaveToDraft() {		new AlertDialog.Builder(mContext).setMessage(R.string.publish_draft_title)				.setNegativeButton(R.string.str_photo_cancel, new DialogInterface.OnClickListener() {					@Override					public void onClick(DialogInterface dialog, int which) {						mContext.finish();					}				})				.setPositiveButton(R.string.str_photo_commit, new DialogInterface.OnClickListener() {					@Override					public void onClick(DialogInterface dialog, int which) {						mBean.setStatus(PublishBean.PublishStatus.draft);						mBean.setTiming(System.currentTimeMillis());						PublishDB.addPublish(mBean, MainApp.getInstance().getUser());						mContext.finish();					}				})				.show();	}		private class myAMapLocationListener implements AMapLocationListener {		@Override		public void onLocationChanged(Location arg0) {			// TODO Auto-generated method stub					}		@Override		public void onProviderDisabled(String arg0) {			// TODO Auto-generated method stub					}		@Override		public void onProviderEnabled(String arg0) {			// TODO Auto-generated method stub					}		@Override		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {			// TODO Auto-generated method stub					}		@Override		public void onLocationChanged(AMapLocation amapLocation) {			// TODO Auto-generated method stub			if(amapLocation != null && amapLocation.getAMapException().getErrorCode() == 0){		           //获取位置信息				mLocationProvinceStr = amapLocation.getProvince();				mLocationCityStr = amapLocation.getCity();				mLocationRegionStr = amapLocation.getDistrict();				mLocationAddrStr = amapLocation.getAddress();				mBean.province = mLocationProvinceStr;				mBean.city = mLocationCityStr;				mBean.region = mLocationRegionStr;				mBean.detail = mLocationAddrStr;								DebugTools.getDebug().debug_v(TAG, "定位省："+mLocationProvinceStr);		      }		}			}	public class AddImageGridAdapter extends BaseAdapter {		private Context context;		private List<String> datas;		private FinalBitmap finalBit;		public AddImageGridAdapter(Context context, List<String> datas, GridView list) {			this.context = context;			this.datas = datas;			finalBit = FinalBitmap.create(context);		}		@Override		public int getCount() {			int size =  datas.size() > 9 ? 9 :  datas.size();			return datas == null ? 0 : size + 1;		}		@Override		public Object getItem(int position) {			return position;		}		@Override		public long getItemId(int position) {			return position;		}		@Override		public View getView(final int position, View convertView, ViewGroup parent) {			ViewHolder viewHolder;			if (convertView == null) {				convertView = LayoutInflater.from(context).inflate(						R.layout.addimage_grid, parent, false);				viewHolder = new ViewHolder();				viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image);				viewHolder.deleteImage = (ImageView) convertView.findViewById(R.id.grid_delete_image);				LayoutUtils.rateScale(context, viewHolder.imageView, true);				convertView.setTag(viewHolder);			} else {				viewHolder = (ViewHolder) convertView.getTag();			}			viewHolder.deleteImage.setOnClickListener(new OnClickListener() {				@Override				public void onClick(View arg0) {					// TODO Auto-generated method stub					int index = position;					if (datas != null && index < datas.size() && index >= 0) {						datas.remove(index);						mAdapter.notifyDataSetChanged();						setPics ();					}				}			});			if (position == datas.size()) {				viewHolder.deleteImage.setVisibility(View.GONE);				viewHolder.imageView.setImageResource(R.drawable.addpic);				if (position == Config.SELECT_MAX_NUM) {					viewHolder.imageView.setVisibility(View.GONE);				}			} else {				viewHolder.deleteImage.setVisibility(View.VISIBLE);				//loader.displayImage(datas.get(position), viewHolder.imageView,size, size, R.drawable.ic_launcher);				finalBit.display(viewHolder.imageView, datas.get(position));				//DownLoadImageTool.getInstance(FriendMessageNewActivity.this).displayImage(datas.get(position), viewHolder.imageView);			}			if (position >= Config.SELECT_MAX_NUM) {				convertView.setVisibility(View.GONE);			}			return convertView;		}		public void setData(List<String> datas) {			this.datas = datas;			notifyDataSetChanged();		}		public List<String> getData() {			return this.datas;		}	}	public static class ViewHolder {		public ImageView imageView;		public ImageView deleteImage;	}		@Override	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {		// TODO Auto-generated method stub		if (arg2 == Config.drr.size()) {						startActivity(new Intent(this, ImageGridActivity.class));		} else {						Intent data = new Intent(this, PhotoViewActivity.class);			data.putExtra("SelectPhotos", 1);			data.putExtra("Index", arg2);			startActivity(data);		}	}}