package com.foryou.truck;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.foryou.truck.adapter.MyListViewAdapter;
import com.foryou.truck.adapter.MyListViewAdapter.CallBacks;
import com.foryou.truck.count.TongjiModel;
import com.foryou.truck.entity.AgentListEntity.AgentInfo;
import com.foryou.truck.parser.AgentListJsonParser;
import com.foryou.truck.parser.SimpleJasonParser;
import com.foryou.truck.util.Constant;
import com.foryou.truck.util.NormalNetworkRequest;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UrlConstant;
import com.foryou.truck.util.UtilsLog;
import com.foryou.truck.view.CircleReduceTimeView;
import com.foryou.truck.view.DialogBuilder;
import com.foryou.truck.view.PullDownView;
import com.foryou.truck.view.PullDownView.OnItemClickListener;
import com.foryou.truck.view.PullDownView.OnRefreshListener;
import com.squareup.leakcanary.RefWatcher;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//经纪人报价列表
public class AgentListActivity extends Fragment implements View.OnClickListener {
    private PullDownView pullDownView;
    private ListView listView;
    private MyListViewAdapter mAdapter;
    private List<Map<String, Object>> adapterlist = new ArrayList<Map<String, Object>>();
    private int offset;
    private String TAG = "AgentListActivity";
    private LinearLayout mAgentListReduceTime;

    public AgentAndQuoteDetailAct getAgentAndQuoteActivity() {
        return (AgentAndQuoteDetailAct) getActivity();
    }

    @Override
    public void onDestroy() {
        mTimeDecreaseHandle.removeMessages(DECREASE_TIME);

        super.onDestroy();
        RefWatcher refWatcher = MyApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    private void InitData(AgentListJsonParser mAgentListParser) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        timestamp = mAgentListParser.entity.timestamp;

        for (int i = 0; i < mAgentListParser.entity.data.list.size(); i++) {
            AgentInfo mInfo = mAgentListParser.entity.data.list.get(i);
            Map<String, Object> map = new HashMap<String, Object>();
            //add
            map.put("photo", mInfo.photo);
            map.put("name", mInfo.name);
            map.put("mobile", mInfo.mobile);
            map.put("chengdan_m", mInfo.order_m + "");//月成单
            map.put("chengdan_t", mInfo.order_a + "");//总成单
            map.put("pingjia", mInfo.comment + "");//5条
            map.put("coupon_amount", mInfo.coupon_amount);//可用代金券的数量
            if (Integer.valueOf(mInfo.comment) <= 0) {
                map.put("chakan", false);
            } else {
                map.put("chakan", true);
            }
            map.put("baojia", "¥" + mInfo.quote_price);
            map.put("load_region", mAgentListParser.entity.load_region);
            map.put("load_address", mAgentListParser.entity.load_address);
            map.put("unload_region", mAgentListParser.entity.unload_region);
            map.put("unload_address", mAgentListParser.entity.unload_address);
            map.put("agent_id", mInfo.agent_id);
            map.put("id", mInfo.id);
            //新增
            map.put("is_in_aging", mAgentListParser.entity.is_in_aging);//是否在竞价失效内

            if (mInfo.remark == null) {
                mInfo.remark = "无";
            } else {
                if (mInfo.remark.equals("")) {
                    mInfo.remark = "无";
                }
            }
            map.put("baojia_remark", mInfo.remark);
            // Long remainTime = (Long) (Long.valueOf(mInfo.expire_time) -
            // System.currentTimeMillis() / 1000);
            Long remainTime = Long.valueOf(mInfo.expire_time);
            Log.i(TAG, "remainTime:" + remainTime);
            // if (remainTime <= 0) {
            // map.put("remain_time_layout", false);
            // } else {
            map.put("remain_time_layout", true);
            // }
            map.put("expire_time", mInfo.expire_time);
            map.put("reopen", mInfo.reopen);
            // map.put("phone_number", mInfo.mobile);
            map.put("remain_time", remainTime);
            if (mInfo.credit_point.equals("0")) {
                map.put("xinyi", mInfo.credit_point);
            } else {
                map.put("xinyi", mInfo.credit_point + "%");
            }
            map.put("pay_type", mAgentListParser.entity.pay_type);
            if (getAgentAndQuoteActivity().quote_list) {
                map.put("confirm_btn", false);
            } else {
                map.put("confirm_btn", true);
            }

            adapterlist.add(map);
        }
        mAdapter.notifyDataSetChanged();
        mTimeDecreaseHandle.removeMessages(DECREASE_TIME);
        mTimeDecreaseHandle.sendEmptyMessageDelayed(DECREASE_TIME, 1000);

        if (mAgentListParser.entity.data.list.size() == 0 && !isRefresh) {
            pullDownView.showNoDataView(true);
        } else {
            pullDownView.showNoDataView(false);
        }
    }

