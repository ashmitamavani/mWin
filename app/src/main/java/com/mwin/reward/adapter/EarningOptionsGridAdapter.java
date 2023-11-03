/*
 * Copyright (c) 2021.  Hurricane Development Studios
 */

package com.mwin.reward.adapter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mwin.reward.R;
import com.mwin.reward.async.models.HomeDataListItem;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.ArrowPositionRules;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
import com.skydoves.balloon.BalloonSizeSpec;
import com.skydoves.balloon.OnBalloonClickListener;

import java.util.List;

public class EarningOptionsGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public List<HomeDataListItem> itemList;
    private AppCompatActivity context;
    private ClickListener clickListener;
    private Typeface typefaceMedium;
    private Animation blinkAnimation;
    private Balloon balloon;

    public EarningOptionsGridAdapter(AppCompatActivity context, Animation animation, List<HomeDataListItem> list, ClickListener clickListener) {
        this.itemList = list;
        this.context = context;
        this.clickListener = clickListener;
        typefaceMedium = ResourcesCompat.getFont(context, R.font.noto_medium);
        blinkAnimation = animation;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_earning_options, parent, false);
        return new SavedHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder parentHolder, final int position) {
        try {
            SavedHolder holder = (SavedHolder) parentHolder;
            if (!CommonMethodsUtils.isStringNullOrEmpty(itemList.get(position).getFullImage())) {
                holder.cardFullImage.setVisibility(View.VISIBLE);
                holder.cardContent.setVisibility(View.GONE);
                if (itemList.get(position).getFullImage().contains(".json")) {
                    holder.ivIconFullImage.setVisibility(View.GONE);
                    holder.ivLottieFullImage.setVisibility(View.VISIBLE);
                    CommonMethodsUtils.setLottieAnimation(holder.ivLottieFullImage, itemList.get(position).getFullImage());
                    holder.ivLottieFullImage.setRepeatCount(LottieDrawable.INFINITE);
                    holder.progressBarFullImage.setVisibility(View.GONE);
                } else {
                    holder.ivIconFullImage.setVisibility(View.VISIBLE);
                    holder.ivLottieFullImage.setVisibility(View.GONE);
                    Glide.with(context)
                            .load(itemList.get(position).getFullImage())
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                                    holder.progressBarFullImage.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .into(holder.ivIconFullImage);
                }
            } else {
                holder.cardFullImage.setVisibility(View.GONE);
                holder.cardContent.setVisibility(View.VISIBLE);
                if (!CommonMethodsUtils.isStringNullOrEmpty(itemList.get(position).getIcon())) {
                    if (itemList.get(position).getIcon().endsWith(".json")) {
                        holder.ivIcon.setVisibility(View.INVISIBLE);
                        holder.ivLottie.setVisibility(View.VISIBLE);
                        CommonMethodsUtils.setLottieAnimation(holder.ivLottie, itemList.get(position).getIcon());
                        holder.ivLottie.setRepeatCount(LottieDrawable.INFINITE);
                        holder.progressBar.setVisibility(View.GONE);
                    } else {
                        holder.ivIcon.setVisibility(View.VISIBLE);
                        holder.ivLottie.setVisibility(View.INVISIBLE);
                        Glide.with(context)
                                .asBitmap()
                                .load(itemList.get(position).getIcon())
                                .override((int) context.getResources().getDimensionPixelSize(R.dimen.dim_56))
                                .listener(new RequestListener<Bitmap>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                        holder.progressBar.setVisibility(View.GONE);
                                        return false;
                                    }
                                })
                                .into(holder.ivIcon);
                    }
                }

                holder.lblTitle.setText(itemList.get(position).getTitle());
                holder.lblSubTitle.setText(itemList.get(position).getSubTitle());
                holder.tvNote.setText(itemList.get(position).getNote());

                if (!CommonMethodsUtils.isStringNullOrEmpty(itemList.get(position).getLabel())) {
                    holder.tvLabel.setText(itemList.get(position).getLabel());
                    holder.tvLabel.setVisibility(View.VISIBLE);
                    holder.tvLabel.startAnimation(blinkAnimation);
                } else {
                    holder.tvLabel.setVisibility(View.INVISIBLE);
                    holder.tvLabel.clearAnimation();
                }

                holder.cardContent.setCardBackgroundColor(Color.parseColor(itemList.get(position).getIconBGColor()));

                if (!CommonMethodsUtils.isStringNullOrEmpty(itemList.get(position).getBgColor())) {
                    Drawable mDrawable = ContextCompat.getDrawable(context, R.drawable.rectangle_white);
                    mDrawable.setColorFilter(new PorterDuffColorFilter(Color.parseColor(itemList.get(position).getBgColor()), PorterDuff.Mode.SRC_IN));
                    holder.layoutContent.setBackground(mDrawable);
                }
                if (!CommonMethodsUtils.isStringNullOrEmpty(itemList.get(position).getIsTodayTaskCompleted()) && itemList.get(position).getIsTodayTaskCompleted().equals("0")) {
                    holder.layoutLock.setVisibility(View.VISIBLE);
                    holder.layoutLock.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                balloon = new Balloon.Builder(context)
                                        .setArrowSize(10)
                                        .setArrowOrientation(ArrowOrientation.TOP)
                                        .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
                                        .setArrowPosition(0.5f)
                                        .setWidth(BalloonSizeSpec.WRAP)
                                        .setHeight(65)
                                        .setTextSize(15f)
                                        .setPaddingHorizontal(10)
                                        .setCornerRadius(4f)
                                        .setAlpha(0.9f)
                                        .setTextTypeface(typefaceMedium)
                                        .setText("Complete <font color='#FFCC66'>" + itemList.get(position).getTaskCount() + " Tasks </font> to <font color='#FFCC66'>UNLOCK</font> " + itemList.get(position).getTitle())
                                        .setTextColor(ContextCompat.getColor(context, R.color.white))
                                        .setTextIsHtml(true)
                                        .setBackgroundColor(ContextCompat.getColor(context, R.color.black_transparent))
                                        .setOnBalloonClickListener(new OnBalloonClickListener() {
                                            @Override
                                            public void onBalloonClick(@NonNull View view) {
                                                balloon.dismiss();
                                            }
                                        })
                                        .setBalloonAnimation(BalloonAnimation.FADE)
                                        .setLifecycleOwner(context)
                                        .build();
                                balloon.showAlignBottom(view);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    holder.layoutLock.setVisibility(View.GONE);
                }

                holder.layoutViewDetails.setBackgroundColor(Color.parseColor(itemList.get(position).getIconBGColor()));
                if (!CommonMethodsUtils.isStringNullOrEmpty(itemList.get(position).getIconBGColor())) {
                    Drawable mDrawable = ContextCompat.getDrawable(context, R.drawable.rectangle_white);
                    mDrawable.setColorFilter(new PorterDuffColorFilter(Color.parseColor(itemList.get(position).getIconBGColor()), PorterDuff.Mode.SRC_IN));
                    holder.layoutIcon.setBackground(mDrawable);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public class SavedHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView lblTitle, lblSubTitle, tvNote, tvLabel;
        private ImageView ivIcon, ivIconFullImage;
        private LottieAnimationView ivLottie, ivLottieFullImage;
        private ProgressBar progressBar, progressBarFullImage;
        private CardView cardContent, cardFullImage;
        private LinearLayout layoutViewDetails, layoutContent, layoutLock;
        private RelativeLayout layoutIcon;

        public SavedHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            ivLottie = itemView.findViewById(R.id.ivLottie);
            layoutViewDetails = itemView.findViewById(R.id.layoutViewDetails);
            lblTitle = itemView.findViewById(R.id.lblTitle);
            lblSubTitle = itemView.findViewById(R.id.lblSubTitle);
            tvNote = itemView.findViewById(R.id.tvNote);
            layoutLock = itemView.findViewById(R.id.layoutLock);
            tvLabel = itemView.findViewById(R.id.tvLabel);
            cardContent = itemView.findViewById(R.id.cardContent);
            layoutContent = itemView.findViewById(R.id.layoutContent);
            layoutIcon = itemView.findViewById(R.id.layoutIcon);
            ivIconFullImage = itemView.findViewById(R.id.ivIconFullImage);
            ivLottieFullImage = itemView.findViewById(R.id.ivLottieFullImage);
            progressBarFullImage = itemView.findViewById(R.id.progressBarFullImage);
            cardFullImage = itemView.findViewById(R.id.cardFullImage);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(this.getLayoutPosition(), v);
        }
    }

}