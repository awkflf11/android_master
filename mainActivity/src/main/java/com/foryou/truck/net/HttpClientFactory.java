package com.foryou.truck.net;

import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

/**
 * HttpClient工厂类 全局一个
 * @author liuweixiang
 *
 */
public class HttpClientFactory {

	protected final static String YOUR_TARGET_IP = "YOUR_TARGET_IP";
	/** 最大连接数 */
	protected final static int MAX_TOTAL_CONNECTIONS = 150;

	/** 连接池获取连接的最大等待时间 */
	protected final static int WAIT_TIMEOUT = 1000;

	/** 每个路由最大连接数 */
	protected final static int MAX_ROUTE_CONNECTIONS = 80;

	/** 设置连接超时时间 */
	protected final static int CONNECT_TIMEOUT = 12000;

	/** socket读取数据超时时间 */
	protected final static int READ_TIMEOUT = 20000;

	private HttpClientFactory() {
	}

//	private static DefaultHttpClient client;

	/**
	 * 获取HttpClient
	 * 
	 * @return
	 */
	public static synchronized DefaultHttpClient getHttpClient() {
//		if (client != null)
//			return client;
//		else {
//			Apn.init();
//			client = createDefaultHttpClient();
//			return client;
//		}
		Apn.init();
		return createDefaultHttpClient();
	}

	/**
	 * 创建DefaultHttpClient
	 * 
	 * @return
	 */
	public static DefaultHttpClient createDefaultHttpClient() {
		HttpParams params = createHttpParams();
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme(NetConstants.HTTP_SCHEME, PlainSocketFactory.getSocketFactory(), 80));
		registry.register(new Scheme(NetConstants.HTTP_SAFETY_SCHEME, SSLSocketFactory.getSocketFactory(), 443));

		ClientConnectionManager connectionManager = new ThreadSafeClientConnManager(params, registry);
		DefaultHttpClient httpclient = new DefaultHttpClient(connectionManager, params);
		if (Apn.M_USE_PROXY) {
			HttpHost proxy = new HttpHost(Apn.M_APN_PROXY, Apn.M_APN_PORT, NetConstants.HTTP_SCHEME);
			httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}

		return httpclient;
	}

	/**
	 * 设置HttpParams
	 * 
	 * @return
	 */
	private static final HttpParams createHttpParams() {
		HttpParams httpParams = new BasicHttpParams();
		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		// 设置最大连接数
		ConnManagerParams.setMaxTotalConnections(httpParams, MAX_TOTAL_CONNECTIONS);
		// 设置从连接池获取连接的最大等待时间
		ConnManagerParams.setTimeout(httpParams, WAIT_TIMEOUT);
		// 设置每个路由最大连接数
		ConnPerRouteBean connPerRoute = new ConnPerRouteBean(MAX_ROUTE_CONNECTIONS);
		ConnManagerParams.setMaxConnectionsPerRoute(httpParams, connPerRoute);
		// 设置连接超时时间
		HttpConnectionParams.setConnectionTimeout(httpParams, CONNECT_TIMEOUT);
		// 设置socket读取数据超时时间
		HttpConnectionParams.setSoTimeout(httpParams, READ_TIMEOUT);
		// 设置以Socket 缓存大小
		HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
		HttpConnectionParams.setStaleCheckingEnabled(httpParams, false);
		// 设置重定向，缺省为 true , 这里设置成不重定向
		HttpClientParams.setRedirecting(httpParams, false);
		return httpParams;
	}
}
