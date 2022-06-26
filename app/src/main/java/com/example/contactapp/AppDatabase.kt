package com.example.contactapp

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ContactEntity::class], version = 3)
abstract class AppDatabase : RoomDatabase() {

    abstract fun contactDao(): ContactDao

}