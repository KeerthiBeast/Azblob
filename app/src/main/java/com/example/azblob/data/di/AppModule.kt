package com.example.azblob.data.di

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.azblob.data.download.DownloaderImp
import com.example.azblob.data.network.AzblobApi
import com.example.azblob.data.network.AzureApi
import com.example.azblob.data.repository.AzblobRepositoryImpl
import com.example.azblob.data.repository.AzureRepositoryImpl
import com.example.azblob.domain.download.Downloader
import com.example.azblob.domain.repository.AzblobRepository
import com.example.azblob.domain.repository.AzureRepository
import com.example.azblob.utils.Utils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import javax.inject.Singleton

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAzblobApi(): AzblobApi =
        Retrofit.Builder()
            .baseUrl(Utils.Base)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AzblobApi::class.java)

    @Provides
    @Singleton
    fun provideAzureApi(): AzureApi =
        Retrofit.Builder()
            .baseUrl(Utils.Azbase)
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build()
            .create(AzureApi::class.java)

    @Provides
    @Singleton
    fun provideAzblobRepository(api: AzblobApi): AzblobRepository = AzblobRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideAzureRepository(
        api: AzureApi,
        @ApplicationContext context: Context,
    ): AzureRepository =
        AzureRepositoryImpl(api, context)

    @Provides
    @Singleton
    fun provideDownloadApi(@ApplicationContext context: Context): Downloader = DownloaderImp(context)
}