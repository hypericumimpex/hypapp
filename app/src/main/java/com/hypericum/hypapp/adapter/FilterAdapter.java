package com.hypericum.hypapp.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.hypericum.hypapp.R;
import com.hypericum.hypapp.activity.FilterActivity;
import com.hypericum.hypapp.customview.textview.TextViewRegular;
import com.hypericum.hypapp.interfaces.OnItemClickListner;
import com.hypericum.hypapp.javaclasses.FilterSelectedList;
import com.hypericum.hypapp.utils.BaseActivity;
import com.hypericum.hypapp.utils.Constant;
import com.hypericum.hypapp.utils.RequestParamUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bhumi Shah on 11/7/2017.
 */

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterViewHolder> {

    private List<String> list = new ArrayList<>();
    private Activity activity;
    private OnItemClickListner onItemClickListner;
    private int width = 0, height = 0;
    private int outerPosition;


    public FilterAdapter(Activity activity, OnItemClickListner onItemClickListner) {
        this.activity = activity;

        this.onItemClickListner = onItemClickListner;
    }

    public void addAll(List<String> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void setOuterListPosition(int outerPosition) {
        this.outerPosition = outerPosition;

    }


    @Override
    public FilterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_filter, parent, false);

        return new FilterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FilterViewHolder holder, final int position) {
        holder.tvName.setText(list.get(position));

        if (FilterSelectedList.selectedOtherOptionList.get(outerPosition).options.size() > 0 && !FilterActivity.clearFilter) {
            if (FilterSelectedList.selectedOtherOptionList.get(outerPosition).options.contains(list.get(position))) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.ckSelect.getButtonDrawable().setColorFilter(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)), PorterDuff.Mode.SRC_IN);
                }
                holder.ckSelect.setChecked(true);

            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.ckSelect.getButtonDrawable().setColorFilter(activity.getColor(R.color.gray_light), PorterDuff.Mode.SRC_IN);
                } else {

                }
                holder.ckSelect.setChecked(false);
            }
        } else {
            holder.ckSelect.setChecked(false);
        }



        holder.ckSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        holder.ckSelect.getButtonDrawable().setColorFilter(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)), PorterDuff.Mode.SRC_IN);
                    }
                    onItemClickListner.onItemClick(position, RequestParamUtils.strtrue, outerPosition);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        holder.ckSelect.getButtonDrawable().setColorFilter(activity.getColor(R.color.gray_light), PorterDuff.Mode.SRC_IN);
                    }
                    onItemClickListner.onItemClick(position, RequestParamUtils.strfalse, outerPosition);
                }
            }
        });


    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public class FilterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.llMain)
        LinearLayout llMain;

        @BindView(R.id.tvName)
        TextViewRegular tvName;

        @BindView(R.id.ckSelect)
        CheckBox ckSelect;



        public FilterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }


    }

    public void getWidthAndHeight() {
        int height_value = activity.getResources().getInteger(R.integer.height);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels / 2 - 20;
        height = width - height_value;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


}