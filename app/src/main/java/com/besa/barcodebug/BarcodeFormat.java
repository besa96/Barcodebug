package com.besa.barcodebug;

import android.util.SparseArray;

enum BarcodeFormat
{
    UNKNOWN(0),
    CODE_128(1),
    CODE_39(2),
    CODE_93(4),
    CODABAR(8),
    DATA_MATRIX(16),
    EAN_13(32),
    EAN_8(64),
    ITF(128),
    QR_CODE(256),
    UPC_A(512),
    UPC_E(1024),
    PDF417(2048),
    AZTEC(4096)
    ;

    private final int format;
    private static final SparseArray<BarcodeFormat> lookup = new SparseArray<>();

    BarcodeFormat(final int format)
    {
        this.format = format;
    }

    static
    {
        for(BarcodeFormat barcodeType : BarcodeFormat.values())
        {
            lookup.put(barcodeType.format, barcodeType);
        }
    }

    public static BarcodeFormat getFormat(final Integer format)
    {
        return lookup.get(format, UNKNOWN);
    }
}