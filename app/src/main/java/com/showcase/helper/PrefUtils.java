package com.showcase.helper;

import android.content.Context;


public class PrefUtils {

    public static String IS_FIRST_TIME = "IS_FIRST_TIME";
    public static String IS_LOCKED = "IS_LOCKED";
    public static String USER_PIN = "USER_PIN";
    public static String IS_PATTERN = "IS_PATTERN";

    public static String OBJ = "OBJ";

    public static void setIsFirstTime(Context context, boolean isFirstTime) {
        Prefs.with(context).save(IS_FIRST_TIME, isFirstTime);
    }

    public static boolean checkFirstTime(Context context) {
        return Prefs.with(context).getBoolean(IS_FIRST_TIME, true);//by Default its true for the first time when application runs
    }

    public static void setLockStatus(Context context, boolean isLoggedIn) {
        Prefs.with(context).save(IS_LOCKED, isLoggedIn);
    }

    public static boolean getLockStatus(Context context) {
        return Prefs.with(context).getBoolean(IS_LOCKED, false);
    }

    public static void setIsPattern(Context context, boolean isPatternLocked) {
        Prefs.with(context).save(IS_PATTERN, isPatternLocked);
    }

    public static boolean isPattern(Context context) {
        return Prefs.with(context).getBoolean(IS_PATTERN, false);
    }

    public static void setUserPassword(Context context, String pin) {
        Prefs.with(context).save(USER_PIN, pin);
    }

    public static String getUserPassword(Context context) {
        return Prefs.with(context).getString(USER_PIN, "");
    }


    /**
     * Demo of storing an object and accessing the same is as below
     */

    /************************************************************************************
     public static void setNextDateListResponse(Context context, NextDateListResponse nextDateListResponse) {
     String text = PSPLApplication.getGson().toJson(nextDateListResponse);
     Prefs.with(context).save(NEXTDATE, text);
     }

     public static NextDateListResponse getNextDateListResponse(Context context) {
     NextDateListResponse nextDateListResponse = new NextDateListResponse();
     String text = Prefs.with(context).getString(NEXTDATE, "");
     if (!TextUtils.isEmpty(text)) {
     nextDateListResponse = PSPLApplication.getGson().fromJson(text, NextDateListResponse.class);
     }
     return nextDateListResponse;
     }

     **************************************************************************************/
}
