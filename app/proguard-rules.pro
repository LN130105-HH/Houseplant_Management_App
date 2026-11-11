# ProGuard rules for PlantCare
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
-dontwarn com.github.mikephil.charting.**
-keep class com.github.mikephil.charting.** { *; }
