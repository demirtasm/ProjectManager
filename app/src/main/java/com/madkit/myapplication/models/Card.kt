package com.madkit.myapplication.models

import android.os.Parcel
import android.os.Parcelable

data class Card(
    val name: String = "",
    val createdBy : String = "",
    val assignedTo: ArrayList<String> = ArrayList()
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
       parcel.createStringArrayList()!!
    ) {
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(name)
        writeString(createdBy)
        writeStringList(assignedTo)
    }

    companion object CREATOR : Parcelable.Creator<Card> {
        override fun createFromParcel(parcel: Parcel): Card {
            return Card(parcel)
        }

        override fun newArray(size: Int): Array<Card?> {
            return arrayOfNulls(size)
        }
    }
}