package rjt.com.android_redfin_app.buyer.restrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by ryellap on 3/21/18.
 */

public interface UsesServices {
    
    @GET("register.php?property&add&")
    public Call<ResponseBody> addProperty(@Query("propertyname") String propertyname, @Query("propertytype") String propertytype, @Query("propertycat") String propertycat, @Query("propertyaddress1") String propertyaddress1, @Query("propertyaddress2") String propertyaddress2, @Query("propertyzip") String propertyzip, @Query("propertylat") String propertylat, @Query("propertylong") String propertylong, @Query("propertycost") String propertycost, @Query("propertysize") String propertysize, @Query("propertydescription") String propertydescription, @Query("propertyimg1") String propertyimg1, @Query("propertyimg2") String propertyimg2, @Query("propertyimg3") String propertyimg3);

    @GET("register.php?property&delete&")
    public Call<ResponseBody> deleteProperty(@Query("pptyid") String propertyId);

    @GET("register.php?property&edit&")
    public Call<ResponseBody> editProperty(@Query("pptyid") String propertyId, @Query("propertyname") String propertyname, @Query("propertytype") String propertytype, @Query("propertycat") String propertycat, @Query("propertyaddress1") String propertyaddress1, @Query("propertyaddress2") String propertyaddress2, @Query("propertyzip") String propertyzip, @Query("propertylat") String propertylat, @Query("propertylong") String propertylong, @Query("propertycost") String propertycost, @Query("propertysize") String propertysize, @Query("propertydescription") String propertydescription, @Query("propertyimg1") String propertyimg1, @Query("propertyimg2") String propertyimg2, @Query("propertyimg3") String propertyimg3);

}
