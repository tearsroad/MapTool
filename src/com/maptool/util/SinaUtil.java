package com.maptool.util;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.maptool.artical.Article;
import com.maptool.artical.ArticleItem;

/**
 * 读取新浪博客
 * 
 * @author mzb
 * 
 */
public class SinaUtil {

	public static List<ArticleItem> getBlogItemList(int blogType,
			String categoryHTML) {
		List<ArticleItem> list = new ArrayList<ArticleItem>();
		Document doc = Jsoup.parse(categoryHTML);

		Elements blogList = doc.getElementsByClass("articleCell");
		for (Element e : blogList) {
			ArticleItem item = new ArticleItem();
			item.setTitle(e.select(".atc_title a").text());
			item.setLink(e.select(".atc_title a").attr("href"));
			list.add(item);
		}
		return list;
	}

	public static Article getArtcle(String articleHTML) {
		Article article = new Article();

		Document doc = Jsoup.parse(articleHTML);

		// 获取标题
		Element title = doc.getElementsByClass("titName").get(0);
		article.setTitle(ToDBC(title.text()));

		// 获取正文
		Element articleElement = doc.getElementsByClass("articalContent")
				.get(0);
		//新浪博客的图片需要特殊处理
		Elements imgs =  articleElement.select("img");
		imgs.attr("style","");
		imgs.attr("width","");
		imgs.attr("height","");
		for(Element imgEle : imgs){
			String imageUrl =imgEle.attr("real_src");
			imgEle.select("img").attr("src",imageUrl);
		}
		String content = ToDBC(articleElement.html());
		
		article.setContent(content);
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
