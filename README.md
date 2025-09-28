# Azblob
Application to download Spotify playlists and songs to Azure blob and download those songs to mobile.
Uses [Spotdl](https://github.com/spotDL/spotify-downloader) library to download Playlists and songs 
and upload them to Google buckets using [GCblob-API](https://github.com/KeerthiBeast/GCblob-api)
Developed in Kotlin using Jetpack Compose UI library. Android version must be 13 or above for the features to work as intended.

# Usage
- Initialize the required values in the Utils file in Utils folder
- Clone the GCblob-API and start the server
- Server Ip can be changed from the settings page of the application
- View the songs and download them to your mobile from your bucket
- To download songs select a default location from the settings page
- Default playlist should also be selected from the settings page

# How it works
This application should be used with [GCblob-API](https://github.com/KeerthiBeast/GCblob-api). 
The songs can be downloaded to your Google bucket using the GCblob-API. The songs in the bucket can then be accessed and downloaded using
API.
