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
-verbose                                                                        # 混淆时记录日志
#-dontshrink                                                                     # 关闭压缩
-dontpreverify                                                                  # 关闭预校验(作用于Java平台，Android不需要，去掉可加快混淆)
#-dontoptimize                                                                   # 关闭代码优化
#-dontobfuscate                                                                  # 关闭混淆
-ignorewarnings                                                                 # 忽略警告
-dontusemixedcaseclassnames                                                     # 混淆后类型都为小写
-dontskipnonpubliclibraryclasses                                                # 不跳过非公共的库的类
-printmapping mapping.txt                                                       # 生成原类名与混淆后类名的映射文件mapping.txt
-useuniqueclassmembernames                                                      # 把混淆类中的方法名也混淆
-allowaccessmodification                                                        # 优化时允许访问并修改有修饰符的类及类的成员
-renamesourcefileattribute SourceFile                                           # 将源码中有意义的类名转换成SourceFile，用于混淆具体崩溃代码
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*        # 指定混淆时采用的算法
-keepattributes Signature                                                       # 保留泛型
-keepattributes SourceFile,LineNumberTable                                      # 抛出异常时保留代码行号，在异常分析中可以方便定位


# 指定外部模糊字典
-obfuscationdictionary ./dictionary
# 指定class模糊字典
-classobfuscationdictionary ./dictionary
# 指定package模糊字典
-packageobfuscationdictionary ./dictionary


# 保留R下面的资源
-keep class **.R$* {*;}

# 数据类
-keep class **.data.**{*;}

-keep class com.slot.twostepverification.help.store.**{*;}

# hutool-core hutool-crypto
-keep class cn.hutool.core.**{*;}
-keep class cn.hutool.crypto.**{*;}
-keep class org.bouncycastle.**{*;}
-dontwarn cn.hutool.**

-keep class android.support.**{*;}

# 保留本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers class * {
    public <init> (org.json.JSONObject);
}

# 枚举类
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保留Parcelable序列化类不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# 保留Serializable序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 移除Log类打印各个等级日志的代码，打正式包的时候可以做为禁log使用，这里可以作为禁止log打印的功能使用
# 记得proguard-android.txt中一定不要加-dontoptimize才起作用
# 另外的一种实现方案是通过BuildConfig.DEBUG的变量来控制
#-assumenosideeffects class android.util.Log {
#    public static int v(...);
#    public static int i(...);
#    public static int w(...);
#    public static int d(...);
#    public static int e(...);
#}

##----------------------------------------- 第三方依赖库 --------------------------------------------
#----------------------------- gson ---------------------------------
-keep,allowobfuscation,allowshrinking class com.google.gson.** { *; }
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

#----------------------------- okhttp ---------------------------------
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}



#---------------------------- retrofit --------------------------------
# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Keep annotation default values (e.g., retrofit2.http.Field.encoded).
-keepattributes AnnotationDefault

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# Keep inherited services.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface * extends <1>

# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation



-keep class javax.swing.**{*;}
-dontwarn javax.swing.**
-keep class java.awt.**{*;}
-dontwarn java.awt.**
-keep class sun.misc.**{*;}
-dontwarn sun.misc.**
-keep class org.slf4j.**{*;}
-dontwarn org.slf4j.**
-keep class sun.reflect.**{*;}
-dontwarn sun.reflect.**

##JSOUP
-keep class org.jsoup.**{*;}
-keep class **.xpath.**{*;}

# Android support library annotations will get converted to androidx ones
# which we want to keep.
-keep @interface androidx.annotation.Keep
-keep @androidx.annotation.Keep class *

# error  Library class android.content.res.XmlResourceParser implements program class org.xmlpull.v1.XmlPullParser
-dontwarn org.xmlpull.v1.**
-dontwarn org.kxml2.io.**
-dontwarn android.content.res.**
-dontwarn org.slf4j.impl.StaticLoggerBinder

-keep class org.xmlpull.** { *; }
-keepclassmembers class org.xmlpull.** { *; }

# protobuf
-keep class * extends com.google.protobuf.GeneratedMessageLite { *; }

# coroutines
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlin.coroutines.SafeContinuation {
    volatile <fields>;
}