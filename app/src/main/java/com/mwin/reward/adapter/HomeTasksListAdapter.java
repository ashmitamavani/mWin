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

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.async.models.HomeDataItem;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;

import java.util.List;

public class HomeTasksListAdapter extends RecyclerView.Adapter<HomeTasksListAdapter.SavedHolder> {
    public List<HomeDataItem> data;
    private MainResponseModel responseMain;
    private Context context;
    private ClickListener clickListener;
    private RequestOptions requestOptions = new RequestOptions();

    public HomeTasksListAdapter(List<HomeDataItem> list, Context context, ClickListener clickListener) {
        this.data = list;
        this.context = context;
        this.clickListener = clickListener;
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(context.getResources().getDimensionPixelSize(R.dimen.dim_5)));
        responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
    }

    @NonNull
    @Override
    public SavedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_home_tasks_list, parent, false);
        return new SavedHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedHolder holder, final int position) {
        try {
            String image = null;
            if (!CommonMethodsUtils.isStringNullOrEmpty(data.get(position).getJsonImage())) {
                image = data.get(position).getJsonImage();
            } else if (!CommonMethodsUtils.isStringNullOrEmpty(data.get(position).getIcon())) {
                image = data.get(position).getIcon();
            }
            if (image != null) {
                if (image.contains(".json")) {
                    holder.ivIcon.setVisibility(View.GONE);
                    holder.ivLottie.setVisibility(View.VISIBLE);
                    CommonMethodsUtils.setLottieAnimation(holder.ivLottie, image);
                    holder.ivLottie.setRepeatCount(LottieDrawable.INFINITE);
                    holder.probr.setVisibility(View.GONE);
                } else {
                    holder.ivIcon.setVisibility(View.VISIBLE);
                    holder.ivLottie.setVisibility(View.GONE);
                    Glide.with(context)
                            .load(image)
                            .override(context.getResources().getDimensionPixelSize(R.dimen.dim_56), context.getResources().getDimensionPixelSize(R.dimen.dim_56))
                            .addListener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    holder.probr.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .into(holder.ivIcon);
                }
            }

            holder.tvName.setText(data.get(position).getTitle());
            holder.tvDescription.setText(data.get(position).getDescription());
            holder.tvPoints.setText("Get " + data.get(position).getPoints());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public class SavedHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvName, tvDescription, tvPoints;
        private ImageView ivIcon;
        private LottieAnimationView ivLottie;
        private ProgressBar probr;

        public SavedHolder(@NonNull View itemView) {
            super(itemView);
            ivLottie = itemView.findViewById(R.id.ivLottie);
            tvName = itemView.findViewById(R.id.tvName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvPoints = itemView.findViewById(R.id.tvPoints);
            probr = itemView.findViewById(R.id.probr);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(this.getLayoutPosition(), v);
        }
    }

}
