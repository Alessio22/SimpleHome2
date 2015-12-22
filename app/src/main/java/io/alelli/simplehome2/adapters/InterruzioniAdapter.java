package io.alelli.simplehome2.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import io.alelli.simplehome2.R;
import io.alelli.simplehome2.models.Interruzione;

public class InterruzioniAdapter extends BaseAdapter {
    private static final String TAG = "LuciAdapter";

    private Context context;
    private Intent interruzioniService;

    private ArrayList<Interruzione> interruzioniList = new ArrayList<>();

    public InterruzioniAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return interruzioniList.size();
    }

    @Override
    public Interruzione getItem(int position) {
        return interruzioniList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return interruzioniList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.list_item_interruzioni, parent, false);
        Interruzione interruzione = interruzioniList.get(position);

        TextView textView = (TextView) convertView.findViewById(R.id.nome_interruzione);
        textView.setText(interruzione.getNome());

        return convertView;
    }

    public void add(Interruzione interruzione) {
        interruzioniList.add(interruzione);
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<Interruzione> interruzioni) {
        interruzioniList.clear();
        interruzioniList.addAll(interruzioni);
        notifyDataSetChanged();
    }

}
