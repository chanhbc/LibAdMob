
-keep public class com.myadmob.AdMob
-keep class com.myadmob.AdMob { *; }
-keep class com.myadmob.AdMob$Builder { *; }
-keepnames class com.myadmob.AdMob
-keepclassmembernames class com.myadmob.AdMob {
    public <methods>;
    public <fields>;
}

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}