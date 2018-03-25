package rjt.com.android_redfin_app.buyer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ryellap on 3/22/18.
 */

public class ImageClass {

    @SerializedName("title")
    private String Title;
    @SerializedName("image")
    private String Image;
    @SerializedName("response")
    private String Response;

    public String getResponse() {
        return Response;
    }


}
