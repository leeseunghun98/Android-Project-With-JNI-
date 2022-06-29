package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import android.os.Bundle;

import java.io.ByteArrayOutputStream;
import java.util.Random;


public class game_main extends AppCompatActivity {

    static {
        System.loadLibrary("JNIDriver");
    }

    private native static int openDriver(String path);
    private native static void closeDriver();
    private native static void writeDriver(byte[] data, int length);
    private native static int openDriverseg(String path);
    private native static void closeDriverseg();
    private native static void writeDriverseg(byte[] data, int length);

    int data_int, i;
    boolean mThreadRun,mStart;
    SegmentThread mSegThread;
    int right_position = 0;
    int remain_move = 100;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_main);
        TextView text_main = findViewById(R.id.text_top);

        // m, n값 받아오기
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        int m = bundle.getInt("m");
        int n = bundle.getInt("n");
        int m_temp = m-1, n_temp = n-1, rand = 0;
        text_main.setText(m + " x " + n + " 퍼즐");

        // bitmap 받아오기
        byte[] arr = getIntent().getByteArrayExtra("image");
        Bitmap img_bitmap = BitmapFactory.decodeByteArray(arr, 0, arr.length);
        ImageView imggg = findViewById(R.id.img1);
        Bitmap slicedbit = null;
        Drawable temp;
        Random random = new Random();
        System.out.println(img_bitmap.getWidth() +"+"+ img_bitmap.getHeight());

        Drawable blck = getResources().getDrawable(R.color.black);

        LinearLayout vertical_layout = findViewById(R.id.vertical_layout);
        LinearLayout[] linearLayouts = new LinearLayout[n];
        Button[][] buttons = new Button[m][n];
        Drawable[][] d = new Drawable[m][n];
        //출처 : https://m.blog.naver.com/diceworld/220896628939
        LinearLayout.LayoutParams linearLayoutParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        for(int i = 0; i < n; i++) {
            linearLayouts[i] = new LinearLayout(this);
            linearLayouts[i].setOrientation(LinearLayout.HORIZONTAL);

            for (int j = 0; j < m; j++) {
                buttons[j][i] = new Button(this);

                slicedbit = Bitmap.createBitmap(img_bitmap, j * (img_bitmap.getWidth()/m), i * (img_bitmap.getHeight()/n)
                        , img_bitmap.getWidth()/m, img_bitmap.getHeight()/n);
                d[j][i] = new BitmapDrawable(getResources(), slicedbit);

                buttons[j][i].setBackground(d[j][i]);
                buttons[j][i].setLayoutParams(linearLayoutParams);
                buttons[j][i].setWidth(800 / m);
                buttons[j][i].setHeight(533 / n);
                buttons[j][i].setClickable(true);
                buttons[j][i].setId(10);

                if(j == m-1 && i == n-1){
                    buttons[j][i].setBackground(blck);
                }

                // Layout에 추가
                linearLayouts[i].addView(buttons[j][i]);
            }
            vertical_layout.addView(linearLayouts[i]);
        }

