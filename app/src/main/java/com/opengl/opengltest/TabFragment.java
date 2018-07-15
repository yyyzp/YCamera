package com.opengl.opengltest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Answer on 2018/4/28.
 */

public class TabFragment  extends Fragment{
    private String mTitle;
    private RecyclerView recyclerView;
    public void setTitle(String title) {
        this.mTitle = title;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


//        TextView textView = new TextView(getContext());
//        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
//        textView.setGravity(Gravity.CENTER);
//        textView.setText(mTitle);
//        return textView;
        return inflater.inflate(R.layout.fragment,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}