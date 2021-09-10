package com.besa.barcodebug;

import android.os.Parcel;
import android.os.Parcelable;

class BarcodePar implements Parcelable
{
    private String type;
    private String format;
    private String displayValue;
    private String rawValue;
    private byte[] rawBytes;

    public BarcodePar()
    {

    }

    public String getType()
    {
        return type;
    }

    public void setType(final String type)
    {
        this.type = type;
    }

    public String getFormat()
    {
        return format;
    }

    public void setFormat(final String format)
    {
        this.format = format;
    }

    public String getDisplayValue()
    {
        return displayValue;
    }

    public void setDisplayValue(final String displayValue)
    {
        this.displayValue = displayValue;
    }

    public String getRawValue()
    {
        return rawValue;
    }

    public void setRawValue(final String rawValue)
    {
        this.rawValue = rawValue;
    }

    public byte[] getRawBytes()
    {
        return rawBytes;
    }

    public void setRawBytes(final byte[] rawBytes)
    {
        this.rawBytes = rawBytes;
    }

    protected BarcodePar(final Parcel in)//Parcelable const
    {
        type = in.readString();
        format = in.readString();
        displayValue = in.readString();
        rawValue = in.readString();
        rawBytes = in.createByteArray();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(type);
        dest.writeString(format);
        dest.writeString(displayValue);
        dest.writeString(rawValue);
        dest.writeByteArray(rawBytes);
    }

    public static final Creator<BarcodePar> CREATOR = new Creator<BarcodePar>()
    {
        @Override
        public BarcodePar createFromParcel(Parcel in)
        {
            return new BarcodePar(in);
        }

        @Override
        public BarcodePar[] newArray(final int size)
        {
            return new BarcodePar[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }
}