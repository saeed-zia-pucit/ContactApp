package com.example.contactapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Long
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HomeActivity : AppCompatActivity(),ClickListener{
    val PERMISSIONS_REQUEST_READ_CONTACTS = 2
    var recyclerView: RecyclerView? = null
    var adapter: ContactAdapter? = null
    var dbList = ArrayList<ContactEntity>()
    var logList = ArrayList<ContactEntity>()
    var finalList = ArrayList<ContactEntity>()
    val REQUEST_READ_CONTACTS = 79
    var contactList = ArrayList<ContactEntity>()
    var list: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setRecyclerView()
       // loadContacts()
        getContactsFromDb()

        setListeners()

    }
    private fun showCustomDialog(index: Int) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.user_input_dialog)
        val phone = dialog.findViewById(R.id.et_phone) as EditText
        val name = dialog.findViewById(R.id.et_name) as EditText
        val busNumber = dialog.findViewById(R.id.et_bus) as EditText
        val rate = dialog.findViewById(R.id.et_rate) as EditText
        val note = dialog.findViewById(R.id.et_note) as EditText


        phone.setText(finalList.get(index).number)
        name.setText(finalList.get(index).name)
        busNumber.setText(finalList.get(index).busNumber)
        rate.setText(finalList.get(index).rate.toString())
        note.setText(finalList.get(index).note)
        val yesBtn = dialog.findViewById(R.id.btn_save) as Button
        val noBtn = dialog.findViewById(R.id.btn_cancel) as Button
        val temp= finalList.get(index)
        yesBtn.setOnClickListener {

            finalList.set(index,ContactEntity(name.text.toString(),phone.text.toString(),temp.duration,temp.time,busNumber.text.toString(),note.text.toString(),Integer.parseInt(rate.text.toString()),temp.callType))
            saveContactsToDb(ContactEntity(name.text.toString(),phone.text.toString(),temp.duration,temp.time,busNumber.text.toString(),note.text.toString(),Integer.parseInt(rate.text.toString()),temp.callType))
            adapter?.notifyDataSetChanged()

            dialog.dismiss()
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()

    }
    fun populateRecyclerView() {
        //requestContactPermission()
        var counter = 0
        finalList.clear()
        var dbContact: ContactEntity? = null
        if (logList != null && logList.size != 0) {
            for (contact: ContactEntity in logList) {
                var duplicate = finalList.stream()
                    .filter { c -> contact.number.equals(c.number) }
                    .findAny()
                    .orElse(null)
                if (duplicate == null){

                    if (dbList != null && dbList.size != 0) {

                        dbContact = dbList.stream()
                            .filter { c -> contact.number.equals(c.number) }
                            .findAny()
                            .orElse(null)
                    }
                if (dbContact == null) {
                    finalList.add(contact)
                } else {
                    dbContact.time = contact.time
                    finalList.add(dbContact)
                }
                counter = counter + 1
                if (counter == 40) {
                    break;
                }
            }
        }
            //saveContactsToDb()
            runOnUiThread {
                adapter?.notifyDataSetChanged()

            }
        }
    }

    fun getContactsFromDb() {

        val dbHelper = DatabaseHelperImpl(DatabaseBuilder.getInstance(applicationContext))

        GlobalScope.launch {
           // dbHelper.insertAll(list)
            dbList = dbHelper.getUsers() as ArrayList<ContactEntity>
            requestContactPermission()

        }

    }
    fun saveContactsToDb(contactEntity: ContactEntity) {

        val dbHelper = DatabaseHelperImpl(DatabaseBuilder.getInstance(applicationContext))

        GlobalScope.launch {
            dbHelper.insert(contactEntity)

        }

    }

    fun setRecyclerView() {

        adapter = ContactAdapter(finalList,this)
        recyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView

        recyclerView!!.setAdapter(adapter)
        recyclerView!!.setLayoutManager(LinearLayoutManager(this))
    }

    fun requestContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED)) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.READ_CONTACTS)) {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Read Contacts permission")
                    builder.setPositiveButton(android.R.string.ok, null)
                    builder.setMessage("Please enable access to contacts.")
                    builder.setOnDismissListener {
                        requestPermissions(
                            arrayOf(
                                Manifest.permission.READ_CONTACTS,
                                Manifest.permission.READ_CALL_LOG),
                            MainActivity.PERMISSIONS_REQUEST_READ_CONTACTS)
                    }
                    builder.show()
                } else {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.READ_CONTACTS,Manifest.permission.READ_CALL_LOG),
                        MainActivity.PERMISSIONS_REQUEST_READ_CONTACTS)
                }
            } else {
                getContacts()
            }
        } else {
            getContacts()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MainActivity.PERMISSIONS_REQUEST_READ_CONTACTS -> {
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                {
                    getContacts()
                } else {
                    Toast.makeText(this,"You have disabled a contacts permission",Toast.LENGTH_LONG).show()
                }
                return
            }
            PERMISSIONS_REQUEST_READ_CONTACTS ->{
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadContacts()
                } else {
                    //  toast("Permission must be granted in order to display contacts information")
                }
            }
        }
    }
    @SuppressLint("Range")
    private fun getContactsFromContactList(): StringBuilder {
        val builder = StringBuilder()
        val resolver: ContentResolver = contentResolver;
        val cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null,
            null)

        if (cursor != null) {
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                    val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val phoneNumber = (cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))).toInt()

                    if (phoneNumber > 0) {
                        val cursorPhone = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", arrayOf(id), null)

                        if (cursorPhone != null) {
                            if(cursorPhone.count > 0) {
                                while (cursorPhone.moveToNext()) {
                                    val phoneNumValue = cursorPhone.getString(
                                        cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                                    builder.append("Contact: ").append(name).append(", Phone Number: ").append(phoneNumValue).append("\n\n")
                                    contactList.add(ContactEntity(name,phoneNumValue,"","","","",0,""))
                                    Log.e("Name ===>",phoneNumValue);
                                }
                            }
                        }
                        if (cursorPhone != null) {
                            cursorPhone.close()
                        }
                    }
                }
            } else {
                //   toast("No contacts available!")
            }
        }
        if (cursor != null) {
            cursor.close()
        }
        return builder
    }
    private fun loadContacts() {
        var builder = StringBuilder()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS),
                PERMISSIONS_REQUEST_READ_CONTACTS)
            //callback onRequestPermissionsResult
        } else {
           getContactsFromContactList()
        }
    }
    private fun getContacts(): String? {
        val stringBuffer = StringBuffer()
        val cursor = this.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null, null, null, CallLog.Calls.DATE + " DESC")
        val name = cursor!!.getColumnIndex(CallLog.Calls.CACHED_NAME)
        val number = cursor!!.getColumnIndex(CallLog.Calls.NUMBER)
        val type = cursor.getColumnIndex(CallLog.Calls.TYPE)
        val date = cursor.getColumnIndex(CallLog.Calls.DATE)
        val duration = cursor.getColumnIndex(CallLog.Calls.DURATION)
        while (cursor.moveToNext()) {
            val phNumber = cursor.getString(number)
            val callType = cursor.getString(type)
            val callDate = cursor.getString(date)
            val callDayTime = Date(Long.valueOf(callDate))
            val callDuration = cursor.getString(duration)
            val userName = cursor.getString(name)
            var dir: String? = null
            val dircode = callType.toInt()
            when (dircode) {
                CallLog.Calls.OUTGOING_TYPE -> dir = "OUTGOING"
                CallLog.Calls.INCOMING_TYPE -> dir = "INCOMING"
                CallLog.Calls.MISSED_TYPE -> dir = "MISSED"
            }

            val sdf = SimpleDateFormat("hh:mm:ss aa")
            var k = sdf.format(callDayTime.time)
            logList.add(ContactEntity(userName, phNumber, callDuration,k, "", "",0, dir))
        }

        cursor.close()
        populateRecyclerView()
        return stringBuffer.toString()
    }

    override fun onCall(index: Int) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:0123456789")
        startActivity(intent)
    }

    override fun onMsg(index: Int) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("smsto:" + Uri.encode(finalList.get(index).number))
        startActivity(intent)
    }

    override fun onItemClick(index: Int) {
        showCustomDialog(index)
    }
    fun setListeners(){
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
        val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object :
            ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.DOWN or ItemTouchHelper.UP
            ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                Toast.makeText(applicationContext, "Deleted", Toast.LENGTH_SHORT).show()
                //Remove swiped item from list and notify the RecyclerView
                val position = viewHolder.adapterPosition
                finalList.removeAt(position)
                adapter!!.notifyDataSetChanged()
            }
        }

}