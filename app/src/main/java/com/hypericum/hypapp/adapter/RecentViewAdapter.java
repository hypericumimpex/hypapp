package com.hypericum.hypapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
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
import com.hypericum.hypapp.customview.MaterialRatingBar;
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

public class RecentViewAdapter extends RecyclerView.Adapter<RecentViewAdapter.RecentViewHolde> {

    private List<CategoryList> list = new ArrayList<>();
    private Activity activity;
    private OnItemClickListner onItemClickListner;
    private int width = 0, height = 0;

    public RecentViewAdapter(Activity activity, OnItemClickListner onItemClickListner) {
        this.activity = activity;
        this.onItemClickListner = onItemClickListner;
    }

    public void addAll(List<CategoryList> list) {
        this.list = list;
        if (this.list == null) {
            this.list = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    @Override
    public RecentViewHolde onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent_view, parent, false);

        return new RecentViewHolde(itemView);
    }

    @Override
    public void onBindViewHolder(RecentViewHolde holder, final int position) {
        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (list.get(position).type.equals(RequestParamUtils.external)) {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(list.get(position).externalUrl));
                    activity.startActivity(browserIntent);
                } else {
                    Constant.CATEGORYDETAIL = list.get(position);
                    Intent intent = new Intent(activity, ProductDetailActivity.class);
                    intent.putExtra(RequestParamUtils.ID, list.get(position).id);
                    activity.startActivity(intent);
                }
            }
        });

        if(list.get(position).appthumbnail!=null) {
            Picasso.with(activity).load(list.get(position).appthumbnail)
                    .error(R.drawable.no_image_available)
                    .into(holder.ivImage);
        }else {
            holder.ivImage.setImageResource(R.drawable.no_image_available);
        }
        if (android.os.Build.VERSION.SDK_INT >= android.
                os.Build.VERSION_CODES.N) {
            holder.tvName.setText(Html.fromHtml(list.get(position).name + "", Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.tvName.setText(Html.fromHtml(list.get(position).name + ""));
        }

        holder.tvPrice.setTextSize(15);
        if (list.get(position).priceHtml != null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.tvPrice.setText(Html.fromHtml(list.get(position).priceHtml + "", Html.FROM_HTML_MODE_COMPACT));
            } else {
                holder.tvPrice.setText(Html.fromHtml(list.get(position).priceHtml) + "");
            }
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
                    holder.tvPrice.setPaintFlags(holder.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
            }
        } else {
            holder.tvPrice1.setText(holder.tvPrice.getText().toString());
            holder.tvPrice.setText("");
            holder.tvPrice1.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
        }
        try {
            holder.ratingBar.setRating(Float.valueOf(list.get(position).averageRating));
        } catch (Exception e) {
            Log.e("Exception is ", e.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        if (list.size() > 5) {
            return 5;
        }
        return list.size();
    }

    public class RecentViewHolde extends RecyclerView.ViewHolder {

        @BindView(R.id.llMain)
        LinearLayout llMain;

        @BindView(R.id.tvName)
        TextViewLight tvName;

        @BindView(R.id.tvPrice)
        TextViewRegular tvPrice;

        @BindView(R.id.tvPrice1)
        TextViewRegular tvPrice1;

        @BindView(R.id.ivImage)
        ImageView ivImage;

        @BindView(R.id.ratingBar)
        MaterialRatingBar ratingBar;

        public RecentViewHolde(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}