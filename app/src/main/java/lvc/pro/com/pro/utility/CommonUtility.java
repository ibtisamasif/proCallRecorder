package lvc.pro.com.pro.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import java.lang.reflect.Method;

import lvc.pro.com.pro.constants.Constants;

/**
 * Created by chicmic on 3/8/18.
 */

public class CommonUtility {

    public static int isConnectedToNetwork(Context pContext) {

        final ConnectivityManager connMgr = (ConnectivityManager)
                pContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMgr != null) {
            final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifi.isConnectedOrConnecting()) {
                return Constants.sCONNECTED_TO_WIFI;
            } else if (mobile.isConnectedOrConnecting()) {
                return Constants.sCONNECTED_TO_MOBILE_DATA;
            } else {
                return Constants.sNOT_CONNECTED;
            }
        } else {
            return Constants.sNOT_CONNECTED;
        }

    }

}
