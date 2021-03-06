package io.alelli.simplehome2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.alelli.simplehome2.R;
import io.alelli.simplehome2.models.Profilo;

public class ProfiliAdapter extends BaseAdapter {
    private static final String TAG = "ProfiliAdapter";

    private Context context;

    private ArrayList<Profilo> elencoProfili = new ArrayList<>();

    public ProfiliAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return elencoProfili.size();
    }

    @Override
    public Profilo getItem(int position) {
        return elencoProfili.get(position);
    }

    @Override
    public long getItemId(int position) {
        return elencoProfili.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Profilo profilo = getItem(position);
        convertView = LayoutInflater.from(context).inflate(R.layout.list_item_profili, parent, false);

        TextView etichetta = (TextView) convertView.findViewById(R.id.profilo_etichetta);
        etichetta.setText(profilo.getEtichetta());
        TextView url = (TextView) convertView.findViewById(R.id.profilo_url);
        url.setText(profilo.getUrl());

        return convertView;
    }

    public void add(Profilo profilo) {
        elencoProfili.add(profilo);
        notifyDataSetChanged();
    }

    public void addAll(List<Profilo> profili) {
        elencoProfili.addAll(profili);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        elencoProfili.remove(position);
        notifyDataSetChanged();
    }

}
