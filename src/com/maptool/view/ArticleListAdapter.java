package com.maptool.view;

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


public class ArticleListAdapter extends BaseAdapter {
	List<ArticleItem> items;
	Context context;
	private int parentPositon;
	private AsyncImageListLoader imageLoader;
	public ArticleListAdapter(Context context, List<ArticleItem> items) {
		this.context = context;
		this.items = items;
		imageLoader = new AsyncImageListLoader(context);
		
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_listview_article, null);          
            viewHolder=new ViewHolder();
            viewHolder.tv_name = (TextView)convertView.findViewById(R.id.tv_title);
            viewHolder.tv_manager = (TextView)convertView.findViewById(R.id.tv_url);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        } 
	    ArticleItem item = items.get(position);
        viewHolder.tv_name.setText(item.getTitle());
        viewHolder.tv_manager.setText(item.getImgLink());
//        imageLoader.loadImage(viewHolder.shoplogoImageView, item.getImgLink(), item.getImgLink()
//        		, context.getResources().getDrawable(R.drawable.company_default_logo));
        return convertView;
	}
	class ViewHolder {
        TextView tv_name,headcount,tv_manager;
        ImageView shoplogoImageView;
        LinearLayout ll_parent;
	}
	

	
	

}
