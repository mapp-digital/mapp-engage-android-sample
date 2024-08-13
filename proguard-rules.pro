# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/yonigross/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}


-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,EnclosingMethod,*Annotation*
#-dontshrink
#-dontoptimize

# public API
-keep public class com.appoxee.** { *; }
-keep class com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.reflect.TypeToken
-keep public class * implements java.lang.reflect.Type
#-keep public class com.appoxee.analytics.* { public *; }
#-keep public class com.appoxee.push.* { public *; }
#-keepclassmembers class com.appoxee.internal.inapp.** { *; }
#-keep class com.appoxee.internal.inapp.model.InAppStatistics  {*;}
# serialized objects
-keep class * implements com.appoxee.internal.network.Networkable { *; }
-keep class * implements com.appoxee.internal.commandstore.Model { *; }
-keep class * implements com.appoxee.internal.network.request.NetworkRequestFactory { *; }
-keep class * implements com.appoxee.internal.badge.Badger { *; }
-keep class com.appoxee.internal.geo.** { *; }
-keep class * extends com.appoxee.internal.command.Command { *; }

#-keep class com.appoxee.internal.model.Device$* { *; }
#-keep public class com.appoxee.internal.inapp.model.* { public *; }
# -keepclassmembers class com.appoxee.internal.inapp.model.APXInboxMessage** {
#    *;
# }
#
#-keep class com.appoxee.internal.inapp.InAppInboxEventService { *; }
#
#-keep public class com.appoxee.internal.inapp.InAppEventService { public protected private *; }
#
#-keepclassmembers public class com.appoxee.internal.inapp.InAppEventService {
#     private <methods> ;
# }
#
# -keepclassmembers public class com.appoxee.internal.inapp.InAppEventService {
#      private <fields> ;
#  }
#
#-keep public class com.appoxee.internal.inapp.** { public *; }
#
## for Gson
#-keep class sun.misc.Unsafe { *; }
#-keep class android.support.v4.** { *; }
#-keep interface android.support.v4.** { *; }
#
## for android.gms.location
#-keep class com.google.android.gms.location.** { *; }
##
##
#-keepclassmembers class * implements android.os.Parcelable {
#    static android.os.Parcelable$Creator CREATOR;
#}
##
#-keepnames class * implements java.io.Serializable
#-keepclassmembers class * implements java.io.Serializable {
#    static final long serialVersionUID;
#    private static final java.io.ObjectStreamField[] serialPersistentFields;
#    !static !transient <fields>;
#    private void writeObject(java.io.ObjectOutputStream);
#    private void readObject(java.io.ObjectInputStream);
#    java.lang.Object writeReplace();
#    java.lang.Object readResolve();
#}
#
#
#-keep public class * extends android.app.Activity
#-keep public class * extends android.app.Application
#-keep public class * extends android.app.MapActivity
#-keep public class * extends android.app.Service
#-keep public class * extends android.content.BroadcastReceiver
#-keep public class * extends android.content.ContentProvider
#-keep public class * extends android.app.Dialog
#-keep public class * extends android.support.v4.app.NotificationCompa
#-keep public class android.support.v4.** { *; }
#
#
#-keep public class * extends android.view.View {
#    public <init>(android.content.Context);
#    public <init>(android.content.Context, android.util.AttributeSet);
#    public <init>(android.content.Context, android.util.AttributeSet, int);
#    public void set*(...);
#}
#
#-keepclasseswithmembers class * {
#    public <init>(android.content.Context, android.util.AttributeSet);
#}
#
#-keepclasseswithmembers class * {
#    public <init>(android.content.Context, android.util.AttributeSet, int);
#}
#
#-keepclassmembers class * extends android.content.Context {
#   public void *(android.view.View);
#   public void *(android.view.MenuItem);
#}
#
#-keep class com.appoxee.internal.badge.** { <init>(...); }
#
# -keep interface * {
#   <methods>;
# }
#
#-keep class * extends WebViewClient {
#    *;
#}
#
#-keepclassmembers class * {
#    @android.webkit.JavascriptInterface <methods>;
#}
#
#-dontwarn android.support.v7.**
#-keep class android.support.v7.** { *; }
#-keep interface android.support.v7.** { *; }
#-keep class android.support.** { *;}
#-keepclassmembers class * extends android.app.Activity {
#   public void *(android.view.View);
#}
#
## Google service
-keep public class com.google.android.gms.* { public *; }
-dontwarn com.google.android.gms.**

