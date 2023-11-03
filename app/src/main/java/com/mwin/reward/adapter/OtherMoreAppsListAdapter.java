/*
 * Copyright (c) 2021.  Hurricane Development Studios
 */

package com.mwin.reward.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.async.models.AppListItem;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;

import java.util.List;

public class OtherMoreAppsListAdapter extends RecyclerView.Adapter<OtherMoreAppsListAdapter.SavedHolder> {
    public List<AppListItem> listMoreApps;
    private MainResponseModel responseMain;
    private Context context;
    private ClickListener clickListener;
    private RequestOptions requestOptions = new RequestOptions();

    public OtherMoreAppsListAdapter(List<AppListItem> list, Context context, ClickListener clickListener) {
        this.listMoreApps = list;
        this.context = context;
        this.clickListener = clickListener;
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(context.getResources().getDimensionPixelSize(R.dimen.dim_5)));
        responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
    }

    @NonNull
    @Override
    public SavedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_other_more_apps, parent, false);
        return new SavedHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedHolder holder, final int position) {
        try {
            if ((position + 1) % 5 == 0 && CommonMethodsUtils.isShowAppLovinNativeAds()) {
                loadAppLovinNativeAds(holder.fl_adplaceholder);
            }
            if (!CommonMethodsUtils.isStringNullOrEmpty(listMoreApps.get(position).getBgImage())) {
                holder.probr.setVisibility(View.VISIBLE);
                if (listMoreApps.get(position).getBgImage().contains(".json")) {
                    holder.ivBackground.setVisibility(View.GONE);
                    holder.ivLottieViewBG.setVisibility(View.VISIBLE);
                    CommonMethodsUtils.setLottieAnimation(holder.ivLottieViewBG, listMoreApps.get(position).getBgImage());
                    holder.ivLottieViewBG.setRepeatCount(LottieDrawable.INFINITE);
                    holder.probr.setVisibility(View.GONE);
                } else {
                    Glide.with(context)
                            .asBitmap()
                            .load(listMoreApps.get(position).getBgImage())
                            .override((int) holder.ivBackground.getWidth(), (int) holder.ivBackground.getHeight())
                            .apply(requestOptions)
                            .listener(new RequestListener<Bitmap>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                    holder.probr.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .into(holder.ivBackground);
                }
            }
            if (!CommonMethodsUtils.isStringNullOrEmpty(listMoreApps.get(position).getIcone())) {
                Glide.with(context)
                        .asBitmap()
                        .load(listMoreApps.get(position).getIcone())
                        .override((int) holder.ivIcon.getWidth(), (int) holder.ivIcon.getHeight())
                        .apply(requestOptions)
                        .into(holder.ivIcon);
            }
            holder.tvAppName.setText(listMoreApps.get(position).getTitle());
            holder.btnAction.setText(listMoreApps.get(position).getBtnName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return listMoreApps.size();
    }

    private void loadAppLovinNativeAds(FrameLayout adLayout) {
        try {
            MaxNativeAdLoader nativeAdLoader = new MaxNativeAdLoader(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinNativeID()), context);
            nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
                @Override
                public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad) {
                    adLayout.removeAllViews();
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) adLayout.getLayoutParams();
                    params.height = context.getResources().getDimensionPixelSize(R.dimen.dim_300);
                    params.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    adLayout.setLayoutParams(params);
                    adLayout.setPadding((int) context.getResources().getDimension(R.dimen.dim_10), (int) context.getResources().getDimension(R.dimen.dim_10), (int) context.getResources().getDimension(R.dimen.dim_10), (int) context.getResources().getDimension(R.dimen.dim_10));
                    adLayout.addView(nativeAdView);
                    adLayout.setVisibility(View.VISIBLE);
                }

                @Override
                public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
                    adLayout.setVisibility(View.GONE);
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
        void onButtonClick(int position, View v);
    }

    public class SavedHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvAppName;
        private ImageView ivIcon, ivBackground;
        private Button btnAction;
        private FrameLayout fl_adplaceholder;
        private ProgressBar probr;
        private LottieAnimationView ivLottieViewBG;

        public SavedHolder(@NonNull View itemView) {
            super(itemView);
            fl_adplaceholder = itemView.findViewById(R.id.fl_adplaceholder);
            tvAppName = itemView.findViewById(R.id.tvAppName);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            ivBackground = itemView.findViewById(R.id.ivBackground);
            btnAction = itemView.findViewById(R.id.btnAction);
            probr = itemView.findViewById(R.id.probr);
            ivLottieViewBG = itemView.findViewById(R.id.ivLottieViewBG);
            btnAction.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onButtonClick(this.getLayoutPosition(), v);
        }
    }
}
