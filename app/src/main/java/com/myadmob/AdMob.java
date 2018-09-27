package com.myadmob;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appnext.banners.BannerAdRequest;
import com.appnext.banners.BannerListener;
import com.appnext.banners.BannerSize;
import com.appnext.banners.BannerView;
import com.appnext.base.Appnext;
import com.appnext.core.AppnextError;
import com.appnext.core.callbacks.OnAdClicked;
import com.appnext.core.callbacks.OnAdClosed;
import com.appnext.core.callbacks.OnAdError;
import com.appnext.core.callbacks.OnAdLoaded;
import com.appnext.core.callbacks.OnAdOpened;
import com.appnext.nativeads.NativeAdRequest;
import com.appnext.nativeads.NativeAdView;
import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.myadmob.ui.NativeActivity;
import com.myadmob.util.ILog;
import com.myadmob.util.IShared;
import com.myadmob.util.Utils;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressLint ("StaticFieldLeak")
public class AdMob {
	private Context context;
	private String adsGGBannerId;
	private String adsGGFullId;
	private String adsFBBannerId;
	private String adsFBFullId;
	private String adsFBNativeId;
	private String adsANBannerId;
	private String adsANFullId;
	private String adsANNativeId;
	private static com.myadmob.AdMob adMob;
	private InterstitialAd mInterstitialAdGG;
	private com.facebook.ads.InterstitialAd mInterstitialAdFB;
	private com.appnext.ads.interstitial.Interstitial mInterstitialAdAN;
	private boolean isFullShow;
	private static final int ID_BANNER_VIEW = 0x111;
	private static final int ID_CONTAIN_AD_VIEW = 0x112;
	private boolean isInit;

	private enum AdMobType {GOOGLE, FACEBOOK, APP_NEXT, VUN_GLE}

	private AdMobType adsBannerType = AdMobType.GOOGLE;
	private AdMobType adsFullType = AdMobType.GOOGLE;
	private AdMobType adsNativeType = AdMobType.FACEBOOK;

	private AdView adsGGBannerView;
	private com.facebook.ads.AdView adsFBBannerView;
	private BannerView adsANBannerView;
	private com.appnext.nativeads.NativeAd adsANNativeAd;

	/**
	 * initialize
	 */
	private AdMob() {

	}

