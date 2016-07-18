package pe.edu.tecsup.proyectomovil;

import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class GCMTestActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static final String EXTRA_MESSAGE = "message";
    private static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String PROPERTY_EXPIRATION_TIME = "onServerExpirationTimeMs";
    private static final String PROPERTY_USER = "user";

    public static final long EXPIRATION_TIME_MS = 1000 * 3600 * 24 * 7;

    String SENDER_ID = "519943413107";

    static final String TAG = "GCMDemo";

    private Context context;
    private String regid;
    private GoogleCloudMessaging gcm;

    private EditText edtUsuario;
    private Button btnRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gcmtest);

        edtUsuario = (EditText) findViewById(R.id.edtUsuario);
        btnRegistrar = (Button) findViewById(R.id.btnGuadar);

        btnRegistrar.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                context = getApplicationContext();

                gcm = GoogleCloudMessaging.getInstance(GCMTestActivity.this);

                // Obtenemos el Registration ID guardado
                regid = getRegistrationId(context);

                if (regid.equals("")) {
                    TareaRegistroGCM tarea = new TareaRegistroGCM();
                    tarea.execute(edtUsuario.getText().toString());
                }
            }
        });
    }


    private String getRegistrationId(Context context) {
        SharedPreferences prefs = getSharedPreferences(GCMTestActivity.class.getSimpleName(), Context.MODE_PRIVATE);

        String registrationId = prefs.getString(PROPERTY_REG_ID, "");

        if (registrationId.length() == 0) {
            Log.d(TAG, "Registro GCM no encontrado.");
            return "";
        }

        String registeredUser = prefs.getString(PROPERTY_USER, "user");

        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);

        long expirationTime = prefs.getLong(PROPERTY_EXPIRATION_TIME, -1);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String expirationDate = sdf.format(new Date(expirationTime));

        Log.d(TAG, "Registro GCM encontrado (usuario=" + registeredUser + ", version=" + registeredVersion + ", expira=" + expirationDate + ")");

        int currentVersion = getAppVersion(context);

        if (registeredVersion != currentVersion) {
            Log.d(TAG, "Nueva versión de la aplicación.");
            return "";
        } else if (System.currentTimeMillis() > expirationTime) {
            Log.d(TAG, "Registro GCM expirado.");
            return "";
        } else if (!edtUsuario.getText().toString().equals(registeredUser)) {
            Log.d(TAG, "Nuevo nombre de usuario.");
            return "";
        }

        return registrationId;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            throw new RuntimeException("Error al obtener versión: " + e);
        }
    }

    private class TareaRegistroGCM extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String msg = "";

            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }

                // Nos registramos en los servidores de GCM
                regid = gcm.register(SENDER_ID);

                Log.d(TAG, "Registrado en GCM: registration_id=" + regid);

                // Nos registramos en nuestro servidor
                String aplicacion = "proyectomovil";
                WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                String mac = manager.getConnectionInfo().getMacAddress();
                boolean registrado = registroServidor(params[0], regid, aplicacion, mac);

                // Guardamos los datos del registro
                if (registrado) {
                    setRegistrationId(context, params[0], regid);
                }
            } catch (IOException ex) {
                Log.d(TAG, "Error registro en GCM:" + ex.getMessage());
            }

            return msg;
        }
    }

    private void setRegistrationId(Context context, String user, String regId) {
        SharedPreferences prefs = getSharedPreferences(GCMTestActivity.class.getSimpleName(), Context.MODE_PRIVATE);

        int appVersion = getAppVersion(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_USER, user);
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.putLong(PROPERTY_EXPIRATION_TIME, System.currentTimeMillis()    + EXPIRATION_TIME_MS);

        editor.commit();
    }

    private boolean registroServidor(String usuario, String regId, String aplicacion, String mac) {
        boolean reg = false;

        Log.i("======================", "registroServidor()");

        try {
            HashMap data = new HashMap<String, String>();

            data.put("usuario", usuario);
            data.put("codigoGCM", regId);
            data.put("aplicacion", aplicacion);
            data.put("mac", mac);

            HttpRequest httpRequest = HttpRequest.post("http://renzovilela.tk/rest/dispositivos");
            httpRequest.form(data);
            String respuesta = httpRequest.body().toString();
            Gson gson = new Gson();
            Type stringStringMap = new TypeToken<Map<String, Object>>() {}.getType();

            final Map<String, Object> retorno = gson.fromJson(respuesta, stringStringMap);


            if ( retorno.get("estado").toString().equals("CORRECTO")) {
                reg = true;
            }

        } catch (Exception ex) {
            Log.e("GCMIntentService", "Error: " + ex);
        }
        return reg;
    }

}