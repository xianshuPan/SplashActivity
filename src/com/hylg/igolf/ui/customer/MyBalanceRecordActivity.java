package com.hylg.igolf.ui.customer;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.MyBalanceRecordInfo;
import com.hylg.igolf.cs.loader.GetMyBalanceRecordListLoader;
import com.hylg.igolf.cs.loader.GetMyBalanceRecordListLoader.GetBalanceRecordListCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.GetMyBalanceAmount;
import com.hylg.igolf.ui.MainActivity;
import com.hylg.igolf.ui.SplashActivity;
import com.hylg.igolf.ui.view.EhecdListview;
import com.hylg.igolf.ui.view.EhecdListview.OnLoadMoreListener;
import com.hylg.igolf.ui.view.EhecdListview.OnRefreshListener;
import com.hylg.igolf.ui.widget.IgBaseAdapter;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

import java.util.ArrayList;

public class MyBalanceRecordActivity extends FragmentActivity {
	
	private final String 				TAG 						= "MyBalanceRecordActivity";
	
	private ImageButton  				mBack 						= null;

	private TextView                    mBalanceTxt,mWithdrawTxt;
	
	private EhecdListview 				mList 						= null;
	
	private Activity 					mContext 					= null;
	
	private long                        customer_id                 = 0;
	
	private int 						startPage					= 0, 
										pageSize					= 10;
	
	private GetMyBalanceRecordListLoader  reqLoader                 = null;
	
	
	private MyBalanceRecordAdapter      mMyFollowerAdapter			= null;
	
	
	public static void startMyBalanceRecordActivity(Activity context) {

		Intent intent = new Intent(context, MyBalanceRecordActivity.class);
		context.startActivity(intent);
	}
	
	public static void startMyBalanceRecordActivity(Fragment context) {

		Intent intent = new Intent(context.getActivity(), MyBalanceRecordActivity.class);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.customer_info_my_balance_record);
		
		mBack =  (ImageButton)  findViewById(R.id.customer_info_my_balance_record_back);
		mBalanceTxt = (TextView) findViewById(R.id.customer_info_my_balance_record_text);
		mWithdrawTxt = (TextView) findViewById(R.id.customer_info_my_balance_record_withdraw_text);
		mList = (EhecdListview) findViewById(R.id.customer_info_my_balance_record_back_listview);
		
		mBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				MainActivity.startMainActivity(mContext);
				mContext.finish();

			}
		});
		mWithdrawTxt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				double balance = MainApp.getInstance().getGlobalData().getBalance();

				if (balance > 0) {

					if (MainApp.getInstance().getGlobalData().getCardNo() == null ||
							MainApp.getInstance().getGlobalData().getCardNo().length() <16) {

						BindCardActivity.startBindCardActivity(mContext, Const.BING_CARD_AND_PASSWORD);
						mContext.finish();

					} else {

						ToCashActivity.startToCashActivity(mContext);
						mContext.finish();
					}
				} else {

					//WaitDialog.showWaitDialog(mContext, R.string.str_loading_msg);
					getMyBalanceAmount();
				}

			}
		});

		customer_id = MainApp.getInstance().getCustomer().id;
		startPage = MainApp.getInstance().getGlobalData().startPage;
		pageSize = MainApp.getInstance().getGlobalData().pageSize;
		
		mList.setOnLoadMoreListener(new OnLoadMoreListener() {
			
			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
				loadMoreData();
			}
		});
		
		mList.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				refreshData();
			}
		});
		
		initDataAysnc();

		getMyBalanceAmount();
	}
	
	@Override
	protected void onResume () {

		super.onResume();
	}

	@Override
	public boolean onKeyDown(int key_code,KeyEvent envent){

		if (key_code == KeyEvent.KEYCODE_BACK) {

			MainActivity.startMainActivity(mContext);
			mContext.finish();
		}

		return super.onKeyDown(key_code,envent);
	}
	
	private void getMyBalanceAmount() {

		final GetMyBalanceAmount request = new GetMyBalanceAmount(mContext,customer_id);
		new AsyncTask<Object,Object,Integer>(){

			@Override
			protected Integer doInBackground (Object... params) {

				return request.connectUrl();
			}

			@Override
			protected void onPostExecute(Integer retId) {
				super.onPostExecute(retId);

				if(BaseRequest.REQ_RET_F_NO_DATA == retId ) {


				} else if(BaseRequest.REQ_RET_OK == retId) {


					mBalanceTxt.setText(String.valueOf(request.getMyBalance()));
					MainApp.getInstance().getGlobalData().setBalance(request.getMyBalance());
					MainApp.getInstance().getGlobalData().setCardNo(request.card_no);
					MainApp.getInstance().getGlobalData().setBankName(request.card_name);
				}

				//WaitDialog.dismissWaitDialog();
			}

		}.execute(null,null,null);
	}
	
	/*初始化数据请求*/
	private void initDataAysnc() {
		if(!Utils.isConnected(mContext)) {
			return ;
		}
		WaitDialog.showWaitDialog(mContext, R.string.str_loading_msg);
		clearLoader();
		/*sn 暂时等于1*/
		reqLoader = new GetMyBalanceRecordListLoader(mContext,customer_id, startPage,pageSize,
				new GetBalanceRecordListCallback() {
					
			@Override
			public void callBack(int retId, String msg, ArrayList<MyBalanceRecordInfo> List) {
				// TODO Auto-generated method stub
				
				if(BaseRequest.REQ_RET_F_NO_DATA == retId || List.size() == 0) {
					if(msg.trim().length() == 0) {
						msg = getString(R.string.str_friend_no_data);
					}

					// display reload page
					//loadFail.displayNoDataRetry(msg);
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
					
				} else if(BaseRequest.REQ_RET_OK == retId) {
					
					//loadFail.displayNone();
					initListView(List);
					MainApp.getInstance().getGlobalData().setHasStartNewInvite(false);
					
				} else {
					//loadFail.displayFail(msg);
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
				}
				mList.onRefreshComplete();
				WaitDialog.dismissWaitDialog();
				reqLoader = null;	
			}
		});
		reqLoader.requestData();
	}
	
	/*加载更多*/
	private void loadMoreData() {
		if(!Utils.isConnected(mContext)) {
			return ;
		}
		//WaitDialog.showWaitDialog(mContext, R.string.str_loading_msg);
		clearLoader();
		
		startPage++;
		/*sn 暂时等于1*/
		reqLoader = new GetMyBalanceRecordListLoader(mContext,customer_id, startPage,pageSize,
				
		new GetBalanceRecordListCallback() {
					
			@Override
			public void callBack(int retId, String msg, ArrayList<MyBalanceRecordInfo> List) {
				// TODO Auto-generated method stub
				
				if(BaseRequest.REQ_RET_F_NO_DATA == retId || List.size() == 0) {
					if(msg.trim().length() == 0) {
						msg = getString(R.string.str_friend_no_data);
					}
					// display reload page
					//loadFail.displayNoDataRetry(msg);
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
					
				} else if(BaseRequest.REQ_RET_OK == retId) {
					
					//loadFail.displayNone();
					//initListView(List);
					mMyFollowerAdapter.appendListInfo(List);
					
					//listFooter.refreshFooterView(inviteList.size(), pageSize);
					MainApp.getInstance().getGlobalData().setHasStartNewInvite(false);

				} else {

					//loadFail.displayFail(msg);
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
				}
				//WaitDialog.dismissWaitDialog();
				mList.onRefreshComplete();
				reqLoader = null;	
			}
		});
		reqLoader.requestData();
	}
	
	/*初始化数据请求*/
	private void refreshData() {
		if(!Utils.isConnected(mContext)) {
			return ;
		}
		//WaitDialog.showWaitDialog(mContext, R.string.str_loading_msg);
		clearLoader();
		startPage = 1;
		/*sn 暂时等于1*/
		reqLoader = new GetMyBalanceRecordListLoader(mContext,customer_id, startPage,pageSize,
				
		new GetBalanceRecordListCallback() {
					
			@Override
			public void callBack(int retId, String msg, ArrayList<MyBalanceRecordInfo> List) {
				// TODO Auto-generated method stub
				
				if(BaseRequest.REQ_RET_F_NO_DATA == retId || List.size() == 0) {
					if(msg.trim().length() == 0) {
						msg = getString(R.string.str_friend_no_data);
					}
					// display reload page
					//loadFail.displayNoDataRetry(msg);
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
					
				} else if(BaseRequest.REQ_RET_OK == retId) {
					
					initListView(List);
					//listFooter.refreshFooterView(inviteList.size(), pageSize);
					MainApp.getInstance().getGlobalData().setHasStartNewInvite(false);
				} else {
					//loadFail.displayFail(msg);
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
				}
				//WaitDialog.dismissWaitDialog();
				mList.onRefreshComplete();
				reqLoader = null;

			}
		});
		reqLoader.requestData();
	}
	
	private void clearLoader() {
		if(isLoading()) {
			GetMyBalanceRecordListLoader loader = reqLoader;
			loader.stopTask(true);
		}
	}
	
	private boolean isLoading() {
		return (null != reqLoader && reqLoader.isRunning());
	}
	
	
	private void initListView(ArrayList<MyBalanceRecordInfo> inviteList) {
		
		mMyFollowerAdapter = new MyBalanceRecordAdapter(inviteList);
		mList.setAdapter(mMyFollowerAdapter);
	}

	/*
	*
	* 我的交易记录
	* */
	private class MyBalanceRecordAdapter extends IgBaseAdapter{
		
		private ArrayList<MyBalanceRecordInfo> list;
		
		public MyBalanceRecordAdapter(ArrayList<MyBalanceRecordInfo> list) {
			
			this.list = list;
		}
		
		public void refreshListInfo(ArrayList<MyBalanceRecordInfo> list) {
			this.list.clear();
			this.list = list;
			notifyDataSetChanged();
		}
		
		public void appendListInfo(ArrayList<MyBalanceRecordInfo> list) {
			for(int i=0, size=list.size(); i<size; i++) {
				this.list.add(list.get(i));
			}
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
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

			ViewHolder holder;
			
			if (arg1 == null) {
				
				holder = new ViewHolder();
				arg1 = mContext.getLayoutInflater().inflate(R.layout.customer_info_my_balance_record_item, null);

				holder.userName = (TextView)arg1.findViewById(R.id.customer_info_my_balance_record_item_nameText);
				holder.type = (TextView)arg1.findViewById(R.id.customer_info_my_balance_record_item_typeText);
				holder.time = (TextView)arg1.findViewById(R.id.customer_info_my_balance_record_item_timeText);
				holder.amount = (TextView)arg1.findViewById(R.id.customer_info_my_balance_record_item_amountText);
				
				arg1.setTag(holder);
				
			} else {
				
				holder = (ViewHolder)arg1.getTag();
			}
			

			holder.time.setText(Utils.longTimeToString(list.get(arg0).dealTime));
			
			if (list.get(arg0).type == 0) {
				
				holder.type.setText("提现");//color_tab_green
				holder.amount.setText("-" + list.get(arg0).amount);
				holder.amount.setTextColor(getResources().getColor(R.color.color_yellow));
				holder.userName.setText(getStatusStr(list.get(arg0).status));
				
			} else if (list.get(arg0).type == 1) {

				holder.amount.setText("+"+list.get(arg0).amount);
				holder.type.setText("收到学费");
				holder.userName.setText(list.get(arg0).from_who);
				holder.amount.setTextColor(getResources().getColor(R.color.color_tab_green));
			}

			return arg1;
		}
		
	}
	
	private static class ViewHolder {

		public TextView userName;
		public TextView type;
		public TextView time;
		public TextView amount;
	}


	private String getStatusStr (int status ) {

		if (status <0 || status > 1) {

			return "";
		}

		if (status == 0) {

			return "正在处理";
		} else if (status == 1) {

			return "成功";
		}

		return "";
	}
}
