package com.kvest.wearabletest.ui.acticity;

import android.os.Bundle;
import android.os.Parcel;

/**
 * Created by roman on 12/25/15.
 */
public class Utils {
    public static byte[] bundle2Bytes(Bundle bundle) {
        Parcel parcel = Parcel.obtain();
        try {
            parcel.writeBundle(bundle);
            return parcel.marshall();
        } finally {
            parcel.recycle();
        }
    }

    public static Bundle bytes2Bundle(byte[] bytes) {
        Parcel parcel = Parcel.obtain();
        try {
            parcel.unmarshall(bytes, 0, bytes.length);
            parcel.setDataPosition(0);
            return parcel.readBundle();
        } finally {
            parcel.recycle();
        }
    }
}
