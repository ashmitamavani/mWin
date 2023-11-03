package com.mwin.reward.utils;

import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Html;
import android.text.format.Formatter;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.LottieListener;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.appluck.sdk.AppLuckSDK;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.mwin.reward.ApplicationController;
import com.mwin.reward.BuildConfig;
import com.mwin.reward.R;
import com.mwin.reward.activity.AToZTypeActivity;
import com.mwin.reward.activity.AdjoeLeaderboardActivity;
import com.mwin.reward.activity.AdjoeLeaderboardHistoryActivity;
import com.mwin.reward.activity.ContactUsActivity;
import com.mwin.reward.activity.EarnedPointHistoryActivity;
import com.mwin.reward.activity.EarnedWalletBalanceActivity;
import com.mwin.reward.activity.EarningOptionsActivity;
import com.mwin.reward.activity.EverydayCheckinActivity;
import com.mwin.reward.activity.EverydayRewardActivity;
import com.mwin.reward.activity.GiveawayGiftActivity;
import com.mwin.reward.activity.HomeActivity;
import com.mwin.reward.activity.LuckyNumberContestActivity;
import com.mwin.reward.activity.MilestoneTargetListActivity;
import com.mwin.reward.activity.MinesweeperActivity;
import com.mwin.reward.activity.MySplashActivity;
import com.mwin.reward.activity.NotificationListActivity;
import com.mwin.reward.activity.NumberStoringActivity;
import com.mwin.reward.activity.OtherMoreAppsActivity;
import com.mwin.reward.activity.RedeemOptionsListActivity;
import com.mwin.reward.activity.ReferUsersActivity;
import com.mwin.reward.activity.ScratchCardListActivity;
import com.mwin.reward.activity.SeeVideoListActivity;
import com.mwin.reward.activity.SigninActivity;
import com.mwin.reward.activity.TaskInfoActivity;
import com.mwin.reward.activity.TaskListActivity;
import com.mwin.reward.activity.TypeTextGameActivity;
import com.mwin.reward.activity.UserProfileActivity;
import com.mwin.reward.activity.WebViewActivity;
import com.mwin.reward.activity.WordStoringActivity;
import com.mwin.reward.async.DeleteUserAccountAsync;
import com.mwin.reward.async.models.AppLuckAd;
import com.mwin.reward.async.models.BottomAds;
import com.mwin.reward.async.models.MainResponseModel;
import com.mwin.reward.async.models.TopAds;
import com.mwin.reward.value.Constants;
import com.pubscale.sdkone.offerwall.OfferWall;
import com.pubscale.sdkone.offerwall.models.OfferWallListener;
import com.pubscale.sdkone.offerwall.models.Reward;
import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.RotationRatingBar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.adjoe.sdk.Adjoe;
import io.adjoe.sdk.AdjoeException;
import io.adjoe.sdk.AdjoeInitialisationListener;
import io.adjoe.sdk.AdjoeNotInitializedException;

public class CommonMethodsUtils {
    public static final String DATE_TIME_FORMAT_STANDARDIZED_UTC = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_STANDARDIZED_UTC = "yyyy-MM-dd";
    public static final Pattern VALID_MOBILE_REGEX =
            Pattern.compile("^[6-9]\\d{9}$", Pattern.CASE_INSENSITIVE);
    static Dialog dialog;
    private static Dialog dialogLoader, dialogAdLoader;
    private static long mLastClickTime = 0;
    private static Handler handler1;
    private static String finalUrl = "";
    private static Activity activityLoad;
    private static WebView webLoader;
    private static Handler handler;
    private static Dialog dialogLoaderOffer;

