package com.maptool;

import java.util.ArrayList;
import java.util.HashMap;

import com.maptool.artical.ArticleItem;
import com.maptool.view.adapter.XuanjiaoListAdapter;

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
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ArticleGridActivity extends Activity implements
		OnItemClickListener {

	private ListView lv_article;
	private ArrayList<HashMap<String, Object>> items;
//	private int[] images = new int[] { R.drawable.article_free,
//			R.drawable.article_secret, R.drawable.article_medicine,
//			R.drawable.article_cycle, R.drawable.article_necessary };
	private String[] titles = new String[] { "免费政策","免费品种", "避孕秘籍", "爱之密语", "线上领取" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article_gridview);

		lv_article = (ListView) findViewById(R.id.lv_article);
		XuanjiaoListAdapter mAdapter = new XuanjiaoListAdapter(this, titles);
		lv_article.setAdapter(mAdapter);
		lv_article.setOnItemClickListener(this);
		
		findViewById(R.id.rl_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ArticleGridActivity.this, MapActivity.class);
				startActivity(intent);
			}
		});
	}

	

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(arg2<4){
			Intent intent = new Intent(this, ArticleListActivity.class);
			intent.putExtra("type", arg2);
			intent.putExtra("title", titles[arg2]);
			startActivity(intent);
		}else{
			Intent intent = new Intent(this , ArticleActivity.class);
			intent.putExtra("url", "http://www.qzmfyj.org/linqu_zs.html");
			intent.putExtra("title", "线上领取");
			intent.putExtra("isNormal", true);
			startActivity(intent);
		}
	}
}
