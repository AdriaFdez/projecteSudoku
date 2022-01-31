package edu.fje.dam2.projectesudoku;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.w3c.dom.Text;

public class Victoria extends AppCompatActivity {

    private TextView difTV,tempsTV,ptsTV;
    private ContentResolver contentResolver;
    private int dif, puntuacio;
    private double difMultp;
    private long tempsSobrant, duracio;
    private String difText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        dif = intent.getIntExtra("dificultat",0);
        tempsSobrant = intent.getLongExtra("tempsSobrant",0);
        duracio = intent.getLongExtra("duracio",0);

        if(dif == 1){
            difText = "FACIL";
            difMultp = 1;
        }else if(dif == 2){
            difText = "MITJA";
            difMultp = 1.5;
        }else if(dif == 3){
            difText = "DIFICIL";
            difMultp = 2;
        }

        puntuacio = (int) ((tempsSobrant * difMultp) / 1000);

        setContentView(R.layout.victoria);
        difTV = (TextView) findViewById(R.id.diff);
        tempsTV = (TextView) findViewById(R.id.temps);
        ptsTV = (TextView) findViewById(R.id.puntuacio);

        difTV.setText("Dificultat " + difText);
        tempsTV.setText("Temps " + String.valueOf(duracio) +" segons");
        ptsTV.setText("PUNTUACIO TOTAL " + puntuacio);






        contentResolver = getContentResolver();
    }

}
