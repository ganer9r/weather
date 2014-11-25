package com.ganer.weather;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ganer.db.CacheDB;
import com.ganer.data.Emotion;
import com.ganer.data.RequestClient;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class HomeActivity extends ActionBarActivity implements View.OnClickListener {
    int mMonth;
    Button btnChange, btnAddr;
    ImageView mImageview;
    TextView mMsgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Calendar cal = Calendar.getInstance();
        mMonth = cal.get(Calendar.MONTH)+1;
        btnChange   = (Button) findViewById(R.id.btn_change);
        btnAddr     = (Button) findViewById(R.id.btn_addr);
        mImageview  = (ImageView) findViewById(R.id.character);
        mMsgView  = (TextView) findViewById(R.id.message);

        btnChange.setOnClickListener(this);
        btnAddr.setOnClickListener(this);



        SharedPreferences prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
        String dong = prefs.getString("dong", "");

        if(dong == ""){
            Intent intent = new Intent(this, AddrActivity.class);
            startActivity(intent);
        }else{
            getWeather();
        }

        AdView adView = (AdView)this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getWeather();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode) {
            case KeyEvent.KEYCODE_BACK:
                final View innerView = getLayoutInflater().inflate(R.layout.dialog_exit, null);
                AdView adView = (AdView)innerView.findViewById(R.id.adViweExit);
                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest);

                new AlertDialog.Builder(this)
                        .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                finish();
                            }
                        })
                        .setView(innerView)
                        .setNegativeButton("취소", null).show();


                return false;
            default:
                return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setContent(Bundle data){
        ArrayList<Bundle> list = (ArrayList<Bundle>) data.getSerializable("items");

        TextView temp  = (TextView) findViewById(R.id.temp);
        TextView addr  = (TextView) findViewById(R.id.addr);
        TextView wfKor  = (TextView) findViewById(R.id.wfKor);

        temp.setText( list.get(0).getDouble("temp")+"" );
        addr.setText( data.getString("location") );
        wfKor.setText( list.get(0).getString("wfKor") );

        Bundle emotion = Emotion.get(list);
        mImageview.setImageResource(emotion.getInt("emotion"));
        mMsgView.setText(emotion.getString("message"));

    }

    protected void getWeather(){
        Bundle data;


        SharedPreferences prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
        String dong = prefs.getString("dong", "");

        final CacheDB cache = new CacheDB(getApplicationContext(), "weather_"+dong );
        Bundle b    = cache.get();
        String res  = b.getString("value");
        Bundle weather = parseWeather(res);

        SimpleDateFormat formatter = new SimpleDateFormat( "yyyyMMddHH", Locale.KOREA );
        Date currentTime = new Date( );
        int dTime = Integer.parseInt(formatter.format (currentTime) );
        int cTime = weather.getInt("YmdH");


        if(  dTime > cTime ){
            RequestClient.get("http://web.kma.go.kr/wid/queryDFSRSS.jsp?zone=" + dong, null, weatherHandler);
        }else{
            setContent(weather);
        }

    }

    private AsyncHttpResponseHandler weatherHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onStart() {
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] response,
                              Throwable e) {
            e.printStackTrace();


            Toast toast = Toast.makeText(getApplicationContext(),
                    "네트워크 연결에 실패하였습니다.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
            // TODO Auto-generated method stub
            String res  = new String(response);


            //DB 저장
            SharedPreferences prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
            String dong = prefs.getString("dong", "");
            final CacheDB cache = new CacheDB(getApplicationContext(), "weather_"+dong );
            cache.set(res);

            Bundle weather = parseWeather(res);
            setContent(weather);
        }
    };

    public Bundle parseWeather(String res){
        Document doc = Jsoup.parse(res);

        Elements loc  = doc.select("item>category");
        Elements day  = doc.select("description>header");
        Elements items  = doc.select("data");


        Bundle data = new Bundle();
        ArrayList<Bundle> list = new ArrayList<Bundle>();
        Bundle b;

        for (Element row : items) {
            b = new Bundle();
            b.putInt("hour", Integer.parseInt(row.select("hour").get(0).text()) );
            b.putInt("day", Integer.parseInt(row.select("day").get(0).text()) );
            b.putInt("sky", Integer.parseInt(row.select("sky").get(0).text()) );
            b.putInt("pty", Integer.parseInt(row.select("pty").get(0).text()) );
            b.putInt("pop", Integer.parseInt(row.select("pop").get(0).text()) );
            b.putDouble("temp", Double.parseDouble(row.select("temp").get(0).text()) );
            b.putDouble("tmx", Double.parseDouble(row.select("tmx").get(0).text()) );
            b.putDouble("tmn", Double.parseDouble(row.select("tmn").get(0).text()) );
            b.putString("wfKor", row.select("wfKor").get(0).text() );

            list.add(b);
        }

        data.putString("location", loc.text());
        data.putSerializable("items", list);
        String t = day.select("tm").text();

        if(t.length() > 0) {
            String dt = t.substring(0, 8) + String.format("%02d", list.get(0).getInt("hour"));
            data.putInt("YmdH", Integer.parseInt(dt));
        }

        return data;
    }

    public void onClick(View v){
        if(v == btnChange){
            double r = Math.random();
            int i = (int) ((r*8));

            switch(i){
                case 0: mImageview.setImageResource(R.drawable.char_1_1); break;
                case 1: mImageview.setImageResource(R.drawable.char_1_5); break;
                case 2: mImageview.setImageResource(R.drawable.char_2_1); break;
                case 3: mImageview.setImageResource(R.drawable.char_2_5); break;
                case 4: mImageview.setImageResource(R.drawable.char_3_1); break;
                case 5: mImageview.setImageResource(R.drawable.char_3_5); break;
                case 6: mImageview.setImageResource(R.drawable.char_4_1); break;
                case 7: mImageview.setImageResource(R.drawable.char_4_7); break;

            }

        }else if(v == btnAddr){
            Intent intent = new Intent(this, AddrActivity.class);
            startActivity(intent);
        }
    }

}