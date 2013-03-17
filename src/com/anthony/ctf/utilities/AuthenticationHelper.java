package com.anthony.ctf.utilities;

import android.util.Base64;

/**
 *
 * @author anthonyhughes
 */
public class AuthenticationHelper {
    
    public AuthenticationHelper(){}
    
    public static String getEncodedBasicAuthenticationString(String details){           
        return "Basic " + Base64.encode(details.getBytes(), Base64.DEFAULT);
    }
    
}
