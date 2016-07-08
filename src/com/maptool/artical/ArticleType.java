package com.maptool.artical;

/**
 * 文章类型
 *     
 * @author maxw  
 * @date 2015-5-6 
 *
 */
public enum ArticleType {
//	MFZC("免费政策","http://blog.sina.com.cn/s/articlelist_5615124616_1_1.html"), //免费政策
//	BYMJ("避孕秘籍","http://blog.sina.com.cn/s/articlelist_5615124616_2_1.html"), //避孕秘籍
//	YJJS("药具介绍","http://blog.sina.com.cn/s/articlelist_5615124616_3_1.html"), //药具介绍
//	ZQGL("周期管理","http://blog.sina.com.cn/s/articlelist_5615124616_4_1.html"), //周期管理
//	XFBB("性福避备","http://blog.sina.com.cn/s/articlelist_5615124616_5_1.html"); //性福避备
	
	MFZC("免费政策","http://www.jianshu.com/notebooks/4783253/latest"), 
	BYMJ("避孕秘籍","http://www.jianshu.com/notebooks/4784262/latest"), 
	ZQGL("爱之密语","http://www.jianshu.com/notebooks/4784343/latest"),
	YJJS("生命探索","http://www.jianshu.com/notebooks/4784340/latest");
//	XFBB("性福避备","http://blog.sina.com.cn/s/articlelist_5615124616_5_1.html"); //性福避备
	
	private String name;
	private String url;
	
	private ArticleType(String name,String url){
		this.name = name;
		this.url = url;
	}
	
	public String getName(){
		return name;
	}
	
	public String getUrl(int page){
		//return url+"/"+page;
		return url;
	}
}
