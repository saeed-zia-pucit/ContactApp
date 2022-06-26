package com.example.contactapp

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contact_item")
data class ContactEntity(

    @ColumnInfo(name = "name") var name: String?,
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "number") var number: String,
    @ColumnInfo(name = "duration") var duration: String?,
    @ColumnInfo(name = "time") var time: String?,
    @ColumnInfo(name = "busId") var busNumber:String?,
    @ColumnInfo(name = "note") var note: String?,
    @ColumnInfo(name = "rate") var rate: Int?,
    @ColumnInfo(name = "callType") var callType: String?)