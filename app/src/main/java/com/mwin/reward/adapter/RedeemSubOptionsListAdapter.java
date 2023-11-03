/*
 * Copyright (c) 2021.  Hurricane Development Studios
 */

package com.mwin.reward.adapter;

import android.content.Context;
import android.graphics.Bitmap;
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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.async.models.WithdrawList;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;

import java.util.List;

public class RedeemSubOptionsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public List<WithdrawList> listWithdrawTypes;
    public static final int ITEM_AD = 2;
    public static final int ITEM_DATA = 0;
    private Context context;
    private ClickListener clickListener;
    private MainResponseModel responseMain;

    public RedeemSubOptionsListAdapter(List<WithdrawList> list, Context context, ClickListener clickListener) {
        this.listWithdrawTypes = list;
        this.context = context;
        this.clickListener = clickListener;
        responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
    }

    @Override
    public int getItemViewType(int i) {
        if (this.listWithdrawTypes.get(i).getType() == null) {
            return ITEM_AD;
        }
        return ITEM_DATA;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_DATA) {
            return new RedeemSubOptionsListAdapter.SavedHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_redeem_sub_options_list, parent, false));
        }
        if (viewType == ITEM_AD) {
            return new RedeemSubOptionsListAdapter.AdHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inflate_native_ad, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder parentHolder, final int position) {
        try {
            int itemViewType = getItemViewType(position);
            if (itemViewType == ITEM_DATA) {
                RedeemSubOptionsListAdapter.SavedHolder holder = (RedeemSubOptionsListAdapter.SavedHolder) parentHolder;
                if (!CommonMethodsUtils.isStringNullOrEmpty(listWithdrawTypes.get(position).getIcon())) {
                    if (listWithdrawTypes.get(position).getIcon().endsWith(".json")) {
                        holder.ivBanner.setVisibility(View.GONE);
                        holder.ivLottieView.setVisibility(View.VISIBLE);
                        CommonMethodsUtils.setLottieAnimation(holder.ivLottieView, listWithdrawTypes.get(position).getIcon());
                        holder.ivLottieView.setRepeatCount(LottieDrawable.INFINITE);
                        holder.progressBar.setVisibility(View.GONE);
                    } else {
                        holder.ivBanner.setVisibility(View.VISIBLE);
                        holder.ivLottieView.setVisibility(View.GONE);
                        Glide.with(context)
                                .asBitmap()
                                .load(listWithdrawTypes.get(position).getIcon())
                                .override((int) holder.ivBanner.getWidth(), (int) holder.ivBanner.getHeight())
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
                                .into(holder.ivBanner);
                    }
                }

                holder.tvPoints.setText("Pay: " + listWithdrawTypes.get(position).getMinPoint());
                holder.tvAmount.setText(listWithdrawTypes.get(position).getAmount());
            } else {
                ((RedeemSubOptionsListAdapter.AdHolder) parentHolder).adLayoutLovin.setVisibility(View.VISIBLE);
                ((RedeemSubOptionsListAdapter.AdHolder) parentHolder).cardNative.setVisibility(View.VISIBLE);
                loadAppLovinNativeAds(((RedeemSubOptionsListAdapter.AdHolder) parentHolder).adLayoutLovin, ((RedeemSubOptionsListAdapter.AdHolder) parentHolder).cardNative);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return listWithdrawTypes.size();
    }


    private void loadAppLovinNativeAds(FrameLayout adLayout, CardView cardView) {
        try {
            MaxNativeAdLoader nativeAdLoader = new MaxNativeAdLoader(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinNativeID()), context);
            nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
                @Override
                public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad) {
                    adLayout.removeAllViews();
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) adLayout.getLayoutParams();
                    params.height = context.getResources().getDimensionPixelSize(R.dimen.dim_300);
                    params.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    adLayout.setLayoutParams(params);
                    adLayout.setPadding((int) context.getResources().getDimension(R.dimen.dim_10), (int) context.getResources().getDimension(R.dimen.dim_10), (int) context.getResources().getDimension(R.dimen.dim_10), (int) context.getResources().getDimension(R.dimen.dim_10));
                    adLayout.addView(nativeAdView);
                    adLayout.setVisibility(View.VISIBLE);
                    cardView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
                    cardView.setVisibility(View.GONE);
                }

                @Override
                public void onNativeAdClicked(final MaxAd ad) {

                }
            });
            nativeAdLoader.loadAd();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public class AdHolder extends RecyclerView.ViewHolder {
        FrameLayout adLayoutLovin;
        private CardView cardNative;


        public AdHolder(View view) {
            super(view);
            adLayoutLovin = view.findViewById(R.id.adLayoutLovinMain);
            cardNative = view.findViewById(R.id.cardNativeMain);
        }
    }

    public class SavedHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvPoints, tvAmount;
        private ImageView ivBanner;
        private LottieAnimationView ivLottieView;
        private ProgressBar progressBar;
        private FrameLayout fl_adplaceholder;

        public SavedHolder(@NonNull View itemView) {
            super(itemView);

            fl_adplaceholder = itemView.findViewById(R.id.fl_adplaceholder);
            tvPoints = itemView.findViewById(R.id.tvPoints);
            tvAmount = itemView.findViewById(R.id.tvAmount);

            progressBar = itemView.findViewById(R.id.progressBar);
            ivBanner = itemView.findViewById(R.id.ivBanner);
            ivLottieView = itemView.findViewById(R.id.ivLottieView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(this.getLayoutPosition(), v);
        }
    }
}