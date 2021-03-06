package com.hypericum.hypapp.activity;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ciyashop.library.apicall.PostApi;
import com.ciyashop.library.apicall.URLS;
import com.ciyashop.library.apicall.interfaces.OnResponseListner;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hypericum.hypapp.R;
import com.hypericum.hypapp.adapter.MyRewardsAdapter;
import com.hypericum.hypapp.customview.textview.TextViewBold;
import com.hypericum.hypapp.customview.textview.TextViewLight;
import com.hypericum.hypapp.customview.textview.TextViewRegular;
import com.hypericum.hypapp.interfaces.OnItemClickListner;
import com.hypericum.hypapp.javaclasses.FilterSelectedList;
import com.hypericum.hypapp.model.Rewards;
import com.hypericum.hypapp.utils.BaseActivity;
import com.hypericum.hypapp.utils.Constant;
import com.hypericum.hypapp.utils.RequestParamUtils;
import com.hypericum.hypapp.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RewardsActivity extends BaseActivity implements OnItemClickListner, OnResponseListner {

    @BindView(R.id.rvMyRewards)
    RecyclerView rvMyRewards;

    @BindView(R.id.llEmpty)
    LinearLayout llEmpty;

    @BindView(R.id.tvEmptyTitle)
    TextViewBold tvEmptyTitle;

    @BindView(R.id.tvEmptyDesc)
    TextViewLight tvEmptyDesc;

    @BindView(R.id.tvContinueShopping)
    TextViewRegular tvContinueShopping;

    private MyRewardsAdapter myRewardsAdapter;
    private int page = 1;

    private List<Rewards> list = new ArrayList<>();

    int pastVisiblesItems, visibleItemCount, totalItemCount;
    Boolean setNoItemFound = false;
    private boolean loading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards);
        ButterKnife.bind(this);
        setToolbarTheme();
        hideSearchNotification();
        settvTitle(getResources().getString(R.string.my_reward));
        showBackButton();
        myRewards(true);
        seMyRewardAdapter();
        setScreenLayoutDirection();
        setEmptyColor();
    }

    public void setEmptyColor() {

        TextViewRegular tvContinueShopping = findViewById(R.id.tvContinueShopping);
        ImageView ivGo = findViewById(R.id.ivGo);
        tvContinueShopping.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setStroke(5, Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvContinueShopping.setBackground(gradientDrawable);
        ivGo.setColorFilter(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
    }

    public void seMyRewardAdapter() {
        myRewardsAdapter = new MyRewardsAdapter(this, this);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvMyRewards.setLayoutManager(mLayoutManager);
        rvMyRewards.setAdapter(myRewardsAdapter);
        rvMyRewards.setNestedScrollingEnabled(false);
        rvMyRewards.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            if (setNoItemFound != true) {
                                loading = false;
                                page = page + 1;
                                Log.e("End ", "Last Item Wow  and page no:- " + page);
                                myRewards(false);
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onItemClick(int position, String value, int outerPos) {

    }

    public void myRewards(Boolean dialog) {
        if (Utils.isInternetConnected(this)) {
            if (dialog) {
                showProgress("");
            }

            PostApi postApi = new PostApi(this, RequestParamUtils.coupons, this, getlanuage());
            try {
                JSONObject jsonObject;
                if (FilterSelectedList.filterJson.equals("")) {
                    jsonObject = new JSONObject();
                } else {
                    jsonObject = new JSONObject(FilterSelectedList.filterJson);
                }
                if (Constant.DEVICE_TOKEN == null || Constant.DEVICE_TOKEN.equals("")) {
                    String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                    Constant.DEVICE_TOKEN = refreshedToken;
                }
                jsonObject.put(RequestParamUtils.PAGE, page);
                jsonObject.put(RequestParamUtils.DEVICE_TOKEN, Constant.DEVICE_TOKEN);
                postApi.callPostApi(new URLS().REWARDS, jsonObject.toString());
            } catch (Exception e) {
                Log.e("Json Exception", e.getMessage());
            }

        } else {
            Toast.makeText(this, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onResponse(String response, String methodName) {

        if (methodName.equals(RequestParamUtils.coupons)) {

            dismissProgress();
            if (response != null && response.length() > 0) {
                try {
                    //set call here
                    loading = true;

                    Rewards rewardsRider = new Gson().fromJson(
                            response, new TypeToken<Rewards>() {
                            }.getType());
                    myRewardsAdapter.addAll(rewardsRider.data);

                    if (myRewardsAdapter.getList().size() == 0) {
                        setNoItemFound = true;
                        if (rvMyRewards.getAdapter().getItemCount() == 0) {
                            noCouponFound();
                        }
                    }
                } catch (Exception e) {
                    try {
                        JSONObject object = new JSONObject(response);
//                        if (object.getString("message").equals("No Coupons found")) {
//
//                        }
                        noCouponFound();
                    } catch (JSONException e1) {
                        Log.e("noproductJSONException", e1.getMessage());
                    }
                    Log.e(methodName + "Gson Exception is ", e.getMessage());
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show(); //display in long period of time
            }
        }
    }

    public void noCouponFound() {
        setNoItemFound = true;
        if (rvMyRewards.getAdapter().getItemCount() == 0) {
            llEmpty.setVisibility(View.VISIBLE);
            tvEmptyTitle.setText(R.string.no_coupon_found);
            tvContinueShopping.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }
}
