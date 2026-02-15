package com.ghost.bolt.di

import com.ghost.bolt.api.TmdbApi
import com.ghost.bolt.data.store.ApiKeyStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory


@Module
@InstallIn(SingletonComponent::class)
class ApiModule {
    @Provides
    @Singleton
    fun provideTmdbApi(apiKeyStore: ApiKeyStore): TmdbApi {

        // Dynamic Interceptor: Reads the CURRENT key from the store for every request
        val authInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val originalUrl = originalRequest.url

            // Add api_key param to every URL: https://api.tmdb.org/...?api_key=XYZ
            val newUrl = originalUrl.newBuilder()
                .addQueryParameter("api_key", apiKeyStore.apiKey)
                .build()

            val newRequest = originalRequest.newBuilder()
                .url(newUrl)
                .build()

            chain.proceed(newRequest)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .client(client)
            // MUST use Gson to match your @SerializedName models
            .addConverterFactory(
                Json.asConverterFactory(
                    "application/json; charset=utf-8".toMediaType()
                )
            )
            .build()
            .create(TmdbApi::class.java)
    }
}