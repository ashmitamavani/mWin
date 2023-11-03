package com.mwin.reward.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mwin.reward.R;
import com.mwin.reward.async.models.TaskListDataItem;
import com.mwin.reward.utils.CommonMethodsUtils;

import java.util.List;

public class TopOffersListAdapter extends RecyclerView.Adapter<TopOffersListAdapter.ViewHolder> {
    public List<TaskListDataItem> listTask;
    private Context context;
    private ClickListener clickListener;

    public TopOffersListAdapter(Context context, List<TaskListDataItem> list, ClickListener fileListClickInterface) {
        this.context = context;
        this.listTask = list;
        this.clickListener = fileListClickInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_top_offers, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            if (listTask.get(position).getIcon() != null) {
                if (listTask.get(position).getIcon().contains(".json")) {
                    holder.ivIcon.setVisibility(View.GONE);
                    holder.ivLottie.setVisibility(View.VISIBLE);
                    CommonMethodsUtils.setLottieAnimation(holder.ivLottie, listTask.get(position).getIcon());
                    holder.ivLottie.setRepeatCount(LottieDrawable.INFINITE);
                    holder.probr.setVisibility(View.GONE);
                } else {
                    holder.ivIcon.setVisibility(View.VISIBLE);
                    holder.ivLottie.setVisibility(View.GONE);
                    Glide.with(context)
                            .load(listTask.get(position).getIcon())
                            .override(context.getResources().getDimensionPixelSize(R.dimen.dim_54), context.getResources().getDimensionPixelSize(R.dimen.dim_54))
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

            if (!CommonMethodsUtils.isStringNullOrEmpty(listTask.get(position).getLabel())) {
                holder.tvLabel.setText(listTask.get(position).getLabel());
                holder.tvLabel.setVisibility(View.VISIBLE);
            } else {
                holder.tvLabel.setVisibility(View.GONE);
            }
            if (listTask.get(position).getLabelTextColor() != null) {
                holder.tvLabel.setTextColor(Color.parseColor(listTask.get(position).getLabelTextColor()));
            } else {
                holder.tvLabel.setTextColor(context.getColor(R.color.white));
            }
            if (listTask.get(position).getLabelBgColor() != null && listTask.get(position).getLabelBgColor().length() > 0) {
                holder.tvLabel.getBackground().setTint(Color.parseColor(listTask.get(position).getLabelBgColor()));
            } else {
                holder.tvLabel.getBackground().setTint(context.getColor(R.color.orange));
            }

            if (listTask.get(position).getPoints().matches("0")) {
                holder.layoutPoints.setVisibility(View.GONE);
            } else {
                holder.layoutPoints.setVisibility(View.VISIBLE);
                if (listTask.get(position).getPoints() != null) {
                    holder.tvPoints.setText(listTask.get(position).getPoints());
                }
                if (listTask.get(position).getBtnColor() != null && listTask.get(position).getBtnColor().length() > 0) {
                    Drawable mDrawable = ContextCompat.getDrawable(context, R.drawable.ic_btn_rounded_corner_pressed);
                    mDrawable.setColorFilter(new PorterDuffColorFilter(Color.parseColor(listTask.get(position).getBtnColor()), PorterDuff.Mode.SRC_IN));
                    holder.layoutPoints.setBackground(mDrawable);
                }

                if (listTask.get(position).getBtnTextColor() != null && listTask.get(position).getBtnTextColor().length() > 0) {
                    holder.tvPoints.setTextColor(Color.parseColor(listTask.get(position).getBtnTextColor()));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return listTask == null ? 0 : listTask.size();
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvPoints, tvLabel;
        private ImageView ivIcon;
        private FrameLayout layoutMain;
        private LottieAnimationView ivLottie;
        private ProgressBar probr;
        private LinearLayout layoutPoints;

        public ViewHolder(View view) {
            super(view);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            ivLottie = itemView.findViewById(R.id.ivLottie);
            tvLabel = itemView.findViewById(R.id.tvLabel);
            tvPoints = itemView.findViewById(R.id.tvPoints);
            probr = itemView.findViewById(R.id.probr);
            layoutPoints = itemView.findViewById(R.id.layoutPoints);
            layoutMain = itemView.findViewById(R.id.layoutMain);
            layoutMain.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(this.getLayoutPosition(), v);
        }
    }
}
