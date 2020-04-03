package br.com.training.android.getsuntime

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun getSunset(view: View){
        val city = editTxtCityName.text.toString()
        val url = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22$city%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys"

        MyAsyncTask(applicationContext, txtViewSunsetTime).execute(url)
    }

    // "private" rather than "inner": https://medium.com/android-stars/this-asynctask-class-should-be-static-or-leaks-might-occur-2254f3a0f18
    private class MyAsyncTask(ctx: Context, txtViewSunsetTime: TextView) : AsyncTask<String, String, String>() {
        private var ctx: Context? = ctx
        private var txtView = txtViewSunsetTime

        override fun onProgressUpdate(vararg values: String?) {

            try {
                var json = JSONObject(values[0])
                val query = json.getJSONObject("query")
                val astronomy = query.getJSONObject("results").getJSONObject("channel").getJSONObject("astronomy")
                val sunrise = astronomy.getString("sunrise")

                txtView.text = ctx!!.resources.getText(R.string.txt_sunrise_time_is, sunrise)

            } catch (e: Exception){}

        }

        override fun doInBackground(vararg p0: String?): String {
            try {
                val url = URL(p0[0])
                val urlConnect = url.openConnection() as HttpURLConnection

                urlConnect.connectTimeout = 7000

                var streamInString = convertStreamToString(urlConnect.inputStream)

                publishProgress(streamInString)

            } catch (e:Exception) {}

            return "Success"
        }

        private fun convertStreamToString(inputStream: InputStream): String {
            val bufferReader = BufferedReader(InputStreamReader(inputStream))
            var line: String
            var allString = ""

            try {
                do {
                    line = bufferReader.readLine()

                    if (line != null) {
                        allString += line
                    }

                } while (line != null)

                inputStream.close()

            } catch (e: Exception){}

            return allString
        }

    }

}
