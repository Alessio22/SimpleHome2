package io.alelli.simplehome2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import io.alelli.simplehome2.R;
import io.alelli.simplehome2.models.Profilo;

/**
 * Created by Alessio on 12/11/2015.
 */
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
        convertView = LayoutInflater.from(context).inflate(R.layout.profilo_list_item, parent, false);
        return convertView;
    }

    public void add(Profilo profilo) {
        elencoProfili.add(profilo);
        notifyDataSetChanged();
    }

}
