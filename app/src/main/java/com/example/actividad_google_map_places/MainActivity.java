package com.example.actividad_google_map_places;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.actividad_google_map_places.Adaptadores.WindowInfoAdapter;
import com.example.actividad_google_map_places.Modelos.MarcadorMapaDetalle;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {

    ArrayList<MarcadorMapaDetalle> marcadorMapaDetalle;
    MarcadorMapaDetalle marcadorMapaDetalleBase;
    GoogleMap mapa;

    String API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Llamada al fragmento que contiene el mapa
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        API_KEY = "AIzaSyAZmpF3k0bcm-3c-f_0feLZQZRwYu-gdr0";
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        LatLng posicionMarcador = new LatLng(latLng.latitude, latLng.longitude);

        marcadorMapaDetalleBase = new MarcadorMapaDetalle();
        marcadorMapaDetalleBase.setCoordenadas(posicionMarcador);

        /*MarkerOptions marcador = new MarkerOptions();
        marcador.position(posicionMarcador);
        marcador.title("Punto");
        Log.i("TEST_0", posicionMarcador.latitude + " " + posicionMarcador.longitude);

        mapa.addMarker(marcador);*/

        //Datos para ejecutar las APIS:

        Double latitud = latLng.latitude;
        Double longitud = latLng.longitude;
        String radio = "1500";

        //Ejecutar las APIS de Google Places
        //Google places details:

        //Google search near
        String api_map_alrededor = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?fields=name&" +
                "location=" + latitud + "," + longitud + "&" +
                "&radius=" + radio + "&" +
                "type=bar&" +
                "key=" + API_KEY;

        get_lugares_cercanos_mapa(api_map_alrededor, API_KEY);

        mapa.clear();

        marcadorMapaDetalle = new ArrayList<>();
        ArrayList<MarcadorMapaDetalle> marcadores =  marcadorMapaDetalleBase.getLugares_alrededor();
        marcadores = new ArrayList<>();

        marcadorMapaDetalleBase = new MarcadorMapaDetalle();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mapa = googleMap;

        //Cambiar tipo de mapa
        mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mapa.getUiSettings().setZoomControlsEnabled(true);

        //Mover el mapa a una ubicaciòn
        CameraUpdate camUpd1 = CameraUpdateFactory
                //.newLatLngZoom(new LatLng(40.711959596705796, -74.06189862638712), 18);
                .newLatLngZoom(new LatLng(-1.0126002542342059, -79.46938622005604), 18);
        mapa.moveCamera(camUpd1);

        mapa.setOnMapClickListener(this);
        mapa.setOnMarkerClickListener(this);

        mapa.setInfoWindowAdapter(new WindowInfoAdapter(this, null));
    }

    public void get_lugares_cercanos_mapa(String url, String API_KEY){
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("TEST_0", response);
                        try {
                            parsear_informacion_google_alrededores(response, API_KEY);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Formateando los errores que se mostrarán basado en el status de la petición.
                        if(error.networkResponse.statusCode == 400){

                        }
                    }
                });
        queue.add(stringRequest);
    }

    private void get_detalles_lugar_mapa(String url, String place_id){
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("TEST-2", "DETALLES = " + response);
                        try {
                            parsear_informacion_google_detalle(response, place_id);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Formateando los errores que se mostrarán basado en el status de la petición.
                        if(error.networkResponse.statusCode == 400){

                        }
                    }
                });
        queue.add(stringRequest);


    }

    private void parsear_informacion_google_alrededores(String response, String API_KEY) throws JSONException {
        JSONObject alrededorObject = new JSONObject(response);
        JSONArray jArrayResults = alrededorObject.getJSONArray("results");

        marcadorMapaDetalle = new ArrayList<>();
        marcadorMapaDetalle = MarcadorMapaDetalle.getJsonLugaresAlrededor(jArrayResults);

        marcadorMapaDetalleBase.setLugares_alrededor(marcadorMapaDetalle);

        //Definir los marcadores en el mapa:
        for (int i = 0; i < marcadorMapaDetalle.size(); i++){
            MarkerOptions markerOptions = new MarkerOptions();
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);

            markerOptions.position(marcadorMapaDetalle.get(i).getCoordenadas());
            markerOptions.title(marcadorMapaDetalle.get(i).getNombre());
            markerOptions.icon(bitmapDescriptor);

            mapa.addMarker(markerOptions);

            /*Glide.with(this)
                    .asBitmap()
                    .load(marcadorMapaDetalle.get(i).getUrl_icono_tipo_lugar())
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {

                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                            mapa.addMarker(markerOptions);
                        }
                    });
               */

        }
    }


    private void parsear_informacion_google_detalle(String response, String place_id) throws JSONException {

        JSONObject jObjectDetalle = new JSONObject(response);
        JSONObject jObjectResult = jObjectDetalle.getJSONObject("result");

        MarcadorMapaDetalle marcadorSeleccionado = marcadorMapaDetalleBase.set_detalles_lugar(place_id, jObjectResult, API_KEY);

        for (int i = 0; i< marcadorMapaDetalle.size(); i++){
            if (marcadorMapaDetalle.get(i).getUrl_fotos() != null){
                for (int j = 0; j< marcadorMapaDetalle.get(i).getUrl_fotos().size(); j++) {
                    //Obtener la url de las imágenes reales basada en la referencia de la foto:
                    Log.i("CHECK_0", marcadorMapaDetalle.get(i).getUrl_fotos().get(j));
                }
            }
        }



        mapa.setInfoWindowAdapter(new WindowInfoAdapter(this, marcadorSeleccionado));
        markerClicked.showInfoWindow();
    }

    Marker markerClicked;
    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        //Guardar el marcador en el cual se dió click para actualizarlo posteriormente
        //cuando finalice la API
        markerClicked = marker;

        //Obtener el place_id guardado en el array de la clase para obtener los detalles del lugar.
        String place_id = MarcadorMapaDetalle.get_placeid_por_latlng(marcadorMapaDetalle, marker.getPosition());

        if (place_id != ""){
            //URL para ejecutar la API
            String api_map_detalles = "https://maps.googleapis.com/maps/api/place/details/json?fields=name,photo,opening_hours,address_components,formatted_phone_number" +
                    "&place_id=" + place_id +
                    "&key=" + API_KEY;

            //Ejecutar la API
            get_detalles_lugar_mapa(api_map_detalles, place_id);
        }

        return false;
    }
}