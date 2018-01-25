package net.bwie.rxjava;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    protected Button mTest1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mTest1 = (Button) findViewById(R.id.test1);
        mTest1.setOnClickListener(MainActivity.this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.test1) {
            test1();
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
            }
        })
                // 订阅，这里将接受到上面产生的数据，规范要求是最终数据，交给消费者使用
                .subscribe(new Consumer<String>() {

                    // 接收最终数据
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d("1511", "最终街道的数据是：" + s);
                    }
                });
    }
}
