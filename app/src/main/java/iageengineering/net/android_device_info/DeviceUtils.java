package iageengineering.net.android_device_info;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class DeviceUtils {
    private static String userAgent = null;

    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null) return "";
                StringBuilder buf = new StringBuilder();
                for (byte aMac : mac) {
                    buf.append(String.format("%02X:", aMac));
                }
                if (buf.length() > 0) buf.deleteCharAt(buf.length() - 1);
                return buf.toString();
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        } // for now eat exceptions
        return "";
    }

    public static String getLanguage() {
        return Locale.getDefault().getLanguage();
    }

    public static String getCountry() {
        return Locale.getDefault().getCountry();
    }

    public static String getTimeZoneID() {
        TimeZone tz = TimeZone.getDefault();
        return (tz.getID());
    }

    public static String getAndroidID(final Context context) {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static boolean isNetworkAvailable(Context ctx) {
        int networkStatePermission = ctx.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE);

        if (networkStatePermission == PackageManager.PERMISSION_GRANTED) {

            ConnectivityManager mConnectivity = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

            // Skip if no connection, or background data disabled
            NetworkInfo info = mConnectivity.getActiveNetworkInfo();
            if (info == null) {
                return false;
            }
            // Only update if WiFi
            int netType = info.getType();
            // int netSubtype = info.getSubtype();
            return ((netType == ConnectivityManager.TYPE_WIFI) || (netType == ConnectivityManager.TYPE_MOBILE)) && info.isConnected();
        } else {
            return true;
        }
    }

    public static String getConnectionType(Context context) {
        int networkStatePermission = context.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE);

        if (networkStatePermission == PackageManager.PERMISSION_GRANTED) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info == null) {
                return ConnectionType.UNKNOWN;
            }
            int netType = info.getType();
            int netSubtype = info.getSubtype();
            if (netType == ConnectivityManager.TYPE_WIFI) {
                return ConnectionType.WIFI;
            } else if (netType == 6) {
                return ConnectionType.WIMAX;
            } else if (netType == ConnectivityManager.TYPE_MOBILE) {
                switch (netSubtype) {
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                        return ConnectionType.MOBILE_1xRTT;
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                        return ConnectionType.MOBILE_CDMA;
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        return ConnectionType.MOBILE_EDGE;
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        return ConnectionType.MOBILE_EVDO_0;
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        return ConnectionType.MOBILE_EVDO_A;
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                        return ConnectionType.MOBILE_GPRS;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                        return ConnectionType.MOBILE_UMTS;
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                        return ConnectionType.MOBILE_EHRPD;
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        return ConnectionType.MOBILE_EVDO_B;
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                        return ConnectionType.MOBILE_HSDPA;
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                        return ConnectionType.MOBILE_HSPA;
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        return ConnectionType.MOBILE_HSPAP;
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                        return ConnectionType.MOBILE_HSUPA;
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        return ConnectionType.MOBILE_IDEN;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return ConnectionType.MOBILE_LTE;
                    default:
                        return ConnectionType.MOBILE_UNKNOWN;
                }
            } else {
                return ConnectionType.UNKNOWN;
            }
        } else {
            return ConnectionType.UNKNOWN;
        }
    }

    public static String getCarrier(final Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getNetworkOperatorName();
    }

    public static String getCarrierCode(final Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getNetworkOperator();
    }


    public static String getUserAgent(Context context) {
        if (userAgent == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                userAgent = WebSettings.getDefaultUserAgent(context);
            } else {
                userAgent = new WebView(context).getSettings().getUserAgentString();
            }
        }
        return userAgent;
    }

    public static String getWifiMacAddress(final Context context) {

        int wifiStatePermission = context.checkCallingOrSelfPermission(Manifest.permission.ACCESS_WIFI_STATE);
        if (wifiStatePermission == PackageManager.PERMISSION_GRANTED) {
            WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = manager.getConnectionInfo();
            String macAddress = info.getMacAddress();
            if (macAddress == null) {
                macAddress = "NO MAC Address";
            }
            return macAddress;
        } else
            return null;
    }

    public static String getBluetoothMacAddress(Context context) {
        int bluetoothStatePermission = context.checkCallingOrSelfPermission(Manifest.permission.BLUETOOTH);
        if (bluetoothStatePermission == PackageManager.PERMISSION_GRANTED) {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                return "NO Bluetooth Address";
            }
            return mBluetoothAdapter.getAddress();
        } else return null;
    }

    public static ScreenSize getScreenSize(final Context context) {
        int width = 0;
        int height = 0;
        if (context instanceof Activity) {
            Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                DisplayMetrics realMetrics = new DisplayMetrics();
                display.getRealMetrics(realMetrics);
                width = realMetrics.widthPixels;
                height = realMetrics.heightPixels;

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                try {
                    Method mGetRawH = Display.class.getMethod("getRawHeight");
                    Method mGetRawW = Display.class.getMethod("getRawWidth");
                    width = (Integer) mGetRawW.invoke(display);
                    height = (Integer) mGetRawH.invoke(display);
                } catch (Exception e) {
                    Point point = getDisplayPoint(display);
                    height = point.y;
                    width = point.x;
                }

            } else {
                Point point = getDisplayPoint(display);
                height = point.y;
                width = point.x;
            }
        }
        return new ScreenSize(width, height);
    }

    private static Point getDisplayPoint(Display display) {
        Point point = new Point();
        display.getSize(point);
        return point;
    }
}