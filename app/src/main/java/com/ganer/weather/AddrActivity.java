package com.ganer.weather;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ganer.db.CacheDB;
import com.ganer.data.RequestClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class AddrActivity extends ActionBarActivity {
    int type = 0;
    String code = "";
    ArrayList<Bundle> mList;

    AddrAdapter mAdapter;
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addr);

        setSido();
        String[] fromColumns = {ContactsContract.Data.DISPLAY_NAME};
        int[] toViews = {android.R.id.text1}; // The TextView in simple_list_item_1


        mAdapter = new AddrAdapter(this, R.layout.item_addr , mList);
        mListView = (ListView) findViewById(R.id.addr_list);

        AddrClickListener itemListener = new AddrClickListener();
        mListView.setOnItemClickListener(itemListener);
        mListView.setAdapter(mAdapter);
    }

    protected void setSido(){
        mList = new ArrayList<Bundle>();

        Bundle b = new Bundle();
        b.putString("code", "11");
        b.putString("value", "서울특별시");
        mList.add(b);

        b = new Bundle();
        b.putString("code", "26");
        b.putString("value", "부산광역시");
        mList.add(b);

        b = new Bundle();
        b.putString("code", "27");
        b.putString("value", "대구광역시");
        mList.add(b);

        b = new Bundle();
        b.putString("code", "28");
        b.putString("value", "인천광역시");
        mList.add(b);

        b = new Bundle();
        b.putString("code", "29");
        b.putString("value", "광주광역시");
        mList.add(b);

        b = new Bundle();
        b.putString("code", "30");
        b.putString("value", "대전광역시");
        mList.add(b);

        b = new Bundle();
        b.putString("code", "31");
        b.putString("value", "울산광역시");
        mList.add(b);

        b = new Bundle();
        b.putString("code", "41");
        b.putString("value", "경기도");
        mList.add(b);

        b = new Bundle();
        b.putString("code", "42");
        b.putString("value", "강원도");
        mList.add(b);

        b = new Bundle();
        b.putString("code", "43");
        b.putString("value", "충청북도");
        mList.add(b);

        b = new Bundle();
        b.putString("code", "44");
        b.putString("value", "충청남도");
        mList.add(b);

        b = new Bundle();
        b.putString("code", "45");
        b.putString("value", "전라북도");
        mList.add(b);

        b = new Bundle();
        b.putString("code", "46");
        b.putString("value", "전라남도");
        mList.add(b);

        b = new Bundle();
        b.putString("code", "47");
        b.putString("value", "경상북도");
        mList.add(b);

        b = new Bundle();
        b.putString("code", "48");
        b.putString("value", "경상남도");
        mList.add(b);

        b = new Bundle();
        b.putString("code", "50");
        b.putString("value", "제주특별자치도");
        mList.add(b);
    }

    private class AddrAdapter extends BaseAdapter {
        Context mContext;
        ArrayList<Bundle> items;
        int mLayout;
        LayoutInflater inflater;

        public AddrAdapter(Context context, int layout, ArrayList<Bundle> addrs) {
            mContext = context;
            items = addrs;
            mLayout	= layout;

            inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = inflater.inflate(mLayout, null);
            }

            Bundle b = (Bundle) getItem(i);
            TextView t = (TextView) view.findViewById(R.id.addr_text);
            t.setText(b.getString("value"));
            return view;
        }

        public void setItemList(String json){
            ArrayList<Bundle> itemList = new ArrayList<Bundle>();

            if( type == 0 || type == 1) {
                try {
                    JSONArray list = new JSONArray(json);

                    Bundle b;
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject row = list.getJSONObject(i);
                        b = new Bundle();
                        b.putString("code", row.getString("code"));
                        b.putString("value", row.getString("value"));

                        itemList.add(b);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                type++;

            }

            items  = itemList;
            mList  = itemList;
            notifyDataSetChanged();
        }
    }

    private class AddrClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String url = "";
            code = mList.get(i).getString("code");
            final CacheDB cache = new CacheDB(getApplicationContext(), "addr_"+code );
            Bundle b = cache.get();
            String val = b.getString("value");

            if( val == null || val == "") {
                if (type == 0) {
                    url = "http://www.kma.go.kr/DFSROOT/POINT/DATA/mdl." + code + ".json.txt";

                    RequestClient.get(url, null, locHandler);
                } else if (type == 1) {
                    url = "http://www.kma.go.kr/DFSROOT/POINT/DATA/leaf." + code + ".json.txt";

                    RequestClient.get(url, null, locHandler);
                } else if (type == 2) {
                    SharedPreferences prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("dong", code);
                    editor.commit();

                    finish();
                }
            }else {
                mAdapter.setItemList(val);
            }


        }

    }

    private AsyncHttpResponseHandler locHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onStart() {
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] response,
                              Throwable e) {
            e.printStackTrace();

            Toast toast = Toast.makeText(getApplicationContext(),
                    "네트워크 연결에 실패하였습니다.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
            // TODO Auto-generated method stub

            String json = new String(response);

            final CacheDB cache = new CacheDB(getApplicationContext(), "addr_"+code );
            cache.set(json);

            mAdapter.setItemList(json);

        }
    };

}
