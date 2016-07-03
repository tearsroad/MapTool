package com.maptool.view.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maptool.R;
import com.maptool.artical.ArticleItem;
import com.maptool.common.AsyncImageListLoader;


public class XuanjiaoListAdapter extends BaseAdapter {
	String[] items;
	Context context;
	public XuanjiaoListAdapter(Context context, String[] items) {
		this.context = context;
		this.items = items;
		
	}
	@Override
	public int getCount() {
		return items.length;
	}

	@Override
	public Object getItem(int position) {
		return items[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
	    ViewHolder viewHolder = null;
	    if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_listview_xuanjiao, null);          
            viewHolder=new ViewHolder();
            viewHolder.tv_name = (TextView)convertView.findViewById(R.id.tv_title);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        } 
	    String item = items[position];
        viewHolder.tv_name.setText(item);
        return convertView;
	}
	class ViewHolder {
        TextView tv_name;
        LinearLayout ll_parent;
	}

}
