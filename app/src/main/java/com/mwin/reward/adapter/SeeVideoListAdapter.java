/*
 * Copyright (c) 2021.  Hurricane Development Studios
 */

package com.mwin.reward.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.async.models.SeeVideoList;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;

import java.util.List;

public class SeeVideoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int ITEM_AD = 2;
    public static final int ITEM_DATA = 0;
    public List<SeeVideoList> listVideos;
    private MainResponseModel responseMain;
    private Context context;
    private ClickListener clickListener;
    private int lastWatchedVideoId;

    public SeeVideoListAdapter(List<SeeVideoList> list, Context context, int lastWatchedVideoId, ClickListener clickListener) {
        this.listVideos = list;
        this.context = context;
        this.clickListener = clickListener;
        this.lastWatchedVideoId = lastWatchedVideoId;
        responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
    }

    public void updateLastWatchedVideo(int lastWatchedVideoId) {
        this.lastWatchedVideoId = lastWatchedVideoId;
    }

    @Override
    public int getItemViewType(int i) {
        if (this.listVideos.get(i).getVideoId() == null) {
            return ITEM_AD;
        }
        return ITEM_DATA;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_DATA) {
            return new SavedHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_see_video_list, parent, false));
        }
        if (viewType == ITEM_AD) {
            return new AdHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inflate_native_ad, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder parentHolder, final int position) {
        try {
            int itemViewType = getItemViewType(position);
            if (itemViewType == ITEM_DATA) {
                SavedHolder holder = (SavedHolder) parentHolder;
                if (!CommonMethodsUtils.isStringNullOrEmpty(listVideos.get(position).getWatchedVideoPoints())) {
                    holder.txtTitle.setText(listVideos.get(position).getWatchedVideoPoints() + " Points Added");
                    holder.txtDescription.setText("Watched!");
                    Drawable mDrawable = ContextCompat.getDrawable(context, R.drawable.rectangle_white);
                    mDrawable.setColorFilter(new PorterDuffColorFilter(Color.parseColor("#ceebda"), PorterDuff.Mode.SRC_IN));
                    holder.layoutContent.setBackground(mDrawable);
                } else {
                    holder.txtTitle.setText(listVideos.get(position).getTitle());
                    holder.txtDescription.setText(listVideos.get(position).getDesc());
                }
                if (Integer.parseInt(listVideos.get(position).getVideoId()) <= lastWatchedVideoId) {
                    holder.tvButton.setText("Done");
                    holder.tvButton.setTextColor(context.getColor(R.color.green));
                    holder.ivDone.setVisibility(View.VISIBLE);
                    holder.ivLock.setVisibility(View.GONE);
                    holder.layoutButton.setBackground(null);
                    holder.ivIcon.setImageResource(R.drawable.ic_points_coin);
                    holder.viewShine.clearAnimation();
                    holder.viewShine.setVisibility(View.GONE);
                } else if (Integer.parseInt(listVideos.get(position).getVideoId()) == (lastWatchedVideoId + 1)) {
                    holder.ivIcon.setImageResource(R.drawable.ic_gift);
                    if (listVideos.get(position).getButtonText() != null && !listVideos.get(position).getButtonText().equals("Watch Now")) {
                        holder.tvButton.setText(listVideos.get(position).getButtonText());
                        holder.tvButton.setTextColor(context.getColor(R.color.red));
                        holder.layoutButton.setBackgroundResource(R.drawable.ic_btn_grey_rounded_corner);
                        holder.viewShine.clearAnimation();
                        holder.viewShine.setVisibility(View.GONE);
                    } else {
                        holder.tvButton.setText("Watch Now");
                        holder.tvButton.setTextColor(context.getColor(R.color.white));
                        holder.layoutButton.setBackgroundResource(R.drawable.selector_btn);
                        holder.viewShine.setVisibility(View.VISIBLE);
                        Animation animUpDown = AnimationUtils.loadAnimation(context, R.anim.left_to_right);
                        animUpDown.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                holder.viewShine.startAnimation(animUpDown);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        // start the animation
                        holder.viewShine.startAnimation(animUpDown);
                    }
                    holder.ivDone.setVisibility(View.GONE);
                    holder.ivLock.setVisibility(View.GONE);
                } else {
                    holder.viewShine.clearAnimation();
                    holder.viewShine.setVisibility(View.GONE);
                    holder.ivIcon.setImageResource(R.drawable.ic_gift);
                    holder.tvButton.setText("Watch");
                    holder.tvButton.setTextColor(context.getColor(R.color.black));
                    holder.ivDone.setVisibility(View.GONE);
                    holder.ivLock.setVisibility(View.VISIBLE);
                    holder.layoutButton.setBackgroundResource(R.drawable.ic_btn_grey_rounded_corner);
                }
            } else {
                ((AdHolder) parentHolder).adLayoutLovin.setVisibility(View.VISIBLE);
                ((AdHolder) parentHolder).cardNative.setVisibility(View.VISIBLE);
                loadAppLovinNativeAds(((AdHolder) parentHolder).adLayoutLovin, ((AdHolder) parentHolder).cardNative);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return listVideos.size();
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
        private ImageView ivIcon;
        private TextView txtTitle, txtDescription, tvButton;
        private LinearLayout layoutButton, layoutContent;
        private FrameLayout fl_adplaceholder;
        private ImageView ivDone, ivLock;
        private View viewShine;

        public SavedHolder(@NonNull View itemView) {
            super(itemView);
            fl_adplaceholder = itemView.findViewById(R.id.fl_adplaceholder);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            ivLock = itemView.findViewById(R.id.ivLock);
            tvButton = itemView.findViewById(R.id.tvButton);
            layoutButton = itemView.findViewById(R.id.layoutButton);
            ivDone = itemView.findViewById(R.id.ivDone);
            viewShine = itemView.findViewById(R.id.viewShine);
            layoutContent = itemView.findViewById(R.id.layoutContent);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(this.getLayoutPosition(), v);
        }
    }
}
