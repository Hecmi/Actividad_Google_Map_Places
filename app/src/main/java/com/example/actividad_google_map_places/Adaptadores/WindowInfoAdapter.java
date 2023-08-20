package com.example.actividad_google_map_places.Adaptadores;

import android.content.Context;
import android.media.Image;
import android.util.Log;
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
        TextView txtTelefono = (TextView) view.findViewById(R.id.txtTelefono);
        TextView txtCoordenadas = (TextView) view.findViewById(R.id.txtCoordenadas);


        ImageView imgLogo = (ImageView) view.findViewById(R.id.imgLogo);

        if (this.marcadorMapaDetalle != null){
            txtLugar.setText(this.marcadorMapaDetalle.getNombre());
            txtUbicacion.setText(this.marcadorMapaDetalle.getUbicacion());

            if (this.marcadorMapaDetalle.getNum_telefono() != null) txtTelefono.setText("Teléfono: " + this.marcadorMapaDetalle.getNum_telefono());

            //Las coordenadas existen de forma obligatoria, por lo tanto no se validan
            txtCoordenadas.setText("Coordenadas: \n" +
                    "Latitud: " + this.marcadorMapaDetalle.getCoordenadas().latitude + "\n" +
                    "Longitud: " + this.marcadorMapaDetalle.getCoordenadas().longitude);


            if (this.marcadorMapaDetalle.getUrl_fotos() != null)
            {
                if (this.marcadorMapaDetalle.getUrl_fotos().size() > 0)
                {
                    Glide.with(this.ctx)
                            .load(this.marcadorMapaDetalle.getUrl_fotos().get(0))
                            .into(imgLogo);
                }
            }

            if (this.marcadorMapaDetalle.getHorario() != null){
                String horario = "";
                for (int i = 0; i < this.marcadorMapaDetalle.getHorario().size(); i++){
                    horario += this.marcadorMapaDetalle.getHorario().get(i) + "\n";
                }

                if (horario != "" && this.marcadorMapaDetalle.getHorario().size() > 0)
                    txtHorario.setText("Horario: \n" + horario);
            }


        }
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
