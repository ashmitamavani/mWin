/*
 * Copyright (c) 2021.  Hurricane Development Studios
 */

package com.mwin.reward.activity;

import static android.view.View.VISIBLE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.google.gson.Gson;
import com.mwin.reward.R;
import com.mwin.reward.adapter.GiveawayGiftCodeListAdapter;
import com.mwin.reward.adapter.GiveawayGiftSocialMediaListAdapter;
import com.mwin.reward.async.GetGiveawayGiftListAsync;
import com.mwin.reward.async.SaveGiveawayGiftAsync;
import com.mwin.reward.async.models.GiveawayGiftModel;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.utils.AdsUtil;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;
import com.mwin.reward.value.Constants;

public class GiveawayGiftActivity extends AppCompatActivity {
    private RecyclerView rvSocialPlatforms;
    private TextView lblLoadingAds, tvPoints, tvStarLeft, tvStarRight;
    private LottieAnimationView ivLottieNoData;
    private MainResponseModel responseMain;
    private MaxAd nativeAd, nativeAdWin;
    private MaxNativeAdLoader nativeAdLoader, nativeAdLoaderWin;
    private FrameLayout frameLayoutNativeAd;
    private LinearLayout layoutAds;
    private GiveawayGiftSocialMediaListAdapter mAdapter;
    private ImageView ivHistory;
    private LinearLayout layoutPoints, layoutContent;
    private EditText etCouponCode;
    private AppCompatButton btnClaimNow, btnHowToClaim;
    private RelativeLayout layoutMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CommonMethodsUtils.setDayNightTheme(GiveawayGiftActivity.this);
        setContentView(R.layout.activity_giveaway_gift);
        responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);

        layoutMain = findViewById(R.id.layoutMain);

        Animation rotation = AnimationUtils.loadAnimation(GiveawayGiftActivity.this, R.anim.rotate_animation);
        rotation.setFillAfter(true);

        tvStarLeft = findViewById(R.id.tvStarLeft);
        tvStarLeft.startAnimation(rotation);

        tvStarRight = findViewById(R.id.tvStarRight);
        tvStarRight.startAnimation(rotation);

        etCouponCode = findViewById(R.id.etCouponCode);
        etCouponCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                etCouponCode.post(new Runnable() {
                    @Override
                    public void run() {
                        etCouponCode.setLetterSpacing(etCouponCode.getText().toString().length() > 0 ? 0.2f : 0.0f);
                    }
                });
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        btnClaimNow = findViewById(R.id.btnClaimNow);
        btnHowToClaim = findViewById(R.id.btnHowToClaim);
        btnClaimNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonMethodsUtils.setEnableDisable(GiveawayGiftActivity.this, btnClaimNow);
                if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                    if (etCouponCode.getText().toString().trim().length() > 0) {
                        new SaveGiveawayGiftAsync(GiveawayGiftActivity.this, etCouponCode.getText().toString().trim());
                    } else {
                        CommonMethodsUtils.setToast(GiveawayGiftActivity.this, "Enter giveaway code");
                    }
                } else {
                    CommonMethodsUtils.NotifyLogin(GiveawayGiftActivity.this);
                }
            }
        });

        layoutContent = findViewById(R.id.layoutContent);
        ivLottieNoData = findViewById(R.id.ivLottieNoData);
        rvSocialPlatforms = findViewById(R.id.rvSocialPlatforms);

        ivHistory = findViewById(R.id.ivHistory);
        CommonMethodsUtils.startRoundAnimation(GiveawayGiftActivity.this, ivHistory);
        ivHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                    startActivity(new Intent(GiveawayGiftActivity.this, EarnedPointHistoryActivity.class)
                            .putExtra("type", Constants.HistoryType.GIVE_AWAY)
                            .putExtra("title", "Giveaway History"));
                } else {
                    CommonMethodsUtils.NotifyLogin(GiveawayGiftActivity.this);
                }
            }
        });
        layoutPoints = findViewById(R.id.layoutPoints);
        layoutPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                    startActivity(new Intent(GiveawayGiftActivity.this, EarnedWalletBalanceActivity.class));
                } else {
                    CommonMethodsUtils.NotifyLogin(GiveawayGiftActivity.this);
                }
            }
        });
        tvPoints = findViewById(R.id.tvPoints);
        tvPoints.setText(SharePreference.getInstance().getEarningPointString());
        ImageView imBack = findViewById(R.id.ivBack);
        imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        View viewShine = findViewById(R.id.viewShine);
        Animation animUpDown = AnimationUtils.loadAnimation(GiveawayGiftActivity.this, R.anim.left_to_right);
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

        new GetGiveawayGiftListAsync(GiveawayGiftActivity.this);
    }

    public void changeGiveawayDataValues(GiveawayGiftModel responseModel) {
        if (responseModel.getStatus().equals(Constants.STATUS_SUCCESS)) {
            SharePreference.getInstance().putString(SharePreference.EarnedPoints, responseModel.getEarningPoint());
            CommonMethodsUtils.logFirebaseEvent(GiveawayGiftActivity.this, "FeatureUsabilityItemId", "FeatureUsabilityEvent", "Giveaway", "Giveaway Got Reward");
            showWinPopup(responseModel.getCouponPoints());
        } else if (responseModel.getStatus().equals(Constants.STATUS_ERROR) || responseModel.getStatus().equals("2")) {
            showErrorMessage("Daily Giveaway", responseModel);
        }
    }

    private void showErrorMessage(String title, GiveawayGiftModel responseModel) {
        try {
            final Dialog dialog1 = new Dialog(GiveawayGiftActivity.this, android.R.style.Theme_Light);
            dialog1.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog1.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            dialog1.setContentView(R.layout.popup_message_notify);
            dialog1.setCancelable(false);

            Button btnOk = dialog1.findViewById(R.id.btnOk);
            TextView tvTitle = dialog1.findViewById(R.id.tvTitle);
            tvTitle.setText(title);

            TextView tvMessage = dialog1.findViewById(R.id.tvMessage);
            tvMessage.setText(responseModel.getMessage());
            if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getBtnName())) {
                btnOk.setText(responseModel.getBtnName());
            }
            btnOk.setOnClickListener(v -> {
                AdsUtil.showAppLovinInterstitialAd(GiveawayGiftActivity.this, new AdsUtil.AdShownListener() {
                    @Override
                    public void onAdDismiss() {
                        try {
                            if (dialog1 != null) {
                                dialog1.dismiss();
                            }
                            if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getScreenNo())) {
                                CommonMethodsUtils.Redirect(GiveawayGiftActivity.this, responseModel.getScreenNo(), "", "", "", "", "");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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

    public void showWinPopup(String point) {
        Dialog dialogWin = new Dialog(GiveawayGiftActivity.this, android.R.style.Theme_Light);
        dialogWin.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
        dialogWin.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogWin.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        dialogWin.setCancelable(false);
        dialogWin.setCanceledOnTouchOutside(false);
        dialogWin.setContentView(R.layout.popup_win);
        dialogWin.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        FrameLayout fl_adplaceholder = dialogWin.findViewById(R.id.fl_adplaceholder);
        TextView lblLoadingAds = dialogWin.findViewById(R.id.lblLoadingAds);
        if (CommonMethodsUtils.isShowAppLovinNativeAds()) {
            loadAppLovinNativeAds(fl_adplaceholder, lblLoadingAds);
        } else {
            lblLoadingAds.setVisibility(View.GONE);
        }

        TextView tvPoint = dialogWin.findViewById(R.id.tvPoints);

        LottieAnimationView animation_view = dialogWin.findViewById(R.id.animation_view);
        CommonMethodsUtils.setLottieAnimation(animation_view, responseMain.getCelebrationLottieUrl());
        animation_view.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation, boolean isReverse) {
                super.onAnimationStart(animation, isReverse);
                CommonMethodsUtils.startTextCountAnimation(tvPoint, point);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animation_view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        ImageView ivClose = dialogWin.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdsUtil.showAppLovinRewardedAd(GiveawayGiftActivity.this, new AdsUtil.AdShownListener() {
                    @Override
                    public void onAdDismiss() {
                        if (dialogWin != null) {
                            dialogWin.dismiss();
                        }
                    }
                });
            }
        });

        TextView lblPoints = dialogWin.findViewById(R.id.lblPoints);
        AppCompatButton btnOk = dialogWin.findViewById(R.id.btnOk);
        try {
            int pt = Integer.parseInt(point);
            lblPoints.setText((pt <= 1 ? "Point" : "Points"));
        } catch (Exception e) {
            e.printStackTrace();
            lblPoints.setText("Points");
        }

        btnOk.setOnClickListener(v -> {
            AdsUtil.showAppLovinRewardedAd(GiveawayGiftActivity.this, new AdsUtil.AdShownListener() {
                @Override
                public void onAdDismiss() {
                    if (dialogWin != null) {
                        dialogWin.dismiss();
                    }
                }
            });
        });
        dialogWin.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                CommonMethodsUtils.GetCoinAnimation(GiveawayGiftActivity.this, layoutMain, layoutPoints);
                tvPoints.setText(SharePreference.getInstance().getEarningPointString());
            }
        });
        if (!isFinishing() && !dialogWin.isShowing()) {
            dialogWin.show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    animation_view.setVisibility(View.VISIBLE);
                    animation_view.playAnimation();
                }
            }, 500);
        }
    }

    private void loadAppLovinNativeAds(FrameLayout frameLayoutNativeAd, TextView lblLoadingAds) {
        try {
            nativeAdLoaderWin = new MaxNativeAdLoader(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinNativeID()), GiveawayGiftActivity.this);
            nativeAdLoaderWin.setNativeAdListener(new MaxNativeAdListener() {
                @Override
                public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad) {
                    if (nativeAdWin != null) {
                        nativeAdLoaderWin.destroy(nativeAdWin);
                    }
                    nativeAdWin = ad;
                    frameLayoutNativeAd.removeAllViews();
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) frameLayoutNativeAd.getLayoutParams();
                    params.height = getResources().getDimensionPixelSize(R.dimen.dim_300);
                    params.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    frameLayoutNativeAd.setLayoutParams(params);
                    frameLayoutNativeAd.setPadding((int) getResources().getDimension(R.dimen.dim_10), (int) getResources().getDimension(R.dimen.dim_10), (int) getResources().getDimension(R.dimen.dim_10), (int) getResources().getDimension(R.dimen.dim_10));
                    lblLoadingAds.setVisibility(View.GONE);
                    frameLayoutNativeAd.addView(nativeAdView);
                    frameLayoutNativeAd.setVisibility(VISIBLE);
                    //AppLogger.getInstance().e("AppLovin Loaded WIN: ", "===WIN");
                }

                @Override
                public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
                    //AppLogger.getInstance().e("AppLovin Failed WIN: ", error.getMessage());
                    frameLayoutNativeAd.setVisibility(View.GONE);
                    lblLoadingAds.setVisibility(View.GONE);
                }

                @Override
                public void onNativeAdClicked(final MaxAd ad) {

                }
            });
            nativeAdLoaderWin.loadAd();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (responseModel != null && responseModel.getAppLuck() != null) {

                CommonMethodsUtils.dialogShowAppLuck(GiveawayGiftActivity.this, responseModel.getAppLuck());
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            super.onBackPressed();
        }
    }

    private GiveawayGiftModel responseModel;

    public void setData(GiveawayGiftModel responseModel1) {
        responseModel = responseModel1;
        if (responseModel.getSocialMedia() != null && responseModel.getSocialMedia().size() > 0) {
            AdsUtil.showAppLovinInterstitialAd(GiveawayGiftActivity.this, null);

            mAdapter = new GiveawayGiftSocialMediaListAdapter(this, responseModel.getSocialMedia(), new GiveawayGiftSocialMediaListAdapter.ClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    CommonMethodsUtils.openUrl(GiveawayGiftActivity.this, responseModel.getSocialMedia().get(position).getUrl());
                }
            });
            rvSocialPlatforms.setAdapter(mAdapter);

            // Show Default AppLuck
            if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getIsShowAppluck()) && responseMain.getIsShowAppluck().equals("1") && !CommonMethodsUtils.isStringNullOrEmpty(responseModel.getIsDefaultAppluck()) && responseModel.getIsDefaultAppluck().equals("1")) {
                RelativeLayout relMainFlotAppLuck = findViewById(R.id.relMainFlotAppLuck);
                LinearLayout layoutDefaultAd = findViewById(R.id.layoutDefaultAd);
                CommonMethodsUtils.showDefaultAppLuck(GiveawayGiftActivity.this, layoutDefaultAd, relMainFlotAppLuck, responseMain.getDefaultAppluckID());
            }

            // Load giveaway codes
            try {
                LinearLayout layoutGiveawayCodes = findViewById(R.id.layoutGiveawayCodes);
                if (responseModel.getGiveawayCodeList() != null && responseModel.getGiveawayCodeList().size() > 0) {
                    layoutGiveawayCodes.setVisibility(VISIBLE);
                    RecyclerView rvGiveawayCodeList = findViewById(R.id.rvGiveawayCodeList);
                    GiveawayGiftCodeListAdapter mAdapter = new GiveawayGiftCodeListAdapter(this, responseModel.getGiveawayCodeList(), new GiveawayGiftCodeListAdapter.ClickListener() {
                        @Override
                        public void onItemClick(int position, View v) {
                        }

                        @Override
                        public void onCopyButtonClicked(int position, View v) {
                            String val = responseModel.getGiveawayCodeList().get(position).getCouponCode();
                            if (val != null) {
                                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("Copied Text", val);
                                clipboard.setPrimaryClip(clip);
                                CommonMethodsUtils.setToast(GiveawayGiftActivity.this, "Copied!");
                                AdsUtil.showAppLovinRewardedAd(GiveawayGiftActivity.this, null);
                            }
                        }

                        @Override
                        public void onCompleteTaskButtonClicked(int position, View v) {
                            CommonMethodsUtils.Redirect(GiveawayGiftActivity.this, responseModel.getGiveawayCodeList().get(position).getScreenNo(), "", "", "", "", "");
                        }
                    });
                    rvGiveawayCodeList.setLayoutManager(new LinearLayoutManager(GiveawayGiftActivity.this));
                    rvGiveawayCodeList.setAdapter(mAdapter);
                } else {
                    layoutGiveawayCodes.setVisibility(View.GONE);
                    lblLoadingAds = findViewById(R.id.lblLoadingAds);
                    layoutAds = findViewById(R.id.layoutAds);
                    layoutAds.setVisibility(View.VISIBLE);
                    frameLayoutNativeAd = findViewById(R.id.fl_adplaceholder);
                    if (CommonMethodsUtils.isShowAppLovinNativeAds()) {
                        loadAppLovinNativeAds();
                    } else {
                        layoutAds.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
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
                    CommonMethodsUtils.loadTopBannerAd(GiveawayGiftActivity.this, layoutTopAds, responseModel.getTopAds());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getHelpVideoUrl())) {
            btnHowToClaim.setVisibility(VISIBLE);
            btnHowToClaim.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommonMethodsUtils.openUrl(GiveawayGiftActivity.this, responseModel.getHelpVideoUrl());
                }
            });
        } else {
            btnHowToClaim.setVisibility(View.GONE);
        }
        layoutContent.setVisibility(responseModel.getSocialMedia() != null && responseModel.getSocialMedia().size() > 0 ? View.VISIBLE : View.GONE);
        ivLottieNoData.setVisibility(responseModel.getSocialMedia() != null && responseModel.getSocialMedia().size() > 0 ? View.GONE : View.VISIBLE);
        if (responseModel.getSocialMedia() == null && responseModel.getSocialMedia().size() == 0)
            ivLottieNoData.playAnimation();
    }

    private void loadAppLovinNativeAds() {
        try {
            nativeAdLoader = new MaxNativeAdLoader(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinNativeID()), GiveawayGiftActivity.this);
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
                if (nativeAdWin != null && nativeAdLoaderWin != null) {
                    nativeAdLoaderWin.destroy(nativeAdWin);
                    nativeAdWin = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}