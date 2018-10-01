package com.example.damoyeo;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class NMapActivityPresenter {
    private NMapActivity view;// 뷰
    //모델은 각자 클래스 생성

    public NMapActivityPresenter(NMapActivity view){
        this.view = view;
        NMapFragment NMapFragment = new NMapFragment();
        NMapFragment.setArguments(new Bundle());
        FragmentManager fm = this.view.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.fragmentHere, NMapFragment);
        fragmentTransaction.commit();
    }
}