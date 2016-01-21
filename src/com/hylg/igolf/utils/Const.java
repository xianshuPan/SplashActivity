package com.hylg.igolf.utils;


public class Const {
	
	// 配置信息中，全部项key
	public static final String CFG_ALL_INDUSTRY = "INDUSTRY_00";
	public static final String CFG_ALL_REGION = "AREA_00";
	// 其他各选项中，不限的值
	// 付款方式
	public static final int PAY_TYPE_ALL = -1;
	// 球注
	public static final int STAKE_ALL = -1;
	// 开球日期
	public static final int TEE_DATE_ALL = 0;
	// 开球时间
	public static final int TEE_TIME_ALL = 0;
	public static final int TEE_TIME_AM = 1;
	public static final int TEE_TIME_PM = 2;
	
	// 性别
	public static final int SEX_ALL = -1;
	public static final int SEX_MALE = 0;
	public static final int SEX_FEMALE = 1;
	
	
	//教练类型
	public static final int COACH_TYPE_ALL = -1;
	public static final int COACH_TYPE_PROFESSIONAL = 0;
	public static final int COACH_TYPE_HOBBY = 1;
	
	public static final int ALBUM_ORIGINAL = 1;
	public static final int ALBUM_THUMBNAIL = 0;
	
	public static final int SCORE_ORIGINAL = 1;
	public static final int SCORE_THUMBNAIL = 0;
	
	// 成绩有效
	public static final int SCORE_VALIDE = 0;
	
	public static final int INVALID_SELECTION_INT = -100;
	
	/**
	 * 监听广播事件
	 */
	public static final String IG_ACTION_NEW_ALERTS = "ig_action_new_alerts"; // 新提醒主动获取广播
	public static final String IG_ACTION_FORE_JPUSH_NOTIFY = "ig_action_fore_jpush_notify"; // 前台页面，获取极光推送广播
//	public static final String IG_ACTION_BACK_JPUSH_NOTIFY = "ig_action_back_jpush_notify"; // 后台页面，获取极光推送广播
	public static final String IG_ACTION_MY_INVITE_JPUSH_NOTIFY = "ig_action_my_invite_jpush_notify"; // 如果列表存在，更新
	
	/**
	 * bundle key
	 */
	public static final String BUNDLE_KEY_SELECT_REGION = "select_region";
	public static final String BUNDLE_KEY_SELECT_INDUSTRY = "select_industry";
	public static final String BUNDLE_KEY_MY_INFO_CHANGED = "my_info_changed";
//	public static final String BUNDLE_KEY_DELETE_ALBUM = "customer_delete_album";
	/**
	 * request code.
	 */
	public static final int REQUST_CODE_UPDATE = 1;
	public static final int REQUST_CODE_PHOTE_TAKE_PHOTO = REQUST_CODE_UPDATE + 1;
	public static final int REQUST_CODE_PHOTE_GALLERY = REQUST_CODE_PHOTE_TAKE_PHOTO + 1;
	public static final int REQUST_CODE_PHOTE_CROP = REQUST_CODE_PHOTE_GALLERY + 1;
	public static final int REQUST_CODE_INVITE_PLAN = REQUST_CODE_PHOTE_CROP + 1;
	public static final int REQUST_CODE_INVITE_DETAIL_OPEN = REQUST_CODE_INVITE_PLAN + 1;
	public static final int REQUST_CODE_SIGNATURE_MY = REQUST_CODE_INVITE_DETAIL_OPEN + 1;
	public static final int REQUST_CODE_MODIFY_MY_INFO = REQUST_CODE_SIGNATURE_MY + 1;
	public static final int REQUST_CODE_USER_GUIDE = REQUST_CODE_MODIFY_MY_INFO + 1;
	public static final int REQUST_CODE_INVITE_OPEN = REQUST_CODE_MODIFY_MY_INFO + 1;

