package org.dalol.apkdigger.model.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import org.dalol.apkdigger.R;

/**
 * Created by Filippo-TheAppExpert on 7/19/2015.
 */
public class SharedPreferenceUtils {

    public static final String TAG = SharedPreferenceUtils.class.getSimpleName();

    public static void save(Context context, String key, boolean value) {
        if(context == null) {
            return;
        }
        SharedPreferences preferences = context.getSharedPreferences(context.getResources().getString(R.string.account_type),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getValue(Context context, String key) {
        if(context == null) {
            return false;
        }
        SharedPreferences preferences = context.getSharedPreferences(context.getResources().getString(R.string.account_type),
                Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }
}