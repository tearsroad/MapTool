package com.maptool.util;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.util.Log;

import com.maptool.artical.Article;
import com.maptool.artical.ArticleItem;
import com.maptool.artical.ArticleType;
import com.maptool.common.L;

/**
 * 文章工具类
 * 
 * @author mzb
 * 
 */
public class ArticleUtil {

	/**
	 * 获取文章列表回调接口
	 */
	public interface GetArticleListListener {
		public void onGetArticleList(List<ArticleItem> articleList,int total);
		public void onGetArticleErr(String msg);
	}

	/**
	 * 获取文章列表
	 * 
	 * @param type
	 * @return
	 */
	public static void getArticleList(final ArticleType type,final int page,
			final GetArticleListListener listener) {
		new Thread() {
			@Override
			public void run() {
				String url = type.getUrl(1)+"?page="+page; // TODO 目前就获取第一页的
				String content = HttpUtil.httpGet(url);
				if(content!=null){
					List<ArticleItem> articalItems = BlogUtil.getBlogItemList(1,
							content);
					Document doc = Jsoup.parse(content);
					Element te = doc.getElementsByClass("meta").get(0);
					String ts = te.select("a").get(0).text();
					String[] tss = ts.split(" ");
					int total = -1;
					if(tss!=null){
						total = Integer.valueOf(tss[0]);
					}
					if (listener != null) {
						listener.onGetArticleList(articalItems,total);
					}
				}else{
					listener.onGetArticleErr("获取文章列表失败，请检查网络。");
				}
			}
		}.start();
	}

	/**
	 * 获取文章回调接口
	 */
	public interface GetArticleListener {
		public void onGetArticle(Article article);
	}

	public static void getArticle(final String url,
			final GetArticleListener listener) {
		new Thread() {
			@Override
			public void run() {
				String content = HttpUtil.httpGet(url);
				
				if (content != null) {
					Article article = BlogUtil.getArtcle(content);
					if (listener != null) {
//						L.writeLogtoFile(article.getContent());
						listener.onGetArticle(article);
					}
				}
			}
		}.start();
	}
}
