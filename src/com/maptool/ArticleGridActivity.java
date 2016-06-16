package com.maptool;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

public class ArticleGridActivity extends Activity implements
		OnItemClickListener {

	private GridView mGridView;
	private ImageView mBackButton;
	private ArrayList<HashMap<String, Object>> items;
	private int[] images = new int[] { R.drawable.article_free,
			R.drawable.article_secret, R.drawable.article_medicine,
			R.drawable.article_cycle, R.drawable.article_necessary };
	private String[] titles = new String[] { "免费政策", "避孕秘籍", "药具介绍", "周期管理",
			"性福必备" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article_gridview);

		initData();
		mGridView = (GridView) findViewById(R.id.gridview);
		SimpleAdapter saImageItems = new SimpleAdapter(this, items,
				R.layout.activity_article_gridview_item, new String[] {
						"image", "title" },
				new int[] { R.id.image, R.id.title });
		mGridView.setAdapter(saImageItems);
		mGridView.setOnItemClickListener(this);
		
		mBackButton = (ImageView) findViewById(R.id.map);
		mBackButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ArticleGridActivity.this, MapActivity.class);
				startActivity(intent);
			}
		});
	}

	private void initData() {
		items = new ArrayList<HashMap<String, Object>>();

		for (int i = 0; i < images.length; i++) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("image", images[i]);
			item.put("title", titles[i]);
			items.add(item);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent(this, ArticleListActivity.class);
		intent.putExtra("type", arg2);
		startActivity(intent);
	}
}
