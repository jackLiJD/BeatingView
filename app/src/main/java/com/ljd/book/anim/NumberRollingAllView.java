package com.ljd.book.anim;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 版权：XXX公司 版权所有
 * 作者：lijinduo
 * 版本：2.0
 * 创建日期：2018/8/7
 * 描述：(重构)
 * 修订历史：
 * 参考链接：
 */
public class NumberRollingAllView extends TextView{
    int initial=0;
    private List<String> list=new ArrayList<>();
    private boolean run=true;
    public NumberRollingAllView(Context context) {
        super(context);
    }

    public NumberRollingAllView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setContent(double number){
        initial=0;
        run=true;
        String numberStr=number+"";
        if (number == 0||number<0) {
            setText(numberStr);
            return;
        }
        int length = numberStr.length();
        list.clear();
        for (int i = 0; i < length; i++) {
            list.add(numberStr.substring(i,i+1));
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (run){
                    handler.sendEmptyMessage(0x123);
                    try {
                        Thread.sleep(65);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();


    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            initial++;
            if (initial == 9) {
                run=false;
            }
            if (msg.what == 0x123) {
                String string="";
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).equals(",")||list.get(i).equals(".")) {
                        string=string+list.get(i);
                    }else{
                        if (Double.valueOf(list.get(i))<initial) {
                            string=string+list.get(i);
                        }else{
                            string=string+initial;
                        }

                    }
                    setText(string);
                }
            }

        }


    };












}
