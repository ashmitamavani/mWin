/*
 * Copyright (c) 2021.  Hurricane Development Studios
 */

package com.mwin.reward.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.makeopinion.cpxresearchlib.CPXResearchListener;
import com.makeopinion.cpxresearchlib.models.CPXCardConfiguration;
import com.makeopinion.cpxresearchlib.models.CPXCardStyle;
import com.makeopinion.cpxresearchlib.models.SurveyItem;
import com.makeopinion.cpxresearchlib.models.TransactionItem;
import com.mwin.reward.ApplicationController;
import com.mwin.reward.R;
import com.mwin.reward.adapter.DailyTargetAdapter;
import com.mwin.reward.adapter.EarningOptionsGridAdapter;
import com.mwin.reward.adapter.EverydayCheckinAdapter;
import com.mwin.reward.adapter.HomeTasksListAdapter;
import com.mwin.reward.adapter.LiveMilestonesAdapter;
import com.mwin.reward.adapter.QuickTasksAdapter;
import com.mwin.reward.adapter.SingleSliderImageAdapter;
import com.mwin.reward.adapter.TopOffersListAdapter;
import com.mwin.reward.async.GetEanringOptionsScreenAsync;
import com.mwin.reward.async.GetWalletBalanceAsync;
import com.mwin.reward.async.SaveDailyTargetAsync;
import com.mwin.reward.async.SaveQuickTaskAsync;
import com.mwin.reward.async.models.EarningOptionsScreenModel;
import com.mwin.reward.async.models.HomeDataItem;
import com.mwin.reward.async.models.HomeDataListItem;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.utils.ActivityManager;
import com.mwin.reward.utils.AppLogger;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;
import com.mwin.reward.value.Constants;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.ArrowPositionRules;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
import com.skydoves.balloon.BalloonSizeSpec;
import com.skydoves.balloon.OnBalloonClickListener;

import java.util.List;

