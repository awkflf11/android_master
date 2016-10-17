package com.foryou.truck;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.foryou.truck.util.NetWorkUtils;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.UtilsLog;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;

public class PushGexinReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		Log.d("GetuiSdkDemo", "onReceive() action=" + bundle.getInt("action"));
		switch (bundle.getInt(PushConsts.CMD_ACTION)) {

		case PushConsts.GET_MSG_DATA:
			// 获取透传数据
			// String appid = bundle.getString("appid");
			byte[] payload = bundle.getByteArray("payload");

			String taskid = bundle.getString("taskid");
			String messageid = bundle.getString("messageid");

			// smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
			boolean result = PushManager.getInstance().sendFeedbackMessage(
					context, taskid, messageid, 90001);
			System.out.println("第三方回执接口调用" + (result ? "成功" : "失败"));
			try{
			if (payload != null) {
				String data = new String(payload);
				Log.d("GetuiSdkDemo", "Got Payload:" + data);
				String[] str = new String[2];
				str = data.split("&");
				Log.d("GetuiSdkDemo", "str[0]:" + str[0]);
				if (str[0].equals("type=quote")) {
					if (str[1].startsWith("id=")) {
						Log.i("GetuiSdkDemo", "start AgentList...");
						Intent i = new
								Intent(context, AgentAndQuoteDetailAct.class);
						i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						i.putExtra("order_id",
								str[1].substring(3, str[1].length()));
						i.putExtra("quote_detail",true);
						i.putExtra("fromGT",true);
						context.startActivity(i);
						UtilsLog.i("GetuiSdkDemo", "str[1].substring(3, str[1].length())===" + str[1].substring(3, str[1].length()));
					}
				} else if (str[0].equals("type=order")) {
					if (str[1].startsWith("id=")) {
						Log.i("GetuiSdkDemo", "start OrderDetail...");
						Intent i = new Intent(context,
								OrderDetailActivity.class);
						i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						i.putExtra("order_id",
								str[1].substring(3, str[1].length()));
						i.putExtra("fromGT",true);
						context.startActivity(i);

						UtilsLog.i("GetuiSdkDemo", "str[1].substring(3, str[1].length())===" + str[1].substring(3, str[1].length()));
					}
				}
			}
			}catch(Exception e){

			}
				// if (GetuiSdkDemoActivity.tLogView != null)
				// GetuiSdkDemoActivity.tLogView.append(data + "\n");

			break;
		case PushConsts.GET_CLIENTID:
			// 获取ClientID(CID)
			// 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
			String cid = bundle.getString("clientid");
			SharePerfenceUtil.SaveGtCid(context, cid);
			NetWorkUtils.BindGtAction(context);
			break;
		case PushConsts.THIRDPART_FEEDBACK:
			break;
		default:
			break;
		}
	}
}
