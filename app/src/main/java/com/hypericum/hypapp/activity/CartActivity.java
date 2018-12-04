package com.hypericum.hypapp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ciyashop.library.apicall.PostApi;
import com.ciyashop.library.apicall.URLS;
import com.ciyashop.library.apicall.interfaces.OnResponseListner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hypericum.hypapp.R;
import com.hypericum.hypapp.adapter.CartAdapter;
import com.hypericum.hypapp.customview.textview.TextViewBold;
import com.hypericum.hypapp.customview.textview.TextViewLight;
import com.hypericum.hypapp.customview.textview.TextViewRegular;
import com.hypericum.hypapp.helper.DatabaseHelper;
import com.hypericum.hypapp.interfaces.OnItemClickListner;
import com.hypericum.hypapp.model.Cart;
import com.hypericum.hypapp.model.CategoryList;
import com.hypericum.hypapp.model.Customer;
import com.hypericum.hypapp.utils.BaseActivity;
import com.hypericum.hypapp.utils.Constant;
import com.hypericum.hypapp.utils.RequestParamUtils;
import com.hypericum.hypapp.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CartActivity extends BaseActivity implements OnItemClickListner, OnResponseListner {

    private static final String TAG = "CartActivity";
    @BindView(R.id.rvCart)
    RecyclerView rvCart;

    @BindView(R.id.llMain)
    LinearLayout llMain;

    @BindView(R.id.llEmpty)
    LinearLayout llEmpty;

    @BindView(R.id.tvEmptyTitle)
    TextViewBold tvEmptyTitle;

    @BindView(R.id.tvTotalItem)
    TextViewLight tvTotalItem;

    @BindView(R.id.tvAmount)
    TextViewRegular tvAmount;

    @BindView(R.id.tvTotalAmount)
    TextViewRegular tvTotalAmount;

    @BindView(R.id.tvViewAmount)
    TextViewRegular tvViewAmount;

    @BindView(R.id.tvContinue)
    TextViewRegular tvContinue;

    @BindView(R.id.llViewDetail)
    LinearLayout llViewDetail;

    @BindView(R.id.tvContinueShopping)
    TextViewRegular tvContinueShopping;

    @BindView(R.id.ivGo)
    ImageView ivGo;

    private CartAdapter cartAdapter;
    private DatabaseHelper databaseHelper;
    private Bundle bundle;
    private String id;
    private int buyNow;
    private String checkOutUrl, THANKYOU, HOMEURL, THANKYOUMAIN;
    private Customer customer = new Customer();
    private boolean isLogin;
    private String customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(this);
        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);
        setToolbarTheme();
        setThemeColor();
        setScreenLayoutDirection();
        getIntentData();
        setCartAdapter();
        settvTitle(getString(R.string.cart));
        showBackButton();
        hideSearchNotification();
        getCartData();

        customerId = getPreferences().getString(RequestParamUtils.ID, "");
    }

    public void setThemeColor() {
        llViewDetail.setBackgroundColor(Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
        setEmptyColor();
    }

    public void getIntentData() {
        bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getString(RequestParamUtils.ID);
            buyNow = bundle.getInt(RequestParamUtils.buynow);
        }
    }

    public void setCartAdapter() {
        cartAdapter = new CartAdapter(this, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvCart.setLayoutManager(mLayoutManager);
        rvCart.setAdapter(cartAdapter);
        cartAdapter.isFromBuyNow(buyNow);
        rvCart.setNestedScrollingEnabled(false);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Only if you need to restore open/close state when
        // the orientation is changed
        if (cartAdapter != null) {
            cartAdapter.saveStates(outState);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Only if you need to restore open/close state when
        // the orientation is changed
        if (cartAdapter != null) {
            cartAdapter.restoreStates(savedInstanceState);
        }
    }

    /**
     * {@link OnItemClickListner#onItemClick(int position, String value, int OuterPos)) onClick} may be used on the
     * method.
     *
     * @see OnItemClickListner
     */
    @Override
    public void onItemClick(int position, String value, int OuterPos) {
        if (value.equals(RequestParamUtils.delete)) {
            if (cartAdapter.getList().size() == 0) {
                isEmptlyLayout(true);
            } else {
                setTotalCount();
            }
        } else if (value.equals(RequestParamUtils.increment) || value.equals(RequestParamUtils.decrement)) {
            setTotalCount();
        } else if (value.equals(RequestParamUtils.detail)) {
            databaseHelper.deleteFromBuyNow(OuterPos + "");
        }

    }

    public void isEmptlyLayout(boolean isEMpty) {
        if (isEMpty) {
            llEmpty.setVisibility(View.VISIBLE);
            llMain.setVisibility(View.GONE);
            tvEmptyTitle.setText(R.string.cart_empty);
        } else {
            llEmpty.setVisibility(View.GONE);
            llMain.setVisibility(View.VISIBLE);
        }
    }

    public void getCartData() {
        List<Cart> cartList = databaseHelper.getFromCart(buyNow);
        if (cartList.size() > 0) {
            for (int i = 0; i < cartList.size(); i++) {
                String product = cartList.get(i).getProduct();
                try {
                    CategoryList categoryListRider = new Gson().fromJson(product, new TypeToken<CategoryList>() {
                    }.getType());
                    cartList.get(i).setCategoryList(categoryListRider);
                } catch (Exception e) {
                    Log.e("Gson Exception", "in Recent Product Get" + e.getMessage());
                }
            }
            cartAdapter.addAll(cartList);
            setTotalCount();

        } else {
            isEmptlyLayout(true);
        }
    }

    public void setTotalCount() {
        tvTotalItem.setText(cartAdapter.getList().size() + getString(R.string.items));
        for (int i = 0; i < cartAdapter.getList().size(); i++) {
            String price = Html.fromHtml(cartAdapter.getList().get(i).getCategoryList().priceHtml).toString();
            if (Constant.CURRENCYSYMBOL == null && !price.equals("")) {
                Constant.CURRENCYSYMBOL = price.charAt(i) + "";
                break;
            }
        }
        float amount = 0;
        for (int i = 0; i < cartAdapter.getList().size(); i++) {
            amount = amount + (Float.parseFloat(cartAdapter.getList().get(i).getCategoryList().price) * cartAdapter.getList().get(i).getQuantity());
        }
        if (Constant.CURRENCYSYMBOLPOSTION.equals("left")) {
            tvAmount.setText(Constant.CURRENCYSYMBOL + Constant.setDecimal((double) amount) + "");
            tvTotalAmount.setText(Constant.CURRENCYSYMBOL + Constant.setDecimal((double) amount) + "");
            tvViewAmount.setText(Constant.CURRENCYSYMBOL + Constant.setDecimal((double) amount) + "");
        } else if (Constant.CURRENCYSYMBOLPOSTION.equals("right")) {
            tvAmount.setText(Constant.setDecimal((double) amount) + Constant.CURRENCYSYMBOL + "");
            tvTotalAmount.setText(Constant.setDecimal((double) amount) + Constant.CURRENCYSYMBOL + "");
            tvViewAmount.setText(Constant.setDecimal((double) amount) + Constant.CURRENCYSYMBOL + "");
        } else if (Constant.CURRENCYSYMBOLPOSTION.equals("left_space")) {
            tvAmount.setText(Constant.CURRENCYSYMBOL + " " + Constant.setDecimal((double) amount) + "");
            tvTotalAmount.setText(Constant.CURRENCYSYMBOL + " " + Constant.setDecimal((double) amount) + "");
            tvViewAmount.setText(Constant.CURRENCYSYMBOL + " " + Constant.setDecimal((double) amount) + "");
        } else if (Constant.CURRENCYSYMBOLPOSTION.equals("right_space")) {
            tvAmount.setText(Constant.setDecimal((double) amount) + " " + Constant.CURRENCYSYMBOL + "");
            tvTotalAmount.setText(Constant.setDecimal((double) amount) + " " + Constant.CURRENCYSYMBOL + "");
            tvViewAmount.setText(Constant.setDecimal((double) amount) + " " + Constant.CURRENCYSYMBOL + "");
        }
    }


    @OnClick(R.id.tvContinue)
    public void tvContinueClick() {
        if (Constant.IS_GUEST_CHECKOUT_ACTIVE) {
            addToCartCheckOut();
        } else {
            if (customerId == "" || customerId == null) {
                isLogin = true;
                Intent i = new Intent(this, LogInActivity.class);
                startActivity(i);
            } else {
                addToCartCheckOut();
            }
        }
    }


    public JSONArray getCartDataForAPI() {
        List<Cart> cartList = databaseHelper.getFromCart(buyNow);
        if (cartList.size() > 0) {

            try {

                JSONArray jsonArray = new JSONArray();

                for (int i = 0; i < cartList.size(); i++) {
//                    String product = cartList.get(i).getProduct();

                    JSONObject object = new JSONObject();

                    object.put(RequestParamUtils.PRODUCT_ID, cartList.get(i).getProductid() + "");
                    object.put(RequestParamUtils.quantity, cartList.get(i).getQuantity() + "");

                    if (cartList.get(i).getVariation() != null) {
                        JSONObject ob1 = new JSONObject(cartList.get(i).getVariation());

                        object.put(RequestParamUtils.variation, ob1);
                    }


                    object.put(RequestParamUtils.variationId, cartList.get(i).getVariationid() + "");

                    jsonArray.put(object);
                }
                return jsonArray;

            } catch (Exception e) {
                Log.e("error", e.getMessage());
            }
        }
        return null;
    }


    public void addToCartCheckOut() {

        if (Utils.isInternetConnected(this)) {
            showProgress("");

            PostApi postApi = new PostApi(this, RequestParamUtils.addToCart, this, getlanuage());
            try {
                JSONObject jsonObject = new JSONObject();
                String customerId = getPreferences().getString(RequestParamUtils.ID, "");
                jsonObject.put(RequestParamUtils.user_id, customerId);
                jsonObject.put(RequestParamUtils.cartItems, getCartDataForAPI());
                jsonObject.put(RequestParamUtils.os, RequestParamUtils.android);
                jsonObject.put(RequestParamUtils.deviceToken, Constant.DEVICE_TOKEN);

                Log.e(TAG, "addToCartCheckOut: " + jsonObject.toString());
                postApi.callPostApi(new URLS().ADD_TO_CART + getPreferences().getString(RequestParamUtils.CurrencyText, ""), jsonObject.toString());

            } catch (Exception e) {
                Log.e("error", e.getMessage());
            }

        } else {
            Toast.makeText(this, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }


    }


    @Override
    public void onResponse(final String response, String methodName) {

        if (methodName.equals(RequestParamUtils.addToCart)) {
            dismissProgress();
            if (response != null && response.length() > 0) {
                Log.e("Response " + methodName, response);
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    String status = jsonObj.getString("status");
                    if (status.equals("success")) {

                        THANKYOUMAIN = jsonObj.getString(RequestParamUtils.THANKYOU);
                        THANKYOU = jsonObj.getString(RequestParamUtils.THANKYOUEND);
                        checkOutUrl = jsonObj.getString(RequestParamUtils.CHECKOUT_URL);
                        HOMEURL = jsonObj.getString(RequestParamUtils.HOME_URL);

                        if (!THANKYOUMAIN.isEmpty()) {
                            Constant.CheckoutURL.add(THANKYOUMAIN);
                        }
                        if (!THANKYOU.isEmpty()) {
                            Constant.CheckoutURL.add(THANKYOU);
                        }

                        Intent intent = new Intent(this, WebviewActivity.class);
                        intent.putExtra(RequestParamUtils.buynow, buyNow);
                        intent.putExtra(RequestParamUtils.THANKYOU, THANKYOU);
                        intent.putExtra(RequestParamUtils.THANKYOUExtra, THANKYOUMAIN);
                        intent.putExtra(RequestParamUtils.CHECKOUT_URL, checkOutUrl);
                        intent.putExtra(RequestParamUtils.HOME_URL, jsonObj.getString(RequestParamUtils.HOME_URL));
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, R.string.something_went_wrong_try_after_somtime, Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception e) {
                    Log.e("error", e.getMessage());
                }
            }
        }
    }


    @Override
    public void onBackPressed() {

        databaseHelper.deleteFromBuyNow(id);
        super.onBackPressed();
    }


    @OnClick(R.id.tvContinueShopping)
    public void tvContinueShoppingClick() {
        Intent i = new Intent(CartActivity.this, HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("Cart Activity", "On Restart Called");

        customerId = getPreferences().getString(RequestParamUtils.ID, "");

        getCartData();
        if (isLogin) {
            isLogin = false;
            if (customerId != "") {
                addToCartCheckOut();
            }

        }
    }
}