public class EarningOptionsActivity extends AppCompatActivity {
    public static EarningOptionsScreenModel objRewardScreenModel;
    private MainResponseModel responseMain;
    private TextView tvTimer, tvPoints, lblGiveawayCode, tvGiveawayCode, tvTimerDailyTarget;
    private LinearLayout layoutInflate, layoutTimer, layoutPoints, layoutGiveawayCode;
    private RecyclerView rvDailyLoginList;
    private EverydayCheckinAdapter dailyLoginAdapter;
    private MaxAd nativeAd;
    private MaxNativeAdLoader nativeAdLoader;
    private String todayDate,todayDateDailyTarget, endDate,endDateDailyTarget;
    private CountDownTimer timer, timerQuickTask,timerDailyTarget;
    private int time,timeDailyTarget;
    private ImageView ivBack, ivAdjoe, ivAdjoeLeaderboard;
    private Animation blinkAnimation;
    private Balloon balloon;
    private LottieAnimationView lottieAdjoe;
    private boolean isTimerSet = false, isTimerOver = false;
    private QuickTasksAdapter quickTasksAdapter;
    private int selectedQuickTaskPos = -1, selectedDailyTargetPos = -1;
    private DailyTargetAdapter dailyTargetAdapter;
    private View quickTaskView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CommonMethodsUtils.setDayNightTheme(EarningOptionsActivity.this);
        setContentView(R.layout.activity_earning_options);
        responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
        blinkAnimation = new AlphaAnimation(0.3f, 1.0f);
        blinkAnimation.setDuration(500); //You can manage the blinking time with this parameter
        blinkAnimation.setStartOffset(20);
        blinkAnimation.setRepeatMode(Animation.REVERSE);
        blinkAnimation.setRepeatCount(Animation.INFINITE);
        setViews();
    }

    private void setViews() {
        lblGiveawayCode = findViewById(R.id.lblGiveawayCode);
        tvGiveawayCode = findViewById(R.id.tvGiveawayCode);
        layoutGiveawayCode = findViewById(R.id.layoutGiveawayCode);
        layoutInflate = findViewById(R.id.layoutInflate);
        ivAdjoe = findViewById(R.id.ivAdjoe);
        ivAdjoeLeaderboard = findViewById(R.id.ivAdjoeLeaderboard);
        ivAdjoeLeaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EarningOptionsActivity.this, AdjoeLeaderboardActivity.class));
            }
        });
        lottieAdjoe = findViewById(R.id.lottieAdjoe);
        layoutPoints = findViewById(R.id.layoutPoints);
        layoutPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                    startActivity(new Intent(EarningOptionsActivity.this, EarnedWalletBalanceActivity.class));
                } else {
                    CommonMethodsUtils.NotifyLogin(EarningOptionsActivity.this);
                }
            }
        });

        tvPoints = findViewById(R.id.tvPoints);

        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        try {
            if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getIsShowPlaytimeIcon()) && responseMain.getIsShowPlaytimeIcon().equalsIgnoreCase("1")) {
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getImageAdjoeIcon())) {
                    if (responseMain.getImageAdjoeIcon().endsWith(".json")) {
                        lottieAdjoe.setVisibility(View.VISIBLE);
                        lottieAdjoe.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CommonMethodsUtils.openAdjoeOfferWall(EarningOptionsActivity.this);
                            }
                        });
                        CommonMethodsUtils.setLottieAnimation(lottieAdjoe, responseMain.getImageAdjoeIcon());
                    } else {
                        ivAdjoe.setVisibility(View.VISIBLE);
                        ivAdjoe.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CommonMethodsUtils.openAdjoeOfferWall(EarningOptionsActivity.this);
                            }
                        });
                        Glide.with(EarningOptionsActivity.this).load(responseMain.getImageAdjoeIcon()).override(getResources().getDimensionPixelSize(R.dimen.dim_32)).into(ivAdjoe);
                    }
                }
            } else {
                ivAdjoe.setVisibility(View.GONE);
                lottieAdjoe.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showHomeDialog() {
        try {
            if (objRewardScreenModel.getHomeDialog() != null) {
                if (SharePreference.getInstance().getString(SharePreference.rewardDialogShownDate + objRewardScreenModel.getHomeDialog().getId()).length() == 0 || !SharePreference.getInstance().getString(SharePreference.rewardDialogShownDate + objRewardScreenModel.getHomeDialog().getId()).equals(CommonMethodsUtils.getCurrentDate())) {

                    SharePreference.getInstance().putString(SharePreference.rewardDialogShownDate + objRewardScreenModel.getHomeDialog().getId(), CommonMethodsUtils.getCurrentDate());

                    Dialog dialog = new Dialog(EarningOptionsActivity.this, android.R.style.Theme_Light);
                    dialog.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    dialog.setContentView(R.layout.popup_home_data);

                    Button btnOk = dialog.findViewById(R.id.btnSubmit);
                    TextView txtTitle = dialog.findViewById(R.id.txtTitle);
                    TextView btnCancel = dialog.findViewById(R.id.btnCancel);
                    ProgressBar probrBanner = dialog.findViewById(R.id.probrBanner);
                    ImageView imgBanner = dialog.findViewById(R.id.imgBanner);
                    txtTitle.setText(objRewardScreenModel.getHomeDialog().getTitle());
                    TextView txtMessage = dialog.findViewById(R.id.txtMessage);
                    RelativeLayout relPopup = dialog.findViewById(R.id.relPopup);
                    LottieAnimationView ivLottieView = dialog.findViewById(R.id.ivLottieView);
                    txtMessage.setText(objRewardScreenModel.getHomeDialog().getDescription());
                    if (!CommonMethodsUtils.isStringNullOrEmpty(objRewardScreenModel.getHomeDialog().getIsForce()) && objRewardScreenModel.getHomeDialog().getIsForce().equals("1")) {
                        btnCancel.setVisibility(View.GONE);
                    } else {
                        btnCancel.setVisibility(View.VISIBLE);
                    }

                    if (!CommonMethodsUtils.isStringNullOrEmpty(objRewardScreenModel.getHomeDialog().getBtnName())) {
                        btnOk.setText(objRewardScreenModel.getHomeDialog().getBtnName());
                    }

                    if (!CommonMethodsUtils.isStringNullOrEmpty(objRewardScreenModel.getHomeDialog().getImage())) {
                        if (objRewardScreenModel.getHomeDialog().getImage().contains("json")) {
                            probrBanner.setVisibility(View.GONE);
                            imgBanner.setVisibility(View.GONE);
                            ivLottieView.setVisibility(View.VISIBLE);
                            CommonMethodsUtils.setLottieAnimation(ivLottieView, objRewardScreenModel.getHomeDialog().getImage());
                            ivLottieView.setRepeatCount(LottieDrawable.INFINITE);
                        } else {
                            imgBanner.setVisibility(View.VISIBLE);
                            ivLottieView.setVisibility(View.GONE);
                            Glide.with(EarningOptionsActivity.this).load(objRewardScreenModel.getHomeDialog().getImage()).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).addListener(new RequestListener<Drawable>() {
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
                            dialog.dismiss();
                        }
                    });
                    relPopup.setOnClickListener(v -> CommonMethodsUtils.Redirect(EarningOptionsActivity.this, objRewardScreenModel.getHomeDialog().getScreenNo(), objRewardScreenModel.getHomeDialog().getTitle(), objRewardScreenModel.getHomeDialog().getUrl(), objRewardScreenModel.getHomeDialog().getId(), null, objRewardScreenModel.getHomeDialog().getImage()));
                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            CommonMethodsUtils.Redirect(EarningOptionsActivity.this, objRewardScreenModel.getHomeDialog().getScreenNo(), objRewardScreenModel.getHomeDialog().getTitle(), objRewardScreenModel.getHomeDialog().getUrl(), objRewardScreenModel.getHomeDialog().getId(), null, objRewardScreenModel.getHomeDialog().getImage());
                        }
                    });
                    dialog.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (!isTimerSet && !isTimerOver && selectedQuickTaskPos < 0) {
                new GetEanringOptionsScreenAsync(EarningOptionsActivity.this);
            }
            tvPoints.setText(SharePreference.getInstance().getEarningPointString());
            int lastClaimDay = Integer.parseInt(objRewardScreenModel.getDailyBonus().getLastClaimedDay());
            dailyLoginAdapter.setLastClaimedData(lastClaimDay, Integer.parseInt(objRewardScreenModel.getDailyBonus().getIsTodayClaimed()));
            if (lastClaimDay > 3) {
                LinearLayoutManager llm = (LinearLayoutManager) rvDailyLoginList.getLayoutManager();
                llm.scrollToPositionWithOffset(lastClaimDay - 1, 0);
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        try {
            if (isTimerSet && isTimerOver && selectedQuickTaskPos >= 0) {
                new SaveQuickTaskAsync(EarningOptionsActivity.this, quickTasksAdapter.listTasks.get(selectedQuickTaskPos).getPoints(), quickTasksAdapter.listTasks.get(selectedQuickTaskPos).getId());
            }
            isTimerSet = false;
            isTimerOver = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        new GetWalletBalanceAsync(EarningOptionsActivity.this);
    }

    @Override
    public void onBackPressed() {
        try {
            if (objRewardScreenModel != null && objRewardScreenModel.getAppLuck() != null) {
                CommonMethodsUtils.dialogShowAppLuck(EarningOptionsActivity.this, objRewardScreenModel.getAppLuck());
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            super.onBackPressed();
        }
    }

    public void setData(EarningOptionsScreenModel responseModel) {
        objRewardScreenModel = responseModel;

        // Show Default AppLuck
        if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getIsShowAppluck()) && responseMain.getIsShowAppluck().equals("1") && !CommonMethodsUtils.isStringNullOrEmpty(responseModel.getIsDefaultAppluck()) && responseModel.getIsDefaultAppluck().equals("1")) {
            RelativeLayout relMainFlotAppLuck = findViewById(R.id.relMainFlotAppLuck);
            LinearLayout layoutDefaultAd = findViewById(R.id.layoutDefaultAd);
            CommonMethodsUtils.showDefaultAppLuck(EarningOptionsActivity.this, layoutDefaultAd, relMainFlotAppLuck, responseMain.getDefaultAppluckID());
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

        if (!CommonMethodsUtils.isStringNullOrEmpty(responseModel.getIsShowAdjoeLeaderboardIcon()) && responseModel.getIsShowAdjoeLeaderboardIcon().equals("1")) {
            ivAdjoeLeaderboard.setVisibility(View.VISIBLE);
        } else {
            ivAdjoeLeaderboard.setVisibility(View.GONE);
        }
        try {
            if (!CommonMethodsUtils.isStringNullOrEmpty(objRewardScreenModel.getFooterImage())) {
                ImageView ivFooterImage = findViewById(R.id.ivFooterImage);
                Glide.with(EarningOptionsActivity.this).load(objRewardScreenModel.getFooterImage()).into(ivFooterImage);
                ivFooterImage.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getIsShowGiveawayCode()) && responseMain.getIsShowGiveawayCode().equals("1")) {
                layoutGiveawayCode.setVisibility(View.VISIBLE);
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getGiveawayCode())) {
                    lblGiveawayCode.setText("Apply Giveaway Code");
                    tvGiveawayCode.setVisibility(View.VISIBLE);
                    tvGiveawayCode.setText(responseMain.getGiveawayCode());
                } else {
                    lblGiveawayCode.setText("Have a Giveaway Code?");
                    tvGiveawayCode.setVisibility(View.GONE);
                }
                layoutGiveawayCode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(EarningOptionsActivity.this, GiveawayGiftActivity.class));
                    }
                });
            } else {
                layoutGiveawayCode.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (layoutInflate != null) {
            layoutInflate.removeAllViews();
        }

        layoutInflate.setVisibility(View.VISIBLE);

        if (responseModel.getRewardDataList() != null && responseModel.getRewardDataList().size() > 0) {
            for (int i = 0; i < responseModel.getRewardDataList().size(); i++) {
                try {
                    inflateRewardScreenData(responseModel.getRewardDataList().get(i).getType(), responseModel.getRewardDataList().get(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        showHomeDialog();
    }

    public void setupCPX(LinearLayout llSurvey, TextView txtNoSurvey) {
        ApplicationController app = (ApplicationController) getApplication();
        app.getCpxResearch().registerListener(new CPXResearchListener() {
            @Override
            public void onSurveysUpdated() {
                List<SurveyItem> surveys = app.getCpxResearch().getSurveys();
                if (surveys.size() == 0) {
                    txtNoSurvey.setVisibility(View.VISIBLE);
                    llSurvey.setVisibility(View.GONE);
                    txtNoSurvey.setText("Currently no surveys available");
                } else {
                    txtNoSurvey.setVisibility(View.GONE);
                    llSurvey.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onTransactionsUpdated(@NonNull List<TransactionItem> list) {

            }

            @Override
            public void onSurveysDidOpen() {

            }

            @Override
            public void onSurveysDidClose() {

            }

            @Override
            public void onSurveyDidOpen() {

            }

            @Override
            public void onSurveyDidClose() {

            }
        });
        app.getCpxResearch().setSurveyVisibleIfAvailable(false, EarningOptionsActivity.this);

        CPXCardConfiguration cardConfig = new CPXCardConfiguration.Builder()
                .accentColor(Color.parseColor("#6E16E6"))
                .backgroundColor(Color.parseColor("#FFFFFF"))
                .starColor(Color.parseColor("#FF8270"))
                .inactiveStarColor(Color.parseColor("#CAD7DF"))
                .textColor(Color.parseColor("#8E8E93"))
                //.dividerColor(Color.parseColor("#5A7DFE"))
                .cornerRadius(10f)
                .cpxCardStyle(CPXCardStyle.DEFAULT)
                .paddingLeft(15)
                .paddingRight(15)
                .paddingTop(15)
                .paddingBottom(10)
                .fixedCPXCardWidth(120)
                .currencyPrefixImage(R.drawable.ic_points_coin) // set your currency image here!!!
                .hideCurrencyName(true)
                .build();

        app.getCpxResearch().insertCPXResearchCardsIntoContainer(EarningOptionsActivity.this, llSurvey, cardConfig);
        app.getCpxResearch().requestSurveyUpdate(false);
    }

    private void inflateRewardScreenData(String type, HomeDataListItem categoryModel) {
        AppLogger.getInstance().e("EARNING TYPE", "==============" + type);
        switch (type) {
            case Constants.DATA_TYPES.DAILY_TARGET:
                if (categoryModel.getDailyTargetList() != null && categoryModel.getDailyTargetList().size() > 0) {
                    View viewDailyTarget = getLayoutInflater().inflate(R.layout.inflate_home_daily_target, layoutInflate, false);
                    viewDailyTarget.setTag(Constants.DATA_TYPES.DAILY_TARGET);
                    TextView lblMilestones = viewDailyTarget.findViewById(R.id.lblTitle);
                    TextView lblMilestonesSubTitle = viewDailyTarget.findViewById(R.id.lblSubTitle);
                    lblMilestones.setText(categoryModel.getTitle());
                    if (!CommonMethodsUtils.isStringNullOrEmpty(categoryModel.getSubTitle())) {
                        lblMilestonesSubTitle.setText(categoryModel.getSubTitle());
                    } else {
                        lblMilestonesSubTitle.setVisibility(View.GONE);
                    }

                    LinearLayout layoutContent = viewDailyTarget.findViewById(R.id.layoutContent);
                    GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(this, R.drawable.rectangle_white);
                    drawable.mutate(); // only change this instance of the xml, not all components using this xml
                    drawable.setStroke(getResources().getDimensionPixelSize(R.dimen.dim_1_5), Color.parseColor(categoryModel.getIconBGColor())); // set stroke width and stroke color
                    drawable.setColor(Color.parseColor(categoryModel.getBgColor()));
                    layoutContent.setBackground(drawable);

                    TextView tvLabelMilestone = viewDailyTarget.findViewById(R.id.tvLabel);
                    if (!CommonMethodsUtils.isStringNullOrEmpty(categoryModel.getLabel())) {
                        tvLabelMilestone.setText(categoryModel.getLabel());
                        tvLabelMilestone.setVisibility(View.VISIBLE);
                        tvLabelMilestone.startAnimation(blinkAnimation);
                    } else {
                        tvLabelMilestone.setVisibility(View.GONE);
                    }

                    todayDateDailyTarget = categoryModel.getDailyRewardTodayDate();
                    endDateDailyTarget = categoryModel.getDailyRewardEndDate();

                    if (todayDateDailyTarget != null && endDateDailyTarget != null) {
                        tvTimerDailyTarget = viewDailyTarget.findViewById(R.id.tvTimer);
                        setTimerDailyTarget();
                    }

                    RecyclerView rvList = viewDailyTarget.findViewById(R.id.rvList);
                    dailyTargetAdapter = new DailyTargetAdapter(categoryModel.getDailyTargetList(), EarningOptionsActivity.this, new DailyTargetAdapter.ClickListener() {
                        @Override
                        public void onItemClick(int position, View v) {
                            CommonMethodsUtils.setEnableDisable(EarningOptionsActivity.this, v);
                            if (categoryModel.getIsActive() != null && categoryModel.getIsActive().equals("0")) {
                                CommonMethodsUtils.Notify(EarningOptionsActivity.this, getString(R.string.app_name), categoryModel.getNotActiveMessage(), false);
                            } else if (!CommonMethodsUtils.isStringNullOrEmpty(categoryModel.getDailyTargetList().get(position).getIsClaimed()) && categoryModel.getDailyTargetList().get(position).getIsClaimed().equals("0")) {
                                boolean isClaim = false;
                                if (categoryModel.getDailyTargetList().get(position).getType().equals("0"))// number target
                                {
                                    if (Integer.parseInt(categoryModel.getDailyTargetList().get(position).getNoOfCompleted()) >= Integer.parseInt(categoryModel.getDailyTargetList().get(position).getTargetNumber())) {
                                        isClaim = true;
                                    }
                                } else {// points target
                                    if (Integer.parseInt(categoryModel.getDailyTargetList().get(position).getEarnedPoints()) >= Integer.parseInt(categoryModel.getDailyTargetList().get(position).getTargetPoints())) {
                                        isClaim = true;
                                    }
                                }
                                if (isClaim) {
                                    if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                                        selectedDailyTargetPos = position;
                                        new SaveDailyTargetAsync(EarningOptionsActivity.this, categoryModel.getDailyTargetList().get(position).getPoints(), categoryModel.getDailyTargetList().get(position).getId());
                                    } else {
                                        CommonMethodsUtils.NotifyLogin(EarningOptionsActivity.this);
                                    }
                                } else {
                                    CommonMethodsUtils.Redirect(EarningOptionsActivity.this, categoryModel.getDailyTargetList().get(position).getScreenNo(), categoryModel.getDailyTargetList().get(position).getTitle(), null, categoryModel.getDailyTargetList().get(position).getId(), categoryModel.getDailyTargetList().get(position).getId(), categoryModel.getDailyTargetList().get(position).getIcon());
                                }
                            }
                        }

                        @Override
                        public void onClaimClick(int position, View v) {
                            if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                                if (!CommonMethodsUtils.isStringNullOrEmpty(categoryModel.getDailyTargetList().get(position).getIsClaimed()) && categoryModel.getDailyTargetList().get(position).getIsClaimed().equals("0")) {
                                    selectedDailyTargetPos = position;
                                    new SaveDailyTargetAsync(EarningOptionsActivity.this, categoryModel.getDailyTargetList().get(position).getPoints(), categoryModel.getDailyTargetList().get(position).getId());
                                }
                            } else {
                                CommonMethodsUtils.NotifyLogin(EarningOptionsActivity.this);
                            }
                        }
                    });
                    rvList.setAdapter(dailyTargetAdapter);
                    layoutInflate.addView(viewDailyTarget);
                }
                break;
            case Constants.DATA_TYPES.CPX_SURVEY:
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getIsShowSurvey()) && responseMain.getIsShowSurvey().equals("1")) {
                    View viewCPXSurvey = getLayoutInflater().inflate(R.layout.cpx_surveys_inflate, layoutInflate, false);
                    TextView tvTopSurveys = viewCPXSurvey.findViewById(R.id.tvTopSurveys);
                    LinearLayout layoutSurveys = viewCPXSurvey.findViewById(R.id.layoutSurveys);
                    LinearLayout layoutSurveyList = viewCPXSurvey.findViewById(R.id.layoutSurveyList);

                    LinearLayout layoutContent = viewCPXSurvey.findViewById(R.id.layoutContent);
                    GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(this, R.drawable.rectangle_white);
                    drawable.mutate(); // only change this instance of the xml, not all components using this xml
                    drawable.setStroke(getResources().getDimensionPixelSize(R.dimen.dim_1_5), Color.parseColor(categoryModel.getIconBGColor())); // set stroke width and stroke color
                    drawable.setColor(Color.parseColor(categoryModel.getBgColor()));
                    layoutContent.setBackground(drawable);

                    LinearLayout layoutOptions = viewCPXSurvey.findViewById(R.id.layoutOptions);
                    layoutOptions.setBackgroundColor(Color.parseColor(categoryModel.getIconBGColor().replace("#", "#1D")));

                    Drawable bgSelected = ContextCompat.getDrawable(this, R.drawable.bg_survey_selected);
                    bgSelected.setColorFilter(new PorterDuffColorFilter(Color.parseColor(categoryModel.getIconBGColor()), PorterDuff.Mode.SRC_IN));
                    tvTopSurveys.setBackground(bgSelected);

                    TextView txtNoSurvey = viewCPXSurvey.findViewById(R.id.txtNoSurvey);
                    txtNoSurvey.setVisibility(View.VISIBLE);
                    txtNoSurvey.setText("Loading Surveys...");
                    TextView tvSeeSurveyHistory = viewCPXSurvey.findViewById(R.id.tvSeeSurveyHistory);
                    TextView tvTopOffers = viewCPXSurvey.findViewById(R.id.tvTopOffers);
                    RecyclerView rvTopOffers = viewCPXSurvey.findViewById(R.id.rvTopOffers);
                    tvSeeSurveyHistory.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                                startActivity(new Intent(EarningOptionsActivity.this, EarnedPointHistoryActivity.class)
                                        .putExtra("type", Constants.HistoryType.SURVEYS)
                                        .putExtra("title", "Survey History"));
                            } else {
                                CommonMethodsUtils.NotifyLogin(EarningOptionsActivity.this);
                            }
                        }
                    });
                    tvTopSurveys.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            layoutSurveys.setVisibility(View.VISIBLE);
                            rvTopOffers.setVisibility(View.GONE);
                            tvTopSurveys.setBackground(bgSelected);
                            tvTopSurveys.setTextColor(getResources().getColor(R.color.white));

                            tvTopOffers.setBackgroundResource(0);
                            tvTopOffers.setTextColor(getResources().getColor(R.color.black));
                        }
                    });
                    tvTopOffers.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            layoutSurveys.setVisibility(View.GONE);
                            rvTopOffers.setVisibility(View.VISIBLE);

                            tvTopOffers.setBackground(bgSelected);
                            tvTopOffers.setTextColor(getResources().getColor(R.color.white));

                            tvTopSurveys.setBackgroundResource(0);
                            tvTopSurveys.setTextColor(getResources().getColor(R.color.black));
                        }
                    });

                    if (objRewardScreenModel.getTop_offers() != null && objRewardScreenModel.getTop_offers().size() > 0) {
                        TopOffersListAdapter dailyLoginAdapter = new TopOffersListAdapter(EarningOptionsActivity.this, objRewardScreenModel.getTop_offers(), new TopOffersListAdapter.ClickListener() {
                            @Override
                            public void onItemClick(int position, View v) {
                                if (objRewardScreenModel.getTop_offers().get(position).getIsShowDetails() != null && objRewardScreenModel.getTop_offers().get(position).getIsShowDetails().equals("1")) {
                                    Intent intent = new Intent(EarningOptionsActivity.this, TaskInfoActivity.class);
                                    intent.putExtra("taskId", objRewardScreenModel.getTop_offers().get(position).getId());
                                    startActivity(intent);
                                } else {
                                    CommonMethodsUtils.Redirect(EarningOptionsActivity.this, objRewardScreenModel.getTop_offers().get(position).getScreenNo(), objRewardScreenModel.getTop_offers().get(position).getTitle()
                                            , objRewardScreenModel.getTop_offers().get(position).getUrl(), null, objRewardScreenModel.getTop_offers().get(position).getId(), objRewardScreenModel.getTop_offers().get(position).getIcon());
                                }
                            }
                        });
                        rvTopOffers.setLayoutManager(new LinearLayoutManager(EarningOptionsActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        rvTopOffers.setAdapter(dailyLoginAdapter);
                    } else {
                        tvTopOffers.setVisibility(View.GONE);
                        rvTopOffers.setVisibility(View.GONE);
                    }
                    setupCPX(layoutSurveyList, txtNoSurvey);
                    layoutInflate.addView(viewCPXSurvey);
                }
                break;
            case Constants.DATA_TYPES.QUICK_TASK:
                if (categoryModel.getData() != null && categoryModel.getData().size() > 0) {
                    quickTaskView = getLayoutInflater().inflate(R.layout.inflate_quick_tasks, layoutInflate, false);
                    RecyclerView rvSliderlist = quickTaskView.findViewById(R.id.rvData);
                    TextView txtHeader = quickTaskView.findViewById(R.id.txtTitleHeader);

                    if (categoryModel.getTitle() != null && !categoryModel.getTitle().isEmpty()) {
                        txtHeader.setVisibility(View.VISIBLE);
                        txtHeader.setText(categoryModel.getTitle());
                    } else {
                        txtHeader.setVisibility(View.GONE);
                    }

                    LinearLayout layoutContent = quickTaskView.findViewById(R.id.layoutContent);
                    GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(this, R.drawable.rectangle_white);
                    drawable.mutate(); // only change this instance of the xml, not all components using this xml
                    drawable.setStroke(getResources().getDimensionPixelSize(R.dimen.dim_1_5), Color.parseColor(categoryModel.getIconBGColor())); // set stroke width and stroke color
                    drawable.setColor(Color.parseColor(categoryModel.getBgColor()));
                    layoutContent.setBackground(drawable);

//                    CardView cardContent = quickTaskView.findViewById(R.id.cardContent);
//                    cardContent.setCardBackgroundColor(Color.parseColor(categoryModel.getBgColor()));
                    quickTasksAdapter = new QuickTasksAdapter(categoryModel.getData(), EarningOptionsActivity.this, new QuickTasksAdapter.ClickListener() {
                        @Override
                        public void onItemClick(int position, View v) {
                            if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                                selectedQuickTaskPos = position;
                                ActivityManager.isShowAppOpenAd = false;
                                CommonMethodsUtils.Redirect(EarningOptionsActivity.this, categoryModel.getData().get(position).getScreenNo(), categoryModel.getData().get(position).getTitle(), categoryModel.getData().get(position).getUrl(), categoryModel.getData().get(position).getId(), categoryModel.getData().get(position).getTaskId(), categoryModel.getData().get(position).getImage());
                                startQuickTaskTimer(categoryModel.getData(), position);
                            } else {
                                CommonMethodsUtils.NotifyLogin(EarningOptionsActivity.this);
                            }
                        }
                    });
                    rvSliderlist.setLayoutManager(new LinearLayoutManager(EarningOptionsActivity.this, LinearLayoutManager.VERTICAL, false));
                    rvSliderlist.setAdapter(quickTasksAdapter);
                    layoutInflate.addView(quickTaskView);
                }
                break;
            case Constants.DATA_TYPES.LIVE_CONTEST:
                View viewLiveContest = getLayoutInflater().inflate(R.layout.inflate_home_live_contest, layoutInflate, false);
                TextView tvLabel1 = viewLiveContest.findViewById(R.id.tvLabel);
                View viewShineHeader = viewLiveContest.findViewById(R.id.viewShineHeader);
                Animation animShine = AnimationUtils.loadAnimation(EarningOptionsActivity.this, R.anim.left_right_duration);
                animShine.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        viewShineHeader.startAnimation(animShine);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                viewShineHeader.setVisibility(View.VISIBLE);
                viewShineHeader.startAnimation(animShine);
                if (!CommonMethodsUtils.isStringNullOrEmpty(categoryModel.getLabel())) {
                    tvLabel1.setText(categoryModel.getLabel());
                    tvLabel1.setVisibility(View.VISIBLE);
                    if (!CommonMethodsUtils.isStringNullOrEmpty(categoryModel.getIsBlink()) && categoryModel.getIsBlink().equals("1")) {
                        tvLabel1.startAnimation(blinkAnimation);
                    }
                } else {
                    tvLabel1.setVisibility(View.GONE);
                }
                LinearLayout layoutYourRank = viewLiveContest.findViewById(R.id.layoutYourRank);
                layoutYourRank.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            startActivity(new Intent(EarningOptionsActivity.this, AdjoeLeaderboardActivity.class));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                LinearLayout layoutContentClick = viewLiveContest.findViewById(R.id.layoutContentClick);
                layoutContentClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            CommonMethodsUtils.setEnableDisable(EarningOptionsActivity.this, view);
                            CommonMethodsUtils.Redirect(EarningOptionsActivity.this, categoryModel.getScreenNo(), categoryModel.getTitle(), categoryModel.getUrl(), categoryModel.getId(), categoryModel.getTaskId(), categoryModel.getImage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

//                CardView cardContent1 = viewLiveContest.findViewById(R.id.cardContent);
//                cardContent1.setCardBackgroundColor(Color.parseColor(categoryModel.getBgColor()));

                LinearLayout layoutContent1 = viewLiveContest.findViewById(R.id.layoutContent);
                TextView lblMilestonesSubTitle1 = viewLiveContest.findViewById(R.id.lblSubTitle);

                if (!CommonMethodsUtils.isStringNullOrEmpty(categoryModel.getBgColor())) {
                    GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(this, R.drawable.rectangle_white);
                    drawable.mutate(); // only change this instance of the xml, not all components using this xml
                    drawable.setStroke(getResources().getDimensionPixelSize(R.dimen.dim_1_5), Color.parseColor(categoryModel.getIconBGColor())); // set stroke width and stroke color
                    drawable.setColor(Color.parseColor(categoryModel.getBgColor()));
                    layoutContent1.setBackground(drawable);

                    Drawable mDrawable2 = ContextCompat.getDrawable(EarningOptionsActivity.this, R.drawable.ad_live_leaderboard_sub_title);
                    mDrawable2.setColorFilter(new PorterDuffColorFilter(Color.parseColor(categoryModel.getIconBGColor()), PorterDuff.Mode.SRC_IN));
                    lblMilestonesSubTitle1.setBackground(mDrawable2);
                }
                TextView lblMilestones1 = viewLiveContest.findViewById(R.id.lblTitle);

                lblMilestones1.setText(categoryModel.getTitle());
                if (!CommonMethodsUtils.isStringNullOrEmpty(categoryModel.getSubTitle())) {
                    lblMilestonesSubTitle1.setText(categoryModel.getSubTitle());
                } else {
                    lblMilestonesSubTitle1.setVisibility(View.GONE);
                }
                if (!CommonMethodsUtils.isStringNullOrEmpty(categoryModel.getLottieBgUrl())) {
                    LottieAnimationView lottieBg = viewLiveContest.findViewById(R.id.lottieBg);
                    lottieBg.setVisibility(View.VISIBLE);
                    CommonMethodsUtils.setLottieAnimation(lottieBg, categoryModel.getLottieBgUrl());
                }
                TextView tvYourRank = viewLiveContest.findViewById(R.id.tvYourRank);
                TextView lblPoints = viewLiveContest.findViewById(R.id.lblPoints);
                TextView tvPointsLiveContest = viewLiveContest.findViewById(R.id.tvPoints);

                tvPointsLiveContest.setText(categoryModel.getWinningPoints());
                tvYourRank.setText(categoryModel.getLeaderboardRank());
                try {
                    if (Integer.parseInt(categoryModel.getLeaderboardRank()) > 3) {
                        lblPoints.setText("Chance to Win");
                    } else {
                        lblPoints.setText("Win Points");
                    }
                } catch (NumberFormatException e) {
                    lblPoints.setText("Chance to Win");
                }
                layoutInflate.addView(viewLiveContest);
                break;
            case Constants.DATA_TYPES.LIVE_MILESTONES:
                if (categoryModel.getMilestoneData() != null && categoryModel.getMilestoneData().size() > 0) {
                    View viewMilestones = getLayoutInflater().inflate(R.layout.inflate_reward_live_milestones, layoutInflate, false);
                    setDetailsView(viewMilestones, type, categoryModel);

                    TextView lblMilestones = viewMilestones.findViewById(R.id.lblTitle);
                    TextView lblMilestonesSubTitle = viewMilestones.findViewById(R.id.lblSubTitle);
                    lblMilestones.setText(categoryModel.getTitle());
                    if (!CommonMethodsUtils.isStringNullOrEmpty(categoryModel.getSubTitle())) {
                        lblMilestonesSubTitle.setText(categoryModel.getSubTitle());
                    } else {
                        lblMilestonesSubTitle.setVisibility(View.GONE);
                    }

                    TextView tvViewAll = viewMilestones.findViewById(R.id.tvViewAll);
                    tvViewAll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(EarningOptionsActivity.this, MilestoneTargetListActivity.class));
                        }
                    });

                    RecyclerView rvList = viewMilestones.findViewById(R.id.rvList);
                    LiveMilestonesAdapter milestoneAdapter = new LiveMilestonesAdapter(categoryModel.getMilestoneData(), EarningOptionsActivity.this, categoryModel.getIconBGColor(), new LiveMilestonesAdapter.ClickListener() {
                        @Override
                        public void onItemClick(int position, View v) {
                            CommonMethodsUtils.setEnableDisable(EarningOptionsActivity.this, v);
                            if (categoryModel.getIsActive() != null && categoryModel.getIsActive().equals("0")) {
                                CommonMethodsUtils.Notify(EarningOptionsActivity.this, getString(R.string.app_name), categoryModel.getNotActiveMessage(), false);
                            } else {
                                boolean isClaim = false;
                                if (categoryModel.getMilestoneData().get(position).getType().equals("0"))// number target
                                {
                                    if (Integer.parseInt(categoryModel.getMilestoneData().get(position).getNoOfCompleted()) >= Integer.parseInt(categoryModel.getMilestoneData().get(position).getTargetNumber())) {
                                        isClaim = true;
                                    }
                                } else {// points target
                                    if (Integer.parseInt(categoryModel.getMilestoneData().get(position).getEarnedPoints()) >= Integer.parseInt(categoryModel.getMilestoneData().get(position).getTargetPoints())) {
                                        isClaim = true;
                                    }
                                }
                                if (isClaim) {
                                    if (!CommonMethodsUtils.isStringNullOrEmpty(categoryModel.getMilestoneData().get(position).getIsShowDetails()) && categoryModel.getMilestoneData().get(position).getIsShowDetails().equals("1")) {
                                        Intent intent = new Intent(EarningOptionsActivity.this, MilestoneTargetDetailsActivity.class).putExtra("milestoneId", categoryModel.getMilestoneData().get(position).getId());
                                        startActivity(intent);
                                    }
                                } else {
                                    CommonMethodsUtils.Redirect(EarningOptionsActivity.this, categoryModel.getMilestoneData().get(position).getScreenNo(), categoryModel.getMilestoneData().get(position).getTitle(), null, categoryModel.getMilestoneData().get(position).getId(), categoryModel.getMilestoneData().get(position).getId(), categoryModel.getMilestoneData().get(position).getIcon());
                                }
                            }
                        }
                    });
                    rvList.setAdapter(milestoneAdapter);
                    layoutInflate.addView(viewMilestones);
                }
                break;
            case Constants.DATA_TYPES.DAILY_BONUS:
                View viewDailyLogin = getLayoutInflater().inflate(R.layout.inflate_reward_daily_login, layoutInflate, false);
                setDetailsView(viewDailyLogin, type, categoryModel);
                TextView lblDailyLogin = viewDailyLogin.findViewById(R.id.lblTitle);
                TextView lblDailyLoginSubTitle = viewDailyLogin.findViewById(R.id.lblSubTitle);
                lblDailyLogin.setText(categoryModel.getTitle());
                LinearLayout layoutViewDetails = viewDailyLogin.findViewById(R.id.layoutViewDetails);
                if (!CommonMethodsUtils.isStringNullOrEmpty(categoryModel.getSubTitle())) {
                    lblDailyLoginSubTitle.setText(categoryModel.getSubTitle());
                    layoutViewDetails.setVisibility(View.VISIBLE);
                } else {
                    layoutViewDetails.setVisibility(View.GONE);
                }

                rvDailyLoginList = viewDailyLogin.findViewById(R.id.rvDailyLoginList);
                if (objRewardScreenModel.getDailyBonus() != null && objRewardScreenModel.getDailyBonus().getData() != null && objRewardScreenModel.getDailyBonus().getData().size() > 0) {
                    int lastClaimDay = Integer.parseInt(objRewardScreenModel.getDailyBonus().getLastClaimedDay());
                    dailyLoginAdapter = new EverydayCheckinAdapter(objRewardScreenModel.getDailyBonus().getData(), EarningOptionsActivity.this, lastClaimDay, Integer.parseInt(objRewardScreenModel.getDailyBonus().getIsTodayClaimed()), categoryModel.getIconBGColor(), new EverydayCheckinAdapter.ClickListener() {
                        @Override
                        public void onItemClick(int position, View v) {
                            CommonMethodsUtils.setEnableDisable(EarningOptionsActivity.this, v);
                            if (!CommonMethodsUtils.isStringNullOrEmpty(categoryModel.getIsTodayTaskCompleted()) && categoryModel.getIsTodayTaskCompleted().equals("1")) {
                                if (categoryModel.getIsActive() != null && categoryModel.getIsActive().equals("0")) {
                                    CommonMethodsUtils.Notify(EarningOptionsActivity.this, getString(R.string.app_name), categoryModel.getNotActiveMessage(), false);
                                } else {
                                    startActivity(new Intent(EarningOptionsActivity.this, EverydayCheckinActivity.class)
                                            .putExtra("objRewardScreenModel", objRewardScreenModel)
                                            .putExtra("title", categoryModel.getTitle())
                                            .putExtra("subTitle", categoryModel.getSubTitle())
                                            .putExtra("bgColor", categoryModel.getIconBGColor()));
                                }
                            } else {
                                showTaskPopup(categoryModel);
                            }
                        }
                    });
                    rvDailyLoginList.setAdapter(dailyLoginAdapter);
                    if (lastClaimDay > 3) {
                        LinearLayoutManager llm = (LinearLayoutManager) rvDailyLoginList.getLayoutManager();
                        llm.scrollToPositionWithOffset(lastClaimDay - 1, 0);
                    }
                } else {
                    lblDailyLogin.setVisibility(View.GONE);
                    rvDailyLoginList.setVisibility(View.GONE);
                }
                layoutInflate.addView(viewDailyLogin);
                break;
            case Constants.DATA_TYPES.SINGLE_SLIDER:
                View iconSingleSlider = getLayoutInflater().inflate(R.layout.inflate_home_general_layout, layoutInflate, false);
                RecyclerView rvSliderlist = iconSingleSlider.findViewById(R.id.rvIconlist);
                TextView txtHeader = iconSingleSlider.findViewById(R.id.txtTitleHeader);

                if (categoryModel.getTitle() != null && !categoryModel.getTitle().isEmpty()) {
                    txtHeader.setVisibility(View.VISIBLE);
                    txtHeader.setText(categoryModel.getTitle());
                } else {
                    txtHeader.setVisibility(View.GONE);
                }
                ////Loge("Size--)", "" + categoryModel.getData().size());
                SingleSliderImageAdapter homeSingleSilderAdapter = new SingleSliderImageAdapter(EarningOptionsActivity.this, categoryModel.getData(), false, new SingleSliderImageAdapter.ClickListener() {
                    @Override
                    public void onItemClick(int position, View v) {
                        if (categoryModel.getIsActive() != null && categoryModel.getIsActive().equals("0")) {
                            CommonMethodsUtils.Notify(EarningOptionsActivity.this, getString(R.string.app_name), categoryModel.getNotActiveMessage(), false);
                        } else {
                            CommonMethodsUtils.Redirect(EarningOptionsActivity.this, categoryModel.getData().get(position).getScreenNo(), categoryModel.getData().get(position).getTitle(), categoryModel.getData().get(position).getUrl(), categoryModel.getData().get(position).getId(), categoryModel.getData().get(position).getTaskId(), categoryModel.getData().get(position).getImage());
                        }
                    }
                });
                rvSliderlist.setLayoutManager(new LinearLayoutManager(EarningOptionsActivity.this, LinearLayoutManager.VERTICAL, false));
                rvSliderlist.setAdapter(homeSingleSilderAdapter);
                layoutInflate.addView(iconSingleSlider);
                break;
            case Constants.DATA_TYPES.NATIVE_AD:
                View viewNativeAd = getLayoutInflater().inflate(R.layout.inflate_native_ad, layoutInflate, false);
                FrameLayout fl_adplaceholder = viewNativeAd.findViewById(R.id.fl_adplaceholder);
                TextView lblLoadingAds = viewNativeAd.findViewById(R.id.lblLoadingAds);
                if (CommonMethodsUtils.isShowAppLovinNativeAds()) {
                    loadAppLovinNativeAds(fl_adplaceholder, lblLoadingAds);
                    layoutInflate.addView(viewNativeAd);
                }
                break;
            case Constants.DATA_TYPES.GRID:
                try {
                    View viewGrid = getLayoutInflater().inflate(R.layout.inflate_reward_grid, layoutInflate, false);
                    RecyclerView rvList = viewGrid.findViewById(R.id.rvList);
                    EarningOptionsGridAdapter gridAdapter = new EarningOptionsGridAdapter(EarningOptionsActivity.this, blinkAnimation, categoryModel.getGridData(), new EarningOptionsGridAdapter.ClickListener() {
                        @Override
                        public void onItemClick(int position, View v) {
                            try {
                                performItemClick(v, categoryModel.getGridData().get(position));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    GridLayoutManager mGridLayoutManager = new GridLayoutManager(EarningOptionsActivity.this, Integer.parseInt(categoryModel.getColumnCount()));
                    mGridLayoutManager.setOrientation(RecyclerView.VERTICAL);
                    rvList.setLayoutManager(mGridLayoutManager);
                    rvList.setAdapter(gridAdapter);
                    layoutInflate.addView(viewGrid);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                View spinView;
                if (categoryModel.getType().equals(Constants.DATA_TYPES.DAILY_REWARDS_CHALLENGE)) {
                    spinView = getLayoutInflater().inflate(R.layout.inflate_reward_daily_rewards, layoutInflate, false);
                } else {
                    spinView = getLayoutInflater().inflate(R.layout.inflate_reward_spin, layoutInflate, false);
                }
                if (!CommonMethodsUtils.isStringNullOrEmpty(categoryModel.getFullImage())) {
                    CardView cardContent = spinView.findViewById(R.id.cardContent);
                    cardContent.setVisibility(View.GONE);
                    ImageView ivIconFullImage = spinView.findViewById(R.id.ivIconFullImage);
                    LottieAnimationView ivLottieFullImage = spinView.findViewById(R.id.ivLottieFullImage);
                    ProgressBar progressBarFullImage = spinView.findViewById(R.id.progressBarFullImage);
                    if (categoryModel.getFullImage().contains(".json")) {
                        ivIconFullImage.setVisibility(View.GONE);
                        ivLottieFullImage.setVisibility(View.VISIBLE);
                        CommonMethodsUtils.setLottieAnimation(ivLottieFullImage, categoryModel.getFullImage());
                        ivLottieFullImage.setRepeatCount(LottieDrawable.INFINITE);
                        progressBarFullImage.setVisibility(View.GONE);
                    } else {
                        ivIconFullImage.setVisibility(View.VISIBLE);
                        ivLottieFullImage.setVisibility(View.GONE);
                        Glide.with(EarningOptionsActivity.this).load(categoryModel.getFullImage()).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                                progressBarFullImage.setVisibility(View.GONE);
                                return false;
                            }
                        }).into(ivIconFullImage);
                    }
                    CardView cardFullImage = spinView.findViewById(R.id.cardFullImage);
                    cardFullImage.setVisibility(View.VISIBLE);
                    cardFullImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                performItemClick(view, categoryModel);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    setDetailsView(spinView, type, categoryModel);
                    RelativeLayout layoutIcon = spinView.findViewById(R.id.layoutIcon);

                    TextView lblSpin = spinView.findViewById(R.id.lblTitle);
                    ProgressBar probr = spinView.findViewById(R.id.probr);
                    TextView lblSpinSubTitle = spinView.findViewById(R.id.lblSubTitle);
                    LottieAnimationView ivLottieView = spinView.findViewById(R.id.ivLottie);
                    ivLottieView.setSpeed(0.6f);
                    if (!CommonMethodsUtils.isStringNullOrEmpty(categoryModel.getIconBGColor())) {
                        Drawable mDrawable = ContextCompat.getDrawable(EarningOptionsActivity.this, R.drawable.rectangle_white);
                        mDrawable.setColorFilter(new PorterDuffColorFilter(Color.parseColor(categoryModel.getIconBGColor()), PorterDuff.Mode.SRC_IN));
                        layoutIcon.setBackground(mDrawable);
                    }

                    ImageView ivIcon = spinView.findViewById(R.id.ivIcon);
                    if (categoryModel.getIcon() != null) {
                        if (categoryModel.getIcon().contains(".json")) {
                            ivIcon.setVisibility(View.GONE);
                            ivLottieView.setVisibility(View.VISIBLE);
                            CommonMethodsUtils.setLottieAnimation(ivLottieView, categoryModel.getIcon());
                            ivLottieView.setRepeatCount(LottieDrawable.INFINITE);
                            probr.setVisibility(View.GONE);
                        } else {
                            ivIcon.setVisibility(View.VISIBLE);
                            ivLottieView.setVisibility(View.GONE);
                            Glide.with(EarningOptionsActivity.this).load(categoryModel.getIcon()).override(getResources().getDimensionPixelSize(R.dimen.dim_50)).listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                                    probr.setVisibility(View.GONE);
                                    return false;
                                }
                            }).into(ivIcon);
                        }
                    }

                    if (categoryModel.getType().equals(Constants.DATA_TYPES.DAILY_REWARDS_CHALLENGE)) {
                        todayDate = categoryModel.getDailyRewardTodayDate();
                        endDate = categoryModel.getDailyRewardEndDate();
                        LinearLayout layoutPoints = spinView.findViewById(R.id.layoutPoints);
                        layoutPoints.setVisibility(View.VISIBLE);

                        if (todayDate != null && endDate != null) {
                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) lblSpinSubTitle.getLayoutParams();
                            params.setMargins(0, getResources().getDimensionPixelSize(R.dimen.dim_5), getResources().getDimensionPixelSize(R.dimen.dim_10), getResources().getDimensionPixelSize(R.dimen.dim_8));
                            lblSpinSubTitle.setLayoutParams(params);
                            TextView tvPoints = spinView.findViewById(R.id.tvPoints);
                            tvPoints.setText(categoryModel.getDailyRewardPoints());
                            layoutTimer = spinView.findViewById(R.id.layoutTimer);
                            layoutTimer.setVisibility(View.VISIBLE);
                            tvTimer = spinView.findViewById(R.id.tvTimer);
                            setTimer();
                        }
                    }
                    lblSpin.setText(categoryModel.getTitle());
                    lblSpinSubTitle.setText(categoryModel.getSubTitle());
                }
                layoutInflate.addView(spinView);
                break;
        }

    }

    private void setTimerDailyTarget() {
        try {
            if (timerDailyTarget != null) {
                timerDailyTarget.cancel();
            }
            timeDailyTarget = CommonMethodsUtils.timeDiff(endDateDailyTarget, todayDateDailyTarget);
            timerDailyTarget = new CountDownTimer(timeDailyTarget * 60000L, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    tvTimerDailyTarget.setText(CommonMethodsUtils.updateTimeRemainingLuckyNumber(millisUntilFinished));
                }

                @Override
                public void onFinish() {
                    tvTimerDailyTarget.setText("00:00:00");
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startQuickTaskTimer(List<HomeDataItem> data, int position) {
        if (timerQuickTask != null) {
            timerQuickTask.cancel();
        }
        timerQuickTask = new CountDownTimer(Integer.parseInt(data.get(position).getDelay()) * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                isTimerOver = true;
                timerQuickTask.cancel();
                timerQuickTask = null;
            }
        };
        isTimerSet = true;
        timerQuickTask.start();
    }

    private void setDetailsView(View spinView, String type, HomeDataListItem categoryModel) {
        TextView tvLabel = spinView.findViewById(R.id.tvLabel);
        if (!CommonMethodsUtils.isStringNullOrEmpty(categoryModel.getLabel())) {
            tvLabel.setText(categoryModel.getLabel());
            tvLabel.setVisibility(View.VISIBLE);
            tvLabel.startAnimation(blinkAnimation);
        } else {
            tvLabel.setVisibility(View.GONE);
        }

//        CardView cardContent = spinView.findViewById(R.id.cardContent);
//        cardContent.setCardBackgroundColor(Color.parseColor(categoryModel.getIconBGColor()));

        LinearLayout layoutContent = spinView.findViewById(R.id.layoutContent);
        if (!CommonMethodsUtils.isStringNullOrEmpty(categoryModel.getBgColor())) {
            GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(this, R.drawable.rectangle_white);
            drawable.mutate(); // only change this instance of the xml, not all components using this xml
            drawable.setStroke(getResources().getDimensionPixelSize(R.dimen.dim_1_5), Color.parseColor(categoryModel.getIconBGColor())); // set stroke width and stroke color
            drawable.setColor(Color.parseColor(categoryModel.getBgColor()));
            layoutContent.setBackground(drawable);
        }
        try {
            ImageView ivLock = spinView.findViewById(R.id.ivLock);
            if (!CommonMethodsUtils.isStringNullOrEmpty(categoryModel.getIsTodayTaskCompleted()) && categoryModel.getIsTodayTaskCompleted().equals("0")) {
                ivLock.setVisibility(View.VISIBLE);
                Typeface typefaceMedium = ResourcesCompat.getFont(EarningOptionsActivity.this, R.font.noto_medium);
                ivLock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            balloon = new Balloon.Builder(EarningOptionsActivity.this).setArrowSize(10).setArrowOrientation(ArrowOrientation.TOP).setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR).setArrowPosition(0.5f).setWidth(BalloonSizeSpec.WRAP).setHeight(65).setTextSize(15f).setPaddingHorizontal(10).setCornerRadius(4f).setAlpha(0.9f).setTextTypeface(typefaceMedium).setText("Complete <font color='#FFCC66'>" + categoryModel.getTaskCount() + " Tasks </font> to <font color='#FFCC66'>UNLOCK</font> " + categoryModel.getTitle()).setTextColor(ContextCompat.getColor(EarningOptionsActivity.this, R.color.white)).setTextIsHtml(true).setBackgroundColor(ContextCompat.getColor(EarningOptionsActivity.this, R.color.black_transparent)).setOnBalloonClickListener(new OnBalloonClickListener() {
                                @Override
                                public void onBalloonClick(@NonNull View view) {
                                    balloon.dismiss();
                                }
                            }).setBalloonAnimation(BalloonAnimation.FADE).setLifecycleOwner(EarningOptionsActivity.this).build();
                            balloon.showAlignBottom(view);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                ivLock.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            LinearLayout layoutViewDetails = spinView.findViewById(R.id.layoutViewDetails);
            if (!categoryModel.getType().equals(Constants.DATA_TYPES.DAILY_BONUS)) {
                LinearLayout layoutContentClick = spinView.findViewById(R.id.layoutContentClick);
                layoutContentClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        performItemClick(view, categoryModel);
                    }
                });
                layoutViewDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        performItemClick(view, categoryModel);
                    }
                });
            }
            layoutViewDetails.setBackgroundColor(Color.parseColor(categoryModel.getIconBGColor()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            TextView tvNote = spinView.findViewById(R.id.tvNote);
            tvNote.setText(categoryModel.getNote());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void performItemClick(View v, HomeDataListItem categoryModel) {
        try {
            CommonMethodsUtils.setEnableDisable(EarningOptionsActivity.this, v);
            if (!CommonMethodsUtils.isStringNullOrEmpty(categoryModel.getIsTodayTaskCompleted()) && categoryModel.getIsTodayTaskCompleted().equals("1")) {
                if (categoryModel.getIsActive() != null && categoryModel.getIsActive().equals("0")) {
                    CommonMethodsUtils.Notify(EarningOptionsActivity.this, getString(R.string.app_name), categoryModel.getNotActiveMessage(), false);
                } else {
                    if (categoryModel.getType().equals(Constants.DATA_TYPES.DAILY_BONUS)) {
                        startActivity(new Intent(EarningOptionsActivity.this, EverydayCheckinActivity.class).putExtra("objRewardScreenModel", objRewardScreenModel).putExtra("title", categoryModel.getTitle()).putExtra("subTitle", categoryModel.getSubTitle()));
                    } else {
                        CommonMethodsUtils.Redirect(EarningOptionsActivity.this, categoryModel.getScreenNo(), categoryModel.getTitle(), categoryModel.getUrl(), categoryModel.getId(), categoryModel.getTaskId(), categoryModel.getImage());
                    }
                }
            } else {
                showTaskPopup(categoryModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showTaskPopup(HomeDataListItem categoryModel) {
        try {
            Dialog dialogTaskList = new Dialog(EarningOptionsActivity.this, android.R.style.Theme_Light);
            dialogTaskList.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
            dialogTaskList.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogTaskList.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            dialogTaskList.setCancelable(true);
            dialogTaskList.setCanceledOnTouchOutside(true);
            dialogTaskList.setContentView(R.layout.popup_complete_task);

            RecyclerView rvTasks = dialogTaskList.findViewById(R.id.rvTasks);
            TextView tvDescription = dialogTaskList.findViewById(R.id.tvDescription);
            if (!CommonMethodsUtils.isStringNullOrEmpty(objRewardScreenModel.getTodayCompletedTask())) {
                int completed = Integer.parseInt(objRewardScreenModel.getTodayCompletedTask());
                if (completed > 0) {
                    tvDescription.setText("Complete " + (Integer.parseInt(categoryModel.getTaskCount()) - completed) + " more easy tasks to unlock " + categoryModel.getTitle());
                } else {
                    tvDescription.setText("Complete " + categoryModel.getTaskCount() + " easy tasks to unlock " + categoryModel.getTitle());
                }
            } else {
                tvDescription.setText("Complete " + categoryModel.getTaskCount() + " easy tasks to unlock " + categoryModel.getTitle());
            }
            if (objRewardScreenModel.getTaskList() != null && objRewardScreenModel.getTaskList().size() > 0) {
                HomeTasksListAdapter taskListAdapter = new HomeTasksListAdapter(objRewardScreenModel.getTaskList(), EarningOptionsActivity.this, new HomeTasksListAdapter.ClickListener() {
                    @Override
                    public void onItemClick(int position, View v) {
                        dialogTaskList.dismiss();
                        CommonMethodsUtils.Redirect(EarningOptionsActivity.this, objRewardScreenModel.getTaskList().get(position).getScreenNo(), objRewardScreenModel.getTaskList().get(position).getTitle(), objRewardScreenModel.getTaskList().get(position).getUrl(), objRewardScreenModel.getTaskList().get(position).getId(), objRewardScreenModel.getTaskList().get(position).getTaskId(), objRewardScreenModel.getTaskList().get(position).getImage());
                    }
                });
                rvTasks.setLayoutManager(new LinearLayoutManager(EarningOptionsActivity.this, LinearLayoutManager.VERTICAL, false));
                rvTasks.setAdapter(taskListAdapter);
            } else {
                rvTasks.setVisibility(View.GONE);
            }

            Button btnViewAll = dialogTaskList.findViewById(R.id.btnViewAll);
            btnViewAll.setOnClickListener(v -> {
                dialogTaskList.dismiss();
                startActivity(new Intent(EarningOptionsActivity.this, TaskListActivity.class));
            });

            dialogTaskList.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTimer() {
        try {
            if (timer != null) {
                timer.cancel();
            }
            time = CommonMethodsUtils.timeDiff(endDate, todayDate);
            timer = new CountDownTimer(time * 60000L, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    tvTimer.setText(CommonMethodsUtils.updateTimeRemainingLuckyNumber(millisUntilFinished));
                }

                @Override
                public void onFinish() {
                    tvTimer.setText("");
                    layoutTimer.setVisibility(View.GONE);
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAppLovinNativeAds(FrameLayout frameLayout, TextView lblLoadingAds) {
        try {
            nativeAdLoader = new MaxNativeAdLoader(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinNativeID()), EarningOptionsActivity.this);
            nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
                @Override
                public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad) {
                    if (nativeAd != null) {
                        nativeAdLoader.destroy(nativeAd);
                    }
                    nativeAd = ad;
                    frameLayout.removeAllViews();
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
                    params.height = getResources().getDimensionPixelSize(R.dimen.dim_300);
                    params.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    frameLayout.setLayoutParams(params);
                    frameLayout.setPadding((int) getResources().getDimension(R.dimen.dim_10), (int) getResources().getDimension(R.dimen.dim_10), (int) getResources().getDimension(R.dimen.dim_10), (int) getResources().getDimension(R.dimen.dim_10));
                    frameLayout.addView(nativeAdView);
                    frameLayout.setVisibility(View.VISIBLE);
                    lblLoadingAds.setVisibility(View.GONE);
                    //AppLogger.getInstance().e("AppLovin Loaded: ", "===");
                }

                @Override
                public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
                    frameLayout.setVisibility(View.GONE);
                    lblLoadingAds.setVisibility(View.GONE);
                    //AppLogger.getInstance().e("AppLovin Failed: ", error.getMessage());
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
                removeAds();
                if (timer != null) {
                    timer.cancel();
                }
                if (timerQuickTask != null) {
                    timerQuickTask.cancel();
                }
                if (timerDailyTarget != null) {
                    timerDailyTarget.cancel();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void removeAds() {
//        //AppLogger.getInstance().e("removeAds", "removeAds");
        try {
            if (nativeAd != null && nativeAdLoader != null) {
                nativeAdLoader.destroy(nativeAd);
                nativeAd = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateQuickTask(boolean isSuccess, String id) {
        try {
            if (isSuccess) {
                for (int i = 0; i < quickTasksAdapter.listTasks.size(); i++) {
                    if (quickTasksAdapter.listTasks.get(i).getId().equals(id)) {
                        quickTasksAdapter.listTasks.remove(i);
                        quickTasksAdapter.notifyDataSetChanged();
                        break;
                    }
                }
                tvPoints.setText(SharePreference.getInstance().getEarningPointString());
                if (quickTasksAdapter.listTasks.size() == 0) {
                    layoutInflate.removeView(quickTaskView);
                }
                Intent i = new Intent();
                i.putExtra("isSuccess", isSuccess);
                i.putExtra("id", id);
                setResult(RESULT_OK, i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        selectedQuickTaskPos = -1;
    }

    public void onUpdateWalletBalance() {
        tvPoints.setText(SharePreference.getInstance().getEarningPointString());
    }

    public void changeDailyTargetValues(boolean isSuccess) {
        try {
            if (isSuccess) {
                dailyTargetAdapter.listMilestones.get(selectedDailyTargetPos).setIsClaimed("1");
                dailyTargetAdapter.notifyItemChanged(selectedDailyTargetPos);
                tvPoints.setText(SharePreference.getInstance().getEarningPointString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        selectedDailyTargetPos = -1;
    }

}