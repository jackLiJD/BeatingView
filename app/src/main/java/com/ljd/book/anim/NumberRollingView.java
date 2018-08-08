package com.ljd.book.anim;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 版权：XXX公司 版权所有
 * 作者：lijinduo
 * 版本：2.0
 * 创建日期：2018/8/7
 * 描述：(重构)
 * 修订历史：
 * 参考链接：
 */
public class NumberRollingView extends TextView{
    private static final int MONEY_TYPE = 0;
    private static final int NUM_TYPE = 1;
    private int frameNum;// 总共跳跃的帧数，默认30跳
    private int textType;// 内容的类型，默认是金钱类型
    private boolean useCommaFormat;// 是否使用每三位数字一个逗号的格式，让数字显得比较好看，默认使用
    private boolean runWhenChange;// 是否当内容有改变才使用动画,默认是

    private ExecutorService threadPool = Executors.newFixedThreadPool(1);// 1个线程的线程池
    private DecimalFormat formatter = new DecimalFormat("0.00");// 格式化金额，保留两位小数

    private double nowMoneyNum = 0.00;// 记录每帧增加后的金额数字
    private double finalMoneyNum;// 目标金额数字（最终的金额数字）

    private int nowNum;//记录每帧增加后的数字
    private int finalNum;//目标数字（最终的数字）
    private String preStr;
    public NumberRollingView(Context context) {
        super(context);
    }

