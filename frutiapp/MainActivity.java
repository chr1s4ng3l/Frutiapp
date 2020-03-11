package google.tamayo.christopher.frutiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private ImageView img_personaje;
    private EditText et_nombre;
    private TextView tv_score;
    private MediaPlayer mp;

    int num_ramdom = (int) (Math.random() *10);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img_personaje = (ImageView)findViewById(R.id.imageView_Personaje);
        et_nombre = (EditText)findViewById(R.id.txt_nombre);
        tv_score = (TextView)findViewById(R.id.textView_BestScore);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        //cambiar imagen del ImageView al abrir la aplicacion
        int id;
        if(num_ramdom == 0  || num_ramdom == 10){
            id = getResources().getIdentifier("mango", "drawable", getPackageName());
            img_personaje.setImageResource(id);

        }else if(num_ramdom == 1 || num_ramdom == 9){
            id = getResources().getIdentifier("fresa", "drawable", getPackageName());
            img_personaje.setImageResource(id);

        }else if(num_ramdom == 2  || num_ramdom == 8){
            id = getResources().getIdentifier("manzana", "drawable", getPackageName());
            img_personaje.setImageResource(id);

        }else if(num_ramdom == 3  || num_ramdom == 7){
            id = getResources().getIdentifier("sandia", "drawable", getPackageName());
            img_personaje.setImageResource(id);

        }else if(num_ramdom == 4  || num_ramdom == 5 || num_ramdom == 6){
            id = getResources().getIdentifier("uva", "drawable", getPackageName());
            img_personaje.setImageResource(id);

        }
        //conexion a la base de datos
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "BD", null, 1);
        SQLiteDatabase BD = admin.getWritableDatabase();

        //seleccionar el mayor puntaje de la base de datos
        Cursor consulta = BD.rawQuery("select * from puntaje where score = (select max(score) from puntaje)", null);

        //si encontro un score en la BD
        if(consulta.moveToFirst()){

            //obtener datos de la base de datos segun el campo nombre(0) y score (1)
            String temp_nombre = consulta.getString(0);
            String temp_score = consulta.getString(1);
            tv_score.setText("Record "+ temp_score + " de "+ temp_nombre);
            BD.close();

        }else{
            BD.close();

        }

        //pista de audio
        mp = MediaPlayer.create(this, R.raw.alphabet_song);
        mp.start();
        mp.setLooping(true);

    }

    public void Jugar(View view){
        String nombre = et_nombre.getText().toString();

        if(!nombre.equals("")){
            //detener audio
            mp.stop();
            //desechar media player
            mp.release();
            //mostrar activity 2
            Intent intent = new Intent(this, Main2Activity_Nivel1.class);
            //pasar dato jugador a la segunda activity
            intent.putExtra("jugador", nombre);
            //mostrar
            startActivity(intent);
            finish();

        }else{
            Toast.makeText(this, "Debes ingresar un nombre", Toast.LENGTH_SHORT).show();
            //INDICAR AL PROGRAMA QUE SE ABRA EL TECLADO PARA ESCRIBIR EN EL ET_NOMBRE
            et_nombre.requestFocus();
            InputMethodManager imm= (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
            imm.showSoftInput(et_nombre, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    //metodo para la flecha de regreso del action bar
    @Override
    public void onBackPressed(){

    }


}
