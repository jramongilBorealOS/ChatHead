package com.borealos.chathead;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;

public class JWTDecode {

    public static String decodeJWT(String JWTEncoded) throws UnsupportedEncodingException {


            String[] split = JWTEncoded.split("\\.");
            String decodedJWT = "Header: " + getJson(split[0]) + "\n" + "Body: " + getJson(split[1]);
            Log.d("ChatHead", decodedJWT);

            return decodedJWT;

    }

    private static String getJson(String strEncoded) throws UnsupportedEncodingException{
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }

}
