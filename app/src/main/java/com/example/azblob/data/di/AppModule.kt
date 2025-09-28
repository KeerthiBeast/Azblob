package com.example.azblob.data.di

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.azblob.data.download.DownloaderImp
import com.example.azblob.data.network.AzblobApi
import com.example.azblob.data.network.SpotifyApi
import com.example.azblob.data.network.SpotifyAuthApi
import com.example.azblob.data.repository.AzblobRepositoryImpl
import com.example.azblob.data.repository.SpotifyAuthRepositoryImpl
import com.example.azblob.data.repository.SpotifyRepositoryImpl
import com.example.azblob.domain.download.Downloader
import com.example.azblob.domain.repository.AzblobRepository
import com.example.azblob.domain.repository.SpotifyAuthRepository
import com.example.azblob.domain.repository.SpotifyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    const val Base = "https://accounts.spotify.com/api/"
    const val BaseApi = "https://api.spotify.com/v1/"

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideAzblobApi(sharedPreferences: SharedPreferences): AzblobApi =
        Retrofit.Builder()
            .baseUrl(sharedPreferences.getString("server_ip", "http://0.0.0.0:6942")!!)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AzblobApi::class.java)

    @Provides
    @Singleton
    fun provideSpotifyAuthApi(): SpotifyAuthApi =
        Retrofit.Builder()
            .baseUrl(Base)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpotifyAuthApi::class.java)

    @Provides
    @Singleton
    fun provideSpotifyApi(): SpotifyApi =
        Retrofit.Builder()
            .baseUrl(BaseApi)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpotifyApi::class.java)

    @Provides
    @Singleton
    fun provideAzblobRepository(
        api: AzblobApi,
        @ApplicationContext context: Context
    ): AzblobRepository = AzblobRepositoryImpl(api, context)

    @Provides
    @Singleton
    fun provideSpotifyAuthRepository(
        api: SpotifyAuthApi,
        @ApplicationContext context: Context
    ): SpotifyAuthRepository = SpotifyAuthRepositoryImpl(api, context)

    @Provides
    @Singleton
    fun provideSpotifyRepository(
        api: SpotifyApi,
        auth: SpotifyAuthRepository,
        @ApplicationContext context: Context
    ): SpotifyRepository = SpotifyRepositoryImpl(api, auth, context)

    @Provides
    @Singleton
    fun provideDownloadApi(@ApplicationContext context: Context): Downloader = DownloaderImp(context)
}