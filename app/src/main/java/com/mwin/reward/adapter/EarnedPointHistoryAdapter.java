/*
 * Copyright (c) 2021.  Hurricane Development Studios
 */

package com.mwin.reward.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.async.models.WalletListItem;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;
import com.mwin.reward.value.Constants;

import java.util.List;

public class EarnedPointHistoryAdapter extends RecyclerView.Adapter<EarnedPointHistoryAdapter.SavedHolder> {
    public List<WalletListItem> listPointHistory;
    private MainResponseModel responseMain;
    private Context context;
    private RequestOptions requestOptions = new RequestOptions();
    private String type;

    public EarnedPointHistoryAdapter(List<WalletListItem> list, Context context, String type) {
        this.listPointHistory = list;
        this.context = context;
        this.type = type;
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(context.getResources().getDimensionPixelSize(R.dimen.dim_5)));
        responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
    }

    @NonNull
    @Override
    public SavedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_earned_points_history, parent, false);
        return new SavedHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedHolder holder, final int position) {
        try {
            if ((position + 1) % 5 == 0 && CommonMethodsUtils.isShowAppLovinNativeAds()) {
                loadAppLovinNativeAds(holder.fl_adplaceholder);
            }
            String image;
            if (type.equals(Constants.HistoryType.WATCH_WEBSITE)) {
                image = listPointHistory.get(position).getIcon();
            } else {
                image = type.equals(Constants.HistoryType.TASK) ? listPointHistory.get(position).getIcone() : listPointHistory.get(position).getImage();
            }
            if (image != null) {
                if (image.contains(".json")) {
                    holder.ivIcon.setVisibility(View.GONE);
                    holder.ivLottieView.setVisibility(View.VISIBLE);
                    CommonMethodsUtils.setLottieAnimation(holder.ivLottieView, image);
                    holder.ivLottieView.setRepeatCount(LottieDrawable.INFINITE);
                    holder.probr.setVisibility(View.GONE);
                } else {
                    holder.ivIcon.setVisibility(View.VISIBLE);
                    holder.ivLottieView.setVisibility(View.GONE);
                    Glide.with(context)
                            .load(image)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
//                                     holder.ivIcon.setBackground(context.getResources().getDrawable(R.drawable.rectangle_white));
                                    holder.probr.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .into(holder.ivIcon);
                }
            }

            if (type.equals(Constants.HistoryType.TASK) && listPointHistory.get(position).getCampaignName() != null) {
                holder.txtTitle.setText(listPointHistory.get(position).getCampaignName());
            } else if (listPointHistory.get(position).getTitle() != null) {
                holder.txtTitle.setText(listPointHistory.get(position).getTitle());
            }
            if (!type.equals(Constants.HistoryType.SCRATCH_CARD) &&
                    !type.equals(Constants.HistoryType.DAILY_REWARD) &&
                    !type.equals(Constants.HistoryType.GIVE_AWAY) &&
                    !type.equals(Constants.HistoryType.TASK) && listPointHistory.get(position).getSettleAmount() != null) {
                holder.txtSettleAmt.setText(listPointHistory.get(position).getSettleAmount() + " Points");
                holder.layoutWalletBalance.setVisibility(View.VISIBLE);
            } else {
                holder.layoutWalletBalance.setVisibility(View.GONE);
            }

            if (type.equals(Constants.HistoryType.SCRATCH_CARD) || type.equals(Constants.HistoryType.GIVE_AWAY)) {
                holder.layoutSubTitle.setVisibility(View.VISIBLE);
                holder.lblSubTitle.setText(type.equals(Constants.HistoryType.SCRATCH_CARD) ? "Task Name:  " : "Code:  ");
                holder.txtSubTitle.setText(type.equals(Constants.HistoryType.SCRATCH_CARD) ? listPointHistory.get(position).getTaskTitle() : listPointHistory.get(position).getCouponCode());
            } else {
                holder.layoutSubTitle.setVisibility(View.GONE);
            }

            if (listPointHistory.get(position).getType() != null) {
                if (listPointHistory.get(position).getType().matches("1")) {
                    holder.txtPoint.setTextColor(context.getColor(R.color.green));
                } else {
                    holder.txtPoint.setTextColor(context.getColor(R.color.red));
                }
            }
            if (listPointHistory.get(position).getEntryDate() != null) {
                if (type.equals(Constants.HistoryType.SCRATCH_CARD)) {
                    holder.txtDate.setText(CommonMethodsUtils.modifyDateLayout(listPointHistory.get(position).getScratchedDate()));
                } else if (type.equals(Constants.HistoryType.TYPE_TEXT_TYPING)) {
                    holder.txtDate.setText(CommonMethodsUtils.modifyDateLayout(listPointHistory.get(position).getUpdateDate()));
                } else {
                    holder.txtDate.setText(CommonMethodsUtils.modifyDateLayout(listPointHistory.get(position).getEntryDate()));
                }
            }

            if (type.equals(Constants.HistoryType.SCRATCH_CARD) && listPointHistory.get(position).getScratchCardPoints() != null) {
                holder.txtPoint.setText("+" + listPointHistory.get(position).getScratchCardPoints());
                holder.lblPoints.setText(Integer.parseInt(listPointHistory.get(position).getScratchCardPoints()) > 1 ? "Points" : "Point");
            } else if (type.equals(Constants.HistoryType.TYPE_TEXT_TYPING) && listPointHistory.get(position).getWinningPoints() != null) {
                holder.txtPoint.setText("+" + listPointHistory.get(position).getWinningPoints());
                holder.lblPoints.setText(Integer.parseInt(listPointHistory.get(position).getWinningPoints()) > 1 ? "Points" : "Point");
            } else if (listPointHistory.get(position).getPoints() != null) {
                if (listPointHistory.get(position).getType() != null) {
                    if (listPointHistory.get(position).getType().matches("1")) {
                        holder.txtPoint.setText("+" + listPointHistory.get(position).getPoints());
                    } else {
                        holder.txtPoint.setText("-" + listPointHistory.get(position).getPoints());
                    }
                } else {
                    holder.txtPoint.setText("+" + listPointHistory.get(position).getPoints());
                }
                holder.lblPoints.setText(Integer.parseInt(listPointHistory.get(position).getPoints()) > 1 ? "Points" : "Point");
            } else {
                holder.txtPoint.setText("0");
                holder.lblPoints.setText("Point");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return listPointHistory.size();
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

    public class SavedHolder extends RecyclerView.ViewHolder {
        private ImageView ivIcon;
        private LottieAnimationView ivLottieView;
        private TextView txtTitle, txtPoint, txtSettleAmt, txtDate, lblPoints, lblSubTitle, txtSubTitle;
        private ProgressBar probr;
        private FrameLayout fl_adplaceholder;
        private RelativeLayout layoutWalletBalance;
        private LinearLayout layoutSubTitle;

        public SavedHolder(@NonNull View itemView) {
            super(itemView);
            fl_adplaceholder = itemView.findViewById(R.id.fl_adplaceholder);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            ivLottieView = itemView.findViewById(R.id.ivLottieView);
            txtPoint = itemView.findViewById(R.id.txtPoint);
            lblPoints = itemView.findViewById(R.id.lblPoints);
            txtSettleAmt = itemView.findViewById(R.id.txtSettleAmt);
            txtDate = itemView.findViewById(R.id.txtDate);
            probr = itemView.findViewById(R.id.probr);
            layoutWalletBalance = itemView.findViewById(R.id.layoutWalletBalance);
            layoutSubTitle = itemView.findViewById(R.id.layoutSubTitle);
            lblSubTitle = itemView.findViewById(R.id.lblSubTitle);
            txtSubTitle = itemView.findViewById(R.id.txtSubTitle);
        }

    }
}
