package com.sharpdeep.wifiscanner;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.sharpdeep.wifiscanner.utils.NetUtil;

import org.w3c.dom.Text;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    private ListView mWifiInfoList;
    private Button mStartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupView();
    }

    private void setupView(){
        mWifiInfoList = (ListView)findViewById(R.id.list_wifi_info);
        mStartBtn = (Button)findViewById(R.id.btn_start);

        mWifiInfoList.setAdapter(new MyAdapter());

        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetUtil.isWifiEnable(MainActivity.this)) {
                    NetUtil.openWifi(MainActivity.this);
                } else {
                    updateListView();
                }
            }
        });
    }

    private void updateListView() {

        ((MyAdapter) mWifiInfoList.getAdapter()).clearAllItem();
        if (NetUtil.isWifiConnected(MainActivity.this)){
            HashMap<String, String> wifiInfoMap = (HashMap) NetUtil.getWifiInfo(MainActivity.this);
            InfoItem wifiInfo = new InfoItem(wifiInfoMap.get("SSID"),
                    "BSSID:\t" + wifiInfoMap.get("BSSID") + "\n" +
                            "MAC::\t" + wifiInfoMap.get("mac") + "\n" +
                            "IP:\t" + wifiInfoMap.get("ip") + "\n" +
                            "掩码:\t" + wifiInfoMap.get("mask") + "\n" +
                            "网关:\t" + wifiInfoMap.get("netgate") + "\n" +
                            "DNS:\t" + wifiInfoMap.get("dns") + "\n" +
                            "RSSI:\t" + wifiInfoMap.get("rssi") + "\n" +
                            "LinkSpeed:\t" + wifiInfoMap.get("linkspeed"));
            ((MyAdapter) mWifiInfoList.getAdapter()).addItem(wifiInfo);
        }else{
            Toast.makeText(MainActivity.this,"没有连接wifi",Toast.LENGTH_SHORT).show();
        }

        if (NetUtil.isWifiEnable(MainActivity.this)){
            ArrayList<HashMap<String,String>> allWifiInfoList = (ArrayList<HashMap<String, String>>) NetUtil.getAllWifiInfo(MainActivity.this);
            for(HashMap<String,String> map : allWifiInfoList){
                InfoItem item = new InfoItem(map.get("SSID"),
                        "BSSID:\t"+map.get("BSSID")+"\n"+
                                "强度:\t"+map.get("level"));
                ((MyAdapter) mWifiInfoList.getAdapter()).addItem(item);
            }
        }else{
            Toast.makeText(MainActivity.this,"没有打开wifi",Toast.LENGTH_SHORT).show();
        }
    }

    private class MyAdapter extends BaseAdapter {

        private ArrayList<InfoItem> mData = new ArrayList<InfoItem>();
        private LayoutInflater mInflater;

        public MyAdapter() {
            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addItem(final InfoItem item) {
            mData.add(item);
            notifyDataSetChanged();
        }

        public void clearAllItem(){
            mData.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public InfoItem getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(android.R.layout.simple_expandable_list_item_2, null);
                holder = new ViewHolder();
                holder.titleTxtView = (TextView)convertView.findViewById(android.R.id.text1);
                holder.infoTxtView = (TextView)convertView.findViewById(android.R.id.text2);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.titleTxtView.setText(mData.get(position).title);
            holder.infoTxtView.setText(mData.get(position).info);
            return convertView;
        }

    }

    private static class ViewHolder {
        public TextView titleTxtView;
        public TextView infoTxtView;
    }

    private class InfoItem{
        public String title;
        public String info;
        public InfoItem(String title,String info){
            this.title = title;
            this.info = info;
        }
    }

}
