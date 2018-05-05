package com.example.qrecyclerviewpaging;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = LayoutInflater.from(this).inflate(R.layout.activity_main, null);
        setContentView(view);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                RecyclerView recyclerView = findViewById(R.id.recyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                QPagingScrollHelper snapHelper = new QPagingScrollHelper();
                snapHelper.attachToRecyclerView(recyclerView);

                DisplayMetrics dm = new DisplayMetrics();
                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                wm.getDefaultDisplay().getMetrics(dm);
                recyclerView.setAdapter(new RecyclerViewAdapter(MainActivity.this, dm.widthPixels, dm.heightPixels - getStatusBarHeight() - getSupportActionBar().getHeight()));
            }
        }, 1000);
    }

    private int getStatusBarHeight() {
        int statusBarHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusBarHeight = getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }
}
