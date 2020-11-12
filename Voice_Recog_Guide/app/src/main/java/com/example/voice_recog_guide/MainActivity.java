package com.example.voice_recog_guide;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.Image;
import android.os.Build;
import android.os.Looper;
import android.speech.tts.TextToSpeech;

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
import android.speech.tts.Voice;
import android.telecom.Call;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextToSpeech tts1;
    Button btn1, emergency_button;
    TextView txt1, txt2, txt3;
    ImageButton call_button;
    Intent call_intent;
    ImageButton upbutton;
    ImageButton downbutton;
    ArrayList<String> results;
    String query;
    String contact_name;
    String name, number, result_voice_call;
    ListView list1;
    static int request_code = 10;
    Integer index = 0;
    Integer contactindex = 0;
    boolean isMatched;
    public Cursor cursor1, cursor2;
    public TelephonyManager tM;
    //adress instance
    Geocoder geocoder;
    List<Address> addresses;
    Criteria criteria;
    double current_enlem,current_boylam;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn1 = (Button) findViewById(R.id.button2);
        txt1 = (TextView) findViewById(R.id.textView3);
        upbutton = (ImageButton) findViewById(R.id.imageButton3);
        downbutton = (ImageButton) findViewById(R.id.imageButton4);
        cursor1 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        cursor2 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, "starred = ?", new String[]{"1"}, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        call_button = (ImageButton) findViewById(R.id.imageButton5);
        emergency_button = (Button) findViewById(R.id.button);

// String preprocess = "Fide Nuran'ı ara";
////        String preprocess = "Sebahattin'i ara";
//       String [] arr =  preprocess.split(" ");
//        for(int j=0;j<arr.length;j++)
//        {
//            Log.d("parcalar",arr[j] + " "+j+".parca" +"\n");
//      }
//       if(arr[arr.length - 1].compareToIgnoreCase("ara") == 0)
//       {
//            Log.d("durum","durum doğru");
//            if(preprocess.split("'")[0] == "")
//       {
//           contact_name = preprocess.substring(0,preprocess.length() - 5);
//       }
//           else
//            {
//                contact_name = preprocess.split("'")[0];
//            }
//            Log.d("kayıtlı isim",contact_name);
//        }
//        else
//           contact_name = "hasancan";

        //Phone State biri ararken uygulamaya girersek çalıyor diyor
        TelephonyManager tM =
                (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        final PhoneStateListener callStateListener = new PhoneStateListener() {
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    Toast.makeText(getApplicationContext(), "Phone Is Riging",
                            Toast.LENGTH_LONG).show();
                }
                if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    Toast.makeText(getApplicationContext(), "Phone is Currently in A call",
                            Toast.LENGTH_LONG).show();
                }

                if (state == TelephonyManager.CALL_STATE_IDLE) {
                    Toast.makeText(getApplicationContext(), "phone is neither ringing nor in a call",
                            Toast.LENGTH_LONG).show();

                }
            }
        };

        tM.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        //telephonyManager.listen(callStateListener,PhoneStateListener.LISTEN_CALL_STATE);
        // Location