    public NumberRollingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.NumberRollingView);
        frameNum = ta.getInt(R.styleable.NumberRollingView_frameNum, 70);
        textType = ta.getInt(R.styleable.NumberRollingView_textType, MONEY_TYPE);
        useCommaFormat = ta.getBoolean(R.styleable.NumberRollingView_useCommaFormat, true);
        runWhenChange = ta.getBoolean(R.styleable.NumberRollingView_runWhenChange, true);
    }
    /**
     * @Description 设置需要滚动的金钱(必须为正数)或整数(必须为正数)的字符串
     */
    public void setContent(String str) {
        useAnimByType(str);
    }

    private void useAnimByType(String str) {
        if (textType == MONEY_TYPE) {
            startMoneyAnim(str);
        } else {
            startNumAnim(str);
        }
    }

    /**
     * @Description 开始数字动画的方法
     */
    public void startNumAnim(String numStr) {
        // 如果传入的数字已经格式化了，则将包含的符号去除
        String num = numStr.replace(",", "").replace("-", "");
        try {
            finalNum = Integer.parseInt(num);
            if (finalNum < frameNum) {
                // 如果传入的数字比帧数小，则直接使用setText()
                NumberRollingView.this.setText(numStr);
                return;
            }
            // 默认从0开始动画
            nowNum = 0;
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    Message msg = handler.obtainMessage();
                    // 将传入的数字除以帧数，得到每帧间隔的大小
                    int temp = finalNum / frameNum;
                    msg.what = NUM_TYPE;
                    msg.obj = temp;
                    // 发送消息改变UI
                    handler.sendMessage(msg);
                }
            });
        } catch (NumberFormatException e) {
            e.printStackTrace();
            //如果转换Integer失败则直接用setText
            NumberRollingView.this.setText(numStr);
        }
    }

    /**
     * @Description 开始金额数字动画的方法
     */
    public void startMoneyAnim(String moneyStr) {
        // 如果传入的数字已经格式化了，则将包含的符号去除
        String money = moneyStr.replace(",", "").replace("-", "");
        try {
            finalMoneyNum = Double.parseDouble(money);
            if (finalMoneyNum == 0) {
                // 如果传入的数字为0则直接使用setText()进行显示
                NumberRollingView.this.setText(moneyStr);
                return;
            }
            nowMoneyNum = 0;
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    Message msg = handler.obtainMessage();
                    // 将传入的数字除以帧数，得到每帧间隔的大小
                    double size = finalMoneyNum / frameNum;
                    msg.what = MONEY_TYPE;
                    // 如果每帧的间隔小于0.01，则设置间隔为0.01
                    msg.obj = size < 0.01 ? 0.01 : size;
                    // 发送消息改变UI
                    handler.sendMessage(msg);
                }
            });
        } catch (NumberFormatException e) {
            e.printStackTrace();
            //如果转换Double失败则直接用setText()
            NumberRollingView.this.setText(moneyStr);
        }
    }




    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MONEY_TYPE://金额数字的滚动
                    //保留两位小数的字符串
                    String str = formatter.format(nowMoneyNum).toString();

                    // 更新显示的内容
                    if (useCommaFormat) {
                        //使用每三位数字一个逗号格式的字符串
                        String formatStr = addComma(str, true);
                        NumberRollingView.this.setText(formatStr);
                    } else {
                        NumberRollingView.this.setText(str);
                    }

                    //记录当前每帧递增后的数字
                    nowMoneyNum += (double) msg.obj;

                    if (nowMoneyNum < finalMoneyNum) {
                        //如果当前记录的金额数字小于目标金额数字，即还没达到目标金额数字，继续递增
                        Message msg2 = handler.obtainMessage();
                        msg2.what = MONEY_TYPE;
                        msg2.obj = msg.obj;
                        // 继续发送通知改变UI
                        handler.sendMessage(msg2);
                    } else {
                        //已经达到目标的金额数字，显示最终的数字
                        if (useCommaFormat) {
                            NumberRollingView.this.setText(addComma(formatter.format(finalMoneyNum), true));
                        } else {
                            NumberRollingView.this.setText(formatter.format(finalMoneyNum));
                        }
                    }
                    break;
                case NUM_TYPE://普通数字滚动
                    // 更新显示的内容
                    if (useCommaFormat) {
                        //使用每三位数字一个逗号格式的字符串
                        String formatStr = addComma(String.valueOf(nowNum), false);
                        NumberRollingView.this.setText(formatStr);
                    } else {
                        NumberRollingView.this.setText(String.valueOf(nowNum));
                    }

                    //记录当前每帧递增后的数字
                    nowNum += (Integer) msg.obj;
                    if (nowNum < finalNum) {
                        //如果当前记录的数字小于目标数字，即还没达到目标数字，继续递增
                        Message msg2 = handler.obtainMessage();
                        msg2.what = NUM_TYPE;
                        msg2.obj = msg.obj;
                        // 继续发送通知改变UI
                        handler.sendMessage(msg2);
                    } else {
                        //已经达到目标的数字，显示最终的内容
                        if (useCommaFormat) {
                            NumberRollingView.this.setText(addComma(String.valueOf(finalNum), false));
                        } else {
                            NumberRollingView.this.setText(String.valueOf(finalNum));
                        }
                    }
                    break;
            }
        }
    };

    /**
     * @param str       字符串只能为两位小数或者整数
     * @param isDecimal 是否是小数
     * @Description 格式化字符串，每三位用逗号隔开
     */
    public String addComma(String str, boolean isDecimal) {
        //先将字符串颠倒顺序
        str = new StringBuilder(str).reverse().toString();
        if (str.equals("0")) {
            return str;
        }
        String str2 = "";
        for (int i = 0; i < str.length(); i++) {
            if (i * 3 + 3 > str.length()) {
                str2 += str.substring(i * 3, str.length());
                break;
            }
            str2 += str.substring(i * 3, i * 3 + 3) + ",";
        }
        if (str2.endsWith(",")) {
            str2 = str2.substring(0, str2.length() - 1);
        }
        //最后再将顺序反转过来
        String temp = new StringBuilder(str2).reverse().toString();
        if (isDecimal) {
            //去掉最后的","
            return temp.substring(0, temp.lastIndexOf(",")) + temp.substring(temp.lastIndexOf(",") + 1, temp.length());
        } else {
            return temp;
        }
    }
}
