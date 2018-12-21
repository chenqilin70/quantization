package com.kylin.quantization.util;

import org.apache.commons.lang3.exception.ExceptionUtils;

public class ExceptionTool {
	public static String toString(Throwable  e){
		String[] names=ExceptionUtils.getStackFrames(e);
		StringBuffer sb=new StringBuffer("");
		for(String s:names){
			sb.append(s+"\r\n");
		}
		return sb.toString();
	}

}
