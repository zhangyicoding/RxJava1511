package net.bwie.rxjava.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import net.bwie.rxjava.R;
import net.bwie.rxjava.bean.FoodBean;
import net.bwie.rxjava.httpservice.FoodHttpService;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 1、构建Retrofit：基础url，GSON自动解析器
 * 2、获取httpService接口
 * 3、不再获取接口中的任务Call -> 而是获取接口中的Observable
 * 4、产生数据、数据变换、线程跳转等过程的操作
 * 5、最终产生的数据还给我们，我们加以利用
 */
// http://www.qubaobei.com/ios/cf/dish_list.php?stage_id=1&limit=10&page=1
public class RetrofitRxJavaActivity extends AppCompatActivity {

    private Retrofit mRetrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit_rx_java);

        loadData();
    }

    private void loadData() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl("http://www.qubaobei.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())// 将Call转换为RxJava的Observable
                .build();
        // 创建接口的实例
        FoodHttpService httpService = mRetrofit.create(FoodHttpService.class);
        // 获得观察者（工人）对象。Retrofit自动下载、解析数据，并自动将数据源设置给观察者
        // 我们只需要做线程跳转，数据变换操作就可以了
        Observable<FoodBean> observable = httpService.getFoodObservable(1);
        // 跳转到子线程下载网络数据
        // 如何记住subscribeOn和ObserveOn顺序？
        // 技巧：先出现单词后使用，后出现的单词先使用，交叉记
        // subscribe后出现，先使用subscribeOn，跳转到子线程下载、解析、变换
        // Observable先出现，后使用ObserveOn，跳转主线程，展示
        observable.subscribeOn(Schedulers.io())
                // 将自动得到的FoodBean变换为我们想要的List集合，里面都是吃的DataBean
                .map(new Function<FoodBean, List<FoodBean.DataBean>>() {
                        @Override
                        public List<FoodBean.DataBean> apply(FoodBean foodBean) throws Exception {
                            // 在这个过程中我们做数据变换
                            return foodBean.getData();
                        }
                })
                // 将子线程中产生的数据返回主线程
                .observeOn(AndroidSchedulers.mainThread())
                // 接收最终的数据
                .subscribe(new Consumer<List<FoodBean.DataBean>>() {
                    @Override
                    public void accept(List<FoodBean.DataBean> datas) throws Exception {
                        // 我们在主线程直接获取到了最终数据，可以加以利用了
                        for (FoodBean.DataBean data : datas) {
                            Log.d("1511", data.getTitle());
                        }

                    }
                });


    }
}
