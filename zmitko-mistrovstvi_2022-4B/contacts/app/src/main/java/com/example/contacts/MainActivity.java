package com.example.contacts;

import android.Manifest;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
// Vysvetlenie?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
// Asynchrónne zobrazenie vysvetlenia
            } else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},1);

            }
        }
        String atributy[] = new String[] {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
        };
        ContentResolver contentResolver = getContentResolver();
        String where = " (("+ContactsContract.Contacts.DISPLAY_NAME + "
        NOTNULL) AND("+ContactsContract.Contacts.DISPLAY_NAME + " != ''))";
        String sort = ContactsContract.Contacts._ID + "ASC";
        Cursor cursor =
                contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                        atributy, where, null, sort);
        setListAdapter(new KontaktyAdapter(this, R.layout.kontakt, cursor, 0));
    }
}
