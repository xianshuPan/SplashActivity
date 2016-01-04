package com.hylg.igolf.ui.hall.adapter;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;

import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.InviteRoleInfo;
import com.hylg.igolf.cs.data.MyInviteDetail;
import com.hylg.igolf.cs.loader.AsyncImageLoader;
import com.hylg.igolf.cs.loader.AsyncImageLoader.ImageCallback;
import com.hylg.igolf.ui.hall.InviteDetailActivity;
import com.hylg.igolf.ui.member.MemDetailActivityNew;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.Utils;

public class ApplicantsAdapter extends BaseAdapter {
	private InviteDetailActivity context;
	private ArrayList<InviteRoleInfo> applicatants;
	private int curSelect; // 只在接受操作时，作为返回值查询索引；避免通过sn循环查询。
	private String curSn; // 当前已选择的，或默认第一个申请人编号
	/*
	 * 2015.07.07
	 * updater pxs
	 * 选中的申请人从一个变成多个（最多3个）
	 * */
	
	private ArrayList<InviteRoleInfo> selectedApplicantsList;
	
	private boolean selectable;

	/**
	 * 
	 * @param context
	 * @param selectable
	 * @param sn 已经接受申请人的会员编号
	 */
	public ApplicantsAdapter(InviteDetailActivity context, MyInviteDetail detail, boolean selectable, String sn) {
		this.context = context;
		this.applicatants = detail.applicants;
		this.selectable = selectable;
		
		selectedApplicantsList = new ArrayList<InviteRoleInfo>();
		
		if (detail.selectApplicants != null && detail.selectApplicants.size() > 0) {
			
			for(int i=0, size=detail.selectApplicants.size(); i<size; i++) {
				this.selectedApplicantsList.add(detail.selectApplicants.get(i));
			}
			
		} else {
			
			this.selectedApplicantsList.add(detail.applicants.get(0));
		}
		
		
		if(selectable) {
			curSelect = 0;
			curSn = applicatants.get(0).sn;
			
		} else {
			// 撤销等状态，无sn，只显示列表即可
			curSn = null == sn ? "" : sn;
		}
		
	}
	
//	public ApplicantsInfo getSelectedApplicatant() {
//		
//		
//		return applicatants.get(curSelect);
//	}
	
	/*获取选中的申请人*/
	public ArrayList<InviteRoleInfo> getSelectedApplicatant() {
		
			
		return selectedApplicantsList;
			
	}
	
	/**
	 * 操作导致状态变化后，临时设置不可选择。
	 * 退出再进入时，根据实际状态更新页面。
	 * @param able
	 */
	public void setSelectable(boolean able) {
		selectable = able;
		notifyDataSetChanged();
	}
	
	/**
	 * 撤销约球，或操作返回过期时，需更清除sn，及可选状态新页面。
	 */
	public void clearApplicantSn() {
		selectable = false;
		curSn = "";
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return applicatants.size();
	}

	@Override
	public Object getItem(int position) {
		return applicatants.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(null == convertView) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.invite_applicant_item, null);
			holder.itemSelectRegion = (RelativeLayout) convertView.findViewById(R.id.invite_applicant_item_select_region);
			holder.itemDetailBtn = (TextView) convertView.findViewById(R.id.invite_applicant_item_detail_btn);
			holder.itemAvatar = (ImageView) convertView.findViewById(R.id.invite_applicant_item_avatar);
			holder.itemNickname = (TextView) convertView.findViewById(R.id.invite_applicant_item_nickname);
			holder.itemSex = (ImageView) convertView.findViewById(R.id.invite_applicant_item_sex);
			holder.itemImg = (ImageView) convertView.findViewById(R.id.invite_applicant_item_img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		InviteRoleInfo applicant = applicatants.get(position);
		
		//boolean selectedBoolean = curSnList.contains(position);
		
		boolean selectedBoolean = verifyApplicantsSelect(applicant);
		
		// 是否为当前选择项，或者已接受项
//		if(curSn.equals(applicant.sn)) {
//			holder.itemSelectRegion.setSelected(true);
//			Utils.setVisible(holder.itemImg);
//		} else {
//			holder.itemSelectRegion.setSelected(false);
//			Utils.setInvisible(holder.itemImg);
//		}
		
		if(selectedBoolean) {
			
			holder.itemSelectRegion.setSelected(true);
			Utils.setVisible(holder.itemImg);
		} else {
			
			holder.itemSelectRegion.setSelected(false);
			Utils.setInvisible(holder.itemImg);
		}
		
		
		
		// 是否可点击选择
		if(selectable) {
			convertView.setClickable(true);
			convertView.setOnClickListener(new onApplicantItemClickListener(position));
		} else {
			convertView.setClickable(false);
		}
		// 查看详情
		holder.itemDetailBtn.setOnClickListener(new onDetailBtnClickListener(position));
		
		loadAvatar(applicant.sn, applicant.avatar, holder.itemAvatar);
		holder.itemNickname.setText(applicant.nickname);
		if(Const.SEX_MALE == applicant.sex) {
			holder.itemSex.setImageResource(R.drawable.ic_male);
		} else {
			holder.itemSex.setImageResource(R.drawable.ic_female);
		}
		return convertView;
	}
	
