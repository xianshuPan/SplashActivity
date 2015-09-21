package com.hylg.igolf.ui.hall;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.cs.data.Customer;
import com.hylg.igolf.cs.data.OpenInvitationInfo;

import android.os.Bundle;

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

}
