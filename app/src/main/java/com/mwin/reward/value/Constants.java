package com.mwin.reward.value;

public class Constants {

    public static int SHOW_RATE_US_POPUP_COUNT = 3;
    public static String APP_OPEN_ADD_DISMISSED = "APP_OPEN_ADD_DISMISSED";
    public static String APP_OPEN_ADD_LOADED = "APP_OPEN_ADD_LOADED";
    public static String WATCH_WEBSITE_RESULT = "WATCH_WEBSITE_RESULT";
    public static String DAILY_TARGET_RESULT = "DAILY_TARGET_RESULT";
    public static String QUICK_TASK_RESULT = "QUICK_TASK_RESULT";
    public static String LIVE_MILESTONE_RESULT = "LIVE_MILESTONE_RESULT";
    public static int ratePopupHourDifference = 24;
    public static int countDownTimerCount = 5;

    public static String msg_Service_Error = "Oops! This service is taking too much time to respond. please check your internet connection & try again.";
    public static String STATUS_ERROR = "0";
    public static String STATUS_SUCCESS = "1";
    public static String STATUS_LOGOUT = "5";
    public static String APPlOVIN_AD = "1";

    public static String APPLOVIN_INTERSTITIAL = "1";
    public static String APPLOVIN_REWARD = "2";
    public static String telegramPackageName = "org.telegram.messenger";
    public static String whatsappPackageName = "com.whatsapp";
    public static String TASK_TYPE_ALL = "0";
    public static String TASK_TYPE_HIGHEST_PAYING = "1";

    public static native String getUrlValue();

    public static native String getMivValue();

    public static native String getMkeyValue();

    public static native String getToken();

    public interface HistoryType {
        String WITHDRAW_HISTORY = "17";
        String TASK = "11";
        String DAILY_LOGIN = "15";
        String ALL = "0";
        String REFER_POINT = "13";
        String REFER_USERS = "16";
        String WATCH_VIDEO = "18";
        String GIVE_AWAY = "19";
        String SCRATCH_CARD = "20";
        String LUCKY_NUMBER_MY_CONTEST = "1";
        String LUCKY_NUMBER_ALL_CONTEST = "2";
        String DAILY_REWARD = "22";
        String WATCH_WEBSITE = "24";
        String TYPE_TEXT_TYPING = "26";
        String MILESTONES = "27";
        String NUMBER_SORTING = "29";
        String WORD_SORTING = "30";
        String SURVEYS = "31";
        String MINE_SWEEPER = "32";
        String ALPHABET_GAME = "33";
    }

    public interface DATA_TYPES {
        String ICON_LIST = "iconlist";
        String SINGLE_SLIDER = "singleslider";
        String TWO_GRID = "twogrid"; // title, image, points
        String SINGLE_BIG_TASK = "singleBigTask";
        String NATIVE_AD = "nativeAd";
        String EARN_GRID = "earnGrid";
        String TASK_LIST = "taskList";
        String CPX_SURVEY = "CPXSurvey";
        String LIVE_MILESTONES = "live_milestones";
        String LIVE_CONTEST = "live_contest";
        String QUICK_TASK = "Quicktask";
        String DAILY_BONUS = "dailyBonus";
        String GRID = "grid";
        String DAILY_REWARDS_CHALLENGE = "daily_reward_challenge";
        String DAILY_TARGET = "daily_target";
    }
}
