/*
 * Copyright (c) 2021.  Hurricane Development Studios
 */

package com.mwin.reward.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.adapter.RedeemOptionsAdapter;
import com.mwin.reward.adapter.RedeemSubOptionsListAdapter;
import com.mwin.reward.async.GetRedeemSubOptionsListAsync;
import com.mwin.reward.async.RedeemWalletPointsAsync;
import com.mwin.reward.async.models.ExitDialog;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.async.models.RedeemOptionsSubListResponseModel;
import com.mwin.reward.async.models.RedeemPoints;
import com.mwin.reward.async.models.WithdrawList;
import com.mwin.reward.utils.AdsUtil;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;
import com.mwin.reward.value.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RedeemOptionsSubListActivity extends AppCompatActivity {
    private RecyclerView rvList;
    private List<WithdrawList> listWithdrawTypes = new ArrayList<>();
    private TextView lblLoadingAds, tvPoints, tvTitle;
    private LottieAnimationView ivLottieNoData;
    private LinearLayout layoutPoints;
    private MainResponseModel responseMain;
    private MaxAd nativeAd;
    private MaxNativeAdLoader nativeAdLoader;
    private FrameLayout frameLayoutNativeAd;
    private LinearLayout layoutAds;
    private ImageView ivHistory;
    private Dialog dialogExit;
    private RedeemOptionsSubListResponseModel objData;
    private int selectedPos = -1;
    private boolean isSetData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CommonMethodsUtils.setDayNightTheme(RedeemOptionsSubListActivity.this);
        setContentView(R.layout.activity_redeem_options_sub_list);
        responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);

        tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(getIntent().getStringExtra("title"));

        layoutPoints = findViewById(R.id.layoutPoints);
        layoutPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                    startActivity(new Intent(RedeemOptionsSubListActivity.this, EarnedWalletBalanceActivity.class));
                } else {
                    CommonMethodsUtils.NotifyLogin(RedeemOptionsSubListActivity.this);
                }
            }
        });

        ivHistory = findViewById(R.id.ivHistory);
        CommonMethodsUtils.startRoundAnimation(RedeemOptionsSubListActivity.this, ivHistory);
        ivHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                    Intent intent = new Intent(RedeemOptionsSubListActivity.this, EarnedPointHistoryActivity.class);
                    intent.putExtra("type", Constants.HistoryType.WITHDRAW_HISTORY);
                    intent.putExtra("title", "Withdrawal History");
                    startActivity(intent);
                } else {
                    CommonMethodsUtils.NotifyLogin(RedeemOptionsSubListActivity.this);
                }
            }
        });

        tvPoints = findViewById(R.id.tvPoints);
        tvPoints.setText(SharePreference.getInstance().getEarningPointString());

        rvList = findViewById(R.id.rvList);
        ivLottieNoData = findViewById(R.id.ivLottieNoData);

        ImageView imBack = findViewById(R.id.ivBack);
        imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        new GetRedeemSubOptionsListAsync(RedeemOptionsSubListActivity.this, getIntent().getStringExtra("type"));
    }

    private boolean isShownPopup = false;

    @Override
    public void onBackPressed() {
        try {
            if (objData != null && objData.getAppLuck() != null) {
                CommonMethodsUtils.dialogShowAppLuck(RedeemOptionsSubListActivity.this, objData.getAppLuck());
            } else if (dialogExit != null && !dialogExit.isShowing() && !isShownPopup) {
                isShownPopup = true;
                dialogExit.show();
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            super.onBackPressed();
        }
    }

    public void setData(RedeemOptionsSubListResponseModel responseModel) {
        objData = responseModel;
        if (responseModel.getWithdrawList() != null && responseModel.getWithdrawList().size() > 0) {
            listWithdrawTypes.clear();
            if (!isSetData) {
                AdsUtil.showAppLovinInterstitialAd(RedeemOptionsSubListActivity.this, null);
            }
            listWithdrawTypes.addAll(responseModel.getWithdrawList());
            if (CommonMethodsUtils.isShowAppLovinNativeAds()) {
                if (listWithdrawTypes.size() <= 4) {
                    listWithdrawTypes.add(listWithdrawTypes.size(), new WithdrawList());
                } else {
                    for (int i2 = 0; i2 < this.listWithdrawTypes.size(); i2++) {
                        if ((i2 + 1) % 5 == 0) {
                            listWithdrawTypes.add(i2, new WithdrawList());
                            break; // add only 1 ad view
                        }
                    }
                }
            }
            if (!isSetData) {
                RedeemSubOptionsListAdapter adapter = new RedeemSubOptionsListAdapter(listWithdrawTypes, RedeemOptionsSubListActivity.this, new RedeemSubOptionsListAdapter.ClickListener() {
                    @Override
                    public void onItemClick(int position, View v) {
                        showRedeemDialog(position);
                    }
                });
                GridLayoutManager mGridLayoutManager = new GridLayoutManager(RedeemOptionsSubListActivity.this, 2);
                mGridLayoutManager.setOrientation(RecyclerView.VERTICAL);
                mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        if (adapter.getItemViewType(position) == RedeemOptionsAdapter.ITEM_AD) {
                            return 2;
                        }
                        return 1;
                    }
                });
                rvList.setLayoutManager(mGridLayoutManager);
                rvList.setAdapter(adapter);

                // Show Default AppLuck
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getIsShowAppluck()) && responseMain.getIsShowAppluck().equals("1") && !CommonMethodsUtils.isStringNullOrEmpty(responseModel.getIsDefaultAppluck()) && responseModel.getIsDefaultAppluck().equals("1")) {
                    RelativeLayout relMainFlotAppLuck = findViewById(R.id.relMainFlotAppLuck);
                    LinearLayout layoutDefaultAd = findViewById(R.id.layoutDefaultAd);
                    CommonMethodsUtils.showDefaultAppLuck(RedeemOptionsSubListActivity.this, layoutDefaultAd, relMainFlotAppLuck, responseMain.getDefaultAppluckID());
                }

                // Load home note webview top
                try {
                    if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getHomeNote())) {
                        WebView webNote = findViewById(R.id.webNote);
                        webNote.getSettings().setJavaScriptEnabled(true);
                        webNote.setVisibility(View.VISIBLE);
                        webNote.loadDataWithBaseURL(null, responseModel.getHomeNote(), "text/html", "UTF-8", null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Load top ad
                try {
                    if (responseModel.getTopAds() != null && !CommonMethodsUtils.isStringNullOrEmpty(responseModel.getTopAds().getImage())) {
                        LinearLayout layoutTopAds = findViewById(R.id.layoutTopAds);
                        CommonMethodsUtils.loadTopBannerAd(
                                RedeemOptionsSubListActivity.this, layoutTopAds, responseModel.getTopAds());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (responseModel.getExitDialog() != null) {
                    loadExitDialog(RedeemOptionsSubListActivity.this, responseModel.getExitDialog());
                }

            } else {
                rvList.getAdapter().notifyDataSetChanged();
            }
        }
        lblLoadingAds = findViewById(R.id.lblLoadingAds);
        layoutAds = findViewById(R.id.layoutAds);
        layoutAds.setVisibility(View.VISIBLE);
        frameLayoutNativeAd = findViewById(R.id.fl_adplaceholder);

        if (listWithdrawTypes.isEmpty() && CommonMethodsUtils.isShowAppLovinNativeAds()) {
            loadAppLovinNativeAds();
        } else {
            layoutAds.setVisibility(View.GONE);
        }
        rvList.setVisibility(listWithdrawTypes.isEmpty() ? View.GONE : View.VISIBLE);
        ivLottieNoData.setVisibility(listWithdrawTypes.isEmpty() ? View.VISIBLE : View.GONE);
        if (listWithdrawTypes.isEmpty())
            ivLottieNoData.playAnimation();
        isSetData = true;
    }

    private long mLastClickTime = 0;

    private void showRedeemDialog(int position) {
        selectedPos = -1;
        if (Integer.parseInt(SharePreference.getInstance().getEarningPointString()) >= Integer.parseInt(listWithdrawTypes.get(position).getMinPoint())) {
            Dialog dialogRedeem = new Dialog(RedeemOptionsSubListActivity.this, android.R.style.Theme_Light);
            dialogRedeem.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
            dialogRedeem.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogRedeem.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            dialogRedeem.setCancelable(true);
            dialogRedeem.setCanceledOnTouchOutside(true);
            dialogRedeem.setContentView(R.layout.popup_redeem);

            EditText etMobile = dialogRedeem.findViewById(R.id.etMobile);
            if (!CommonMethodsUtils.isStringNullOrEmpty(listWithdrawTypes.get(position).getInputType()) && listWithdrawTypes.get(position).getInputType().equals("1")) {
                etMobile.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL); //for decimal numbers. 1 = mobile
            } else {
                etMobile.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS); //for email. 2 = email
            }

            TextView tvHint = dialogRedeem.findViewById(R.id.tvHint);
            tvHint.setText(listWithdrawTypes.get(position).getTitle());

            AppCompatButton btnCancel = dialogRedeem.findViewById(R.id.btnCancel);
            AppCompatButton btnRedeem = dialogRedeem.findViewById(R.id.btnRedeem);

            ImageView ivIconDialog = dialogRedeem.findViewById(R.id.ivIconDailog);
            LottieAnimationView ivLottieViewDialog = dialogRedeem.findViewById(R.id.ivLottieViewDailog);
            ProgressBar probrBanner = dialogRedeem.findViewById(R.id.probrBanner);

            etMobile.setHint(listWithdrawTypes.get(position).getHintName());

            if (listWithdrawTypes.get(position).getIcon() != null) {
                if (listWithdrawTypes.get(position).getIcon().contains(".json")) {
                    ivIconDialog.setVisibility(View.GONE);
                    ivLottieViewDialog.setVisibility(View.VISIBLE);
                    CommonMethodsUtils.setLottieAnimation(ivLottieViewDialog, listWithdrawTypes.get(position).getIcon());
                    ivLottieViewDialog.setRepeatCount(LottieDrawable.INFINITE);
                    ivLottieViewDialog.playAnimation();
                    probrBanner.setVisibility(View.GONE);
                } else {
                    ivIconDialog.setVisibility(View.VISIBLE);
                    ivLottieViewDialog.setVisibility(View.GONE);
                    Glide.with(getApplicationContext()).load(listWithdrawTypes.get(position).getIcon()).listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                                    probrBanner.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .override(getResources().getDimensionPixelSize(R.dimen.dim_80)).into(ivIconDialog);
                }
            } else {
                probrBanner.setVisibility(View.GONE);
                ivIconDialog.setVisibility(View.GONE);
                ivLottieViewDialog.setVisibility(View.GONE);
            }
            btnRedeem.setOnClickListener(v -> {
                try {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (getIntent().getStringExtra("type").equals("1") && etMobile.getText().toString().trim().length() == 10 && CommonMethodsUtils.isValidMobile(etMobile.getText().toString().trim())) {
                        selectedPos = position;
                        dialogRedeem.dismiss();
                        new RedeemWalletPointsAsync(RedeemOptionsSubListActivity.this, listWithdrawTypes.get(position).getId(), listWithdrawTypes.get(position).getTitle(), listWithdrawTypes.get(position).getType(), etMobile.getText().toString().trim());
                    } else if (!getIntent().getStringExtra("type").equals("1") && checkValidation(listWithdrawTypes.get(position).getRegxPatten(), etMobile.getText().toString().trim())) {
                        selectedPos = position;
                        dialogRedeem.dismiss();
                        new RedeemWalletPointsAsync(RedeemOptionsSubListActivity.this, listWithdrawTypes.get(position).getId(), listWithdrawTypes.get(position).getTitle(), listWithdrawTypes.get(position).getType(), etMobile.getText().toString().trim());
                    } else {
                        CommonMethodsUtils.Notify(RedeemOptionsSubListActivity.this, getString(R.string.app_name), listWithdrawTypes.get(position).getHintName() + " is Invalid!", false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            btnCancel.setOnClickListener(v -> {
                dialogRedeem.dismiss();
            });

            if (!isFinishing() && !dialogRedeem.isShowing()) {
                dialogRedeem.show();
            }
        } else {
            NotifyCoin(RedeemOptionsSubListActivity.this);
        }
    }

    public boolean checkValidation(String valPattern, String val) {
        if (!Pattern.matches(valPattern, val)) {
            return false;
        } else {
            return true;
        }
    }

    public void checkWithdraw(RedeemPoints responseModel) {
        if (responseModel != null) {
            if (responseModel.getStatus().matches(Constants.STATUS_SUCCESS)) {
                responseMain.setNextWithdrawAmount(responseModel.getNextWithdrawAmount());
                SharePreference.getInstance().putString(SharePreference.HomeData, new Gson().toJson(responseMain));
                CommonMethodsUtils.logFirebaseEvent(RedeemOptionsSubListActivity.this, "FeatureUsabilityItemId", "FeatureUsabilityEvent", "Withdraw", "Withdraw Success -> " + listWithdrawTypes.get(selectedPos).getTitle());
                SharePreference.getInstance().putString(SharePreference.EarnedPoints, responseModel.getEarningPoint());
                tvPoints.setText(SharePreference.getInstance().getEarningPointString());
                if (!CommonMethodsUtils.isStringNullOrEmpty(objData.getIsRateus()) && objData.getIsRateus().equals("1") && !SharePreference.getInstance().getBoolean(SharePreference.IS_REVIEW_GIVEN)) {
                    AdsUtil.showAppLovinRewardedAd(RedeemOptionsSubListActivity.this, new AdsUtil.AdShownListener() {
                        @Override
                        public void onAdDismiss() {
                            SuccessDialog(RedeemOptionsSubListActivity.this, getString(R.string.app_name), responseModel.getMessage(), responseModel.getTxnStatus(), true);
                        }
                    });
                } else {
                    SuccessDialog(RedeemOptionsSubListActivity.this, getString(R.string.app_name), responseModel.getMessage(), responseModel.getTxnStatus(), false);
                }
            } else {
                CommonMethodsUtils.logFirebaseEvent(RedeemOptionsSubListActivity.this, "FeatureUsabilityItemId", "FeatureUsabilityEvent", "Withdraw", "Withdraw Fail -> " + listWithdrawTypes.get(selectedPos).getTitle());
                showFailDialog(getString(R.string.app_name), responseModel.getMessage());
            }
        } else {
            CommonMethodsUtils.logFirebaseEvent(RedeemOptionsSubListActivity.this, "FeatureUsabilityItemId", "FeatureUsabilityEvent", "Withdraw", "Withdraw Error -> " + listWithdrawTypes.get(selectedPos).getTitle());
            CommonMethodsUtils.Notify(RedeemOptionsSubListActivity.this, getString(R.string.app_name), "Something went wrong, please try again later.", false);
        }
    }

    private void showFailDialog(String title, String message) {
        try {
            final Dialog dialog1 = new Dialog(RedeemOptionsSubListActivity.this, android.R.style.Theme_Light);
            dialog1.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog1.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            dialog1.setContentView(R.layout.popup_message_notify);
            dialog1.setCancelable(false);

            Button btnOk = dialog1.findViewById(R.id.btnOk);
            TextView tvTitle = dialog1.findViewById(R.id.tvTitle);
            tvTitle.setText(title);

            TextView tvMessage = dialog1.findViewById(R.id.tvMessage);
            tvMessage.setText(message);
            btnOk.setOnClickListener(v -> {
                AdsUtil.showAppLovinRewardedAd(RedeemOptionsSubListActivity.this, new AdsUtil.AdShownListener() {
                    @Override
                    public void onAdDismiss() {
                        if (dialog1 != null) {
                            dialog1.dismiss();
                        }
                    }
                });
            });
            if (!isFinishing()) {
                dialog1.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadExitDialog(Activity activity, ExitDialog popData) {
        if (activity != null && popData != null) {
            dialogExit = new Dialog(activity, android.R.style.Theme_Light);
            dialogExit.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
            dialogExit.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogExit.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            dialogExit.setContentView(R.layout.popup_home_data);
            dialogExit.setCancelable(true);

            Button btnOk = dialogExit.findViewById(R.id.btnSubmit);

            TextView txtTitle = dialogExit.findViewById(R.id.txtTitle);
            txtTitle.setText(popData.getTitle());

            TextView btnCancel = dialogExit.findViewById(R.id.btnCancel);

            ProgressBar probrBanner = dialogExit.findViewById(R.id.probrBanner);
            ImageView imgBanner = dialogExit.findViewById(R.id.imgBanner);
            RelativeLayout relPopup = dialogExit.findViewById(R.id.relPopup);
            LottieAnimationView ivLottieView = dialogExit.findViewById(R.id.ivLottieView);

            TextView txtMessage = dialogExit.findViewById(R.id.txtMessage);
            txtMessage.setText(popData.getDescription());

            btnCancel.setVisibility(View.GONE);

            if (!CommonMethodsUtils.isStringNullOrEmpty(popData.getBtnName())) {
                btnOk.setText(popData.getBtnName());
            }
            if (!CommonMethodsUtils.isStringNullOrEmpty(popData.getBtnColor())) {
                Drawable mDrawable = ContextCompat.getDrawable(RedeemOptionsSubListActivity.this, R.drawable.ic_btn_rounded_corner);
                mDrawable.setColorFilter(new PorterDuffColorFilter(Color.parseColor(popData.getBtnColor()), PorterDuff.Mode.SRC_IN));
                btnOk.setBackground(mDrawable);
            }
            if (!CommonMethodsUtils.isStringNullOrEmpty(popData.getImage())) {
                if (popData.getImage().contains("json")) {
                    probrBanner.setVisibility(View.GONE);
                    imgBanner.setVisibility(View.GONE);
                    ivLottieView.setVisibility(View.VISIBLE);
                    CommonMethodsUtils.setLottieAnimation(ivLottieView, popData.getImage());
                    ivLottieView.setRepeatCount(LottieDrawable.INFINITE);
                } else {
                    imgBanner.setVisibility(View.VISIBLE);
                    ivLottieView.setVisibility(View.GONE);
                    Glide.with(RedeemOptionsSubListActivity.this).load(popData.getImage()).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            probrBanner.setVisibility(View.GONE);
                            return false;
                        }
                    }).into(imgBanner);
                }
            } else {
                imgBanner.setVisibility(View.GONE);
                probrBanner.setVisibility(View.GONE);
            }
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogExit.dismiss();
                }
            });
            relPopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogExit.dismiss();
                    if (popData.getIsShowAds() != null && popData.getIsShowAds().equals(Constants.APPLOVIN_INTERSTITIAL)) {
                        AdsUtil.showAppLovinInterstitialAd(RedeemOptionsSubListActivity.this, new AdsUtil.AdShownListener() {
                            @Override
                            public void onAdDismiss() {
                                CommonMethodsUtils.Redirect(activity, popData.getScreenNo(), popData.getTitle(), popData.getUrl(), null, null, popData.getImage());
                            }
                        });
                    } else if (popData.getIsShowAds() != null && popData.getIsShowAds().equals(Constants.APPLOVIN_REWARD)) {
                        AdsUtil.showAppLovinRewardedAd(RedeemOptionsSubListActivity.this, new AdsUtil.AdShownListener() {
                            @Override
                            public void onAdDismiss() {
                                CommonMethodsUtils.Redirect(activity, popData.getScreenNo(), popData.getTitle(), popData.getUrl(), null, null, popData.getImage());
                            }
                        });
                    } else {
                        CommonMethodsUtils.Redirect(activity, popData.getScreenNo(), popData.getTitle(), popData.getUrl(), null, null, popData.getImage());
                    }
                }
            });
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogExit.dismiss();
                    if (popData.getIsShowAds() != null && popData.getIsShowAds().equals(Constants.APPLOVIN_INTERSTITIAL)) {
                        AdsUtil.showAppLovinInterstitialAd(RedeemOptionsSubListActivity.this, new AdsUtil.AdShownListener() {
                            @Override
                            public void onAdDismiss() {
                                CommonMethodsUtils.Redirect(activity, popData.getScreenNo(), popData.getTitle(), popData.getUrl(), null, null, popData.getImage());
                            }
                        });
                    } else if (popData.getIsShowAds() != null && popData.getIsShowAds().equals(Constants.APPLOVIN_REWARD)) {
                        AdsUtil.showAppLovinRewardedAd(RedeemOptionsSubListActivity.this, new AdsUtil.AdShownListener() {
                            @Override
                            public void onAdDismiss() {
                                CommonMethodsUtils.Redirect(activity, popData.getScreenNo(), popData.getTitle(), popData.getUrl(), null, null, popData.getImage());
                            }
                        });
                    } else {
                        CommonMethodsUtils.Redirect(activity, popData.getScreenNo(), popData.getTitle(), popData.getUrl(), null, null, popData.getImage());
                    }
                }
            });
        }
    }

    public void SuccessDialog(final Activity activity, String title, String message, String txtStatus, boolean isShowRate) {
        ////Log.e("title1--)", "" + title + "--)" + message);
        if (activity != null) {
            final Dialog dialog1 = new Dialog(activity, android.R.style.Theme_Light);
            dialog1.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog1.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            dialog1.setContentView(R.layout.popup_notify_win);
            dialog1.setCancelable(false);
            Button btnOk = dialog1.findViewById(R.id.btnOk);
            LottieAnimationView lottiePlay = dialog1.findViewById(R.id.animation_view);

            //AppLogger.getInstance().e("txtStatus--)", "" + txtStatus);
            if (txtStatus.matches("1")) {
                lottiePlay.setVisibility(View.VISIBLE);
                lottiePlay.setAnimation(R.raw.lottie_win);
                lottiePlay.setRepeatCount(LottieDrawable.INFINITE);
                lottiePlay.playAnimation();
                Drawable mDrawable = ContextCompat.getDrawable(activity, R.drawable.ic_btn_rounded_corner);
                mDrawable.setColorFilter(new PorterDuffColorFilter(getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN));
                btnOk.setBackground(mDrawable);
            } else if (txtStatus.matches("0")) {
                lottiePlay.setVisibility(View.VISIBLE);
                lottiePlay.setAnimation(R.raw.pending_anim);
                lottiePlay.setRepeatCount(LottieDrawable.INFINITE);
                lottiePlay.playAnimation();
                Drawable mDrawable = ContextCompat.getDrawable(activity, R.drawable.ic_btn_rounded_corner);
                mDrawable.setColorFilter(new PorterDuffColorFilter(getColor(R.color.orange_yellow), PorterDuff.Mode.SRC_IN));
                btnOk.setBackground(mDrawable);
            } else if (txtStatus.matches("2")) {
                lottiePlay.setVisibility(View.VISIBLE);
                lottiePlay.setAnimation(R.raw.revert_anim);
                lottiePlay.setRepeatCount(LottieDrawable.INFINITE);
                lottiePlay.playAnimation();
                Drawable mDrawable = ContextCompat.getDrawable(activity, R.drawable.ic_btn_rounded_corner);
                mDrawable.setColorFilter(new PorterDuffColorFilter(getColor(R.color.red), PorterDuff.Mode.SRC_IN));
                btnOk.setBackground(mDrawable);
            } else if (txtStatus.matches("3")) {
                lottiePlay.setVisibility(View.VISIBLE);
                lottiePlay.setAnimation(R.raw.revert_anim);
                lottiePlay.setRepeatCount(LottieDrawable.INFINITE);
                lottiePlay.playAnimation();
                Drawable mDrawable = ContextCompat.getDrawable(activity, R.drawable.ic_btn_rounded_corner);
                mDrawable.setColorFilter(new PorterDuffColorFilter(getColor(R.color.red), PorterDuff.Mode.SRC_IN));
                btnOk.setBackground(mDrawable);
            }

            TextView tvTitle = dialog1.findViewById(R.id.tvTitle);
            tvTitle.setText(title);
            TextView tvMessage = dialog1.findViewById(R.id.tvMessage);
            tvMessage.setText(message);
            btnOk.setOnClickListener(v -> {
                if (isShowRate) {
                    if (!activity.isFinishing()) {
                        dialog1.dismiss();
                    }
                    CommonMethodsUtils.showRatePopup(RedeemOptionsSubListActivity.this);
                } else {
                    AdsUtil.showAppLovinRewardedAd(RedeemOptionsSubListActivity.this, new AdsUtil.AdShownListener() {
                        @Override
                        public void onAdDismiss() {
                            if (!activity.isFinishing()) {
                                dialog1.dismiss();
                            }
                        }
                    });
                }
            });
            dialog1.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    new GetRedeemSubOptionsListAsync(RedeemOptionsSubListActivity.this, getIntent().getStringExtra("type"));
                }
            });
            if (!activity.isFinishing()) {
                dialog1.show();
            }
        }
    }

    public void NotifyCoin(Activity activity) {
        if (activity != null) {
            Dialog dialog1 = new Dialog(RedeemOptionsSubListActivity.this, android.R.style.Theme_Light);
            dialog1.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog1.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            dialog1.setCancelable(true);
            dialog1.setCanceledOnTouchOutside(true);
            dialog1.setContentView(R.layout.popup_not_enough_points);

            TextView tvTitle = dialog1.findViewById(R.id.tvTitle);
            tvTitle.setTextColor(getColor(R.color.red));
            tvTitle.setText("Not Enough Coins!");

            TextView tvMessage = dialog1.findViewById(R.id.tvMessage);
            tvMessage.setText("You don't have enough points to withdraw. Earn more points and then try again.");

            View viewShine = dialog1.findViewById(R.id.viewShine);
            Animation animUpDown = AnimationUtils.loadAnimation(RedeemOptionsSubListActivity.this, R.anim.left_to_right);
            animUpDown.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    viewShine.startAnimation(animUpDown);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            // start the animation
            viewShine.startAnimation(animUpDown);

            Button btnEarnMore = dialog1.findViewById(R.id.btnEarnMore);
            btnEarnMore.setOnClickListener(v -> {
                if (!activity.isFinishing()) {
                    dialog1.dismiss();
                }
                Intent intent = new Intent(RedeemOptionsSubListActivity.this, TaskListActivity.class);
                startActivity(intent);
            });
            if (!activity.isFinishing()) {
                dialog1.show();
            }
        }
    }

    private void loadAppLovinNativeAds() {
        try {
            nativeAdLoader = new MaxNativeAdLoader(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinNativeID()), RedeemOptionsSubListActivity.this);
            nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
                @Override
                public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad) {
                    frameLayoutNativeAd = findViewById(R.id.fl_adplaceholder);
                    if (nativeAd != null) {
                        nativeAdLoader.destroy(nativeAd);
                    }
                    nativeAd = ad;
                    frameLayoutNativeAd.removeAllViews();
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) frameLayoutNativeAd.getLayoutParams();
                    params.height = getResources().getDimensionPixelSize(R.dimen.dim_300);
                    params.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    frameLayoutNativeAd.setLayoutParams(params);
                    frameLayoutNativeAd.setPadding((int) getResources().getDimension(R.dimen.dim_10), (int) getResources().getDimension(R.dimen.dim_10), (int) getResources().getDimension(R.dimen.dim_10), (int) getResources().getDimension(R.dimen.dim_10));
                    frameLayoutNativeAd.addView(nativeAdView);
                    lblLoadingAds.setVisibility(View.GONE);
                    layoutAds.setVisibility(View.VISIBLE);

                    //AppLogger.getInstance().e("AppLovin Loaded: ", "===");
                }

                @Override
                public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
                    //AppLogger.getInstance().e("AppLovin Failed: ", error.getMessage());
                    layoutAds.setVisibility(View.GONE);
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

    @Override
    protected void onStop() {
        super.onStop();
        if (isFinishing()) {
            try {
                if (nativeAd != null && nativeAdLoader != null) {
                    nativeAdLoader.destroy(nativeAd);
                    nativeAd = null;
                    frameLayoutNativeAd = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}