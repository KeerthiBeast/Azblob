import com.example.azblob.model.BlobModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

/*Interface for Azure Rest API
* restype=container&comp=list&x-ms-version=2015-04-05
* The above is used as the query parameters rather than headers to get download links of the songs
* This allows us to download songs and not have to use SAS tokens to download songs*/

interface AzureApiInterface {
    @GET("?restype=container&comp=list&x-ms-version=2015-04-05")
    suspend fun listBlobs(
        @Query("Authorization") authorization: String,
        @Query("x-ms-date") date: String
    ): Response<BlobModel>
}