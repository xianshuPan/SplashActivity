package com.hylg.igolf.ui.customer;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hylg.igolf.R;

import java.util.List;

/**
 * 联系人绑定类
 */
class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int NORMAL_ITEM = 0;
    private static final int GROUP_ITEM = 1;
    private Activity mBaseActivity;
    private List<ContactEntity> mDataList;
    private LayoutInflater mLayoutInflater;

    public ContactAdapter(FragmentActivity context, List<ContactEntity> mDataList) {
        this.mBaseActivity = context;
        this.mDataList = mDataList;
        mLayoutInflater = LayoutInflater.from(mBaseActivity);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == NORMAL_ITEM) {
            return new NormalItemHolder(mLayoutInflater.inflate(R.layout.contact_list_item_normal, parent, false));
        } else {
            return new GroupItemHolder(mLayoutInflater.inflate(R.layout.contact_list_item_group, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ContactEntity entity = mDataList.get(position);

        if (null == entity)
            return;

        if (holder instanceof GroupItemHolder) {
            bindGroupItem(entity, (GroupItemHolder) holder);
        } else {
            bindNormalItem(entity, (NormalItemHolder) holder);
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return GROUP_ITEM;

        char current = mDataList.get(position).getSortkey();
        char previous = mDataList.get(position - 1).getSortkey();

        return current != previous ? GROUP_ITEM : NORMAL_ITEM;
    }

    private void bindNormalItem(ContactEntity entity, NormalItemHolder normalItemHolder) {

        normalItemHolder.userName.setText(entity.userName);


        //normalItemHolder.userJob.setText(entity.getJobTitle());
    }

    private void bindGroupItem(ContactEntity entity, GroupItemHolder holder) {
        bindNormalItem(entity, holder);
        holder.userGroup.setText(String.valueOf(entity.getSortkey()));
    }

    private String GetCallNumber(boolean isCallShort, String longNo, String shortNo) {

        if (shortNo.isEmpty())
            return longNo;

        return isCallShort ? shortNo : longNo;
    }

    public class NormalItemHolder extends RecyclerView.ViewHolder {
        TextView userName;
        ImageView userSelect;

        public NormalItemHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.contact_userName);
            userSelect = (ImageView) itemView.findViewById(R.id.contact_select);

            itemView.findViewById(R.id.contact_container).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContactEntity entity = mDataList.get(getPosition());
                    String number =  entity.phone;
                    if (number.isEmpty()) {

                        //Utils.toast(mBaseActivity, R.string.alert_empty_number);

                        return;
                    }

                    //DoAction.Call(mBaseActivity, number);
                    //mContactListFragment.isNeedToAddContact = true;
                    //mContactListFragment.contactToAdd = entity;
                }
            });

            itemView.findViewById(R.id.contact_container).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ContactEntity entity = mDataList.get(getPosition());

                    return true;
                }
            });
        }
    }

    public class GroupItemHolder extends NormalItemHolder {
        TextView userGroup;

        public GroupItemHolder(View itemView) {
            super(itemView);
            userGroup = (TextView) itemView.findViewById(R.id.contact_group);
        }
    }
}
