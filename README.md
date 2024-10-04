# Azblob
Application to download Spotify playlists and songs to Azure blob and download those songs to mobile.
Uses [Spotdl](https://github.com/spotDL/spotify-downloader) library to download Playlists and songs 
and upload them to Azure blob using [Azblob-API](https://github.com/KeerthiBeast/Azblob-API)
Developed in Kotlin using Jetpack Compose UI library. Android must be 13 or above for the features to work as intended.

# Usage
- Initialize the required values in the Utils file in Utils folder
- Clone the Azblob-API and start the server
- If server url is a raw IP, that should be added in the network_security_config in the res -> xml folder.
- Download songs the your blob storage the see the items show up in the front page

# How it works
This application should be used with [Azblob-API](https://github.com/KeerthiBeast/Azblob-API) to utilize its full features. 
The songs can be downloaded to your Azure blob using the Azblob-API. The songs in the blobs can then be accessed and downloaded using
Azure Storage REST API.

# My Motive
From what you have read above it feels like a roundabout way of downloading songs from spotify. You could have just 
downloaded the songs using the same Spotdl library directly to mobile rather than uploading the files to Blob storage and
downloading the songs from Blob. My justification to this is that you can setup this app with your credentials and
just sync the songs to an another phone which is lot easier than download it through Spotdl as it take more time to
search through and download than downloading it directly from the Blob. Another reason is that I wanted to learn 
Azure and Kotlin so this is a nice way of learning.