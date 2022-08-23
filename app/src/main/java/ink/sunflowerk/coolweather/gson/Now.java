package ink.sunflowerk.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @Author zhaokuii11@163.com
 * @Date 2022-08-22 14:04
 * @Description
 */
public class Now {
    @SerializedName("tmp")
    public String temperature;
    @SerializedName("cond")
    public More more;

    public class More {
        @SerializedName("txt")
        public String info;
    }
}
