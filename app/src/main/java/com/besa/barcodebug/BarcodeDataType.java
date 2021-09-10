package com.besa.barcodebug;

import android.util.SparseArray;

enum BarcodeDataType
{
    CONTACT_INFO(1),
    EMAIL(2),
    ISBN(3),
    PHONE(4),
    PRODUCT(5),
    SMS(6),
    TEXT(7),
    URL(8),
    WIFI(9),
    GEO(10),
    CALENDAR_EVENT(11),
    DRIVER_LICENSE(12)
    ;

    private final int type;
    private static final SparseArray<BarcodeDataType> lookup = new SparseArray<>();

    BarcodeDataType(final int type)
    {
        this.type = type;
    }

    static
    {
        for(BarcodeDataType barcodeFormat : BarcodeDataType.values())
        {
            lookup.put(barcodeFormat.type, barcodeFormat);
        }
    }

    public static BarcodeDataType getType(final Integer type)
    {
        return lookup.get(type);
    }
}