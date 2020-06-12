package com.stoken.stoken.utils;

import android.content.Context;
import android.widget.Toast;

public class MyClasses {

    // no internet function
    public static void noInternetDialog(final Context ctx) {
        Toast.makeText(ctx, "No internet connection!", Toast.LENGTH_SHORT).show();
    }

}
