# DiaryVerse ProGuard Rules for Production Security
# Critical: Protect sensitive classes from reverse engineering

# ========== ROOM DATABASE PROTECTION ==========
# Keep all database entities and DAOs
-keep @androidx.room.Entity class * {
    <fields>;
}
-keep @androidx.room.Dao class * {
    <methods>;
}
-keep class * extends androidx.room.RoomDatabase {
    <methods>;
}

# ========== SENSITIVE BUSINESS LOGIC ==========
# Premium features - hide subscription logic
-keep class com.diary.superjournalapp.utils.PremiumFeatureManager {
    <methods>;
}

# Backup encryption - critical security
-keep class com.diary.superjournalapp.backup.BackupEncryption {
    <methods>;
}

# App lock system
-keep class com.diary.superjournalapp.applock.** {
    <methods>;
}

# Journal lock manager
-keep class com.diary.superjournalapp.utils.JournalLockManager {
    <methods>;
}

# ========== API & NETWORKING ==========
# Google Drive API classes
-keep class com.google.api.** { *; }
-keep class com.google.apis.** { *; }

# Gson serialization
-keep class com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.reflect.TypeToken
-keep public class * implements java.lang.reflect.Type

# Keep model classes for JSON serialization
-keep class com.diary.superjournalapp.dto.** { *; }
-keep class com.diary.superjournalapp.entity.** { *; }

# ========== SECURITY MEASURES ==========
# Hide debug information in release
-assumenosideeffects class android.util.Log {
    public static int d(...);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
}

# Remove debugging attributes
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# ========== WEBVIEW & RICH TEXT ==========
# Rich text editor
-keep class jp.wasabeef.richeditor.** { *; }

# ========== EXPORT FUNCTIONALITY ==========
# PDF export library
-keep class com.itextpdf.** { *; }

# ========== CRASH PREVENTION ==========
# Material Design components
-keep class com.google.android.material.** { *; }

# AndroidX libraries
-keep class androidx.** { *; }

# ========== REFLECTION PROTECTION ==========
# Prevent reflection attacks on sensitive classes
-keepclassmembers class com.diary.superjournalapp.backup.** {
    !private <fields>;
    !private <methods>;
}

-keepclassmembers class com.diary.superjournalapp.utils.PremiumFeatureManager {
    !private <fields>;
    !private <methods>;
}

# ========== MISSING CLASSES - IGNORE WARNINGS ==========
# These classes are referenced by libraries but not available on Android
# Safe to ignore as they are desktop/server-only dependencies

# Jackson JSON library (optional for iText PDF)
-dontwarn com.fasterxml.jackson.**

# Java AWT/Desktop classes (not available on Android)
-dontwarn java.awt.**
-dontwarn java.awt.image.**
-dontwarn javax.imageio.**

# JGSS/Kerberos classes (server authentication, not needed on mobile)
-dontwarn org.ietf.jgss.**
-dontwarn javax.naming.**
-dontwarn javax.naming.directory.**
-dontwarn javax.naming.ldap.**

# SLF4J logging (optional dependency)
-dontwarn org.slf4j.**

# Apache HTTP Client optional classes
-dontwarn org.apache.http.conn.ssl.**
-dontwarn org.apache.http.impl.auth.**

# ========== ADDITIONAL MISSING CLASSES ==========
# Add any other missing classes that R8 complains about
-dontwarn javax.xml.**
-dontwarn org.w3c.dom.**
-dontwarn org.apache.commons.**
-dontwarn org.bouncycastle.**