package com.example.newyorkweather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.newyorkweather.api.OpenWeatherMapService
import com.example.newyorkweather.model.OpenWeatherMapResponseData
import kotlinx.android.synthetic.main.activity_main.main_description as descriptionView
import kotlinx.android.synthetic.main.activity_main.main_status as statusView
import kotlinx.android.synthetic.main.activity_main.main_title as titleView
import kotlinx.android.synthetic.main.activity_main.main_weather_icon as weatherIconView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity() {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }
    private val weatherApiService by lazy {
        retrofit.create(OpenWeatherMapService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        weatherApiService
            .getWeather("New York", "[YOUR TOKEN]")
            .enqueue(object : Callback<OpenWeatherMapResponseData> {
                override fun onFailure(call: Call<OpenWeatherMapResponseData>, t: Throwable) {
                    showError("Response failed: ${t.message}")
                }

                override fun onResponse(
                    call: Call<OpenWeatherMapResponseData>,
                    response: Response<OpenWeatherMapResponseData>
                ) = handleResponse(response)
            })
    }

    private fun handleResponse(response: Response<OpenWeatherMapResponseData>) =
        if (response.isSuccessful) {
            response.body()?.let { validResponse ->
                handleValidResponse(validResponse)
            } ?: Unit
        } else {
            showError("Response was unsuccessful: ${response.errorBody()}")
        }

    private fun handleValidResponse(response: OpenWeatherMapResponseData) {
        titleView.text = response.locationName
        response.weather.firstOrNull()?.let { weather ->
            statusView.text = weather.status
            descriptionView.text = weather.description
            Glide.with(this)
                .load("https://openweathermap.org/img/wn/${weather.icon}@2x.png")
                .centerInside()
                .into(weatherIconView)
        }
    }

    private fun showError(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT)
            .show()
}
