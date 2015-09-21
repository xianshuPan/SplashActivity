package com.hylg.igolf.cs.data;

import java.io.Serializable;

public class OpenInviteDetail implements Serializable {
	private static final long serialVersionUID = 8223141563981138668L;
	
	public InviteRoleInfo inviter;
	public String message;
	public int stake;
	public int paymentType;
	public boolean isApplied;
	
	public InviteRoleInfo invitee = null;
	public InviteRoleInfo inviteeone = null;
	public InviteRoleInfo inviteetwo = null;
	
	public String log() {
		return "OpenInviteDetail {" +
					"\n	inviter: " + inviter.log() +
					"\n message: " + message +
					"\n stake: " + stake +
					"\n paymentType: " + paymentType +
					"\n isApplied: " + isApplied;
	}
}
