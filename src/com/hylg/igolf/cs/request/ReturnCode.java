package com.hylg.igolf.cs.request;

/**************提示信息变量***********/
public interface ReturnCode {
	/**异常*/
	final static int REQ_RET_EXCEP = -1;
	/**成功*/
	final static int REQ_RET_OK = 0;
	/**返回通用失败标识*/
	final static int REQ_RET_FAIL = 1;
	/**其他失败标识*/
	final static int REQ_RET_FAIL_OHTER = 100;
	/**密码输入错误*/
	final static int REQ_RET_F_PWD_ERROR = REQ_RET_FAIL_OHTER + 1;
	/**登录时，用户不存在*/
	final static int REQ_RET_F_ACCOUNT_NOT_EXIST = REQ_RET_F_PWD_ERROR + 1;
	/**注册时，账户已注册*/
	final static int REQ_RET_F_REG_ACCOUNT_EXIST = REQ_RET_F_ACCOUNT_NOT_EXIST + 1;
	/**注册个人信息时，昵称重复*/
	final static int REQ_RET_F_REG_NICKNAME_REPEAT = REQ_RET_F_REG_ACCOUNT_EXIST + 1;
	/**重置密码时，输入的手机号未注册*/
	final static int REQ_RET_F_RESET_NOT_REGISTERD = REQ_RET_F_REG_NICKNAME_REPEAT + 1;
	/**搜索的大厅开放式约球列表已更新*/
	final static int REQ_RET_F_OPEN_LIST_REFRESH = REQ_RET_F_RESET_NOT_REGISTERD + 1;
	/**列表信息无数据*/
	final static int REQ_RET_F_NO_DATA = REQ_RET_F_OPEN_LIST_REFRESH + 1;
	
	/**大厅开放式约球详情中，进行申请失败：对方已接受其他人的申请，已无申请权限*/
	final static int REQ_RET_F_OPEN_ACCEPT_OTHER = REQ_RET_F_NO_DATA + 1;
	/**约球详情中，申请、取消申请、撤销失败：已过期*/
	final static int REQ_RET_F_APP_OVERDUE = REQ_RET_F_OPEN_ACCEPT_OTHER + 1;
	/**约球详情中，接受失败：对方已取消申请或撤销约球*/
	final static int REQ_RET_F_APP_REVOKE = REQ_RET_F_APP_OVERDUE + 1;
	/**约球详情中，取消申请、撤销失败：对方已接受*/
	final static int REQ_RET_F_APP_ACCEPT = REQ_RET_F_APP_REVOKE + 1;
	/**约球详情中，取消申请、撤销失败：进入准备阶段*/
	final static int REQ_RET_F_APP_PREPARE = REQ_RET_F_APP_ACCEPT + 1;
	/**约球详情中，签到，记分登记失败：不在有效范围内*/
	final static int REQ_RET_F_APP_OUTSIDE_COURSE = REQ_RET_F_APP_PREPARE + 1;
	/**未入选排行榜：符合筛选条件，但未进入榜单*/
	final static int REQ_RET_F_RANK_OUT_OF = REQ_RET_F_APP_OUTSIDE_COURSE + 1;
	/**未入选排行榜：不符合筛选条件*/
	final static int REQ_RET_F_RANK_NOT_FIT = REQ_RET_F_RANK_OUT_OF + 1;
	/**发起约球失败：不足2小时*/
	final static int REQ_RET_F_START_INVITE_LESS_TWO = REQ_RET_F_RANK_NOT_FIT + 1;
	
	/**客户端本地定义错误信息*/
	final static int REQ_RET_F_C_START = 2000;
	final static int REQ_RET_F_CUS_DEF = REQ_RET_F_C_START + 1;
	final static int REQ_RET_F_JSON_EXCEP = REQ_RET_F_CUS_DEF + 1;
	final static int REQ_RET_F_UPDATE_AVATAR_FAIL = REQ_RET_F_JSON_EXCEP + 1;
	/**地址异常*/
	final static int REQ_RET_F_URL_ERROR = REQ_RET_F_UPDATE_AVATAR_FAIL + 1;
	/**网络异常*/
	final static int T_RESULT_EX_CONNECT_TIMEOUT = REQ_RET_F_URL_ERROR + 1;
}
