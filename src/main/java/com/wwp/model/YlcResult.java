package com.wwp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

//import com.neo.common.constant.CommonConstant;

//不同节点之间的数据传递全部采用result的方式  result里面的success仅仅代表 数据格式  发送成功等  不代表设备的反应是否成功
//设备的反应是否成功采用 YlcDevMsg里面的success
public class YlcResult<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 成功标志
	 */

	private boolean success = true;

	/**
	 * 返回处理消息
	 */

	private String message = "操作成功！";




	private YlcDevMsg ylcDevMsg;

	/**
	 * 返回数据对象 data
	 */

	private T result;





	public YlcResult() {

	}

	public YlcResult(YlcDevMsg msg) {
		this.ylcDevMsg = msg;
		this.success = true;
	}

	public YlcResult(boolean b,T t,String m) {
		this.success = b;
		this.result=t;
		this.message = m;

	}
	public void setSuccess(boolean b){
		this.success=b;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public void setResult(T result) {this.result = result;	}
	public String getMessage()	{	return message;	}
	public T getResult(){	return result;	}
	public boolean getSuccess(){return success;}

	public void setYlcDevMsg(YlcDevMsg ylcDevMsg) {
		this.ylcDevMsg = ylcDevMsg;
	}
	public YlcDevMsg getYlcDevMsg() {
		return ylcDevMsg;
	}



	public static<T> YlcResult<T> OK(String msg) {
		YlcResult<T> r = new YlcResult<T>();
		r.setSuccess(true);
		r.setMessage(msg);
		return r;
	}



	public static<T> YlcResult<T> OK(String msg, T data) {
		YlcResult<T> r = new YlcResult<T>();
		r.setSuccess(true);
		r.setMessage(msg);
		r.setResult(data);
		return r;
	}

	public static<T> YlcResult<T> error() {
		YlcResult<T> r = new YlcResult<T>();
		r.setSuccess(false);
		r.setMessage("失败");
		return r;
	}

	public static<T> YlcResult<T> error(String msg) {
		YlcResult<T> r = new YlcResult<T>();
		r.setSuccess(false);
		r.setMessage(msg);
		return r;
	}
	@JsonIgnore
	private String onlTable;

}