# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#------------- Kotlin Coroutines -------------
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.flow.internal.ChannelFlowStep
-keepclassmembers class kotlinx.coroutines.flow.internal.ChannelFlowStep {
    <init>(...);
}
-keepclassmembers class kotlinx.coroutines.flow.internal.ChannelFlow {
    <init>(...);
}
-keepclassmembers class kotlinx.coroutines.selects.SelectBuilderImpl {
    <init>(...);
}

#------------- Model Classes ---------------
-keep class hymnal.model.** { *; }

#To remove debug logs:
-assumenosideeffects class android.util.Log {
public static *** d(...);
public static *** v(...);
public static *** e(...);
public static *** i(...);
}

#To keep line numbers for crash reports
-renamesourcefileattribute SourceFile
-keepattributes SourceFile, LineNumberTable
