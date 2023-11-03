/*
 * Copyright (c) 2021.  Hurricane Development Studios
 */

package com.mwin.reward.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.mwin.reward.R;
import com.mwin.reward.activity.EverydayCheckinActivity;
import com.mwin.reward.async.models.EverydayBonusItem;

import java.util.List;

public class EverydayCheckinAdapter extends RecyclerView.Adapter<EverydayCheckinAdapter.SavedHolder> {

    public List<EverydayBonusItem> listDailyBonus;
    private Context context;
    private int lastClaimedDay, isLoginToday;
    private ClickListener clickListener;
    private boolean isClicked = false;
    private String bgColor;

    public EverydayCheckinAdapter(List<EverydayBonusItem> list, Context context, int lastClaimedDay, int isLoginToday, String bgColor, ClickListener clickListener) {
        this.listDailyBonus = list;
        this.context = context;
        this.lastClaimedDay = lastClaimedDay;
        this.isLoginToday = isLoginToday;
        this.clickListener = clickListener;
        this.bgColor = bgColor;
    }

    @NonNull
    @Override
    public SavedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_everyday_checkin, parent, false);
        return new SavedHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedHolder holder, final int position) {
        try {
            holder.tvDay.setText("Day " + listDailyBonus.get(position).getDay_id());
            holder.tvPoints.setText(listDailyBonus.get(position).getDay_points());
            if (isLoginToday != 1 && Integer.parseInt(listDailyBonus.get(position).getDay_id()) == lastClaimedDay + 1 && position > 0) {
                holder.layoutLock.setVisibility(View.GONE);
            } else if (Integer.parseInt(listDailyBonus.get(position).getDay_id()) > lastClaimedDay && position > 0) {
                holder.layoutLock.setVisibility(View.VISIBLE);
            } else {
                holder.layoutLock.setVisibility(View.GONE);
            }
            if ((Integer.parseInt(listDailyBonus.get(position).getDay_id()) == lastClaimedDay + 1 || (lastClaimedDay == 0 && position == 0)) && isLoginToday != 1 && !isClicked && context instanceof EverydayCheckinActivity) {
                holder.viewTouch.setVisibility(View.VISIBLE);
                holder.viewTouch.playAnimation();
            } else {
                holder.viewTouch.setVisibility(View.GONE);
                holder.viewTouch.clearAnimation();
            }
            if ((Integer.parseInt(listDailyBonus.get(position).getDay_id()) == lastClaimedDay + 1 || (lastClaimedDay == 0 && position == 0)) && isLoginToday != 1) {
                holder.lottieLight.setVisibility(View.VISIBLE);
                holder.lottieLight.playAnimation();
            } else {
                holder.lottieLight.setVisibility(View.GONE);
                holder.lottieLight.clearAnimation();
            }

            GradientDrawable drawable = (GradientDrawable) holder.layoutPoints.getBackground();
            drawable.mutate(); // only change this instance of the xml, not all components using this xml
            drawable.setStroke(context.getResources().getDimensionPixelSize(R.dimen.dim_1), Color.parseColor(bgColor)); // set stroke width and stroke color
            drawable.setColor(Color.parseColor(bgColor.replace("#","#1a")));
            holder.layoutPoints.setBackground(drawable);
            holder.ivDone.setVisibility(Integer.parseInt(listDailyBonus.get(position).getDay_id()) <= lastClaimedDay ? View.VISIBLE : View.GONE);
//            holder.ivCoin.setVisibility(Integer.parseInt(listDailyBonus.get(position).getDay_id()) <= lastClaimedDay ? View.GONE : View.VISIBLE);
//            holder.tvPoints.setVisibility(Integer.parseInt(listDailyBonus.get(position).getDay_id()) <= lastClaimedDay ? View.GONE : View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setLastClaimedData(int lastClaimedDay, int isLoginToday) {
        this.lastClaimedDay = lastClaimedDay;
        this.isLoginToday = isLoginToday;
        notifyDataSetChanged();
    }

    public void setClicked() {
        isClicked = true;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listDailyBonus.size();
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public class SavedHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout layoutLock;
        private TextView tvPoints, tvDay;
        private ImageView ivDone, ivCoin;
        private RelativeLayout layoutPoints;
        private LottieAnimationView viewTouch, lottieLight;

        public SavedHolder(@NonNull View itemView) {
            super(itemView);
            tvPoints = itemView.findViewById(R.id.tvPoints);
            tvDay = itemView.findViewById(R.id.tvDay);
            layoutPoints = itemView.findViewById(R.id.layoutPoints);
            layoutLock = itemView.findViewById(R.id.layoutLock);
            ivDone = itemView.findViewById(R.id.ivDone);
            viewTouch = itemView.findViewById(R.id.viewTouch);
            lottieLight = itemView.findViewById(R.id.lottieLight);
            ivCoin = itemView.findViewById(R.id.ivCoin);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(this.getLayoutPosition(), v);
        }
    }
}
