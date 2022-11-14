package com.example.conversormoedasretrofit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import com.example.conversormoedasretrofit.api.Endpoint
import com.example.conversormoedasretrofit.util.NetworkUtils
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create

class MainActivity : AppCompatActivity() {
    private lateinit var spFrom : Spinner
    private lateinit var spTo : Spinner
    private lateinit var btConvert : Button
    private lateinit var tvResult: TextView
    private lateinit var etValueFrom: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        spFrom = findViewById(R.id.spFrom)
        spTo = findViewById(R.id.spTo)
        btConvert = findViewById(R.id.btConvert)
        tvResult = findViewById(R.id.tvResult)
        etValueFrom = findViewById(R.id.etValueFrom)

        getCurrencies()

        btConvert.setOnClickListener { convertMoney() }
    }

    fun convertMoney(){
        val retrofitClient = NetworkUtils.getRetrofitInstance("https://cdn.jsdelivr.net/")
        val endpoint = retrofitClient.create(Endpoint::class.java)

        endpoint.getCurrencyRate(spFrom.selectedItem.toString(), spTo.selectedItem.toString()).enqueue(object :
        retrofit2.Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                var data = response.body()?.entrySet()?.find { it.key == spTo.selectedItem.toString() }
                val rate : Double = data?.value.toString().toDouble()
                val conversion = etValueFrom.text.toString().toDouble() * rate

                tvResult.setText(conversion.toString())
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println("Não foi")
            }

        })
    }



    fun getCurrencies(){
        val retrofitClient = NetworkUtils.getRetrofitInstance("https://cdn.jsdelivr.net/")
        val endpoint = retrofitClient.create(Endpoint::class.java)

        endpoint.getCurencies().enqueue(object : retrofit2.Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                var data = mutableListOf<String>()

                response.body()?.keySet()?.iterator()?.forEach {
                    data.add(it)
                }

                val posBRL = data.indexOf("brl")
                val posUSD = data.indexOf("usd")

                spFrom.setSelection(posBRL)
                spTo.setSelection(posUSD)


                val adapter = ArrayAdapter(baseContext,android.R.layout.simple_spinner_dropdown_item, data)
                spFrom.adapter = adapter
                spTo.adapter = adapter
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println("Não foi")
            }

        })
    }
}