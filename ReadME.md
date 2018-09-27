
#Lưu ý: Đọc kĩ hướng dẫn sử dụng trước khi dùng########

[![](https://jitpack.io/v/chanhbc/admob.svg)](https://jitpack.io/#chanhbc/admob)

1. Thêm lib "admob.jar" vào thư mục lib (app/libs).

2. Thêm các thư viện sau vào build.gradle (app):

	implementation files('libs/admob.jar')
    implementation 'com.google.android.gms:play-services-ads:15.0.0'
    implementation 'com.google.android.gms:play-services-location:15.0.0'
    implementation 'com.facebook.android:audience-network-sdk:4.99.1'
    implementation 'com.appnext.sdk:banners:2.4.2.472'
    implementation 'com.appnext.sdk:ads:2.4.2.472'
    implementation 'com.appnext.sdk:native-ads2:2.4.2.472'
    implementation 'org.jsoup:jsoup:1.11.3'

như sau:

	dependencies {
		// ...
		// ngay chỗ này, thêm tại đây
	}

3. Thêm maven build.grade (Project):

    google()
    maven {
        url "http://dl.appnext.com/"
    }

như sau:

	allprojects {
        repositories {
        	google()
            jcenter()
            // thêm vào tại đây
        }
    }

4. Bấm "Sync Now"

5. Thêm vào file style.xml

	<style name="Theme.AppCompat.Translucent" parent="AppTheme">
        <item name="android:windowNoTitle">true</item>
        <item name="android:windowBackground">@android:color/transparent</item>
        <item name="android:windowIsTranslucent">true</item>
        <item name="android:windowAnimationStyle">@android:style/Animation</item>
    </style>

6. Copy đoạn code sau vào file manifest:

	-permission

    <uses-permission android:name="android.permission.INTERNET"/>

    -application

	<activity
        android:name="com.myadmob.ui.NativeActivity"
        android:configChanges="keyboard|keyboardHidden|orientation|screenLayout|uiMode|screenSize|smallestScreenSize"
        android:theme="@style/Theme.AppCompat.Translucent"/>

#Hướng dẫn sử dụng

---Thêm ID quảng cáo vào file string.xml

	<string name="google_admob_app_id">ca-app-pub-6649049261121782~4343430031</string>
    <string name="google_ads_banner_id">ca-app-pub-6649049261121782/9329654099</string>
    <string name="google_ads_full_id">ca-app-pub-6649049261121782/3577143272</string>
    <string name="facebook_ads_banner_id"></string>
    <string name="facebook_ads_full_id"></string>
    <string name="facebook_ads_native_id"></string>
    <string name="app_next_ads_banner_id">474ae793-eaea-45b4-91ba-89241bf56214</string>
    <string name="app_next_ads_full_id">474ae793-eaea-45b4-91ba-89241bf56214</string>
    <string name="app_next_ads_native_id">474ae793-eaea-45b4-91ba-89241bf56214</string>
-
---cài đặt 1 lần dùng được nhiều chỗ(nên cài đặt ở activity đầu tiên, các activity sau chỉ việc gọi)

	new AdMob.Builder(context)
		.setGoogleAppId(getString(R.string.google_admob_app_id))
		.setGoogleBannerId(getString(R.string.google_ads_banner_id))
		.setGoogleFullId(getString(R.string.google_ads_full_id))
		.setFacebookBannerId(getString(R.string.facebook_ads_banner_id))
		.setFacebookFullId(getString(R.string.facebook_ads_full_id))
		.setFacebookNativeId(getString(R.string.facebook_ads_native_id))
		.setAppNextBannerId(getString(R.string.app_next_ads_banner_id))
		.setAppNextFullId(getString(R.string.app_next_ads_full_id))
		.setAppNextNativeId(getString(R.string.app_next_ads_native_id))
		.build();

---gọi quảng cáo.

+++ banner: gọi trên activity hoặc có context activity là được, banner tự thêm vào dưới cùng của activity

	AdMob adMob = new AdMob.Builder().build();
	adMob.showAdsBanner(activity);
	 // context activity

+++ full screen: gọi bất cứ đâu, gọi là hiện :D
	
	AdMob adMob = new AdMob.Builder().build();
	adMob.showAdsFull(); // show quảng cáo sau mỗi lần gọi hàm này
	// or
	adMob.showAdsFull(5); // show quảng cáo sau 5 lần gọi hàm này

+++ native: trả về 1 RelativeLayout chữa view quảng cáo, dùng để chèn quảng cáo vào các view trên layout như trong các list view, v.v...
	
	AdMob adMob = new AdMob.Builder().build();
	RelativeLayout nativeAdView = adMob.getNativeView();
	View view = findViewById(...);
	view.addView(nativeAdView, new LayoutParam(-1, -1));

+++ admob time out: quảng cáo giống với full screen, có thể set thời gian và kích thước, cũng có thể gọi ở bất cứ đâu

	AdMob adMob = new AdMob.Builder().build();
	adMob.showNativeTimeOut(); // default 5000(ms), 840x640
	// or
	adMob.showNativeTimeOut(10000, 640, 480); //10000(ms),  640x480

