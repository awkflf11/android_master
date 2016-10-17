package com.foryou.truck.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.foryou.truck.BaseActivity;
import com.foryou.truck.MyApplication;
import com.foryou.truck.entity.CarLoadEntity;
import com.foryou.truck.entity.CommonConfigEntity;
import com.foryou.truck.parser.AgreeMentJsonParser;
import com.foryou.truck.parser.AreasJsonParser;
import com.foryou.truck.parser.BaseJsonParser;
import com.foryou.truck.parser.CarLoadParser;
import com.foryou.truck.parser.CommentJsonParser;
import com.foryou.truck.parser.CommonConfigJsonParser;
import com.foryou.truck.parser.CommonTagParser;
import com.foryou.truck.parser.SimpleJasonParser;
import com.foryou.truck.parser.UserDetailJsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetWorkUtils {
    private static String TAG = "NetWorkUtils";

    public interface NetJsonRespon {
        void onRespon(BaseJsonParser parser);
    }

    public static void BindGtAction(final Context mContext) {

        String uid = SharePerfenceUtil.getUid(mContext);
        final String cid = SharePerfenceUtil.GetGtCid(mContext);
        //boolean bindResult = SharePerfenceUtil.IsGTBindSuccess(mContext);

        UtilsLog.i(TAG, "cid:" + TextUtils.isEmpty(cid) + ",uid:" + TextUtils.isEmpty(uid));
        if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(cid)) {
            String url = UrlConstant.BASE_URL
                    + "/user/bindGt";
            final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
                    mContext, Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // TODO Auto-generated method stub
                            CommonConfigJsonParser mParser = new CommonConfigJsonParser();
                            int result = mParser.parser(response);
                            if (result == 1) {
                                if (mParser.entity.status.equals("Y")) {
                                    //    SharePerfenceUtil
                                    //            .SaveBindGTSuccessResult(mContext);
                                } else {

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
                }

            }, true) {
                @Override
                public Map<String, String> getPostBodyData() {
                    // TODO Auto-generated method stub
                    Map<String, String> parmas = super.getPostBodyData();
                    parmas.put("gt_id", cid);
                    parmas.put("os_type", "0");
                    return parmas;
                }
            };
            MyApplication.getInstance().addRequest(stringRequest, TAG);
        }
    }

    public static void getConfigData(final Context mContext, String tag,
                                     final NetJsonRespon mRespon) {
        String url = UrlConstant.BASE_URL
                + "/common/config";
        final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
                mContext, Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // TODO Auto-generated method stub
                        CommonConfigJsonParser mParser = new CommonConfigJsonParser();
                        int result = mParser.parser(response);
                        if (result == 1) {
                            if (mParser.entity.status.equals("Y")) {
                                SharePerfenceUtil.SaveConfigData(mContext,
                                        mParser.entity);

                                if (mRespon != null) {
                                    mRespon.onRespon(mParser);
                                }
                            } else {

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
            }

        }, true) {
            @Override
            public Map<String, String> getPostBodyData() {
                // TODO Auto-generated method stub
                Map<String, String> parmas = super.getPostBodyData();
                return parmas;
            }
        };
        MyApplication.getInstance().addRequest(stringRequest, tag);
    }

    public static void getAreaData(final Context mContext, String tag,
                                   final NetJsonRespon mRespon) {
        String url = UrlConstant.BASE_URL
                + "/common/region";
        final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
                mContext, Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // TODO Auto-generated method stub
                        SharePerfenceUtil.SaveAreaData(mContext, response);
                        AreasJsonParser mParser = new AreasJsonParser();
                        int result = mParser.parser(response);
                        if (mRespon != null) {
                            mRespon.onRespon(mParser);
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
            }

        }, true) {
            @Override
            public Map<String, String> getPostBodyData() {
                // TODO Auto-generated method stub
                Map<String, String> parmas = super.getPostBodyData();
                return parmas;
            }
        };
        MyApplication.getInstance().addRequest(stringRequest, tag);
    }

    public static void getUserDetail(final Context mContext, String tag,
                                     final NetJsonRespon mRespon) {
        Map<String, String> parmas = new HashMap<String, String>();
        String url = NormalNetworkRequest.getUrl(mContext, UrlConstant.BASE_URL
                 + "/user/detail", parmas);
        final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
                mContext, Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // TODO Auto-generated method stub
                        UtilsLog.i(TAG, "response:" + response);
                        UserDetailJsonParser mParser = new UserDetailJsonParser();
                        int result = mParser.parser(response);
                        if (result == 1) {
                            if (mParser.entity.status.equals("Y")) {
                                SharePerfenceUtil.SaveUserDetail(mContext,
                                        mParser.entity);
                                if (mRespon != null) {
                                    mRespon.onRespon(mParser);
                                }
                            } else {
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
            }

        }, true) {
            @Override
            public Map<String, String> getPostBodyData() {
                // TODO Auto-generated method stub
                Map<String, String> parmas = super.getPostBodyData();
                return parmas;
            }
        };
        MyApplication.getInstance().addRequest(stringRequest, tag);
    }

    // 得到的标准载重：
    public static void SaveCarLoad(final Context mContext,
                                   final NetJsonRespon parser) {
        String url = UrlConstant.BASE_URL
                + "/common/carload";
        final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
                mContext, Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // TODO Auto-generated method stub
                        UtilsLog.i(TAG, "SaveCarLoad response:" + response);
                        CarLoadParser mParser = new CarLoadParser();
                        int result = mParser.parser(response);
                        if (result == 1) {
                            if (mParser.entity.status.equals("Y")) {
                                SharePerfenceUtil.SaveCarLoadData(mContext,
                                        mParser.entity);
                                parser.onRespon(mParser);
                            } else {

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
            }

        }, true) {
            @Override
            public Map<String, String> getPostBodyData() {
                // TODO Auto-generated method stub
                Map<String, String> parmas = super.getPostBodyData();
                return parmas;
            }
        };
        MyApplication.getInstance().addRequest(stringRequest, TAG);
    }

    public static String getCarLoad(String carModeKey, String carLengthKey,
                                    CarLoadEntity entity) {
        String carLoadWeight = "";
        for (int i = 0; i < entity.data.size(); i++) {
            for (int j = 0; j < entity.data.get(i).load_info.size(); j++) {
                if (entity.data.get(i).key.equals(carModeKey)
                        && entity.data.get(i).load_info.get(j).key
                        .equals(carLengthKey)) {
                    carLoadWeight = entity.data.get(i).load_info.get(j).carload;
                    return carLoadWeight;
                }
            }
        }
        return carLoadWeight;

    }

    // 得到常用的标签
    public static void SaveCommonTag(final Context mContext,
                                     final NetJsonRespon parser) {
        // String url = UrlConstant.BASE_URL + "/common/commonTag";
        Map<String, String> parmas = new HashMap<String, String>();
        String url = NormalNetworkRequest.getUrl(mContext, UrlConstant.BASE_URL
                 + "/common/commonTag", parmas);
        UtilsLog.i(TAG, "url:" + url);
        final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
                mContext, Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        UtilsLog.i(TAG, "response:" + response);
                        CommonTagParser mParser = new CommonTagParser();
                        int result = mParser.parser(response);
                        if (result == 1) {
                            if (mParser.entity.status.equals("Y")) {
                                SharePerfenceUtil.SaveCommonTagData(mContext,
                                        mParser.entity);
                                parser.onRespon(mParser);
                            } else {
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
            }

        }, true) {
            @Override
            public Map<String, String> getPostBodyData() {
                Map<String, String> parmas = super.getPostBodyData();
                return parmas;
            }
        };
        MyApplication.getInstance().addRequest(stringRequest, TAG);
    }

    public static void cuiChuArrayDriver(final BaseActivity mContext,
                                         final String tag, final Map<String, String> parmas) {
        String url = UrlConstant.BASE_URL
                + "/order/notifyArrange";

        final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
                mContext, Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        UtilsLog.i(TAG, "response:" + response);
                        mContext.cancelProgressDialog();
                        SimpleJasonParser mParser = new SimpleJasonParser();
                        int result = mParser.parser(response);
                        if (result == 1) {
                            if (mParser.entity.status.equals("Y")) {
                                ToastUtils.toast(mContext, mParser.entity.msg);
                            } else {
                                ToastUtils.toast(mContext, mParser.entity.msg);
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
                mContext.cancelProgressDialog();
            }

        }, true) {
            @Override
            public Map<String, String> getPostBodyData() {
                return parmas;
            }
        };

        mContext.showProgressDialog();
        MyApplication.getInstance().addRequest(stringRequest, TAG);
    }

    public static void reFreshLocation(final BaseActivity mContext,
                                       final String tag, final Map<String, String> parmas,
                                       final NetJsonRespon mRespon) {
        String url = UrlConstant.BASE_URL
                + "/order/locate";

        final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
                mContext, Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        UtilsLog.i(TAG, "response:" + response);
                        mContext.cancelProgressDialog();
                        SimpleJasonParser mParser = new SimpleJasonParser();
                        int result = mParser.parser(response);
                        if (result == 1) {
                            if (mParser.entity.status.equals("Y")) {
                                mRespon.onRespon(mParser);
                            } else {
                                ToastUtils.toast(mContext, mParser.entity.msg);
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
                mContext.cancelProgressDialog();
            }

        }, true) {
            @Override
            public Map<String, String> getPostBodyData() {
                return parmas;
            }
        };

        mContext.showProgressDialog();
        MyApplication.getInstance().addRequest(stringRequest, TAG);
    }

    public static void CommentAdd(final BaseActivity mContext,
                                  final String tag, final Map<String, String> parmas,
                                  final NetJsonRespon mRespon) {
        String url = UrlConstant.BASE_URL
                + "/comment/add";

        final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
                mContext, Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        UtilsLog.i(TAG, "response:" + response);
                        mContext.cancelProgressDialog();
                        SimpleJasonParser mParser = new SimpleJasonParser();
                        int result = mParser.parser(response);
                        if (result == 1) {
                            if (mParser.entity.status.equals("Y")) {
                                mRespon.onRespon(mParser);
                            } else {
                                ToastUtils.toast(mContext, mParser.entity.msg);
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
                mContext.cancelProgressDialog();
            }

        }, true) {
            @Override
            public Map<String, String> getPostBodyData() {
                return parmas;
            }
        };

        mContext.showProgressDialog();
        MyApplication.getInstance().addRequest(stringRequest, TAG);
    }

    public static void getComment(final BaseActivity mContext,
                                  final String tag, final Map<String, String> parmas,
                                  final NetJsonRespon mRespon) {
        String url = UrlConstant.BASE_URL
                + "/comment/getComment";

        final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
                mContext, Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        UtilsLog.i(TAG, "response:" + response);
                        mContext.cancelProgressDialog();
                        CommentJsonParser mParser = new CommentJsonParser();
                        int result = mParser.parser(response);
                        if (result == 1) {
                            if (mParser.entity.status.equals("Y")) {
                                mRespon.onRespon(mParser);
                            } else {
                                ToastUtils.toast(mContext, mParser.entity.msg);
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
                mContext.cancelProgressDialog();
            }

        }, true) {
            @Override
            public Map<String, String> getPostBodyData() {
                return parmas;
            }
        };

        mContext.showProgressDialog();
        MyApplication.getInstance().addRequest(stringRequest, TAG);
    }

    public static void getAggreeMent(final BaseActivity mContext,
                                     final String tag, final Map<String, String> parmas,
                                     final NetJsonRespon mRespon) {
        String url = NormalNetworkRequest.getUrl(mContext, UrlConstant.BASE_URL
                 + "/order/agreement", parmas);

        final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
                mContext, Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        UtilsLog.i(TAG, "response:" + response);
                        mContext.cancelProgressDialog();
                        AgreeMentJsonParser mParser = new AgreeMentJsonParser();
                        int result = mParser.parser(response);
                        if (result == 1) {
                            if (mParser.entity.status.equals("Y")) {
                                mRespon.onRespon(mParser);
                            } else {
                                ToastUtils.toast(mContext, mParser.entity.msg);
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
                mContext.cancelProgressDialog();
            }

        }, true) {
            @Override
            public Map<String, String> getPostBodyData() {
                return parmas;
            }
        };

        mContext.showProgressDialog();
        MyApplication.getInstance().addRequest(stringRequest, TAG);
    }

    //获取广告位
    public static void getAdsData(final BaseActivity mContext,
                                     final String tag, final Map<String, String> parmas,
                                     final NetJsonRespon mRespon) {
        String url = NormalNetworkRequest.getUrl(mContext, UrlConstant.BASE_URL
                + "/common/indexad", parmas);

        final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
                mContext, Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        UtilsLog.i(TAG, "response:" + response);
                        mContext.cancelProgressDialog();
                        AgreeMentJsonParser mParser = new AgreeMentJsonParser();
                        int result = mParser.parser(response);
                        if (result == 1) {
                            if (mParser.entity.status.equals("Y")) {
                                mRespon.onRespon(mParser);
                            } else {
                                ToastUtils.toast(mContext, mParser.entity.msg);
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
                mContext.cancelProgressDialog();
            }

        }, true) {
            @Override
            public Map<String, String> getPostBodyData() {
                return parmas;
            }
        };
        mContext.showProgressDialog();
        MyApplication.getInstance().addRequest(stringRequest, TAG);
    }


    public static void getUserContract(final Context mContext,
                                       Response.Listener<String> response, final String tag) {
        Map<String, String> parmas = new HashMap<String, String>();
        String url = NormalNetworkRequest.getUrl(mContext, UrlConstant.BASE_URL
                 + "/user/contact", parmas);

        final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
                mContext, Request.Method.GET, url, response,
                new Response.ErrorListener() {
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
                    }

                }, true) {

        };
        MyApplication.getInstance().addRequest(stringRequest, tag);
    }


    public static void BasePostNetWorkRequest(final Context mContext,
                                              final Response.Listener<String> response, final String tag
            , final String baseUrl, final Map<String, String> map) {

        String url = UrlConstant.BASE_URL + "/" + baseUrl;

        final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
                mContext, Request.Method.POST, url, response,
                new Response.ErrorListener() {
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
                        //ToastUtils.toast(mContext, "网络连接失败,请稍候再试");
                    }

                }, true) {
            @Override
            public Map<String, String> getPostBodyData() {
                if (map != null) {
                    return map;
                } else {
                    return super.getPostBodyData();
                }
            }
        };
        MyApplication.getInstance().addRequest(stringRequest, tag);
    }

    public static void BaseGetNetWorkRequest(final Context mContext,
                                             final Response.Listener<String> response, final String tag
            , final String baseUrl, final Map<String, String> map) {

        String url = NormalNetworkRequest.getUrl(mContext, UrlConstant.BASE_URL
                + baseUrl, map);

        final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
                mContext, Request.Method.GET, url, response,
                new Response.ErrorListener() {
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
                       // ToastUtils.toast(mContext, "网络连接失败,请稍候再试");
                    }

                }, true) {
        };
        MyApplication.getInstance().addRequest(stringRequest, tag);
    }


    /**
     * @des:将常用的车型，车长和 全的车型，车长组装成一个集合：
     */

    public static List<Map<String, Object>> getCommonCarLength(
            CommonConfigEntity mConfigEntity) {
        List<Map<String, Object>> commonCarLengthList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < mConfigEntity.data.car_length_common.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("value", mConfigEntity.data.car_length_common.get(i).value);
            map.put("key", mConfigEntity.data.car_length_common.get(i).key);
            commonCarLengthList.add(map);
        }
        List<Map<String, Object>> commonCarLengthList2 = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < mConfigEntity.data.car_length.size(); i++) {
            {
                int j = 0;
                for (j = 0; j < commonCarLengthList.size(); j++) {
                    if (mConfigEntity.data.car_length.get(i).value
                            .equals(commonCarLengthList.get(j).get(
                                    "value"))) {
                        break;
                    }
                }
                if (j == commonCarLengthList.size()) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("value", mConfigEntity.data.car_length.get(i).value);
                    map.put("key", mConfigEntity.data.car_length.get(i).key);
                    commonCarLengthList2.add(map);
                }
            }

        }
        commonCarLengthList.addAll(commonCarLengthList2);
        return commonCarLengthList;
    }

    public static List<Map<String, Object>> getCommonCarType(
            CommonConfigEntity mConfigEntity) {

        List<Map<String, Object>> commonCarTypeList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < mConfigEntity.data.car_model_common.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("value", mConfigEntity.data.car_model_common.get(i).value);
            map.put("key", mConfigEntity.data.car_model_common.get(i).key);
            commonCarTypeList.add(map);
        }
        List<Map<String, Object>> commonCarTypeList2 = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < mConfigEntity.data.car_model.size(); i++) {
            {
                int j = 0;
                for (j = 0; j < commonCarTypeList.size(); j++) {
                    if (mConfigEntity.data.car_model.get(i).value
                            .equals(commonCarTypeList.get(j).get(
                                    "value"))) {
                        break;
                    }
                }
                if (j == commonCarTypeList.size()) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("value", mConfigEntity.data.car_model.get(i).value);
                    map.put("key", mConfigEntity.data.car_model.get(i).key);
                    commonCarTypeList2.add(map);
                }
            }

        }
        commonCarTypeList.addAll(commonCarTypeList2);
        return commonCarTypeList;
    }

}
