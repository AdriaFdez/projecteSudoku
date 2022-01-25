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
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import edu.fje.dam2.projectesudoku.R;

/**
 * Activitat que demostra el funcionament d'afegir al
 * calendari un "event", posteriorment els consulta.
 * Cal afegir els permissos WRITE_CALENDAR i READ_CALENDAR
 * al fitxer de manifest
 *
 * @author sergi.grau@fje.edu
 * @version 1.0 10.01.2015
 * @version 2.0, 1/10/2020 actualització a API30
 */
public class Sudoku extends AppCompatActivity {

    private ContentResolver contentResolver;
    private Set<String> calendaris = new HashSet<String>();
    private List<String> events = new ArrayList<String>();
    private static final int PERMISSIONS_REQUEST_READ_CALENDARS = 100;
    private static final int PERMISSIONS_REQUEST_WRITE_CALENDARS = 200;

    private List<String> sudokus = Arrays.asList("sudoku1", "sudoku2", "sudoku3");
    private String[] numerosSudoku;
    private String[] numerosPartida;
    private ArrayList<Integer> posicionsIncorrecte;
    private EditText a1,a2,a3,a4,a5,a6,a7,a8,a9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        int diffSelector = intent.getIntExtra("dificultat",0);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CALENDAR)) {


            } else {


                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CALENDAR},
                        PERMISSIONS_REQUEST_READ_CALENDARS);
            }
        }


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_CALENDAR)) {


            } else {


                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_CALENDAR},
                        PERMISSIONS_REQUEST_WRITE_CALENDARS);
            }
        }

        setContentView(R.layout.sudoku);
        generarSudoku(diffSelector);

        a1 = (EditText) findViewById(R.id.a1);
        if(!numerosPartida[0].equals("0")) {
            a1.setText(numerosPartida[0]);
            a1.setEnabled(false);
        }

        a2 = (EditText) findViewById(R.id.a2);
        if(!numerosPartida[1].equals("0")) {
            a2.setText(numerosPartida[1]);
            a2.setEnabled(false);
        }

        a3 = (EditText) findViewById(R.id.a3);
        if(!numerosPartida[2].equals("0")) {
            a3.setText(numerosPartida[2]);
            a3.setEnabled(false);
        }

        a4 = (EditText) findViewById(R.id.a4);
        if(!numerosPartida[3].equals("0")) {
            a4.setText(numerosPartida[3]);
            a4.setEnabled(false);
        }

        a5 = (EditText) findViewById(R.id.a5);
        if(!numerosPartida[4].equals("0")) {
            a5.setText(numerosPartida[4]);
            a5.setEnabled(false);
        }

        a6 = (EditText) findViewById(R.id.a6);
        if(!numerosPartida[5].equals("0")) {
            a6.setText(numerosPartida[5]);
            a6.setEnabled(false);
        }

        a7 = (EditText) findViewById(R.id.a7);
        if(!numerosPartida[6].equals("0")) {
            a7.setText(numerosPartida[6]);
            a7.setEnabled(false);
        }

        a8 = (EditText) findViewById(R.id.a8);
        if(!numerosPartida[7].equals("0")) {
            a8.setText(numerosPartida[7]);
            a8.setEnabled(false);
        }

        a9 = (EditText) findViewById(R.id.a9);
        if(!numerosPartida[8].equals("0")) {
            a9.setText(numerosPartida[8]);
            a9.setEnabled(false);
        }


        contentResolver = getContentResolver();
    }

    public void generarSudoku(Integer diffSelector){
        Log.i("GENERASUDOKU", String.valueOf(diffSelector));
        Random nSudoku = new Random();
        int index = nSudoku.nextInt(sudokus.size());

        if(index == 0){
            numerosSudoku = getResources().getStringArray(R.array.sudoku1);
        }else if(index == 1){
            numerosSudoku = getResources().getStringArray(R.array.sudoku2);
        }else if(index == 2){
            numerosSudoku = getResources().getStringArray(R.array.sudoku3);
        }

        Log.i("NSUDOKU", String.valueOf(index));

        int contNumerosEliminar = 0;
        if(diffSelector == 1){
            contNumerosEliminar = 10;
        }
        if(diffSelector == 2){
            contNumerosEliminar = 20;
        }
        if(diffSelector == 3){
            contNumerosEliminar = 50;
        }

        numerosPartida = creaSudoku(numerosSudoku, contNumerosEliminar);
    }

    public String[] creaSudoku(String[] sudoku, int contNumerosEliminar){
        Log.i("CREASUDOKU", String.valueOf(contNumerosEliminar));
        List<String> posicionsSudoku = Arrays.asList(sudoku);
        List<String> copy = new ArrayList<>(posicionsSudoku);

        Set<Integer> generated = new LinkedHashSet<Integer>();

        while(generated.size() < contNumerosEliminar){
            Random value = new Random();
            int p = value.nextInt(81-1) + 1;

            generated.add(p);
        }

        for(int pos:generated){
            copy.set(pos,"0");
        }

        String[] partida = copy.toArray(new String[0]);
        return partida;
    }

    public void onClickCmp(View view) {
        boolean correcte = true;

        for(int i = 0; i<numerosSudoku.length; i++){
            String numS = numerosSudoku[i];
            String numP = numerosPartida[i];
            Log.i(String.valueOf(numS),String.valueOf(numP));

            if(!numS.equals(numP)){
                correcte = false;
                //posicionsIncorrecte.add(67);
            }
        }

        if(correcte) {
            //SQLITE


            //CALENDARI
            afegirEvent();          //CREA L'EVENT
            obtenirEvents();
            Log.i("Events ", events.toString());        //OBTENIM I MOSTREM ELS EVENTS
        }else{
            //MOSTRAR POSICIONES INCORRECTAS?
            //posicionsIncorrectes
            Toast.makeText(getApplicationContext(), "Sudoku incorrecte, continua intentant...",
                    Toast.LENGTH_SHORT).show();
        }
        }

    /**
     * Mètode que permet afegir un event a un calendari de l'usuari
     */
    private void afegirEvent() {

        ContentValues esdeveniment = new ContentValues();
        esdeveniment.put(CalendarContract.Events.CALENDAR_ID, 1); // Tipus de calendari
        esdeveniment.put(CalendarContract.Events.TITLE, "Partida de sudoku");
        esdeveniment.put(CalendarContract.Events.DTSTART, Calendar.getInstance().getTimeInMillis());
        esdeveniment.put(CalendarContract.Events.DTEND, Calendar.getInstance().getTimeInMillis());
        esdeveniment.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Madrid");
        Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, esdeveniment);

        // La URI conté el contentProvider i retorna el id del event creat
        int id = Integer.parseInt(uri.getLastPathSegment());
        Toast.makeText(getApplicationContext(), "Partida guardada amb codi" + id,
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Mètode que recupera determinats events d'un calendari.
     * Filtra pel titol del esdeveniment
     */
    private void obtenirEvents() {
        events.clear();
        Uri uri = CalendarContract.Events.CONTENT_URI;
        String seleccio = String.format("(%s = ?)", CalendarContract.Events.TITLE);
        String[] seleccioArgs = new String[]{"Partida de sudoku"};
        String[] projeccio = new String[]{
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DTSTART
        };
        Cursor cursor = contentResolver.query(uri, projeccio, seleccio, seleccioArgs, null);
        while (cursor.moveToNext()) {
            long id = cursor.getLong(0);
            String titol = cursor.getString(1);
            events.add(titol);
        }
    }

}