package com.paysera.currency.exchange.client

import okhttp3.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by eduardo.delito on 3/11/20.
 */
class PayseraApiClient(okHttp: OkHttpClient.Builder) : Interceptor, Authenticator {

    private var retrofit: Retrofit

    init {
        okHttp.addInterceptor(this)
        okHttp.authenticator(this)
        retrofit = Retrofit.Builder()
            .client(okHttp.build())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BuildConfig.BASE_HTTP_URL)
            .build()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        request = request.newBuilder()
            .addHeader(HEADER_CONTENT_TYPE, HEADER_VALUE_APP_JSON)
            .addHeader(HEADER_ACCEPT, HEADER_VALUE_APP_JSON)
            .build()
        return chain.proceed(request)
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        return response.request().newBuilder().build()
    }

    /**
     * Function to create TrackApiService instance
     *
     * @return the created instance of TrackApiService
     */
    fun getService(): PayseraApiService {
        return retrofit.create(PayseraApiService::class.java)
    }

    companion object {
        const val HEADER_CONTENT_TYPE = "Content-Type"
        const val HEADER_ACCEPT = "Accept"
        const val HEADER_VALUE_APP_JSON = "application/json"
    }
}
