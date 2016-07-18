package pe.edu.tecsup.proyectomovil;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public class NoticiaActivity extends AppCompatActivity {

    private TextView tvTitulo, tvDescripcion;
    private String idNoticia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticia);

        tvTitulo = (TextView) findViewById(R.id.tvTitulo);
        tvDescripcion = (TextView) findViewById(R.id.tvDescripcion);

        Intent intent = getIntent();
        idNoticia = intent.getStringExtra("idNoticia");

        new ObtenerHttpREST().execute();
    }

    private class ObtenerHttpREST extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                HttpRequest httpRequest = HttpRequest.get("http://renzovilela.tk/rest/noticias/" + idNoticia);
                String respuesta = httpRequest.body().toString();
                Gson gson = new Gson();
                Type stringStringMap = new TypeToken<Map<String, Object>>(){}.getType();
                final Map<String, Object> retorno = gson.fromJson(respuesta, stringStringMap);
                runOnUiThread(new Runnable() {
                    public void run() {
                        tvTitulo.setText((String)retorno.get("titulo"));
                        tvDescripcion.setText((String)retorno.get("descripcion"));
                    }
                });
            } catch (Exception ex) {
                Log.e("===>", "Error: " + ex);
            }
            return null;
        }

    }

}
