# 保留使用@AutoService注解的类、方法和字段
-keep,allowobfuscation @interface com.google.auto.service.AutoService
-keep @com.google.auto.service.AutoService class **
-keep @com.google.auto.service.AutoService class ** {
    @com.google.auto.service.AutoService <fields>;
    @com.google.auto.service.AutoService <methods>;
}

-keep,allowobfuscation @interface com.tencent.gamehelper.common.api.annotation.DefaultImpl
-keep @xyz.dean.framework.common.annotation.DefaultImpl class **
-keep @xyz.dean.framework.common.annotation.DefaultImpl class ** {
    @xyz.dean.framework.common.annotation.DefaultImpl <fields>;
    @xyz.dean.framework.common.annotation.DefaultImpl <methods>;
}

-keep,allowobfuscation @interface com.tencent.gamehelper.common.api.annotation.Overridable
-keep @xyz.dean.framework.common.annotation.Overridable class **
-keep @xyz.dean.framework.common.annotation.Overridable class ** {
    @xyz.dean.framework.common.annotation.Overridable <fields>;
    @xyz.dean.framework.common.annotation.Overridable <methods>;
}

-keep class * implements xyz.dean.framework.common.event.IComponentEvent {*;}
-keep interface * extends xyz.dean.framework.common.event.IComponentEvent {*;}
-keep class * implements xyz.dean.framework.common.service.IComponentService {*;}
-keep interface * extends xyz.dean.framework.common.service.IComponentService {*;}
-keep class * implements xyz.dean.framework.common.BaseApp {*;}
-keep class * implements xyz.dean.framework.common.BaseLifecycleApp {*;}