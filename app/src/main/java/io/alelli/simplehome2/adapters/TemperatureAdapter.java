package io.alelli.simplehome2.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import io.alelli.simplehome2.MainActivity;
import io.alelli.simplehome2.R;
import io.alelli.simplehome2.dao.ProfiloDAO;
import io.alelli.simplehome2.models.Temperatura;
import io.alelli.simplehome2.services.TemperatureIntentService;

/**
 * Created by Alessio on 04/12/2015.
 */
public class TemperatureAdapter extends BaseAdapter {
    private static final String TAG = "TemperatureAdapter";

    private Context context;
    private Intent temperatureService;

    private ArrayList<Temperatura> elencoTemperature = new ArrayList<>();

    public TemperatureAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return elencoTemperature.size();
    }

    @Override
    public Temperatura getItem(int position) {
        return elencoTemperature.get(position);
    }

    @Override
    public long getItemId(int position) {
        return elencoTemperature.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Temperatura temperatura = elencoTemperature.get(position);
        convertView = LayoutInflater.from(context).inflate(R.layout.list_item_temperature, parent, false);

        final SharedPreferences prefs = ((MainActivity) context).getPreferences(Context.MODE_PRIVATE);
        ProfiloDAO profiloDAO = new ProfiloDAO(context, prefs);
        final Long idProfiloAttivo = profiloDAO.getIdProfileActive();
        temperatureService = new Intent(context, TemperatureIntentService.class);

        final TextView descrizione = (TextView) convertView.findViewById(R.id.descrizione_temperatura);
        descrizione.setText(temperatura.getTxtTemp());
        TextView temp = (TextView) convertView.findViewById(R.id.temperatura);
        temp.setText(temperatura.getTemperatura());
        TextView setPoint = (TextView) convertView.findViewById(R.id.set_point_temperatura);
        setPoint.setText(temperatura.getSetPoint());

        FloatingActionButton btnUp = (FloatingActionButton) convertView.findViewById(R.id.btn_up_teperatura);
        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer id = position;
                Log.d(TAG, "onClick btnUp: " + id);
                String nomeLuce = (String) descrizione.getText();

                temperatureService.setAction(TemperatureIntentService.ACTION_UP);
                temperatureService.putExtra(TemperatureIntentService.EXTRA_ID_PROFILO, idProfiloAttivo);
                temperatureService.putExtra(TemperatureIntentService.EXTRA_ID, id);
                temperatureService.putExtra(TemperatureIntentService.EXTRA_NOME, nomeLuce);
                context.startService(temperatureService);
            }
        });

        FloatingActionButton btnDown = (FloatingActionButton) convertView.findViewById(R.id.btn_down_teperatura);
        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long id = getItemId(position);
                Log.d(TAG, "onClick btnDown: " + id);
                String nomeLuce = (String) descrizione.getText();

                temperatureService.setAction(TemperatureIntentService.ACTION_DOWN);
                temperatureService.putExtra(TemperatureIntentService.EXTRA_ID_PROFILO, idProfiloAttivo);
                temperatureService.putExtra(TemperatureIntentService.EXTRA_ID, id);
                temperatureService.putExtra(TemperatureIntentService.EXTRA_NOME, nomeLuce);
                context.startService(temperatureService);
            }
        });

        return convertView;
    }

    public void clear() {
        elencoTemperature.clear();
        notifyDataSetChanged();
    }
    public void add(Temperatura temperatura) {
        elencoTemperature.add(temperatura);
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<Temperatura> temperatura) {
        elencoTemperature.clear();
        elencoTemperature.addAll(temperatura);
        notifyDataSetChanged();
    }
}
