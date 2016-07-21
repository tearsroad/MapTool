package com.maptool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.maptool.artical.ArticleItem;
import com.maptool.artical.ArticleType;
import com.maptool.common.L;
import com.maptool.util.ArticleUtil;
import com.maptool.util.ArticleUtil.GetArticleListListener;
import com.maptool.view.adapter.ArticleListAdapter;

public class ArticleListActivity extends Activity implements OnItemClickListener{

	private ListView mListView;
	private ImageView mBackButton;
	private ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
	private List<ArticleItem> mArticleItems;
	private String title;
	private TextView tvTitle;

	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			initUI();
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article_listview);
		// 绑定XML中的ListView，作为Item的容器
		mListView = (ListView) findViewById(R.id.listview);
		tvTitle = (TextView) findViewById(R.id.textView1);
		mListView.setOnItemClickListener(this);
		initData();
		title = getIntent().getStringExtra("title");
		tvTitle.setText(title);
		mBackButton = (ImageView) findViewById(R.id.map);
		mBackButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(ArticleListActivity.this, MapActivity.class);
//				startActivity(intent);
				finish();
			}
		});
	}

	private void initData() {
		Intent intent = getIntent();
		int type = intent.getIntExtra("type", 0);
		ArticleUtil.getArticleList(ArticleType.values()[type],
				new GetArticleListListener() {

					@Override
					public void onGetArticleList(List<ArticleItem> articleList) {
						// TODO Auto-generated method stub
						mArticleItems = articleList;
						for (ArticleItem articleItem : articleList) {
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("title", articleItem.getTitle());
							mylist.add(map);
						}
						mHandler.sendEmptyMessage(0);
					}

					@Override
					public void onGetArticleErr(String msg) {
						// TODO Auto-generated method stub
//						L.setContext(getApplicationContext());
//						L.showToast(msg, Toast.LENGTH_LONG);
					}
				});
	}

	private void initUI() {
		// 生成适配器，数组===》ListItem
//		SimpleAdapter mSchedule = new SimpleAdapter(this, // 没什么解释
//				mylist,// 数据来源
//				R.layout.activity_article_listview_item,// ListItem的XML实现
//
//				// 动态数组与ListItem对应的子项
//				new String[] { "title"},
//
//				// ListItem的XML文件里面的两个TextView ID
//				new int[] { R.id.title });
		ArticleListAdapter adapter = new ArticleListAdapter(this, mArticleItems);
		// 添加并且显示
		mListView.setAdapter(adapter);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		if(mArticleItems != null){
			ArticleItem article = mArticleItems.get(position);
			Intent intent = new Intent(this , ArticleActivity.class);
			intent.putExtra("url", article.getLink());
			intent.putExtra("title", article.getTitle());
			startActivity(intent);
		}
	}
}
