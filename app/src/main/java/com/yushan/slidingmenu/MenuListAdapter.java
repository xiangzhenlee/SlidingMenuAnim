package com.yushan.slidingmenu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by beiyong on 2017-1-17.
 */

public class MenuListAdapter extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<String> menuData;

    public MenuListAdapter(Context context, ArrayList<String> arrayList) {
        mContext = context;
        menuData = arrayList;
    }

    @Override
    public int getCount() {
        if (menuData == null || menuData.size() < 0) {
            return 0;
        } else {
            return menuData.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return menuData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_menu,null);
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_main);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String str = menuData.get(position);
        if (!"".equals(str)) {
            holder.tv_name.setText(str);
        }

        return convertView;
    }

    class ViewHolder {
        private TextView tv_name;
    }
}
