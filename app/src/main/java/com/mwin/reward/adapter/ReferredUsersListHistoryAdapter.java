/*
 * Copyright (c) 2021.  Hurricane Development Studios
 */

package com.mwin.reward.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.async.models.WalletListItem;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;

import java.util.List;

public class ReferredUsersListHistoryAdapter extends RecyclerView.Adapter<ReferredUsersListHistoryAdapter.SavedHolder> {
    public List<WalletListItem> listPointHistory;
    private MainResponseModel responseMain;
    private Context context;
    private RequestOptions requestOptions = new RequestOptions();

    public ReferredUsersListHistoryAdapter(List<WalletListItem> list, Context context) {
        this.listPointHistory = list;
        this.context = context;
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(context.getResources().getDimensionPixelSize(R.dimen.dim_5)));
        responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
    }

    @NonNull
    @Override
    public SavedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_referred_user_list_history, parent, false);
        return new SavedHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedHolder holder, final int position) {
        try {
            if ((position + 1) % 5 == 0 && CommonMethodsUtils.isShowAppLovinNativeAds()) {
                loadAppLovinNativeAds(holder.fl_adplaceholder);
            }
            String name = "";
            if (listPointHistory.get(position).getFirstName() != null && listPointHistory.get(position).getFirstName().length() > 0) {
                name = listPointHistory.get(position).getFirstName();
            }
            if (listPointHistory.get(position).getLastName() != null && listPointHistory.get(position).getLastName().length() > 0) {
                name = name.length() > 0 ? name + " " + listPointHistory.get(position).getLastName() : listPointHistory.get(position).getLastName();
            }
            holder.txtTitle.setText(name);
            if (listPointHistory.get(position).getSettleAmount() != null) {
                holder.txtSettleAmt.setText(listPointHistory.get(position).getSettleAmount() + " Points");
            }
            if (listPointHistory.get(position).getEntryDate() != null) {
                holder.txtDate.setText(CommonMethodsUtils.modifyDateLayout(listPointHistory.get(position).getEntryDate()));
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
        private TextView txtTitle, txtSettleAmt, txtDate;
        private FrameLayout fl_adplaceholder;

        public SavedHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtSettleAmt = itemView.findViewById(R.id.txtSettleAmt);
            txtDate = itemView.findViewById(R.id.txtDate);
            fl_adplaceholder = itemView.findViewById(R.id.fl_adplaceholder);
        }

    }
}
