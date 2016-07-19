package pe.edu.tecsup.proyectomovil;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapaActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        GoogleMap googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa)).getMap();
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        //CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(-12.04592, -77.030565));
        //CameraUpdate zoom = CameraUpdateFactory.zoomTo(11);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-12.04592, -77.030565), 11));

        //googleMap.moveCamera(center);
        //googleMap.animateCamera(zoom);

        final LatLng centroLima = new LatLng(-12.04592, -77.030565);
        Marker mCentroLima = googleMap.addMarker(new MarkerOptions().position(centroLima).title("Centro de Lima"));
        mCentroLima.showInfoWindow();
        mCentroLima.setDraggable(true);

        final LatLng santaAnita = new LatLng(-12.026718, -76.9725697);
        Marker msantaAnita = googleMap.addMarker(new MarkerOptions().position(santaAnita).title("Santa Anita"));
        msantaAnita.setDraggable(true);
    }

}
