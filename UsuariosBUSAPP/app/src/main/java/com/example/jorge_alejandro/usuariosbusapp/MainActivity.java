package com.example.jorge_alejandro.usuariosbusapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button mapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mapa=(Button)findViewById(R.id.button1);
        mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent i=new Intent(MainActivity.this,MapsActivity.class);
            startActivity(i);

            }
        });

    }
}
