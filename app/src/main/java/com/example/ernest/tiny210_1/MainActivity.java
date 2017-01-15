package com.example.ernest.tiny210_1;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.UpdateAppearance;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    public Handler updateBitmapHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1://更新照片
                    img1.setImageBitmap((Bitmap) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };
    private final String DEBUG_TAG = "Activity01";
    Button bt;
    TextView text1;
    ImageView img1;
    Button bt2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //强制在主线程中执行网络访问
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        bt = (Button) findViewById(R.id.aj);
        bt2 = (Button) findViewById(R.id.aj2);
        text1 = (TextView) findViewById(R.id.text1);
        img1 = (ImageView) findViewById(R.id.img1);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDisplayInfomation();
                getDensity();
            }
        });
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new getVideo().start();
            }
        });

    }

    private void getDensity(){
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        Log.d(DEBUG_TAG,"Density is "+displayMetrics.density+" densityDpi is "+displayMetrics.densityDpi+" height: "+displayMetrics.heightPixels+
        " width: "+displayMetrics.widthPixels);
    }
    private void getDisplayInfomation() {
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        Log.d(DEBUG_TAG,"the screen size is "+point.toString());
    }


    public Bitmap byteArray2Bitmap(byte[] bytes,int image_length){
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,image_length);
        return bitmap;
        }
    class getVideo extends Thread{
        @Override
        public void run() {
            String httpUrl = "http://192.168.1.101/videostream.cgi?loginuse=admin&amp;loginpas=";
            //获得的数据
            String resultData = "";
            byte[] buffer = new byte[100000];
            byte[] unbuffer = new byte[60];
            String str_length = "";
            int num_cnt = 0;
            Bitmap bmp = null;
            URL url = null;
            try
            {
                //构造一个URL对象
                url = new URL(httpUrl);
            }
            catch (MalformedURLException e)
            {
                Log.e(DEBUG_TAG, "MalformedURLException");
            }
            if (url != null)
            {
                try
                {
                    // 使用HttpURLConnection打开连接
                    HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                    //得到读取的内容(流)
                    InputStream in = urlConn.getInputStream();
                    // 为输出创建BufferedReader
                    String inputLine = null;
                    //使用循环来读取获得的数据

                    while (true) {
                        for (int i=0;i<59;i++) {
                            unbuffer[i]=(byte)in.read();
                        }
                        for (int i = 0; i < 5; i++)
                        {
                            str_length += (char)in.read();
                        }
                        System.out.println("长度为："+str_length);
                        num_cnt=Integer.parseInt(str_length);
                        str_length = "";
                        for (int i = 0; i < 4; i++)
                        {
                            unbuffer[i] = (byte)in.read();
                            //System.out.println(unbuffer[i]);
                        }
                        for (int i = 0; i < num_cnt; i++)
                        {
                            buffer[i] = (byte)in.read();
                            //System.out.println(buffer[i]);
                        }
                        System.out.println("图片接收完毕");
                        unbuffer[1] = (byte)in.read();
                        unbuffer[1] = (byte)in.read();

                        bmp=byteArray2Bitmap(buffer,num_cnt+1);
                        //img1.setImageBitmap(bmp);//设置图片
                        Message msg = new Message();
                        msg.obj = bmp;
                        msg.what=1;
                        updateBitmapHandler.sendMessage(msg);
                    }


                    //照片收集完毕
//                        while (((inputLine = in.readLine()) != null))
//                        {
//                            //我们在每一行后面加上一个"\n"来换行
//                            resultData += inputLine + "\n";
//                            System.out.println("视频流:"+inputLine);
//                        }
                    //关闭InputStreamReader
                    //in.close();
                    //关闭http连接
                    //urlConn.disconnect();
                    //设置显示取得的内容
                    }
                catch (IOException e)
                {
                    Log.e(DEBUG_TAG, "IOException");
                }
            }
            else
            {
                Log.e(DEBUG_TAG, "Url NULL");
            }
        }
    }
}




