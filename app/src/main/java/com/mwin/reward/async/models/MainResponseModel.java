package com.mwin.reward.async.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class MainResponseModel implements Serializable {

    @SerializedName("lovinInterstitialID")
    private List<String> lovinInterstitialID;
    @SerializedName("appVersion")
    private String appVersion;
    @SerializedName("userToken")
    private String userToken;
    @SerializedName("fakeEarningPoint")
    private String fakeEarningPoint;
    @SerializedName("pointValue")
    private String pointValue;
    @SerializedName("MenuBanner")
    private DrawerBanner menuBanner;
    @SerializedName("homeDialog")
    private HomeDialog homeDialog;
    @SerializedName("lovinRewardID")
    private List<String> lovinRewardID;
    @SerializedName("lovinSmallNativeID")
    private List<String> lovinSmallNativeID;
    @SerializedName("celebrationLottieUrl")
    private String celebrationLottieUrl;
    @SerializedName("homeDataList")
    private List<HomeDataListItem> homeDataList;
    @SerializedName("exitDialog")
    private ExitDialog exitDialog;
    @SerializedName("updateMessage")
    private String updateMessage;
    @SerializedName("status")
    private String status;
    @SerializedName("lovinNativeID")
    private List<String> lovinNativeID;
    @SerializedName("isShowNativeAdsOnAppExit")
    private String isShowNativeAdsOnAppExit;
    @SerializedName("storyView")
    private List<StoryViewItem> storyView;
    @SerializedName("aboutUsUrl")
    private String aboutUsUrl;
    @SerializedName("homeSlider")
    private List<HomeSliderItem> homeSlider;
    @SerializedName("isBackAdsInterstitial")
    private String isBackAdsInterstitial;
    @SerializedName("sideMenuList")
    private List<MenuListItem> sideMenuList;
    @SerializedName("lovinBannerID")
    private List<String> lovinBannerID;
    @SerializedName("isAppLovinAdShow")
    private String isAppLovinAdShow;
    @SerializedName("privacyPolicy")
    private String privacyPolicy;
    @SerializedName("isForceUpdate")
    private String isForceUpdate;
    @SerializedName("appUrl")
    private String appUrl;
    @SerializedName("lovinAppOpenID")
    private List<String> lovinAppOpenID;
    @SerializedName("message")
    private String message;
    @SerializedName("termsConditionUrl")
    private String termsConditionUrl;
    @SerializedName("telegramUrl")
    private String telegramUrl;

    @SerializedName("youtubeUrl")
    private String youtubeUrl;

    @SerializedName("instagramUrl")
    private String instagramUrl;

    @SerializedName("homeNote")
    private String homeNote;

    @SerializedName("topAds")
    private TopAds topAds;
    @SerializedName("packageInstallTrackingUrl")
    private String packageInstallTrackingUrl;
    @SerializedName("pid")
    private String pid;
    @SerializedName("offer_id")
    private String offer_id;
    @SerializedName("earningPoint")
    private String earningPoint;

    @SerializedName("adFailUrl")
    private String adFailUrl;

    @SerializedName("loginSlider")
    private List<HomeSliderItem> loginSlider;
    @SerializedName("loginSliderWhatsApp")
    private List<HomeSliderItem> loginSliderWhatsApp;

    @SerializedName("tigerInApp")
    private String tigerInApp;

    @SerializedName("isShowWhatsAppAuth")
    private String isShowWhatsAppAuth;

    @SerializedName("rewardLabel")
    private String rewardLabel;

    @SerializedName("isShowAccountDeleteOption")
    private String isShowAccountDeleteOption;

    @SerializedName("adjoeKeyHash")
    private String adjoeKeyHash;

    @SerializedName("imageAdjoeIcon")
    private String imageAdjoeIcon;

    @SerializedName("isShowPlaytimeIcon")
    private String isShowPlaytimeIcon;

    @SerializedName("isShowGiveawayCode")
    private String isShowGiveawayCode;
    @SerializedName("giveawayCode")
    private String giveawayCode;
    @SerializedName("footerImage")
    private String footerImage;
    @SerializedName("footerTaskIcon")
    private String footerTaskIcon;
    @SerializedName("isShowAdjoeLeaderboardIcon")
    private String isShowAdjoeLeaderboardIcon;

    @SerializedName("isShowSurvey")
    private String isShowSurvey;
    @SerializedName("isShowAppluck")
    private String isShowAppluck;
    @SerializedName("InterAppluckID")
    private String InterAppluckID;
    @SerializedName("DefaultAppluckID")
    private String DefaultAppluckID;
    @SerializedName("IncentiveAppluckID")
    private String IncentiveAppluckID;
    @SerializedName("isDefaultAppluck")
    private String isDefaultAppluck;
    @SerializedName("top_offers")
    private List<TaskListDataItem> top_offers;

    @SerializedName("isShowPubScale")
    private String isShowPubScale;
    @SerializedName("isShowWelcomeBonusPopup")
    private String isShowWelcomeBonusPopup;
    @SerializedName("welcomeBonus")
    private String welcomeBonus;
    @SerializedName("nextWithdrawAmount")
    private String nextWithdrawAmount;
    @SerializedName("isShowFooterTaskIcon")
    private String isShowFooterTaskIcon;
    @SerializedName("isShowHotOffers")
    private String isShowHotOffers;
    @SerializedName("hotOffersScreenNo")
    private String hotOffersScreenNo;

    public String getIsShowHotOffers() {
        return isShowHotOffers;
    }

    public String getHotOffersScreenNo() {
        return hotOffersScreenNo;
    }

    public String getIsShowFooterTaskIcon() {
        return isShowFooterTaskIcon;
    }

    public String getNextWithdrawAmount() {
        return nextWithdrawAmount;
    }

    public void setNextWithdrawAmount(String nextWithdrawAmount) {
        this.nextWithdrawAmount = nextWithdrawAmount;
    }

    public String getWelcomeBonus() {
        return welcomeBonus;
    }

    public String getIsShowWelcomeBonusPopup() {
        return isShowWelcomeBonusPopup;
    }

    public String getIsShowPubScale() {
        return isShowPubScale;
    }

    public List<String> getLovinSmallNativeID() {
        return lovinSmallNativeID;
    }

    public List<TaskListDataItem> getTop_offers() {
        return top_offers;
    }

    public String getIsShowSurvey() {
        return isShowSurvey;
    }

    public String getIsShowAppluck() {
        return isShowAppluck;
    }

    public String getInterAppluckID() {
        return InterAppluckID;
    }

    public String getDefaultAppluckID() {
        return DefaultAppluckID;
    }

    public String getIncentiveAppluckID() {
        return IncentiveAppluckID;
    }

    public String getIsDefaultAppluck() {
        return isDefaultAppluck;
    }

    public String getIsShowAdjoeLeaderboardIcon() {
        return isShowAdjoeLeaderboardIcon;
    }

    public String getFooterTaskIcon() {
        return footerTaskIcon;
    }

    public String getFooterImage() {
        return footerImage;
    }

    public String getIsShowGiveawayCode() {
        return isShowGiveawayCode;
    }

    public String getGiveawayCode() {
        return giveawayCode;
    }

    public String getTigerInApp() {
        return tigerInApp;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public DrawerBanner getMenuBanner() {
        return menuBanner;
    }

    public HomeDialog getHomeDialog() {
        return homeDialog;
    }

    public List<String> getLovinRewardID() {
        return lovinRewardID;
    }

    public void setLovinRewardID(List<String> lovinRewardID) {
        this.lovinRewardID = lovinRewardID;
    }

    public ExitDialog getExitDialog() {
        return exitDialog;
    }

    public String getUpdateMessage() {
        return updateMessage;
    }

    public String getStatus() {
        return status;
    }

    public String getIsShowNativeAdsOnAppExit() {
        return isShowNativeAdsOnAppExit;
    }

    public List<StoryViewItem> getStoryView() {
        return storyView;
    }

    public String getAboutUsUrl() {
        return aboutUsUrl;
    }

    public List<HomeSliderItem> getHomeSlider() {
        return homeSlider;
    }

    public String getIsBackAdsInterstitial() {
        return isBackAdsInterstitial;
    }

    public List<MenuListItem> getSideMenuList() {
        return sideMenuList;
    }

    public String getIsAppLovinAdShow() {
        return isAppLovinAdShow;
    }

    public void setIsAppLovinAdShow(String isAppLovinAdShow) {
        this.isAppLovinAdShow = isAppLovinAdShow;
    }

    public String getPrivacyPolicy() {
        return privacyPolicy;
    }

    public String getIsForceUpdate() {
        return isForceUpdate;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public String getMessage() {
        return message;
    }

    public String getTermsConditionUrl() {
        return termsConditionUrl;
    }

    public List<String> getLovinInterstitialID() {
        return lovinInterstitialID;
    }

    public void setLovinInterstitialID(List<String> lovinInterstitialID) {
        this.lovinInterstitialID = lovinInterstitialID;
    }

    public List<String> getLovinNativeID() {
        return lovinNativeID;
    }

    public void setLovinNativeID(List<String> lovinNativeID) {
        this.lovinNativeID = lovinNativeID;
    }

    public List<String> getLovinBannerID() {
        return lovinBannerID;
    }

    public void setLovinBannerID(List<String> lovinBannerID) {
        this.lovinBannerID = lovinBannerID;
    }

    public List<String> getLovinAppOpenID() {
        return lovinAppOpenID;
    }

    public void setLovinAppOpenID(List<String> lovinAppOpenID) {
        this.lovinAppOpenID = lovinAppOpenID;
    }

    public String getTelegramUrl() {
        return telegramUrl;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public String getInstagramUrl() {
        return instagramUrl;
    }

    public List<HomeDataListItem> getHomeDataList() {
        return homeDataList;
    }

    public String getHomeNote() {
        return homeNote;
    }

    public TopAds getTopAds() {
        return topAds;
    }

    public String getFakeEarningPoint() {
        return fakeEarningPoint;
    }

    public String getPointValue() {
        return pointValue;
    }

    public String getPackageInstallTrackingUrl() {
        return packageInstallTrackingUrl;
    }

    public String getPid() {
        return pid;
    }

    public String getOffer_id() {
        return offer_id;
    }

    public String getEarningPoint() {
        return earningPoint;
    }

    public String getAdFailUrl() {
        return adFailUrl;
    }

    public List<HomeSliderItem> getLoginSlider() {
        return loginSlider;
    }

    public void setLoginSlider(List<HomeSliderItem> loginSlider) {
        this.loginSlider = loginSlider;
    }

    public String getIsShowWhatsAppAuth() {
        return isShowWhatsAppAuth;
    }

    public List<HomeSliderItem> getLoginSliderWhatsApp() {
        return loginSliderWhatsApp;
    }

    public void setLoginSliderWhatsApp(List<HomeSliderItem> loginSliderWhatsApp) {
        this.loginSliderWhatsApp = loginSliderWhatsApp;
    }

    public String getUserToken() {
        return userToken;
    }

    public String getCelebrationLottieUrl() {
        return celebrationLottieUrl;
    }

    public String getRewardLabel() {
        return rewardLabel;
    }

    public String getIsShowAccountDeleteOption() {
        return isShowAccountDeleteOption;
    }

    public String getAdjoeKeyHash() {
        return adjoeKeyHash;
    }

    public String getImageAdjoeIcon() {
        return imageAdjoeIcon;
    }

    public String getIsShowPlaytimeIcon() {
        return isShowPlaytimeIcon;
    }
}