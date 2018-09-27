package com.myadmob.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appnext.core.AppnextError;
import com.appnext.nativeads.NativeAdRequest;
import com.appnext.nativeads.NativeAdView;
import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;
import com.myadmob.util.ILog;

import java.util.ArrayList;
import java.util.List;

public class NativeActivity extends Activity {
	private boolean isClose = false;
	private Context context;
	private NativeAd adsFBNativeAd;
	private RelativeLayout rlContainer;
	private String adsFBNativeId;
	private String adsANNativeId;
	private int timeOut = 4000;
	private int width = 840;
	private int height = 640;
	private com.appnext.nativeads.NativeAd adsANNativeAd;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		if (getIntent().getBooleanExtra("is_full_screen", false)) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		String[] adsNativeIds = getIntent().getStringExtra("ads_native_id").split("@");
		switch (adsNativeIds.length) {
			case 2:
				adsANNativeId = adsNativeIds[1];
			case 1:
				adsFBNativeId = adsNativeIds[0];
				break;
			default:
				break;
		}
		timeOut = getIntent().getIntExtra("time_out", timeOut);
		width = getIntent().getIntExtra("width", width);
		height = getIntent().getIntExtra("height", height);
		if(getIntent().getBooleanExtra("is_die", false)){
			loadANNativeAd();
		}else {
			loadFBNativeAd();
		}
		rlContainer = new RelativeLayout(context);
		ProgressBar progressBar = new ProgressBar(context);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		rlContainer.addView(progressBar, layoutParams);
		setContentView(rlContainer);
	}


	private void loadFBNativeAd() {
		adsFBNativeAd = new NativeAd(this, adsFBNativeId);
		adsFBNativeAd.setAdListener(adsFBNativeListener);
		adsFBNativeAd.loadAd();
	}

	private void loadANNativeAd() {
		adsANNativeAd = new com.appnext.nativeads.NativeAd(context, adsANNativeId);
		adsANNativeAd.setAdListener(adsANNativeListener);
		adsANNativeAd.loadAd(new NativeAdRequest());
	}

	@SuppressLint ("SetTextI18n")
	private void inflateAd(NativeAd nativeAd) {
		rlContainer.removeAllViews();
		nativeAd.unregisterView();
		int padding3 = convertDpToPx(3);
		int padding5 = convertDpToPx(5);
		int padding10 = convertDpToPx(10);

		// todo native top
		LinearLayout nativeTop = new LinearLayout(context);
		nativeTop.setOrientation(LinearLayout.HORIZONTAL);
		nativeTop.setPadding(0, padding10, 0, padding10);
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
		int size36 = convertDpToPx(36);
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
				convertDpToPx(60), LinearLayout.LayoutParams.WRAP_CONTENT));
		LinearLayout adView = new LinearLayout(context);
		adView.setBackgroundColor(Color.WHITE);
		adView.setOrientation(LinearLayout.VERTICAL);
		adView.setPadding(padding10, padding10, padding10, padding10);
		adView.addView(nativeTop, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

		// todo native mid
		LinearLayout nativeMid = new LinearLayout(context);
		MediaView nativeMediaView = new MediaView(context);
		nativeMediaView.setGravity(Gravity.CENTER);
		nativeMid.addView(nativeMediaView);
		adView.addView(nativeMid);

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
		btnAction.setPadding(padding3, padding3, padding3, padding3);
		btnAction.setGravity(Gravity.CENTER);
		btnAction.setTextSize(12);
		btnAction.setVisibility(View.GONE);
		nativeBot.addView(btnAction, lpBtn);
		adView.addView(nativeBot);

		RelativeLayout.LayoutParams lpAdView = new RelativeLayout.LayoutParams(width, height);
		lpAdView.addRule(RelativeLayout.CENTER_IN_PARENT);
		rlContainer.addView(adView, lpAdView);

		// todo text view close
		final TextView tvClose = new TextView(this);
		GradientDrawable gradientDrawable = new GradientDrawable();
		gradientDrawable.setColor(Color.WHITE);
		gradientDrawable.setShape(GradientDrawable.OVAL);
		int size30 = convertDpToPx(30);
		gradientDrawable.setSize(size30, size30);
		gradientDrawable.setStroke(convertDpToPx(0.5f), Color.GRAY);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			tvClose.setBackground(gradientDrawable);
		} else {
			tvClose.setBackgroundDrawable(gradientDrawable);
		}
		tvClose.setTextColor(Color.GRAY);
		tvClose.setTextSize(15);
		tvClose.setGravity(Gravity.CENTER);
		tvClose.setEnabled(false);
		tvClose.setPadding(padding3, padding3, padding3, padding3);
		tvClose.setText((timeOut / 1000) + "");
		tvClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		RelativeLayout.LayoutParams lpClose = new RelativeLayout.LayoutParams(-2, -2);
		lpClose.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lpClose.rightMargin = (rlContainer.getWidth() - width) / 2 - padding10;
		lpClose.topMargin = (rlContainer.getHeight() - height) / 2 - padding10;
		rlContainer.addView(tvClose, lpClose);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (timeOut > 0) {
						Thread.sleep(1000);
						timeOut -= 1000;
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (timeOut <= 0) {
									tvClose.setText("X");
									tvClose.setEnabled(true);
									isClose = true;
								} else {
									tvClose.setText(timeOut / 1000 + "");
								}
							}
						});
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

		// todo set
		tvTitle.setText(nativeAd.getAdvertiserName());
		tvDetail.setText(nativeAd.getAdBodyText());
		tvContent.setText(nativeAd.getAdSocialContext());
		btnAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
		btnAction.setText(nativeAd.getAdCallToAction());
		tvLabel.setText(nativeAd.getSponsoredTranslation());

		// todo click event
		List<View> clickableViews = new ArrayList<>();
		clickableViews.add(tvTitle);
		clickableViews.add(nativeMediaView);
		clickableViews.add(adIconView);
		clickableViews.add(btnAction);

		// todo register click
		nativeAd.registerViewForInteraction(
				adView,
				nativeMediaView,
				adIconView,
				clickableViews);
	}

	@SuppressLint ("SetTextI18n")
	private void getANNativeView(com.appnext.nativeads.NativeAd adsANNativeAd) {
		rlContainer.removeAllViews();
		int padding10 = convertDpToPx(10);
		int padding5 = convertDpToPx(5);
		int padding3 = convertDpToPx(3);

		NativeAdView nativeAdView = new NativeAdView(context);
		nativeAdView.setBackgroundColor(Color.WHITE);
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
//		ivStar.setImageResource(R.drawable.apnxt_ads_full_star);
		LinearLayout llStar = new LinearLayout(context);
		llStar.setOrientation(LinearLayout.HORIZONTAL);
		llStar.setGravity(Gravity.CENTER_VERTICAL);
		llStar.addView(tvLabel);
		llStar.addView(ivStar);
		nativeTitle.setPadding(padding5, 0, padding5, 0);
		nativeTitle.addView(tvTitle);
		nativeTitle.addView(llStar);
		nativeTitle.addView(tvDescription);
		int size36 = convertDpToPx(36);
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
		RelativeLayout.LayoutParams rlNativeAdView = new RelativeLayout.LayoutParams(width, height);
		rlNativeAdView.addRule(RelativeLayout.CENTER_IN_PARENT);
		rlContainer.addView(nativeAdView, rlNativeAdView);

		// todo text view close
		final TextView tvClose = new TextView(this);
		GradientDrawable gradientDrawable = new GradientDrawable();
		gradientDrawable.setColor(Color.WHITE);
		gradientDrawable.setShape(GradientDrawable.OVAL);
		int size30 = convertDpToPx(30);
		gradientDrawable.setSize(size30, size30);
		gradientDrawable.setStroke(convertDpToPx(0.5f), Color.GRAY);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			tvClose.setBackground(gradientDrawable);
		} else {
			tvClose.setBackgroundDrawable(gradientDrawable);
		}
		tvClose.setTextColor(Color.GRAY);
		tvClose.setTextSize(15);
		tvClose.setGravity(Gravity.CENTER);
		tvClose.setEnabled(false);
		tvClose.setPadding(padding3, padding3, padding3, padding3);
		tvClose.setText((timeOut / 1000) + "");
		tvClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		RelativeLayout.LayoutParams lpClose = new RelativeLayout.LayoutParams(-2, -2);
		lpClose.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lpClose.rightMargin = (rlContainer.getWidth() - width) / 2 - padding10;
		lpClose.topMargin = (rlContainer.getHeight() - height) / 2 - padding10;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (timeOut > 0) {
						Thread.sleep(1000);
						timeOut -= 1000;
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (timeOut <= 0) {
									tvClose.setText("X");
									tvClose.setEnabled(true);
									isClose = true;
								} else {
									tvClose.setText(timeOut / 1000 + "");
								}
							}
						});
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
		rlContainer.addView(tvClose, lpClose);
	}

	private NativeAdListener adsFBNativeListener = new NativeAdListener() {

		@Override
		public void onMediaDownloaded(Ad ad) {
			ILog.e("facebook native: onMediaDownloaded");
		}

		@Override
		public void onError(Ad ad, AdError adError) {
			// rlContainer.removeAllViews();
			loadANNativeAd();
			ILog.e("facebook native time out: onError-" + adError.getErrorMessage());
		}

		@Override
		public void onAdLoaded(Ad ad) {
			ILog.e("facebook native time out: onAdLoaded");
			if (adsFBNativeAd == null || adsFBNativeAd != ad) {
				loadANNativeAd();
				return;
			}
			inflateAd(adsFBNativeAd);
		}

		@Override
		public void onAdClicked(Ad ad) {
			ILog.e("facebook native time out: onAdClicked");
		}

		@Override
		public void onLoggingImpression(Ad ad) {
			ILog.e("facebook native time out: onLoggingImpression");
		}

	};

	private com.appnext.nativeads.NativeAdListener adsANNativeListener = new com.appnext.nativeads.NativeAdListener() {
		@Override
		public void onAdLoaded(com.appnext.nativeads.NativeAd nativeAd) {
			super.onAdLoaded(nativeAd);
			ILog.e("app next native time out: onAdLoaded");
			if (nativeAd != adsANNativeAd) {
				return;
			}
			getANNativeView(adsANNativeAd);
		}

		@Override
		public void onAdClicked(com.appnext.nativeads.NativeAd nativeAd) {
			super.onAdClicked(nativeAd);
			ILog.e("app next native time out: onAdClicked");
		}

		@Override
		public void onError(com.appnext.nativeads.NativeAd nativeAd, AppnextError appnextError) {
			super.onError(nativeAd, appnextError);
			finish();
			ILog.e("app next native time out: onError-" + appnextError.getErrorMessage());
		}

		@Override
		public void adImpression(com.appnext.nativeads.NativeAd nativeAd) {
			super.adImpression(nativeAd);
			ILog.e("app next native time out: adImpression");
		}
	};

	private int convertDpToPx(float dp) {
		return (int) (dp * getResources().getDisplayMetrics().density);
	}


	@Override
	public void onBackPressed() {
		if (isClose) {
			super.onBackPressed();
		}
	}
}
