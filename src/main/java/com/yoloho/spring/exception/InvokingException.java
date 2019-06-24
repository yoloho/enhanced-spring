package com.yoloho.spring.exception;

public class InvokingException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private int code;
	private String msg;
	
	public InvokingException() {
	    this(0, "");
    }
	
	public InvokingException(int code, String msg) {
	    this.code = code;
	    this.msg = msg;
	}
	
	public int getCode() {
        return code;
    }
	
	public String getMsg() {
        return msg;
    }
	
}
