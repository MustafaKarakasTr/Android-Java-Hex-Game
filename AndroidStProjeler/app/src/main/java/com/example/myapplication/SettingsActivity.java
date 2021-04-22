package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }
    public void takeSize(View v)
    {
        //Intent i = new Intent(this,)
        //Integer size =Integer.parseInt (((Button)findViewById(R.id.sizeTextField)).getText().toString());
        String size = ((EditText)findViewById(R.id.sizeTextField)).getText().toString();
        Intent i = new Intent(this,takeGameType.class);
        i.putExtra("Size",size);
        startActivity(i);
        //int _size = Integer.parseInt(size);

        /*Button b = (Button)v;
        b.setText(size);
        */
        //System.out.println(size);


    }
}