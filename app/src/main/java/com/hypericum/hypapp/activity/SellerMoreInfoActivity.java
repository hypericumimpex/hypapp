package com.hypericum.hypapp.activity;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;

import com.hypericum.hypapp.R;
import com.hypericum.hypapp.customview.textview.TextViewLight;
import com.hypericum.hypapp.utils.BaseActivity;
import com.hypericum.hypapp.utils.RequestParamUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SellerMoreInfoActivity extends BaseActivity {

    @BindView(R.id.tvContentDesc)
    TextViewLight tvContentDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_more_info);
        ButterKnife.bind(this);
        settvTitle(RequestParamUtils.Dealer);
        setToolbarTheme();
        showBackButton();
        String data = getIntent().getExtras().getString(RequestParamUtils.data);
        if(data == null || data.equals("")) {
            tvContentDesc.setText(R.string.no_data_found);
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvContentDesc.setText(Html.fromHtml(data, Html.FROM_HTML_MODE_COMPACT));
            } else {
                tvContentDesc.setText(Html.fromHtml(data));
            }
        }

    }
}
