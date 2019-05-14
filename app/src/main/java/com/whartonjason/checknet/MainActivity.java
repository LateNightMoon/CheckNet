package com.whartonjason.checknet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.sudoku.checknet.annotation.CheckNet;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @CheckNet
    public void click(View view) {
        Toast.makeText(this, "msg", Toast.LENGTH_SHORT).show();
    }

}
