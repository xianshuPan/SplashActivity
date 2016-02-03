package com.hylg.igolf.ui.hall;

import android.os.Bundle;
import android.view.View;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.Customer;
import com.hylg.igolf.cs.data.OpenInvitationInfo;
import com.hylg.igolf.ui.view.ShareMenuInviteGolferDetail;

public class InviteDetailOpenActivity extends InviteDetailActivity {
	protected static final String BUNDLE_KEY_OPEN_INVITATION_INFO = "open_invitation_info";

	protected OpenInvitationInfo invitation;
	protected Customer customer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getData();
	}
	
	private void getData() {
		invitation = (OpenInvitationInfo) getIntent().getSerializableExtra(BUNDLE_KEY_OPEN_INVITATION_INFO);
		customer = MainApp.getInstance().getCustomer();
	}
	
	@Override
	public boolean getResultByCallback() {
		return false;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.invite_detail_share_image:

				ShareMenuInviteGolferDetail share = new ShareMenuInviteGolferDetail(this,operBarLl,invitation.inviterSn,invitation.sn);
				share.showPopupWindow();

				break;
			default:
				super.onClick(v);
				break;
		}
	}

}
