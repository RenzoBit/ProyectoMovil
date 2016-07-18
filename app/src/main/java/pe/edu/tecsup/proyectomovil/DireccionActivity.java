package pe.edu.tecsup.proyectomovil;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class DireccionActivity extends AppCompatActivity {

    private ListView lstDirecciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direccion);

        lstDirecciones = (ListView) findViewById(R.id.lstDirecciones);

        String[] from = new String[]{"Descripcion", "Direccion"};
        int[] to = new int[]{R.id.tvDescripcion, R.id.tvDireccion};

        ArrayList<String[]> lista = new ArrayList<String[]>();
        String[] direccion1 = { "Estadio Municipal", "Palmeras, Santa Anita 15007, Perú" };
        String[] direccion2 = { "Plaza De Armas", "Distrito de Lima 15009, Perú" };
        String[] direccion3 = { "Polideportivo Municipal de Santa Anita", "Los Ruiseñores, Santa Anita 15008, Perú" };
        String[] direccion4 = { "Municipalidad De Santa Anita", "Los Eucaliptos, Santa Anita 15008, Perú" };
        String[] direccion5 = { "Casa Del Adulto Mayor", "calle s/n Mz.”L” frente al Parque Lampa de Oro en la Cooperativa de Vivienda Los Chancas de Andahuaylas" };
        String[] direccion6 = { "Clínica Municipal de Santa Anita", "Calle 8, Santa Anita 15011, Perú" };
        String[] direccion7 = { "Casa de la Mujer", "Cruce Jr. San Fernando con Jr. San Isidro" };
        String[] direccion8 = { "PISCINA MUNICIPAL DE SANTA ANITA", "Alameda De Ate 2 Etapa, Santa Anita 15011, Perú" };
        lista.add(direccion1);
        lista.add(direccion2);
        lista.add(direccion3);
        lista.add(direccion4);
        lista.add(direccion5);
        lista.add(direccion6);
        lista.add(direccion7);
        lista.add(direccion8);

        ArrayList<HashMap<String, String>> direcciones = new ArrayList<HashMap<String, String>>();
        for (String[] direccion : lista) {
            HashMap<String, String> datosDireccion = new HashMap<String, String>();
            datosDireccion.put("Descripcion", direccion[0]);
            datosDireccion.put("Direccion", direccion[1]);
            direcciones.add(datosDireccion);
        }
        SimpleAdapter listaAdapter = new SimpleAdapter(DireccionActivity.this, direcciones, R.layout.fila_direccion, from, to);
        lstDirecciones.setAdapter(listaAdapter);
    }
}
