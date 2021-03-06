package io.alelli.simplehome2.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.ArrayList;

import io.alelli.simplehome2.MainActivity;
import io.alelli.simplehome2.R;
import io.alelli.simplehome2.dao.ProfiloDAO;
import io.alelli.simplehome2.models.Luci;
import io.alelli.simplehome2.services.LuciIntentService;

public class LuciAdapter extends BaseAdapter {
    private static final String TAG = "LuciAdapter";

    private Context context;
    private Intent luciService;

    private ArrayList<Luci> elencoLuci = new ArrayList<>();

    public LuciAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return elencoLuci.size();
    }

    @Override
    public Luci getItem(int position) {
        return elencoLuci.get(position);
    }

    @Override
    public long getItemId(int position) {
        return elencoLuci.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Luci luce = elencoLuci.get(position);
        convertView = LayoutInflater.from(context).inflate(R.layout.list_item_luci, parent, false);

        final SharedPreferences prefs = ((MainActivity) context).getPreferences(Context.MODE_PRIVATE);
        ProfiloDAO profiloDAO = new ProfiloDAO(context, prefs);
        final Long idProfiloAttivo = profiloDAO.getIdProfileActive();
        luciService = new Intent(context, LuciIntentService.class);

        Switch switchStato = (Switch) convertView.findViewById(R.id.luciSwitch);
        switchStato.setTag(luce.getId());
        switchStato.setText(luce.getNome());
        switchStato.setChecked(luce.getStato());

        switchStato.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Switch switchStato = (Switch) buttonView;
                Integer idLuce = (Integer) switchStato.getTag();
                String nomeLuce = (String) switchStato.getText();
                Log.d(TAG, "idLuce: " + (isChecked ? "ON" : "OFF"));

                luciService.setAction(LuciIntentService.ACTION_CHANGE);
                luciService.putExtra(LuciIntentService.EXTRA_ID_PROFILO, idProfiloAttivo);
                luciService.putExtra(LuciIntentService.EXTRA_ID, idLuce);
                luciService.putExtra(LuciIntentService.EXTRA_NOME, nomeLuce);

                luciService.putExtra(LuciIntentService.EXTRA_STATO, isChecked ? context.getString(R.string.luce_accesa) : context.getString(R.string.luce_spenta));
                context.startService(luciService);
            }
        });

        return convertView;
    }

    public void add(Luci luce) {
        elencoLuci.add(luce);
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<Luci> luci) {
        elencoLuci.clear();
        elencoLuci.addAll(luci);
        notifyDataSetChanged();
    }

}
