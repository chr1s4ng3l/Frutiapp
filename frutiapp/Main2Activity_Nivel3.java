package google.tamayo.christopher.frutiapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Main2Activity_Nivel3 extends AppCompatActivity {

    private TextView tvNombre, tvScore;
    private ImageView ivAuno, ivAdos, ivVidas;
    private EditText etRespuesta;
    private MediaPlayer mp, mpGreat, mpBad;

    int score, numAleatorio_uno, numAleatorio_dos, resultado, vidas = 3;
    String nombre_jugador, string_score, string_vidas;

    String numero[] = {"cero","uno","dos","tres","cuatro","cinco","seis","siete","ocho", "nueve"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2__nivel3);

        Toast.makeText(this, "Nivel 3 - Restas Basicas", Toast.LENGTH_SHORT).show();

        tvNombre = (TextView)findViewById(R.id.textView_nombre);
        tvScore = (TextView)findViewById(R.id.textView_score);
        ivVidas = (ImageView) findViewById(R.id.imageView_Vidas);
        ivAuno = (ImageView) findViewById(R.id.imageView_Num1);
        ivAdos = (ImageView) findViewById(R.id.imageViewNum2);
        etRespuesta = (EditText)findViewById(R.id.editText_Resultado);


        //traer nombre del jugador de la acty principal
        nombre_jugador = getIntent().getStringExtra("jugador");
        tvNombre.setText("Jugador: "+nombre_jugador);

        string_score = getIntent().getStringExtra("score");
        score = Integer.parseInt(string_score);
        tvScore.setText("Score: "+score);

        string_vidas = getIntent().getStringExtra("vidas");
        vidas = Integer.parseInt(string_vidas);
        if(vidas == 3){

            ivVidas.setImageResource(R.drawable.tresvidas);
        }
        if(vidas == 2){

            ivVidas.setImageResource(R.drawable.dosvidas);
        }
        if(vidas == 1){

            ivVidas.setImageResource(R.drawable.dosvidas);
        }

        //colocar icono en el action bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        //audio ciclado
        mp = MediaPlayer.create(this, R.raw.goats);
        mp.start();
        mp.setLooping(true);


        //audio del boton
        mpGreat = MediaPlayer.create(this, R.raw.wonderful);
        mpBad = MediaPlayer.create(this, R.raw.bad);

        //mandamos llamar el metodo
        NumAleatorio();


    }

    public void Comparar(View view){
        String respuesta = etRespuesta.getText().toString();

        if(!respuesta.equals("")){
            int respuesta_jugador = Integer.parseInt(respuesta);
            //si contesta correctamente
            if(resultado == respuesta_jugador){
                mpGreat.start();
                score ++;
                tvScore.setText("Score: "+score);
                etRespuesta.setText("");
                BaseDeDatos();

                //si contesta incorrecto
            }else{

                mpBad.start();
                vidas --;
                BaseDeDatos();

                switch (vidas){
                    case 3:
                        ivVidas.setImageResource(R.drawable.tresvidas);
                        break;
                    case 2:
                        Toast.makeText(this, "Te quedan dos manzanas", Toast.LENGTH_SHORT).show();
                        ivVidas.setImageResource(R.drawable.dosvidas);
                        break;
                    case 1:
                        Toast.makeText(this, "Te queda una manzana", Toast.LENGTH_SHORT).show();
                        ivVidas.setImageResource(R.drawable.unavida);
                        break;
                    case 0:
                        Toast.makeText(this, "Has perdido todas las manzanas", Toast.LENGTH_SHORT).show();
                        Intent intent =  new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        mp.stop();
                        //liberar recursos
                        mp.release();
                        break;
                }
                etRespuesta.setText("");
            }

            NumAleatorio();

        }else{
            Toast.makeText(this, "Debes escribir una respuesta", Toast.LENGTH_SHORT).show();
        }

    }

    //metodo para poner imagenes aleatorias
    public void NumAleatorio(){

        if(score <= 29){
            numAleatorio_uno = (int) (Math.random() * 10);
            numAleatorio_dos = (int) (Math.random() * 10);

            resultado = numAleatorio_uno - numAleatorio_dos;


            if(resultado>=0){
                for (int i = 0; i < numero.length; i++ ){

                    int id = getResources().getIdentifier(numero[i],"drawable", getPackageName());
                    if(numAleatorio_uno == i){
                        ivAuno.setImageResource(id);

                    }if(numAleatorio_dos == i){
                        ivAdos.setImageResource(id);

                    }

                }
            }else {

                NumAleatorio();
            }

        }else{

            Intent intent = new Intent(this, Main2Activity_Nivel4.class);

            string_score = String.valueOf(score);
            string_vidas = String.valueOf(vidas);
            intent.putExtra("jugador", nombre_jugador);
            intent.putExtra("score", string_score);
            intent.putExtra("vidas", string_vidas);

            startActivity(intent);
            finish();
            mp.stop();
            mp.release();
        }
    }

    public void BaseDeDatos(){
        //creacion del objeto de la clase donde crea la base de datos
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "BD", null, 1);
        //lectura y escritura de la base de datos
        SQLiteDatabase BD = admin.getWritableDatabase();

        //objeto para crear la line de codido en sql
        Cursor consulta = BD.rawQuery("select * from puntaje where score = (select max(score) from puntaje)", null);

        //para saber si hubo respuesta de la consulta si hubo registros encontrados
        if(consulta.moveToFirst()){
            //recuperar datos de la base de datos (las columnas empiezan de 0,1,2,3..n) es decir mi campo nombre esta en la columna 0 y el campo score en la 1
            String temp_nombre = consulta.getString(0);
            String temp_score = consulta.getString(1);

            int best_score = Integer.parseInt(temp_score);

            if(score > best_score){
                ContentValues modificacion = new ContentValues();
                modificacion.put("nombre", nombre_jugador);
                modificacion.put("score", score);

                BD.update("puntaje", modificacion, "score= "+score, null);
            }
            BD.close();
        }else{
            ContentValues insertar =new ContentValues();
            insertar.put("nombre", nombre_jugador);
            insertar.put("score", score);

            BD.insert("puntaje", null, insertar);
            BD.close();

        }
    }

    @Override
    public void onBackPressed(){

    }
}
