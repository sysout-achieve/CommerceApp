package com.gunt.commerceapp.di

import android.content.Context
import com.gunt.commerceapp.common.Constants.BASE_URL
import com.gunt.data.mock.MockInterceptor
import com.gunt.data.remote.MainSectionService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .connectionPool(ConnectionPool(5, 20, TimeUnit.SECONDS))
            .addInterceptor(MockInterceptor(context))
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun provideHomeSectionService(retrofit: Retrofit): MainSectionService =
        retrofit.create(MainSectionService::class.java)
}