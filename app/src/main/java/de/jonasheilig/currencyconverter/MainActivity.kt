package de.jonasheilig.currencyconverter

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var amountEditText: EditText
    private lateinit var fromCurrencySpinner: Spinner
    private lateinit var toCurrencySpinner: Spinner
    private lateinit var convertButton: Button
    private lateinit var resultTextView: TextView

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        amountEditText = findViewById(R.id.amountEditText)
        fromCurrencySpinner = findViewById(R.id.fromCurrencySpinner)
        toCurrencySpinner = findViewById(R.id.toCurrencySpinner)
        convertButton = findViewById(R.id.convertButton)
        resultTextView = findViewById(R.id.resultTextView)

        val currencies = arrayOf("USD", "EUR", "GBP", "JPY", "AUD")
        fromCurrencySpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, currencies)
        toCurrencySpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, currencies)

        convertButton.setOnClickListener {
            val amount = amountEditText.text.toString().toDoubleOrNull()
            if (amount != null) {
                val fromCurrency = fromCurrencySpinner.selectedItem.toString()
                val toCurrency = toCurrencySpinner.selectedItem.toString()
                fetchConversionRate(fromCurrency, toCurrency, amount)
            } else {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchConversionRate(fromCurrency: String, toCurrency: String, amount: Double) {
        val url = "" // Enter URL

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val json = response.body?.string()
                if (json != null) {
                    val jsonObject = JSONObject(json)
                    val rate = jsonObject.getJSONObject("rates").getDouble(toCurrency)
                    val convertedAmount = amount * rate

                    runOnUiThread {
                        resultTextView.text = "$amount $fromCurrency = $convertedAmount $toCurrency"
                    }
                }
            }
        })
    }
}