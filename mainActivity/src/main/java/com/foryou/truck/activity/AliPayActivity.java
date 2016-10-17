package com.foryou.truck.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Window;
import android.widget.Toast;
import com.alipay.sdk.app.PayTask;
import com.foryou.truck.model.PayResult;
import com.foryou.truck.util.UtilsLog;


public class AliPayActivity extends Activity{
	public String TAG="AliPayActivity";
	private static final int SDK_PAY_FLAG = 1;
	private static final int SDK_CHECK_FLAG = 2;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				PayResult payResult = new PayResult((String) msg.obj);

				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				String resultInfo = payResult.getResult();

				String resultStatus = payResult.getResultStatus();
				
				Intent intent=new Intent();
				
				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					Toast.makeText(AliPayActivity.this, "支付成功",
							Toast.LENGTH_SHORT).show();
					intent.putExtra("code", "9000");
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(AliPayActivity.this, "支付结果确认中",
								Toast.LENGTH_SHORT).show();
//						finish();

					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						Toast.makeText(AliPayActivity.this, "支付失败",
								Toast.LENGTH_SHORT).show();
					}
					intent.putExtra("code",resultStatus);
				}
				
				setResult(RESULT_OK,intent);
				finish();
				break;
			}
			case SDK_CHECK_FLAG: {
				Toast.makeText(AliPayActivity.this, "检查结果为：" + msg.obj,
						Toast.LENGTH_SHORT).show();
				break;
			}
			default:
				break;
				
			}
			finish();
		}
	};
	String payInfo1="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		payInfo1=getIntent().getStringExtra("payurl");
		UtilsLog.i(TAG, "payInfo1url=="+payInfo1);
		
		pay();
	}

	public void pay() {
		//payInfo1为服务器返回的结果
		//final String payInfo1 = "_input_charset=utf-8&body=%E9%BE%99%E7%9C%BCx1+&notify_url=http%3A%2F%2Fapi.1mxian.com%2Forders%2Fnotify&out_trade_no=SC44579&partner=2088712098245477&payment_type=1&seller_id=tech%401mxian.com&service=mobile.securitypay.pay&subject=%E9%BE%99%E7%9C%BCx1+&total_fee=9.5&sign=JTKmCLIfi0wZ5ron2RhTd4oGaHOWC86BRtvVxr4D%2FN1rZd5MsLR8LrRe4%%2BaersTwg%3D&sign_type=RSA";
		//final String payInfo1 ="_input_charset=utf-8&body=%E4%BB%8E+%E6%B1%9F%E8%8B%8F-%E5%8D%97%E4%BA%AC-%E7%99%BD%E4%B8%8B%E5%8C%BA+%E5%8F%91%E9%80%81%E8%87%B3+%E6%B2%B3%E5%8C%97-%E7%A7%A6%E7%9A%87%E5%B2%9B-%E6%B5%B7%E6%B8%AF%E5%8C%BA%EF%BC%8C%E8%BF%90%E5%8D%95%E5%8F%B7%EF%BC%9A4913104&extra_common_param=2015123011143819066&notify_url=http%3A%2F%2Fsscallback.fuyoukache.com%2Fpayment%2Falipaycallback&out_trade_no=4913104&partner=2088311468317619&payment_type=1&seller_email=fyzx%40foryou56.com&seller_id=2088311468317619&service=mobile.securitypay.pay&subject=%E6%B5%8B%E8%AF%95%E9%85%8D%E4%BB%B6&total_fee=0.01&sign=563990ce0c938238e695d5e4720bb0fb&sign_type=MD5";
		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(AliPayActivity.this);
				// 调用支付接口，获取支付结果
				if( TextUtils.isEmpty(payInfo1)){
					return;
				}
				String result = alipay.pay(payInfo1);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

}
