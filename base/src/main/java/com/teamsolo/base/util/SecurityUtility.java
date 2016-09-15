package com.teamsolo.base.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * description: security util
 * author: Melody
 * date: 2016/6/18
 * version: 0.0.0.1
 */
@SuppressWarnings("unused")
public final class SecurityUtility {

    private static final String DEVICE_ID_UUID = "device_id_uuid";

    private SecurityUtility() {
    }

    @NonNull
    public static String MD5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5")
                    .digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }

    @NonNull
    @SuppressLint("HardwareIds")
    public static String getDeviceId(Context context) {
        StringBuilder deviceId = new StringBuilder();
        deviceId.append("android-");

        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();

            if (!TextUtils.isEmpty(imei)) {
                deviceId.append("imei:");
                deviceId.append(imei);
                return deviceId.toString();
            }

            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            String wifiMac = info.getMacAddress();

            if (!TextUtils.isEmpty(wifiMac)) {
                deviceId.append("wifi:");
                deviceId.append(wifiMac);
                return deviceId.toString();
            }

            String sn = tm.getSimSerialNumber();

            if (!TextUtils.isEmpty(sn)) {
                deviceId.append("sn:");
                deviceId.append(sn);
                return deviceId.toString();
            }

            String uuid = getDeviceUUID(context);

            if (!TextUtils.isEmpty(uuid)) {
                deviceId.append("id:");
                deviceId.append(uuid);
                return deviceId.toString();
            }
        } catch (Exception e) {
            deviceId.append("id:").append(getDeviceUUID(context));
        }

        return deviceId.toString();
    }

    private static String getDeviceUUID(Context context) {
        String uuid = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(DEVICE_ID_UUID, "");

        if (TextUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString();
            PreferenceManager.getDefaultSharedPreferences(context)
                    .edit()
                    .putString(DEVICE_ID_UUID, uuid)
                    .apply();
        }

        return uuid;
    }

    public static String decodeString(String source) {
        try {
            return URLDecoder.decode(source, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return source;
        }
    }
}
