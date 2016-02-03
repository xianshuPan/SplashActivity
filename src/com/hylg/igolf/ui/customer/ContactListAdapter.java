package com.hylg.igolf.ui.customer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hylg.igolf.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 联系人绑定类
 */
public class ContactListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<ContactEntity> list;
    private HashMap<Character, Integer> alphaIndexer; // 字母索引
    private String[] sections; // 存储每个章节
    private Context ctx; // 上下文

    public ArrayList<String> selectPhoneArray ;

    private TextView mCommitTxt = null;

    public ContactListAdapter(Context context, List<ContactEntity> list,TextView commitTxt) {
        this.ctx = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.alphaIndexer = new HashMap<Character, Integer>();
        this.sections = new String[list.size()];

        for (int i = 0; i < list.size(); i++) {
            // 得到字母
            char name = list.get(i).getSortkey();
            if (!alphaIndexer.containsKey(name)) {
                alphaIndexer.put(name, i);
            }
        }

        mCommitTxt = commitTxt;
        selectPhoneArray = new ArrayList<String>();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void remove(int position) {
        list.remove(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.contact_list_item, null);
            holder = new ViewHolder();
            holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.select = (ImageView) convertView.findViewById(R.id.contact_select);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ContactEntity contact = list.get(position);
        String name = contact.userName;
        final String number = contact.phone;

        if (selectPhoneArray.contains(number)) {

            holder.select.setImageResource(R.drawable.pin_choice);

        } else {

            holder.select.setImageResource(R.drawable.pin_un);
        }

        holder.name.setText(name);
        //holder.number.setText(number);
        // 当前字母
        char currentStr = contact.getSortkey();
        // 前面的字母
        char previewStr = (position - 1) >= 0 ? list.get(position - 1).getSortkey() : 'a';

        if (!(previewStr == currentStr)) {
            holder.alpha.setVisibility(View.VISIBLE);
            holder.alpha.setText(currentStr+"");
        } else {
            holder.alpha.setVisibility(View.GONE);
        }

        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (selectPhoneArray.contains(number)) {

                    selectPhoneArray.remove(number);

                } else {

                    selectPhoneArray.add(number);
                }

                if (selectPhoneArray == null && selectPhoneArray.size() <= 0) {

                   // mCommitTxt.setEnabled(false);
                    //mCommitTxt.setClickable(false);
                }
                else {

                    //mCommitTxt.setEnabled(true);
                   // mCommitTxt.setClickable(true);
                }
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        TextView alpha;
        TextView name;
        ImageView select;
    }

    /**
     * 获取首字母
     *
     * @param str
     * @return
     */
    private String getAlpha(String str) {
        if (str == null) {
            return "#";
        }
        if (str.trim().length() == 0) {
            return "#";
        }
        //char c = str.trim().substring(0, 1).charAt(0);
        // 正则表达式匹配
//        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
//
//        if (pattern.matcher(c + "").matches()) {
//            return (c + "").toUpperCase(); // 将小写字母转换为大写
//        } else {
//            return "#";
//        }

        return str.trim().substring(0, 1).toUpperCase();
    }
}