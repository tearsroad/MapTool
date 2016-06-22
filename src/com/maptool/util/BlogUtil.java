package com.maptool.util;

import java.util.List;

import com.maptool.artical.Article;
import com.maptool.artical.ArticleItem;
import com.maptool.common.Const;

public class BlogUtil {
	public static List<ArticleItem> getBlogItemList(int blogType, String categoryHTML){
		if(Const.blogSystem.equals("sina")){
			return SinaUtil.getBlogItemList(blogType, categoryHTML);
		}else if(Const.blogSystem.equals(Const.blogJianshu)){
			return JianShuUtil.getBlogItemList(blogType, categoryHTML);
		}
		return null;
	}
	public static Article getArtcle(String articleHTML){
		if(Const.blogSystem.equals("sina")){
			return SinaUtil.getArtcle(articleHTML);
		}else if(Const.blogSystem.equals(Const.blogJianshu)){
			return JianShuUtil.getArtcle(articleHTML);
		}
		return null;
	}
}
