package from.bhaskar.mycontacts

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    private val cols = listOf(
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER,
        ContactsContract.CommonDataKinds.Phone._ID).toTypedArray()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS) !=
            PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, Array(1){Manifest.permission.READ_CONTACTS},
                111)

        }
        else {

            readContacts()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            readContacts()
        }
    }

    private fun readContacts() {

        ActivityCompat.requestPermissions(this,Array(1){Manifest.permission.CALL_PHONE},
            11)

        val fromArray = listOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER).toTypedArray()

        val toArray = intArrayOf(android.R.id.text1,android.R.id.text2)

        val rs = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            cols,null,null,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)

        val cursorAdapter = SimpleCursorAdapter(
            this,android.R.layout.simple_list_item_2,
            rs,
            fromArray,
            toArray,
            0
        )

        val listView = findViewById<ListView>(R.id.contactsList)
        val serachView = findViewById<SearchView>(R.id.contactsSearchView)
        listView.adapter = cursorAdapter
        serachView.queryHint = "Total Contacts".plus(listView.count)

        serachView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean  = false

            override fun onQueryTextChange(p0: String?): Boolean {
                val rs = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    cols,
                    "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?" ,
                    Array(1){"%$p0%"},
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                cursorAdapter.changeCursor(rs)
                return false
            }
        })

        listView.setOnItemClickListener { adapterView, view, i, l ->
            val num = view.findViewById<TextView>(android.R.id.text2).text
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$num"))
            startActivity(intent)
        }


    }
}