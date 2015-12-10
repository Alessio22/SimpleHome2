package io.alelli.simplehome2.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import io.alelli.simplehome2.models.Allarme;
import io.alelli.simplehome2.services.AllarmeIntentService;
import io.alelli.simplehome2.services.LuciIntentService;

public class AllarmiAdapter extends BaseAdapter {
    private static final String TAG = "TemperatureAdapter";

    private Context context;
    private Intent allarmeService;

    private ArrayList<Allarme> elencoAllarmi = new ArrayList<>();

    public AllarmiAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return elencoAllarmi.size();
    }

    @Override
    public Allarme getItem(int position) {
        return elencoAllarmi.get(position);
    }

    @Override
    public long getItemId(int position) {
        return elencoAllarmi.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Allarme allarme = elencoAllarmi.get(position);
        convertView = LayoutInflater.from(context).inflate(R.layout.list_item_allarmi, parent, false);

        final SharedPreferences prefs = ((MainActivity) context).getPreferences(Context.MODE_PRIVATE);
        ProfiloDAO profiloDAO = new ProfiloDAO(prefs);
        final Long idProfiloAttivo = profiloDAO.getIdProfileActive();
        allarmeService = new Intent(context, AllarmeIntentService.class);

        Switch switchStato = (Switch) convertView.findViewById(R.id.switch_stato);
        switchStato.setTag(allarme.getId());
        switchStato.setText("Area " + allarme.getId());
        switchStato.setChecked(allarme.getStato());

        // TODO dichiararne uno solo e settarlo a tutti e 3 gli switch
        switchStato.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Switch switchStato = (Switch) buttonView;
                Integer idArea = (Integer) switchStato.getTag();
                String nomeArea = (String) switchStato.getText();

                allarmeService.setAction(LuciIntentService.ACTION_CHANGE);
                allarmeService.putExtra(LuciIntentService.EXTRA_ID_PROFILO, idProfiloAttivo);
                allarmeService.putExtra(LuciIntentService.EXTRA_ID, idArea);
                allarmeService.putExtra(LuciIntentService.EXTRA_NOME, nomeArea);

                allarmeService.putExtra(LuciIntentService.EXTRA_STATO, isChecked ? context.getString(R.string.allarme_attivato) : context.getString(R.string.allarme_disattivato));
                context.startService(allarmeService);
            }
        });

        Switch switchP1 = (Switch) convertView.findViewById(R.id.switch_p1);
        switchP1.setTag("P1" + allarme.getId());
        switchP1.setText("Area " + allarme.getId() + " P1");
        switchP1.setChecked(allarme.getStatoP1());

        Switch switchP2 = (Switch) convertView.findViewById(R.id.switch_p2);
        switchP2.setTag("P2" + allarme.getId());
        switchP1.setText("Area " + allarme.getId() + " P2");
        switchP2.setChecked(allarme.getStatoP2());

        return convertView;
    }

    public void clear() {
        elencoAllarmi.clear();
        notifyDataSetChanged();
    }
    public void add(Allarme allarme) {
        elencoAllarmi.add(allarme);
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<Allarme> allarmi) {
        elencoAllarmi.clear();
        elencoAllarmi.addAll(allarmi);
        notifyDataSetChanged();
    }
}
