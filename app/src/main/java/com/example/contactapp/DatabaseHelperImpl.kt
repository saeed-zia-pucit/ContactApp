package com.example.contactapp

class DatabaseHelperImpl(private val appDatabase: AppDatabase) : DbHelper {

    override suspend fun getUsers(): List<ContactEntity> = appDatabase.contactDao().getAll()

    override suspend fun insertAll(contacts: List<ContactEntity>) = appDatabase.contactDao().insertAll(contacts)
    override suspend fun insert(contactEntity: ContactEntity) = appDatabase.contactDao().insert(contactEntity)

}