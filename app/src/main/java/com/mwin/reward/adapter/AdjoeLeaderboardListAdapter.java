/*
 * Copyright (c) 2021.  Hurricane Development Studios
 */

package com.mwin.reward.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mwin.reward.R;
import com.mwin.reward.async.models.AdjoeLeaderboardItem;
import com.mwin.reward.utils.CommonMethodsUtils;

import java.util.List;

public class AdjoeLeaderboardListAdapter extends RecyclerView.Adapter<AdjoeLeaderboardListAdapter.SavedHolder> {

    public List<AdjoeLeaderboardItem> listLeaderboard;
    private Context context;

    public AdjoeLeaderboardListAdapter(List<AdjoeLeaderboardItem> list, Context context) {
        this.listLeaderboard = list;
        this.context = context;
    }

    @NonNull
    @Override
    public SavedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_adjoe_leaderboard, parent, false);
        return new SavedHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedHolder holder, final int position) {
        try {
            if (!CommonMethodsUtils.isStringNullOrEmpty(listLeaderboard.get(position).getProfileImage())) {
                Glide.with(context)
                        .load(listLeaderboard.get(position).getProfileImage())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                holder.probr.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                                holder.probr.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(holder.ivIcon);
            } else {
                holder.ivIcon.setImageResource(0);
                holder.probr.setVisibility(View.GONE);
            }

            holder.tvNumber.setText("" + (position + 4));
            holder.tvPoints.setText(listLeaderboard.get(position).getPoints());
            holder.tvName.setText(listLeaderboard.get(position).getFirstName() + " " + listLeaderboard.get(position).getLastName());
            holder.sep.setVisibility(position == listLeaderboard.size() - 1 ? View.GONE : View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return listLeaderboard.size();
    }

    public class SavedHolder extends RecyclerView.ViewHolder {
        private ImageView ivIcon;
        private TextView tvName, tvPoints, tvNumber;
        private ProgressBar probr;
        private View sep;

        public SavedHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvNumber = itemView.findViewById(R.id.tvNumber);
            tvName = itemView.findViewById(R.id.tvName);
            tvPoints = itemView.findViewById(R.id.tvPoints);
            probr = itemView.findViewById(R.id.probr);
            sep = itemView.findViewById(R.id.sep);
        }
    }
}
