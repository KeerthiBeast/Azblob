package com.example.azblob.api

import AzureApiInterface
import com.example.azblob.utils.Utils
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

object AzureRetrofitInstance {
    val azapi: AzureApiInterface by lazy {
        Retrofit.Builder()
            .baseUrl(Utils.Azbase)
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build()
            .create(AzureApiInterface::class.java)
    }
}