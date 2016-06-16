package com.maptool.common;

public class ErrorFactory {
	public static MyError ErrorJson = new MyError(100,"json解析错误","网络请求失败");
	public static MyError ErrorNetworkBaiduStatus = new MyError(101,"百度请求返回status！=0","百度地图请求失败");
	
}
