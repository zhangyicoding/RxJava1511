package net.bwie.rxjava.httpservice;


import net.bwie.rxjava.bean.FoodBean;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FoodHttpService {

//    @GET("ios/cf/dish_list.php?stage_id=1&limit=10")
//    Call<FoodBean> getFoodCall(@Query("page") int page);

    @GET("ios/cf/dish_list.php?stage_id=1&limit=10")
    Observable<FoodBean> getFoodObservable(@Query("page") int page);

}
