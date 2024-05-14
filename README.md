This document explains how to add the Android Mapp SDK code to your application code.

**The latest Mapp Engage SDK version** [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.mapp.sdk/mapp-android/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/maven-central/com.mapp.sdk/mapp-android)

<h2>Source code integration</h2>

Make sure your `app/build.gradle` file include the `applicationId` attribute in `defaultConfig` block. 
Include the following dependencies to your app's `gradle.build` dependencies section :

```
apply plugin: 'com.android.application'
apply plugin: 'com.google.gms.google-services'

android {
    compileSdkVersion 34 // or above
    buildToolsVersion '34.0.0' // or above
    defaultConfig {
        applicationId "com.yourapppackage.app" //make sure you have this
        minSdkVersion 19
        targetSdkVersion 34
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }
}

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation 'androidx.appcompat:appcompat:1.6.1'
    .
    .
    .
    implementation platform('com.google.firebase:firebase-bom:32.8.1')
    implementation('com.google.firebase:firebase-messaging')
    implementation 'com.google.firebase:firebase-analytics'
    implementation 'com.google.firebase:firebase-crashlytics'

    implementation('com.mapp.sdk:mapp-android:X.X.X') //the latest Mapp Engage SDK version
}
```

Create class which extends `android.app.Application`. And add the following code snippet:

```
public class MappApp extends Application {

    private AppoxeeOptions opt;

    private Appoxee.OnInitCompletedListener initFinishedListener = new Appoxee.OnInitCompletedListener() {
        @Override
        public void onInitCompleted(boolean successful, Exception failReason) {
            Log.i("APX", "init completed listener - Application class");
			Appoxee.instance().setPushEnabled(true);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        opt = new AppoxeeOptions();
        opt.sdkKey = SDK_KEY;
        opt.appID = APP_ID;
        opt.tenantID = "TENANT_ID;
        opt.notificationMode = NotificationMode.BACKGROUND_AND_FOREGROUND;
        opt.server = AppoxeeOptions.Server.EMC;


		Appoxee.engage(this, opt);
        Appoxee.instance().setReceiver(MyPushBroadcastReceiver.class);   
        Appoxee.instance().addInitListener(initFinishedListener);
        Appoxee.setOrientation(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
```

`SDK_KEY`, `APP_ID` are present in your CEP dashboard. 

`NotificationMode` is an enum and you can choose one of three options:

`BACKGROUND_ONLY` - notification will show only when the app is closed or in idle mode.

`BACKGROUND_AND_FOREGROUND` - notification will show every time when a push notification comes.

`SILENT_ONLY` - notification never shows on the device.

If you don't choose one of these options, by default is `BACKGROUND_ONLY`.  

`AppoxeeOptions.Server` is enum and you can choose one of four options:

```
L3

L3_US

EMC

EMC_US

CROC

TEST
```

Account manager will provide you info which one you should use in your application (L3, EMC or CROC). 

If you don't choose one of these options, by default is a TEST. 
Our developers use TEST for development purpose and you shouldn't use this one.


Add application in `AndroidManifest.xml` file

```
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="mapp.android.demo">

    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:name=".MappApp"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

For receiving push events like push received, push opened, push dismissed you need to create on the receiver, which extends `PushDataReceiver`.

```
public class MyPushBroadcastReceiver extends PushDataReceiver {
    @Override
    public void onPushReceived(PushData pushData) {
        Log.d("APX", "Push received " + pushData);
    }

    @Override
    public void onPushOpened(PushData pushData) {
        Log.d("APX", "Push opened " + pushData);
    }

    @Override
    public void onPushDismissed(PushData pushData) {
        Log.d("APX", "Push dismissed " + pushData);
    }

    @Override
    public void onSilentPush(PushData pushData) {
        Log.d("APX", "Push Silent " + pushData);
    }
}
```

In `AplicationManifest` within application tag you should add:

```
<receiver
    android:name=".MyPushBroadcastReceiver"
    android:enabled="true"
    android:exported="false">
    <intent-filter>
        <action android:name="com.appoxee.PUSH_OPENED" />
        <action android:name="com.appoxee.PUSH_RECEIVED" />
        <action android:name="com.appoxee.PUSH_DISMISSED" />

		<category android:name="${applicationId}" />
    </intent-filter>