//        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
//        Criteria criteria = new Criteria();
//        String provider = service.getBestProvider(criteria, false);
//        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    Activity#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for Activity#requestPermissions for more details.
//            return;
//        }
//        Location location = service.getLastKnownLocation(provider);
//        LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());

        tts1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                Locale locale = new Locale("tr", "TR");
                if(status != TextToSpeech.ERROR) {
                    tts1.setLanguage(locale);
                }
            }
        });
        btn1.setOnClickListener(new View.OnClickListener() {//burası ayrı bir sınıf olusturur her tıklandıgında yeniden baslatmaz

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


        upbutton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v)
            {
                if(cursor1.getPosition() == cursor1.getCount() - 1)
                {
                    Toast.makeText(getApplicationContext(),"Rehberin Sonundasınız",Toast.LENGTH_SHORT).show();
                    SpeakContact("Lütfen Aşağı Tuşuna Basın");
                }
                else
                {
                    Integer cont = cursor1.getCount();
                    Log.d("count",cont.toString());
                    cursor1.moveToNext();
                    contact_name = cursor1.getString(cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    number = cursor1.getString(cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    contactindex = cursor1.getPosition();
                    txt1.setText("İndex = "+contactindex +"\n"+ " Ad = "+contact_name+"\n"+" Numara = "+number);
                    SpeakContact(contact_name);
                }

            }
        });
        downbutton.setOnClickListener(new View.OnClickListener() {
            //Integer info = cursor1.getPosition();
            @Override
            public void onClick(View v)
            {
                //Log.d("konum",info.toString());
                if(cursor1.getPosition() == 0 || cursor1.getPosition() == -1)
                {
                    Toast.makeText(getApplicationContext(),"Rehberin Başındasınız",Toast.LENGTH_SHORT).show();
                    SpeakContact("Lütfen Yukarı Tuşuna Basın");
                }
                else
                {
                    cursor1.moveToPosition(cursor1.getPosition() - 1);
                    contact_name = cursor1.getString(cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    number = cursor1.getString(cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    contactindex = cursor1.getPosition();
                    txt1.setText("İndex = "+contactindex +"\n"+ " Ad = "+contact_name+"\n"+" Numara = "+number);
                    SpeakContact(contact_name);

                }


            }
        });
        call_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d("test",txt1.getText().toString());
                if(txt1.getText().toString().compareToIgnoreCase("TextView") == 0)
                {
                    Toast.makeText(getApplicationContext(),"Lütfen Aramak İstediğiniz Kişiyi Seçin",Toast.LENGTH_LONG).show();
                    SpeakContact("Lütfen Aramak İstediğiniz Kişiyi Seçin");
                }
                else
                {
                    call_intent = new Intent(Intent.ACTION_CALL);
                    call_intent.setData(Uri.parse("tel:"+cursor1.getString(cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))));
                    if(ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED)
                    {
                        Toast.makeText(getApplicationContext(),"Lutfen Bu Hizmeti Kullanmak İçin Telefon Çağrı Servisine İzin Verin",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    startActivity(call_intent);

                }


            }

        });
        //acil_durum buttonu
        emergency_button.setOnClickListener(new View.OnClickListener() {
            public int status;
            String Emergency_Message;
            SmsManager sms;
            TelephonyManager tM =
                    (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            final PhoneStateListener callStateListener = new PhoneStateListener() {
                public void onCallStateChanged(int state, String incomingNumber)
                {

                    if(state==TelephonyManager.CALL_STATE_OFFHOOK){
                        Log.d("Status","Calıyor");
                    }
                    else  if(state==TelephonyManager.CALL_STATE_IDLE){

                        Toast.makeText(getApplicationContext(),"Acil Durum Kişileri Aranıyor",Toast.LENGTH_SHORT).show();
                        status = TelephonyManager.CALL_STATE_IDLE;

                    }
                }
            };


            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View v)
            {
                //guzel favorileri arayacak sırayla
               //unreach oluyor while true olunca dinlemiyor
                if(cursor2.getCount() == 0)
                {
                    Toast.makeText(getApplicationContext(),"Acil Durum Listesi Boş",Toast.LENGTH_SHORT).show();
                }
                for(int i=0;i<cursor2.getCount();i++)
                {
                    cursor2.moveToNext();
                    call_intent = new Intent(Intent.ACTION_CALL);
                    call_intent.setData(Uri.parse("tel:"+cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))));
                    if(ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED)
                    {
                        Toast.makeText(getApplicationContext(),"Lutfen Bu Hizmeti Kullanmak İçin Telefon Çağrı Servisine İzin Verin",Toast.LENGTH_SHORT).show();
                        return;
                    }
                   startActivity(call_intent);
                    status = TelephonyManager.CALL_STATE_OFFHOOK;
                    for(int j = 0;j<10;j++)
                    {
                        try
                        {
                            Thread.sleep(6000);
                        }catch (Exception e){e.printStackTrace();}
                        tM.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
                        if(status == TelephonyManager.CALL_STATE_IDLE)
                            break;
                    }
                    //15 sn bekle eğer dusmemisse veya reddedilmisse telefon
                }
                Log.d("durum","Kimse Telefona Bakmadı");
                //ambulansı ara .d Konum bilgili bir SMS Oluştur
//                LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
//                criteria = new Criteria();
//                String provider = service.getBestProvider(criteria, false);
//                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(getApplicationContext(),"Lütfen Bu Uygulama İçin Konum Servislerine İzin Verin",Toast.LENGTH_LONG).show();
//                    return;
//                }
//                if(!service.isProviderEnabled(provider))
//                {
//                    Toast.makeText(getApplicationContext(),"Lütfen Konum Bulmayı Açın",Toast.LENGTH_LONG).show();
//                    return;
//                }
//                Location location = service.getLastKnownLocation(provider);
//                LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());
//                current_boylam = userLocation.longitude;
//                current_enlem = userLocation.latitude;
//                geocoder = new Geocoder(v.getContext(), Locale.getDefault());
//                try
//                {
//                    addresses = geocoder.getFromLocation(current_enlem, current_boylam, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//                }
//                catch(Exception e)
//                {
//                    e.printStackTrace();
//                }
//
//                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//                String city = addresses.get(0).getLocality();
//                String state = addresses.get(0).getAdminArea();
//                String country = addresses.get(0).getCountryName();
//                String postalCode = addresses.get(0).getPostalCode();
//                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
//                //acil durum mesajı oluştur
//                Toast.makeText(getApplicationContext(),address,Toast.LENGTH_SHORT).show();
//                Emergency_Message = "Acil Durumdayım,Yakınlarımı aradım ama ulaşamadım Adresim : "+address ;
                //normalde ambulans fakat deneme şimdilik
//                sms = SmsManager.getDefault();
//                for(int i = 0;i<cursor2.getCount();i++)
//                {
//                    if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
//                        Toast.makeText(getApplicationContext(),"Lütfen Bu Uygulama İçin Mesaj Servislerine İzin Verin",Toast.LENGTH_LONG).show();
//                        return;
//                    }
//                    cursor2.moveToNext();
//                    sms.sendTextMessage(cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)),null,Emergency_Message,null,null);
//                }
//                Toast.makeText(getApplicationContext(),"Acil Durum Mesajı Gönderildi",Toast.LENGTH_SHORT).show();
                //Integer con = cursor2.getCount();
                //Log.d("con",con.toString());
                //eğer bu sırada yakınlarından biri ararsa ambulans aransın gelmesin :) ya da yakınlarına ambulansa adres gitti yazısı cıksın bunu cocuk kacırılmaları icinde kullanabiliriz
                //hatta reel time konum paylaşımı olabilir sürekli canlı
            }

        });

    }
    protected void SpeakContact(String name)
    {
        //rehberden aldıgı kisinin ismini desin
        //***Önemli Eğer uygulamamız için bir izin sorgusu telefonda uygulama izinleri kısmında sorgulansın istiyosak manifest dosyasına user permissin adlı tag koymamız lazım
        tts1.setSpeechRate(0.85f);
        tts1.speak(name,TextToSpeech.QUEUE_FLUSH,null);
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
        else
        {
            Log.d("aa","Algılanmadı");
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
        String [] arr =  results.get(0).split(" ");
        String [] res = new String[4];
        for(int j=0;j<arr.length;j++)
        {
            Log.d("parcalar",arr[j] + " "+j+".parca" +"\n");
        }
        if(arr[arr.length - 1].compareToIgnoreCase("ara") == 0)
        {

            if(results.get(0).split("'")[0] == results.get(0))
            {
                res[0] = "";
                res[1] = results.get(0).substring(0,results.get(0).length() - 5);//i yi olabilir her ihtimali substring yapıp bir diziye atarız hepsini deneriz
                res[2] = results.get(0).substring(0,results.get(0).length() - 6);
                res[3] = results.get(0).substring(0,results.get(0).length() - 7);
            }
            else {
                res[0] = results.get(0).split("'")[0];
                res[1] = "";
                res[2] = "";
                res[3] = "";
            }
        }
        else
        {
            res[0] = "";
            res[1] = "";
            res[2] = "";
            res[3] = "";
        }

        isMatched = false;
        int char_count = 0;
      Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        startManagingCursor(cursor);
        while(cursor.moveToNext())
            {
                //alttaki tamamı eslesirse arar bizim yapacağımız sonucu boleceğiz
                //result bir dizi bulduğu seyleri yazıyor
                name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                for(int j = 0;j<res.length;j++)
                {
                    if(res[j].compareToIgnoreCase(name)==0)
                    {
                        isMatched = true;
                        call_intent = new Intent(Intent.ACTION_CALL);
                        call_intent.setData(Uri.parse("tel:"+cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))));
                        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED)
                        {
                            Toast.makeText(getApplicationContext(),"Lutfen Bu Hizmeti Kullanmak İçin Telefon Çağrı Servisine İzin Verin",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        startActivity(call_intent);
                        break;
                    }
                }
            }

        if(!isMatched)
            Toast.makeText(getApplicationContext(),"Bu İsimde Bir Kayıt Bulunamadı.",Toast.LENGTH_LONG).show();

    }



}
