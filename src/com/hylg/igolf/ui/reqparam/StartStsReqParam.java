package com.hylg.igolf.ui.reqparam;

import java.io.Serializable;
import java.util.ArrayList;

import com.hylg.igolf.ui.hall.data.PlanSubmitInfo;

public class StartStsReqParam implements Serializable {
	private static final long serialVersionUID = -1364064786831716903L;

	public String sn; // 会员编号
	public String inviteeSn; // 被邀约人会员编号
	public ArrayList<PlanSubmitInfo> plans; // 约球方案
	public int stake; // 球注
	public int payType; // 付费方式
	public String msg; // 信息
	public String appName; // 称呼
}
