package com.foryou.truck.viewholds;


import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.foryou.truck.R;
import com.foryou.truck.entity.BankNameListEntity;
import in.srain.cube.views.list.ViewHolderBase;

/**
 *
 */
public class BankNameListVH extends ViewHolderBase<BankNameListEntity.BankNameItem> {

    private String TAG="BankNameListAct";
    private TextView bankNameTv;

    private int mCurrentIndex;
    BankNameListEntity.BankNameItem mDataItem;

    public BankNameListVH( String tag) {
        TAG=tag;
    }


    @Override
    public View createView(LayoutInflater inflater) {
        View v = inflater.inflate(R.layout.item_bankname, null);
        bankNameTv = (TextView) v.findViewById(R.id.bank_name_tv);
       // v.setOnClickListener(mItemListener);
        return v;
    }

    @Override
    public void showData(int position, BankNameListEntity.BankNameItem itemData) {
        mDataItem = itemData;
        bankNameTv.setText(itemData.name);

    }


    private View.OnClickListener mItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        }
    };

}