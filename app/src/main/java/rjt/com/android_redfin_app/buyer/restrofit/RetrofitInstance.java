package rjt.com.android_redfin_app.buyer.restrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ryellap on 3/20/18.
 */
public class RetrofitInstance {

    public static final String BASE_URL = "http://www.rjtmobile.com/realestate/";

    static Retrofit retrofit = null;

    public static Retrofit getRetrofitInstance(){
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

}
