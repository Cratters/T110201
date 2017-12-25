package com.example.auser.t110201;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    File imgFile;
    ImageView imageView,imageView2;
    ProgressBar progressBar,progressBar2;
    TextView textView;
    Context context = this;
    int readSum =0;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  //隱藏狀態列
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        textView = (TextView) findViewById(R.id.textView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar2  = (ProgressBar) findViewById(R.id.progressBar2);
        imgFile = new File(getFilesDir() + File.separator + "Download.jpg");
        handler = new Handler();

    }

    class MyTask extends AsyncTask<String, Integer, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bmp=null;
            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                byte b[] = new byte[1024];
                final int fullSize = conn.getContentLength();
                int readSize;
                while ((readSize = is.read(b)) != -1) {
                    os.write(b, 0, readSize);
                    readSum += readSize;
                    Log.d("NET","read" + readSum);
                    publishProgress(readSum, 100 * readSum / fullSize);
                }
                byte result[] = os.toByteArray();
                bmp = BitmapFactory.decodeByteArray(result, 0, result.length);
                FileOutputStream fos = new FileOutputStream(imgFile);
                fos.write(result);
                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bmp;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            textView.setText(String.valueOf(values[1]));
            progressBar2.setProgress(values[1]);

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            progressBar.setVisibility(View.INVISIBLE);
            imageView.setImageBitmap(bitmap);
        }
    }

    public void getData(View v) {

        MyTask task = new MyTask();
        task.execute("https://i.ytimg.com/vi/mm_M7TE2qJ0/maxresdefault.jpg");
        progressBar.setVisibility(View.VISIBLE);
        readSum = 0;

    }

    public void getImg(View v) throws FileNotFoundException {

        if(imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView2.setImageBitmap(bitmap);
        }
    }
}
