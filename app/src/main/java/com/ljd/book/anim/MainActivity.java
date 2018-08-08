package com.ljd.book.anim;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    private NumberRollingView tvMoney;
    private NumberRollingAllView numberrolling;
    private NumberRollingMoveView rolling;
    private Button go;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvMoney = (NumberRollingView) findViewById(R.id.tv_money);
        tvMoney.setContent("86366.8");
        numberrolling = findViewById(R.id.numberrolling);
        numberrolling.setContent(2973607.58);
        rolling = findViewById(R.id.rolling);
        rolling.startRolling("8175109546.26");
        go=findViewById(R.id.go);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rolling.startRolling("8175109.26");
                numberrolling.setContent(2973607.58);
                tvMoney.setContent("86366.8");
            }
        });
    }
}
