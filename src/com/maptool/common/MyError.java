package com.maptool.common;


public class MyError extends Exception{
	private static final long serialVersionUID = 1L;
	protected int code;
	protected String msg;
	protected String showMsg;
	protected Exception e;
	protected String info ="";
	
	public MyError(int code, String msg, String showMsg) {
		super();
		this.code = code;
		this.msg = msg;
		this.showMsg = showMsg;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getShowMsg() {
		return showMsg;
	}
	public MyError setShowMsg(String showMsg) {
		this.showMsg = showMsg;
		return this;
	}
	
	public MyError setException(Exception e){
		this.e = e;
		return this;
	}
	public MyError setInfo(String info){
		this.info = info;
		return this;
	}
	
	public String toString(){
		String errMsg = e!=null?" exception:"+e.getLocalizedMessage():"";
		String message = "code:"+code+" msg:"+msg+" info:"+info+errMsg;
		L.d(this,message);
		if(Const.debug){
			return message;
		}else{
			return showMsg;
		}
	}
}