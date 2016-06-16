package com.maptool.view;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.maptool.ArticleActivity;
import com.maptool.artical.ArticleItem;
import com.maptool.artical.ArticleType;
import com.maptool.util.ArticleUtil;
import com.maptool.util.ArticleUtil.GetArticleListListener;

public class ArticleListView extends ListView{
	
	Context mContext;
	ArticleType mType;
	Handler mHandler;
	List<ArticleItem> mArticleList;
	
	public ArticleListView(final Context context,ArticleType type) {
		super(context);
		
		mContext = context;
		mType = type;
		mHandler = new Handler();
		ArticleUtil.getArticleList(type, new GetArticleListListener() {
			@Override
			public void onGetArticleList(final List<ArticleItem> articleList) {
				mArticleList = articleList;
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						setAdapter(new ArticleListAdapter(context,articleList));
						invalidate();
					}
				});
				
			}
		});
		
		setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(mArticleList!=null){
					ArticleItem article = mArticleList.get(position);
					Intent intent = new Intent(mContext,ArticleActivity.class);
					intent.putExtra("url", article.getLink());
					mContext.startActivity(intent);
				}
			}
		});
	}
	
	private class ArticleListAdapter extends BaseAdapter{
		
		private List<ArticleItem> mArticleList;
		private Context mContext;
		
		public ArticleListAdapter(Context context,List<ArticleItem> articleList) {
			mArticleList = articleList;
			mContext = context;
		}

		@Override
		public int getCount() {
			return mArticleList.size();
		}

		@Override
		public Object getItem(int position) {
			return mArticleList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = new TextView(mContext);
			}
			
			TextView tv = (TextView)convertView;
			tv.setText(mArticleList.get(position).getTitle());
			
			return convertView;
		}
	}

}