    Handler mTimeDecreaseHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case DECREASE_TIME:
                    for (int i = 0; i < adapterlist.size(); i++) {
                        String expire_time = (String) adapterlist.get(i).get(
                                "expire_time");
                        Long dueTime = Long.valueOf(expire_time);
                        dueTime--;
                        adapterlist.get(i).put("expire_time", "" + dueTime);
                    }
                    mAdapter.notifyDataSetChanged();
                    mTimeDecreaseHandle.removeMessages(DECREASE_TIME);
                    mTimeDecreaseHandle
                            .sendEmptyMessageDelayed(DECREASE_TIME, 1000);
                    break;
            }
        }
    };
    OnScrollListener scorllListener = new OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            // TODO Auto-generated method stub
            Log.i("aa", "firstVisibleItem" + firstVisibleItem
                    + "visibleItemCount" + visibleItemCount + "totalItemCount"
                    + totalItemCount);
            if (((firstVisibleItem + visibleItemCount) == totalItemCount)
                    && !isRefresh) {
                getAgentListTask(true);
            } else {

            }
        }

    };

    private void ShowAlertDialog(final int positon) {
        AlertDialog.Builder mDialog = DialogBuilder.NewAlertDialog(getAgentAndQuoteActivity());
        mDialog.setTitle("");
        String name = (String) adapterlist.get(positon).get("name");
        String price = (String) adapterlist.get(positon).get("baojia");
        mDialog.setMessage("确定请求询问经纪人" + name + "的" + price + "元报价是否可以下单？");
        mDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                TongjiModel.addEvent(getAgentAndQuoteActivity(), "经纪人报价列表",
                        TongjiModel.TYPE_BUTTON_CLIKC, "激活报价");
                QuoteReopenTask(positon);
            }

        });
        mDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }

        });
        mDialog.show();
    }

    private void InitListView(View view) {
        pullDownView = (PullDownView) view.findViewById(R.id.feeds);
        pullDownView.init();
        pullDownView.setFooterView(R.layout.footer_item);
        pullDownView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh .......");
                timestamp = "";
                getAgentListTask(false);
            }
        });

        pullDownView.showFooterView(false);
        listView = pullDownView.getListView();

        listView.setDividerHeight(0);
        mAdapter = new MyListViewAdapter(getAgentAndQuoteActivity(), adapterlist,
                R.layout.agent_list_item, new String[]{"photo", "name",
                "baojia", "remain_time",
                "remain_time_layout", "baojia_remark", "confirm_btn"},
                new int[]{R.id.mCover, R.id.name,
                        R.id.baojia, R.id.remain_time,
                        R.id.remain_time_layout,
                        R.id.baojia_remark,
                        R.id.confirm_btn}, true);

        mAdapter.setOnClickViewLisener(R.id.pingjia_layout, new CallBacks() {
            @Override
            public void onViewClicked(final int positon, int viewId) {
                // TODO Auto-generated method stub
                TongjiModel.addEvent(getAgentAndQuoteActivity(), "经纪人报价列表",
                        TongjiModel.TYPE_BUTTON_CLIKC, "查看评价");

                String expire_time = (String) adapterlist.get(positon).get(
                        "expire_time");
                Intent intent = getBeginSendProductIntent(positon);
                intent.setClass(getAgentAndQuoteActivity(), CommentActivity.class);
                intent.putExtra("xinyi", (String) adapterlist.get(positon).get("xinyi"));
                intent.putExtra("mobile", (String) adapterlist.get(positon).get("mobile"));
                intent.putExtra("photo", (String) adapterlist.get(positon).get("photo"));
                intent.putExtra("chengdan_m", (String) adapterlist.get(positon).get("chengdan_m"));
                intent.putExtra("chengdan_t", (String) adapterlist.get(positon).get("chengdan_t"));

                if (Long.valueOf(expire_time) > 0) {
                    if(getAgentAndQuoteActivity().quote_list){
                        intent.putExtra("confirm_btn", false);
                    }else{
                        intent.putExtra("confirm_btn", true);
                    }
                } else {
                    intent.putExtra("confirm_btn", false);
                }
                startActivity(intent);
            }
        });

        mAdapter.setOnClickViewLisener(R.id.callphone_ll, new CallBacks() {
            @Override
            public void onViewClicked(final int positon, int viewId) {
                Constant.GotoDialPage(getAgentAndQuoteActivity(),
                        (String) adapterlist.get(positon).get("mobile"));
            }
        });

        mAdapter.setOnClickViewLisener(R.id.confirm_ll, new CallBacks() {
            @Override
            public void onViewClicked(int positon, int viewId) {
                // TODO Auto-generated method stub
                String expire_time = (String) adapterlist.get(positon).get(
                        "expire_time");
                // Long remainTime = (Long) (Long.valueOf(expire_time) -
                // System.currentTimeMillis() / 1000);
                Long remainTime = Long.valueOf(expire_time);
                if (remainTime <= 0) {
                    String reopen = (String) adapterlist.get(positon).get("reopen");
                    String is_in_aging = (String) adapterlist.get(positon).get("is_in_aging");

                    if (reopen.equals("0") && (boolean) adapterlist.get(positon).get("confirm_btn")
                            && !"1".equals(is_in_aging)) {    //普通报价下的，  0 激活报价  2等待激活报价
                        ShowAlertDialog(positon);
                    }

                } else {
                    if(getAgentAndQuoteActivity().quote_list){// true,按钮显示报价失效，不跳到BeginSendProductAct
                    }else{
                        Intent intent = getBeginSendProductIntent(positon);
                        intent.setClass(getAgentAndQuoteActivity(), BeginSendProductAct.class);
                        startActivity(intent);
                    }
                }
            }
        });

        listView.setAdapter(mAdapter);
        pullDownView
                .setPullDownViewOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        // TODO Auto-generated method stub
                    }
                });
    }

    private Intent getBeginSendProductIntent(int positon) {
        Intent intent = new Intent();
        intent.putExtra("order_id", getAgentAndQuoteActivity().OrderId);
        //可以代金券的数量
        intent.putExtra("coupon_amount", (String) adapterlist.get(positon).get("coupon_amount"));

        String baojia = (String) adapterlist.get(positon).get("baojia");
        if (baojia.contains("¥")) {
            baojia = baojia.substring(1, baojia.length());
        }
        intent.putExtra("baojia", baojia);
        intent.putExtra("agent_id",
                (String) adapterlist.get(positon).get("agent_id"));
        intent.putExtra("name", (String) adapterlist.get(positon).get("name"));
        intent.putExtra("pay_type",
                (String) adapterlist.get(positon).get("pay_type"));
        intent.putExtra("load_region", (String) adapterlist.get(positon).get("load_region"));
        intent.putExtra("load_address", (String) adapterlist.get(positon).get("load_address"));
        intent.putExtra(
                "unload_region",
                (String) adapterlist.get(positon).get(
                        "unload_region"));
        intent.putExtra(
                "unload_address",
                (String) adapterlist.get(positon).get(
                        "unload_address"));
        return intent;
    }

    private final int DECREASE_TIME = 2;
    //public AgentListJsonParser mAgentListParser;
    private boolean isRefresh = false;
    private int PAGE_SIZE = 5;
    private int PAGE_INDEX = 1;

    private String timestamp = "";


    private void initReduceSeconds(AgentListJsonParser mParser) {
        if (!TextUtils.isEmpty(mParser.entity.expire_time)) {
            long seconds = Integer.valueOf(mParser.entity.expire_time) - System.currentTimeMillis() / 1000;
            CircleReduceTimeView mCircleReduceTimeView = (CircleReduceTimeView) mAgentListReduceTime.findViewById(R.id.reduce_time_circle_view);

            mCircleReduceTimeView.setOnInValidListener(new CircleReduceTimeView.InvalidListener() {
                @Override
                public void OnInValid() {
                    getAgentListTask(false);
                }

                @Override
                public void OnSeconds(long seconds) {

                }
            });
            mCircleReduceTimeView.setSeconds("" + seconds);
            TextView lowestPrice = (TextView) mAgentListReduceTime.findViewById(R.id.lowest_price);
            TextView lowestPriceText = (TextView) mAgentListReduceTime.findViewById(R.id.lowest_price_text);
            if(!TextUtils.isEmpty(mParser.entity.reference_price)
                    && !mParser.entity.reference_price.equals("0")){
                lowestPrice.setText("¥" + mParser.entity.reference_price);
                lowestPriceText.setVisibility(View.VISIBLE);
                lowestPrice.setVisibility(View.VISIBLE);
            }else{
                lowestPriceText.setVisibility(View.GONE);
                lowestPrice.setVisibility(View.GONE);
            }
        }
    }


    private void getAgentListTask(boolean getMore) {
        if (getMore) {
            PAGE_INDEX += 1;
            isRefresh = true;
            listView.setOnScrollListener(null);
        } else {
            PAGE_INDEX = 1;
        }
        Map<String, String> parmams = new HashMap<String, String>();
        parmams.put("page", "" + PAGE_INDEX);
        parmams.put("order_id", getAgentAndQuoteActivity().OrderId);
        // parmas.put("timestamp", timestamp);
        parmams.put("timestamp", "0");
        if (timestamp.equals("")) {
            getAgentAndQuoteActivity().showProgressDialog();
        }
        String url = NormalNetworkRequest.getUrl(getAgentAndQuoteActivity(), UrlConstant.BASE_URL
                + "/order/agentQuoteList", parmams);
        final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
                getAgentAndQuoteActivity(), Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // TODO Auto-generated method stub
                        UtilsLog.i(TAG, "response:" + response);
                        getAgentAndQuoteActivity().cancelProgressDialog();
                        pullDownView.notifyRefreshComplete();
                        AgentListJsonParser mParser = new AgentListJsonParser();
                        int result = mParser.parser(response);
                        if (result == 1) {
                            if (mParser.entity.status.equals("Y")) {
                                if (!TextUtils.isEmpty(mParser.entity.is_in_aging)
                                        && mParser.entity.is_in_aging.equals("1")) {
                                    mAgentListReduceTime.setVisibility(View.VISIBLE);
                                    pullDownView.setVisibility(View.GONE);
                                    initReduceSeconds(mParser);
                                    return;
                                }else{
                                    pullDownView.setVisibility(View.VISIBLE);
                                    mAgentListReduceTime.setVisibility(View.GONE);
                                }

                                if (!isRefresh) {// 正常刷新加载
                                    if (mParser.entity.data.list
                                            .size() == 0
                                            && !timestamp.equals("")) {
                                        return;
                                    }
                                    adapterlist.clear();
                                    InitData(mParser);
                                } else {// 加载更多
                                    InitData(mParser);
                                    isRefresh = false;
                                }
                                if (mParser.entity.data.list.size() == PAGE_SIZE) {
                                    listView.setOnScrollListener(scorllListener);
                                    pullDownView.showFooterView(true);
                                } else {
                                    listView.setOnScrollListener(null);
                                    pullDownView.showFooterView(false);
                                }
                            } else {
                                if (!TextUtils.isEmpty(mParser.entity.msg)) {
                                    ToastUtils.toast(getAgentAndQuoteActivity(),
                                            mParser.entity.msg);
                                }
                                listView.setOnScrollListener(null);
                                pullDownView.showFooterView(false);
                            }
                        } else {
                            Log.i(TAG, "解析错误");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NetworkError) {
                    Log.i(TAG, "NetworkError");
                } else if (error instanceof ServerError) {
                    Log.i(TAG, "ServerError");
                } else if (error instanceof AuthFailureError) {
                    Log.i(TAG, "AuthFailureError");
                } else if (error instanceof ParseError) {
                    Log.i(TAG, "ParseError");
                } else if (error instanceof NoConnectionError) {
                    Log.i(TAG, "NoConnectionError");
                } else if (error instanceof TimeoutError) {
                    Log.i(TAG, "TimeoutError");
                }
                getAgentAndQuoteActivity().cancelProgressDialog();
            }

        }, true) {

            @Override
            public Map<String, String> getPostBodyData() {
                // TODO Auto-generated method stub
                Map<String, String> parmas = super.getPostBodyData();
                return parmas;
            }

        };
        getAgentAndQuoteActivity().showProgressDialog();
        MyApplication.getInstance().addRequest(stringRequest, TAG);
    }

    private void QuoteReopenTask(final int position) {
        String url = UrlConstant.BASE_URL + "/order/quoteReOpen";
        final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
                getAgentAndQuoteActivity(), Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // TODO Auto-generated method stub
                        UtilsLog.i(TAG, "response:" + response);
                        getAgentAndQuoteActivity().cancelProgressDialog();
                        SimpleJasonParser mParser = new SimpleJasonParser();
                        int result = mParser.parser(response);
                        if (result == 1) {
                            if (mParser.entity.status.equals("Y")) {
                                //已成功给经纪人发送尝试下单需求,当经纪人激活报价后,会及时通知您~
                                Toast.makeText(getAgentAndQuoteActivity(), "激活报价成功", Toast.LENGTH_LONG).show();
                                getAgentListTask(false);
                            } else {
                                ToastUtils.toast(getAgentAndQuoteActivity(), mParser.entity.msg);
                            }
                        } else {
                            Log.i(TAG, "解析错误");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NetworkError) {
                    Log.i(TAG, "NetworkError");
                } else if (error instanceof ServerError) {
                    Log.i(TAG, "ServerError");
                } else if (error instanceof AuthFailureError) {
                    Log.i(TAG, "AuthFailureError");
                } else if (error instanceof ParseError) {
                    Log.i(TAG, "ParseError");
                } else if (error instanceof NoConnectionError) {
                    Log.i(TAG, "NoConnectionError");
                } else if (error instanceof TimeoutError) {
                    Log.i(TAG, "TimeoutError");
                }
                getAgentAndQuoteActivity().cancelProgressDialog();
            }

        }, true) {

            @Override
            public Map<String, String> getPostBodyData() {
                // TODO Auto-generated method stub
                Map<String, String> parmas = super.getPostBodyData();
                parmas.put("order_id", getAgentAndQuoteActivity().OrderId);
                parmas.put("agent_id",
                        (String) adapterlist.get(position).get("agent_id"));
                parmas.put("push_id",
                        (String) adapterlist.get(position).get("id"));
                return parmas;
            }

        };
        getAgentAndQuoteActivity().showProgressDialog();
        MyApplication.getInstance().addRequest(stringRequest, TAG);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        getAgentListTask(false);
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        MyApplication.getInstance().cancelAllRequests(TAG);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        // OrderId = getIntent().getStringExtra("order_id");
        // OrderSn = getIntent().getStringExtra("order_sn");
        // quoteList = getIntent().getBooleanExtra("quote_list", false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        LayoutInflater lf = LayoutInflater.from(getActivity());
        View view = lf.inflate(R.layout.my_query_list, null);
        mAgentListReduceTime = (LinearLayout) view.findViewById(R.id.agent_list_reduce_time);
        InitListView(view);
        return view;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
    }

}
