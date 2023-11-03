/*
 * Copyright (c) 2021.  Hurricane Development Studios
 */

package com.mwin.reward.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
import com.mwin.reward.async.models.HomeDataItem;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;

import java.util.List;

public class GiveawayGiftCodeListAdapter extends RecyclerView.Adapter<GiveawayGiftCodeListAdapter.SavedHolder> {
    public List<HomeDataItem> data;
    private MainResponseModel responseMain;
    private Context context;
    private ClickListener clickListener;
    private RequestOptions requestOptions = new RequestOptions();

    public GiveawayGiftCodeListAdapter(Context context, List<HomeDataItem> list, ClickListener clickListener) {
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
        View view = inflater.inflate(R.layout.item_giveaway__gift_code, parent, false);
        return new SavedHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedHolder holder, final int position) {
        try {
            if (position == 0 && CommonMethodsUtils.isShowAppLovinNativeAds()) {
                loadAppLovinNativeAds(holder.fl_adplaceholder);
            }
            if (!CommonMethodsUtils.isStringNullOrEmpty(data.get(position).getBtnName())) {
                holder.btnCompleteTask.setVisibility(View.VISIBLE);
                holder.btnCompleteTask.setText(data.get(position).getBtnName());
            } else {
                holder.btnCompleteTask.setVisibility(View.GONE);
            }
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
            holder.tvCouponCode.setText(data.get(position).getCouponCode());
            holder.tvName.setText(data.get(position).getTitle());
            holder.tvDescription.setText(data.get(position).getDescription());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
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
        void onItemClick(int position, View v);

        void onCopyButtonClicked(int position, View v);

        void onCompleteTaskButtonClicked(int position, View v);
    }

    public class SavedHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvName, tvDescription, tvCouponCode, btnCompleteTask;
        private ImageView ivIcon;
        private LottieAnimationView ivLottie;
        private ProgressBar probr;
        private Button btnCopy;
        private FrameLayout fl_adplaceholder;

        public SavedHolder(@NonNull View itemView) {
            super(itemView);
            btnCompleteTask = itemView.findViewById(R.id.btnCompleteTask);
            tvCouponCode = itemView.findViewById(R.id.tvCouponCode);
            fl_adplaceholder = itemView.findViewById(R.id.fl_adplaceholder);
            ivLottie = itemView.findViewById(R.id.ivLottie);
            tvName = itemView.findViewById(R.id.tvName);
            btnCopy = itemView.findViewById(R.id.btnCopy);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            probr = itemView.findViewById(R.id.probr);
            itemView.setOnClickListener(this);
            btnCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onCopyButtonClicked(getAdapterPosition(), view);
                }
            });
            btnCompleteTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onCompleteTaskButtonClicked(getAdapterPosition(), view);
                }
            });
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(this.getLayoutPosition(), v);
        }
    }
}
