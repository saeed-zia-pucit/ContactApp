package com.example.contactapp

import androidx.room.*


@Dao
interface ContactDao {
    @Query("SELECT * FROM contact_item")
    fun getAll(): List<ContactEntity>

    @Insert
    fun insertAll( contacts: List<ContactEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert( contactEntity: ContactEntity)

    @Delete
    fun delete(todo: ContactEntity)

    @Update
    fun updateTodo( todos: ContactEntity)
}