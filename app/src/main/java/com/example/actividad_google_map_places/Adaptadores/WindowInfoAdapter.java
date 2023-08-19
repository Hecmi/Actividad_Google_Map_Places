package com.example.actividad_google_map_places.Adaptadores;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.actividad_google_map_places.Modelos.MarcadorMapaDetalle;
import com.example.actividad_google_map_places.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

public class WindowInfoAdapter implements GoogleMap.InfoWindowAdapter {
    Context ctx;
    View mView;
    MarcadorMapaDetalle marcadorMapaDetalle;

    public WindowInfoAdapter(Context context, MarcadorMapaDetalle marcadorMapaDetalle){
        this.ctx = context;
        this.marcadorMapaDetalle = marcadorMapaDetalle;
        mView = LayoutInflater.from(context).inflate(R.layout.window_info_adapter_mapa, null);
    }

    private void setInformacionEnLayout(Marker marker, View view){
        TextView txtLugar = (TextView) view.findViewById(R.id.txtLugar);
        TextView txtHorario = (TextView) view.findViewById(R.id.txtHorarios);
        TextView txtUbicacion = (TextView) view.findViewById(R.id.txtUbicacion);
        //TextView txtCoordenadas = (TextView) view.findViewById(R.id.txtLugar);

        ImageView imgLogo = (ImageView) view.findViewById(R.id.imgLogo);

        txtLugar.setText("PEPE");
        txtUbicacion.setText("PEPEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE\nPEPE\nPEPE");
        Glide.with(this.ctx)
                .load("https://maps.gstatic.com/mapfiles/place_api/icons/v1/png_71/bar-71.png")
                .into(imgLogo);

        /*if (this.marcadorMapaDetalle != null){
            txtLugar.setText(this.marcadorMapaDetalle.getNombre());
            txtUbicacion.setText(this.marcadorMapaDetalle.getUbicacion());

            Glide.with(this.ctx)
                    .load(this.marcadorMapaDetalle.getUrl_fotos().get(0))
                    .into(imgLogo);
        }*/
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        setInformacionEnLayout(marker, this.mView);
        return this.mView;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        setInformacionEnLayout(marker, this.mView);
        return this.mView;
    }
}
