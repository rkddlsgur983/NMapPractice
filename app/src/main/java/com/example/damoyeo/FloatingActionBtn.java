package com.example.damoyeo;

import android.support.design.widget.FloatingActionButton;
import android.view.animation.Animation;

public class FloatingActionBtn {
    private Animation fabOpen, fabClose;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2;

    public Animation getFabOpen() {
        return fabOpen;
    }

    public void setFabOpen(Boolean fabOpen) {
        isFabOpen = fabOpen;
    }

    public FloatingActionButton getFab() {
        return fab;
    }

    public void setFab(FloatingActionButton fab) {
        this.fab = fab;
    }

    public FloatingActionButton getFab1() {
        return fab1;
    }

    public void setFab1(FloatingActionButton fab1) {
        this.fab1 = fab1;
    }

    public FloatingActionButton getFab2() {
        return fab2;
    }

    public void setFab2(FloatingActionButton fab2) {
        this.fab2 = fab2;
    }

    public void setFabOpen(Animation fabOpen) {
        this.fabOpen = fabOpen;
    }

    public Animation getFabClose() {
        return fabClose;
    }

    public void setFabClose(Animation fabClose) {
        this.fabClose = fabClose;
    }

    public void anim() {
        if (isFabOpen) {
            fab1.startAnimation(fabClose);
            fab2.startAnimation(fabClose);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;
        } else {
            fab1.startAnimation(fabOpen);
            fab2.startAnimation(fabOpen);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
        }
    }
}
