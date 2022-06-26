package com.example.contactapp

interface DbHelper {

    suspend fun getUsers(): List<ContactEntity>

    suspend fun insertAll(users: List<ContactEntity>)
    suspend fun insert(contactEntity: ContactEntity)

}