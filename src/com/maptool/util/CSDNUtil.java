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
 * 
 * @author wwj_748
 * @date 2014/8/10
 */
public class CSDNUtil {
	public static boolean contentFirstPage = true; // 第一页
	public static boolean contentLastPage = true; // 最后一页
	public static boolean multiPages = false; // 多页
	private static final String BLOG_URL = "http://blog.csdn.net"; // CSDN博客地址

	// 链接样式文件，代码块高亮的处理
	public final static String linkCss = "<script type=\"text/javascript\" src=\"file:///android_asset/shCore.js\"></script>"
			+ "<script type=\"text/javascript\" src=\"file:///android_asset/shBrushJScript.js\"></script>"
			+ "<script type=\"text/javascript\" src=\"file:///android_asset/shBrushJava.js\"></script>"
			+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/shThemeDefault.css\">"
			+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/shCore.css\">"
			+ "<script type=\"text/javascript\">SyntaxHighlighter.all();</script>";

	public static void resetPages() {
		contentFirstPage = true;
		contentLastPage = true;
		multiPages = false;
	}

	/**
	 * 使用Jsoup解析html文档
	 * 
	 * @param blogType
	 * @param str
	 * @return
	 */
	public static List<ArticleItem> getBlogItemList(int blogType, String str) {
		// Log.e("URL---->", str);
		List<ArticleItem> list = new ArrayList<ArticleItem>();
		// 获取文档对象
		Document doc = Jsoup.parse(str);
		// Log.e("doc--->", doc.toString());
		// 获取class="article_item"的所有元素
		Elements blogList = doc.getElementsByClass("article_item");
		// Log.e("elements--->", blogList.toString());

		for (Element blogItem : blogList) {
			ArticleItem item = new ArticleItem();
			String title = blogItem.select("h1").text(); // 得到标题
			// System.out.println("title----->" + title);
			String description = blogItem.select("div.article_description")
					.text();
			// System.out.println("descrition--->" + description);
			String msg = blogItem.select("div.article_manage").text();
			// System.out.println("msg--->" + msg);
			String date = blogItem.getElementsByClass("article_manage").get(0)
					.text();
			// System.out.println("date--->" + date);
			String link = BLOG_URL
					+ blogItem.select("h1").select("a").attr("href");
			// System.out.println("link--->" + link);
			item.setTitle(title);
			item.setMsg(msg);
			item.setContent(description);
			item.setDate(date);
			item.setLink(link);
			item.setType(blogType);

			// 没有图片
			item.setImgLink(null);
			list.add(item);

		}
		return list;
	}

	/**
	 * 获取文章内容
	 * 
	 * @author mzb
	 */
	public static Article getArtcle(String html) {
		Article article = new Article();
		
		Document doc = Jsoup.parse(html);

		// 获取标题
		Element title = doc.getElementsByClass("article_title").get(0);
		article.setTitle(ToDBC(title.text()));
		
		//获取正文
		Element articleElement = doc.getElementById("article_content");
		article.setContent(ToDBC(articleElement.html()));
		return article;
	}

	/**
	 * 扒取传入url地址的博客详细内容
	 * 
	 * @param url
	 * @param str
	 * @return
	 */
	public static List<Article> getContent(String url, String str) {
		List<Article> list = new ArrayList<Article>();

		// 获取文档内容
		Document doc = Jsoup.parse(str);

		// 获取class="details"的元素
		Element detail = doc.getElementsByClass("details").get(0);
		detail.select("script").remove(); // 删除每个匹配元素的DOM。

		// 获取标题
		Element title = detail.getElementsByClass("article_title").get(0);
		Article blogTitle = new Article();
		blogTitle.setState(Constants.DEF_BLOG_ITEM_TYPE.TITLE); // 设置状态
		blogTitle.setContent(ToDBC(title.text())); // 设置标题内容

		// 获取文章内容
		Element content = detail.select("div.article_content").get(0);

		// 获取所有标签为<a的元素
		Elements as = detail.getElementsByTag("a");
		for (int b = 0; b < as.size(); b++) {
			Element blockquote = as.get(b);
			// 改变这个元素的标记。例如,<span>转换为<div> 如el.tagName("div");。
			blockquote.tagName("bold"); // 转为粗体
		}

		Elements ss = detail.getElementsByTag("strong");
		for (int b = 0; b < ss.size(); b++) {
			Element blockquote = ss.get(b);
			blockquote.tagName("bold");
		}

		// 获取所有标签为<p的元素
		Elements ps = detail.getElementsByTag("p");
		for (int b = 0; b < ps.size(); b++) {
			Element blockquote = ps.get(b);
			blockquote.tagName("body");
		}

		// 获取所有引用元素
		Elements blockquotes = detail.getElementsByTag("blockquote");
		for (int b = 0; b < blockquotes.size(); b++) {
			Element blockquote = blockquotes.get(b);
			blockquote.tagName("body");
		}

		// 获取所有标签为<ul的元素
		Elements uls = detail.getElementsByTag("ul");
		for (int b = 0; b < uls.size(); b++) {
			Element blockquote = uls.get(b);
			blockquote.tagName("body");
		}

		// 找出粗体
		Elements bs = detail.getElementsByTag("b");
		for (int b = 0; b < bs.size(); b++) {
			Element bold = bs.get(b);
			bold.tagName("bold");
		}

		// 遍历博客内容中的所有元素
		for (int j = 0; j < content.children().size(); j++) {
			Element c = content.child(j); // 获取每个元素

			// 抽取出图片
			if (c.select("img").size() > 0) {
				Elements imgs = c.getElementsByTag("img");
				System.out.println("img");
				for (Element img : imgs) {
					if (!img.attr("src").equals("")) {
						Article blogImgs = new Article();
						// 大图链接
						if (!img.parent().attr("href").equals("")) {
							blogImgs.setImgLink(img.parent().attr("href"));
							System.out.println("href="
									+ img.parent().attr("href"));
							if (img.parent().parent().tagName().equals("p")) {
								// img.parent().parent().remove();
							}
							img.parent().remove();
						}
						blogImgs.setContent(img.attr("src"));
						blogImgs.setImgLink(img.attr("src"));
						System.out.println(blogImgs.getContent());
						blogImgs.setState(Constants.DEF_BLOG_ITEM_TYPE.IMG);
						list.add(blogImgs);
					}
				}
			}
			c.select("img").remove();

			// 获取博客内容
			Article blogContent = new Article();
			blogContent.setState(Constants.DEF_BLOG_ITEM_TYPE.CONTENT);

			if (c.text().equals("")) {
				continue;
			} else if (c.children().size() == 1) {
				if (c.child(0).tagName().equals("bold")
						|| c.child(0).tagName().equals("span")) {
					if (c.ownText().equals("")) {
						// 小标题，咖啡色
						blogContent
								.setState(Constants.DEF_BLOG_ITEM_TYPE.BOLD_TITLE);
					}
				}
			}

			// 代码
			if (c.select("pre").attr("name").equals("code")) {
				blogContent.setState(Constants.DEF_BLOG_ITEM_TYPE.CODE);
				blogContent.setContent(ToDBC(c.outerHtml()));
			} else {
				blogContent.setContent(ToDBC(c.outerHtml()));
			}
			list.add(blogContent);
		}

		return list;
	}

	/**
	 * 半角转换为全角 全角---指一个字符占用两个标准字符位置。 半角---指一字符占用一个标准的字符位置。
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
