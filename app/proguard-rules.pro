# Keep JNI entry points
-keepclasseswithmembernames class * {
    native <methods>;
}
-keep class com.justcode.machinedaw.audio.AudioEngineBridge { *;
}
