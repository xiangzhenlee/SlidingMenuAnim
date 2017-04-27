package com.yushan.slidingmenu;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private ImageView iv_menu;

    private MyLinearLayout dl_main;
    private SlideMenu slideMenu;
    private ArrayList<String> menuData;
    private ListView lv_menu;
    private MenuListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }

    private void initData(){
        menuData = new ArrayList<>();
        for (int i = 0; i < 50; i++){
            String menuStr = "第"+ (i + 1) +"个条目";
            menuData.add(menuStr);
        }

        adapter = new MenuListAdapter(this,menuData);
        lv_menu.setAdapter(adapter);
    }

    private void initView(){
        dl_main = (MyLinearLayout) findViewById(R.id.dl_main);
        slideMenu = (SlideMenu) findViewById(R.id.slideMenu);

        dl_main.setSlideMenu(slideMenu);
        slideMenu.setOnSlideStateChangeListener(new SlideMenu.OnSlideStateChangeListener() {
            @Override
            public void onOpen() {

            }

            @Override
            public void onClose() {
                // 借助NineOldAndroid的VIewPropertyAnimator
                ViewPropertyAnimator.animate(dl_main)
                        .translationX(5)
                        .setInterpolator(new CycleInterpolator(4))
                        .setDuration(500)
                        .start();
            }

            @Override
            public void onDragging(float fraction) {
                ViewHelper.setAlpha(dl_main, 1 - fraction/2);
            }
        });

        lv_menu = (ListView)findViewById(R.id.lv_menu);
        lv_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String str = menuData.get(position);
                Toast.makeText(MainActivity.this,str,Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_menu:
                break;
        }
    }


}