    public static void showProgressLoader(Context activity) {
        try {
            if (dialogLoader == null || !dialogLoader.isShowing()) {
                //AppLogger.getInstance().e("Activity Loader:", "=================LOADER===============" + activity);
                dialogLoader = new Dialog(activity, android.R.style.Theme_Light);
                dialogLoader.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
                dialogLoader.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogLoader.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                dialogLoader.setCancelable(true);
                dialogLoader.setCanceledOnTouchOutside(true);
                dialogLoader.setContentView(R.layout.popup_progress_loader);
                dialogLoader.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dismissProgressLoader() {
        try {
            if (dialogLoader != null && dialogLoader.isShowing()) {
                dialogLoader.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showAdLoader(Context activity, String title) {
        try {
            if (dialogAdLoader == null || !dialogAdLoader.isShowing()) {
                dialogAdLoader = new Dialog(activity, android.R.style.Theme_Light);
                dialogAdLoader.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
                dialogAdLoader.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogAdLoader.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                dialogAdLoader.setCancelable(true);
                dialogAdLoader.setCanceledOnTouchOutside(true);
                dialogAdLoader.setContentView(R.layout.popup_ads_loader);
                TextView tvTitle = dialogAdLoader.findViewById(R.id.tvTitle);
                tvTitle.setText(title);
                dialogAdLoader.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dismissAdLoader() {
        try {
            if (dialogAdLoader != null && dialogAdLoader.isShowing()) {
                dialogAdLoader.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            View view = activity.getCurrentFocus();
            if (view == null) {
                view = new View(activity);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void setToast(Context _mContext, String str) {
        Toast toast = Toast.makeText(_mContext, str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void RateApp(Context context) {
        final String appName = context.getPackageName();
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
        } catch (ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
        }
    }

    public static void setDayNightTheme(Activity context) {
        try {
//            if (SharePreference.getInstance().getString(SharePreference.NIGHT_MODE) == null || SharePreference.getInstance().getString(SharePreference.NIGHT_MODE).length() <= 0) {
            setLightTheme(context);
//            } else if (SharePreference.getInstance().getString(SharePreference.NIGHT_MODE).equals("no")) {
//                setLightTheme(context);
//            } else if (SharePreference.getInstance().getString(SharePreference.NIGHT_MODE).equals("yes")) {
//                setDarkTheme(context);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setLightTheme(Activity context) {
        Window window = context.getWindow();
        Drawable background = context.getDrawable(R.drawable.toolbar_bg);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        window.setNavigationBarColor(context.getColor(R.color.white));
        if (context instanceof MySplashActivity) {
            window.setStatusBarColor(Color.parseColor("#4530b3"));
        } else if (context instanceof SigninActivity) {
            background = context.getDrawable(R.drawable.toolbar_bg);
            window.setStatusBarColor(context.getColor(android.R.color.transparent));
        } else {
            window.setStatusBarColor(context.getColor(android.R.color.transparent));
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setBackgroundDrawable(background);
    }

    public static void sendEmailUsingGmail(Context c, String subject, String message, String recipient) {
        try {
            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{recipient});
            email.putExtra(Intent.EXTRA_SUBJECT, subject);
            email.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(message));
            //need this to prompts email client only
            email.setType("message/rfc822");
            email.setPackage("com.google.android.gm");
            c.startActivity(Intent.createChooser(email, "Send Feedback"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isShowRatePopup() {
        if (SharePreference.getInstance().getInt(SharePreference.RATE_POPUP_SHOWN_COUNT) >= 5) {
            return false;
        } else if (SharePreference.getInstance().getInt(SharePreference.RATE_POPUP_MOVE_TO_PLAY_STORE_COUNT) > Constants.SHOW_RATE_US_POPUP_COUNT) {
            return false;
        }
        return true;
    }

    public static void showRatePopup(Context context) {
        if (dialog == null || !dialog.isShowing()) {
            try {
                dialog = new Dialog(context, android.R.style.Theme_Light);
                dialog.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.popup_give_review);

                TextView tvMessage = dialog.findViewById(R.id.tvMessage);
                TextView tvRateUs = dialog.findViewById(R.id.tvRateUs);
                ImageView ivClose = dialog.findViewById(R.id.ivClose);
                ImageView ivArrow = dialog.findViewById(R.id.ivArrow);
                // Animation
                Animation animUpDown = AnimationUtils.loadAnimation(context, R.anim.up_down_animation);
                animUpDown.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ivArrow.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                // start the animation
                ivArrow.startAnimation(animUpDown);

                RotationRatingBar ratingBar = dialog.findViewById(R.id.ratingBar);
                ratingBar.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
                    @Override
                    public void onRatingChange(BaseRatingBar ratingBar, float rating, boolean fromUser) {
                        tvMessage.setVisibility(rating < 4 ? View.VISIBLE : View.INVISIBLE);
                        tvRateUs.setVisibility(rating < 4 ? View.INVISIBLE : View.VISIBLE);
                        ivClose.setVisibility(rating < 4 ? View.VISIBLE : View.INVISIBLE);
                    }
                });

                tvRateUs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ratingBar.getRating() < 4) {
                            if (ratingBar.getRating() <= 0) {
                                setToast(context, "Please select rating stars");
                                return;
                            }
                        } else {
                            dialog.dismiss();
                            SharePreference.getInstance().putInt(SharePreference.RATE_POPUP_MOVE_TO_PLAY_STORE_COUNT, (SharePreference.getInstance().getInt(SharePreference.RATE_POPUP_MOVE_TO_PLAY_STORE_COUNT) + 1));
                            SharePreference.getInstance().putBoolean(SharePreference.IS_REVIEW_GIVEN, true);
                            RateApp(context);
                        }
                    }
                });

                ivClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog1) {
                        dialog = null;
                    }
                });
                dialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getBase64(Context c, int drawable, int h, int w) {
        Bitmap bitmap = getResizedBitmap(BitmapFactory.decodeResource(c.getResources(), drawable), h, w);
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        byte[] byteArray = byteStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    public static String getVersionName(Context c) {
        try {
            PackageManager manager = c.getPackageManager();
            PackageInfo info = manager.getPackageInfo(c.getPackageName(), 0);
            return (info.versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getSizeFromBytes(long size) {
        if (size <= 0)
            return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static String getCurrentDate() {
        return new SimpleDateFormat(DATE_FORMAT_STANDARDIZED_UTC).format(new Date());
    }

    public static String getCurrentDateTime() {
        return new SimpleDateFormat(DATE_TIME_FORMAT_STANDARDIZED_UTC).format(new Date());
    }

    public static Date getDateObjectFromString(String datetime) throws ParseException {
        return new SimpleDateFormat(DATE_TIME_FORMAT_STANDARDIZED_UTC).parse(datetime);
    }

    public static long getTimeDifference(Date currentDate, Date previousDate) {
        try {
            long diff = Math.abs(previousDate.getTime() - currentDate.getTime());
            long hour = diff / (60 * 60 * 1000);
            return hour;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean shouldShowRatePopup() {
        try {
            if (SharePreference.getInstance().getString(SharePreference.RATE_POPUP_LAST_SHOW_DATE).length() == 0) {
                SharePreference.getInstance().putString(SharePreference.RATE_POPUP_LAST_SHOW_DATE, CommonMethodsUtils.getCurrentDateTime());
            }
            long timeDiff = 0;
            try {
                timeDiff = CommonMethodsUtils.getTimeDifference(new Date(), CommonMethodsUtils.getDateObjectFromString(SharePreference.getInstance().getString(SharePreference.RATE_POPUP_LAST_SHOW_DATE)));
            } catch (ParseException e) {
                e.printStackTrace();
                SharePreference.getInstance().putString(SharePreference.RATE_POPUP_LAST_SHOW_DATE, CommonMethodsUtils.getCurrentDateTime());
            }
            if (timeDiff >= Constants.ratePopupHourDifference && SharePreference.getInstance().getInt(SharePreference.RATE_POPUP_MOVE_TO_PLAY_STORE_COUNT) < Constants.SHOW_RATE_US_POPUP_COUNT) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void logFirebaseEvent(Context context, String title, String contentType, String eventName, String itemId) {
        try {
            if (!BuildConfig.DEBUG) {
                FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, itemId);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, title);
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType);
                mFirebaseAnalytics.logEvent(eventName, bundle);
                //AppLogger.getInstance().e("EVENT: ", "LOGGED========" + contentType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }

    public static boolean verifyInstallerId(Activity context) {
        // A list with valid installers package name
        List<String> validInstallers = new ArrayList<>(Arrays.asList("com.android.vending", "com.google.android.feedback"));

        // The package name of the app that has installed your app
        final String installer = context.getPackageManager().getInstallerPackageName(context.getPackageName());

        // true if your app has been downloaded from Play Store
        return installer != null && validInstallers.contains(installer);
    }

    public static int getRandomNumberBetweenRange(int min, int max) {
        if (max == 0) {
            return 0;
        }
        Random r = new Random();
        int i1 = r.nextInt(max - min) + min;// min inclusive & max exclusive
        return i1;
    }

    public static String getRandomAdUnitId(List<String> list) {
        return list.get(getRandomNumberBetweenRange(0, list.size()));
    }

    public static boolean isShowAppLovinAds() {
        try {
            MainResponseModel responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
            if (responseMain.getIsAppLovinAdShow() != null && responseMain.getIsAppLovinAdShow().equals("1"))
                return true;
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isShowAppLovinNativeAds() {
        try {
            MainResponseModel responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
            if (responseMain.getIsAppLovinAdShow() != null && responseMain.getIsAppLovinAdShow().equals("1") && responseMain.getLovinNativeID() != null && responseMain.getLovinNativeID().size() > 0)
                return true;
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isLoadAppLovinInterstitialAds() {
        try {
            MainResponseModel responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
            if (responseMain.getIsAppLovinAdShow() != null && responseMain.getIsAppLovinAdShow().equals("1") && responseMain.getLovinInterstitialID() != null && responseMain.getLovinInterstitialID().size() > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isLoadAppLovinRewardedAds() {
        try {
            MainResponseModel responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
            if (responseMain.getIsAppLovinAdShow() != null && responseMain.getIsAppLovinAdShow().equals("1") && responseMain.getLovinRewardID() != null && responseMain.getLovinRewardID().size() > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isShowAppLovinAppOpenAds() {
        try {
            MainResponseModel responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
            if (responseMain.getIsAppLovinAdShow() != null && responseMain.getIsAppLovinAdShow().equals("1") && responseMain.getLovinAppOpenID() != null && responseMain.getLovinAppOpenID().size() > 0)
                return true;
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isShowAppLovinBannerAds() {
        try {
            MainResponseModel responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
            if (responseMain.getIsAppLovinAdShow() != null && responseMain.getIsAppLovinAdShow().equals("1") && responseMain.getLovinBannerID() != null && responseMain.getLovinBannerID().size() > 0)
                return true;
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void NotifyLogin(final Activity activity) {
        try {
            if (activity != null) {
                final Dialog dialog1 = new Dialog(activity, android.R.style.Theme_Light);
                dialog1.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
                dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog1.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                dialog1.setContentView(R.layout.popup_login);
                dialog1.setCancelable(false);

                AppCompatButton btnLogin = dialog1.findViewById(R.id.btnLogin);
                btnLogin.setOnClickListener(v -> {
                    dialog1.dismiss();
                    activity.startActivity(new Intent(activity, SigninActivity.class));
                });

                AppCompatButton btnNotNow = dialog1.findViewById(R.id.btnNotNow);
                btnNotNow.setOnClickListener(v -> {
                    dialog1.dismiss();
                });

                if (!activity.isFinishing()) {
                    dialog1.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void NotifyDeleteAccount(final Activity activity) {
        try {
            if (activity != null) {
                final Dialog dialog1 = new Dialog(activity, android.R.style.Theme_Light);
                dialog1.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
                dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog1.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                dialog1.setContentView(R.layout.popup_logout);
                dialog1.setCancelable(false);

                TextView tvTitle = dialog1.findViewById(R.id.tvTitle);
                tvTitle.setText("Delete Account");
                TextView tvMessage = dialog1.findViewById(R.id.tvMessage);
                tvMessage.setText("You will not be able to use mWin with same account details in future. Are you sure you want to permanently delete your mWin account?");
                RelativeLayout relPopup = dialog1.findViewById(R.id.relPopup);
                relPopup.setVisibility(View.GONE);
                ImageView ivIcon = dialog1.findViewById(R.id.ivIcon);
                ivIcon.setImageResource(R.drawable.ic_delete_forever);

                AppCompatButton btnLogout = dialog1.findViewById(R.id.btnLogout);
                btnLogout.setOnClickListener(v -> {
                    dialog1.dismiss();
                    new DeleteUserAccountAsync(activity);
                });

                AppCompatButton btnNotNow = dialog1.findViewById(R.id.btnNotNow);
                btnNotNow.setText("No");
                btnNotNow.setOnClickListener(v -> {
                    dialog1.dismiss();
                });

                if (!activity.isFinishing()) {
                    dialog1.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void NotifyLogout(final Activity activity, String imageLink) {
        try {
            if (activity != null) {
                final Dialog dialog1 = new Dialog(activity, android.R.style.Theme_Light);
                dialog1.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
                dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog1.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                dialog1.setContentView(R.layout.popup_logout);
                dialog1.setCancelable(false);

                ProgressBar probrBanner = dialog1.findViewById(R.id.probrBanner);
                LottieAnimationView ivLottieView = dialog1.findViewById(R.id.ivLottieView);
                ImageView imgBanner = dialog1.findViewById(R.id.imgBanner);

                if (!CommonMethodsUtils.isStringNullOrEmpty(imageLink)) {
                    if (imageLink.contains(".json")) {
                        probrBanner.setVisibility(View.GONE);
                        imgBanner.setVisibility(View.GONE);
                        ivLottieView.setVisibility(View.VISIBLE);
                        CommonMethodsUtils.setLottieAnimation(ivLottieView, imageLink);
                        ivLottieView.setRepeatCount(LottieDrawable.INFINITE);
                    } else {
                        probrBanner.setVisibility(View.VISIBLE);
                        imgBanner.setVisibility(View.VISIBLE);
                        ivLottieView.setVisibility(View.GONE);
                        Glide.with(activity)
                                .load(imageLink)
                                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                                .listener(new RequestListener<Drawable>() {
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
                                .into(imgBanner);
                    }
                } else {
                    imgBanner.setVisibility(View.GONE);
                    probrBanner.setVisibility(View.GONE);
                }

                AppCompatButton btnLogout = dialog1.findViewById(R.id.btnLogout);
                btnLogout.setOnClickListener(v -> {
                    dialog1.dismiss();
                    doLogout(activity);
                });

                AppCompatButton btnNotNow = dialog1.findViewById(R.id.btnNotNow);
                btnNotNow.setOnClickListener(v -> {
                    dialog1.dismiss();
                });

                if (!activity.isFinishing()) {
                    dialog1.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void doLogout(Activity activity) {
        try {
            String whatsAppAuth = SharePreference.getInstance().getString(SharePreference.isShowWhatsAppAuth);
            String adId = SharePreference.getInstance().getString(SharePreference.AdID);
            String FCMToken = SharePreference.getInstance().getString(SharePreference.FCMregId);

            MainResponseModel responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
            MainResponseModel responseMain1 = new MainResponseModel();

            responseMain1.setLoginSlider(responseMain.getLoginSlider());
            responseMain1.setLoginSliderWhatsApp(responseMain.getLoginSliderWhatsApp());
            responseMain1.setLovinAppOpenID(responseMain.getLovinAppOpenID());
            responseMain1.setLovinBannerID(responseMain.getLovinBannerID());
            responseMain1.setLovinInterstitialID(responseMain.getLovinInterstitialID());
            responseMain1.setLovinNativeID(responseMain.getLovinNativeID());
            responseMain1.setLovinAppOpenID(responseMain.getLovinAppOpenID());
            responseMain1.setIsAppLovinAdShow(responseMain.getIsAppLovinAdShow());

            SharePreference.getInstance().clearSharePrefs();
            SharePreference.getInstance().putString(SharePreference.HomeData, new Gson().toJson(responseMain1));
            SharePreference.getInstance().putString(SharePreference.isShowWhatsAppAuth, whatsAppAuth);
            SharePreference.getInstance().putString(SharePreference.AdID, adId);
            SharePreference.getInstance().putString(SharePreference.FCMregId, FCMToken);
            SharePreference.getInstance().putBoolean(SharePreference.IS_USER_CONSENT_ACCEPTED, true);
            SharePreference.getInstance().putBoolean(SharePreference.IS_FIRST_LOGIN, false);
            SharePreference.getInstance().putBoolean(SharePreference.IS_SKIPPED_LOGIN, true);
            activity.startActivity(new Intent(activity, SigninActivity.class));
            activity.finishAffinity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//
//    public static void loadCustomNativeAds(Activity activity, LinearLayout
//            banner, NativeAds
//                                                   model, TextView lblAdSpace) {
//        View layout2 = LayoutInflater.from(activity).inflate(R.layout.ad_custom_banner, banner, false);
//
//        ImageView imgTopBanner = (ImageView) layout2.findViewById(R.id.imgTopBanner);
//        if (!CommonUtils.isStringNullOrEmpty(model.getImage())) {
//            imgTopBanner.setVisibility(View.VISIBLE);
//            lblAdSpace.setVisibility(View.GONE);
//            banner.setVisibility(View.VISIBLE);
//            Glide.with(activity)
//                    .load(model.getImage())
//                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
//                    .into(imgTopBanner);
//            imgTopBanner.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    openUrl(activity, url);
//                }
//            });
//        } else {
//            imgTopBanner.setVisibility(View.GONE);
//        }
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//        banner.addView(layout2, params);
//    }

    public static void Notify(final Activity activity, String title, String message, boolean isFinish) {
        try {
            if (activity != null) {
                final Dialog dialog1 = new Dialog(activity, android.R.style.Theme_Light);
                dialog1.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
                dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog1.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                dialog1.setContentView(R.layout.popup_message_notify);
                dialog1.setCancelable(false);

                Button btnOk = dialog1.findViewById(R.id.btnOk);
                TextView tvTitle = dialog1.findViewById(R.id.tvTitle);
                tvTitle.setText(title);
//                tvTitle.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
//                        ClipData clip = ClipData.newPlainText("Copied Text", message);
//                        clipboard.setPrimaryClip(clip);
//                        setToast(activity, "Copied");
//                    }
//                });
                TextView tvMessage = dialog1.findViewById(R.id.tvMessage);
                tvMessage.setText(message);
                btnOk.setOnClickListener(v -> {
                    dialog1.dismiss();
                    if (isFinish && !activity.isFinishing()) {
                        activity.finish();
                    }
                });
                if (!activity.isFinishing()) {
                    dialog1.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void NotifyMessage(final Activity activity, String title, String message, boolean isFinish) {
        try {
            if (activity != null) {
                final Dialog dialog1 = new Dialog(activity, android.R.style.Theme_Light);
                dialog1.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
                dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog1.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                dialog1.setContentView(R.layout.popup_notes);
                dialog1.setCancelable(true);

                ImageView ivClose = dialog1.findViewById(R.id.ivClose);
                TextView tvTitle = dialog1.findViewById(R.id.tvTitle);
                tvTitle.setText(title);

                TextView tvMessage = dialog1.findViewById(R.id.tvMessage);
                tvMessage.setText(message);

                ivClose.setOnClickListener(v -> {
                    dialog1.dismiss();
                    if (isFinish && !activity.isFinishing()) {
                        activity.finish();
                    }
                });
                if (!activity.isFinishing()) {
                    dialog1.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void NotifySuccess(final Activity activity, String title, String message, boolean isFinish) {
        try {
            if (activity != null) {
                final Dialog dialog1 = new Dialog(activity, android.R.style.Theme_Light);
                dialog1.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
                dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog1.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                dialog1.setContentView(R.layout.popup_notify_win);
                dialog1.setCancelable(false);

                Button btnOk = dialog1.findViewById(R.id.btnOk);
                TextView tvTitle = dialog1.findViewById(R.id.tvTitle);
                tvTitle.setText(title);
                TextView tvMessage = dialog1.findViewById(R.id.tvMessage);
                tvMessage.setText(message);
                btnOk.setOnClickListener(v -> {
                    dialog1.dismiss();
                    if (isFinish && !activity.isFinishing()) {
                        activity.finish();
                    }
                });
                if (!activity.isFinishing()) {
                    dialog1.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void UpdateApp(final Activity activity, String isForupdate,
                                 final String appurl, String msg) {
        try {
            if (activity != null) {
                final Dialog dialog1 = new Dialog(activity, android.R.style.Theme_Light);
                dialog1.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
                dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog1.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                dialog1.setContentView(R.layout.popup_update_app);

                Button btnUpdate = dialog1.findViewById(R.id.btnUpdate);
                Button btnCancel = dialog1.findViewById(R.id.btnCancel);
                TextView tvMessage = dialog1.findViewById(R.id.tvMessage);
                tvMessage.setText(msg);
                if (isForupdate.equals("1")) {
                    dialog1.setCancelable(false);
                    btnUpdate.setVisibility(View.VISIBLE);
                    btnCancel.setVisibility(View.GONE);
                } else {
                    dialog1.setCancelable(true);
                    btnUpdate.setVisibility(View.VISIBLE);
                    btnCancel.setVisibility(View.VISIBLE);
                }
                btnUpdate.setOnClickListener(v -> {
                    if (!activity.isFinishing() && !isForupdate.equals("1")) {
                        dialog1.dismiss();
                    }
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(appurl));
                        activity.startActivity(browserIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(activity, "No application can handle this request." + " Please install a web browser", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                });
                btnCancel.setOnClickListener(view -> {
                    if (!activity.isFinishing()) {
                        dialog1.dismiss();
                    }
                });
                if (!activity.isFinishing()) {
                    dialog1.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isStringNullOrEmpty(String text) {
        return (text == null || text.trim().equals("null") || text.trim()
                .length() <= 0);
    }

    public static String formatString(double value) {
        try {
//            return (String.format("%,.2f", value));

            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            return formatter.format(value).replace("₹ ", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(value);
    }

    public static void LoadBottomBannerAd(Activity activity, LinearLayout
            layoutBannerAdBottom, BottomAds model, TextView lblAdSpaceBottom, MainResponseModel
                                                  responseMain) {
        try {
            if (model.getType().equals("1")) {
                lblAdSpaceBottom.setHeight(activity.getResources().getDimensionPixelSize(R.dimen.applovin_banner_height));
                MaxAdView adView = new MaxAdView(getRandomAdUnitId(responseMain.getLovinBannerID()), activity);
                adView.setListener(new MaxAdViewAdListener() {
                    @Override
                    public void onAdExpanded(MaxAd ad) {

                    }

                    @Override
                    public void onAdCollapsed(MaxAd ad) {

                    }

                    @Override
                    public void onAdLoaded(MaxAd ad) {
                        lblAdSpaceBottom.setVisibility(View.GONE);
                        layoutBannerAdBottom.removeAllViews();
                        layoutBannerAdBottom.addView(adView);
                    }

                    @Override
                    public void onAdDisplayed(MaxAd ad) {

                    }

                    @Override
                    public void onAdHidden(MaxAd ad) {

                    }

                    @Override
                    public void onAdClicked(MaxAd ad) {

                    }

                    @Override
                    public void onAdLoadFailed(String adUnitId, MaxError error) {
                        layoutBannerAdBottom.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                        layoutBannerAdBottom.setVisibility(View.GONE);
                    }
                });
                adView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, activity.getResources().getDimensionPixelSize(R.dimen.applovin_banner_height)));
                adView.loadAd();
            } else if (model.getType().equals("2")) {
                View layout2 = LayoutInflater.from(activity).inflate(R.layout.custom_banner_ad, layoutBannerAdBottom, false);
                ImageView imgTopBanner = (ImageView) layout2.findViewById(R.id.imgTopBanner);
                if (!CommonMethodsUtils.isStringNullOrEmpty(model.getImage())) {
                    imgTopBanner.setVisibility(View.VISIBLE);
                    lblAdSpaceBottom.setVisibility(View.GONE);
                    Glide.with(activity)
                            .load(model.getImage())
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                            .into(imgTopBanner);
                    imgTopBanner.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openUrl(activity, model.getUrl());
                        }
                    });
                } else {
                    imgTopBanner.setVisibility(View.GONE);
                }
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                layoutBannerAdBottom.addView(layout2, params);
            } else {
                layoutBannerAdBottom.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            layoutBannerAdBottom.setVisibility(View.GONE);
        }
    }

    public static void loadTopBannerAd(Activity activity, LinearLayout layoutTopAds, TopAds model) {
        try {
            if (!CommonMethodsUtils.isStringNullOrEmpty(model.getImage())) {
                layoutTopAds.setVisibility(View.VISIBLE);
                ProgressBar progressBar = layoutTopAds.findViewById(R.id.progressBar);
                if (model.getImage().endsWith(".json")) {
                    LottieAnimationView ivLottieTopBanner = layoutTopAds.findViewById(R.id.ivLottieTopBanner);
                    ivLottieTopBanner.setVisibility(android.view.View.VISIBLE);
                    CommonMethodsUtils.setLottieAnimation(ivLottieTopBanner, model.getImage());
                    ivLottieTopBanner.setRepeatCount(LottieDrawable.INFINITE);
                    ivLottieTopBanner.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CommonMethodsUtils.Redirect(activity, model.getScreenNo(), model.getTitle(), model.getUrl(), model.getId(), model.getTaskId(), model.getImage());
                        }
                    });
                    progressBar.setVisibility(View.GONE);
                } else {
                    ImageView imgTopBanner = layoutTopAds.findViewById(R.id.imgTopBanner);
                    imgTopBanner.setVisibility(View.VISIBLE);
                    imgTopBanner.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CommonMethodsUtils.Redirect(activity, model.getScreenNo(), model.getTitle(), model.getUrl(), model.getId(), model.getTaskId(), model.getImage());
                        }
                    });
                    Glide.with(activity)
                            .load(model.getImage())
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    layoutTopAds.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                                    progressBar.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .into(imgTopBanner);
                }
            } else {
                layoutTopAds.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            layoutTopAds.setVisibility(View.GONE);
        }
    }

    public static void showWatchWebDialog(Context context, String url, int time, String points) {
        try {
            Dialog dialog = new Dialog(context, android.R.style.Theme_Light);
            dialog.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            dialog.setContentView(R.layout.popup_see_website);

            TextView tvMessage = dialog.findViewById(R.id.tvMessage);
            tvMessage.setText(Html.fromHtml("You are winning <b>" + points + " points</b>,<br/>Visit the website for <b>" + time + " seconds</b><br/>and transfer points to your wallet.</b>"));

            Button btnVisitWebsite = dialog.findViewById(R.id.btnVisitWebsite);

            btnVisitWebsite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                    int mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
                    boolean granted;
                    if (mode == AppOpsManager.MODE_DEFAULT) {
                        granted = (context.checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
                    } else {
                        granted = (mode == AppOpsManager.MODE_ALLOWED);
                    }
                    if (granted) {
                        dialog.dismiss();
                        if (context instanceof EverydayCheckinActivity) {
                            ((EverydayCheckinActivity) context).registerBroadcast();
                        }
                        ActivityManager.timeToWatchInSeconds = time;
                        ActivityManager.isShowAppOpenAd = false;
                        ActivityManager.isStartTimer = true;
                        CommonMethodsUtils.openUrlInChrome(context, url);
                    } else {
                        showUsageAccessPermissionDialog(context, "To track this feature you need to give <b>Usage Access Permission</b>. Kindly go to settings screen and turn on toggle button to allow this permission.");
                    }
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showUsageAccessPermissionDialog(Context context, String message) {
        Dialog dialog = new Dialog(context, android.R.style.Theme_Light);
        dialog.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        dialog.setContentView(R.layout.popup_update_app);

        LinearLayout layoutUsageAccessPermission = dialog.findViewById(R.id.layoutUsageAccessPermission);
        layoutUsageAccessPermission.setVisibility(View.VISIBLE);

        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
        tvTitle.setText("Usage Access Permission");

        TextView tvMessage = dialog.findViewById(R.id.tvMessage);
        tvMessage.setText(Html.fromHtml(message));

        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnOk = dialog.findViewById(R.id.btnUpdate);
        btnOk.setText("Go to Settings");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    dialog.dismiss();
                    ActivityManager.isShowAppOpenAd = false;
                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    intent.setData(Uri.parse("package:" + context.getPackageName()));
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    setToast(context, "No able to open settings screen");
                }
            }
        });
        layoutUsageAccessPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    dialog.dismiss();
                    ActivityManager.isShowAppOpenAd = false;
                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    intent.setData(Uri.parse("package:" + context.getPackageName()));
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    setToast(context, "No able to open settings screen");
                }
            }
        });
        dialog.show();
    }

    public static void showUsageAccessPermissionDialog(Context context) {
        Dialog dialog = new Dialog(context, android.R.style.Theme_Light);
        dialog.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        dialog.setContentView(R.layout.popup_usage_access_permission);

        LinearLayout layoutUsageAccessPermission = dialog.findViewById(R.id.layoutUsageAccessPermission);

        Button btnContinue = dialog.findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    dialog.dismiss();
                    ActivityManager.isShowAppOpenAd = false;
                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    intent.setData(Uri.parse("package:" + context.getPackageName()));
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    setToast(context, "Not able to open settings screen");
                }
            }
        });
        layoutUsageAccessPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    dialog.dismiss();
                    ActivityManager.isShowAppOpenAd = false;
                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    intent.setData(Uri.parse("package:" + context.getPackageName()));
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    setToast(context, "Not able to open settings screen");
                }
            }
        });
        dialog.show();
    }

//    public static void showPopupAds(Context c, PopupAds popupAds, String sharePrefKey) {
//        try {
//            if (popupAds != null && !CommonUtils.isStringNullOrEmpty(popupAds.getImage())) {
//                if (SharePreference.getInstance().getString(sharePrefKey + popupAds.getId()).length() == 0 || !SharePreference.getInstance().getString(sharePrefKey + popupAds.getId()).equals(CommonUtils.getCurrentDate())) {
//                    SharePreference.getInstance().putString(sharePrefKey + popupAds.getId(), CommonUtils.getCurrentDate());
//
//                    Dialog dialog = new Dialog(c, android.R.style.Theme_Light);
//                    dialog.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
//                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                    dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                    dialog.setContentView(R.layout.dialog_home_ad);
//
//                    ImageView ivCloseAd = dialog.findViewById(R.id.ivCloseAd);
//                    ImageView ivMiniAd = dialog.findViewById(R.id.ivMiniAd);
//
//                    Glide.with(c)
//                            .load(popupAds.getImage()).addListener(new RequestListener<Drawable>() {
//                                @Override
//                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                                    if (dialog.isShowing())
//                                        dialog.dismiss();
//                                    return false;
//                                }
//
//                                @Override
//                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                                    ivCloseAd.setVisibility(View.VISIBLE);
//                                    return false;
//                                }
//                            })
//                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
//                            .into(ivMiniAd);
//
//                    ivMiniAd.setOnClickListener(v -> CommonUtils.openUrl(c, popupAds.getUrl()));
//
//                    ivCloseAd.setOnClickListener(view -> dialog.dismiss());
//
//                    dialog.show();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static void openUrl(Context c, String url) {
        if (!CommonMethodsUtils.isStringNullOrEmpty(url)) {
            if (url.contains("/t.me/") || url.contains("telegram") || url.contains("facebook.com") || url.contains("instagram.com") || url.contains("youtube.com") || url.contains("play.google.com/store/apps/details") || url.contains("market.android.com/details")) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    c.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    openUrlInChrome(c, url);
                }
            } else {
                openUrlInChrome(c, url);
            }
        }
    }

    public static void openUrlInChrome(Context c, String url) {
        //AppLogger.getInstance().e("URL openUrlInChrome :============", url);
        if (!CommonMethodsUtils.isStringNullOrEmpty(url)) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            try {
                intent.setPackage("com.android.chrome");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                c.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    intent.setPackage(null);
                    c.startActivity(intent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    setToast(c, "No application found to handle this url");
                }
            }
        }
    }

    public static void setViewTheme(View view, String colorStr) {
        int color;
        if (colorStr == null || colorStr.length() == 0) {
            return;
        } else {
            color = Color.parseColor(colorStr);
        }

        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * 0.8f);
        int g = Math.round(Color.green(color) * 0.8f);
        int b = Math.round(Color.blue(color) * 0.8f);
        int darkColor = Color.argb(a,
                Math.min(r, 255),
                Math.min(g, 255),
                Math.min(b, 255));
        if (view instanceof Button) {

            int[][] states = new int[][]{
                    new int[]{android.R.attr.state_pressed},  // pressed
                    new int[]{-android.R.attr.state_pressed},  // pressed
            };

            int[] colors = new int[]{
                    darkColor,
                    color,
            };

            ColorStateList myList = new ColorStateList(states, colors);
            view.setBackgroundTintList(myList);
        }
    }

    public static void loadBannerAds(Activity c, LinearLayout layoutBannerAd, TextView
            lblAdSpace) {
        try {
            if (CommonMethodsUtils.isShowAppLovinBannerAds()) {
                lblAdSpace.setHeight(c.getResources().getDimensionPixelSize(R.dimen.applovin_banner_height));
                MaxAdView adView = new MaxAdView(getRandomAdUnitId(new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class).getLovinBannerID()), c);
                adView.setListener(new MaxAdViewAdListener() {
                    @Override
                    public void onAdExpanded(MaxAd ad) {

                    }

                    @Override
                    public void onAdCollapsed(MaxAd ad) {

                    }

                    @Override
                    public void onAdLoaded(MaxAd ad) {
                        lblAdSpace.setVisibility(View.GONE);
                        layoutBannerAd.removeAllViews();
                        layoutBannerAd.addView(adView);
                    }

                    @Override
                    public void onAdDisplayed(MaxAd ad) {

                    }

                    @Override
                    public void onAdHidden(MaxAd ad) {

                    }

                    @Override
                    public void onAdClicked(MaxAd ad) {

                    }

                    @Override
                    public void onAdLoadFailed(String adUnitId, MaxError error) {
                        //AppLogger.getInstance().e("APPLOVIN BANNER onAdLoadFailed==", "===" + error.getMessage());
                        layoutBannerAd.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                        //AppLogger.getInstance().e("APPLOVIN BANNER onAdDisplayFailed==", "===" + error.getMessage());
                        layoutBannerAd.setVisibility(View.GONE);
                    }
                });
                adView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, c.getResources().getDimensionPixelSize(R.dimen.applovin_banner_height)));
                adView.loadAd();
            } else {
                layoutBannerAd.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            layoutBannerAd.setVisibility(View.GONE);
        }
    }

    public static boolean appInstalledOrNot(Activity activity, String uri) {
        PackageManager pm = activity.getPackageManager();
        try {
            pm.getPackageInfo(uri, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void showImageLotteGIF(Activity mActivity, String imgURL, ImageView ivIcon, ProgressBar progressBar) {
        if (imgURL != null && imgURL.length() > 0) {
            Glide.with(mActivity)
                    .load(imgURL)

                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            if (progressBar != null) {
                                progressBar.setVisibility(View.GONE);
                            }
                            return false;
                        }
                    })
                    .into(ivIcon);
        }
    }

    public static void showImageLotteGIF(Context context, String jsonURL, String imgURL, LottieAnimationView ltView, ImageView ivBanner, ImageView ivGIF, ProgressBar progressBar, CardView cardView) {
        if (jsonURL != null && jsonURL.length() > 0) {
            ivGIF.setVisibility(View.GONE);
            ivBanner.setVisibility(View.GONE);
            if (cardView != null) {
                cardView.setVisibility(View.VISIBLE);
            }
            CommonMethodsUtils.setLottieAnimation(ltView, jsonURL);
            ltView.setRepeatCount(LottieDrawable.INFINITE);
            ltView.playAnimation();
            ltView.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }

                    //AppLogger.getInstance().e("Lottie1--)", "Start");
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                    //AppLogger.getInstance().e("Lottie1--)", "Repeat");
                }
            });
        } else {
            if (imgURL != null && imgURL.length() > 0) {
                if (imgURL.contains("gif")) {
                    ivGIF.setVisibility(View.VISIBLE);
                    ltView.setVisibility(View.GONE);
                    if (cardView != null) {
                        cardView.setVisibility(View.GONE);
                    }
                    ivBanner.setVisibility(View.GONE);
                    Glide.with(context)
                            .load(imgURL)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                                    if (progressBar != null) {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                    return false;
                                }
                            })
                            .into(ivGIF);
                } else {

                    ivGIF.setVisibility(View.GONE);
                    ltView.setVisibility(View.GONE);
                    if (cardView != null) {
                        cardView.setVisibility(View.GONE);
                    }
                    ivBanner.setVisibility(View.VISIBLE);
                    Glide.with(context)
                            .load(imgURL)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                                    if (progressBar != null) {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                    return false;
                                }
                            })
                            .into(ivBanner);
                }

            }
        }

    }

    public static String changeDateFormat(String currentFormat, String requiredFormat, String dateString) {
        String result = "";
        if (isStringNullOrEmpty(dateString)) {
            return result;
        }
        SimpleDateFormat formatterOld = new SimpleDateFormat(currentFormat, Locale.getDefault());
        SimpleDateFormat formatterNew = new SimpleDateFormat(requiredFormat, Locale.getDefault());
        Date date = null;
        try {
            date = formatterOld.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            result = formatterNew.format(date);
        }
        return result;
    }

    public static void setEnableDisable(final Activity activity, final View v) {
        v.setEnabled(false);
        Timer buttonTimer = new Timer();
        buttonTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        v.setEnabled(true);
                    }
                });
            }
        }, 2000);
    }

    public static String getPathFromURI(final Context context, final Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                if (id != null && (id.startsWith("msf:") || id.startsWith("raw:"))) {
                    final File file = new File(context.getCacheDir(), "temp." + Objects.requireNonNull(context.getContentResolver().getType(uri)).split("/")[1]);
                    try (final InputStream inputStream = context.getContentResolver().openInputStream(uri); OutputStream output = new FileOutputStream(file)) {
                        final byte[] buffer = new byte[4 * 1024]; // or other buffer size
                        int read;

                        while ((read = inputStream.read(buffer)) != -1) {
                            output.write(buffer, 0, read);
                        }

                        output.flush();
                        return file.getAbsolutePath();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    return null;
                } else {
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    return getDataColumn(context, contentUri, null, null);
                }
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static long convertTimeInMillis(String dateTimeFormat, String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        try {
            Date mDate = sdf.parse(date);
            return mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void Redirect(Activity context, String screenNo, String title, String url, String id, String taskId, String imageLink) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        //AppLogger.getInstance().e("ClickEvent--)", "" + url + "--)" + screenNo);
        if (screenNo != null && screenNo.length() > 0) {
            switch (screenNo) {
                case "1":
                    if (context instanceof HomeActivity) {
                        ((HomeActivity) context).performHomeClick();
                    } else {
                        Intent intent = new Intent(context, HomeActivity.class);
                        context.startActivity(intent);
                    }
                    break;
                case "2":
                    // Open link externally
                    openUrl(context, url);
                    break;
                case "3":
                    if (title != null || url != null) {
                        Intent in = new Intent(context, WebViewActivity.class);
                        in.putExtra("Title", title);
                        in.putExtra("URL", url);
                        context.startActivity(in);
                    }
                    break;
                case "5":
                    if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                        Intent intent = new Intent(context, RedeemOptionsListActivity.class);
                        context.startActivity(intent);
                    } else {
                        NotifyLogin(context);
                    }
                    break;
                case "6":
                    if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                        Intent intent = new Intent(context, EarnedPointHistoryActivity.class);
                        intent.putExtra("type", Constants.HistoryType.WITHDRAW_HISTORY);
                        intent.putExtra("title", "Withdrawal History");
                        context.startActivity(intent);
                    } else {
                        NotifyLogin(context);
                    }
                    break;
                case "7":
                    if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                        Intent inHistory = new Intent(context, EarnedPointHistoryActivity.class);
                        context.startActivity(inHistory);
                    } else {
                        NotifyLogin(context);
                    }
                    break;
                case "8":
                    if (context instanceof HomeActivity) {
                        ((HomeActivity) context).performInviteClick();
                    } else {
                        Intent intentInvite = new Intent(context, ReferUsersActivity.class);
                        context.startActivity(intentInvite);
                    }
                    break;
                case "9":
                    if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                        Intent inWallet = new Intent(context, EarnedWalletBalanceActivity.class);
                        context.startActivity(inWallet);
                    } else {
                        NotifyLogin(context);
                    }
                    break;
                case "10":
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    context.startActivity(intent);
                    break;
                case "11":
                    if (context instanceof HomeActivity) {
                        ((HomeActivity) context).performTaskClick();
                    } else {
                        Intent intentTask = new Intent(context, TaskListActivity.class);
                        context.startActivity(intentTask);
                    }
                    break;
                case "12":
                    Intent inTaskDetail = new Intent(context, TaskInfoActivity.class);
                    if (taskId != null) {
                        inTaskDetail.putExtra("taskId", taskId);
                    } else {
                        inTaskDetail.putExtra("taskId", id);
                    }
                    context.startActivity(inTaskDetail);
                    break;
                case "13":
                    Intent inNotification = new Intent(context, NotificationListActivity.class);
                    context.startActivity(inNotification);
                    break;
                case "14":
                    if (context instanceof HomeActivity) {
                        ((HomeActivity) context).performRewardClick();
                    } else {
                        Intent intentReward = new Intent(context, EarningOptionsActivity.class);
                        context.startActivity(intentReward);
                    }
                    break;
                case "15":
                    if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                        if (url != null) {
                            loadOffer(context, url);
                        }
                    } else {
                        NotifyLogin(context);
                    }
                    break;
                case "16":
                    break;
                case "17":
                    Intent intentTaskList = new Intent(context, TaskListActivity.class);
                    context.startActivity(intentTaskList);
                    break;
                case "18":
                    NotifyLogout(context, imageLink);
                    break;
                case "19":
//                    Intent inGroupTask = new Intent(context, TopOfferListActivity.class);
//                    context.startActivity(inGroupTask);
                    break;
                case "20":
//                    Intent inTopProductTask = new Intent(context, TopProductListActivity.class);
//                    context.startActivity(inTopProductTask);
                    break;
                case "21":
                    context.startActivity(new Intent(context, SeeVideoListActivity.class));
                    break;
                case "22":
                    if (url != null && url.length() > 0) {
                        Intent i = new Intent(context, WebViewActivity.class);
                        i.putExtra("URL", url);
                        i.putExtra("Title", title);
                        context.startActivity(i);
                    }
                    break;
                case "23":
                    context.startActivity(new Intent(context, ContactUsActivity.class).putExtra("title", title));
                    break;
                case "24":
                    showRatePopup(context);
                    break;
                case "25":
                    context.startActivity(new Intent(context, OtherMoreAppsActivity.class));
                    break;
                case "26":
                    context.startActivity(new Intent(context, ScratchCardListActivity.class));
                    break;
                case "27":
                    context.startActivity(new Intent(context, GiveawayGiftActivity.class));
                    break;
                case "28":
                    context.startActivity(new Intent(context, LuckyNumberContestActivity.class));
                    break;
                case "31":
                    openAdjoeOfferWall(context);
                    break;
                case "32":
                    context.startActivity(new Intent(context, EverydayRewardActivity.class));
                    break;
                case "41":
                    if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                        Intent intentTextTyping = new Intent(context, TypeTextGameActivity.class);
                        context.startActivity(intentTextTyping);
                    } else {
                        NotifyLogin(context);
                    }
                    break;
                case "42":
                    Intent intentTextTyping = new Intent(context, MilestoneTargetListActivity.class);
                    context.startActivity(intentTextTyping);
                    break;
                case "43":
                    Intent intentNumberSorting = new Intent(context, NumberStoringActivity.class);
                    context.startActivity(intentNumberSorting);
                    break;
                case "44":
                    Intent intentWordSorting = new Intent(context, WordStoringActivity.class);
                    context.startActivity(intentWordSorting);
                    break;
                case "45":
                    Intent adjoeLeaderboard = new Intent(context, AdjoeLeaderboardActivity.class);
                    context.startActivity(adjoeLeaderboard);
                    break;
                case "46":
                    Intent adjoeLeaderboardHistory = new Intent(context, AdjoeLeaderboardHistoryActivity.class);
                    context.startActivity(adjoeLeaderboardHistory);
                    break;
                case "47":
                    try {
                        MainResponseModel responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
                        if (!isStringNullOrEmpty(responseMain.getIsShowAppluck()) && responseMain.getIsShowAppluck().equals("1")) {
                            AppLuckSDK.openInteractiveAds(responseMain.getInterAppluckID(), 1, 1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "48":
                    try {
                        MainResponseModel responseMain1 = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
                        if (!isStringNullOrEmpty(responseMain1.getIsShowAppluck()) && responseMain1.getIsShowAppluck().equals("1")) {
                            AppLuckSDK.openInteractiveAds(responseMain1.getIncentiveAppluckID(), 2, 3);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "49":
                    try {
                        context.startActivity(new Intent(context, MinesweeperActivity.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "50":
                    try {
                        context.startActivity(new Intent(context, AToZTypeActivity.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "51":
                    try {
                        MainResponseModel responseMain1 = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
                        if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain1.getIsShowPubScale()) && responseMain1.getIsShowPubScale().matches("1")) {
                            OfferWall.launch(context, new OfferWallListener() {
                                @Override
                                public void onOfferWallShowed() {
                                    logFirebaseEvent(context, "FeatureUsabilityItemId", "FeatureUsabilityEvent", "Pubscale", "Offerwall Opened");
                                    AppLogger.getInstance().e("INIT", "PUBSCALE onOfferWallShowed==========");
                                }

                                @Override
                                public void onOfferWallClosed() {
                                    AppLogger.getInstance().e("INIT", "PUBSCALE onOfferWallClosed==========");
                                }

                                @Override
                                public void onRewardClaimed(Reward reward) {
                                    AppLogger.getInstance().e("INIT", "PUBSCALE onRewardClaimed==========");
                                }

                                @Override
                                public void onFailed(String s) {
                                    AppLogger.getInstance().e("INIT", "PUBSCALE onFailed==========");
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    public static void showDefaultAppLuck(Activity activity, LinearLayout llAddView, RelativeLayout relMainFlotAppLuck, String defaultAdUnitId) {
        try {
//            Logger_App.getInstance().e("LOAD AppLuck DEFAULT AD ", "" + defaultAdUnitId+" SCREEN: "+activity);
            // relMainFlotAppLuck.setVisibility(View.GONE);
            if (AppLuckSDK.isPlacementReady(defaultAdUnitId)) {
                if (llAddView != null) {
                    View iconView = AppLuckSDK.showInteractiveEntrance(activity, defaultAdUnitId, 1);
                    if (iconView != null) {
                        if (iconView.getParent() != null) {
                            ViewGroup viewGroup = (ViewGroup) iconView.getParent();
                            viewGroup.removeView(iconView);
                        }
                        iconView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        relMainFlotAppLuck.setVisibility(View.VISIBLE);
                        llAddView.removeAllViews();
                        llAddView.addView(iconView);
//                        Logger_App.getInstance().e("LOAD AppLuck DEFAULT AD ", "" + defaultAdUnitId+" LOADED===");
                    }
                    return;
                }

            }
            if (handler1 != null) {
                handler1.removeCallbacks(null);
            }
//            Logger_App.getInstance().e("LOAD AppLuck DEFAULT AD ", "" + defaultAdUnitId+" NOT LOADED===");
            handler1 = new Handler();
            handler1.postDelayed(() -> {
                showDefaultAppLuck(activity, llAddView, relMainFlotAppLuck, defaultAdUnitId);
            }, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadOffer(Activity activity, String str) {
        activityLoad = activity;
        dialogLoaderOffer = new Dialog(activity, android.R.style.Theme_Light);
        dialogLoaderOffer.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
        dialogLoaderOffer.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogLoaderOffer.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        dialogLoaderOffer.setCancelable(true);
        dialogLoaderOffer.setCanceledOnTouchOutside(true);
        dialogLoaderOffer.setContentView(R.layout.popup_progress_loader);

        webLoader = dialogLoaderOffer.findViewById(R.id.webloader);

        if (!activity.isFinishing() && !dialogLoaderOffer.isShowing()) {
            dialogLoaderOffer.show();
        }

        webLoader.getSettings().setJavaScriptEnabled(true);
        webLoader.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView webView, String str) {
            }

            public boolean shouldOverrideUrlLoading(WebView webView, String str) {
                webView.loadUrl(str);
                return true;
            }

            public void onPageStarted(WebView webView, String str, Bitmap bitmap) {
                super.onPageStarted(webView, str, bitmap);
                //AppLogger.getInstance().e("finalUrl11--)", "" + finalUrl);
                if (str.startsWith("market://") || str.startsWith("intent://") || str.startsWith("http://") || str.startsWith("https://")) {
                    finalUrl = str;
                }
                if (str.startsWith("market://") || str.startsWith("intent://")) {
                    if (handler != null) {
                        handler.removeCallbacksAndMessages(null);
                    }
                    openWebPage();
                }
            }
        });
        webLoader.loadUrl(str);
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        startTimer();
    }

    public static void openWebPage() {
        try {
            if (dialogLoaderOffer != null && !activityLoad.isFinishing()) {
                dialogLoaderOffer.dismiss();
            }

            if (finalUrl != null) {
                if (finalUrl.startsWith("intent:")) {
                    try {
                        Intent intent = Intent.parseUri(finalUrl, Intent.URI_INTENT_SCHEME);
                        if (intent.resolveActivity(activityLoad.getPackageManager()) != null) {
                            activityLoad.startActivity(intent);
                            return;
                        }
                        //try to find fallback url
                        String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                        if (fallbackUrl != null) {
                            webLoader.loadUrl(fallbackUrl);
                            return;
                        }
                        //invite to install
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW).setData(
                                Uri.parse("market://details?id=" + intent.getPackage()));
                        if (marketIntent.resolveActivity(activityLoad.getPackageManager()) != null) {
                            activityLoad.startActivity(marketIntent);
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (finalUrl.startsWith("market://")) {
                    try {
                        //invite to install
                        String packageName = finalUrl.substring("market://details?id=".length());
                        if (packageName.contains("&")) {
                            packageName = packageName.substring(0, packageName.indexOf("&"));
                        }
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("market://details?id=" + packageName));
                        if (marketIntent.resolveActivity(activityLoad.getPackageManager()) != null) {
                            activityLoad.startActivity(marketIntent);
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                ActivityManager.isShowAppOpenAd = false;
                CommonMethodsUtils.openUrl(activityLoad, finalUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        finalUrl = "";
        activityLoad = null;
        webLoader = null;
        handler = null;
        dialogLoaderOffer = null;
    }

    private static void startTimer() {
        handler = new Handler();
        handler.postDelayed(CommonMethodsUtils::openWebPage, 8000);
    }

    public static String modifyDateLayout(String inputDate) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(inputDate);
            String time = new SimpleDateFormat("dd MMM yyyy, hh:mm a").format(date);
            time.replace("AM", "am");
            time.replace("PM", "pm");
            return time;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void GetCoinAnimation(Context context, final RelativeLayout frameLayout, View view) {
        int i;
        float f;
        float f2;
        float f3;
        Random random;
        int i2;
        RelativeLayout frameLayout2 = frameLayout;
        Random random2 = new Random();
        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList arrayList = new ArrayList();
        int i3 = 0;
        int i4 = 0;
        while (true) {
            i = 12;
            if (i4 >= 12) {
                break;
            }
            arrayList.add(Integer.valueOf((i4 * 50) + 500));
            i4++;
        }
        int round = Math.round(Resources.getSystem().getDisplayMetrics().density * 20.0f);
        ViewParent parent = view.getParent();
        float f4 = 0.0f;
        if (parent instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) parent;
            f4 = viewGroup.getX();
            f = viewGroup.getY();
        } else {
            f = 0.0f;
        }
        float f5 = 0.5f;
        Point point = new Point((int) (((view.getWidth() - round) * 0.5f) + view.getX() + f4), (int) (((view.getHeight() - round) * 0.5f) + view.getY() + f));
        int i5 = 0;
        while (i5 < i) {
            final View view2 = new View(context);
            frameLayout2.addView(view2, new ViewGroup.LayoutParams(round, round));
            view2.setBackgroundResource(R.drawable.ic_points_coin);
            if (i5 == 10) {
                view2.setTag(5);
            } else {
                view2.setTag(Integer.valueOf(i3));
            }
            float width = (frameLayout.getWidth() * f5) + frameLayout.getX();
            float height = (frameLayout.getHeight() * 0.4f) + frameLayout.getY();
            double radians = Math.toRadians((i5 * 15) + 180);
            Path path = new Path();
            float f6 = round * 0.5f;
            int i6 = round;
            double nextInt = ((random2.nextInt(10) * 0.01f) + 0.2f) * Math.min(frameLayout.getWidth(), frameLayout.getHeight());
            Random random3 = random2;
            float cos = (((float) (Math.cos(radians) * nextInt)) + width) - f6;
            float sin = (height - ((float) (Math.sin(radians) * nextInt))) - f6;
            path.moveTo(width, height);
            path.lineTo(cos, sin);
            if (cos <= width) {
                f2 = 0.5f;
                f3 = cos * 0.5f;
            } else {
                f2 = 0.5f;
                f3 = cos * 1.5f;
            }
            path.quadTo(f3, sin * 1.5f, point.x, point.y);
            i5++;
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view2, View.X, View.Y, path);
            ofFloat.setInterpolator(new AccelerateInterpolator());
            if (arrayList.size() > 0) {
                random = random3;
                i2 = random.nextInt(arrayList.size());
            } else {
                random = random3;
                i2 = 0;
            }
            ofFloat.setDuration(((Integer) arrayList.get(i2)).intValue());
            arrayList.remove(i2);
            ofFloat.addListener(new AnimatorListenerAdapter() { // from class: com.lenoketo.videomaker.statusstory.pro16.Utils.utils.1
                @Override
                // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    frameLayout.removeView(view2);
                }
            });
            animatorSet.playTogether(ofFloat);
            random2 = random;
            frameLayout2 = frameLayout;
            round = i6;
            i = 12;
            f5 = f2;
            i3 = 0;
        }
        animatorSet.start();
    }

    public static String updateTimeRemaining(long timeDiff) {
        if (timeDiff > 0) {
            int seconds = (int) (timeDiff / 1000) % 60;
            int minutes = (int) ((timeDiff / (1000 * 60)) % 60);
            int hours = (int) ((timeDiff / (1000 * 60 * 60)) % 24);
            int days = (int) (timeDiff / (1000 * 60 * 60 * 24));
            if (days > 3) {
                return String.format(Locale.getDefault(), "%02d days left", days);
            } else {
                return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours + (days * 24), minutes, seconds);
            }
        } else {
            return "You can spin now!!";
        }
    }

    public static String updateTimeRemainingScratch(long timeDiff) {
        if (timeDiff > 0) {
            int seconds = (int) (timeDiff / 1000) % 60;
            int minutes = (int) ((timeDiff / (1000 * 60)) % 60);
            int hours = (int) ((timeDiff / (1000 * 60 * 60)) % 24);
            int days = (int) (timeDiff / (1000 * 60 * 60 * 24));
            if (days > 3) {
                return String.format(Locale.getDefault(), "%02d days left", days);
            } else {
                return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours + (days * 24), minutes, seconds);
            }
        } else {
            return "You can scratch now!!";
        }
    }

    public static String updateTimeRemainingWatchVideo(long timeDiff) {
        if (timeDiff > 0) {
            int seconds = (int) (timeDiff / 1000) % 60;
            int minutes = (int) ((timeDiff / (1000 * 60)) % 60);
            int hours = (int) ((timeDiff / (1000 * 60 * 60)) % 24);
            int days = (int) (timeDiff / (1000 * 60 * 60 * 24));
            if (days > 3) {
                return String.format(Locale.getDefault(), "%02d days left", days);
            } else {
                return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours + (days * 24), minutes, seconds);
            }
        } else {
            return "Watch Now";
        }
    }

    public static String updateTimeRemainingLuckyNumber(long timeDiff) {
        if (timeDiff > 0) {
            int seconds = (int) (timeDiff / 1000) % 60;
            int minutes = (int) ((timeDiff / (1000 * 60)) % 60);
            int hours = (int) ((timeDiff / (1000 * 60 * 60)) % 24);
            int days = (int) (timeDiff / (1000 * 60 * 60 * 24));
            if (days > 3) {
                return String.format(Locale.getDefault(), "%02d days", days);
            } else {
                return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours + (days * 24), minutes, seconds);
            }
        } else {
            return "";
        }
    }

    public static String calculateRemainingTime(long timeDiff) {
        if (timeDiff > 0) {
            String text = "";
            try {
                int seconds = (int) (timeDiff / 1000) % 60;
                int minutes = (int) ((timeDiff / (1000 * 60)) % 60);
                int hours = (int) ((timeDiff / (1000 * 60 * 60)) % 24);
                int days = (int) (timeDiff / (1000 * 60 * 60 * 24));

                if (days > 0) {
                    if (days == 1) {
                        text = days + " day";
                    } else {
                        text = days + " days";
                    }
                }
                if (hours > 0) {
                    if (text.length() > 0)
                        text = text + " ";
                    if (hours == 1) {
                        text = text + hours + " hour";
                    } else {
                        text = text + hours + " hours";
                    }
                }
                if (minutes > 0) {
                    if (text.length() > 0)
                        text = text + " ";
                    if (minutes == 1) {
                        text = text + minutes + " minute";
                    } else {
                        text = text + minutes + " minutes";
                    }
                }
                if (seconds > 0) {
                    if (text.length() > 0)
                        text = text + " ";
                    text = text + seconds + (seconds > 1 ? " seconds" : " second");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (text.length() == 0)
                return "You can spin now!!";
            else
                return "Spin will be unlocked after " + text;
        } else {
            return "You can spin now!!";
        }
    }

    public static int getDaysDiff(String date1, String Date2) {
        int days = 0;
        try {
            long timeDiff = CommonMethodsUtils.convertTimeInMillis("yyyy-MM-dd HH:mm:ss", date1) - CommonMethodsUtils.convertTimeInMillis("yyyy-MM-dd HH:mm:ss", Date2);
            if (timeDiff > 0) {
                days = (int) (timeDiff / (1000 * 60 * 60 * 24));
                return days;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return days;
    }

    public static void startTextCountAnimation(TextView tvPoints, String point) {
        ValueAnimator animator = ValueAnimator.ofInt(0, Integer.parseInt(point));
        animator.setDuration(1500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                tvPoints.setText(animation.getAnimatedValue().toString());
            }
        });
        animator.start();
    }

    public static int timeDiff(String date1, String Date2) {
        long diff = CommonMethodsUtils.convertTimeInMillis("yyyy-MM-dd HH:mm:ss", date1) - CommonMethodsUtils.convertTimeInMillis("yyyy-MM-dd HH:mm:ss", Date2);
        double seconds = Math.abs(diff) / 1000;
        int minutes = (int) (seconds / 60);
        return minutes;
    }

    public static int timeDiffSeconds(String date1, String Date2) {
        long diff = CommonMethodsUtils.convertTimeInMillis("yyyy-MM-dd HH:mm:ss", date1) - CommonMethodsUtils.convertTimeInMillis("yyyy-MM-dd HH:mm:ss", Date2);
        double seconds = Math.abs(diff) / 1000;
        return (int) seconds;
    }

    public static void startRoundAnimation(Context context, ImageView ivIcon) {
        try {
            Animation rotation = AnimationUtils.loadAnimation(context, R.anim.rotate_animation);
            rotation.setFillAfter(true);
            ivIcon.startAnimation(rotation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isValidMobile(String mobile) {
        Matcher matcher = VALID_MOBILE_REGEX.matcher(mobile);
        return matcher.matches();
    }

    public static void setLottieAnimation(LottieAnimationView animation_view, String
            celebrationLottieUrl) {
        try {
            animation_view.setFailureListener(new LottieListener<Throwable>() {
                @Override
                public void onResult(Throwable result) {

                }
            });
            animation_view.setAnimationFromUrl(celebrationLottieUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initializeAdJoe(Activity context, boolean isOpenAdJoe) {
        try {
            MainResponseModel responseMain = new Gson().fromJson(SharePreference.getInstance().getString(SharePreference.HomeData), MainResponseModel.class);
            if (!CommonMethodsUtils.isStringNullOrEmpty(responseMain.getAdjoeKeyHash())) {
                String userId = SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN) ? SharePreference.getInstance().getString(SharePreference.userId) : "0";
                Adjoe.Options options = new Adjoe.Options().setUserId(userId);
                Adjoe.init(context, responseMain.getAdjoeKeyHash(), options, new AdjoeInitialisationListener() {
                    @Override
                    public void onInitialisationFinished() {
                        dismissProgressLoader();
                        // the adjoe SDK was initialized successfully
                        //AppLogger.getInstance().e("MainActivity->OnResume===", "the adjoe SDK was initialized successfully");
                        if (isOpenAdJoe) {
                            try {
                                if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
                                    Intent adjoeOfferwallIntent = Adjoe.getOfferwallIntent(context);
                                    context.startActivity(adjoeOfferwallIntent);
                                    if (Adjoe.canShowOfferwall(context)) {
                                        Adjoe.sendUserEvent(context, Adjoe.EVENT_TEASER_SHOWN, null, null);
                                        CommonMethodsUtils.logFirebaseEvent(context, "FeatureUsabilityItemId", "FeatureUsabilityEvent", "Adjoe_Playtime", "Offerwall Opened");
                                    }
                                } else {
                                    NotifyLogin(context);
                                }
                            } catch (AdjoeNotInitializedException notInitializedException) {
                                // you have not initialized the adjoe SDK
                                setToast(context, "Games are not ready yet");
                                //AppLogger.getInstance().e("COMMON UTILS===initializeAdJoe", "ADJOE SDK NOT INITIALIZED");
                            } catch (AdjoeException exception) {
                                // the offerwall cannot be displayed for some other reason
                                if (exception.getMessage() != null) {
                                    if (exception.getMessage().equals("no content available")) {
                                        setToast(context, "No games are available, try after some time");
                                    } else if (exception.getMessage().equals("cannot display the offerwall without a network connection")) {
                                        setToast(context, "Games are not ready yet");
                                    } else if (exception.getMessage().equals("not available for this user")) {
                                        setToast(context, "Your access is restricted due to fraud behaviour");
                                    } else {
                                        setToast(context, "Games are not ready yet");
                                    }
                                }
                                //AppLogger.getInstance().e("COMMON UTILS===initializeAdJoe", "ADJOE SDK EXCEPTION");
                            } catch (Exception exception) {
                                // other exceptions
                                //AppLogger.getInstance().e("COMMON UTILS===initializeAdJoe", "EXCEPTION");
                            }
                        }
                    }

                    @Override
                    public void onInitialisationError(Exception exception) {
                        dismissProgressLoader();
                        // an error occurred while initializing the adjoe SDK.
                        // note that exception might be null
                        //AppLogger.getInstance().e("MainActivity->OnResume===", "the adjoe SDK initialization Error");
                    }
                });
            } else {
                dismissProgressLoader();
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    public static void InitializeApplovinSDK() {
        try {
            AppLovinSdk.getInstance(ApplicationController.getContext()).setMediationProvider("max");
            AppLovinSdk.initializeSdk(ApplicationController.getContext(), new AppLovinSdk.SdkInitializationListener() {
                @Override
                public void onSdkInitialized(final AppLovinSdkConfiguration configuration) {
                    // AppLovin SDK is initialized, start loading ads
                    //AppLogger.getInstance().e("AppLovinSdk initialize", "DONE=========");
                    AdsUtil.loadAppOpenAdd(ApplicationController.getContext());
                    AdsUtil.loadAdMobInterstitialAds(false);
                    AdsUtil.loadRewardedAd(false);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void expand(final View v) {
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Expansion speed of 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Collapse speed of 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static String convertPointsInINR(String points, String pointValue) {
        try {
            double calRupees = (Double.parseDouble(points) * 1) / Double.parseDouble(pointValue);
            String value = String.valueOf(calRupees);
            if (value.endsWith(".00")) {
                value = value.replace(".00", "");
            }
            if (value.endsWith(".0")) {
                value = value.replace(".0", "");
            }
            return "₹ " + value;
        } catch (Exception e) {
        }
        return "";
    }

    public static String getRandomWinMessage() {
        ArrayList<String> listMessage = new ArrayList<>();
        listMessage.add("Amazing!");
        listMessage.add("Fantastic!");
        listMessage.add("Cool!");
        listMessage.add("Super!");
        listMessage.add("Nice!");
        listMessage.add("Wow!");
        listMessage.add("Great!");
        listMessage.add("Congratulations!");
        listMessage.add("Well done!");
        return CommonMethodsUtils.getRandomAdUnitId(listMessage);
    }

    public static void openAdjoeOfferWall(Activity context) {
        try {
            if (SharePreference.getInstance().getBoolean(SharePreference.IS_LOGIN)) {
//                if (!CommonMethodsUtils.hasUsageAccessPermission(context)) {
//                    CommonMethodsUtils.showUsageAccessPermissionDialog(context);
//                } else {
                    Intent adjoeOfferwallIntent = Adjoe.getOfferwallIntent(context);
                    context.startActivity(adjoeOfferwallIntent);
                    if (Adjoe.canShowOfferwall(context)) {
                        Adjoe.sendUserEvent(context, Adjoe.EVENT_TEASER_SHOWN, null, null);
                        CommonMethodsUtils.logFirebaseEvent(context, "FeatureUsabilityItemId", "FeatureUsabilityEvent", "Adjoe_Playtime", "Offerwall Opened");
                    }
//                }
            } else {
                NotifyLogin(context);
            }
        } catch (AdjoeNotInitializedException notInitializedException) {
            // you have not initialized the adjoe SDK
            showProgressLoader(context);
            initializeAdJoe(context, true);
            //AppLogger.getInstance().e("COMMON UTILS===", "ADJOE SDK NOT INITIALIZED");
        } catch (AdjoeException exception) {
            if (exception.getMessage() != null) {
                if (exception.getMessage().equals("no content available")) {
                    setToast(context, "No games are available, try after some time");
                } else if (exception.getMessage().equals("cannot display the offerwall without a network connection")) {
                    setToast(context, "Games are not ready yet");
                } else if (exception.getMessage().equals("not available for this user")) {
                    setToast(context, "Your access is restricted due to fraud behaviour");
                } else {
                    setToast(context, "Games are not ready yet");
                }
            }
            // the offerwall cannot be displayed for some other reason
            //AppLogger.getInstance().e("COMMON UTILS===", "ADJOE SDK EXCEPTION");
        } catch (Exception exception) {
            // other exceptions
            //AppLogger.getInstance().e("COMMON UTILS===", "EXCEPTION");
        }
    }

    public static boolean hasUsageAccessPermission(Activity activity) {
        AppOpsManager appOps = (AppOpsManager) activity.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), activity.getPackageName());
        boolean granted;
        if (mode == AppOpsManager.MODE_DEFAULT) {
            granted = (activity.checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
        } else {
            granted = (mode == AppOpsManager.MODE_ALLOWED);
        }
        return granted;
    }

    @SuppressWarnings("deprecation")
    public static String getIpAddress(Context context) {
        try {
            WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            return ip;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static void dialogShowAppLuck(Activity context, AppLuckAd objAppLuck) {
        Dialog dialog = new Dialog(context, android.R.style.Theme_Light);
        dialog.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        dialog.setContentView(R.layout.dialog_appluck);

        ImageView ivCloseAd = dialog.findViewById(R.id.ivCloseAd);
        ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
        ImageView ivAd = dialog.findViewById(R.id.ivAd);
        LottieAnimationView ivLottieViewAd = dialog.findViewById(R.id.ivLottieViewAd);
        ivCloseAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if (objAppLuck.getImage() != null) {
            if (objAppLuck.getImage().contains(".json")) {
                ivAd.setVisibility(View.GONE);
                ivLottieViewAd.setVisibility(View.VISIBLE);
                setLottieAnimation(ivLottieViewAd, objAppLuck.getImage());
                ivLottieViewAd.setRepeatCount(LottieDrawable.INFINITE);
                progressBar.setVisibility(View.GONE);
                ivLottieViewAd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        Redirect(context, objAppLuck.getScreenNo(), "", objAppLuck.getUrl(), "", "", "");
                    }
                });
            } else {
                ivAd.setVisibility(View.VISIBLE);
                ivAd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        Redirect(context, objAppLuck.getScreenNo(), "", objAppLuck.getUrl(), "", "", "");
                    }
                });
                ivLottieViewAd.setVisibility(View.GONE);
                Glide.with(context)
                        .load(objAppLuck.getImage())
                        .addListener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(ivAd);
            }
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    context.finish();
                }
            });
            if (context != null && !context.isFinishing()) {
                dialog.show();
            }
        }
    }
}
