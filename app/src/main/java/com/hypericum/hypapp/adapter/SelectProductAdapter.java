package com.hypericum.hypapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ciyashop.library.apicall.PostApi;
import com.ciyashop.library.apicall.URLS;
import com.ciyashop.library.apicall.interfaces.OnResponseListner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hypericum.hypapp.R;
import com.hypericum.hypapp.activity.ProductDetailActivity;
import com.hypericum.hypapp.customview.MaterialRatingBar;
import com.hypericum.hypapp.customview.textview.TextViewLight;
import com.hypericum.hypapp.customview.textview.TextViewRegular;
import com.hypericum.hypapp.interfaces.OnItemClickListner;
import com.hypericum.hypapp.model.CategoryList;
import com.hypericum.hypapp.model.Home;
import com.hypericum.hypapp.utils.BaseActivity;
import com.hypericum.hypapp.utils.Constant;
import com.hypericum.hypapp.utils.RequestParamUtils;
import com.hypericum.hypapp.utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectProductAdapter extends RecyclerView.Adapter<SelectProductAdapter.SelectProductViewHolder> implements OnResponseListner {

    private List<Home.Product> list = new ArrayList<>();
    private Activity activity;
    private OnItemClickListner onItemClickListner;
    private int width = 0, height = 0;

    public SelectProductAdapter(Activity activity, OnItemClickListner onItemClickListner) {
        this.activity = activity;
        this.onItemClickListner = onItemClickListner;
        notifyDataSetChanged();
    }

    public void addAll(List<Home.Product> list) {
        this.list = list;
        getWidthAndHeight();
        notifyDataSetChanged();
    }

    public List<Home.Product> getList() {
        return list;
    }

    @Override
    public SelectProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);

        return new SelectProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SelectProductViewHolder holder, final int position) {
        holder.llMain.getLayoutParams().width = width;
        holder.llMain.getLayoutParams().height = height;
        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getProductDetail(String.valueOf(list.get(position).id));
            }
        });
        if (list.get(position).image != null) {
            Picasso.with(activity).load(list.get(position).image)
                    .error(R.drawable.no_image_available)
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.drawable.no_image_available);
           /*
            Picasso.with(activity).load(R.drawable.no_image_available)
                    .placeholder(R.drawable.no_image_available)
                    .error(R.drawable.no_image_available)
                    .into(holder.ivImage);*/
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            holder.tvName.setText(Html.fromHtml(list.get(position).title + "", Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.tvName.setText(Html.fromHtml(list.get(position).title + ""));
        }

        holder.tvPrice.setTextSize(15);
        if (list.get(position).priceHtml != null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.tvPrice.setText(Html.fromHtml(list.get(position).priceHtml + "", Html.FROM_HTML_MODE_COMPACT));

            } else {
                holder.tvPrice.setText(Html.fromHtml(list.get(position).priceHtml) + "");
            }

        if (holder.tvPrice.getText().toString().contains("–")) {
            holder.tvPrice.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
            holder.tvPrice1.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
            holder.tvPrice1.setText("");
        } else if (holder.tvPrice.getText().toString().contains(" ")) {
            String[] array = holder.tvPrice.getText().toString().split(" ");
            if (array.length > 1) {
                String firstPrice = array[0];
                String seconfPrice = array[1];

                String htmlText = "<html>" + " " + firstPrice + "</font></html>";
                String htmlText1 = "<html>" + " " + seconfPrice + "</font></html>";

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    holder.tvPrice.setText(Html.fromHtml(htmlText, Html.FROM_HTML_MODE_COMPACT));
                    holder.tvPrice1.setText(Html.fromHtml(htmlText1, Html.FROM_HTML_MODE_COMPACT));

                } else {
                    holder.tvPrice.setText(Html.fromHtml(htmlText));
                    holder.tvPrice1.setText(Html.fromHtml(htmlText1));
                }
                String price11 = holder.tvPrice.getText().toString().replace(Constant.CURRENCYSYMBOL, "");
                String price22 = holder.tvPrice1.getText().toString().replace(Constant.CURRENCYSYMBOL, "");

                String price1 = price11.replace(",", "");
                String price2 = price22.replace(",", "");
                try {

                    if (Double.parseDouble(price1) > Double.parseDouble(price2)) {
                        holder.tvPrice.setPaintFlags(holder.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        holder.tvPrice1.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
                        holder.tvPrice.setTextColor(((BaseActivity) activity).getResources().getColor(R.color.gray_light));
                        holder.tvPrice.setTextSize(14);
                        holder.tvPrice1.setTextSize(15);
                    } else {
                        holder.tvPrice1.setPaintFlags(holder.tvPrice1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        holder.tvPrice.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
                        holder.tvPrice1.setTextColor(((BaseActivity) activity).getResources().getColor(R.color.gray_light));
                        holder.tvPrice.setTextSize(15);
                        holder.tvPrice1.setTextSize(14);
                    }
                } catch (Exception e) {
                    Log.e("Exception is ", e.getMessage());
                    holder.tvPrice.setPaintFlags(holder.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
            }
        } else {
            holder.tvPrice1.setText(holder.tvPrice.getText().toString());
            holder.tvPrice1.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
            holder.tvPrice.setText("");
        }

        holder.ratingBar.setRating(Float.parseFloat(list.get(position).rating));

    }

    @Override
    public int getItemCount() {

        if (list.size() > 4) {
            return 4;
        } else {
            return list.size();
        }

    }

    public void getWidthAndHeight() {
        int height_value = activity.getResources().getInteger(R.integer.height);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels / 2 - 10;
        height = width + height_value;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void getProductDetail(String groupid) {
        if (Utils.isInternetConnected(activity)) {
            ((BaseActivity) activity).showProgress("");
            PostApi postApi = new PostApi(activity, RequestParamUtils.getProductDetail, this, ((BaseActivity) activity).getlanuage());
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(RequestParamUtils.INCLUDE, groupid);
                postApi.callPostApi(new URLS().PRODUCT_URL + ((BaseActivity) activity).getPreferences().getString(RequestParamUtils.CurrencyText, ""), jsonObject.toString());
            } catch (Exception e) {
                Log.e("Json Exception", e.getMessage());
            }
        } else {
            Toast.makeText(activity, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onResponse(String response, String methodName) {
        if (methodName.equals(RequestParamUtils.getProductDetail)) {
            if (response != null && response.length() > 0) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    CategoryList categoryListRider = new Gson().fromJson(
                            jsonArray.get(0).toString(), new TypeToken<CategoryList>() {
                            }.getType());
                    Constant.CATEGORYDETAIL = categoryListRider;

                    if (categoryListRider.type.equals(RequestParamUtils.external)) {

                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(categoryListRider.externalUrl));
                        activity.startActivity(browserIntent);
                    } else {
                        Intent intent = new Intent(activity, ProductDetailActivity.class);
                        activity.startActivity(intent);
                    }


                } catch (Exception e) {
                    Log.e(methodName + "Gson Exception is ", e.getMessage());
                }
                ((BaseActivity) activity).dismissProgress();
            }
        }
    }

    public class SelectProductViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.llMain)
        LinearLayout llMain;

        @BindView(R.id.ivImage)
        ImageView ivImage;

        @BindView(R.id.tvPrice)
        TextViewRegular tvPrice;

        @BindView(R.id.tvName)
        TextViewLight tvName;

        @BindView(R.id.ratingBar)
        MaterialRatingBar ratingBar;

        @BindView(R.id.tvPrice1)
        TextViewRegular tvPrice1;

        public SelectProductViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


}
