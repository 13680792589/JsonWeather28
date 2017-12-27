package com.example.administrator.jsonweather28;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    HttpURLConnection httpConn = null;
    InputStream din = null;

    Button find = null;
    EditText value = null;
    TextView tv_show = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("天气查询");
        find = (Button) findViewById(R.id.find);
        value = (EditText) findViewById(R.id.value);
        value.setText("广州");
        tv_show = (TextView) findViewById(R.id.tv_show);

        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_show.setText("");
                Toast.makeText(MainActivity.this, "正在查询天气信息~~", Toast.LENGTH_SHORT).show();
                GetJson gd = new GetJson(value.getText().toString());
                gd.start();

            }
        });

    }


    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 123:
                    showData((String) msg.obj);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void showData(String jData) {
        try {
            JSONObject jobj = new JSONObject(jData);
            JSONObject weather = jobj.getJSONObject("data");
            StringBuffer wbf = new StringBuffer();
            wbf.append("天气提示：" + weather.getString("ganmao") + "\n");
            wbf.append("当前温度：" + weather.getString("wendu") + "\n");
            JSONArray jary = weather.getJSONArray("forecast");
            for (int i = 0; i < jary.length(); i++) {
                JSONObject pobj = (JSONObject) jary.opt(i);
                wbf.append("日期：" + pobj.getString("date") + "\n");
                wbf.append("最高温：" + pobj.getString("high") + "\n");
                wbf.append("最低温：" + pobj.getString("low") + "\n");
                wbf.append("风向：" + pobj.getString("fengxiang") + "\n");
                wbf.append("风力：" + pobj.getString("fengli") + "\n");
                wbf.append("天气：" + pobj.getString("type") + "\n");
            }
            tv_show.setText(wbf.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    class GetJson extends Thread {

        private String urlstr = "http://wthrcdn.etouch.cn/weather_mini?city=";

        public GetJson(String cityname) {
            try {
                urlstr = urlstr + URLEncoder.encode(cityname, "UTF-8");

            } catch (Exception ee) {

            }
        }

        @Override
        public void run() {
            try {
                URL url = new URL(urlstr);
                httpConn = (HttpURLConnection) url.openConnection();
                httpConn.setRequestMethod("GET");
                din = httpConn.getInputStream();
                InputStreamReader in = new InputStreamReader(din);
                BufferedReader buffer = new BufferedReader(in);
                StringBuffer sbf = new StringBuffer();
                String line = null;
                while ((line = buffer.readLine()) != null) {
                    sbf.append(line);
                }
                Message msg = new Message();
                msg.obj = sbf.toString();
                msg.what = 123;
                handler.sendMessage(msg);
                Looper.prepare();
                Toast.makeText(MainActivity.this, "现在由廖志伟为你们讲一下最近的天气", Toast.LENGTH_LONG).show();
                Looper.loop();


            } catch (Exception ee) {
                Looper.prepare();
                Toast.makeText(MainActivity.this, "获取数据失败，网络连接失败或输入有误", Toast.LENGTH_LONG).show();
                Looper.loop();
                ee.printStackTrace();
            } finally {
                try {
                    httpConn.disconnect();
                    din.close();

                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        }
    }
}