//        imggg.setImageBitmap(slicedbit);
        imggg.setImageBitmap(img_bitmap);

        // mix
        for(int i = 0; i < 100; i++){
            rand = random.nextInt(4);
            try{
                if(rand == 0){
                    swapButtonbg(buttons[m_temp][n_temp], buttons[m_temp-1][n_temp]);
                    m_temp = m_temp -1;
                }else if(rand == 1){
                    swapButtonbg(buttons[m_temp][n_temp], buttons[m_temp+1][n_temp]);
                    m_temp = m_temp +1;
                }else if(rand == 2){
                    swapButtonbg(buttons[m_temp][n_temp], buttons[m_temp][n_temp+1]);
                    n_temp = n_temp +1;
                }else{
                    swapButtonbg(buttons[m_temp][n_temp], buttons[m_temp][n_temp-1]);
                    n_temp = n_temp -1;
                }
            }catch (Exception e){
                continue;
            }
        }

        for(int i = 0; i < n; i++){
            for(int j = 0; j < m; j++){
                buttons[j][i].setOnClickListener(new btnOnClickListener(j, i){
                    @Override
                    public void onClick(View v){
                        try {
                            if(h != 0 && h != n-1){
                                if(w != 0 && w != m-1){
                                    if(buttons[w-1][h].getBackground() == blck){
                                        remain_move--;
                                        swapButtonbg(buttons[w][h], buttons[w-1][h]);
                                    } else if(buttons[w+1][h].getBackground() == blck){
                                        remain_move--;
                                        swapButtonbg(buttons[w][h], buttons[w+1][h]);
                                    } else if(buttons[w][h+1].getBackground() == blck){
                                        remain_move--;
                                        swapButtonbg(buttons[w][h], buttons[w][h+1]);
                                    } else if(buttons[w][h-1].getBackground() == blck){
                                        remain_move--;
                                        swapButtonbg(buttons[w][h], buttons[w][h-1]);
                                    }
                                } else if(w == 0){
                                    if(buttons[w+1][h].getBackground() == blck){
                                        remain_move--;
                                        swapButtonbg(buttons[w][h], buttons[w+1][h]);
                                    } else if(buttons[w][h+1].getBackground() == blck){
                                        remain_move--;
                                        swapButtonbg(buttons[w][h], buttons[w][h+1]);
                                    } else if(buttons[w][h-1].getBackground() == blck){
                                        remain_move--;
                                        swapButtonbg(buttons[w][h], buttons[w][h-1]);
                                    }
                                } else {
                                    if(buttons[w-1][h].getBackground() == blck){
                                        remain_move--;
                                        swapButtonbg(buttons[w][h], buttons[w-1][h]);
                                    } else if(buttons[w][h+1].getBackground() == blck){
                                        remain_move--;
                                        swapButtonbg(buttons[w][h], buttons[w][h+1]);
                                    } else if(buttons[w][h-1].getBackground() == blck){
                                        remain_move--;
                                        swapButtonbg(buttons[w][h], buttons[w][h-1]);
                                    }
                                }
                            } else if(h == 0){
                                if(w != 0 && w != m-1){
                                    if(buttons[w-1][h].getBackground() == blck){
                                        remain_move--;
                                        swapButtonbg(buttons[w][h], buttons[w-1][h]);
                                    } else if(buttons[w+1][h].getBackground() == blck){
                                        remain_move--;
                                        swapButtonbg(buttons[w][h], buttons[w+1][h]);
                                    } else if(buttons[w][h+1].getBackground() == blck){
                                        remain_move--;
                                        swapButtonbg(buttons[w][h], buttons[w][h+1]);
                                    }
                                } else if(w == 0){
                                    if(buttons[w+1][h].getBackground() == blck){
                                        remain_move--;
                                        swapButtonbg(buttons[w][h], buttons[w+1][h]);
                                    } else if(buttons[w][h+1].getBackground() == blck){
                                        remain_move--;
                                        swapButtonbg(buttons[w][h], buttons[w][h+1]);
                                    }
                                } else {
                                    if(buttons[w-1][h].getBackground() == blck){
                                        remain_move--;
                                        swapButtonbg(buttons[w][h], buttons[w-1][h]);
                                    } else if(buttons[w][h+1].getBackground() == blck){
                                        remain_move--;
                                        swapButtonbg(buttons[w][h], buttons[w][h+1]);
                                    }
                                }
                            } else{
                                if(w != 0 && w != m-1){
                                    if(buttons[w-1][h].getBackground() == blck){
                                        remain_move--;
                                        swapButtonbg(buttons[w][h], buttons[w-1][h]);
                                    } else if(buttons[w+1][h].getBackground() == blck){
                                        remain_move--;
                                        swapButtonbg(buttons[w][h], buttons[w+1][h]);
                                    } else if(buttons[w][h-1].getBackground() == blck){
                                        remain_move--;
                                        swapButtonbg(buttons[w][h], buttons[w][h-1]);
                                    }
                                } else if(w == 0){
                                    if(buttons[w+1][h].getBackground() == blck){
                                        remain_move--;
                                        swapButtonbg(buttons[w][h], buttons[w+1][h]);
                                    } else if(buttons[w][h-1].getBackground() == blck){
                                        remain_move--;
                                        swapButtonbg(buttons[w][h], buttons[w][h-1]);
                                    }
                                } else {
                                    if(buttons[w-1][h].getBackground() == blck){
                                        remain_move--;
                                        swapButtonbg(buttons[w][h], buttons[w-1][h]);
                                    } else if(buttons[w][h-1].getBackground() == blck){
                                        remain_move--;
                                        swapButtonbg(buttons[w][h], buttons[w][h-1]);
                                    }
                                }
                            }
                        }catch (Exception e){
                            return;
                        }finally {
                            data_int = remain_move;
                            mStart = true;
                            right_position = 0;
                            for(int i = 0; i < n; i++){
                                for(int j = 0; j < m; j++){
                                    if(buttons[j][i].getBackground() == d[j][i]) {
                                        right_position++;
                                    }
                                }
                            }
                            int right_nums = m * n - 1 - right_position;
                            byte[] data = {0,0,0,0,0,0,0,0};
                            for(int k = 0; k < right_nums; k++){
                                if(k < 8){
                                    data[k] = 1;
                                }
                            }
                            writeDriver(data, data.length);
                            if(right_position == m * n - 1){
                                text_main.setText("성공하였습니다!");
                            }
                        }
                    }
                });
            }
        }
    }

    public abstract class btnOnClickListener implements View.OnClickListener {
        protected int w, h;
        public btnOnClickListener(int w, int h) {
            this.w = w;
            this.h = h;
        }
    }

    public void swapButtonbg(Button a, Button b){
        Drawable tmp = a.getBackground();
        a.setBackground(b.getBackground());
        b.setBackground(tmp);
    }

    private class SegmentThread extends Thread{
        @Override
        public void run(){
            super.run();
            while(mThreadRun) {
                byte[] n = {0, 0, 0, 0, 0, 0, 0};

                if (mStart == false) {
                    writeDriverseg(n, n.length);
                } else {
                    for (i = 0; i < 100; i++) {
                        n[0] = (byte) (data_int % 1000000 / 100000);
                        n[1] = (byte) (data_int % 100000 / 10000);
                        n[2] = (byte) (data_int % 10000 / 1000);
                        n[3] = (byte) (data_int % 1000 / 100);
                        n[4] = (byte) (data_int % 100 / 10);
                        n[5] = (byte) (data_int % 10);
                        writeDriverseg(n, n.length);
                    }
                }
            }
        }
    }
    @Override
    protected void onPause(){
        // TODO Auto-generated method stub
        closeDriver();
        closeDriverseg();
        mThreadRun=false;
        mSegThread=null;
        super.onPause();
    }

    @Override
    protected void onResume(){
        if(openDriver("/dev/sm9s5422_led")<0){
            Toast.makeText(game_main.this, "Driver Open Failed", Toast.LENGTH_SHORT).show();
        }
        if(openDriverseg("/dev/sm9s5422_segment")<0){
            Toast.makeText(game_main.this, "Driver Open Failed", Toast.LENGTH_SHORT).show();
        }
        mThreadRun=true;
        mSegThread=new SegmentThread();
        mSegThread.start();

        super.onResume();
    }
}