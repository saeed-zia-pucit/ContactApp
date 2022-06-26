package com.example.contactapp

import android.content.Context
import androidx.room.Room
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException

class DatabaseHelper {
    private lateinit var todoDao: ContactDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb(context: Context) {

        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()
        //todoDao = db.ContactDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() {
        //val todo: ContactEntity = ContactEntity(1,"title", "detail")
       // todoDao.insertAll(todo)

    }
}