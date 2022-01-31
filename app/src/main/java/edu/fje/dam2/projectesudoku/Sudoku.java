package edu.fje.dam2.projectesudoku;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.CalendarContract;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TableLayout;
import android.widget.TableRow;
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

    private int diffSelector;
    private List<String> sudokus = Arrays.asList("sudoku1", "sudoku2", "sudoku3");
    private String[] numerosSudoku;
    private String[] numerosPartida;
    private ArrayList<Integer> posicionsIncorrecte;
    private EditText numSudoku, numText;
    private Chronometer simpleChronometer;
    private long tempsMaxim = 1800000;          //30 min en miliseconds

    TableLayout tl1;
    TableRow tr1;
    EditText et1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        diffSelector = intent.getIntExtra("dificultat",0);

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
        escullSudoku(diffSelector);

        simpleChronometer = (Chronometer) findViewById(R.id.simpleChronometer); // initiate a chronometer

        simpleChronometer.start();
        dibuixarSudoku();




        contentResolver = getContentResolver();
    }

    public void escullSudoku(Integer diffSelector){
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

        long temps = SystemClock.elapsedRealtime() - simpleChronometer.getBase();
        long duracio = temps/1000;

        long tpsSobrant;

        if(temps > tempsMaxim) {    //si trigues mes de 30 min
            tpsSobrant = 1000;
        } else {
            tpsSobrant = tempsMaxim - temps;
        }

        Intent intentVct = new Intent(this, Victoria.class);
        intentVct.putExtra("dificultat", diffSelector);
        intentVct.putExtra("tempsSobrant", tpsSobrant);
        intentVct.putExtra("duracio", duracio);
        startActivity(intentVct);

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

    public void dibuixarSudoku() {

        tl1 = findViewById(R.id.tl1);

        ArrayList<String> arr1 = new ArrayList<String>();
        arr1.add("03");
        arr1.add("04");
        arr1.add("05");
        arr1.add("13");
        arr1.add("14");
        arr1.add("15");
        arr1.add("23");
        arr1.add("24");
        arr1.add("25");

        arr1.add("30");
        arr1.add("31");
        arr1.add("32");
        arr1.add("40");
        arr1.add("41");
        arr1.add("42");
        arr1.add("50");
        arr1.add("51");
        arr1.add("52");

        arr1.add("36");
        arr1.add("37");
        arr1.add("38");
        arr1.add("46");
        arr1.add("47");
        arr1.add("48");
        arr1.add("56");
        arr1.add("57");
        arr1.add("58");

        arr1.add("63");
        arr1.add("64");
        arr1.add("65");
        arr1.add("73");
        arr1.add("74");
        arr1.add("75");
        arr1.add("83");
        arr1.add("84");
        arr1.add("85");

        String cellID = "";

        int contadorCelda = 0;
        for (int countFilas = 0; countFilas < 9; countFilas++) {
            tr1 = new TableRow(this);
            for (int countColumnas = 0; countColumnas < 9; countColumnas++) {
                et1 = new EditText(this);
                et1.setInputType(InputType.TYPE_CLASS_NUMBER);
                et1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                et1.setHeight(110);
                et1.setWidth(110);
                cellID = "" + countFilas + countColumnas;

                if (!numerosPartida[contadorCelda].equals("0")) {
                    et1.setText(numerosPartida[contadorCelda]);
                    et1.setEnabled(false);
                } else {
                    et1.setText("");
                }
                contadorCelda++;

                et1.setId(Integer.parseInt(cellID));
                //dictID.put(cellID, Integer.parseInt(cellID));


                if (arr1.contains(cellID)) et1.setBackgroundResource(R.drawable.cell2);
                else et1.setBackgroundResource(R.drawable.cell);

                tr1.addView(et1);
            }
            tl1.addView(tr1);

        }
    }

}