package com.foryou.truck.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.foryou.truck.BaseActivity;
import com.foryou.truck.R;

import java.io.File;

/**
 * Created by dubin on 16/6/16.
 */
public class PdfViewAct extends BaseActivity{
//    //@BindView(id=R.id.pdfView)
//    PDFView pdfView;
//    @BindView(id=R.id.scrollBar)
//    ScrollBar scrollBar;

    String wuyou_pdf;
    String TAG = "PdfViewAct";

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.pdf_view);
    }


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){
                cancelProgressDialog();
//                pdfView.setScrollBar(scrollBar);
//                File file = (File) msg.obj;
//                pdfView.fromFile(file)
//                        .defaultPage(1)
//                        .showMinimap(true)
//                        .enableSwipe(true)
//                        .swipeVertical(true)
//                        .load();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("查看保险单");
        ShowBackView();
        wuyou_pdf = getIntent().getStringExtra("wuyou_pdf");
        Thread t = new Thread() {
            @Override
            public void run() {
                super.run();
                File file = DisplayPdf.downLoadPdfFile("order_sn",wuyou_pdf);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = file;
                mHandler.sendMessage(msg);
            }
        };
        t.start();
        showProgressDialog();
    }

    @Override
    public void onClickListener(int id) {

    }
}
