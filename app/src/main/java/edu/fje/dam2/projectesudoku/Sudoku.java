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
    private EditText numSudoku, numText;

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
        escullSudoku(diffSelector);

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

    public void dibuixarSudoku(){

        escriuNum(numSudoku,R.id.a1,numerosPartida[0]);
        escriuNum(numSudoku,R.id.a2,numerosPartida[1]);
        escriuNum(numSudoku,R.id.a3,numerosPartida[2]);
        escriuNum(numSudoku,R.id.a4,numerosPartida[3]);
        escriuNum(numSudoku,R.id.a5,numerosPartida[4]);
        escriuNum(numSudoku,R.id.a6,numerosPartida[5]);
        escriuNum(numSudoku,R.id.a7,numerosPartida[6]);
        escriuNum(numSudoku,R.id.a8,numerosPartida[7]);
        escriuNum(numSudoku,R.id.a9,numerosPartida[8]);

        escriuNum(numText,R.id.a10,numerosPartida[9]);
        escriuNum(numText,R.id.a11,numerosPartida[10]);
        escriuNum(numText,R.id.a12,numerosPartida[11]);
        escriuNum(numText,R.id.a13,numerosPartida[12]);
        escriuNum(numText,R.id.a14,numerosPartida[13]);
        escriuNum(numText,R.id.a15,numerosPartida[14]);
        escriuNum(numText,R.id.a16,numerosPartida[15]);
        escriuNum(numText,R.id.a17,numerosPartida[16]);
        escriuNum(numText,R.id.a18,numerosPartida[17]);

        escriuNum(numText,R.id.a19,numerosPartida[18]);
        escriuNum(numText,R.id.a20,numerosPartida[19]);
        escriuNum(numText,R.id.a21,numerosPartida[20]);
        escriuNum(numText,R.id.a22,numerosPartida[21]);
        escriuNum(numText,R.id.a23,numerosPartida[22]);
        escriuNum(numText,R.id.a24,numerosPartida[23]);
        escriuNum(numText,R.id.a25,numerosPartida[24]);
        escriuNum(numText,R.id.a26,numerosPartida[25]);
        escriuNum(numText,R.id.a27,numerosPartida[26]);

        escriuNum(numText,R.id.a28,numerosPartida[27]);
        escriuNum(numText,R.id.a29,numerosPartida[28]);
        escriuNum(numText,R.id.a30,numerosPartida[29]);
        escriuNum(numText,R.id.a31,numerosPartida[30]);
        escriuNum(numText,R.id.a32,numerosPartida[31]);
        escriuNum(numText,R.id.a33,numerosPartida[32]);
        escriuNum(numText,R.id.a34,numerosPartida[33]);
        escriuNum(numText,R.id.a35,numerosPartida[34]);
        escriuNum(numText,R.id.a36,numerosPartida[35]);

        escriuNum(numText,R.id.a37,numerosPartida[36]);
        escriuNum(numText,R.id.a38,numerosPartida[37]);
        escriuNum(numText,R.id.a39,numerosPartida[38]);
        escriuNum(numText,R.id.a40,numerosPartida[39]);
        escriuNum(numText,R.id.a41,numerosPartida[40]);
        escriuNum(numText,R.id.a42,numerosPartida[41]);
        escriuNum(numText,R.id.a43,numerosPartida[42]);
        escriuNum(numText,R.id.a44,numerosPartida[43]);
        escriuNum(numText,R.id.a45,numerosPartida[44]);

        escriuNum(numSudoku,R.id.a46,numerosPartida[45]);
        escriuNum(numSudoku,R.id.a47,numerosPartida[46]);
        escriuNum(numSudoku,R.id.a48,numerosPartida[47]);
        escriuNum(numSudoku,R.id.a49,numerosPartida[48]);
        escriuNum(numSudoku,R.id.a50,numerosPartida[49]);
        escriuNum(numSudoku,R.id.a51,numerosPartida[50]);
        escriuNum(numSudoku,R.id.a52,numerosPartida[51]);
        escriuNum(numSudoku,R.id.a53,numerosPartida[52]);
        escriuNum(numSudoku,R.id.a54,numerosPartida[53]);
        escriuNum(numSudoku,R.id.a55,numerosPartida[54]);
        escriuNum(numSudoku,R.id.a56,numerosPartida[55]);
        escriuNum(numSudoku,R.id.a57,numerosPartida[56]);
        escriuNum(numSudoku,R.id.a58,numerosPartida[57]);
        escriuNum(numSudoku,R.id.a59,numerosPartida[58]);
        escriuNum(numSudoku,R.id.a60,numerosPartida[59]);
        escriuNum(numSudoku,R.id.a61,numerosPartida[60]);
        escriuNum(numSudoku,R.id.a62,numerosPartida[61]);
        escriuNum(numSudoku,R.id.a63,numerosPartida[62]);
        escriuNum(numSudoku,R.id.a64,numerosPartida[63]);
        escriuNum(numSudoku,R.id.a65,numerosPartida[64]);
        escriuNum(numSudoku,R.id.a66,numerosPartida[65]);
        escriuNum(numSudoku,R.id.a67,numerosPartida[66]);

        escriuNum(numSudoku,R.id.a68,numerosPartida[67]);
        escriuNum(numSudoku,R.id.a69,numerosPartida[68]);
        escriuNum(numSudoku,R.id.a70,numerosPartida[69]);
        escriuNum(numSudoku,R.id.a71,numerosPartida[70]);
        escriuNum(numSudoku,R.id.a72,numerosPartida[71]);
        escriuNum(numSudoku,R.id.a73,numerosPartida[72]);
        escriuNum(numSudoku,R.id.a74,numerosPartida[73]);

        escriuNum(numSudoku,R.id.a75,numerosPartida[74]);
        escriuNum(numSudoku,R.id.a76,numerosPartida[75]);
        escriuNum(numSudoku,R.id.a77,numerosPartida[76]);
        escriuNum(numSudoku,R.id.a78,numerosPartida[77]);
        escriuNum(numSudoku,R.id.a79,numerosPartida[78]);

        escriuNum(numSudoku,R.id.a80,numerosPartida[79]);
        escriuNum(numSudoku,R.id.a81,numerosPartida[80]);

    }

    public void escriuNum(EditText aPos, int id, String num){
        aPos = (EditText) findViewById(id);
        if(!num.equals("0")) {
            aPos.setText(num);
            aPos.setEnabled(false);
        }
    }

}