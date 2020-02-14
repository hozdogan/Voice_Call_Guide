package com.example.voice_recog_guide;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.LocaleList;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button btn1;
    TextView txt1,txt2,txt3;
    Button btn2;
    Intent call_intent;
    ArrayList<String> results;
    String query;
    String name,number,result_voice_call;
    ListView list1;
    static int request_code = 10;
    Integer index = 0;
    boolean isMatched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn1 = (Button)findViewById(R.id.button2);
        txt1 = (TextView)findViewById(R.id.textView3);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected())
                {
                    Voice_Recog();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Lüften Internet Bağlantınızı Kontrol Edin",Toast.LENGTH_LONG).show();
                }


            }
        });

    }

    protected boolean isConnected()
    {

        ConnectivityManager conmanager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = conmanager.getActiveNetworkInfo();
        if(netinfo != null && netinfo.isAvailable()&&netinfo.isConnected())
            return true;
        else
            return false;

    }
    protected void Voice_Recog()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        if(intent.resolveActivity(getPackageManager())!= null)
        {
            startActivityForResult(intent,request_code);
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Bu Cihaz Sesli Komut Deskteklemiyor",Toast.LENGTH_LONG).show();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == request_code && resultCode == RESULT_OK)
        {
            results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            txt1.setText(results.get(0));
            Query_Guide();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*protected void Access_Guide()
    {
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        startManagingCursor(cursor);

        String[] from = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone._ID};

        int[] to = {android.R.id.text1,android.R.id.text2};

        SimpleCursorAdapter sca = new SimpleCursorAdapter(this,android.R.layout.simple_list_item_2,cursor,from,to);
        list1.setAdapter(sca);
        list1.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }*/
    protected void Query_Guide()
    {
        isMatched = false;
      Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        startManagingCursor(cursor);
        for(int i=0;i<results.size();i++)
        {
            while(cursor.moveToNext())
            {
                name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                if(results.get(i).compareToIgnoreCase(name)==0)
                {
                    isMatched = true;
                    call_intent = new Intent(Intent.ACTION_CALL);
                    call_intent.setData(Uri.parse("tel:"+cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))));
                    if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED)
                    {
                        return;
                    }
                    startActivity(call_intent);

                }

            }
        }
        if(!isMatched)
            Toast.makeText(getApplicationContext(),"Bu İsimde Bir Kayıt Bulunamadı.",Toast.LENGTH_LONG).show();

    }


}
