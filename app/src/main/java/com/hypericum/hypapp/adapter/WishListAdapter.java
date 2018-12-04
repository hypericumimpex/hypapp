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
import com.hypericum.hypapp.customview.like.animation.SparkButton;
import com.hypericum.hypapp.customview.textview.TextViewLight;
import com.hypericum.hypapp.customview.textview.TextViewRegular;
import com.hypericum.hypapp.helper.DatabaseHelper;
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
 * Created by User on 17-11-2017.
 */

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.RecentViewHolder> {


    private List<CategoryList> list = new ArrayList<>();
    private Activity activity;
    private OnItemClickListner onItemClickListner;
    private DatabaseHelper databaseHelper;


    public WishListAdapter(Activity activity, OnItemClickListner onItemClickListner) {
        this.activity = activity;
        this.onItemClickListner = onItemClickListner;
        databaseHelper = new DatabaseHelper(activity);
    }

    public void addAll(List<CategoryList> list) {
        this.list = list;

        notifyDataSetChanged();
    }


    @Override
    public RecentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_category, parent, false);

        return new RecentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecentViewHolder holder, final int position) {
        holder.ivCart.setVisibility(View.VISIBLE);
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


        if (!list.get(position).averageRating.equals("")) {
            holder.ratingBar.setRating(Float.parseFloat(list.get(position).averageRating));
        } else {
            holder.ratingBar.setRating(0);
        }
        Picasso.with(activity).load(list.get(position).appthumbnail).into(holder.ivImage);
//        if (list.get(position).images.size() > 0) {
//            holder.ivImage.setVisibility(View.VISIBLE);
//
//        } else {
//            holder.ivImage.setVisibility(View.INVISIBLE);
//        }
//        holder.tvName.setText(list.get(position).name);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//            tvProductName.setText(categoryList.name + "");
            holder.tvName.setText(Html.fromHtml(list.get(position).name + "", Html.FROM_HTML_MODE_LEGACY));
        } else {
//            tvProductName.setText(categoryList.name + "");
            holder.tvName.setText(Html.fromHtml(list.get(position).name + ""));
        }

        holder.tvPrice.setTextSize(15);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvPrice.setText(Html.fromHtml(list.get(position).priceHtml, Html.FROM_HTML_MODE_COMPACT));

        } else {
            holder.tvPrice.setText(Html.fromHtml(list.get(position).priceHtml));
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
        holder.ivWishList.setActivetint(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
        holder.ivWishList.setColors(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)), Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));

        holder.ivCart.setVisibility(View.VISIBLE);
        if (Constant.IS_WISH_LIST_ACTIVE) {
            holder.ivWishList.setVisibility(View.VISIBLE);
            holder.ivWishList.setChecked(true);
        } else {
            holder.ivWishList.setVisibility(View.GONE);
        }


        holder.ivWishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ivWishList.setChecked(false);
                onItemClickListner.onItemClick(list.get(position).id, RequestParamUtils.delete, list.size());
                databaseHelper.deleteFromWishList(list.get(position).id + "");
                list.remove(position);
                notifyDataSetChanged();
            }
        });
        holder.ivCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.CATEGORYDETAIL = list.get(position);
                Intent intent = new Intent(activity, ProductDetailActivity.class);
                intent.putExtra(RequestParamUtils.ID, list.get(position).id);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class RecentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.llMain)
        LinearLayout llMain;

        @BindView(R.id.ratingBar)
        MaterialRatingBar ratingBar;

        @BindView(R.id.ivImage)
        ImageView ivImage;

        @BindView(R.id.ivCart)
        ImageView ivCart;

        @BindView(R.id.ivWishList)
        SparkButton ivWishList;

        @BindView(R.id.tvName)
        TextViewLight tvName;

        @BindView(R.id.tvPrice)
        TextViewRegular tvPrice;

        @BindView(R.id.tvPrice1)
        TextViewRegular tvPrice1;


        public RecentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
