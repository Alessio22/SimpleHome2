package io.alelli.simplehome2.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import io.alelli.simplehome2.MainActivity;
import io.alelli.simplehome2.R;
import io.alelli.simplehome2.dao.ProfiloDAO;
import io.alelli.simplehome2.models.Temperature;
import io.alelli.simplehome2.services.TemperatureIntentService;

/**
 * Created by Alessio on 04/12/2015.
 */
public class TemperatureAdapter extends BaseAdapter {
    private static final String TAG = "TemperatureAdapter";

    private Context context;
    private Intent temperatureService;

    private ArrayList<Temperature> elencoTemperature = new ArrayList<>();

    public TemperatureAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return elencoTemperature.size();
    }

    @Override
    public Temperature getItem(int position) {
        return elencoTemperature.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Temperature temperatura = elencoTemperature.get(position);
        convertView = LayoutInflater.from(context).inflate(R.layout.temperature_list_item, parent, false);

        final SharedPreferences prefs = ((MainActivity) context).getPreferences(Context.MODE_PRIVATE);
        ProfiloDAO profiloDAO = new ProfiloDAO(prefs);
        final Long idProfiloAttivo = profiloDAO.getIdProfileActive();
        temperatureService = new Intent(context, TemperatureIntentService.class);

        TextView descrizione = (TextView) convertView.findViewById(R.id.descrizione_temperatura);
        descrizione.setText(temperatura.getTxtTemp());
        TextView temp = (TextView) convertView.findViewById(R.id.temperatura);
        temp.setText(temperatura.getTemperatura());
        TextView setPoint = (TextView) convertView.findViewById(R.id.set_point_temperatura);
        setPoint.setText(temperatura.getSetPoint());

        // TODO btn click

        return convertView;
    }

    public void add(Temperature temperatura) {
        elencoTemperature.add(temperatura);
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<Temperature> temperature) {
        elencoTemperature = new ArrayList<>();
        elencoTemperature.addAll(temperature);
        notifyDataSetChanged();
    }
}
