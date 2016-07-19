package pe.edu.tecsup.proyectomovil;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pe.edu.tecsup.proyectomovil.dao.DAOExcepcion;
import pe.edu.tecsup.proyectomovil.dao.NoticiaDAO;

public class PrincipalActivity extends AppCompatActivity implements View.OnClickListener {

    private TabHost tabHost;
    private ListView lstNoticias, lstTelefonos;
    private TextView tvId, tvDescripcion, tvNumero;
    private Spinner spnTipos;
    private EditText edtDescripcion;
    private Button btnCapturar, btnEnviar;
    private ImageView imageView;

    private Map<String, String> data;

    private String mImageFileLocation = "sinfoto";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0;

    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        tabHost = (TabHost) findViewById(R.id.tabHost);
        lstNoticias = (ListView) findViewById(R.id.lstNoticias);
        lstTelefonos = (ListView) findViewById(R.id.lstTelefonos);
        spnTipos = (Spinner) findViewById(R.id.spnTipos);
        edtDescripcion = (EditText) findViewById(R.id.edtDescripcion);
        imageView = (ImageView) this.findViewById(R.id.imageView);
        btnEnviar = (Button) findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(this);
        btnCapturar = (Button) findViewById(R.id.btnCapturar);
        btnCapturar.setOnClickListener(this);

        tabHost.setup();

        TabHost.TabSpec spec;

        spec = tabHost.newTabSpec("tab1");
        spec.setContent(R.id.lnlTab1);
        spec.setIndicator("Noticias");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("tab2");
        spec.setContent(R.id.lnlTab2);
        spec.setIndicator("Teléfonos");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("tab3");
        spec.setContent(R.id.lnlTab3);
        spec.setIndicator("Contacto");
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);

        new ListarHttpREST().execute();

        lstNoticias.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                tvId = (TextView) view.findViewById(R.id.tvId);
                String idNoticia = tvId.getText().toString();
                Intent intent = new Intent(PrincipalActivity.this, NoticiaActivity.class);
                intent.putExtra("idNoticia", idNoticia);
                startActivity(intent);
            }
        });

        String[] from = new String[]{"Descripcion", "Numero"};
        int[] to = new int[]{R.id.tvDescripcion, R.id.tvNumero};
        ArrayList<String[]> lista = new ArrayList<String[]>();
        String[] telefono1 = {"Serenazgo", "3190000"};
        String[] telefono2 = {"Comisaría 1", "5121021"};
        String[] telefono3 = {"Comisaría 2", "5120122"};
        String[] telefono4 = {"Bomberos Zona 1", "105"};
        lista.add(telefono1);
        lista.add(telefono2);
        lista.add(telefono3);
        lista.add(telefono4);
        ArrayList<HashMap<String, String>> telefonos = new ArrayList<HashMap<String, String>>();
        for (String[] telefono : lista) {
            HashMap<String, String> datosTelefono = new HashMap<String, String>();
            datosTelefono.put("Descripcion", telefono[0]);
            datosTelefono.put("Numero", telefono[1]);
            telefonos.add(datosTelefono);
        }
        SimpleAdapter listaAdapter = new SimpleAdapter(PrincipalActivity.this, telefonos, R.layout.fila_telefono, from, to);
        lstTelefonos.setAdapter(listaAdapter);
        lstTelefonos.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tvDescripcion = (TextView) view.findViewById(R.id.tvDescripcion);
                tvNumero = (TextView) view.findViewById(R.id.tvNumero);
                confirmDialog(tvDescripcion.getText().toString(), tvNumero.getText().toString());
            }
        });

        final String tipos[] = {"Denuncia", "Consulta"};
        ArrayAdapter<String> tiposAdapter = new ArrayAdapter<String>(PrincipalActivity.this, android.R.layout.simple_spinner_item, tipos);
        tiposAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTipos.setAdapter(tiposAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_inicio:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.action_direcciones:
                intent = new Intent(this, DireccionActivity.class);
                startActivity(intent);
                break;
            case R.id.action_lugares:
                intent = new Intent(this, MapaActivity.class);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEnviar:
                File file = new File(Environment.getExternalStorageDirectory(), mImageFileLocation);
                String foto = "";
                if (file.exists()) {
                    /*Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] b = baos.toByteArray();
                    foto = Base64.encodeToString(b, Base64.DEFAULT);*/

                    InputStream inputStream = null;
                    try {
                        inputStream = new FileInputStream(file.getAbsolutePath());
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        ByteArrayOutputStream output = new ByteArrayOutputStream();
                        Base64OutputStream output64 = new Base64OutputStream(output, Base64.DEFAULT);
                        try {
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                output64.write(buffer, 0, bytesRead);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        output64.close();
                        foto = output.toString();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                data = new HashMap<String, String>();
                data.put("tipo", spnTipos.getSelectedItem().toString());
                data.put("descripcion", edtDescripcion.getText().toString());
                data.put("archivo", mImageFileLocation);
                data.put("foto", foto);
                new GuardarHttpREST().execute();
                break;
            case R.id.btnCapturar:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mImageFileLocation = "fname_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
                Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), mImageFileLocation));
                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            File imgFile = new File(Environment.getExternalStorageDirectory(), mImageFileLocation);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
            }
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT);
        }
    }

    private class ListarHttpREST extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                HttpRequest httpRequest = HttpRequest.get("http://renzovilela.tk/rest/noticias");
                String respuesta = httpRequest.body().toString();
                Gson gson = new Gson();
                Type stringStringMap = new TypeToken<ArrayList<Map<String, Object>>>() {}.getType();
                final ArrayList<Map<String, Object>> retorno = gson.fromJson(respuesta, stringStringMap);
                NoticiaDAO dao = new NoticiaDAO(getBaseContext());
                dao.eliminarTodos();
                for (Map<String, Object> x : retorno) {
                    dao.insertar((String) x.get("idNoticia"), (String) x.get("titulo"), (String) x.get("fecha"));
                }
            } catch (Exception ex) {
                Log.e("===>", "Error: " + ex);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            NoticiaDAO dao = new NoticiaDAO(getBaseContext());
            String[] from = new String[]{"_id", "titulo", "fecha"};
            int[] to = new int[]{R.id.tvId, R.id.tvTitulo, R.id.tvFecha};
            Cursor cursor = null;
            try {
                cursor = dao.listar2();
                SimpleCursorAdapter adapter = new SimpleCursorAdapter(PrincipalActivity.this, R.layout.fila_noticia, cursor, from, to);
                adapter.notifyDataSetChanged();
                lstNoticias.setAdapter(adapter);
                //cursor.close();
            } catch (DAOExcepcion daoExcepcion) {
                daoExcepcion.printStackTrace();
            }
        }

    }

    private class GuardarHttpREST extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                HttpRequest httpRequest = HttpRequest.post("http://renzovilela.tk/rest/mensajes");
                httpRequest.form(data);
                String respuesta = httpRequest.body().toString();
                Log.e("respuesta", respuesta);
                Gson gson = new Gson();
                Type stringStringMap = new TypeToken<Map<String, Object>>() {}.getType();
                final Map<String, Object> retorno = gson.fromJson(respuesta, stringStringMap);
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "" + retorno.get("mensaje"), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception ex) {
                Log.e("===>", "Error: " + ex);
            }
            return null;
        }

    }

    private void confirmDialog(String descripcion, final String numero) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PrincipalActivity.this);
        builder.setTitle("Aviso").setMessage("¿Seguro que desea llamar a " + descripcion + "?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + numero));
                        if (ActivityCompat.checkSelfPermission(PrincipalActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        startActivity(callIntent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).show();
    }

}