</receiver>
```

For custom layout notifications, add this code in Application class, before calling `Appoxee.engage` method

```
CustomXmlLayoutNotificationCreator.Builder builder = new CustomXmlLayoutNotificationCreator.Builder(this);
 builder.setLayoutResource(R.layout.custom_notification_layout)  //your custom notification layout
         .setIconResourceId(R.id.appoxee_default_push_icon)
         .setTextResourceId(R.id.appoxee_default_push_message)
         .setTitleResourceId(R.id.appoxee_default_push_subject)
         .setTimeResourceId(R.id.appoxee_default_push_hour);
 opt.customNotificationCreator = new CustomXmlLayoutNotificationCreator(builder);

 Appoxee.engage(this, opt);
```

<h2>POST NOTIFICATIONS Runtime permission (Android 13+)</h2>

>From Android 13 (Tiramisu, SDK 33), for displaying notifications, application should request  POST_NOTIFICATIONS permission. More on this can be found on the official Google's documentation.

First, add in `ApplicationManifest.xml` required permission:

```
<manifest ...>
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
    <application ...>
        ...
    </application>
</manifest>
```

Mapp Engage SDK provides proper method for requesting permission in runtime. 

Explain to a user why permission is needed and request permission during some logical use case. 

Permission can be requested from Activity or Fragment with the following method:

```
Appoxee.instance().requestNotificationsPermission(this, results -> {
    if (results.containsKey(Manifest.permission.POST_NOTIFICATIONS) && 
			results.get(Manifest.permission.POST_NOTIFICATIONS) == PermissionsManager.PERMISSION_GRANTED) {
        	Toast.makeText(MainActivity.this,"POST NOTIFICATIONS GRANTED!", Toast.LENGTH_SHORT).show();
       	}
    }
);
```

Add a callback when appoxee finished all initialization, the callback will be called when Appoxee is up and ready, or if server hand-shaking is failed.

```
Appoxee.instance().addInitListener(new Appoxee.OnInitCompletedListener() {
    @Override
    public void onInitCompleted(boolean successful, Exception failReason) {
        
    }
});
```

Another way to check the initialization finish or not is to use the following method:

```
Appoxee.instance().isReady();
```

<h3>Notification icon</h3>

Default Notification icon and color (optional). You can specify a custom default icon and a custom default color. You can choose one option of offered: 

**First option**

Add meta-data tags in the `AndroidManifest.xml` file to define attributes:
`com.mapp.default_notification_icon` and `com.mapp.default_notification_color`.

```
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">
    <application ...>
          <activity...>
            ...
          </activity>
          ...
          
          <meta-data android:name="com.mapp.default_notification_icon"
                     android:resource="@drawable/notification_icon" />
          <meta-data android:name="com.mapp.default_notification_color"
                     android:resource="@color/blue" />
    </application>
```

**Second option is to add two icons in the drawable folder(s) with the following names:**

```
apx_small_icon
apx_large_icon
```

<h3>Enable additional Push Messaging service.</h3>

Create class e.g. `MyMessagingService` and extend it from `MappMessagingService`, or if you already have created that class, change its parent class from `FirebaseMessagingService → MappMessagingService`.

```
public class MyMessageService extends MappMessagingService {

}
```

Add following into `AndroidManifest.xml` inside of the `<application></application>` tags.

```
<service android:name="com.appoxee.push.fcm.MappMessagingService"
            android:exported="true"
            tools:node="remove">
            <intent-filter>
                <action android:name="com.google.firebase.MESSAGING_EVENT" />
            </intent-filter>
        </service>

        <service android:name=".MyMessageService"
            android:exported="false">
            <intent-filter>
                <action android:name="com.google.firebase.MESSAGING_EVENT" />
            </intent-filter>
        </service>
```

Then, in your extended class `MyMessagingService` override methods `onMessageReceived` and `onNewToken`, and add required calls like this:

```
public class MyMessageService extends MappMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if(remoteMessage.getData().containsKey("p")) {
            // handle Mapp push messages
            super.onMessageReceived(remoteMessage);
        }else{
            // handle your own push messages
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
		// subscribe on your own service with firebase token
    }
}
```

<h2>Proguard rules</h2>

When application applies `minifyEnabled` **true** to a `build.gradle`, then the following `proguard-rules` must be applied so that SDK functions properly.

```
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,EnclosingMethod,*Annotation*

-keep public class com.appoxee.** { *; }
-keep class com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.reflect.TypeToken
-keep public class * implements java.lang.reflect.Type
-keep class * implements com.appoxee.internal.network.Networkable { *; }
-keep class * implements com.appoxee.internal.commandstore.Model { *; }
-keep class * implements com.appoxee.internal.network.request.NetworkRequestFactory { *; }
-keep class * implements com.appoxee.internal.badge.Badger { *; }
-keep class com.appoxee.internal.geo.** { *; }
-keep class * extends com.appoxee.internal.command.Command { *; }
```
