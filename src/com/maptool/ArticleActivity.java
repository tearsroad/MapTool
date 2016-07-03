package com.maptool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.maptool.artical.Article;
import com.maptool.util.ArticleUtil;
import com.maptool.util.ArticleUtil.GetArticleListener;
import com.maptool.view.ProgressWebView;

public class ArticleActivity extends Activity {
	private static final String TAG = "ArticleActivity";
	TextView tvTitle;
	ProgressWebView wvContent;
	Handler mHandler;
	private ImageView mBackButton;
	private boolean isNormal = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article);

		mHandler = new Handler();
		tvTitle = (TextView) findViewById(R.id.tv_title);
		wvContent = (ProgressWebView) findViewById(R.id.webView_content);

//		wvContent.setWebChromeClient(new WebChromeClient());
//		wvContent.getSettings().setLayoutAlgorithm(
//				LayoutAlgorithm.SINGLE_COLUMN);
//		wvContent.getSettings().setLoadWithOverviewMode(true);
//		wvContent.getSettings().setJavaScriptEnabled(true); 
		
//		wvContent.getSettings().setUseWideViewPort(true);
//		wvContent.getSettings().setLoadWithOverviewMode(true);
		wvContent.getSettings().setJavaScriptEnabled(true);
//		wvContent.loadUrl("http://mp.weixin.qq.com/s?__biz=MzI4MzAyNDU4NA==&mid=2650939198&idx=1&sn=fe6bcf4103a564ad327a75ada57a123f&scene=0");
		Intent intent = getIntent();
		String url = intent.getStringExtra("url");
		Log.i(TAG, "url:"+url);
		tvTitle.setText(intent.getStringExtra("title"));
		isNormal = intent.getBooleanExtra("isNormal", false);
		if(isNormal){
			wvContent.loadUrl(url);
		}else{
			ArticleUtil.getArticle(url, new GetArticleListener() {
				@Override
				public void onGetArticle(final Article article) {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
//							tvTitle.setText(article.getTitle());
							Log.e(TAG, article.getContent());
//							String html = dealArticleContent(article.getContent());
							wvContent.loadData(article.getContent(),
									"text/html;charset=utf-8", null);
//							wvContent.loadUrl(article.getLink());

						}

						
					});
				}
			});
		}
		
		

		mBackButton = (ImageView) findViewById(R.id.iv_back);
		mBackButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private String dealArticleContent(String content) {
		String header = "<html> <head> <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8x\" /> " +
				"<style type=\"text/css\"> *{word-break:break-all;}" +
				"span{word-break:normal; width:auto; display:block; white-space:pre-wrap;word-wrap : break-word ;overflow: hidden ;}  " +
				"img{max-width: 100%;} </style> " +
				"</head> <body>";
		String footer = "</body> </html>";
		return header+content+footer;
	}
}
