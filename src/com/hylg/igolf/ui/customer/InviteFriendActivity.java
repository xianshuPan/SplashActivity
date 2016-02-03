package com.hylg.igolf.ui.customer;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.Customer;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.SavePhoneBook;
import com.hylg.igolf.cs.request.SendMsgToFriend;
import com.hylg.igolf.ui.view.LetterSideBar;
import com.hylg.igolf.ui.widget.IgBaseAdapter;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class InviteFriendActivity extends FragmentActivity implements LetterSideBar.OnLetterChangedListener, View.OnClickListener{
	private static final int MIN_TASK_SHOW_IMAGE_PROGRESS_BAR = 30;
	private static final int REFRESH_PROCESS_IMAGE_DURATION = 100;
	private static final int MESSAGE_REFRESH_COUNT = 1;
	private static final int MESSAGE_DONE = 2;

	public static final int CUSTOMER_INFO = 1;
	public static final int OPEN_INVITE = 0;

	/**
	 * wifi下自动更新，最低间隔为1天，单位毫秒
	 */
	private static final long REFRESH_CONTACTS_MIN_DURATION = 86400000;
	public boolean isLoadingImage;
	public boolean isNeedToAddContact;
	public FragmentActivity mBaseActivity;
	public ContactEntity contactToAdd;

	private InputMethodManager imm;
	private boolean isRequestData;
	private int previousIndex = 0;
	private ContactAdapter contactAdapter;

	private ContactListAdapter contactListAdapter;
	private LinearLayoutManager mLinearLayoutManager;
	private List<ContactEntity> mTotalContacts;
	private List<ContactEntity> mSearchContacts;
	private HashMap<Character, Integer> mIndexMap;
	private List<Character> mLetterList;
	private LetterSideBar letterSideBar;
	private TextView mTextViewChar,registeredFriendCountTxt;
	private EditText mSearchText;

	ListView recyclerView,registeredFriendList;

	ContentResolver cr;

	Cursor cursor;

	private AsyncQueryHandler asyncQueryHandler;

	private String phone_numbers = "";

	private Customer customer = null;

	private TextView mCommitTxt = null,mSettingTxt;

	private int from_type = -1;

	private long app_id = -1;

	public ArrayList<String> selectPhoneArray ;


	public static void startInviteFriendActivity (Activity context,int from,long appId) {

		Intent intent1 = new Intent(context, InviteFriendActivity.class);

		intent1.putExtra("from",from);
		intent1.putExtra("appId",appId);
		context.startActivity(intent1);
		context.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}

	public static void startInviteFriendActivity (Activity context,int from) {

		Intent intent1 = new Intent(context, InviteFriendActivity.class);
		intent1.putExtra("from",from);
		context.startActivity(intent1);
		context.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.customer_info_invite_friend);
		mBaseActivity = this;
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mIndexMap = new HashMap<Character, Integer>();
		mLetterList = new ArrayList<Character>();

		cr = getContentResolver();
		customer = MainApp.getInstance().getCustomer();
		asyncQueryHandler = new MyAsyncQueryHandler(getContentResolver());

		selectPhoneArray = new ArrayList<String>();

		initUI();
	}

	public void initUI() {

		findViewById(R.id.invite_friend_back).setOnClickListener(this);
		letterSideBar = ((LetterSideBar) findViewById(R.id.contact_letter_side_bar));
		letterSideBar.setOnLetterChangedListener(this);
		mCommitTxt = (TextView) findViewById(R.id.invite_friend_commit_text);
		mSettingTxt = (TextView) findViewById(R.id.invite_friend_setting_text);
		registeredFriendCountTxt = (TextView) findViewById(R.id.invite_friend_registered_friend_count_text);
		mLinearLayoutManager = new LinearLayoutManager(mBaseActivity);
		recyclerView = (ListView) findViewById(R.id.contact_list);
		registeredFriendList = (ListView) findViewById(R.id.invite_friend_registered_friend_list);
		ImageView mKeyboardBtn = (ImageView)findViewById(R.id.bar_keyboard_btn);
		mKeyboardBtn.setOnClickListener(this);
		mSearchText = (EditText) findViewById(R.id.bar_keyboard_text);
		mSearchText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				searchContacts();

				contactListAdapter.notifyDataSetChanged();
			}
		});

		mTextViewChar = (TextView) findViewById(R.id.contact_group_char);


		//getPackageManager().checkPermission("android.permission.READ_CONTACTS",getPackageName().toString());

		//boolean permission = (PackageManager.PERMISSION_GRANTED == getPackageManager().checkPermission("android.permission.READ_CONTACTS", getPackageName()));

		//int permission1 = checkPermission("android.permission.READ_CONTACTS", Binder.getCallingPid(), Binder.getCallingUid());

		//checkPermission()

		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; // 联系人Uri；
		// 查询的字段
		String[] projection = { ContactsContract.CommonDataKinds.Phone._ID,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.DATA1, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
				ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
				ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,ContactsContract.CommonDataKinds.Phone.LABEL};
		asyncQueryHandler.startQuery(0, null, uri, projection, null, null, " display_name DESC");

		mCommitTxt.setOnClickListener(this);
		mSettingTxt.setOnClickListener(this);

