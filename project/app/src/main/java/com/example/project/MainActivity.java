package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.View;
import android.widget.*;
import android.widget.FrameLayout;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;

import java.io.ByteArrayOutputStream;


public class MainActivity extends AppCompatActivity implements JNIListener{
    private Camera mCamera;
    private camera_ mPreview;
    private ImageView capturedImageHolder;
    private Bitmap img1;
    TextView tv;
    int m, n;
    int captured = 0;

    JNIDriver mDriver;

    static {
        System.loadLibrary("JNIDriver");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.text_mn);

        Button caputre_btn = (Button)findViewById(R.id.button_capture);
        Button submit_btn = (Button)findViewById(R.id.button_submit);

        capturedImageHolder = (ImageView)findViewById(R.id.captured_image);
        // Create an instance of Camera
        mCamera = getCameraInstance();
        mCamera.setDisplayOrientation(180);
        // Create out Preview view and set it as the content of our activity.
        mPreview = new camera_(this, mCamera);
        FrameLayout preview = (FrameLayout)findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        // GPIO
        mDriver = new JNIDriver();
        mDriver.setListener(this);
        if(mDriver.open("/dev/sm9s5422_interrupt")< 0){
            Toast.makeText(MainActivity.this,"Driver Open Failed", Toast.LENGTH_SHORT).show();
        }

        m = 2;
        n = 2;

        caputre_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mCamera.takePicture(null, null, pictureCallback);
                captured = 1;
            }
        });
        //출처 : https://kworks.tistory.com/377

        submit_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(captured == 1){
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    img1.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    Intent intent = new Intent(MainActivity.this, game_main.class);
                    intent.putExtra("m", m);
                    intent.putExtra("n", n);
                    intent.putExtra("image", byteArray);

                    startActivity(intent);
                } else {
                    tv.setText("사진을 찍어주세요");
                }
            }
        });
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            // Camera is not available
        }
        return c;
    }

    PictureCallback pictureCallback = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();

            Matrix mtx = new Matrix();
            mtx.postRotate(180);
            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);

            if(bitmap == null){
                Toast.makeText(MainActivity.this, "Captured image is empty", Toast.LENGTH_LONG).show();
                return;
            }
            img1 = scaleDownBitmapImage(rotatedBitmap, 450, 300);
            capturedImageHolder.setImageBitmap(img1);
        }
    };
    private Bitmap scaleDownBitmapImage(Bitmap bitmap, int newWidth, int newHeigth){
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeigth, true);
        return resizedBitmap;
    }

    @Override
    protected void onPause(){
        super.onPause();
        releaseMediaRecorder();
        releaseCamera();
        mDriver.close();
    }

    private void releaseMediaRecorder(){mCamera.lock();}

    private void releaseCamera(){
        if(mCamera != null){
            mCamera.release();
            mCamera = null;
        }
    }
    public Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.arg1){
                case 1:
                    n++;
                    tv.setText("가로 : " + m + " , 세로 : " + n);
                    break;
                case 2:
                    if(n>2){
                        n--;
                        tv.setText("가로 : " + m + " , 세로 : " + n);
                    }
                    break;
                case 3:
                    if(m>2){
                        m--;
                        tv.setText("가로 : " + m + " , 세로 : " + n);
                    }
                    break;
                case 4:
                    m++;
                    tv.setText("가로 : " + m + " , 세로 : " + n);
                    break;
                case 5:
                    tv.setText("Retry");
                    break;
            }
        }
    };
    @Override
    protected void onResume(){
        super.onResume();
    }
    @Override
    public void onReceive(int val) {
        Message text = Message.obtain();
        text.arg1=val;
        handler.sendMessage(text);
    }
}