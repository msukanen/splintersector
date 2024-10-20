package net.msukanen.splintersector

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Net
import com.badlogic.gdx.net.HttpRequestBuilder

class DataFetcher(
    private val application: Application = Gdx.app,
    private val net: Net = Gdx.net
) {
    companion object {
        private const val SERVER_URI = "http://localhost:15551"
        private const val API_USAGE_URL = "$SERVER_URI/splnsect/api"
    }

    fun fetchApiUsage(callback: DataCallback) = application.postRunnable {
        val httpGet = HttpRequestBuilder()
            .newRequest()
            .method(Net.HttpMethods.GET)
            .url(API_USAGE_URL)
            .build()
        net.sendHttpRequest(httpGet, object : Net.HttpResponseListener {
            override fun handleHttpResponse(httpResponse: Net.HttpResponse) {
                val responseString = httpResponse.resultAsString
                callback.onSuccess(responseString)
            }

            override fun failed(t: Throwable) {
                callback.onFailure(t)
            }

            override fun cancelled() {
                callback.onFailure(Exception("Req cancelled"))
            }
        })
    }

    interface DataCallback {
        fun onSuccess(data: String)
        fun onFailure(t: Throwable)
    }
}
