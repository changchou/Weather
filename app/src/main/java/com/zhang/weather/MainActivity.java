package com.zhang.weather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.zhang.citylist.CityActivity;
import com.zhang.weather.swiperefresh.PullToRefreshBase;
import com.zhang.weather.swiperefresh.PullToRefreshScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    private TextView tvCity,//城市
            tvRelease,//发布时间
            tvNowWeather,//天气
            tvHumidity,//湿度
            tvTemp,//温度
            tvWind,//风向
            tvPm,//pm25
            tvQuality,//空气质量
            tvSecWeather,//明日天气
            tvSecTemp,//明日温度
            tvSecWind1,//明日风向
            tvSecWind2,//明日风力
            tvSecDay,//明日星期
            tvThiWeather,
            tvThiTemp,
            tvThiWind1,
            tvThiWind2,
            tvThiDay,
            tvFouWeather,
            tvFouTemp,
            tvFouWind1,
            tvFouWind2,
            tvFouDay,
            tvFifWeather,
            tvFifTemp,
            tvFifWind1,
            tvFifWind2,
            tvFifDay,
            tvDress,
            tvCatchCold,
            tvWashCar,
            tvSport,
            tvUltraviolet;

    private ImageView ivNowWeather,
            ivSecWeather,
            ivThiWeather,
            ivFouWeather,
            ivFifWeather;

    private PullToRefreshScrollView refreshScrollView;
    private ScrollView scrollView;

    private String city;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendar = Calendar.getInstance();

        initView();

    }

    private void initView() {
        //选择城市
        findViewById(R.id.rl_city).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, CityActivity.class), 0);
            }
        });

        tvCity = (TextView) findViewById(R.id.tv_city);
        tvCity.setText(getPreferences(Context.MODE_PRIVATE).getString("city", "北京"));
        tvRelease = (TextView) findViewById(R.id.tv_release);

        ivNowWeather = (ImageView) findViewById(R.id.iv_now_weather);
        tvNowWeather = (TextView) findViewById(R.id.tv_now_weather);
        tvHumidity = (TextView) findViewById(R.id.tv_today_humidity);
        tvTemp = (TextView) findViewById(R.id.tv_now_temp);
        tvWind = (TextView) findViewById(R.id.tv_wind);
        tvPm = (TextView) findViewById(R.id.tv_pm);
        tvQuality = (TextView) findViewById(R.id.tv_quality);

        tvSecWeather = (TextView) findViewById(R.id.tv_sec_weather);
        tvSecTemp = (TextView) findViewById(R.id.tv_sec_temp);
        tvSecWind1 = (TextView) findViewById(R.id.tv_sec_wind1);
        tvSecWind2 = (TextView) findViewById(R.id.tv_sec_wind2);
        tvSecDay = (TextView) findViewById(R.id.tv_sec_day);
        ivSecWeather = (ImageView) findViewById(R.id.iv_sec_weather);

        tvThiWeather = (TextView) findViewById(R.id.tv_thi_weather);
        tvThiTemp = (TextView) findViewById(R.id.tv_thi_temp);
        tvThiWind1 = (TextView) findViewById(R.id.tv_thi_wind1);
        tvThiWind2 = (TextView) findViewById(R.id.tv_thi_wind2);
        tvThiDay = (TextView) findViewById(R.id.tv_thi_day);
        ivThiWeather = (ImageView) findViewById(R.id.iv_thi_weather);

        tvFouWeather = (TextView) findViewById(R.id.tv_fou_weather);
        tvFouTemp = (TextView) findViewById(R.id.tv_fou_temp);
        tvFouWind1 = (TextView) findViewById(R.id.tv_fou_wind1);
        tvFouWind2 = (TextView) findViewById(R.id.tv_fou_wind2);
        tvFouDay = (TextView) findViewById(R.id.tv_fou_day);
        ivFouWeather = (ImageView) findViewById(R.id.iv_fou_weather);

        tvFifWeather = (TextView) findViewById(R.id.tv_fif_weather);
        tvFifTemp = (TextView) findViewById(R.id.tv_fif_temp);
        tvFifWind1 = (TextView) findViewById(R.id.tv_fif_wind1);
        tvFifWind2 = (TextView) findViewById(R.id.tv_fif_wind2);
        tvFifDay = (TextView) findViewById(R.id.tv_fif_day);
        ivFifWeather = (ImageView) findViewById(R.id.iv_fif_weather);

        tvDress = (TextView) findViewById(R.id.tv_dressing_index);
        tvCatchCold = (TextView) findViewById(R.id.tv_catch_cold);
        tvWashCar = (TextView) findViewById(R.id.tv_wash_car);
        tvSport = (TextView) findViewById(R.id.tv_sport);
        tvUltraviolet = (TextView) findViewById(R.id.tv_ultraviolet);

        refreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
        refreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                getJsonData();
                refreshScrollView.onRefreshComplete();
            }
        });

        scrollView = refreshScrollView.getRefreshableView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        city = tvCity.getText().toString();
        getJsonData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        tvCity.setText(data.getStringExtra("city"));
        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
        editor.putString("city", data.getStringExtra("city"));
        editor.apply();
    }

    private void getJsonData() {
        String url = "http://op.juhe.cn/onebox/weather/query?cityname=" + city + "&key=fe08983fb39d952de7e74808004177b9";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        dealJsonData(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
        Volley.newRequestQueue(this).add(request);
    }

    private void dealJsonData(JSONObject jsonObject) {
        try {

            JSONObject result = jsonObject.getJSONObject("result");
            System.out.println("result   >>>>>>>>>>>>>>>>>>>" + result);
            JSONObject data = result.getJSONObject("data");
            System.out.println("data     >>>>>>>>>>>>>>>>>>>" + data);
            JSONObject realtime = data.getJSONObject("realtime");
            System.out.println("realtime      >>>>>>>>>>>>>>>>>>>" + realtime);
            //发布时间
            tvRelease.setText(realtime.getString("time") + " 发布");

            JSONObject weather = realtime.getJSONObject("weather");
            System.out.println("weather     >>>>>>>>>>>>>>>>>>>" + weather);
            //天气
            tvNowWeather.setText(weather.getString("info"));
            int imgId = Integer.parseInt(weather.getString("img"));
            String imgid = String.format("%02d", imgId);
            String dOrn;
            if (calendar.get(Calendar.HOUR_OF_DAY) >= 6 &&
                    calendar.get(Calendar.HOUR_OF_DAY) < 18) {
                dOrn = "d";
            } else {
                dOrn = "n";
            }
            //天气图标
            ivNowWeather.setImageResource(getResources().getIdentifier(dOrn + imgid, "drawable", "com.zhang.weather"));
            //湿度
            tvHumidity.setText("湿度 " + weather.getString("humidity") + "%");
            //温度
            tvTemp.setText(weather.getString("temperature") + "℃");

            JSONObject wind = realtime.getJSONObject("wind");
//            System.out.println("wind     >>>>>>>>>>>>>>>>>>>" + wind);
            //风
            tvWind.setText(wind.getString("direct") + "  " + wind.getString("power"));

            JSONObject pm25 = data.getJSONObject("pm25").getJSONObject("pm25");
            System.out.println("pm25     >>>>>>>>>>>>>>>>>>>" + pm25);
            //PM25
            tvPm.setText(pm25.getString("curPm"));
            tvQuality.setText(pm25.getString("quality"));

            JSONArray weatherArray = data.getJSONArray("weather");
            System.out.println("weatherArray     >>>>>>>>>>>>>>>>>>>" + weatherArray);

            //明日天气
            JSONObject secDay = weatherArray.getJSONObject(1);
//            System.out.println("secDay     >>>>>>>>>>>>>>>>>>>" + secDay);
            tvSecDay.setText("星期" + secDay.getString("week"));
            JSONArray secWeather = secDay.getJSONObject("info").getJSONArray("day");
//            System.out.println("secWeather     >>>>>>>>>>>>>>>>>>>" + secWeather);
            tvSecWeather.setText(secWeather.getString(1));
            tvSecTemp.setText(secWeather.getString(2) + "°");
            tvSecWind1.setText(secWeather.getString(3));
            tvSecWind2.setText(secWeather.getString(4));
            ivSecWeather.setImageResource(getResources().getIdentifier("d" + String.format("%02d", Integer.parseInt(secWeather.getString(0))),
                    "drawable", "com.zhang.weather"));

            //后天天气
            JSONObject thiDay = weatherArray.getJSONObject(2);
//            System.out.println("thiDay     >>>>>>>>>>>>>>>>>>>" + thiDay);
            tvThiDay.setText("星期" + thiDay.getString("week"));
            JSONArray thiWeather = thiDay.getJSONObject("info").getJSONArray("day");
//            System.out.println("thiWeather     >>>>>>>>>>>>>>>>>>>" + thiWeather);
            tvThiWeather.setText(thiWeather.getString(1));
            tvThiTemp.setText(thiWeather.getString(2) + "°");
            tvThiWind1.setText(thiWeather.getString(3));
            tvThiWind2.setText(thiWeather.getString(4));
            ivThiWeather.setImageResource(getResources().getIdentifier("d" + String.format("%02d", Integer.parseInt(thiWeather.getString(0))),
                    "drawable", "com.zhang.weather"));

            //第四天天气
            JSONObject fouDay = weatherArray.getJSONObject(3);
//            System.out.println("fouDay     >>>>>>>>>>>>>>>>>>>" + fouDay);
            tvFouDay.setText("星期" + fouDay.getString("week"));
            JSONArray fouWeather = fouDay.getJSONObject("info").getJSONArray("day");
//            System.out.println("fouWeather     >>>>>>>>>>>>>>>>>>>" + fouWeather);
            tvFouWeather.setText(fouWeather.getString(1));
            tvFouTemp.setText(fouWeather.getString(2) + "°");
            tvFouWind1.setText(fouWeather.getString(3));
            tvFouWind2.setText(fouWeather.getString(4));
            ivFouWeather.setImageResource(getResources().getIdentifier("d" + String.format("%02d", Integer.parseInt(fouWeather.getString(0))),
                    "drawable", "com.zhang.weather"));

            //第五天天气
            JSONObject fifDay = weatherArray.getJSONObject(4);
//            System.out.println("fifDay     >>>>>>>>>>>>>>>>>>>" + fifDay);
            tvFifDay.setText("星期" + fifDay.getString("week"));
            JSONArray fifWeather = fifDay.getJSONObject("info").getJSONArray("day");
//            System.out.println("fifWeather     >>>>>>>>>>>>>>>>>>>" + fifWeather);
            tvFifWeather.setText(fifWeather.getString(1));
            tvFifTemp.setText(fifWeather.getString(2) + "°");
            tvFifWind1.setText(fifWeather.getString(3));
            tvFifWind2.setText(fifWeather.getString(4));
            ivFifWeather.setImageResource(getResources().getIdentifier("d" + String.format("%02d", Integer.parseInt(fifWeather.getString(0))),
                    "drawable", "com.zhang.weather"));

            //生活指数
            JSONObject lifeInfo = data.getJSONObject("life").getJSONObject("info");
            System.out.println("lifeInfo    >>>>>>>>>>>>>>" + lifeInfo);

            tvDress.setText(lifeInfo.getJSONArray("chuanyi").getString(0));
            tvCatchCold.setText(lifeInfo.getJSONArray("ganmao").getString(0));
            tvWashCar.setText(lifeInfo.getJSONArray("xiche").getString(0));
            tvSport.setText(lifeInfo.getJSONArray("yundong").getString(0));
            tvUltraviolet.setText(lifeInfo.getJSONArray("ziwaixian").getString(0));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
