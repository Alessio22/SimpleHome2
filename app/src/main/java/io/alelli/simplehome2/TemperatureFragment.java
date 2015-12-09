package io.alelli.simplehome2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import io.alelli.simplehome2.adapters.TemperatureAdapter;
import io.alelli.simplehome2.dao.ProfiloDAO;
import io.alelli.simplehome2.models.Temperature;
import io.alelli.simplehome2.services.TemperatureIntentService;


public class TemperatureFragment extends Fragment {
    private static final String TAG = "TemperatureFragment";
    private static Context context;
    private Intent temperatureService;
    private Long idProfiloAttivo;

    private AbsListView mListView;
    private TemperatureAdapter mAdapter;
    private SwipeRefreshLayout swipeContainer;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive");
            Log.i(TAG, intent.getAction());

            if(TemperatureIntentService.BROADCAST_LIST.equals(intent.getAction())) {
                String errore = intent.getStringExtra(TemperatureIntentService.EXTRA_ERROR);
                if(errore == null) {
                    String json = intent.getStringExtra(TemperatureIntentService.EXTRA_LIST);
                    Type listType = new TypeToken<ArrayList<Temperature>>() {}.getType();
                    ArrayList<Temperature> listaTemperature = new Gson().fromJson(json, listType);
                    mAdapter.clear();
                    for (Temperature t : listaTemperature) {
                        if(t.getTxtTemp() != null) {
                            mAdapter.add(t);
                        }
                    }
                    String message = "Aggiornamento completato";
                    if(getView() != null) {
                        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
                    }
                    swipeContainer.setRefreshing(false);
                } else {
                    if(getView() != null) {
                        Snackbar.make(getView(), errore, Snackbar.LENGTH_LONG).show();
                    }
                }
            }

            if(TemperatureIntentService.BROADCAST_UP.equals(intent.getAction())) {
                String errore = intent.getStringExtra(TemperatureIntentService.EXTRA_ERROR);
                if(errore == null) {
                    boolean result = intent.getBooleanExtra(TemperatureIntentService.EXTRA_CHANGE_RESULT, false);
                    String nome = intent.getStringExtra(TemperatureIntentService.EXTRA_NOME);

                    String message = getString(R.string.errore_change_temperatura);
                    if (result) {
                        message = "Temperatura " + nome + " alzata";
                    }
                    if(getView() != null) {
                        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
                    }
                    swipeContainer.setRefreshing(true);
                    temperatureService = new Intent(context, TemperatureIntentService.class);
                    temperatureService.setAction(TemperatureIntentService.ACTION_LIST);
                    temperatureService.putExtra(TemperatureIntentService.EXTRA_ID_PROFILO, idProfiloAttivo);
                    context.startService(temperatureService);
                } else {
                    if(getView() != null) {
                        Snackbar.make(getView(), errore, Snackbar.LENGTH_LONG).show();
                    }
                }
            }

            if(TemperatureIntentService.BROADCAST_DOWN.equals(intent.getAction())) {
                String errore = intent.getStringExtra(TemperatureIntentService.EXTRA_ERROR);
                if(errore == null) {
                    boolean result = intent.getBooleanExtra(TemperatureIntentService.EXTRA_CHANGE_RESULT, false);
                    String nome = intent.getStringExtra(TemperatureIntentService.EXTRA_NOME);

                    String message = getString(R.string.errore_change_temperatura);
                    if (result) {
                        message = "Temperatura " + nome + " abbassata";
                    }
                    if(getView() != null) {
                        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
                    }
                    swipeContainer.setRefreshing(true);
                    temperatureService = new Intent(context, TemperatureIntentService.class);
                    temperatureService.setAction(TemperatureIntentService.ACTION_LIST);
                    temperatureService.putExtra(TemperatureIntentService.EXTRA_ID_PROFILO, idProfiloAttivo);
                    context.startService(temperatureService);
                } else {
                    if(getView() != null) {
                        Snackbar.make(getView(), errore, Snackbar.LENGTH_LONG).show();
                    }
                }
            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        context = getContext();
        final SharedPreferences prefs = this.getActivity().getPreferences(Context.MODE_PRIVATE);
        ProfiloDAO profiloDAO = new ProfiloDAO(prefs);
        idProfiloAttivo = profiloDAO.getIdProfileActive();
        Log.i(TAG, "onCreate: " + idProfiloAttivo);

        mAdapter = new TemperatureAdapter(context);

        temperatureService = new Intent(context, TemperatureIntentService.class);
        temperatureService.setAction(TemperatureIntentService.ACTION_LIST);
        temperatureService.putExtra(TemperatureIntentService.EXTRA_ID_PROFILO, idProfiloAttivo);
        context.startService(temperatureService);

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TemperatureIntentService.BROADCAST_LIST);
        intentFilter.addAction(TemperatureIntentService.BROADCAST_UP);
        intentFilter.addAction(TemperatureIntentService.BROADCAST_DOWN);
        context.registerReceiver(receiver, intentFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_temperature, container, false);

        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                temperatureService.setAction(TemperatureIntentService.ACTION_LIST);
                temperatureService.putExtra(TemperatureIntentService.EXTRA_ID_PROFILO, idProfiloAttivo);
                context.startService(temperatureService);
            }
        });
        swipeContainer.setColorSchemeResources(R.color.primary);
        swipeContainer.setRefreshing(true);

        return view;
    }

    @Override
    public void onDetach() {
        Log.i(TAG, "onDetach");
        super.onDetach();
        context.unregisterReceiver(receiver);
    }
}
