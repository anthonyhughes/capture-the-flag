package com.anthony.ctf.utilities;

import android.content.Context;
import android.widget.Toast;

public class AndroidHelper {
	
    public static final void toast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

}