	/**
	 * result
	 
	// 大厅约球
	public static final String REQUST_KEY_INVITE_DETAIL_OPEN = "invite_detail_open";
	public static final int RESULT_INVITE_DETAIL_ACCEPT_OTHER = 1;
	public static final int RESULT_INVITE_DETAIL_ACCEPT_YOU = RESULT_INVITE_DETAIL_ACCEPT_OTHER + 1;
	public static final int RESULT_INVITE_DETAIL_APP_PREPARE = RESULT_INVITE_DETAIL_ACCEPT_YOU + 1;
	public static final int RESULT_INVITE_DETAIL_APP_OVERDUE = RESULT_INVITE_DETAIL_APP_PREPARE + 1;
	*/
	/******约球类型******/
	public final static int INVITE_TYPE_STS = 0;
	public final static int INVITE_TYPE_OPEN = 1;
    /******大厅的开放式约球*****/
	public final static int INVITE_OPEN_GREEN_WAIT = 0;//虚位以待
	public final static int INVITE_OPEN_YELLOW_APPLYING = INVITE_OPEN_GREEN_WAIT + 1;//申请进行中
	public final static int INVITE_OPEN_RED_ACCEPTED = INVITE_OPEN_YELLOW_APPLYING + 1;//申请结束
	/******我的约球*****/
	public final static int MY_INVITE_COMMON = 100;//公用状态开始
	public final static int MY_INVITE_WAITAPPLY = MY_INVITE_COMMON + 1;//等申请			g
	public final static int MY_INVITE_APPlYED = MY_INVITE_WAITAPPLY + 1;//已申请			g
	public final static int MY_INVITE_WAITACCEPT = MY_INVITE_APPlYED + 1;//待接受		y
	public final static int MY_INVITE_HAVEAPPLY = MY_INVITE_WAITACCEPT + 1;//有申请		y
	public final static int MY_INVITE_ACCEPTED = MY_INVITE_HAVEAPPLY + 1;//已接受		g
	public final static int MY_INVITE_CANCELED = MY_INVITE_ACCEPTED + 1;//已撤销			r
	public final static int MY_INVITE_REVOKED = MY_INVITE_CANCELED + 1;//已撤约			r
	// 待签到时，同时显示撤约，无不可撤约(-2~0小时)标记，请求时，后台返回不可撤约信息。
	public final static int MY_INVITE_WAITSIGN = MY_INVITE_REVOKED + 1;//待签到			y
	public final static int MY_INVITE_PLAYING = MY_INVITE_WAITSIGN + 1;//进行中			y
	public final static int MY_INVITE_COMPLETE = MY_INVITE_PLAYING + 1;//已完成			g
	public final static int MY_INVITE_REFUSED = MY_INVITE_COMPLETE + 1;//已拒绝			r
	public final static int MY_INVITE_SIGNED = MY_INVITE_REFUSED + 1;//已签到 记录签到后到开始打球这一段时间的状态
	
	
	/******我的教学订单状态*****/
	public final static int MY_TEACHING_COMMON = 0;//公用状态开始
	public final static int MY_TEACHING_WAITAPPLY = MY_TEACHING_COMMON ;//等申请
	public final static int MY_TEACHING_REVOKE = MY_TEACHING_WAITAPPLY + 1;//已申请
	public final static int MY_TEACHING_REFUSE = MY_TEACHING_REVOKE + 1;//待接受

	public final static int MY_TEACHING_ACCEPTED = MY_TEACHING_REFUSE + 1;//有申请
	//public final static int MY_TEACHING_START = MY_TEACHING_ACCEPTED + 1;//已接受
	//public final static int MY_TEACHING_END = MY_TEACHING_START + 1;//已撤销

	public final static int MY_TEACHING_FINISHED = MY_TEACHING_ACCEPTED + 1;//已撤约

	public final static int MY_TEACHING_CANCEL = MY_TEACHING_FINISHED + 1;//已撤约
	// 待签到时，同时显示撤约，无不可撤约(-2~0小时)标记，请求时，后台返回不可撤约信息。
	
	
	public final static int                     AVATAR = 1,
												ID_FRONT = 2,
												ID_BACK = 3,
												GRADUATE = 4,
												CERTIFICATE = 5,
												AWARD = 6;
	
	public final static String					FIRST_START_TIME = "first_start_time",
												START_TIME = "start_time",
												PAUSE_TIME = "pause_time",
												PAUSED_TIMES = "paused_times",
												END_TIME = "end_time",
												APP_ID = "app_id";
	
	public final static int                     PROFESSIONAL_COACH = 0,
												HOBBY_COACH = 1;

	public final static int                    	BING_CARD_AND_PASSWORD = 0,
												UPDATE_CARD = 1,
												RESET_PAY_PASSWORD = 2;

	public final static int                     PAY_TYPE_SELECT = 101;
	public final static int                     PAY_TYPE_ALIPAY = 102;
	public final static int                     PAY_TYPE_WECHAT = 103;
	public final static int                     PAY_TYPE_UNION = 104;


	public static final String 					ANDROID = "Android_";


}
