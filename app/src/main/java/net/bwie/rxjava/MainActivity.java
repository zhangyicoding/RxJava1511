package net.bwie.rxjava;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    protected Button mTest1;
    protected Button mTest2;
    protected Button mTest3;
    protected Button mStartSecondBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mTest1 = (Button) findViewById(R.id.test1);
        mTest1.setOnClickListener(MainActivity.this);
        mTest2 = (Button) findViewById(R.id.test2);
        mTest2.setOnClickListener(MainActivity.this);
        mTest3 = (Button) findViewById(R.id.test3);
        mTest3.setOnClickListener(MainActivity.this);
        mStartSecondBtn = (Button) findViewById(R.id.start_second_btn);
        mStartSecondBtn.setOnClickListener(MainActivity.this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.test1) {
            test1();
        } else if (view.getId() == R.id.test2) {
            test2();
        } else if (view.getId() == R.id.test3) {
            test3();
        } else if (view.getId() == R.id.start_second_btn) {
            Intent intent = new Intent(this, RetrofitRxJavaActivity.class);
            startActivity(intent);
        }
    }

    // RxJava对数据传递的思想，参考try-catch-fianlly结构思想
    private void test1() {
        // 定义数据源（零件）
        final String cpu = "麒麟970";
        // 设置数据源（将零件交给工人,Observable）
        Observable<String> observable = Observable.just(cpu);
        // 创建一个处理数据空间，理解为如何观察数据（工人如何加工零件）
        // 数据变换原则：不允许改动原始数据
        observable.create(new ObservableOnSubscribe<String>() {

            // ObservableEmitter：数据传递者，将当前回调中处理的数据传递给下一个人
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                String phone = "华为Mate10 Pro安装了" + cpu;

                // 将产生的新数据传递出去,正常情况传递给下一位
                emitter.onNext(phone);
                // 假设获取最终数据失败，会执行第二个消费者：用于处理错误的情况
//                emitter.onError(new Throwable("CPU需要返厂维修"));
            }
        })
                // 订阅，这里将接受到上面产生的数据，规范要求是最终数据，交给消费者使用
                .subscribe(new Consumer<String>() {

                               // 接收最终数据
                               @Override
                               public void accept(String s) throws Exception {
                                   Log.d("1511", "最终街道的数据是：" + s);
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.d("1511", "数据传递失败，：" + throwable.getMessage());
                            }
                        });
    }

    // map操作符：数据变换
    // 有字符串“25”,求字符串中数字的
    // 数据变换流程：String -> int -> int * int
    private void test2() {
        // 设置数据源
        Observable<String> observable = Observable.just("25");
        // 使用map第一次变换：将String变换为整形int（Integer）
        // 泛型一：原始数据类型
        // 泛型二：变换后的数据类型
        observable.map(new Function<String, Integer>() {
            @Override
            public Integer apply(String s) throws Exception {
                return Integer.valueOf(s);
            }
        })
                // 第二次map变换：做数值平方的处理
                .map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer) throws Exception {
                        return integer * integer;
                    }
                })
                // 订阅：获取最终数据，直接拿去使用
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d("1511", "最终接收到的数字：" + integer);
                    }
                });
    }

    // concatMap：拆分数据
    // 将1个集合/数组中的10条数据拆分成10个单独的数据
    // List<String> -> String
    private void test3() {
        // 设置数据
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("美女主播：" + i);
        }
        Observable<List<String>> observable = Observable.just(list);
        observable.concatMap(new Function<List<String>, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(List<String> list) throws Exception {
                // 将1个list集合拆分成10个含有String数据的观察源
                return Observable.fromIterable(list);
            }
        })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Thread.sleep(1000);
                        Log.d("1511", "有请：" + s);
                    }
                });
    }

}
