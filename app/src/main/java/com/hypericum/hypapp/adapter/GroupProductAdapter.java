package com.hypericum.hypapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hypericum.hypapp.R;
import com.hypericum.hypapp.activity.ProductDetailActivity;
import com.hypericum.hypapp.customview.textview.TextViewLight;
import com.hypericum.hypapp.customview.textview.TextViewRegular;
import com.hypericum.hypapp.interfaces.OnItemClickListner;
import com.hypericum.hypapp.model.CategoryList;
import com.hypericum.hypapp.utils.BaseActivity;
import com.hypericum.hypapp.utils.Constant;
import com.hypericum.hypapp.utils.RequestParamUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bhumi Shah on 11/7/2017.
 */

public class GroupProductAdapter extends RecyclerView.Adapter<GroupProductAdapter.GroupProductViewHolder> {

    private List<CategoryList> list = new ArrayList<>();
    private Activity activity;
    private OnItemClickListner onItemClickListner;

    public GroupProductAdapter(Activity activity, OnItemClickListner onItemClickListner) {
        this.activity = activity;
        this.onItemClickListner = onItemClickListner;

    }

    public void addAll(List<CategoryList> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<CategoryList> getList() {
        return list;
    }

    @Override
    public GroupProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group_product, parent, false);

        return new GroupProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GroupProductViewHolder holder, final int position) {
        if (position == list.size() - 1) {
            holder.tvDivider.setVisibility(View.GONE);
        } else {
            holder.tvDivider.setVisibility(View.VISIBLE);
        }

        holder.tvName.setText(list.get(position).name);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvPrice.setText(Html.fromHtml(list.get(position).priceHtml, Html.FROM_HTML_MODE_COMPACT));

        } else {
            holder.tvPrice.setText(Html.fromHtml(list.get(position).priceHtml));
        }
        holder.tvPrice.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
        holder.tvPrice.setTextSize(15);

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
                try {
                    String price11 = holder.tvPrice.getText().toString().replace(Constant.CURRENCYSYMBOL, "");
                    String price22 = holder.tvPrice1.getText().toString().replace(Constant.CURRENCYSYMBOL, "");

                    String price1 = price11.replace(",", "");
                    String price2 = price22.replace(",", "");

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
                    holder.tvPrice.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
                    holder.tvPrice.setPaintFlags(holder.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
            }





        }

        holder.tvDetail.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));



        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.CATEGORYDETAIL = list.get(position);
                Intent intent = new Intent(activity, ProductDetailActivity.class);
                intent.putExtra(RequestParamUtils.ID, list.get(position).id);
                activity.startActivity(intent);

            }
        });

        if (list.get(position).images.size() > 0) {
            holder.ivImage.setVisibility(View.VISIBLE);
            Picasso.with(activity).load(list.get(position).images.get(0).src).into(holder.ivImage);
        } else {
            holder.ivImage.setVisibility(View.INVISIBLE);
        }

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class GroupProductViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.ivImage)
        ImageView ivImage;

        @BindView(R.id.llMain)
        LinearLayout llMain;

        @BindView(R.id.tvName)
        TextViewRegular tvName;

        @BindView(R.id.tvPrice)
        TextViewRegular tvPrice;

        @BindView(R.id.tvPrice1)
        TextViewRegular tvPrice1;

        @BindView(R.id.tvDivider)
        TextViewRegular tvDivider;

        @BindView(R.id.tvDetail)
        TextViewLight tvDetail;



        public GroupProductViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}