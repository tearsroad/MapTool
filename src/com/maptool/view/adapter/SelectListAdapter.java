package com.maptool.view.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.sax.StartElementListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.maptool.Info_ffpz1_Activity;
import com.maptool.R;
import com.maptool.artical.Article;
import com.maptool.common.AsyncImageListLoader;
import com.maptool.lbs.FFMsgItem;


public class SelectListAdapter extends BaseAdapter {
	List<FFMsgItem> items;
	Context context;
	private int parentPositon;
	private AsyncImageListLoader imageLoader;
	private Handler handler;
	public SelectListAdapter(Context context, List<FFMsgItem> items,Handler handler) {
		this.context = context;
		this.items = items;
		imageLoader = new AsyncImageListLoader(context);
		this.handler = handler;
		
	}
	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
	    ViewHolder viewHolder = null;
	    if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_listview_select, null);          
            viewHolder=new ViewHolder();
            viewHolder.btnSelect = (Button)convertView.findViewById(R.id.btn_select);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        } 
	    final FFMsgItem item = items.get(position);
	    viewHolder.btnSelect.setText(item.getTitle());
        viewHolder.btnSelect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				Toast.makeText(context, item.getTitle(), Toast.LENGTH_LONG).show();
				
				handler.sendEmptyMessage(1);
				Intent intent = new Intent(context, item.getClass());
				context.startActivity(intent);
				
			}
		});
        return convertView;
	}
	class ViewHolder {
        Button btnSelect;
        LinearLayout ll_parent;
	}

}
