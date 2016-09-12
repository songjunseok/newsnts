package com.example.song.newsnts;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button button_js;
    Button button_sy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_js = (Button) findViewById(R.id.button_js);
        button_sy = (Button)findViewById(R.id.button_sy);

        button_js.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(getBaseContext(),com.example.song.newsnts.chat.class);
                startActivity(intent3);
            }
        });

        button_sy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(getBaseContext(),com.example.song.newsnts.SNS.class);
                startActivity(intent4);
            }
        });
}
