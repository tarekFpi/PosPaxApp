# Enhancing code obfuscation and optimization
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose

# Aggressive optimizations
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

# Obfuscate class, method, and field names
-obfuscationdictionary obfuscation-dict.txt
-classobfuscationdictionary class-obfuscation-dict.txt
-packageobfuscationdictionary package-obfuscation-dict.txt

# Keep application classes and essential Android components
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# Keep methods and fields accessed by reflection
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Keep annotation attributes
-keepattributes *Annotation*

# Keep attributes for debugging and tracing
-keepattributes SourceFile,LineNumberTable

# Keep necessary class members and methods
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep Parcelable classes
-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

# Keep serialized classes
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep Gson classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }
-keep class com.google.gson.internal.** { *; }
-keep class com.google.gson.reflect.** { *; }

# Keep Retrofit classes
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses

# Keep OkHttp classes
-dontwarn okhttp3.**
-dontwarn javax.annotation.**
-keep class okhttp3.** { *; }
-keep class okhttp3.internal.** { *; }
-dontwarn okio.**
-keep class okio.** { *; }

# Keep Room classes
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase {
    *;
}
-keep class * extends androidx.room.RoomDatabase_Impl {
    *;
}

# Keep Firebase classes
-keep class com.google.firebase.** { *; }
-keepnames class com.google.firebase.** { *; }
-keepclassmembers class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Keep methods annotated with @Keep
-keep class * {
    @androidx.annotation.Keep *;
}

# Keep Timber classes
-assumenosideeffects class timber.log.Timber {
    public static void d(...);
    public static void d(java.lang.String, java.lang.Object...);
    public static void d(java.lang.Throwable, java.lang.String, java.lang.Object...);
    public static void i(...);
    public static void i(java.lang.String, java.lang.Object...);
    public static void i(java.lang.Throwable, java.lang.String, java.lang.Object...);
    public static void w(...);
    public static void w(java.lang.String, java.lang.Object...);
    public static void w(java.lang.Throwable, java.lang.String, java.lang.Object...);
    public static void e(...);
    public static void e(java.lang.String, java.lang.Object...);
    public static void e(java.lang.Throwable, java.lang.String, java.lang.Object...);
}

# General configuration
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context);
}

-keepclasseswithmembers class * {
    public <init>();
}

# General Android classes
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Support library classes
-keepclassmembers class * extends android.support.v4.app.Fragment {
    public void setUserVisibleHint(boolean);
    public android.support.v4.app.FragmentManager getFragmentManager();
}

# Keep all test classes
-keep class * extends junit.framework.TestCase { *; }
-keep class org.junit.** { *; }
-dontwarn org.junit.**

# Android architecture components
-keep class android.arch.lifecycle.** { *; }
-dontwarn android.arch.lifecycle.**

# Kotlin classes
-keep class kotlin.** { *; }
-keepclassmembers class kotlin.** { *; }
-keepclassmembers class kotlin.Metadata {
    public <fields>;
}