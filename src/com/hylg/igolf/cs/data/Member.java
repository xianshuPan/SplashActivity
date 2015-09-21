package com.hylg.igolf.cs.data;

public class Member extends Customer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5317020281498376693L;
	public int fights;
	public int victories;
	public int inviteCount;
	public int InviteSuccCount;
	public String fightMsg;
	public String scoreMsg;
	public MemInviteNoteInfo[] inviteNotes = null;
}