	private void loadAvatar(final String sn, String name, final ImageView iv) {
		Drawable avatar = AsyncImageLoader.getInstance().getAvatar(context, sn, name,
				(int) context.getResources().getDimension(R.dimen.avatar_detail_size));
		if(null != avatar) {
			iv.setImageDrawable(avatar);
		} else {
			iv.setImageResource(R.drawable.avatar_no_golfer);
			AsyncImageLoader.getInstance().loadAvatar(context, sn, name,
					new ImageCallback() {
						@Override
						public void imageLoaded(Drawable imageDrawable) {
							if(null != imageDrawable ) {
								iv.setImageDrawable(imageDrawable);
							}
						}
				});
		}
	}
	private class onApplicantItemClickListener implements OnClickListener {
		private int position;
		
		public onApplicantItemClickListener(int position) {
			this.position = position;
		}
		
//		private String sn;
//		
//		public onApplicantItemClickListener(String sn) {
//			this.sn = sn;
//		}
		
		@Override
		public void onClick(View v) {
//			if(curSelect == position) {
//				return ;
//			}
			
			changeSelectStatus(position);
		}
		
	}
	
	private class onDetailBtnClickListener implements OnClickListener {
		private int position;
		
		public onDetailBtnClickListener(int position) {
			this.position = position;
		}
		
		@Override
		public void onClick(View v) {
			MemDetailActivityNew.startMemDetailActivity(context, applicatants.get(position).sn);
			context.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
		}
		
	}
	
	class ViewHolder {
		protected RelativeLayout itemSelectRegion;
		protected TextView itemDetailBtn;
		protected ImageView itemAvatar;
		protected ImageView itemSex;
		protected TextView itemNickname;
		protected ImageView itemImg;
	}

//	private void changeSelectStatus(int position) {
//		curSn = applicatants.get(position).sn;
//		curSelect = position;
//		notifyDataSetChanged();
//	}
	
	private void changeSelectStatus(int index) {

		//boolean contain = curSnList.contains(sn);
		
		boolean contain = verifyApplicantsSelect(applicatants.get(index));
		
		if (contain){
			
			
			if (selectedApplicantsList.size() <= 1) {
				
				Toast.makeText(context, "最少选择一个", Toast.LENGTH_SHORT).show();
				
			} else {
				
				//selectedApplicantsList.remove(applicatants.get(sn));
				String sn = applicatants.get(index).sn;
				for(int i=0, size=selectedApplicantsList.size(); i<size; i++) {
					
					
					String snSelect = selectedApplicantsList.get(i).sn;
					
					if (sn.equalsIgnoreCase(snSelect)) {
						
						selectedApplicantsList.remove(i);
						
						break ;
					}
				}
				
			}
			
		} else {
			
			if (selectedApplicantsList.size() >= 3) {
				
				Toast.makeText(context, "最多只能选3个", Toast.LENGTH_SHORT).show();
				
			} else {
				
				selectedApplicantsList.add(applicatants.get(index));
				
			}
		}
		
		
		/*更新头部的显示*/
		if (selectedApplicantsList != null) {
			
			if (selectedApplicantsList.size() >= 1) {
				
				context.detail.invitee = selectedApplicantsList.get(0);
				
			} else {
				
				context.detail.invitee = null;
			}
			
			if (selectedApplicantsList.size() >= 2) {
				
				context.detail.inviteeone = selectedApplicantsList.get(1);
			} else {
				
				context.detail.inviteeone = null;
			}
			
			if (selectedApplicantsList.size() >= 3) {
				
				context.detail.inviteetwo = selectedApplicantsList.get(2);
			} else {
				
				context.detail.inviteetwo = null;
			}
			
			context.appendCommonData(context.detail.inviter, context.detail.invitee,context.detail.inviteeone,
					context.detail.inviteetwo,context.detail.message, context.detail.paymentType, context.detail.stake);
		}
		
		
		
		
		notifyDataSetChanged();
	}
	
	private boolean  verifyApplicantsSelect (InviteRoleInfo applicant) {
		
		boolean result = false;
		
		if (selectedApplicantsList != null && selectedApplicantsList.size() > 0) {
			
			String sn = applicant.sn;
			
			for(int i=0, size=selectedApplicantsList.size(); i<size; i++) {
				
				
				String snSelect = selectedApplicantsList.get(i).sn;
				
				if (sn.equalsIgnoreCase(snSelect)) {

					return true;
				}
			}
			
		}
		
		return result ;
		
	}
	
}
