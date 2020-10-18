package com.test.bigimageview;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private MyBigImageView bigView;
    private ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bigView = findViewById(R.id.bigView);
        image = findViewById(R.id.image);
        //  内存复用模式加载大图
        try {
            InputStream inputStream = getAssets().open("bigpicture.png");
            bigView.setPicture(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*//  传统方式加载大图,注意：因为本项目中图片尺寸较大，可能会引起oom，闪退请查看log日志
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bigpicture, null);
        image.setImageBitmap(bitmap);*/
    }


}