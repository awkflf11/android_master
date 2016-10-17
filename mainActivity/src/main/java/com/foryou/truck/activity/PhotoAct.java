package com.foryou.truck.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;

import com.foryou.truck.BaseActivity;
import com.foryou.truck.MyApplication;
import com.foryou.truck.R;
import com.foryou.truck.tools.ImageLoaderUtils;
import com.foryou.truck.tools.ImageTools;
import com.foryou.truck.tools.SdCardUtil;
import com.foryou.truck.tools.Tools;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.ImageUtils;
import com.foryou.truck.util.ToastUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @des:上传证件的页面
 */
public class PhotoAct extends BaseActivity {
    private static final String TAG = "PhotoAct";
    private Context mContext;
    @BindView(id = R.id.default_img)
    private ImageView defaultIv;
    @BindView(id = R.id.photo_bt, click = true)
    private Button photoBtn;
    @BindView(id = R.id.xiangce_bt, click = true)
    private Button xiangCeBtn;
    @BindView(id = R.id.cancle_bn, click = true)
    private Button cancleBtn;

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.act_photo);
    }

    private int  picType=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        picType=getIntent().getIntExtra("picType",-1);
        //initView();
    }

    private void initView() {
        ShowBackView();
        setTitle("设置");
    }

    @Override
    public void onClickListener(int id) {
        Intent intent;
        switch (id) {
            case R.id.photo_bt:
                 intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 调用相机拍照后的照片存储的路径
                //new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + "avatar.png"))
                File file = new File(SdCardUtil.getTouXiangPath(mContext) + "truck_pic.png");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(intent, 2);
                break;
            case R.id.xiangce_bt:
                 intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
                break;
            case R.id.cancle_bn:
                onBackPressed();
                //finish();
                break;
            default:
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:// 直接从相册获取
                if (data != null) {
                    ImageUtils.startPhotoZoom(mContext, data.getData());
                }
                break;
            case 2://调用相机拍照时
                //getExternalFilesDir(Environment.DIRECTORY_PICTURES)+ File.separator
                File temp = new File(SdCardUtil.getTouXiangPath(mContext) + "truck_pic.png");
                ImageUtils.startPhotoZoom(mContext, Uri.fromFile(temp));
                break;
            case 3:// 取得裁剪后的图片
                if (data != null) {
                    completeCut(data);
                }
                finish();
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void completeCut(Intent data ){
        setResult(RESULT_OK);
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            if (photo == null) {
                return;
            }
           String fileName="";
            if(picType==0){
                fileName="truck_pic0";
            }else if(picType==1){
                fileName="truck_pic1";
            }else if(picType==2){
                fileName="truck_pic2";
            }else if(picType==3){
                fileName="truck_pic3";
            }else if(picType==4){
                fileName="truck_pic4";
            }
            ImageTools.savePhotoToSDCard(photo,SdCardUtil.getTouXiangPath(mContext),fileName);
            //upAvatar();
        }

    }

//    public void saveBitmap() {
//        File f = new File("/sdcard/namecard/", picName);
//        if (f.exists()) {
//            f.delete();
//        }
//        try {
//            FileOutputStream out = new FileOutputStream(f);
//            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
//            out.flush();
//            out.close();
//           // Log.i(TAG, "已经保存");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }


    @Override
    public void onBackPressed(){
        super.onBackPressed();
        this.overridePendingTransition(0, 0);

    }

    @Override
    protected void onStop() {
        super.onStop();

        cancelProgressDialog();
        MyApplication.getInstance().cancelAllRequests(TAG);
    }
}