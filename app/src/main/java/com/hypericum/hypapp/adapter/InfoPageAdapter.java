package com.hypericum.hypapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hypericum.hypapp.R;
import com.hypericum.hypapp.activity.InfoPageDetailActivity;
import com.hypericum.hypapp.customview.textview.TextViewLight;
import com.hypericum.hypapp.interfaces.OnItemClickListner;
import com.hypericum.hypapp.model.InfoPages;
import com.hypericum.hypapp.utils.RequestParamUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by User on 17-11-2017.
 */

public class InfoPageAdapter extends RecyclerView.Adapter<InfoPageAdapter.RecentViewHolder> {

    private List<InfoPages.Datum> list = new ArrayList<>();
    private Activity activity;
    private OnItemClickListner onItemClickListner;
    private int width = 0, height = 0;

    public InfoPageAdapter(Activity activity, OnItemClickListner onItemClickListner) {
        this.activity = activity;
        this.onItemClickListner = onItemClickListner;
    }

    public void addAll(List<InfoPages.Datum> list) {
        this.list = list;
        getWidthAndHeight();
        notifyDataSetChanged();
    }

    @Override
    public InfoPageAdapter.RecentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_info_pages, parent, false);

        return new InfoPageAdapter.RecentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(InfoPageAdapter.RecentViewHolder holder, final int position) {
        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, InfoPageDetailActivity.class);
                intent.putExtra(RequestParamUtils.ID, list.get(position).pageId + "");
                intent.putExtra(RequestParamUtils.title, list.get(position).title + "");
                activity.startActivity(intent);
            }
        });
        holder.tvInfoPageTitle.setText(list.get(position).title);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class RecentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.llMain)
        LinearLayout llMain;

        @BindView(R.id.tvInfoPageTitle)
        TextViewLight tvInfoPageTitle;

        public RecentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void getWidthAndHeight() {
        int height_value = activity.getResources().getInteger(R.integer.height);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels / 2 - height_value * 2;
        height = width / 2 + height_value;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