	private AdMob(Context context) {
		this.context = context;
		Appnext.init(context);
		String date = IShared.getIShare(context).getString("date", "");
		final String dateToday = Utils.getDateToday();
		if (!date.equals(dateToday)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					String link = "https://play.google.com/store/apps/details?id=" + AdMob.this.context.getPackageName();
					try {
						Jsoup.connect(link).get();
						IShared.getIShare(AdMob.this.context).putBoolean("is_die", false);
						ILog.e("app life");
					} catch (IOException e) {
						e.printStackTrace();
						IShared.getIShare(AdMob.this.context).putBoolean("is_die", true);
						ILog.e("app die");
					}
					IShared.getIShare(AdMob.this.context).putString("date", dateToday);
				}
			}).start();
		}
		if (IShared.getIShare(context).getBoolean("is_die", false)) {
			adsBannerType = AdMobType.APP_NEXT;
			adsFullType = AdMobType.APP_NEXT;
			adsNativeType = AdMobType.APP_NEXT;
			ILog.e("ads app next");
		} else {
			adsBannerType = AdMobType.GOOGLE;
			adsFullType = AdMobType.GOOGLE;
			adsNativeType = AdMobType.FACEBOOK;
			ILog.e("ads google");
		}
	}

	private void setContext(Context context) {
		this.context = context;
	}

	private static com.myadmob.AdMob getInstance() {
		if (adMob == null) {
			adMob = new com.myadmob.AdMob();
		}
		return adMob;
	}

	private static com.myadmob.AdMob getInstance(Context context) {
		if (adMob == null) {
			adMob = new com.myadmob.AdMob(context);
		}
		if (adMob.context == null) {
			adMob.setContext(context);
		}
		return adMob;
	}

	public void onDestroy() {
		// if(rlContainer !=)
	}

	/**
	 * set
	 */

	private void setAdsANBannerId(String adsANBannerId) {
		this.adsANBannerId = adsANBannerId;
	}

	private void setAdsANFullId(String adsANFullId) {
		this.adsANFullId = adsANFullId;
		if (adsFullType == AdMobType.APP_NEXT) {
			initAdsANFull();
		}
	}

	private void setAdsANNativeId(String adsANNativeId) {
		this.adsANNativeId = adsANNativeId;
	}

	private void setAdsFBBannerId(String adsFBBannerId) {
		this.adsFBBannerId = adsFBBannerId;
	}

	private void setAdsFBFullId(String adsFBFullId) {
		this.adsFBFullId = adsFBFullId;
		if (adsFullType == AdMobType.FACEBOOK) {
			initAdsFBFull();
		}
	}

	private void setAdsFBNativeId(String adsFBNativeId) {
		this.adsFBNativeId = adsFBNativeId;
	}

	private void setAdsGGBannerId(String adsGGBannerId) {
		this.adsGGBannerId = adsGGBannerId;
	}

	private void setAdsGGAppId(String adsGGAppId) {
		MobileAds.initialize(context, adsGGAppId);
		isInit = true;
	}

	private void setAdsGGFullId(String gAdsFullId) {
		this.adsGGFullId = gAdsFullId;
		if (!isInit) {
			return;
		}
		if (adsFullType == AdMobType.GOOGLE) {
			initAdsGGFull();
		}
	}

	/**
	 * init ads google full
	 */
	private void initAdsGGFull() {
		AdRequest adRequest = new AdRequest.Builder().build();
		mInterstitialAdGG = new InterstitialAd(context);
		mInterstitialAdGG.setAdUnitId(adsGGFullId != null ? adsGGFullId : "");
		mInterstitialAdGG.loadAd(adRequest);
		mInterstitialAdGG.setAdListener(adsGGFullListener);
	}

	/**
	 * init ads facebook full
	 */

	private void initAdsFBFull() {
		mInterstitialAdFB = new com.facebook.ads.InterstitialAd(context, adsFBFullId != null ? adsFBFullId : "");
		mInterstitialAdFB.setAdListener(adsFBFullListener);
		mInterstitialAdFB.loadAd();
	}

	/**
	 * init ads facebook full
	 */

	private void initAdsANFull() {
		adsFullType = AdMobType.APP_NEXT;
		mInterstitialAdAN = new com.appnext.ads.interstitial.Interstitial(context, adsANFullId != null ? adsANFullId : "");
		mInterstitialAdAN.setOnAdErrorCallback(adsANFullOnAdErrorListener);
		mInterstitialAdAN.setOnAdLoadedCallback(adsANFullOnAdLoadedListener);
		mInterstitialAdAN.setOnAdClickedCallback(adsANFullOnAdClickedListener);
		mInterstitialAdAN.setOnAdOpenedCallback(adsANFullOnAdOpenedListener);
		mInterstitialAdAN.setOnAdClosedCallback(adsANFullOnAdClosedListener);
		mInterstitialAdAN.loadAd();
	}

	/**
	 * get ad view google banner
	 */

	private AdView getAdsGGBannerView() {
		AdRequest adRequest = new AdRequest.Builder().build();
		// if (adsGGBannerView == null) {
		adsGGBannerView = new AdView(context);
		adsGGBannerView.setId(ID_BANNER_VIEW);
		adsGGBannerView.setAdSize(AdSize.BANNER);
		adsGGBannerView.setAdUnitId(adsGGBannerId != null ? adsGGBannerId : "");
		adsGGBannerView.setAdListener(adsGGBannerListener);
		// }
		// ViewGroup parent = (ViewGroup) adsGGBannerView.getParent();
		// if (parent != null) {
		// 	parent.removeAllViews();
		// }
		adsGGBannerView.loadAd(adRequest);
		return adsGGBannerView;
	}

	/**
	 * get ad view facebook banner
	 */

	private com.facebook.ads.AdView getAdsFBBannerView() {
		// if (adsFBBannerView == null) {
		adsFBBannerView = new com.facebook.ads.AdView(context,
				adsFBBannerId != null ? adsFBBannerId : "", com.facebook.ads.AdSize.BANNER_HEIGHT_50);
		adsFBBannerView.setId(ID_BANNER_VIEW);
		adsFBBannerView.setAdListener(adsFBBannerListener);
		// }
		// ViewGroup parent = (ViewGroup) adsFBBannerView.getParent();
		// if (parent != null) {
		// 	parent.removeAllViews();
		// }
		adsFBBannerView.loadAd();
		return adsFBBannerView;
	}

	/**
	 * get ad view app next banner
	 */

	private BannerView getAdsANBannerView() {
		// if (adsANBannerView == null) {
		adsANBannerView = new BannerView(context);
		adsANBannerView.setId(ID_BANNER_VIEW);
		adsANBannerView.setBannerSize(BannerSize.BANNER);
		adsANBannerView.setPlacementId(adsANBannerId != null ? adsANBannerId : "");
		adsANBannerView.setBannerListener(adsANBannerListener);
		// }
		// ViewGroup parent = (ViewGroup) adsANBannerView.getParent();
		// if (parent != null) {
		// 	parent.removeAllViews();
		// }
		adsANBannerView.loadAd(new BannerAdRequest());
		return adsANBannerView;
	}

	/**
	 * show native time out
	 */

	public void showNativeTimeOut(int timeOut, int width, int height) {
		boolean isFullscreen = WindowManager.LayoutParams.FLAG_FULLSCREEN ==
				(((Activity) context).getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN);
		Intent intent = new Intent(context, NativeActivity.class);
		intent.putExtra("time_out", timeOut);
		intent.putExtra("width", width);
		intent.putExtra("height", height);
		intent.putExtra("is_full_screen", isFullscreen);
		intent.putExtra("ads_native_id", adsFBNativeId + "@" + adsANNativeId);
		intent.putExtra("is_die", IShared.getIShare(context).getBoolean("is_die", false));
		context.startActivity(intent);
	}

	public void showNativeTimeOut() {
		boolean isFullscreen = false;
		try {
			isFullscreen = WindowManager.LayoutParams.FLAG_FULLSCREEN ==
					(((Activity) context).getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} catch (Exception ignored) {

		}
		Intent intent = new Intent(context, NativeActivity.class);
		intent.putExtra("time_out", 5000);
		intent.putExtra("width", 840);
		intent.putExtra("height", 640);
		intent.putExtra("is_full_screen", isFullscreen);
		intent.putExtra("ads_native_id", adsFBNativeId + "@" + adsANNativeId);
		intent.putExtra("is_die", IShared.getIShare(context).getBoolean("is_die", false));
		context.startActivity(intent);
	}


	/**
	 * get native view
	 */
	private NativeAd adsFBNativeAd;
	private RelativeLayout rlContainer;

	private RelativeLayout getFBNativeView(NativeAd nativeAd) {
		rlContainer.removeAllViews();
		rlContainer.setBackgroundColor(Color.WHITE);
		nativeAd.unregisterView();

		int padding5 = Utils.convertDpToPx(context, 5);
		int padding10 = Utils.convertDpToPx(context, 10);
		// todo native top
		LinearLayout nativeTop = new LinearLayout(context);
		nativeTop.setOrientation(LinearLayout.HORIZONTAL);
		AdIconView adIconView = new AdIconView(context);
		LinearLayout nativeTitle = new LinearLayout(context);
		nativeTitle.setOrientation(LinearLayout.VERTICAL);
		TextView tvTitle = new TextView(context);
		TextView tvLabel = new TextView(context);
		tvTitle.setTextSize(15);
		tvTitle.setLines(1);
		tvTitle.setTextColor(Color.BLACK);
		tvTitle.setEllipsize(TextUtils.TruncateAt.END);
		tvLabel.setTextSize(12);
		tvLabel.setLines(1);
		tvLabel.setTextColor(Color.parseColor("#aaaaaa"));
		tvLabel.setEllipsize(TextUtils.TruncateAt.END);

		nativeTitle.setPadding(padding5, 0, padding5, 0);
		nativeTitle.addView(tvTitle);
		nativeTitle.addView(tvLabel);
		int size36 = Utils.convertDpToPx(context, 36);
		nativeTop.addView(adIconView, new LinearLayout.LayoutParams(size36, size36));
		LinearLayout.LayoutParams lpTitle = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lpTitle.weight = 1;
		nativeTop.addView(nativeTitle, lpTitle);
		LinearLayout adSponsoredLabel = new LinearLayout(context);
		adSponsoredLabel.setGravity(Gravity.END);
		AdChoicesView adChoicesView = new AdChoicesView(context, nativeAd, true);
		adSponsoredLabel.addView(adChoicesView, 0);
		nativeTop.addView(adSponsoredLabel, new LinearLayout.LayoutParams(
				Utils.convertDpToPx(context, 60), LinearLayout.LayoutParams.WRAP_CONTENT));
		LinearLayout adView = new LinearLayout(context);
		adView.addView(nativeTop, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

		// todo native mid
		LinearLayout nativeMid = new LinearLayout(context);
		MediaView nativeMediaView = new MediaView(context);
		nativeMediaView.setGravity(Gravity.CENTER);
		nativeMid.addView(nativeMediaView);
		LinearLayout.LayoutParams lpMid = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lpMid.topMargin = padding10;
		adView.addView(nativeMid, lpMid);

		// todo native bot
		LinearLayout nativeBot = new LinearLayout(context);
		nativeBot.setOrientation(LinearLayout.HORIZONTAL);
		nativeBot.setPadding(padding5, padding5, padding5, padding5);
		LinearLayout nativeContent = new LinearLayout(context);
		TextView tvContent = new TextView(context);
		TextView tvDetail = new TextView(context);
		tvContent.setTextSize(12);
		tvContent.setLines(1);
		tvContent.setTextColor(Color.BLACK);
		tvContent.setEllipsize(TextUtils.TruncateAt.END);
		tvDetail.setTextSize(12);
		tvDetail.setLines(1);
		tvDetail.setTextColor(Color.DKGRAY);
		tvDetail.setEllipsize(TextUtils.TruncateAt.END);
		nativeContent.setOrientation(LinearLayout.VERTICAL);
		nativeContent.addView(tvDetail);
		nativeContent.addView(tvContent);
		LinearLayout.LayoutParams lpContent = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lpContent.weight = 3;
		nativeBot.addView(nativeContent, lpContent);
		Button btnAction = new Button(context);
		LinearLayout.LayoutParams lpBtn = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lpBtn.weight = 1;
		btnAction.setBackgroundColor(Color.parseColor("#4286F4"));
		int padding3 = Utils.convertDpToPx(context, 3);
		btnAction.setPadding(padding3, padding3, padding3, padding3);
		btnAction.setGravity(Gravity.CENTER);
		btnAction.setTextSize(12);
		btnAction.setVisibility(View.GONE);
		nativeBot.addView(btnAction, lpBtn);
		adView.addView(nativeBot);

		adView.setOrientation(LinearLayout.VERTICAL);
		RelativeLayout.LayoutParams lpAdView = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

		adView.setPadding(padding10, padding10, padding10, padding10);
		lpAdView.addRule(RelativeLayout.CENTER_IN_PARENT);

		rlContainer.addView(adView, lpAdView);
		tvTitle.setText(nativeAd.getAdvertiserName());
		tvDetail.setText(nativeAd.getAdBodyText());
		tvContent.setText(nativeAd.getAdSocialContext());
		btnAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
		btnAction.setText(nativeAd.getAdCallToAction());
		tvLabel.setText(nativeAd.getSponsoredTranslation());

		// Create a list of clickable views
		List<View> clickableViews = new ArrayList<>();
		clickableViews.add(tvTitle);
		clickableViews.add(nativeMediaView);
		clickableViews.add(adIconView);
		clickableViews.add(btnAction);
		//
		// // Register the Title and CTA button to listen for clicks.
		nativeAd.registerViewForInteraction(
				adView,
				nativeMediaView,
				adIconView,
				clickableViews);
		return rlContainer;
	}

	private RelativeLayout getANNativeView(com.appnext.nativeads.NativeAd adsANNativeAd) {
		rlContainer.removeAllViews();
		rlContainer.setBackgroundColor(Color.WHITE);
		int padding10 = Utils.convertDpToPx(context, 10);
		int padding5 = Utils.convertDpToPx(context, 5);

		NativeAdView nativeAdView = new NativeAdView(context);
		LinearLayout nativeView = new LinearLayout(context);
		nativeView.setOrientation(LinearLayout.VERTICAL);
		nativeView.setPadding(padding10, padding10, padding10, padding10);

		// todo native top
		LinearLayout nativeTop = new LinearLayout(context);
		nativeTop.setOrientation(LinearLayout.HORIZONTAL);
		// nativeTop.setPadding(0, padding10, 0, padding10);
		ImageView adIconView = new ImageView(context);
		LinearLayout nativeTitle = new LinearLayout(context);
		nativeTitle.setOrientation(LinearLayout.VERTICAL);
		TextView tvTitle = new TextView(context);
		TextView tvLabel = new TextView(context);
		TextView tvDescription = new TextView(context);
		tvTitle.setTextSize(15);
		tvTitle.setLines(1);
		tvTitle.setTextColor(Color.BLACK);
		tvTitle.setEllipsize(TextUtils.TruncateAt.END);
		tvLabel.setTextSize(12);
		tvLabel.setLines(1);
		tvLabel.setTextColor(Color.parseColor("#aaaaaa"));
		tvLabel.setEllipsize(TextUtils.TruncateAt.END);
		ImageView ivStar = new ImageView(context);
		ivStar.setImageResource(android.R.drawable.btn_star_big_on);
		LinearLayout llStar = new LinearLayout(context);
		llStar.setOrientation(LinearLayout.HORIZONTAL);
		llStar.setGravity(Gravity.CENTER_VERTICAL);
		llStar.addView(tvLabel);
		llStar.addView(ivStar);
		nativeTitle.setPadding(padding5, 0, padding5, 0);
		nativeTitle.addView(tvTitle);
		nativeTitle.addView(llStar);
		nativeTitle.addView(tvDescription);
		int size36 = Utils.convertDpToPx(context, 36);
		nativeTop.addView(adIconView, new LinearLayout.LayoutParams(size36, size36));
		LinearLayout.LayoutParams lpTitle = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		nativeTop.addView(nativeTitle, lpTitle);
		nativeView.addView(nativeTop, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

		// todo native mid
		LinearLayout nativeMid = new LinearLayout(context);
		com.appnext.nativeads.MediaView nativeMediaView = new com.appnext.nativeads.MediaView(context);
		nativeMediaView.setGravity(Gravity.CENTER);
		nativeMid.addView(nativeMediaView);
		LinearLayout.LayoutParams lpMid = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lpMid.topMargin = padding10;
		nativeView.addView(nativeMid, lpMid);

		adsANNativeAd.downloadAndDisplayImage(adIconView, adsANNativeAd.getIconURL());

		tvTitle.setText(adsANNativeAd.getAdTitle());
		adsANNativeAd.setMediaView(nativeMediaView);
		tvLabel.setText(adsANNativeAd.getStoreRating());
		tvDescription.setText(adsANNativeAd.getAdDescription());

		List<View> viewArrayList = new ArrayList<>();
		viewArrayList.add(adIconView);
		viewArrayList.add(nativeTitle);
		viewArrayList.add(nativeMediaView);
		adsANNativeAd.registerClickableViews(viewArrayList);

		nativeAdView.addView(nativeView);
		adsANNativeAd.setNativeAdView(nativeAdView);
		ViewGroup parent = (ViewGroup) nativeAdView.getParent();

		if (parent != null) {
			parent.removeAllViews();
		}
		rlContainer.addView(nativeAdView, new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
		return rlContainer;
	}

	private void loadFBNativeAd() {
		adsFBNativeAd = new NativeAd(context, adsFBNativeId != null ? adsFBNativeId : "");
		adsFBNativeAd.setAdListener(adsFBNativeAdListener);
		adsFBNativeAd.loadAd();
	}

	private void loadANNativeAd() {
		adsANNativeAd = new com.appnext.nativeads.NativeAd(context, adsANNativeId);
		adsANNativeAd.setAdListener(adsANNativeListener);
		adsANNativeAd.loadAd(new NativeAdRequest());
	}

	public RelativeLayout getNativeView() {
		switch (adsNativeType) {
			case FACEBOOK:
				loadFBNativeAd();
				break;

			case APP_NEXT:
				loadANNativeAd();
				break;

			default:
				break;
		}
		// if (rlContainer == null) {
			rlContainer = new RelativeLayout(context);
		// } else {
		// 	if (rlContainer.getParent() != null) {
		// 		((ViewGroup) rlContainer.getParent()).removeView(rlContainer);
		// 	}
		// }
		return rlContainer;
	}

	/**
	 * show full
	 */

	private int countClick = 0;

	public void showAdsFull(int count) {
		if (count < 2) {
			count = 2;
		}
		countClick++;
		if (countClick % count == 0) {
			showAdsFull();
			countClick = 0;
		}
	}

	public void showAdsFull() {
		if (!isInit) {
			return;
		}
		switch (adsFullType) {
			case GOOGLE:
				if (mInterstitialAdGG != null) {
					if (mInterstitialAdGG.isLoaded()) {
						mInterstitialAdGG.show();
						isFullShow = false;
					} else {
						isFullShow = true;
					}
				} else {
					initAdsGGFull();
				}
				break;

			case FACEBOOK:
				if (mInterstitialAdFB != null) {
					if (mInterstitialAdFB.isAdLoaded() && !mInterstitialAdFB.isAdInvalidated()) {
						mInterstitialAdFB.show();
						isFullShow = false;
					} else {
						isFullShow = true;
					}
				} else {
					initAdsFBFull();
				}
				break;

			case APP_NEXT:
				if (mInterstitialAdAN != null) {
					if (mInterstitialAdAN.isAdLoaded()) {
						mInterstitialAdAN.showAd();
						isFullShow = false;
					} else {
						isFullShow = true;
					}
				} else {
					initAdsANFull();
				}
				break;

			case VUN_GLE:
				break;

			default:
				break;
		}
	}

	private Activity activity;

	/**
	 * show banner
	 */

	public void showAdsBanner(Activity activity) {
		if (activity == null) {
			return;
		}
		this.activity = activity;
		if (!isInit) {
			return;
		}
		try {
			ViewGroup rootView = activity.findViewById(android.R.id.content);
			if (rootView.getChildCount() > 0) {
				View viewMain = rootView.getChildAt(0);
				rootView.removeView(viewMain);
				LinearLayout linearLayout;
				if (viewMain.findViewById(ID_CONTAIN_AD_VIEW) != null) {
					linearLayout = viewMain.findViewById(ID_CONTAIN_AD_VIEW);
					if (linearLayout.findViewById(ID_BANNER_VIEW) != null) {
						linearLayout.removeView(linearLayout.findViewById(ID_BANNER_VIEW));
					}
				} else {
					linearLayout = new LinearLayout(context);
					linearLayout.setOrientation(LinearLayout.VERTICAL);
					linearLayout.setId(ID_CONTAIN_AD_VIEW);
					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
							ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					layoutParams.weight = 1;
					linearLayout.addView(viewMain, layoutParams);
				}
				switch (adsBannerType) {
					case GOOGLE:
						linearLayout.addView(getAdsGGBannerView());
						break;

					case FACEBOOK:
						linearLayout.addView(getAdsFBBannerView());
						break;

					case APP_NEXT:
						linearLayout.addView(getAdsANBannerView());
						break;

					case VUN_GLE:
						break;

					default:
						break;
				}
				activity.setContentView(linearLayout);
				heightGone = getStatusBarHeight() + getNavigationBarHeight();
				addListenerGlobalLayout(activity.getResources().getDisplayMetrics().heightPixels, rootView);
			}
		} catch (Exception ignored) {
			ignored.printStackTrace();
		}
	}

	private int heightGone = 200;

	private void addListenerGlobalLayout(final int height, final ViewGroup rootView) {
		rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				try {
					Rect r = new Rect();
					rootView.getWindowVisibleDisplayFrame(r);
					int heightDiff = height - (r.bottom - r.top);
					rootView.findViewById(ID_BANNER_VIEW).setVisibility(heightDiff > heightGone + 50 ? View.GONE : View.VISIBLE);
				} catch (Exception ignored) {
					ignored.printStackTrace();
				}
			}
		});
	}

	private int getStatusBarHeight() {
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			return context.getResources().getDimensionPixelSize(resourceId);
		}
		return 0;
	}

	private int getNavigationBarHeight() {
		boolean isNavigationBar = context.getResources().getBoolean(context.getResources().getIdentifier("config_showNavigationBar", "bool", "android"));
		int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
		if (resourceId > 0 && isNavigationBar) {
			return context.getResources().getDimensionPixelSize(resourceId);
		}
		return 0;
	}

	/**
	 * listener app next
	 */

	private com.appnext.nativeads.NativeAdListener adsANNativeListener = new com.appnext.nativeads.NativeAdListener() {
		@Override
		public void onAdLoaded(com.appnext.nativeads.NativeAd nativeAd) {
			super.onAdLoaded(nativeAd);
			ILog.e("app next native: onAdLoaded");
			// inflateAd(nativeAd);
			// LinearLayout linearLayout = findViewById(R.id.native_ad_container);
			// linearLayout.addView(rlContainer);
			if (nativeAd != adsANNativeAd) {
				return;
			}
			rlContainer = getANNativeView(adsANNativeAd);
		}

		@Override
		public void onAdClicked(com.appnext.nativeads.NativeAd nativeAd) {
			super.onAdClicked(nativeAd);
			ILog.e("app next native: onAdClicked");
		}

		@Override
		public void onError(com.appnext.nativeads.NativeAd nativeAd, AppnextError appnextError) {
			super.onError(nativeAd, appnextError);
			ILog.e("app next native: onError-" + appnextError.getErrorMessage());
		}

		@Override
		public void adImpression(com.appnext.nativeads.NativeAd nativeAd) {
			super.adImpression(nativeAd);
			ILog.e("app next native: adImpression");
		}
	};

	private BannerListener adsANBannerListener = new BannerListener() {
		@Override
		public void onAdClicked() {
			super.onAdClicked();
			ILog.e("app next banner: onAdClicked");
		}

		@Override
		public void onAdLoaded(String s) {
			super.onAdLoaded(s);
			ILog.e("app next banner: onAdLoaded-" + s);
		}

		@Override
		public void onError(AppnextError appnextError) {
			super.onError(appnextError);
			ILog.e("app next banner: onError-" + appnextError.getErrorMessage());
		}

		@Override
		public void adImpression() {
			super.adImpression();
			ILog.e("app next banner: adImpression");
		}
	};

	private OnAdClicked adsANFullOnAdClickedListener = new OnAdClicked() {
		@Override
		public void adClicked() {
			ILog.e("app next full: adLoaded");
		}
	};

	private OnAdClosed adsANFullOnAdClosedListener = new OnAdClosed() {
		@Override
		public void onAdClosed() {
			ILog.e("app next full: onAdClosed");
		}
	};

	private OnAdError adsANFullOnAdErrorListener = new OnAdError() {
		@Override
		public void adError(String s) {
			ILog.e("app next full: adError-" + s);
		}
	};

	private OnAdOpened adsANFullOnAdOpenedListener = new OnAdOpened() {
		@Override
		public void adOpened() {
			ILog.e("app next full: adOpened");
		}
	};

	private OnAdLoaded adsANFullOnAdLoadedListener = new OnAdLoaded() {
		@Override
		public void adLoaded(String s) {
			ILog.e("app next full: adLoaded-" + s);
			if (isFullShow) {
				mInterstitialAdAN.showAd();
				isFullShow = false;
			}
		}
	};

	/**
	 * listener facebook
	 */

	private NativeAdListener adsFBNativeAdListener = new NativeAdListener() {

		@Override
		public void onMediaDownloaded(Ad ad) {
			ILog.e("facebook native: onMediaDownloaded");
		}

		@Override
		public void onError(Ad ad, AdError adError) {
			ILog.e("facebook native: onError-" + adError.getErrorMessage());
			loadANNativeAd();
		}

		@Override
		public void onAdLoaded(Ad ad) {
			ILog.e("facebook native: onAdLoaded");
			if (adsFBNativeAd == null || adsFBNativeAd != ad) {
				loadANNativeAd();
				return;
			}
			rlContainer = getFBNativeView(adsFBNativeAd);
		}

		@Override
		public void onAdClicked(Ad ad) {
			ILog.e("facebook native: onAdClicked");
		}

		@Override
		public void onLoggingImpression(Ad ad) {
			// ILog.e("facebook native: onLoggingImpression");
		}

	};

	private com.facebook.ads.InterstitialAdListener adsFBFullListener = new InterstitialAdListener() {
		@Override
		public void onInterstitialDisplayed(Ad ad) {
			ILog.e("facebook full: onInterstitialDisplayed");
		}

		@Override
		public void onInterstitialDismissed(Ad ad) {
			ILog.e("facebook full: onInterstitialDismissed");
			// mInterstitialAdFB.loadAd();
			ad.loadAd();
		}

		@Override
		public void onError(Ad ad, AdError adError) {
			ILog.e("facebook full: onError-" + adError.getErrorMessage());
			adsFullType = AdMobType.APP_NEXT;
			initAdsANFull();
		}

		@Override
		public void onAdLoaded(Ad ad) {
			ILog.e("facebook full: onAdLoaded");
			if (isFullShow) {
				mInterstitialAdFB.show();
				isFullShow = false;
			}
		}

		@Override
		public void onAdClicked(Ad ad) {
			ILog.e("facebook full: onAdClicked");
		}

		@Override
		public void onLoggingImpression(Ad ad) {
			// ILog.e("facebook full: onLoggingImpression");
		}
	};

	private com.facebook.ads.AdListener adsFBBannerListener = new com.facebook.ads.AdListener() {

		@Override
		public void onError(Ad ad, AdError adError) {
			ILog.e("facebook banner: onError-" + adError.getErrorMessage());
			adsBannerType = AdMobType.APP_NEXT;
			showAdsBanner(activity);
		}

		@Override
		public void onAdLoaded(Ad ad) {
			ILog.e("facebook banner: onAdLoaded");
		}

		@Override
		public void onAdClicked(Ad ad) {
			ILog.e("facebook banner: onAdClicked");
		}

		@Override
		public void onLoggingImpression(Ad ad) {
			// ILog.e("facebook banner: onLoggingImpression");
		}
	};

	/**
	 * listener google
	 */

	private AdListener adsGGBannerListener = new AdListener() {
		@Override
		public void onAdLoaded() {
			ILog.e("google banner: onAdLoaded");
		}

		@Override
		public void onAdFailedToLoad(int errorCode) {
			ILog.e("google banner: onAdFailedToLoad-" + errorCode);
			adsBannerType = AdMobType.FACEBOOK;
			showAdsBanner(activity);
		}

		@Override
		public void onAdOpened() {
			ILog.e("google banner: onAdOpened");
		}

		@Override
		public void onAdLeftApplication() {
			ILog.e("google banner: onAdLeftApplication");
		}

		@Override
		public void onAdClosed() {
			ILog.e("google banner: onAdClosed");
		}
	};

	private AdListener adsGGFullListener = new AdListener() {
		@Override
		public void onAdLoaded() {
			ILog.e("google full: onAdLoaded");
			if (isFullShow) {
				mInterstitialAdGG.show();
				isFullShow = false;
			}
		}

		@Override
		public void onAdFailedToLoad(int errorCode) {
			ILog.e("google full: onAdFailedToLoad-" + errorCode);
			adsFullType = AdMobType.FACEBOOK;
			initAdsFBFull();
		}

		@Override
		public void onAdOpened() {
			ILog.e("google full: onAdOpened");
		}

		@Override
		public void onAdLeftApplication() {
			ILog.e("google full: onAdLeftApplication");
		}

		@Override
		public void onAdClosed() {
			ILog.e("google full: onAdClosed");
			mInterstitialAdGG.loadAd(new AdRequest.Builder().build());
		}
	};

	/**
	 * builder
	 */

	public static class Builder {
		private com.myadmob.AdMob adMob;

		public Builder(Context context) {
			adMob = com.myadmob.AdMob.getInstance(context);
		}

		public Builder() {
			adMob = com.myadmob.AdMob.getInstance();
		}

		public Builder setGoogleAppId(String gAdsAppId) {
			adMob.setAdsGGAppId(gAdsAppId);
			return this;
		}

		public Builder setGoogleBannerId(String adsGGBannerId) {
			adMob.setAdsGGBannerId(adsGGBannerId);
			return this;
		}

		public Builder setGoogleFullId(String adsGGFullId) {
			adMob.setAdsGGFullId(adsGGFullId);
			return this;
		}

		public Builder setFacebookBannerId(String adsFBBannerId) {
			adMob.setAdsFBBannerId(adsFBBannerId);
			return this;
		}

		public Builder setFacebookFullId(String adsFBFullId) {
			adMob.setAdsFBFullId(adsFBFullId);
			return this;
		}

		public Builder setFacebookNativeId(String adsFBNativeId) {
			adMob.setAdsFBNativeId(adsFBNativeId);
			return this;
		}

		public Builder setAppNextBannerId(String adsANBannerId) {
			adMob.setAdsANBannerId(adsANBannerId);
			return this;
		}

		public Builder setAppNextFullId(String adsANFullId) {
			adMob.setAdsANFullId(adsANFullId);
			return this;
		}

		public Builder setAppNextNativeId(String adsANNativeId) {
			adMob.setAdsANNativeId(adsANNativeId);
			return this;
		}

		public com.myadmob.AdMob build() {
			return adMob;
		}
	}
}
