package com.maptool.util;

import com.maptool.artical.ArticleType;

@Deprecated
public class URLUtil {

	public static String ARTICLE_URL_MFZC = "http://blog.csdn.net/anzhu_111/article/category/3192873";
	public static String ARTICLE_URL_BYMJ = "";
	public static String ARTICLE_URL_YJJS = "";
	public static String ARTICLE_URL_ZQGL = "";
	public static String ARTICLE_URL_XFBB = "";
	
	public static String getArticleList(ArticleType articalType, int page) {
		String url = null;
		switch (articalType) {
		case MFZC:
			url = ARTICLE_URL_MFZC;
			break;
		case BYMJ:
			url = ARTICLE_URL_BYMJ;
			break;
		case YJJS:
			url = ARTICLE_URL_YJJS;
			break;
		case ZQGL:
			url = ARTICLE_URL_ZQGL;
			break;
//		case XFBB:
//			url = ARTICLE_URL_XFBB;
//			break;
		default:
			break;
		}
		url = url + "/" + page;
		return url;
	}
}
