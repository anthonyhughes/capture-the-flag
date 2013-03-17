package com.anthony.ctf.utilities;

import java.io.InputStream;
import java.util.Scanner;

public class StringHelper {
    public StringHelper(){}
    
    public static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
