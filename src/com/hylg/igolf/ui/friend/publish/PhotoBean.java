package com.hylg.igolf.ui.friend.publish;

import java.io.Serializable;

public class PhotoBean implements Serializable {

	private static final long serialVersionUID = -4887284841243544423L;

	private PicUrls photo;
	
	private StatusContent status;

	public PicUrls getPhoto() {
		return photo;
	}

	public void setPhoto(PicUrls photo) {
		this.photo = photo;
	}

	public StatusContent getStatus() {
		return status;
	}

	public void setStatus(StatusContent status) {
		this.status = status;
	}
	
}