//		try {
//			cursor = cr.query(uri, projection, null, null, null);
//		}catch (Exception e) {
//
//			DebugTools.getDebug().debug_d("Exception", "Could not add a new contact: " + e.getMessage());
//		}

		Intent data = getIntent();
		if (data != null && data.getIntExtra("from",-1) >= 0) {

			from_type = data.getIntExtra("from",-1);
		}

		if (data != null && data.getLongExtra("appId", -1) >= 0) {

			app_id = data.getLongExtra("appId",-1);
		}
	}

	@Override
	public void onResume() {
		super.onResume();

	}


	private void searchContacts() {
		String searchText = mSearchText.getText().toString().toLowerCase();
		mSearchContacts.clear();

		if (searchText.isEmpty()) {
			for (ContactEntity entity : mTotalContacts) {
				mSearchContacts.add(entity);
			}
		} else {
			boolean isChinese = Utils.isChinese(searchText);
			boolean isNumeric = Utils.isNumeric(searchText);

			for (ContactEntity entity : mTotalContacts) {
				boolean isMatch = false;

				if (isChinese) {
					isMatch = entity.userName.contains(searchText);
				} else {
					if (isNumeric)
						isMatch = entity.phone.contains(searchText) ;
//					else
//						isMatch = entity.getUserNamePY().contains(searchText) || entity.getUserNameHeadPY().contains(searchText);
				}

				if (isMatch)
					mSearchContacts.add(entity);
			}
		}
	}

	private void generateIndexMap(List<ContactEntity> dataList) {
		if (dataList == null || dataList.size() == 0)
			return;

		mIndexMap.clear();
		mLetterList.clear();
		char previous = '.';

		for (int i = 0; i < dataList.size(); i++) {
			char current = dataList.get(i).getSortkey();

			if (previous != current) {
				mIndexMap.put(current, i);
				mLetterList.add(current);
				previous = current;
			}
		}
	}

	private int getGroupPos(char groupChar) {
		if (mIndexMap == null || mIndexMap.size() == 0)
			return -1;

		if (mIndexMap.containsKey(groupChar))
			return mIndexMap.get(groupChar);

		return -1;
	}

	@Override
	public void OnTouchDown() {
		mTextViewChar.setVisibility(View.VISIBLE);
	}

	@Override
	public void OnTouchMove(int yPos) {
		mTextViewChar.setY(yPos);
	}

	@Override
	public void OnTouchUp() {
		mTextViewChar.setVisibility(View.GONE);
	}

	@Override
	public void OnLetterChanged(Character s, int index) {
		mTextViewChar.setText(String.valueOf(s));

		int pos = getGroupPos(s);

		if (index < previousIndex)
			//mLinearLayoutManager.scrollToPosition(pos);
			recyclerView.smoothScrollToPosition(pos);
		else
			//mLinearLayoutManager.scrollToPositionWithOffset(pos, 0);
			recyclerView.smoothScrollToPositionFromTop(pos, 0);

		previousIndex = index;
	}

	@Override
	public void onClick(View v) {
		int senderID = v.getId();

		switch (senderID) {
			case R.id.bar_keyboard_btn:
				changeInputState();
				break;

			case R.id.invite_friend_back:
				mBaseActivity.finish();
				break;

			case R.id.invite_friend_commit_text:

				if (from_type == CUSTOMER_INFO) {

					if (contactListAdapter.selectPhoneArray != null &&
							contactListAdapter.selectPhoneArray.size() > 0) {

						sendMsgTofFriend();
					} else {

						Toast.makeText(mBaseActivity, R.string.str_select_friend, Toast.LENGTH_SHORT).show();
					}
				}
				else {

					if ((contactListAdapter.selectPhoneArray == null ||
							contactListAdapter.selectPhoneArray.size() <= 0) &&
							(selectPhoneArray == null ||
							selectPhoneArray.size() <= 0)) {

						Toast.makeText(mBaseActivity, R.string.str_select_friend, Toast.LENGTH_SHORT).show();

					} else {

						sendMsgTofFriend();
					}
				}

				break;


			case R.id.invite_friend_setting_text:

				Intent intent = new Intent(Settings.ACTION_SETTINGS);
				startActivity(intent);
				break;
		}
	}

	private void changeInputState() {
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}


	private void initData() {

		if(!Utils.isConnected(this)) {
			return ;
		}

		WaitDialog.showWaitDialog(this, R.string.str_loading_msg);

		final SavePhoneBook request1 = new SavePhoneBook(mBaseActivity,phone_numbers,customer.sn);
		new AsyncTask<Object,Object,Integer>(){

			@Override
			protected Integer doInBackground (Object... params) {

				return request1.connectUrl();
			}

			@Override
			protected void onPostExecute(Integer retId) {
				super.onPostExecute(retId);


				//Toast.makeText(mBaseActivity,"msg----->>>"+request1.failMsg,Toast.LENGTH_SHORT).show();

				//Toast.makeText(mBaseActivity,"code----->>>"+retId,Toast.LENGTH_SHORT).show();

				if(BaseRequest.REQ_RET_F_NO_DATA == retId ) {


				}
				else if (BaseRequest.REQ_RET_OK == retId) {

					if (request1.contactList!= null && request1.contactList.size() > 0) {

						mTotalContacts = request1.contactList;
						mSearchContacts = new ArrayList<ContactEntity>();

						//mSearchContacts.clear();
						for (ContactEntity entity : mTotalContacts) {
							mSearchContacts.add(entity);
						}
						generateIndexMap(mTotalContacts);
						letterSideBar.Init(mLetterList);
						contactListAdapter = new ContactListAdapter(mBaseActivity,mSearchContacts,mCommitTxt);
						recyclerView.setAdapter(contactListAdapter);

						ViewGroup.LayoutParams lp1 = recyclerView.getLayoutParams();
						int height =(int) (request1.contactList.size()*70*mBaseActivity.getResources().getDisplayMetrics().scaledDensity);

						if (height > mBaseActivity.getResources().getDisplayMetrics().heightPixels) {

							height = mBaseActivity.getResources().getDisplayMetrics().heightPixels;
						}
						lp1.height = height;
						recyclerView.setLayoutParams(lp1);

						//mCommitTxt.setEnabled(false);
						//mCommitTxt.setClickable(false);

						findViewById(R.id.bar_keyboard).setVisibility(View.VISIBLE);
						findViewById(R.id.invite_friend_commit_relative).setVisibility(View.VISIBLE);
					}


					if (request1.registedPerson != null && request1.registedPerson.size() > 0) {

						registeredFriendCountTxt.setText(request1.registedPerson.size() + "位好友已注册");
						registeredFriendList.setAdapter(new MyFriendAdapter(request1.registedPerson));

						ViewGroup.LayoutParams lp = registeredFriendList.getLayoutParams();

						lp.height =(int) (request1.registedPerson.size()*70*mBaseActivity.getResources().getDisplayMetrics().scaledDensity);

						registeredFriendList.setLayoutParams(lp);

						findViewById(R.id.invite_friend_registered_friend_linear).setVisibility(View.VISIBLE);
					}

				}

				WaitDialog.dismissWaitDialog();
			}

		}.execute(null, null, null);
	}

	private void sendMsgTofFriend() {

		if(!Utils.isConnected(this)) {
			return ;
		}

		WaitDialog.showWaitDialog(this, R.string.str_loading_msg);

		StringBuilder ab = new StringBuilder();

		if (contactListAdapter.selectPhoneArray != null && contactListAdapter.selectPhoneArray.size() > 0) {

			for (int i=0 ;i < contactListAdapter.selectPhoneArray.size();i++) {

				ab.append(contactListAdapter.selectPhoneArray.get(i));
				ab.append(",");

			}
		}

		if (selectPhoneArray != null && selectPhoneArray.size() > 0) {

			for (int i=0 ;i < selectPhoneArray.size();i++) {

				ab.append(selectPhoneArray.get(i));
				ab.append(",");

			}
		}

		final SendMsgToFriend request1 = new SendMsgToFriend(mBaseActivity,ab.toString(),customer.sn,from_type,app_id);
		new AsyncTask<Object,Object,Integer>(){

			@Override
			protected Integer doInBackground (Object... params) {

				return request1.connectUrl();
			}

			@Override
			protected void onPostExecute(Integer retId) {
				super.onPostExecute(retId);

				Toast.makeText(mBaseActivity, request1.getFailMsg(), Toast.LENGTH_SHORT).show();
//				if(BaseRequest.REQ_RET_F_NO_DATA == retId ) {
//
//
//				}
//				else if (BaseRequest.REQ_RET_OK == retId) {
//
//
//				}

				WaitDialog.dismissWaitDialog();
			}

		}.execute(null, null, null);
	}


	private class MyAsyncQueryHandler extends AsyncQueryHandler {

		public MyAsyncQueryHandler(ContentResolver cr) {
			super(cr);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if (cursor != null && cursor.getCount() > 0) {
				StringBuilder sb = new StringBuilder();
				mTotalContacts = new ArrayList<ContactEntity>();
				cursor.moveToFirst(); // 游标移动到第一项

				sb.append("{");
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					String name = cursor.getString(1);
					String number = cursor.getString(2);
					String sortKey = cursor.getString(3);
					int contactId = cursor.getInt(4);
					Long photoId = cursor.getLong(5);
					String lookUpKey = cursor.getString(6);
					String lable = cursor.getString(7);

					//String phone_str = "";
					if (number.contains("+86")) {

						number = number.replace("+86", "");
					}

					if (number.contains("17951")) {

						number = number.replace("17951", "");
					}

					number = number.replace(" ", "");

					boolean result = Utils.isMobileNum(number);
					if (result) {

						sb.append(number);
						sb.append(":");
						sb.append("\"" + name + "\"");
						sb.append(",");
					}


				}

				sb.append("}");

				phone_numbers = sb.toString();

				initData();

			}
			else {

				findViewById(R.id.invite_friend_commit_relative).setVisibility(View.GONE);
				findViewById(R.id.invite_friend_scroll).setVisibility(View.GONE);
				findViewById(R.id.invite_firend_setting_linear).setVisibility(View.VISIBLE);
			}

			super.onQueryComplete(token, cookie, cursor);
		}

	}


	private class MyFriendAdapter extends IgBaseAdapter {

		private ArrayList<HashMap<String ,String>> list;

		public MyFriendAdapter(ArrayList<HashMap<String ,String>> list) {

			this.list = list;
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

			final int index = arg0;
			ViewHolder holder ;

			if (arg1 == null) {

				holder = new ViewHolder();
				arg1 = mBaseActivity.getLayoutInflater().inflate(R.layout.customer_info_my_registered_friend_item, null);

				holder.avatarImage = (ImageView) arg1.findViewById(R.id.customer_info_my_follower_headImage);
				holder.userName = (TextView)arg1.findViewById(R.id.customer_info_my_follower_nameText);
				holder.dynamic = (TextView)arg1.findViewById(R.id.customer_info_my_follower_dynamicText);
				holder.attention = (TextView)arg1.findViewById(R.id.customer_info_my_follower_is_attention_text);
				holder.select = (ImageView) arg1.findViewById(R.id.contact_select);

				if (from_type == CUSTOMER_INFO) {

					holder.select.setVisibility(View.GONE);
					holder.attention.setVisibility(View.VISIBLE);
				}
				else {

					holder.select.setVisibility(View.VISIBLE);
					holder.attention.setVisibility(View.GONE);
				}

				arg1.setTag(holder);

			} else {

				holder = (ViewHolder)arg1.getTag();
			}

			loadAvatar(mBaseActivity, list.get(arg0).get("sn"), holder.avatarImage);

			final String phone_str = list.get(arg0).get("phone");
			holder.userName.setText(list.get(arg0).get("nickName"));
			holder.dynamic.setText(Utils.getHintPhone(phone_str));

//			if(list.get(arg0).attentionOrNot == 0) {
//
//				holder.attention.setText(R.string.str_friend_attention);//color_tab_green
//				holder.attention.setTextColor(getResources().getColor(R.color.color_tab_green));
//				holder.attention.setBackgroundResource(R.drawable.attent_color);
//
//			} else if (list.get(arg0).attentionOrNot == 1) {

				holder.attention.setText(R.string.str_friend_attented);
				holder.attention.setTextColor(getResources().getColor(R.color.color_white));
				holder.attention.setBackgroundResource(R.drawable.attented_color);
//			}

			if (selectPhoneArray.contains(phone_str)) {

				holder.select.setImageResource(R.drawable.pin_choice);

			} else {

				holder.select.setImageResource(R.drawable.pin_un);
			}



			holder.select.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (selectPhoneArray.contains(phone_str)) {

						selectPhoneArray.remove(phone_str);

					} else {

						selectPhoneArray.add(phone_str);
					}

					notifyDataSetChanged();
				}
			});


			//final String sn_tips = list.get(arg0).sn;

			return arg1;
		}

	}

	private static class ViewHolder {

		public ImageView avatarImage ;
		public TextView userName ;
		public TextView dynamic;
		public TextView attention ;
		ImageView select;
	}
}
