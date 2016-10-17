package com.foryou.truck.net;

import java.io.IOException;
import java.io.InputStream;

public interface NetConstants {

	String ENCODING = "UTF-8";
	String HTTP_SAFETY_SCHEME = "https";
	String HTTP_SCHEME = "http";
	String HTTP_HOST = "dev.krakenjs.avosapps.com";
	
	String METHOD = "item";
	String HTTP_URL = HTTP_SCHEME + "://" + HTTP_HOST;

	boolean HTTP_METHOD = true;
	String MD = "mlhttp";
	
	

	/**
	 * 对流转化成字符串
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	String getContentByString(InputStream is) throws IOException;

	/**
	 * 关闭连接
	 */
	void close();
}
