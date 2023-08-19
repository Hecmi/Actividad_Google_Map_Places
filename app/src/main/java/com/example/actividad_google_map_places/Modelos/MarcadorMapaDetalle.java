package com.example.actividad_google_map_places.Modelos;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MarcadorMapaDetalle {

    String place_id;
    String nombre;
    String ubicacion;
    LatLng coordenadas;
    String num_telefono;
    String rating;



    ArrayList<String> url_fotos;
    ArrayList<String> horario;

    ArrayList<MarcadorMapaDetalle> lugares_alrededor;

    public MarcadorMapaDetalle (JSONObject jObjectLugar) throws JSONException {

        String place_id = jObjectLugar.getString("place_id");

        //Obtener las coordenadas (latitud y longitud de los lugares)
        JSONObject jObjectGeometry = jObjectLugar.getJSONObject("geometry");
        JSONObject jObjectLocation = jObjectGeometry.getJSONObject("location");

        LatLng latLng =  new LatLng(0,0);
        if (jObjectLocation.has("lat") && jObjectLocation.has("lng")) {
            latLng = new LatLng(jObjectLocation.getDouble("lat"), jObjectLocation.getDouble("lng"));
        }

        //Obtener el nombre para el marcador:
        String name_place = jObjectLugar.getString("name");

        this.nombre = name_place;
        this.place_id = place_id;
        this.coordenadas = latLng;
    }

    public MarcadorMapaDetalle(){ }
    public static String get_placeid_por_latlng(ArrayList<MarcadorMapaDetalle> lista_lugares, LatLng latLng){
        String place_id = "";

        if (lista_lugares != null){
            for (int i = 0; i < lista_lugares.size(); i++){
                Log.i("clickeado :D", lista_lugares.get(i).coordenadas + " " + latLng);
                if (lista_lugares.get(i).coordenadas.equals(latLng)){
                    place_id = lista_lugares.get(i).place_id;
                }
            }
        }

        return place_id;
    }

    public static ArrayList<MarcadorMapaDetalle> getJsonLugaresAlrededor(JSONArray jsonArray) throws JSONException {
        JSONArray jArrayResults = jsonArray;

        ArrayList<MarcadorMapaDetalle> arr_Marcadores = new ArrayList<>();

        for (int i = 0; i < jArrayResults.length(); i++) {
            //El primer resultado es lo más cercano al punto seleccionado

            JSONObject jObjectPSeleccionado = jArrayResults.getJSONObject(i);

            MarcadorMapaDetalle marcadorMapaDetalle = new MarcadorMapaDetalle(jObjectPSeleccionado);
            arr_Marcadores.add(marcadorMapaDetalle);
        }


        return arr_Marcadores;
    }

    public MarcadorMapaDetalle getMarcadorMapaPorID(String place_id){
        MarcadorMapaDetalle marcadorMapaDetalle = null;
        for (int i = 0; i < this.lugares_alrededor.size(); i++){
            if (this.lugares_alrededor.get(i).place_id.equals(place_id)){
                marcadorMapaDetalle = this.lugares_alrededor.get(i);
            }
        }

        return marcadorMapaDetalle;
    }

    public MarcadorMapaDetalle set_detalles_lugar(String place_id, JSONObject jObjectLugarDetalle, String API_KEY) throws JSONException {

        MarcadorMapaDetalle marcadorMapaDetalleEditar =  getMarcadorMapaPorID(place_id);

        //Obtener la ubicación (país, ciudad, calle y ruta)
        JSONArray jArrayAdressComponents = jObjectLugarDetalle.getJSONArray("address_components");

        String direccion_completa = "";
        String pais = "";
        String ciudad = "";
        String ruta = "";
        String calle_numero = "";

        for (int i = 0; i < jArrayAdressComponents.length(); i++){
            JSONObject componente_direccion = jArrayAdressComponents.getJSONObject(i);

            JSONArray tipo_componente = componente_direccion.getJSONArray("types");

            for (int j = 0; j < tipo_componente.length(); j++){
                String tipo = tipo_componente.getString(j);

                Log.i("TEST-2", "TIPO = " + tipo);

                if (tipo.equals("country")){
                    pais = componente_direccion.getString("long_name");
                }
                if (tipo.equals("locality")){
                    ciudad = componente_direccion.getString("long_name");
                }
                if (tipo.equals("route")){
                    ruta = componente_direccion.getString("long_name");
                }
                if (tipo.equals("street_number")){
                    calle_numero = componente_direccion.getString("long_name");
                }

            }
        }

        //Definir la dirección completa:
        direccion_completa = pais + ", " + ciudad + ", " + ruta +  ", " + calle_numero;
        marcadorMapaDetalleEditar.ubicacion = direccion_completa;

        Log.i("TEST-2", "direccion_completa = " + direccion_completa);

        if (jObjectLugarDetalle.has("opening_hours")) {
            marcadorMapaDetalleEditar.horario = new ArrayList<>();
            JSONObject jObjectOpeningHours = jObjectLugarDetalle.getJSONObject("opening_hours");

            JSONArray jArrayWeekdayText = jObjectOpeningHours.getJSONArray("weekday_text");

            //Recorrer el jArray para obtener el horario de lunes a domingo:
            for (int i = 0; i < jArrayWeekdayText.length(); i++){
                marcadorMapaDetalleEditar.horario.add(jArrayWeekdayText.get(i) + "");
            }
        }
        if (jObjectLugarDetalle.has("photos")){
            //Imágenes:
            marcadorMapaDetalleEditar.url_fotos = new ArrayList<>();

            JSONArray jArrayFotos = jObjectLugarDetalle.getJSONArray("photos");
            for (int i = 0; i < jArrayFotos.length(); i++){
                JSONObject jObjectFoto = jArrayFotos.getJSONObject(i);
                String foto_url = jObjectFoto.getString("photo_reference");
                Log.i("FOTOS", foto_url);

                //marcadorMapaDetalleEditar.url_fotos.add(foto_url);
                marcadorMapaDetalleEditar.url_fotos.add(get_url_imagen(foto_url, API_KEY));
            }
        }
        if (jObjectLugarDetalle.has("formatted_phone_number")){
            this.num_telefono = jObjectLugarDetalle.getString("formatted_phone_number");
        }

        return marcadorMapaDetalleEditar;
    }

    public String get_url_imagen(String imagen_referencia, String API_KEY){
        //Obtener la url de la imagen:
        String fotoUrl = "https://maps.googleapis.com/maps/api/place/photo?" +
                "maxwidth=400&photoreference=" + imagen_referencia + "&" +
                "key=" + API_KEY;

        return fotoUrl;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRating() {
        return rating;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getNum_telefono() {
        return num_telefono;
    }

    public void setNum_telefono(String num_telefono) {
        this.num_telefono = num_telefono;
    }

    public LatLng getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(LatLng coordenadas) {
        this.coordenadas = coordenadas;
    }

    public ArrayList<String> getUrl_fotos() {
        return url_fotos;
    }

    public void setUrl_fotos(ArrayList<String> url_fotos) {
        this.url_fotos = url_fotos;
    }

    public ArrayList<String> getHorario() {
        return horario;
    }

    public void setHorario(ArrayList<String> horario) {
        this.horario = horario;
    }

    public ArrayList<MarcadorMapaDetalle> getLugares_alrededor() {
        return lugares_alrededor;
    }

    public void setLugares_alrededor(ArrayList<MarcadorMapaDetalle> lugares_alrededor) {
        this.lugares_alrededor = lugares_alrededor;
    }
}
