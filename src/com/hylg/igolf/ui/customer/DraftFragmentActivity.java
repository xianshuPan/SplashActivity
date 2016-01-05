package com.hylg.igolf.ui.customer;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.ui.friend.FriendMessageNewActivity;
import com.hylg.igolf.ui.friend.publish.PublishBean;
import com.hylg.igolf.ui.friend.publish.PublishDB;
import com.hylg.igolf.ui.friend.publish.PublishManager;
import com.hylg.igolf.ui.friend.publish.PublishService;
import com.hylg.igolf.ui.friend.publish.PublishType;
import com.hylg.igolf.ui.friend.publish.TaskException;
import com.hylg.igolf.ui.friend.publish.WorkTask;
import com.hylg.igolf.ui.widget.IgBaseAdapter;
import com.hylg.igolf.utils.Const;


import net.tsz.afinal.FinalBitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 草稿箱
 *
 * @author wangdan
 */
public class DraftFragmentActivity extends FragmentActivity
        implements OnItemClickListener, View.OnClickListener {

    ArrayList<PublishBean> publishBeanArrayList;

    private DraftAdapter mDraftAdapter = null;

    private ListView  mListview = null;

    private DraftFragmentActivity mContext = null;

    private ImageButton mBack;

//    public static DraftFragment newInstance() {
//        return new DraftFragment();
//    }

    public static void startDraftFragmentActivity (Fragment context) {

        Intent intent = new Intent(context.getActivity(), DraftFragmentActivity.class);
        context.startActivity(intent);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PublishBean bean = publishBeanArrayList.get(position);

        switch (bean.getType()) {
            case status:
                //PublishActivity.publishStatus(getActivity(), bean);

                FriendMessageNewActivity.startFriendMessageNewActivity(mContext,bean);
                break;
//            case statusRepost:
//                PublishActivity.publishStatusRepost(getActivity(), bean, bean.getStatusContent());
//                break;
//            case commentReply:
//                PublishActivity.publishCommentReply(getActivity(), bean, bean.getStatusComment(), false);
//                break;
//            case commentCreate:
//                PublishActivity.publishStatusComment(getActivity(), bean, bean.getStatusContent());
//                break;
//            default:
//                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.as_ui_draft);

        mContext = this;

        mBack = (ImageButton)findViewById(R.id.as_ui_draft_back);
        mListview = (ListView) findViewById(R.id.draft_listView);
        mListview.setOnItemClickListener(this);
        mBack.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(PublishManager.ACTION_PUBLISH_CHANNGED);
        registerReceiver(receiver, filter);

        new DraftTask(RefreshMode.reset).execute();


        new DraftTask(RefreshMode.reset).execute();
    }

    @Override
    public void onPause() {
        super.onPause();

        unregisterReceiver(receiver);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && PublishManager.ACTION_PUBLISH_CHANNGED.equals(intent.getAction()))
                new DraftTask(RefreshMode.reset).execute();
        }

    };



    class DraftTask extends WorkTask<Void, Void, ArrayList<PublishBean>> {

        public DraftTask(RefreshMode mode) {


        }

        @Override
        protected void onPrepare() {
            super.onPrepare();

        }

        @Override
        protected void onFailure(TaskException exception) {
            super.onFailure(exception);

        }

        @Override
        protected void onFinished() {
            super.onFinished();

        }

        @Override
        protected void onSuccess (ArrayList<PublishBean> arrayList) {

            super.onSuccess(arrayList);

            publishBeanArrayList = arrayList;

            mDraftAdapter = new DraftAdapter(publishBeanArrayList);
            mListview.setAdapter(mDraftAdapter);
        }


        @Override
        public ArrayList<PublishBean> workInBackground(Void... params) throws TaskException {

            return PublishDB.getPublishList(MainApp.getInstance().getUser());
        }
    }

    class DraftboxItemView extends AbstractItemView<PublishBean> {

        private FinalBitmap finalBit;

        @ViewInject(id = R.id.txtType)
        TextView txtType;
        @ViewInject(id = R.id.txtTiming)
        TextView txtTiming;
        @ViewInject(id = R.id.txtContent)
        TextView txtContent;
        @ViewInject(id = R.id.txtError)
        TextView txtError;
        @ViewInject(id = R.id.container)
        View container;
        @ViewInject(id = R.id.btnDel)
        View btnDel;
        @ViewInject(id = R.id.btnResend)
        View btnResend;

        @ViewInject(id = R.id.tips_image)
        ImageView tips_image;

        public DraftboxItemView () {

            finalBit = FinalBitmap.create(mContext);
        }

        @Override
        public int inflateViewId() {
            return R.layout.as_item_draft;
        }

        @Override
        public void bindingData(View convertView, PublishBean data) {
            //txtTiming.setVisibility(View.GONE);

            PublishType type = data.getType();
            if (type == PublishType.status) {
                txtType.setText(R.string.draft_type_status);
                if (data.getTiming() > 0) {
                    txtTiming.setText(DateUtils.formatDate(data.getTiming(), getString(R.string.draft_date_format)));
                    txtTiming.setVisibility(View.VISIBLE);
                }
            } else if (type == PublishType.commentCreate)
                txtType.setText(R.string.draft_type_create_cmt);
            else if (type == PublishType.commentReply)
                txtType.setText(R.string.draft_type_reply_cmt);
            else if (type == PublishType.statusRepost)
                txtType.setText(R.string.draft_type_repost_status);

            txtContent.setText(data.getText());

//            if (data.getTiming() > 0 && data.getTiming() < System.currentTimeMillis()) {
//                txtError.setVisibility(View.VISIBLE);
//
//                txtError.setText(R.string.draft_timing_expired);
//            } else {
                if (!TextUtils.isEmpty(data.getErrorMsg()))
                    txtError.setText(data.getErrorMsg());
                txtError.setVisibility(TextUtils.isEmpty(data.getErrorMsg()) ? View.GONE : View.VISIBLE);
//            }

//            if (bizFragment != null)
//                bizFragment.bindOnTouchListener(txtContent);

            if (data.getPics() != null && data.getPics().length > 0) {

                tips_image.setVisibility(View.VISIBLE);
               // tips_image.setImageResource();

                finalBit.display(tips_image, "file:///"+data.getPics()[0]);
            }

            btnDel.setTag(data);
            btnDel.setOnClickListener(mContext);
            btnResend.setTag(data);
            btnResend.setOnClickListener(mContext);
        }

    }

    @Override
    public void onClick(View v) {
        PublishBean bean = (PublishBean) v.getTag();
        // 删除
        if (v.getId() == R.id.btnDel) {
            deleteDraft(bean);
        }
        // 重新发送
        else if (v.getId() == R.id.btnResend) {

            PublishService.publish(mContext, bean);
        }
        else if (R.id.as_ui_draft_back == v.getId()) {

            this.finish();
        }
    }

    private void deleteDraft(final PublishBean bean) {
        new AlertDialog.Builder(mContext)
                .setMessage(R.string.draft_del_confirm)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.str_photo_commit, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new WorkTask<Void, Void, Void>() {

                            @Override
                            protected void onPrepare() {

                                ViewUtils.createProgressDialog(mContext, getString(R.string.draft_del_draft_loading), MainApp.getInstance().getResources().getColor(R.color.color_white));
                            }

                            @Override
                            protected void onSuccess(Void result) {

                                publishBeanArrayList.remove(bean);
                                mDraftAdapter.notifyDataSetChanged();

                               // showMessage(R.string.draft_del_draft_hint);

                                String msg = MainApp.getInstance().getResources().getString(R.string.draft_del_draft_hint);
                                ViewUtils.showMessage(msg.toString());

                                Intent intent = new Intent();
                                intent.setAction(PublishManager.ACTION_PUBLISH_CHANNGED);
                                MainApp.getInstance().sendBroadcast(intent);
                            }

                            @Override
                            protected void onFinished() {
                                ViewUtils.dismissProgressDialog();

                                new DraftTask(RefreshMode.reset).execute();
                            }

                            ;

                            @Override
                            public Void workInBackground(Void... params) throws TaskException {
                                try {
                                    Thread.sleep(500);
                                } catch (Exception e) {
                                }

//                                if (bean.getTiming() > 0)
//                                    MyApplication.removePublishAlarm(bean);

                                PublishDB.deletePublish(bean, MainApp.getInstance().getUser());

                                return null;
                            }

                        }.execute();
                    }

                })
                .show();
    }


    abstract public static class AbstractItemView<T extends Serializable> {

        private int position;
        private int size;

        /**
         * ItemView的layoutId
         *
         * @return
         */
        abstract public int inflateViewId();

        /**
         * 绑定ViewHolder视图
         *
         * @param convertView
         */
        public void bindingView(View convertView) {
            InjectUtility.initInjectedView(this, convertView);
        }

        /**
         * 绑定ViewHolder数据
         *
         * @param data
         */
        abstract public void bindingData(View convertView, T data);

        /**
         * 刷新当前ItemView视图
         *
         * @param data
         * @param convertView
         * @param selectedPosition
         */
        public void updateConvertView(T data, View convertView, int selectedPosition) {

        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public int getSize() {
            return size;
        }

        public void recycleView(View view) {

        }

    }

    public enum RefreshMode {
        /**
         * 重设数据
         */
        reset,
        /**
         * 拉取更多
         */
        update,
        /**
         * 刷新最新
         */
        refresh
    }

    private class DraftAdapter extends IgBaseAdapter {

        private ArrayList<PublishBean> list;

        public DraftAdapter(ArrayList<PublishBean> list) {

            this.list = list;
        }


        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup arg2) {
            // TODO Auto-generated method stub

            AbstractItemView<PublishBean> itemViewProcessor;



            if (convertView == null ) {
                itemViewProcessor = new DraftboxItemView();

                convertView = View.inflate(MainApp.getInstance(), itemViewProcessor.inflateViewId(), null);
                convertView.setTag(itemViewProcessor);

                itemViewProcessor.bindingView(convertView);
            } else {
                itemViewProcessor = (AbstractItemView<PublishBean>) convertView.getTag();
            }

            itemViewProcessor.position = position;
            itemViewProcessor.size = publishBeanArrayList.size();

            itemViewProcessor.bindingData(convertView, publishBeanArrayList.get(position));


            return convertView;

        }

    }

}
