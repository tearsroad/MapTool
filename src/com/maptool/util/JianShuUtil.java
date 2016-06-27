package com.maptool.util;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Node;

import com.maptool.artical.Article;
import com.maptool.artical.ArticleItem;

/**
 * 读取简书博客
 * 
 * @author lhx
 * 
 */
public class JianShuUtil {

	public static List<ArticleItem> getBlogItemList(int blogType,
			String categoryHTML) {
		List<ArticleItem> list = new ArrayList<ArticleItem>();
		Document doc = Jsoup.parse(categoryHTML);
		Element blogList = doc.getElementsByClass("article-list").get(0);
		
		for (Element e : blogList.getElementsByTag("li")) {
			ArticleItem item = new ArticleItem();
			item.setTitle(e.select(".title a").text());
			item.setLink("http://www.jianshu.com"+e.select(".title a").attr("href"));
			Elements imgs = e.select("img");
			if(imgs!=null&&imgs.size()>0){
				item.setImgLink(imgs.get(0).attr("src"));
			}else{
				item.setImgLink("t");
			}
			list.add(item);
		}
		return list;
	}

	/**
	 * dropdown-toggle logo
	 * author-info
	 * wrap-btn
	 * @Description: TODO 
	 * @param @param articleHTML
	 * @param @return  
	 * @return Article
	 * @author 罗洪祥
	 * @date 2016年6月27日 上午10:33:39
	 */
	public static Article getArtcle(String articleHTML) {
		Article article = new Article();

		Document doc = Jsoup.parse(articleHTML);
		doc.getElementsByClass("author-info").get(0).remove();
		doc.getElementsByClass("dropdown").get(0).remove();
		doc.getElementsByClass("wrap-btn").get(0).remove();
		doc.getElementsByClass("fixed-btn").get(0).remove();
		doc.getElementsByClass("article").get(0).attr("style","padding:5px 10px 10px 10px");
		
		// 获取url地址
//		Element content = doc.getElementsByClass("show-content").get(0);
//		Elements els = content.select("p");
//		article.setLink(els.get(0).text());
//		article.setTitle(ToDBC(title.text()));

		// 获取正文
//		Element body = doc.body();
////		body.
//		Element articleElement = doc.getElementsByClass("preview")
//				.get(0);
//		

////		//新浪博客的图片需要特殊处理
////		Elements imgs =  articleElement.select("img");
////		imgs.attr("style","");
////		imgs.attr("width","");
////		imgs.attr("height","");
////		for(Element imgEle : imgs){
////			String imageUrl =imgEle.attr("real_src");
////			imgEle.select("img").attr("src",imageUrl);
////		}
//		String content = ToDBC(articleElement.html());
		
		article.setContent(doc.html());
		return article;
	}

	/**
	 * 半角转换为全�? 全角---指一个字符占用两个标准字符位置�?? 半角---指一字符占用�?个标准的字符位置�?
	 * 
	 * @param input
	 * @return
	 */
	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}
}
