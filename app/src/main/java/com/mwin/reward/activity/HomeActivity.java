package com.mwin.reward.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
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
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.makeopinion.cpxresearchlib.CPXResearchListener;
import com.makeopinion.cpxresearchlib.models.CPXCardConfiguration;
import com.makeopinion.cpxresearchlib.models.CPXCardStyle;
import com.makeopinion.cpxresearchlib.models.SurveyItem;
import com.makeopinion.cpxresearchlib.models.TransactionItem;
import com.mwin.reward.ApplicationController;
import com.mwin.reward.R;
import com.mwin.reward.adapter.DailyTargetAdapter;
import com.mwin.reward.adapter.DrawerMenuAdapter;
import com.mwin.reward.adapter.DrawerMenuChildView;
import com.mwin.reward.adapter.DrawerMenuParentView;
import com.mwin.reward.adapter.EarningOptionsGridAdapter;
import com.mwin.reward.adapter.HomeGridAdapter;
import com.mwin.reward.adapter.HomeTasksListAdapter;
import com.mwin.reward.adapter.LiveMilestonesAdapter;
import com.mwin.reward.adapter.QuickTasksAdapter;
import com.mwin.reward.adapter.SingleBigTaskAdapter;
import com.mwin.reward.adapter.SingleSliderImageAdapter;
import com.mwin.reward.adapter.StoryListAdapter;
import com.mwin.reward.adapter.TopOffersListAdapter;
import com.mwin.reward.async.GetWalletBalanceAsync;
import com.mwin.reward.async.MainDataAsync;
import com.mwin.reward.async.SaveDailyTargetAsync;
import com.mwin.reward.async.SavePackageInstallDataAsync;
import com.mwin.reward.async.SaveQuickTaskAsync;
import com.mwin.reward.async.SendNewAppPackageDataAsync;
import com.mwin.reward.async.models.HomeDataItem;
import com.mwin.reward.async.models.HomeDataListItem;
import com.mwin.reward.async.models.HomeSliderItem;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.async.models.MenuListItem;
import com.mwin.reward.async.models.PushNotificationModel;
import com.mwin.reward.async.models.StoryViewItem;
import com.mwin.reward.async.models.SubMenuListItem;
import com.mwin.reward.async.models.UserProfileDetails;
import com.mwin.reward.customviews.recyclerview_pagers.PagerAdapter;
import com.mwin.reward.customviews.recyclerview_pagers.RecyclerViewPager;
import com.mwin.reward.customviews.storyview.StoryView;
import com.mwin.reward.customviews.storyview.callback.OnStoryChangedCallback;
import com.mwin.reward.customviews.storyview.callback.StoryClickListeners;
import com.mwin.reward.utils.ActivityManager;
import com.mwin.reward.utils.AdsUtil;
import com.mwin.reward.utils.AppLogger;
import com.mwin.reward.utils.CommonMethodsUtils;
import com.mwin.reward.utils.SharePreference;
import com.mwin.reward.value.Constants;
import com.onesignal.Continue;
import com.onesignal.OneSignal;
import com.skydoves.progressview.ProgressView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {
    private DrawerLayout drawer;
    private NavigationView nav_view_left;
    private boolean isCheckedForUpdate = false;
    private Handler handlerExit;
    private MainResponseModel responseMain;
    private Dialog dialog, dialogExitDialogAfterInterstitial;
    private boolean doubleBackToExitPressedOnce = false, isExitNativeNotLoaded = false, isHomeSelected = false;
    private MaxAd nativeAdExit;
    private MaxNativeAdLoader nativeAdLoaderExit;
    private FrameLayout frameLayoutExit;
    private AppCompatButton btnWithdraw;
    private LinearLayout layoutHome, layoutReward, layoutTasks, layoutInvite, layoutMe, layoutPoints, layoutGiveawayCode, layoutHotOffers;
    private ImageView ivHome, ivGames, ivReward, ivInvite, imgStory, ivNotification, ivMenu, ivTasks, ivAdjoeLeaderboard;
    private TextView tvNextPayout, tvWithdrawProgress, tvTimer, lblHome, lblReward, lblTasks, lblInvite, lblGames, tvName, tvEmail, tvRewardPoints, tvPoints, lblGiveawayCode, tvGiveawayCode;
    private CircleImageView ivProfilePic;
    private ProgressBar progressBarWithdraw;
    private RelativeLayout layoutSlider, layoutTasks1;
    private RecyclerViewPager rvSlider;
    private LinearLayout layoutInflate;
    private MaxAd nativeAd;
    private MaxNativeAdLoader nativeAdLoader;
    private LottieAnimationView lottieViewTask;
    private Animation blinkAnimation;
    private LiveMilestonesAdapter milestoneAdapter;
    private DailyTargetAdapter dailyTargetAdapter;
    private View viewMilestones;
    private int selectedQuickTaskPos = -1;
    private boolean isTimerSet = false, isTimerOver = false;
    private CountDownTimer timer, timerQuickTask;
    private int time;
    private String todayDate, endDate;
    private QuickTasksAdapter quickTasksAdapter;
    private View quickTaskView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        CommonMethodsUtils.setDayNightTheme(HomeActivity.this);
        setContentView(R.layout.activity_home);

        blinkAnimation = new AlphaAnimation(0.3f, 1.0f);
        blinkAnimation.setDuration(500); //You can manage the blinking time with this parameter
        blinkAnimation.setStartOffset(20);
        blinkAnimation.setRepeatMode(Animation.REVERSE);
        blinkAnimation.setRepeatCount(Animation.INFINITE);

        responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
        if (responseMain == null || (getIntent().getExtras() != null && getIntent().getExtras().containsKey("isFromLogin"))) {
            new MainDataAsync(HomeActivity.this);
        } else {
            setData();
        }
        if (SharePreference.getInstance().getBoolean(SharePreference.isFromNotification) || !SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
            CommonMethodsUtils.InitializeApplovinSDK();
        }
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
            intentFilter.addAction(Intent.ACTION_INSTALL_PACKAGE);
            intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
            intentFilter.addDataScheme("package");
            if (ApplicationController.packageInstallBroadcast == null) {
                ApplicationController.packageInstallBroadcast = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (!intent.getExtras().containsKey(Intent.EXTRA_REPLACING)) {
                            try {
//                    String name = intent.getData().toString().replace("package:", "");
//                    Log.e("InstallPackageReceiver", "NAME: " + name);
                                new SendNewAppPackageDataAsync(HomeActivity.this, intent.getData().toString().replace("package:", ""));
                                new SavePackageInstallDataAsync(intent.getData().toString().replace("package:", ""));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (intent.getExtras().containsKey(Intent.EXTRA_REPLACING) && intent.getData().toString().replace("package:", "").equals(ApplicationController.getContext().getPackageName())) {
                            try {
                                // Notify user about app update
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                registerReceiver(ApplicationController.packageInstallBroadcast, intentFilter);
                //AppLogger.getInstance().e("PACKAGE onCreate=======", "REGISTER");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        registerBroadcast();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (ApplicationController.packageInstallBroadcast != null) {
                unregisterReceiver(ApplicationController.packageInstallBroadcast);
                ApplicationController.packageInstallBroadcast = null;
                //AppLogger.getInstance().e("PACKAGE onDestroy=======", "UNREGISTER");
            }
        } catch (Exception e) {
            ApplicationController.packageInstallBroadcast = null;
            e.printStackTrace();
        }
    }

    private void setData() {
        initView();
        initSlideMenuUI();
        showExitDialog(false);

        if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getIsShowAdjoeLeaderboardIcon()) && responseMain.getIsShowAdjoeLeaderboardIcon().equals("1")) {
            ivAdjoeLeaderboard.setVisibility(View.VISIBLE);
        } else {
            ivAdjoeLeaderboard.setVisibility(View.GONE);
        }
        try {
            if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getIsShowFooterTaskIcon()) && responseMain.getIsShowFooterTaskIcon().equals("1")) {
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getFooterTaskIcon())) {
                    if (responseMain.getFooterTaskIcon().endsWith(".json")) {
                        lottieViewTask.setVisibility(View.VISIBLE);
                        ivTasks.setVisibility(View.GONE);
                        CommonMethodsUtils.setLottieAnimation(lottieViewTask, responseMain.getFooterTaskIcon());
                    } else {
                        Glide.with(HomeActivity.this)
                                .load(responseMain.getFooterTaskIcon())
                                .into(ivTasks);
                        ivTasks.setVisibility(View.VISIBLE);
                        lottieViewTask.setVisibility(View.GONE);
                    }
                } else {
                    lottieViewTask.setVisibility(View.VISIBLE);
                    ivTasks.setVisibility(View.GONE);
                    lottieViewTask.setAnimation(R.raw.ic_tack_anim);
                }
            } else {
                layoutTasks1.setVisibility(View.GONE);
                layoutTasks.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getHotOffersScreenNo()) &&!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getIsShowHotOffers()) && responseMain.getIsShowHotOffers().equalsIgnoreCase("1")) {
                layoutHotOffers.setVisibility(View.VISIBLE);
                layoutHotOffers.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CommonMethodsUtils.Redirect(HomeActivity.this,responseMain.getHotOffersScreenNo(),"","","","","");
                    }
                });

            } else {
                layoutHotOffers.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getFooterImage())) {
                ImageView ivFooterImage = findViewById(R.id.ivFooterImage);
                Glide.with(HomeActivity.this)
                        .load(responseMain.getFooterImage())
                        .into(ivFooterImage);
                ivFooterImage.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (responseMain.getStoryView() != null && responseMain.getStoryView().size() > 0) {
                imgStory.setVisibility(View.VISIBLE);
                imgStory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new StoryView.Builder(HomeActivity.this.getSupportFragmentManager())
                                .setStoriesList((ArrayList<StoryViewItem>) responseMain.getStoryView())
                                .setStoryDuration(5000)
                                .setTitleText("")
                                .setSubtitleText("")
                                .setStoryClickListeners(new StoryClickListeners() {
                                    @Override
                                    public void onDescriptionClickListener(int position) {
                                        CommonMethodsUtils.Redirect(HomeActivity.this, responseMain.getStoryView().get(position).getScreenNo(), responseMain.getStoryView().get(position).getTitle(), responseMain.getStoryView().get(position).getClickUrl(), responseMain.getStoryView().get(position).getId(), responseMain.getStoryView().get(position).getTaskId(), null);
                                    }

                                    @Override
                                    public void onTitleIconClickListener(int position) {

                                    }
                                })
                                .setOnStoryChangedCallback(new OnStoryChangedCallback() {
                                    @Override
                                    public void storyChanged(int position) {
                                    }
                                })
                                .setStartingIndex(0)
                                .setRtl(false)
                                .build()
                                .show();
                    }
                });
            } else {
                imgStory.setVisibility(View.GONE);
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
                        startActivity(new Intent(HomeActivity.this, HomeActivity.class));
                    }
                });
            } else {
                layoutGiveawayCode.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (responseMain.getHomeSlider() != null && responseMain.getHomeSlider().size() > 0) {
                layoutSlider.setVisibility(View.VISIBLE);
                rvSlider.setClear();
                rvSlider.addAll((ArrayList<HomeSliderItem>) responseMain.getHomeSlider());
                rvSlider.start();
                rvSlider.setOnItemClickListener(new PagerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        CommonMethodsUtils.Redirect(HomeActivity.this, responseMain.getHomeSlider().get(position).getScreenNo(), responseMain.getHomeSlider().get(position).getTitle()
                                , responseMain.getHomeSlider().get(position).getUrl(), responseMain.getHomeSlider().get(position).getId(), null, responseMain.getHomeSlider().get(position).getImage());
                    }
                });
            } else {
                layoutSlider.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Show Default AppLuck
        if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getIsShowAppluck()) && responseMain.getIsShowAppluck().equals("1") && !CommonMethodsUtils.isStringNullOrEmpty(responseMain.getIsDefaultAppluck()) && responseMain.getIsDefaultAppluck().equals("1")) {
            RelativeLayout relMainFlotAppLuck = findViewById(R.id.relMainFlotAppLuck);
            LinearLayout layoutDefaultAd = findViewById(R.id.layoutDefaultAd);
            CommonMethodsUtils.showDefaultAppLuck(HomeActivity.this, layoutDefaultAd, relMainFlotAppLuck, responseMain.getDefaultAppluckID());
        }
        // Load home note webview top
        try {
            if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getHomeNote())) {
                WebView webNote = findViewById(R.id.webNote);
                webNote.getSettings().setJavaScriptEnabled(true);
                webNote.setVisibility(View.VISIBLE);
                webNote.loadDataWithBaseURL(null, responseMain.getHomeNote(), "text/html", "UTF-8", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (layoutInflate != null) {
            layoutInflate.removeAllViews();
        }
        layoutInflate.setVisibility(View.VISIBLE);
        try {
            if (responseMain.getHomeDataList() != null && responseMain.getHomeDataList().size() > 0) {
                for (int i = 0; i < responseMain.getHomeDataList().size(); i++) {
                    try {
                        inflateHomeScreenData(responseMain.getHomeDataList().get(i).getType(), responseMain.getHomeDataList().get(i));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        showHomeDialog();
        if (!isCheckedForUpdate) {
            isCheckedForUpdate = true;
            if (responseMain.getAppVersion() != null) {
                try {
                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    String version = pInfo.versionName;
                    if (!responseMain.getAppVersion().equals(version)) {
                        CommonMethodsUtils.UpdateApp(HomeActivity.this, responseMain.getIsForceUpdate(), responseMain.getAppUrl(), responseMain.getUpdateMessage());
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            if (SharePreference.getInstance().getBoolean(SharePreference.isFromNotification)) {
                SharePreference.getInstance().putBoolean(SharePreference.isFromNotification, false);
                PushNotificationModel notificationModel = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.notificationData), PushNotificationModel.class);
                CommonMethodsUtils.Redirect(HomeActivity.this, notificationModel.getScreenNo(), notificationModel.getTitle(), notificationModel.getUrl(), notificationModel.getId(), notificationModel.getTaskId(), notificationModel.getImage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getIsShowWelcomeBonusPopup()) && responseMain.getIsShowWelcomeBonusPopup().equals("1") && !SharePreference.getInstance().getBoolean(SharePreference.IS_WELCOME_POPUP_SHOWN)) {
            CommonMethodsUtils.logFirebaseEvent(HomeActivity.this, "FeatureUsabilityItemId", "FeatureUsabilityEvent", "Sign_up", "Sign up");
            showWelcomeBonusPopup(responseMain.getWelcomeBonus());
        }

        updateNextWithdrawAmount();
//        showWelcomeBonusPopup("100");
//        CommonMethodsUtils.showWatchWebDialog(MainActivity.this);
    }

    private void updateNextWithdrawAmount() {
        try {
            responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
            tvNextPayout.setText("Next payout for ₹ " + responseMain.getNextWithdrawAmount());
            tvWithdrawProgress.setText(SharePreference.getInstance().getEarningPointString() + "/" + (Integer.parseInt(responseMain.getNextWithdrawAmount()) * Integer.parseInt(responseMain.getPointValue())));
            progressBarWithdraw.setMax(100);
            int required = Integer.parseInt(responseMain.getNextWithdrawAmount()) * Integer.parseInt(responseMain.getPointValue());
            int per = Integer.parseInt(SharePreference.getInstance().getEarningPointString()) * 100 / required;
            progressBarWithdraw.setProgress(per);
            btnWithdraw.setEnabled(per >= 100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showWelcomeBonusPopup(String points) {
        Dialog dialogWin = new Dialog(HomeActivity.this, android.R.style.Theme_Light);
        dialogWin.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
        dialogWin.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogWin.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        dialogWin.setCancelable(false);
        dialogWin.setCanceledOnTouchOutside(false);
        dialogWin.setContentView(R.layout.popup_welcome_bonus);
        dialogWin.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        TextView tvPoint = dialogWin.findViewById(R.id.tvPoints);
//        tvPoint.setText(points);

        LottieAnimationView animation_view = dialogWin.findViewById(R.id.animation_view);
        animation_view.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation, boolean isReverse) {
                super.onAnimationStart(animation, isReverse);
                CommonMethodsUtils.startTextCountAnimation(tvPoint, points);
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
        CommonMethodsUtils.setLottieAnimation(animation_view, responseMain.getCelebrationLottieUrl());

        TextView lblPoints = dialogWin.findViewById(R.id.lblPoints);
        try {
            int pt = Integer.parseInt(points);
            lblPoints.setText((pt <= 1 ? "Point" : "Points"));
        } catch (Exception e) {
            e.printStackTrace();
            lblPoints.setText("Points");
        }
        AppCompatButton btnOk = dialogWin.findViewById(R.id.btnOk);
        btnOk.setText("Ok");
        btnOk.setOnClickListener(v -> {
            AdsUtil.showAppLovinInterstitialAd(HomeActivity.this, new AdsUtil.AdShownListener() {
                @Override
                public void onAdDismiss() {
                    if (dialogWin != null) {
                        dialogWin.dismiss();
                    }
                }
            });
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
            SharePreference.getInstance().putBoolean(SharePreference.IS_WELCOME_POPUP_SHOWN, true);
        }
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
        app.getCpxResearch().setSurveyVisibleIfAvailable(false, HomeActivity.this);

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

        app.getCpxResearch().insertCPXResearchCardsIntoContainer(HomeActivity.this, llSurvey, cardConfig);
        app.getCpxResearch().requestSurveyUpdate(false);
    }

    private void inflateHomeScreenData(String type, HomeDataListItem categoryModel) {
        AppLogger.getInstance().e("TYPE===", "TYPE===" + type);
        switch (type) {
            case Constants.DATA_TYPES.CPX_SURVEY:
                if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getIsShowSurvey()) && responseMain.getIsShowSurvey().equals("1")) {
                    View viewCPXSurvey = getLayoutInflater().inflate(R.layout.cpx_surveys_inflate, layoutInflate, false);
                    TextView tvTopSurveys = viewCPXSurvey.findViewById(R.id.tvTopSurveys);
                    LinearLayout layoutSurveys = viewCPXSurvey.findViewById(R.id.layoutSurveys);
                    LinearLayout layoutSurveyList = viewCPXSurvey.findViewById(R.id.layoutSurveyList);
                    TextView txtNoSurvey = viewCPXSurvey.findViewById(R.id.txtNoSurvey);

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

                    txtNoSurvey.setVisibility(View.VISIBLE);
                    txtNoSurvey.setText("Loading Surveys...");
                    TextView tvSeeSurveyHistory = viewCPXSurvey.findViewById(R.id.tvSeeSurveyHistory);
                    TextView tvTopOffers = viewCPXSurvey.findViewById(R.id.tvTopOffers);
                    RecyclerView rvTopOffers = viewCPXSurvey.findViewById(R.id.rvTopOffers);
                    tvSeeSurveyHistory.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                                startActivity(new Intent(HomeActivity.this, EarnedPointHistoryActivity.class)
                                        .putExtra("type", Constants.HistoryType.SURVEYS)
                                        .putExtra("title", "Survey History"));
                            } else {
                                CommonMethodsUtils.NotifyLogin(HomeActivity.this);
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

                    if (responseMain.getTop_offers() != null && responseMain.getTop_offers().size() > 0) {
                        TopOffersListAdapter dailyLoginAdapter = new TopOffersListAdapter(HomeActivity.this, responseMain.getTop_offers(), new TopOffersListAdapter.ClickListener() {
                            @Override
                            public void onItemClick(int position, View v) {
                                if (responseMain.getTop_offers().get(position).getIsShowDetails() != null && responseMain.getTop_offers().get(position).getIsShowDetails().equals("1")) {
                                    Intent intent = new Intent(HomeActivity.this, TaskInfoActivity.class);
                                    intent.putExtra("taskId", responseMain.getTop_offers().get(position).getId());
                                    startActivity(intent);
                                } else {
                                    CommonMethodsUtils.Redirect(HomeActivity.this, responseMain.getTop_offers().get(position).getScreenNo(), responseMain.getTop_offers().get(position).getTitle()
                                            , responseMain.getTop_offers().get(position).getUrl(), null, responseMain.getTop_offers().get(position).getId(), responseMain.getTop_offers().get(position).getIcon());
                                }
                            }
                        });
                        rvTopOffers.setLayoutManager(new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        rvTopOffers.setAdapter(dailyLoginAdapter);
                    } else {
                        tvTopOffers.setVisibility(View.GONE);
                        rvTopOffers.setVisibility(View.GONE);
                    }
                    setupCPX(layoutSurveyList, txtNoSurvey);
                    layoutInflate.addView(viewCPXSurvey);
                }
                break;
            case Constants.DATA_TYPES.LIVE_CONTEST:
                View viewLiveContest = getLayoutInflater().inflate(R.layout.inflate_home_live_contest, layoutInflate, false);
                TextView tvLabel1 = viewLiveContest.findViewById(R.id.tvLabel);
                View viewShineHeader = viewLiveContest.findViewById(R.id.viewShineHeader);
                Animation animShine = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.left_right_duration);
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
                            startActivity(new Intent(HomeActivity.this, AdjoeLeaderboardActivity.class));
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
                            CommonMethodsUtils.setEnableDisable(HomeActivity.this, view);
                            CommonMethodsUtils.Redirect(HomeActivity.this, categoryModel.getScreenNo(), categoryModel.getTitle(), categoryModel.getUrl(), categoryModel.getId(), categoryModel.getTaskId(), categoryModel.getImage());
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

                    Drawable mDrawable2 = ContextCompat.getDrawable(HomeActivity.this, R.drawable.ad_live_leaderboard_sub_title);
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

                    todayDate = categoryModel.getDailyRewardTodayDate();
                    endDate = categoryModel.getDailyRewardEndDate();

                    if (todayDate != null && endDate != null) {
                        tvTimer = viewDailyTarget.findViewById(R.id.tvTimer);
                        setTimer();
                    }

                    RecyclerView rvList = viewDailyTarget.findViewById(R.id.rvList);
                    dailyTargetAdapter = new DailyTargetAdapter(categoryModel.getDailyTargetList(), HomeActivity.this, new DailyTargetAdapter.ClickListener() {
                        @Override
                        public void onItemClick(int position, View v) {
                            CommonMethodsUtils.setEnableDisable(HomeActivity.this, v);
                            if (categoryModel.getIsActive() != null && categoryModel.getIsActive().equals("0")) {
                                CommonMethodsUtils.Notify(HomeActivity.this, getString(R.string.app_name), categoryModel.getNotActiveMessage(), false);
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
                                        new SaveDailyTargetAsync(HomeActivity.this, categoryModel.getDailyTargetList().get(position).getPoints(), categoryModel.getDailyTargetList().get(position).getId());
                                    } else {
                                        CommonMethodsUtils.NotifyLogin(HomeActivity.this);
                                    }
                                } else {
                                    CommonMethodsUtils.Redirect(HomeActivity.this, categoryModel.getDailyTargetList().get(position).getScreenNo(), categoryModel.getDailyTargetList().get(position).getTitle(), null, categoryModel.getDailyTargetList().get(position).getId(), categoryModel.getDailyTargetList().get(position).getId(), categoryModel.getDailyTargetList().get(position).getIcon());
                                }
                            }
                        }

                        @Override
                        public void onClaimClick(int position, View v) {
                            if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                                if (!CommonMethodsUtils.isStringNullOrEmpty(categoryModel.getDailyTargetList().get(position).getIsClaimed()) && categoryModel.getDailyTargetList().get(position).getIsClaimed().equals("0")) {
                                    new SaveDailyTargetAsync(HomeActivity.this, categoryModel.getDailyTargetList().get(position).getPoints(), categoryModel.getDailyTargetList().get(position).getId());
                                }
                            } else {
                                CommonMethodsUtils.NotifyLogin(HomeActivity.this);
                            }
                        }
                    });
                    rvList.setAdapter(dailyTargetAdapter);
                    layoutInflate.addView(viewDailyTarget);
                }
                break;
            case Constants.DATA_TYPES.LIVE_MILESTONES:
                if (categoryModel.getMilestoneData() != null && categoryModel.getMilestoneData().size() > 0) {
                    viewMilestones = getLayoutInflater().inflate(R.layout.inflate_home_live_milestones, layoutInflate, false);
                    viewMilestones.setTag(Constants.DATA_TYPES.LIVE_MILESTONES);
                    TextView lblMilestones = viewMilestones.findViewById(R.id.lblTitle);
                    TextView lblMilestonesSubTitle = viewMilestones.findViewById(R.id.lblSubTitle);
                    lblMilestones.setText(categoryModel.getTitle());
                    if (!CommonMethodsUtils.isStringNullOrEmpty(categoryModel.getSubTitle())) {
                        lblMilestonesSubTitle.setText(categoryModel.getSubTitle());
                    } else {
                        lblMilestonesSubTitle.setVisibility(View.GONE);
                    }

                    TextView tvLabelMilestone = viewMilestones.findViewById(R.id.tvLabel);
                    if (!CommonMethodsUtils.isStringNullOrEmpty(categoryModel.getLabel())) {
                        tvLabelMilestone.setText(categoryModel.getLabel());
                        tvLabelMilestone.setVisibility(View.VISIBLE);
                        tvLabelMilestone.startAnimation(blinkAnimation);
                    } else {
                        tvLabelMilestone.setVisibility(View.GONE);
                    }
                    TextView tvViewAll = viewMilestones.findViewById(R.id.tvViewAll);
                    tvViewAll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(HomeActivity.this, MilestoneTargetListActivity.class));
                        }
                    });

                    RecyclerView rvList = viewMilestones.findViewById(R.id.rvList);
                    milestoneAdapter = new LiveMilestonesAdapter(categoryModel.getMilestoneData(), HomeActivity.this, categoryModel.getIconBGColor(), new LiveMilestonesAdapter.ClickListener() {
                        @Override
                        public void onItemClick(int position, View v) {
                            CommonMethodsUtils.setEnableDisable(HomeActivity.this, v);
                            if (categoryModel.getIsActive() != null && categoryModel.getIsActive().equals("0")) {
                                CommonMethodsUtils.Notify(HomeActivity.this, getString(R.string.app_name), categoryModel.getNotActiveMessage(), false);
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
                                        Intent intent = new Intent(HomeActivity.this, MilestoneTargetDetailsActivity.class).putExtra("milestoneId", categoryModel.getMilestoneData().get(position).getId());
                                        startActivity(intent);
                                    }
                                } else {
                                    CommonMethodsUtils.Redirect(HomeActivity.this, categoryModel.getMilestoneData().get(position).getScreenNo(), categoryModel.getMilestoneData().get(position).getTitle(), null, categoryModel.getMilestoneData().get(position).getId(), categoryModel.getMilestoneData().get(position).getId(), categoryModel.getMilestoneData().get(position).getIcon());
                                }
                            }
                        }
                    });
                    rvList.setAdapter(milestoneAdapter);
                    layoutInflate.addView(viewMilestones);
                }
                break;
            case Constants.DATA_TYPES.TASK_LIST:
                if (categoryModel.getData() != null && categoryModel.getData().size() > 0) {
                    View viewTaskList = getLayoutInflater().inflate(R.layout.inflate_home_general_layout, layoutInflate, false);
                    RecyclerView taskList = viewTaskList.findViewById(R.id.rvIconlist);
                    TextView taskListHeader = viewTaskList.findViewById(R.id.txtTitleHeader);

                    if (categoryModel.getTitle() != null && !categoryModel.getTitle().isEmpty()) {
                        taskListHeader.setVisibility(View.VISIBLE);
                        taskListHeader.setText(categoryModel.getTitle());
                    } else {
                        taskListHeader.setVisibility(View.GONE);
                    }
                    HomeTasksListAdapter taskListAdapter = new HomeTasksListAdapter(categoryModel.getData(), HomeActivity.this, new HomeTasksListAdapter.ClickListener() {
                        @Override
                        public void onItemClick(int position, View v) {
                            CommonMethodsUtils.Redirect(HomeActivity.this, categoryModel.getData().get(position).getScreenNo(), categoryModel.getData().get(position).getTitle(), categoryModel.getData().get(position).getUrl(), categoryModel.getData().get(position).getId(), categoryModel.getData().get(position).getTaskId(), categoryModel.getData().get(position).getImage());
                        }
                    });
                    taskList.setLayoutManager(new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.VERTICAL, false));
                    taskList.setAdapter(taskListAdapter);
                    layoutInflate.addView(viewTaskList);
                }
                break;
            case Constants.DATA_TYPES.ICON_LIST:
                if (categoryModel.getData() != null && categoryModel.getData().size() > 0) {
                    View iconView = getLayoutInflater().inflate(R.layout.inflate_home_iconlist, layoutInflate, false);
                    RecyclerView rvIconlist = iconView.findViewById(R.id.rvIconlist);
                    TextView txtTitleHeader = iconView.findViewById(R.id.txtTitleHeader);

                    if (categoryModel.getTitle() != null && !categoryModel.getTitle().isEmpty()) {
                        txtTitleHeader.setVisibility(View.VISIBLE);
                        txtTitleHeader.setText(categoryModel.getTitle());
                    } else {
                        txtTitleHeader.setVisibility(View.GONE);
                    }
                    StoryListAdapter homeStoryAdapter = new StoryListAdapter(HomeActivity.this, categoryModel.getData(), new StoryListAdapter.ClickListener() {
                        @Override
                        public void onItemClick(int position, View v) {
                            CommonMethodsUtils.Redirect(HomeActivity.this, categoryModel.getData().get(position).getScreenNo(), categoryModel.getData().get(position).getTitle(), categoryModel.getData().get(position).getUrl(), categoryModel.getData().get(position).getId(), categoryModel.getData().get(position).getTaskId(), categoryModel.getData().get(position).getImage());
                        }
                    });
                    rvIconlist.setLayoutManager(new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    rvIconlist.setAdapter(homeStoryAdapter);
                    layoutInflate.addView(iconView);
                }
                break;
            case Constants.DATA_TYPES.SINGLE_SLIDER:
                if (categoryModel.getData() != null && categoryModel.getData().size() > 0) {
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
                    SingleSliderImageAdapter homeSingleSilderAdapter = new SingleSliderImageAdapter(HomeActivity.this, categoryModel.getData(), false, new SingleSliderImageAdapter.ClickListener() {
                        @Override
                        public void onItemClick(int position, View v) {
                            CommonMethodsUtils.Redirect(HomeActivity.this, categoryModel.getData().get(position).getScreenNo(), categoryModel.getData().get(position).getTitle(), categoryModel.getData().get(position).getUrl(), categoryModel.getData().get(position).getId(), categoryModel.getData().get(position).getTaskId(), categoryModel.getData().get(position).getImage());
                        }
                    });
                    rvSliderlist.setLayoutManager(new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.VERTICAL, false));
                    rvSliderlist.setAdapter(homeSingleSilderAdapter);
                    layoutInflate.addView(iconSingleSlider);
                }
                break;
            case Constants.DATA_TYPES.GRID:
                try {
                    View viewGrid = getLayoutInflater().inflate(R.layout.inflate_reward_grid, layoutInflate, false);
                    RecyclerView rvList = viewGrid.findViewById(R.id.rvList);
                    EarningOptionsGridAdapter gridAdapter = new EarningOptionsGridAdapter(HomeActivity.this, blinkAnimation, categoryModel.getGridData(), new EarningOptionsGridAdapter.ClickListener() {
                        @Override
                        public void onItemClick(int position, View v) {
                            try {
                                CommonMethodsUtils.setEnableDisable(HomeActivity.this, v);
                                CommonMethodsUtils.Redirect(HomeActivity.this, categoryModel.getGridData().get(position).getScreenNo(), categoryModel.getGridData().get(position).getTitle(), categoryModel.getGridData().get(position).getUrl(), categoryModel.getGridData().get(position).getId(), categoryModel.getGridData().get(position).getTaskId(), categoryModel.getGridData().get(position).getImage());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    GridLayoutManager mGridLayoutManager = new GridLayoutManager(HomeActivity.this, Integer.parseInt(categoryModel.getColumnCount()));
                    mGridLayoutManager.setOrientation(RecyclerView.VERTICAL);
                    rvList.setLayoutManager(mGridLayoutManager);
                    rvList.setAdapter(gridAdapter);
                    layoutInflate.addView(viewGrid);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case Constants.DATA_TYPES.TWO_GRID:
                if (categoryModel.getData() != null && categoryModel.getData().size() > 0) {
                    View twoGrid = getLayoutInflater().inflate(R.layout.inflate_home_grid, layoutInflate, false);
                    RecyclerView rvGridlist = twoGrid.findViewById(R.id.rvIconlist);
                    TextView txtGridHeader = twoGrid.findViewById(R.id.txtTitleHeader);

                    if (categoryModel.getTitle() != null && !categoryModel.getTitle().isEmpty()) {
                        txtGridHeader.setVisibility(View.VISIBLE);
                        txtGridHeader.setText(categoryModel.getTitle());
                    } else {
                        txtGridHeader.setVisibility(View.GONE);
                    }
                    SingleSliderImageAdapter homeGridAdpater = new SingleSliderImageAdapter(HomeActivity.this, categoryModel.getData(), true, new SingleSliderImageAdapter.ClickListener() {
                        @Override
                        public void onItemClick(int position, View v) {
                            CommonMethodsUtils.Redirect(HomeActivity.this, categoryModel.getData().get(position).getScreenNo(), categoryModel.getData().get(position).getTitle(), categoryModel.getData().get(position).getUrl(), categoryModel.getData().get(position).getId(), categoryModel.getData().get(position).getTaskId(), categoryModel.getData().get(position).getImage());
                        }
                    });
                    rvGridlist.setLayoutManager(new GridLayoutManager(HomeActivity.this, 2));
                    rvGridlist.setAdapter(homeGridAdpater);
                    layoutInflate.addView(twoGrid);
                }
                break;
            case Constants.DATA_TYPES.SINGLE_BIG_TASK:
                if (categoryModel.getData() != null && categoryModel.getData().size() > 0) {
                    View viewSingleBigTaskRow = getLayoutInflater().inflate(R.layout.inflate_home_general_layout, layoutInflate, false);
                    RecyclerView rvSingleBiglist = viewSingleBigTaskRow.findViewById(R.id.rvIconlist);
                    TextView txtSingleBigHeader = viewSingleBigTaskRow.findViewById(R.id.txtTitleHeader);

                    if (categoryModel.getTitle() != null && !categoryModel.getTitle().isEmpty()) {
                        txtSingleBigHeader.setVisibility(View.VISIBLE);
                        txtSingleBigHeader.setText(categoryModel.getTitle());
                    } else {
                        txtSingleBigHeader.setVisibility(View.GONE);
                    }
                    SingleBigTaskAdapter homeSingleBiogtaskAdapter = new SingleBigTaskAdapter(HomeActivity.this, categoryModel.getData(), categoryModel.getPointBackgroundColor(), categoryModel.getPointTextColor(), new SingleBigTaskAdapter.ClickListener() {
                        @Override
                        public void onItemClick(int position, View v) {
                            CommonMethodsUtils.Redirect(HomeActivity.this, categoryModel.getData().get(position).getScreenNo(), categoryModel.getData().get(position).getTitle(), categoryModel.getData().get(position).getUrl(), categoryModel.getData().get(position).getId(), categoryModel.getData().get(position).getTaskId(), categoryModel.getData().get(position).getImage());
                        }
                    });
                    rvSingleBiglist.setLayoutManager(new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.VERTICAL, false));
                    rvSingleBiglist.setAdapter(homeSingleBiogtaskAdapter);
                    layoutInflate.addView(viewSingleBigTaskRow);
                }
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
            case Constants.DATA_TYPES.EARN_GRID:
                if (categoryModel.getData() != null && categoryModel.getData().size() > 0) {
                    View viewEarnGrid = getLayoutInflater().inflate(R.layout.inflate_home_grid, layoutInflate, false);
                    RecyclerView gridList = viewEarnGrid.findViewById(R.id.rvIconlist);
                    TextView earnGridHeader = viewEarnGrid.findViewById(R.id.txtTitleHeader);

                    if (categoryModel.getTitle() != null && !categoryModel.getTitle().isEmpty()) {
                        earnGridHeader.setVisibility(View.VISIBLE);
                        earnGridHeader.setText(categoryModel.getTitle());
                    } else {
                        earnGridHeader.setVisibility(View.GONE);
                    }
                    HomeGridAdapter earnGridAdapter = new HomeGridAdapter(HomeActivity.this, categoryModel.getData(), new HomeGridAdapter.ClickListener() {
                        @Override
                        public void onItemClick(int position, View v) {
                            CommonMethodsUtils.Redirect(HomeActivity.this, categoryModel.getData().get(position).getScreenNo(), categoryModel.getData().get(position).getTitle(), categoryModel.getData().get(position).getUrl(), categoryModel.getData().get(position).getId(), categoryModel.getData().get(position).getTaskId(), categoryModel.getData().get(position).getImage());
                        }
                    });
                    gridList.setLayoutManager(new GridLayoutManager(HomeActivity.this, 2));
                    gridList.setAdapter(earnGridAdapter);
                    layoutInflate.addView(viewEarnGrid);
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
                    quickTasksAdapter = new QuickTasksAdapter(categoryModel.getData(), HomeActivity.this, new QuickTasksAdapter.ClickListener() {
                        @Override
                        public void onItemClick(int position, View v) {
                            if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                                selectedQuickTaskPos = position;
                                ActivityManager.isShowAppOpenAd = false;
                                CommonMethodsUtils.Redirect(HomeActivity.this, categoryModel.getData().get(position).getScreenNo(), categoryModel.getData().get(position).getTitle(), categoryModel.getData().get(position).getUrl(), categoryModel.getData().get(position).getId(), categoryModel.getData().get(position).getTaskId(), categoryModel.getData().get(position).getImage());
                                startQuickTaskTimer(categoryModel.getData(), position);
                            } else {
                                CommonMethodsUtils.NotifyLogin(HomeActivity.this);
                            }
                        }
                    });
                    rvSliderlist.setLayoutManager(new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.VERTICAL, false));
                    rvSliderlist.setAdapter(quickTasksAdapter);
                    layoutInflate.addView(quickTaskView);
                }
                break;
        }
    }

    private void setTimer() {
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
                    tvTimer.setText("00:00:00");
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

    private void showHomeDialog() {
        try {
            if (responseMain.getHomeDialog() != null) {
                if (SharePreference.getInstance().getString(SharePreference.homeDialogShownDate + responseMain.getHomeDialog().getId()).length() == 0
                        || (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getHomeDialog().getIsShowEverytime()) && responseMain.getHomeDialog().getIsShowEverytime().equals("1"))
                        || !SharePreference.getInstance().getString(SharePreference.homeDialogShownDate + responseMain.getHomeDialog().getId()).equals(CommonMethodsUtils.getCurrentDate())) {
                    if (CommonMethodsUtils.isStringNullOrEmpty(responseMain.getHomeDialog().getPackagename())
                            || (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getHomeDialog().getPackagename())
                            && !CommonMethodsUtils.appInstalledOrNot(HomeActivity.this, responseMain.getHomeDialog().getPackagename()))) {
                        SharePreference.getInstance().putString(SharePreference.homeDialogShownDate + responseMain.getHomeDialog().getId(), CommonMethodsUtils.getCurrentDate());

                        Dialog dialog = new Dialog(HomeActivity.this, android.R.style.Theme_Light);
                        dialog.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        dialog.setContentView(R.layout.popup_home_data);

                        Button btnOk = dialog.findViewById(R.id.btnSubmit);
                        TextView txtTitle = dialog.findViewById(R.id.txtTitle);
                        TextView btnCancel = dialog.findViewById(R.id.btnCancel);
                        ProgressBar probrBanner = dialog.findViewById(R.id.probrBanner);
                        ImageView imgBanner = dialog.findViewById(R.id.imgBanner);
                        txtTitle.setText(responseMain.getHomeDialog().getTitle());
                        TextView txtMessage = dialog.findViewById(R.id.txtMessage);
                        RelativeLayout relPopup = dialog.findViewById(R.id.relPopup);
                        LottieAnimationView ivLottieView = dialog.findViewById(R.id.ivLottieView);
                        txtMessage.setText(responseMain.getHomeDialog().getDescription());
                        if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getHomeDialog().getIsForce()) && responseMain.getHomeDialog().getIsForce().equals("1")) {
                            btnCancel.setVisibility(View.GONE);
                        } else {
                            btnCancel.setVisibility(View.VISIBLE);
                        }

                        if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getHomeDialog().getBtnName())) {
                            btnOk.setText(responseMain.getHomeDialog().getBtnName());
                        }

                        if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getHomeDialog().getImage())) {
                            if (responseMain.getHomeDialog().getImage().contains("json")) {
                                probrBanner.setVisibility(View.GONE);
                                imgBanner.setVisibility(View.GONE);
                                ivLottieView.setVisibility(View.VISIBLE);
                                CommonMethodsUtils.setLottieAnimation(ivLottieView, responseMain.getHomeDialog().getImage());
                                ivLottieView.setRepeatCount(LottieDrawable.INFINITE);
                            } else {
                                imgBanner.setVisibility(View.VISIBLE);
                                ivLottieView.setVisibility(View.GONE);
                                Glide.with(HomeActivity.this)
                                        .load(responseMain.getHomeDialog().getImage())
                                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                                        .addListener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                probrBanner.setVisibility(View.GONE);
                                                return false;
                                            }
                                        })
                                        .into(imgBanner);
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
                        relPopup.setOnClickListener(v -> CommonMethodsUtils.Redirect(HomeActivity.this, responseMain.getHomeDialog().getScreenNo(), responseMain.getHomeDialog().getTitle(), responseMain.getHomeDialog().getUrl(), responseMain.getHomeDialog().getId(), null, responseMain.getHomeDialog().getImage()));
                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                CommonMethodsUtils.Redirect(HomeActivity.this, responseMain.getHomeDialog().getScreenNo(), responseMain.getHomeDialog().getTitle(), responseMain.getHomeDialog().getUrl(), responseMain.getHomeDialog().getId(), null, responseMain.getHomeDialog().getImage());
                            }
                        });

                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        showPushNotificationSettingsDialog();
                                    }
                                }, 500);
                            }
                        });
                        dialog.show();
                    } else {
                        showPushNotificationSettingsDialog();
                    }
                } else {
                    showPushNotificationSettingsDialog();
                }
            } else {
                showPushNotificationSettingsDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showPushNotificationSettingsDialog() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                OneSignal.promptForPushNotifications();
                try {
                    OneSignal.getNotifications().requestPermission(true, Continue.with(r -> {
                        if (r.isSuccess()) {
                            if (Boolean.TRUE.equals(r.getData())) {
                                // `requestPermission` completed successfully and the user has accepted permission
                            } else {
                                // `requestPermission` completed successfully but the user has rejected permission
                            }
                        } else {
                            // `requestPermission` completed unsuccessfully, check `r.getThrowable()` for more info on the failure reason
                        }
                    }));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 2000);
    }

    private void loadAppLovinNativeAds(FrameLayout frameLayout, TextView lblLoadingAds) {
        try {
            nativeAdLoader = new MaxNativeAdLoader(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinNativeID()), HomeActivity.this);
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

    private void initView() {
        btnWithdraw = findViewById(R.id.btnWithdraw);
        btnWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                    startActivity(new Intent(HomeActivity.this, RedeemOptionsListActivity.class));
                } else {
                    CommonMethodsUtils.NotifyLogin(HomeActivity.this);
                }
            }
        });
        tvNextPayout = findViewById(R.id.tvNextPayout);
        tvWithdrawProgress = findViewById(R.id.tvWithdrawProgress);
        progressBarWithdraw = findViewById(R.id.progressBarWithdraw);
        ivTasks = findViewById(R.id.ivTasks);
        lottieViewTask = findViewById(R.id.lottieViewTask);
        lblGiveawayCode = findViewById(R.id.lblGiveawayCode);
        tvGiveawayCode = findViewById(R.id.tvGiveawayCode);
        layoutGiveawayCode = findViewById(R.id.layoutGiveawayCode);
        rvSlider = findViewById(R.id.rvSlider);
        layoutSlider = findViewById(R.id.layoutSlider);
        layoutInflate = findViewById(R.id.layoutInflate);
        layoutHotOffers = findViewById(R.id.layoutHotOffers);
        imgStory = findViewById(R.id.ivStories);
        ivNotification = findViewById(R.id.ivNotifications);
        ivNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, NotificationListActivity.class));
            }
        });
        ivMenu = findViewById(R.id.ivMenu);
        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer();
            }
        });

        tvPoints = findViewById(R.id.tvPoints);
        layoutPoints = findViewById(R.id.layoutPoints);

        layoutPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                    startActivity(new Intent(HomeActivity.this, EarnedWalletBalanceActivity.class));
                } else {
                    CommonMethodsUtils.NotifyLogin(HomeActivity.this);
                }
            }
        });

        layoutHome = findViewById(R.id.layoutHome);
        layoutHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performHomeClick();
            }
        });
        layoutReward = findViewById(R.id.layoutReward);
        layoutReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performRewardClick();
            }
        });
        layoutTasks = findViewById(R.id.layoutTasks);
        layoutTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performTaskClick();
            }
        });
        layoutTasks1 = findViewById(R.id.layoutTasks1);
        layoutTasks1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performTaskClick();
            }
        });
        layoutInvite = findViewById(R.id.layoutInvite);
        layoutInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performInviteClick();
            }
        });
        layoutMe = findViewById(R.id.layoutMe);
        layoutMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performProfileClick();
            }
        });
        ivGames = findViewById(R.id.ivGames);
        ivHome = findViewById(R.id.ivHome);
        ivReward = findViewById(R.id.ivReward);
        ivInvite = findViewById(R.id.ivInvite);

        lblHome = findViewById(R.id.lblHome);
        lblReward = findViewById(R.id.lblReward);
        tvRewardPoints = findViewById(R.id.tvRewardPoints);
        lblTasks = findViewById(R.id.lblTasks);
        lblInvite = findViewById(R.id.lblInvite);
        lblGames = findViewById(R.id.lblGames);
        if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getRewardLabel())) {
            tvRewardPoints.setText(responseMain.getRewardLabel());
        } else {
            tvRewardPoints.setVisibility(View.INVISIBLE);
        }
        ivAdjoeLeaderboard = findViewById(R.id.ivAdjoeLeaderboard);
        ivAdjoeLeaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, AdjoeLeaderboardActivity.class));
            }
        });
    }

    public void performInviteClick() {
        try {
            isHomeSelected = false;

            ivInvite.setImageResource(R.drawable.ic_refer_fill);
            lblInvite.setTextColor(getColor(R.color.menu_selected));

            ivHome.setImageResource(R.drawable.ic_home);
            lblHome.setTextColor(getColor(R.color.light_grey_font));

            lblTasks.setTextColor(getColor(R.color.light_grey_font));

            ivReward.setImageResource(R.drawable.ic_earn);
            lblReward.setTextColor(getColor(R.color.light_grey_font));

            ivGames.setImageResource(R.drawable.ic_game);
            lblGames.setTextColor(getColor(R.color.light_grey_font));
            startActivity(new Intent(HomeActivity.this, ReferUsersActivity.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void performHomeClick() {
        try {
            isHomeSelected = true;
            ivInvite.setImageResource(R.drawable.ic_refer);
            lblInvite.setTextColor(getColor(R.color.light_grey_font));

            ivHome.setImageResource(R.drawable.ic_home_fill);
            lblHome.setTextColor(getColor(R.color.menu_selected));

            lblTasks.setTextColor(getColor(R.color.light_grey_font));

            ivReward.setImageResource(R.drawable.ic_earn);
            lblReward.setTextColor(getColor(R.color.light_grey_font));

            ivGames.setImageResource(R.drawable.ic_me);
            lblGames.setTextColor(getColor(R.color.light_grey_font));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void performRewardClick() {
        try {
            isHomeSelected = false;
            ivInvite.setImageResource(R.drawable.ic_refer);
            lblInvite.setTextColor(getColor(R.color.light_grey_font));

            ivHome.setImageResource(R.drawable.ic_home);
            lblHome.setTextColor(getColor(R.color.light_grey_font));

            lblTasks.setTextColor(getColor(R.color.light_grey_font));

            ivReward.setImageResource(R.drawable.ic_earn_fill);
            lblReward.setTextColor(getColor(R.color.menu_selected));

            ivGames.setImageResource(R.drawable.ic_me);
            lblGames.setTextColor(getColor(R.color.light_grey_font));
            startActivityForResult(new Intent(HomeActivity.this, EarningOptionsActivity.class), 1500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void performProfileClick() {
        try {
            isHomeSelected = false;

            ivInvite.setImageResource(R.drawable.ic_refer);
            lblInvite.setTextColor(getColor(R.color.light_grey_font));

            ivHome.setImageResource(R.drawable.ic_home);
            lblHome.setTextColor(getColor(R.color.light_grey_font));

            lblTasks.setTextColor(getColor(R.color.light_grey_font));

            ivReward.setImageResource(R.drawable.ic_earn);
            lblReward.setTextColor(getColor(R.color.light_grey_font));

            ivGames.setImageResource(R.drawable.ic_me_fill);
            lblGames.setTextColor(getColor(R.color.menu_selected));
            startActivity(new Intent(HomeActivity.this, UserProfileActivity.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void performTaskClick() {
        try {
            isHomeSelected = false;

            ivInvite.setImageResource(R.drawable.ic_refer);
            lblInvite.setTextColor(getColor(R.color.light_grey_font));

            ivHome.setImageResource(R.drawable.ic_home);
            lblHome.setTextColor(getColor(R.color.light_grey_font));

            lblTasks.setTextColor(getColor(R.color.menu_selected));

            ivReward.setImageResource(R.drawable.ic_earn);
            lblReward.setTextColor(getColor(R.color.light_grey_font));

            ivGames.setImageResource(R.drawable.ic_game);
            lblGames.setTextColor(getColor(R.color.light_grey_font));
            startActivity(new Intent(HomeActivity.this, TaskListActivity.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setHomeData() {
        responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
        setData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CommonMethodsUtils.initializeAdJoe(HomeActivity.this, false);
        setProfileData();
        performHomeClick();
        if (isTimerSet && isTimerOver && selectedQuickTaskPos >= 0) {
            new SaveQuickTaskAsync(HomeActivity.this, quickTasksAdapter.listTasks.get(selectedQuickTaskPos).getPoints(), quickTasksAdapter.listTasks.get(selectedQuickTaskPos).getId());
        }
        isTimerSet = false;
        isTimerOver = false;
        new GetWalletBalanceAsync(HomeActivity.this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isFinishing()) {
            unRegisterReceivers();
            try {
                if (handlerExit != null) {
                    handlerExit.removeCallbacksAndMessages(null);
                }
                if (nativeAdExit != null && nativeAdLoaderExit != null) {
                    nativeAdLoaderExit.destroy(nativeAdExit);
                    nativeAdExit = null;
                    frameLayoutExit = null;
                }
                if (nativeAd != null && nativeAdLoader != null) {
                    nativeAdLoader.destroy(nativeAd);
                    nativeAd = null;
                }
                if (timerQuickTask != null) {
                    timerQuickTask.cancel();
                }
                if (timer != null) {
                    timer.cancel();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initSlideMenuUI() {
        drawer = findViewById(R.id.drawer_layout);
        nav_view_left = findViewById(R.id.nav_view_left);

        /* Left slide menu UI */

        ivProfilePic = nav_view_left.findViewById(R.id.ivProfilePic);
        LinearLayout layoutUserProfile = nav_view_left.findViewById(R.id.layoutUserProfile);
        layoutUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDrawer();
                startActivity(new Intent(HomeActivity.this, UserProfileActivity.class));
            }
        });
        tvName = nav_view_left.findViewById(R.id.tvName);
        tvEmail = nav_view_left.findViewById(R.id.tvEmail);
        setProfileData();

        TextView tvVersionName = nav_view_left.findViewById(R.id.tvVersionName);
        tvVersionName.setText("App Version " + CommonMethodsUtils.getVersionName(HomeActivity.this));

        ImageView menuAdBanner = nav_view_left.findViewById(R.id.menuAdBanner);
        if (responseMain.getMenuBanner() != null && !CommonMethodsUtils.isStringNullOrEmpty(responseMain.getMenuBanner().getImage())) {
            menuAdBanner.setVisibility(View.VISIBLE);
            Glide.with(HomeActivity.this)
                    .load(responseMain.getMenuBanner().getImage())
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(menuAdBanner);
            menuAdBanner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getMenuBanner().getUrl())) {
                        CommonMethodsUtils.openUrl(HomeActivity.this, responseMain.getMenuBanner().getUrl());
                    }
                }
            });
        }

        LinearLayout layoutTelegram = nav_view_left.findViewById(R.id.layoutTelegram);
        layoutTelegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonMethodsUtils.openUrl(HomeActivity.this, responseMain.getTelegramUrl());
            }
        });
        LinearLayout layoutYoutube = nav_view_left.findViewById(R.id.layoutYoutube);
        layoutYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonMethodsUtils.openUrl(HomeActivity.this, responseMain.getYoutubeUrl());
            }
        });
        LinearLayout layoutInstagram = nav_view_left.findViewById(R.id.layoutInstagram);
        layoutInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonMethodsUtils.openUrl(HomeActivity.this, responseMain.getInstagramUrl());
            }
        });
        LinearLayout layoutMyWallet = nav_view_left.findViewById(R.id.layoutMyWallet);
        layoutMyWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                    startActivity(new Intent(HomeActivity.this, EarnedWalletBalanceActivity.class));
                } else {
                    CommonMethodsUtils.NotifyLogin(HomeActivity.this);
                }
            }
        });
        LinearLayout layoutWithdrawalHistory = nav_view_left.findViewById(R.id.layoutWithdrawalHistory);
        layoutWithdrawalHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                    Intent intent = new Intent(HomeActivity.this, EarnedPointHistoryActivity.class);
                    intent.putExtra("type", Constants.HistoryType.WITHDRAW_HISTORY);
                    intent.putExtra("title", "Withdrawal History");
                    startActivity(intent);
                } else {
                    CommonMethodsUtils.NotifyLogin(HomeActivity.this);
                }
            }
        });
        LinearLayout layoutWithdraw = nav_view_left.findViewById(R.id.layoutWithdraw);
        layoutWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                    Intent inSpin = new Intent(HomeActivity.this, RedeemOptionsListActivity.class);
                    startActivity(inSpin);
                } else {
                    CommonMethodsUtils.NotifyLogin(HomeActivity.this);
                }
            }
        });

        RecyclerView rvMenuList = nav_view_left.findViewById(R.id.rvMenuList);
        if (responseMain.getSideMenuList() != null && responseMain.getSideMenuList().size() > 0) {
            List<DrawerMenuParentView> listSideMenu = new ArrayList<>();
            for (MenuListItem objMenu : responseMain.getSideMenuList()) {
                List<DrawerMenuChildView> subMenuList = new ArrayList<>();
                if (objMenu.getSubMenuList() != null && objMenu.getSubMenuList().size() > 0) {
                    for (SubMenuListItem objSubMenu : objMenu.getSubMenuList()) {
                        subMenuList.add(new DrawerMenuChildView(objSubMenu));
                    }
                }
                DrawerMenuParentView obj = new DrawerMenuParentView(objMenu, subMenuList);
                listSideMenu.add(obj);
            }
            DrawerMenuAdapter mAdapter = new DrawerMenuAdapter(this, listSideMenu);
            rvMenuList.setAdapter(mAdapter);
            rvMenuList.setLayoutManager(new LinearLayoutManager(this));
            rvMenuList.setVisibility(View.VISIBLE);
        } else {
            rvMenuList.setVisibility(View.GONE);
        }
    }

    private void setProfileData() {
        try {
            tvPoints.setText(SharePreference.getInstance().getEarningPointString());
            if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                try {
                    UserProfileDetails userDetails = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.UserDetails), UserProfileDetails.class);
                    tvEmail.setText(userDetails.getEmailId());
                    tvName.setText(userDetails.getFirstName() + " " + userDetails.getLastName());
                    if (userDetails.getProfileImage() != null) {
                        Glide.with(HomeActivity.this).load(userDetails.getProfileImage()).override(getResources().getDimensionPixelSize(R.dimen.dim_90),
                                getResources().getDimensionPixelSize(R.dimen.dim_90)).into(ivProfilePic);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                tvEmail.setVisibility(View.GONE);
                tvName.setText("Login / Signup");
            }
            updateNextWithdrawAmount();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openDrawer() {
        try {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                drawer.openDrawer(GravityCompat.START);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeDrawer() {
        try {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else if (!isHomeSelected) {
                performHomeClick();
            } else if (doubleBackToExitPressedOnce) {
                if (CommonMethodsUtils.isShowAppLovinAds() && responseMain.getIsBackAdsInterstitial() != null) {
                    if (responseMain.getIsBackAdsInterstitial().equals(Constants.APPLOVIN_INTERSTITIAL)) {
                        AdsUtil.showAppLovinInterstitialAd(HomeActivity.this, new AdsUtil.AdShownListener() {
                            @Override
                            public void onAdDismiss() {
                                showFinalExitPopup();
                            }
                        });
                    } else if (responseMain.getIsBackAdsInterstitial().equals(Constants.APPLOVIN_REWARD)) {
                        AdsUtil.showAppLovinRewardedAd(HomeActivity.this, new AdsUtil.AdShownListener() {
                            @Override
                            public void onAdDismiss() {
                                showFinalExitPopup();
                            }
                        });
                    } else {
                        CommonMethodsUtils.logFirebaseEvent(HomeActivity.this, "FeatureUsabilityItemId", "FeatureUsabilityEvent", "Home", "Interstitial Ad Not Loaded -> Exit");
                        exitApp();
                    }
                } else {
                    CommonMethodsUtils.logFirebaseEvent(HomeActivity.this, "FeatureUsabilityItemId", "FeatureUsabilityEvent", "Home", "Not Show Ad -> Exit");
                    exitApp();
                }
            } else {
                showExitDialog(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showFinalExitPopup() {
        try {
            dialogExitDialogAfterInterstitial = new Dialog(HomeActivity.this, android.R.style.Theme_Light);
            dialogExitDialogAfterInterstitial.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
            dialogExitDialogAfterInterstitial.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogExitDialogAfterInterstitial.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            dialogExitDialogAfterInterstitial.setContentView(R.layout.popup_app_exit_after_interstitial);
            dialogExitDialogAfterInterstitial.setCancelable(true);
            TextView tvTitle = dialogExitDialogAfterInterstitial.findViewById(R.id.tvTitle);
            tvTitle.setText("Thank You For Using\n" + getString(R.string.app_name) + "!");

            ImageView ivClose = dialogExitDialogAfterInterstitial.findViewById(R.id.ivClose);
            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogExitDialogAfterInterstitial.dismiss();
                }
            });
            dialogExitDialogAfterInterstitial.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    if (handlerExit != null)
                        handlerExit.removeCallbacksAndMessages(null);
                }
            });
            dialogExitDialogAfterInterstitial.setOnKeyListener(new Dialog.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog1, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                        dialogExitDialogAfterInterstitial.dismiss();
                    }
                    return true;
                }
            });

            Button btnOk = dialogExitDialogAfterInterstitial.findViewById(R.id.btnOk);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommonMethodsUtils.logFirebaseEvent(HomeActivity.this, "FeatureUsabilityItemId", "FeatureUsabilityEvent", "Home", "Show Interstitial Ad -> Exit");
                    exitApp();
                }
            });

            dialogExitDialogAfterInterstitial.show();
            ProgressView progressView = dialogExitDialogAfterInterstitial.findViewById(R.id.progressBar);
            progressView.progressAnimate();
            progressView.setProgress(100);
            handlerExit = new Handler(Looper.getMainLooper());
            handlerExit.postDelayed(new Runnable() {
                @Override
                public void run() {
                    CommonMethodsUtils.logFirebaseEvent(HomeActivity.this, "FeatureUsabilityItemId", "FeatureUsabilityEvent", "Home", "Show Interstitial Ad -> Exit");
                    exitApp();
                }
            }, 2500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exitApp() {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            if (dialogExitDialogAfterInterstitial != null && dialogExitDialogAfterInterstitial.isShowing()) {
                dialogExitDialogAfterInterstitial.dismiss();
            }
            finishAffinity();
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showExitDialog(boolean isShow) {
        try {
            if (isShow && CommonMethodsUtils.isShowAppLovinAds() && responseMain.getIsBackAdsInterstitial() != null && !responseMain.getIsBackAdsInterstitial().equals("0")) {
                try {
                    doubleBackToExitPressedOnce = true;
                    CommonMethodsUtils.setToast(this, getString(R.string.tap_to_exit));
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
            if (isShow && ((responseMain.getExitDialog() == null && responseMain.getIsShowNativeAdsOnAppExit() != null && responseMain.getIsShowNativeAdsOnAppExit().equals("0")) || isExitNativeNotLoaded)) {
                try {
                    doubleBackToExitPressedOnce = true;
                    CommonMethodsUtils.setToast(this, getString(R.string.tap_to_exit));
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
            if (dialog == null) {
                dialog = new Dialog(HomeActivity.this, android.R.style.Theme_Light);
                dialog.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                dialog.setContentView(R.layout.popup_app_exit);
                dialog.setCancelable(false);

                LinearLayout layoutParent = dialog.findViewById(R.id.layoutParent);
                frameLayoutExit = dialog.findViewById(R.id.fl_adplaceholder);
                TextView tvTapAgainToExit = dialog.findViewById(R.id.tvTapAgainToExit);
                if (responseMain.getExitDialog() != null) {
                    View viewCustomAd = getLayoutInflater().inflate(R.layout.ad_exit_dialog_custom_ad, null);
                    ImageView ivExitDialogImage = viewCustomAd.findViewById(R.id.ad_media);
                    LottieAnimationView ivLottieView = dialog.findViewById(R.id.ivLottieView);
                    if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getExitDialog().getImage())) {
                        if (responseMain.getExitDialog().getImage().contains("json")) {
                            ivExitDialogImage.setVisibility(View.GONE);
                            ivLottieView.setVisibility(View.VISIBLE);
                            CommonMethodsUtils.setLottieAnimation(ivLottieView, responseMain.getExitDialog().getImage());
                            ivLottieView.setRepeatCount(LottieDrawable.INFINITE);
                            ivLottieView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    CommonMethodsUtils.Redirect(HomeActivity.this, responseMain.getExitDialog().getScreenNo(), responseMain.getExitDialog().getTitle(), responseMain.getExitDialog().getUrl(), null, null, responseMain.getExitDialog().getImage());
                                }
                            });
                        } else {
                            Glide.with(HomeActivity.this)
                                    .load(responseMain.getExitDialog().getImage())
                                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                                    .into(ivExitDialogImage);
                            ivExitDialogImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    CommonMethodsUtils.Redirect(HomeActivity.this, responseMain.getExitDialog().getScreenNo(), responseMain.getExitDialog().getTitle(), responseMain.getExitDialog().getUrl(), null, null, responseMain.getExitDialog().getImage());
                                }
                            });
                        }
                    } else {
                        ivExitDialogImage.setVisibility(View.GONE);
                    }
                    TextView tvTitle = viewCustomAd.findViewById(R.id.ad_headline);
                    if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getExitDialog().getTitle())) {
                        tvTitle.setText(responseMain.getExitDialog().getTitle());
                    } else {
                        tvTitle.setVisibility(View.GONE);
                    }
                    TextView tvDescription = viewCustomAd.findViewById(R.id.ad_body);
                    if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getExitDialog().getDescription())) {
                        tvDescription.setText(responseMain.getExitDialog().getDescription());
                    } else {
                        tvDescription.setVisibility(View.GONE);
                    }
                    Button btnAction = viewCustomAd.findViewById(R.id.ad_call_to_action);
                    if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getExitDialog().getBtnName())) {
                        btnAction.setText(responseMain.getExitDialog().getBtnName());
                    }
                    if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getExitDialog().getBtnColor())) {
                        btnAction.getBackground().setTint(Color.parseColor(responseMain.getExitDialog().getBtnColor()));
                    }

                    btnAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CommonMethodsUtils.Redirect(HomeActivity.this, responseMain.getExitDialog().getScreenNo(), responseMain.getExitDialog().getTitle(), responseMain.getExitDialog().getUrl(), null, null, responseMain.getExitDialog().getImage());
                        }
                    });
                    // Ensure that the parent view doesn't already contain an ad view.
                    frameLayoutExit.removeAllViews();
                    // Place the AdView into the parent.
                    frameLayoutExit.addView(viewCustomAd);
                    frameLayoutExit.setVisibility(View.VISIBLE);
                } else {
                    if (responseMain.getIsShowNativeAdsOnAppExit() != null && responseMain.getIsShowNativeAdsOnAppExit().equals(Constants.APPlOVIN_AD)) {
                        loadAppLovinNativeAdsExit();
                    } else {
                        isExitNativeNotLoaded = true;
                    }
                }
                tvTapAgainToExit.setOnClickListener(view -> {
                    exitApp();
                });
                layoutParent.setOnClickListener(view -> {
                    dialog.dismiss();
                });
            }
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    doubleBackToExitPressedOnce = false;
                }
            });
            dialog.setOnKeyListener(new Dialog.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog1, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                        if (doubleBackToExitPressedOnce) {
                            CommonMethodsUtils.logFirebaseEvent(HomeActivity.this, "FeatureUsabilityItemId", "FeatureUsabilityEvent", "Home", isExitNativeNotLoaded ? "Exit Dialog With Custom Ad -> Exit" : "Exit Dialog With Native Ad -> Exit");
                            exitApp();
                        }
                    }
                    return true;
                }
            });
            if (isShow && !dialog.isShowing()) {
                doubleBackToExitPressedOnce = true;
                dialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAppLovinNativeAdsExit() {
        try {
            nativeAdLoaderExit = new MaxNativeAdLoader(CommonMethodsUtils.getRandomAdUnitId(responseMain.getLovinNativeID()), HomeActivity.this);
            nativeAdLoaderExit.setNativeAdListener(new MaxNativeAdListener() {
                @Override
                public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad) {
                    if (nativeAdExit != null) {
                        nativeAdLoaderExit.destroy(nativeAdExit);
                    }
                    nativeAdExit = ad;
                    frameLayoutExit.removeAllViews();

                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) frameLayoutExit.getLayoutParams();
                    params.height = getResources().getDimensionPixelSize(R.dimen.dim_300);
                    params.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    frameLayoutExit.setLayoutParams(params);
                    frameLayoutExit.setPadding((int) getResources().getDimension(R.dimen.dim_10), (int) getResources().getDimension(R.dimen.dim_10), (int) getResources().getDimension(R.dimen.dim_10), (int) getResources().getDimension(R.dimen.dim_10));
                    frameLayoutExit.setBackgroundResource(R.drawable.rectangle_white);
                    frameLayoutExit.addView(nativeAdView);
                    frameLayoutExit.setVisibility(View.VISIBLE);
                    //AppLogger.getInstance().e("AppLovin Loaded: ", "===");
                }

                @Override
                public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
                    //AppLogger.getInstance().e("AppLovin Failed: ", error.getMessage());
                    isExitNativeNotLoaded = true;
                }

                @Override
                public void onNativeAdClicked(final MaxAd ad) {

                }
            });
            nativeAdLoaderExit.loadAd();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logoutAndCloseApp() {
        CommonMethodsUtils.doLogout(HomeActivity.this);
        finishAffinity();
    }

    public void onUpdateWalletBalance() {
        tvPoints.setText(SharePreference.getInstance().getEarningPointString());
        updateNextWithdrawAmount();
    }

    private BroadcastReceiver dataChangedBroadcast;
    private IntentFilter intentFilter;

    public void registerBroadcast() {
        if (dataChangedBroadcast == null) {
            dataChangedBroadcast = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //AppLogger.getInstance().e("WATCH WEBSITE", "Broadcast Received==" + intent.getAction());
                    String id = intent.getExtras().getString("id");
                    if (intent.getAction().equals(Constants.DAILY_TARGET_RESULT)) {
                        if (intent.getExtras().getString("status").equals(Constants.STATUS_SUCCESS)) {
                            for (int i = 0; i < dailyTargetAdapter.listMilestones.size(); i++) {
                                if (dailyTargetAdapter.listMilestones.get(i).getId().equals(id)) {
                                    dailyTargetAdapter.listMilestones.get(i).setIsClaimed("1");
                                    dailyTargetAdapter.notifyItemChanged(i);
                                    break;
                                }
                            }
                            tvPoints.setText(SharePreference.getInstance().getEarningPointString());
                            updateNextWithdrawAmount();
                        }
                    } else if (intent.getAction().equals(Constants.QUICK_TASK_RESULT)) {
                        if (intent.getExtras().getString("status").equals(Constants.STATUS_SUCCESS)) {
                            for (int i = 0; i < quickTasksAdapter.listTasks.size(); i++) {
                                if (quickTasksAdapter.listTasks.get(i).getId().equals(id)) {
                                    quickTasksAdapter.listTasks.remove(i);
                                    quickTasksAdapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                            tvPoints.setText(SharePreference.getInstance().getEarningPointString());
                            updateNextWithdrawAmount();
                            if (quickTasksAdapter.listTasks.size() == 0) {
                                layoutInflate.removeView(quickTaskView);
                            }
                        }
                        selectedQuickTaskPos = -1;
                    } else if (intent.getAction().equals(Constants.LIVE_MILESTONE_RESULT)) {
                        if (intent.getExtras().getString("status").equals(Constants.STATUS_SUCCESS)) {
                            try {
                                for (int i = 0; i < milestoneAdapter.listMilestones.size(); i++) {
                                    if (milestoneAdapter.listMilestones.get(i).getId().equals(id)) {
                                        milestoneAdapter.listMilestones.remove(i);
                                        milestoneAdapter.notifyItemRemoved(i);
                                        milestoneAdapter.notifyItemRangeChanged(i, milestoneAdapter.listMilestones.size());
                                        break;
                                    }
                                }
                                if (milestoneAdapter.listMilestones.size() == 0) {
                                    layoutInflate.removeView(viewMilestones);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            };
            intentFilter = new IntentFilter();
            intentFilter.addAction(Constants.DAILY_TARGET_RESULT);
            intentFilter.addAction(Constants.LIVE_MILESTONE_RESULT);
            intentFilter.addAction(Constants.QUICK_TASK_RESULT);
            registerReceiver(dataChangedBroadcast, intentFilter);
        }
    }

    private void unRegisterReceivers() {
        if (dataChangedBroadcast != null) {
//            //AppLogger.getInstance().e("SplashActivity", "Unregister Broadcast");
            unregisterReceiver(dataChangedBroadcast);
            dataChangedBroadcast = null;
        }
    }

}