package ink.sunflowerk.coolweather;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.bumptech.glide.Glide;
import ink.sunflowerk.coolweather.gson.Weather;
import ink.sunflowerk.coolweather.service.AutoUpdateService;
import ink.sunflowerk.coolweather.util.HttpUtil;
import ink.sunflowerk.coolweather.util.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;

public class WeatherActivity extends AppCompatActivity {
    ScrollView weatherLayout;
    TextView titleCity;
    TextView titleUpdateTime;
    TextView degreeText;
    TextView weatherInfoText;
    LinearLayout forecastLayout;
    TextView aqiText;
    TextView pm25Text;
    TextView comfortText;
    TextView carWashText;
    TextView sportText;
    SharedPreferences prefs;
    ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefresh;
    public DrawerLayout drawerLayout;
    ImageButton navButton;
    public static String weatherRefId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        //初始化控件
        weatherLayout = findViewById(R.id.weather_layout);
        titleCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        forecastLayout = findViewById(R.id.forecast_layout);
        aqiText = findViewById(R.id.aqi_text);
        pm25Text = findViewById(R.id.pm25_text);
        comfortText = findViewById(R.id.comfort_text);
        carWashText = findViewById(R.id.car_wash_text);
        sportText = findViewById(R.id.sport_text);
        bingPicImg = findViewById(R.id.bing_pic_img);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        drawerLayout = findViewById(R.id.drawer_layout);
        navButton = findViewById(R.id.nav_button);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        String bingPic = prefs.getString("bing_pic", null);
        String weatherId;
        //天气界面
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            loadBingPic();
        }
        if (weatherString != null) {
            //有缓存直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            weatherId = weather.basic.weatherId;
            Log.e("TAG", "weatherId==null: " + weatherId);
            showWeatherInfo(weather);
        } else {
            //无缓存去服务器查询天气
            weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
            Log.e("TAG", "weatherId!=null: " + weatherId);
        }
        //下拉刷新
        swipeRefresh.setOnRefreshListener(() -> {
            // 如果进行了选择城市了下拉刷新是第一次选择的不会是本次刷新的
            if (weatherRefId != null) {
                Log.e("TAG", "weatherRefId: " + weatherRefId);
                requestWeather(weatherRefId);
            } else {
                Log.e("TAG", "weatherIdFresh: " + weatherId);
                requestWeather(weatherId);
            }
        });
        navButton.setOnClickListener((v) -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });


    }

    /**
     * 加载必应每日一图
     */
    private void loadBingPic() {
        String bingPicUrl = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(bingPicUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic = response.body().string();
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("bing_pic", bingPic);
                edit.apply();
                runOnUiThread(() -> {
                    Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                });
            }
        });
    }

    /**
     * 根据天气 id请求城市天气信息
     *
     * @param weatherId
     */
    public void requestWeather(String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    swipeRefresh.setRefreshing(false);//停止刷新
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                Log.e("TAG", "onResponse: " + response.body());
                Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(() -> {
                    if (weather != null && "ok".equals(weather.status)) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("weather", responseText);
                        editor.apply();
                        showWeatherInfo(weather);
                    } else {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                    swipeRefresh.setRefreshing(false);//停止刷新
                });

            }
        });
    }

    /**
     * 处理并展示 weather实力类中的数据
     *
     * @param weather
     */
    @SuppressLint("SetTextI18n")
    private void showWeatherInfo(Weather weather) {
        if (weather != null && "ok".equals(weather.status)) {
            String cityName = weather.basic.cityName;
            String updateTime = weather.basic.update.updateTime.split(" ")[1];
            String degree = weather.now.temperature + "°C";
            String weatherInfo = weather.now.more.info;
            titleCity.setText(cityName);
            titleUpdateTime.setText(updateTime);
            degreeText.setText(degree);
            weatherInfoText.setText(weatherInfo);
            forecastLayout.removeAllViews();
            weather.foreCastsList.stream().forEach(f -> {
                View view =
                        LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
                TextView dateText = view.findViewById(R.id.date_text);
                TextView infoText = view.findViewById(R.id.info_text);
                TextView maxText = view.findViewById(R.id.max_text);
                TextView minText = view.findViewById(R.id.min_text);
                dateText.setText(f.date);
                infoText.setText(f.more.info);
                maxText.setText(f.temperature.max + "°C");
                minText.setText(f.temperature.min + "°C");
                forecastLayout.addView(view);
            });
            if (weather.aqi != null) {
                aqiText.setText(weather.aqi.city.api);
                pm25Text.setText(weather.aqi.city.pm25);
            }
            String comfort = "舒适度:" + weather.suggestion.comfort.info;
            String carWash = "洗车指数:" + weather.suggestion.carWash.info;
            String sport = "运动建议:" + weather.suggestion.sport.info;
            comfortText.setText(comfort);
            carWashText.setText(carWash);
            sportText.setText(sport);
            weatherLayout.setVisibility(View.VISIBLE);

            //AutoUpdateService 一旦选中了某个城市并成功更新后，AutoUpdateService就会一直在后台运行
            Intent intent = new Intent(this, AutoUpdateService.class);
            startService(intent);
        } else {
            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
        }

    }


}