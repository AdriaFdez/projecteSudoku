package edu.fje.dam2.projectesudoku;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Victoria extends AppCompatActivity {

    private ContentResolver contentResolver;
    private Set<String> calendaris = new HashSet<String>();
    private List<String> events = new ArrayList<String>();
    private static final int PERMISSIONS_REQUEST_READ_CALENDARS = 100;
    private static final int PERMISSIONS_REQUEST_WRITE_CALENDARS = 200;

    private TextView difTV,tempsTV,ptsTV, calendarTV;
    private int dif, puntuacio;
    private double difMultp;
    private long tempsSobrant, duracio;
    private String difText;
    static final int EVENT_AFEGIT = 1;
    private Button boto;


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
        ptsTV.setText("PUNTUACIO " + puntuacio +"pts");

        calendarTV = (TextView) findViewById(R.id.calendarTV);
        calendarTV.setVisibility(View.GONE);

        contentResolver = getContentResolver();
    }

    public void onClickCalendar(View view) {
        afegirEvent(puntuacio);          //CREA L'EVENT

    }

    public void onClickBnv(View view) {
        Intent intentBnv = new Intent(this, Benvinguda.class);
        startActivity(intentBnv);
        contentResolver = getContentResolver();
    }

    private void afegirEvent(int pts) {

        Calendar calendari = Calendar.getInstance();
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", calendari.getTimeInMillis());
        intent.putExtra("allDay", true);
        intent.putExtra("rrule", "FREQ=NONE");
        intent.putExtra("endTime", calendari.getTimeInMillis() + 60 * 60 * 1000);
        intent.putExtra("title", "Partida sudoku");
        intent.putExtra("description", "Puntuacio: " + pts);
        intent.putExtra("eventLocation", "BARCELONA");
        startActivityForResult(intent,EVENT_AFEGIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EVENT_AFEGIT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "s'ha afegit la partida", Toast.LENGTH_SHORT)
                        .show();
            }
        }
        boto = (Button) findViewById(R.id.calendar);
        boto.setVisibility(View.GONE);

        calendarTV.setVisibility(View.VISIBLE);
    }


}
