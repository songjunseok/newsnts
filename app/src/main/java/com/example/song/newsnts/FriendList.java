package com.example.song.newsnts;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class FriendList extends Activity {
    public static final String TAG = "친구목록";

    ContentResolver resolver;
    Cursor cursor;
    int count;
    int phoneCount;
    ArrayList<Integer> phoneIndexList = new ArrayList<Integer>();
    int recordIndex = 0;

    LinearLayout personLayout;
    ImageView personImage;
    TextView personName;
    TextView personMobile;
    TextView personEmail;

    TextView personCount;

    Animation translateLeftAnim;

    public static boolean running = false;
    public static final int INTERVAL_ANIMATION = 5000;

    Handler handler = new Handler();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_list);

        personLayout = (LinearLayout) findViewById(R.id.personLayout);

        personImage = (ImageView) findViewById(R.id.personImage);
        personName = (TextView) findViewById(R.id.personName);
        personMobile = (TextView) findViewById(R.id.personMobile);
        personEmail = (TextView) findViewById(R.id.personEmail);

        personCount = (TextView) findViewById(R.id.personCount);

        translateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right);
        ContactAnimationListener animListener = new ContactAnimationListener();
        translateLeftAnim.setAnimationListener(animListener);

        queryContacts();

        ContactAnimationThread thread = new ContactAnimationThread();
        thread.start();

    }


    private void queryContacts() {
        resolver = getContentResolver();

        cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        //count = cursor.getCount();

        // find only contacts with phone numbers
        phoneCount = 0;
        phoneIndexList.clear();
        while(cursor.moveToNext()) {
            String hasPhoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            if (hasPhoneNumber.equalsIgnoreCase("1")) {
                int curPosition = cursor.getPosition();
                phoneIndexList.add(curPosition);
                phoneCount++;
            }
        }
        Log.d(TAG, "Count of contacts with phone number : " + phoneCount);

        // display the first contact
        int index = phoneIndexList.get(recordIndex);
        displayContact(index);

        personCount.setText("1" + "/" + phoneCount);
    }


    class ContactAnimationThread extends Thread {
        public void run() {
            running = true;
            while(running) {
                try {
                    Thread.sleep(INTERVAL_ANIMATION);
                } catch(Exception ex) {
                    ex.printStackTrace();
                }

                handler.post(new DisplayContactRunnable());
            }
        }
    }

    class DisplayContactRunnable implements Runnable {
        public void run() {
            personLayout.startAnimation(translateLeftAnim);
        }
    }


    private class ContactAnimationListener implements Animation.AnimationListener {
        public void onAnimationEnd(Animation animation) {
            // display next contact
            recordIndex++;
            if (recordIndex >= phoneIndexList.size()) {
                recordIndex = 0;
            }

            int index = phoneIndexList.get(recordIndex);
            displayContact(index);

            personCount.setText((recordIndex+1) + "/" + phoneCount);
        }

        public void onAnimationRepeat(Animation animation) { }
        public void onAnimationStart(Animation animation) { }
    }

    private void displayContact(int index) {
        if (cursor == null) {
            queryContacts();
        }

        cursor.moveToPosition(index);

        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

        if (name != null) {
            personName.setText(name);
        } else {
            personName.setText("");
        }

        String hasPhoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
        if (hasPhoneNumber.equalsIgnoreCase("1")) {
            hasPhoneNumber = "true";
        } else {
            hasPhoneNumber = "false" ;
        }

        String phoneNumber = null;
        if (Boolean.parseBoolean(hasPhoneNumber)) {
            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
            while (phones.moveToNext()) {
                phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
            phones.close();
        }

        if (phoneNumber != null) {
            personMobile.setText(phoneNumber);
        } else {
            personMobile.setText("");
        }

        Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + id, null, null);
        String email = null;
        while (emails.moveToNext()) {
            email = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
        }
        emails.close();

        if (email != null) {
            personEmail.setText(email);
        } else {
            personEmail.setText("");
        }

        Log.d(TAG, "Person : " + id + ", " + name + ", " + phoneNumber + ", " + email);


        // photo
        long photoId = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;


        Uri dataUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(id));
        Bitmap bitmap = loadPhoto(dataUri, options);
        if (bitmap != null) {
            Log.d(TAG, "Photo image exists.");

            personImage.setImageBitmap(bitmap);
        } else {
            Log.d(TAG, "Photo image is null.");

            personImage.setImageResource(R.drawable.user);
        }

    }

    private Bitmap loadPhoto(Uri selectedUri, BitmapFactory.Options options) {
        Uri contactUri = null;
        if (ContactsContract.Contacts.CONTENT_ITEM_TYPE.equals(getContentResolver().getType(selectedUri))) {
            contactUri = ContactsContract.Contacts.lookupContact(getContentResolver(), selectedUri);
        } else {

            Cursor cursor = getContentResolver().query(selectedUri,
                    new String[] { ContactsContract.Data.CONTACT_ID }, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    final long contactId = cursor.getLong(0);
                    contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
                }
            } finally {
                if (cursor != null) cursor.close();
            }
        }

        Cursor cursor = null;
        Bitmap bm = null;

        try {
            Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
            cursor = getContentResolver().query(photoUri, new String[] {ContactsContract.CommonDataKinds.Photo.PHOTO},
                    null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                bm = loadContactPhoto(cursor, 0, options);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }



        return bm;
    }

    private Bitmap loadContactPhoto(long contactId, BitmapFactory.Options options) {
        Bitmap bm = null;

        try {
            final int colPhoto = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO);
            final byte[] photoBlob = cursor.getBlob(colPhoto);
            if (photoBlob != null) {
                bm = BitmapFactory.decodeByteArray(photoBlob, 0, photoBlob.length);
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return bm;
    }

    public Bitmap loadContactPhoto(Cursor cursor, int bitmapColumnIndex, BitmapFactory.Options options) {
        if (cursor == null) {
            return null;
        }

        byte[] data = cursor.getBlob(bitmapColumnIndex);
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }



    protected void onDestroy() {
        running = false;

        if (cursor != null) {
            cursor.close();
        }

        super.onDestroy();
    }


}
