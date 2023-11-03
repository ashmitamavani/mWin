/*
 * Copyright (c) 2021.  Hurricane Development Studios
 */

package com.mwin.reward.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mwin.reward.R;
import com.mwin.reward.async.models.LuckyNumberGameItem;

import java.util.List;

public class LuckyNumberGameAdapter extends RecyclerView.Adapter<LuckyNumberGameAdapter.SavedHolder> {

    public List<LuckyNumberGameItem> listDailyBonus;
    private Context context;
    private ClickListener clickListener;

    public LuckyNumberGameAdapter(List<LuckyNumberGameItem> list, Context context, ClickListener clickListener) {
        this.listDailyBonus = list;
        this.context = context;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public SavedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_lucky_number_game, parent, false);
        return new SavedHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedHolder holder, final int position) {
        try {
            holder.tvNumber.setText("" + listDailyBonus.get(position).getNumber());
            holder.ivClose.setVisibility(listDailyBonus.get(position).getIsSelected() ? View.VISIBLE : View.INVISIBLE);
            holder.tvNumber.setTextColor(context.getColor(listDailyBonus.get(position).getIsSelected() ? R.color.white : R.color.black));
            holder.layoutContent.setBackgroundResource(listDailyBonus.get(position).getIsSelected() ? R.drawable.bg_lucky_number_game_selected : R.drawable.bg_lucky_number_game);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return listDailyBonus.size();
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public class SavedHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout layoutContent;
        private TextView tvNumber;
        private ImageView ivClose;

        public SavedHolder(@NonNull View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tvNumber);
            layoutContent = itemView.findViewById(R.id.layoutContent);
            ivClose = itemView.findViewById(R.id.ivClose);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(this.getLayoutPosition(), v);
        }
    }
}
