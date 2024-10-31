package com.colin.android.demo.kotlin.def

import android.os.Parcel
import android.os.Parcelable

data class ItemBean(val id: Int, val title: String) : Parcelable {

    constructor() : this(-1, "")
    constructor(parcel: Parcel) : this(parcel.readInt(), parcel.readString() ?: "")


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ItemBean> {
        override fun createFromParcel(parcel: Parcel): ItemBean {
            return ItemBean(parcel)
        }

        override fun newArray(size: Int): Array<ItemBean?> {
            return arrayOfNulls(size)
        }
    }
}
