package iageengineering.net.android_device_info;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AsyncTask<Void, Void, AdvertisingIdClient.Info> task = new AsyncTask<Void, Void, AdvertisingIdClient.Info>() {
            @Override
            protected AdvertisingIdClient.Info doInBackground(Void... params) {
                AdvertisingIdClient.Info idInfo = null;
                try {
                    idInfo = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());
                    return idInfo;
                } catch (GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException | IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(AdvertisingIdClient.Info advertisingInfo) {
                String content = getDeviceInfo();
                String advertisingInfoString = "";
                if (advertisingInfo != null) {
                    String advertisingId = advertisingInfo.getId();
                    boolean isLimitTrackingEnabled = advertisingInfo.isLimitAdTrackingEnabled();
                    advertisingInfoString =
                            "ADVERTISING ID : " + advertisingId + "\n IS LIMIT TRACKING : " + isLimitTrackingEnabled;
                }

                TextView deviceInfo = (TextView) findViewById(R.id.deviceInfo);
                deviceInfo.setText(advertisingInfoString + content);
            }

        };
        task.execute();

    }

    private String getDeviceInfo() {
        ScreenSize screenSize = DeviceUtils.getScreenSize(this);
        String info =
                "\nCURRENT TIME : " + System.currentTimeMillis()
                        + "\nANDROID ID : " + DeviceUtils.getAndroidID(this)
                        + "\nCARRIER : " + DeviceUtils.getCarrier(this)
                        + "\nCARRIER Code : " + DeviceUtils.getCarrierCode(this)
                        + "\nCOUNTRY : " + DeviceUtils.getCountry()
                        + "\nTIMEZONE : " + DeviceUtils.getTimeZoneID()
                        + "\nLANGUAGE : " + DeviceUtils.getLanguage()
                        + "\nMAC : " + DeviceUtils.getMACAddress(null)
                        + "\nWIFI MAC : " + DeviceUtils.getWifiMacAddress(this)
                        + "\nBLUETOOTH MAC : " + DeviceUtils.getBluetoothMacAddress(this)
                        + "\nIP V4 : " + DeviceUtils.getIPAddress(true)
                        + "\nIP V6 : " + DeviceUtils.getIPAddress(false)
                        + "\nHAS NETWORK : " + DeviceUtils.isNetworkAvailable(this)
                        + "\nNETWORK TYPE : " + DeviceUtils.getConnectionType(this)
                        + "\nUSERAGENT : " + DeviceUtils.getUserAgent(this)
                        + "\nWIDTH : " + screenSize.getWidth()
                        + "\nHEIGHT : " + screenSize.getHeight()
                        + "\nVERSION.RELEASE : " + Build.VERSION.RELEASE
                        + "\nVERSION.INCREMENTAL : " + Build.VERSION.INCREMENTAL
                        + "\nVERSION.SDK.NUMBER : " + Build.VERSION.SDK_INT
                        + "\nBOARD : " + Build.BOARD
                        + "\nBOOTLOADER : " + Build.BOOTLOADER
                        + "\nBRAND : " + Build.BRAND
                        + "\nDISPLAY : " + Build.DISPLAY
                        + "\nFINGERPRINT : " + Build.FINGERPRINT
                        + "\nHARDWARE : " + Build.HARDWARE
                        + "\nHOST : " + Build.HOST
                        + "\nID : " + Build.ID
                        + "\nMANUFACTURER : " + Build.MANUFACTURER
                        + "\nMODEL : " + Build.MODEL
                        + "\nPRODUCT : " + Build.PRODUCT
                        + "\nSERIAL : " + Build.SERIAL
                        + "\nTAGS : " + Build.TAGS
                        + "\nTIME : " + Build.TIME
                        + "\nTYPE : " + Build.TYPE
                        + "\nUNKNOWN : " + Build.UNKNOWN
                        + "\nIS TABLET : " + DeviceUtils.isTablet(this)
                        + "\nUSER : " + Build.USER;

        return info;
    }
}
