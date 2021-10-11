
package com.example.pharmago.util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;


/**
 * BatteryUtility -
 *
 * @author Ceylon Linux (pvt) Ltd
 */
public class BatteryUtility {

    public static int getBatteryLevel(Context context) {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, intentFilter);

        if (batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) == 0) {
            return 100;
        }

        return batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
    }
}
