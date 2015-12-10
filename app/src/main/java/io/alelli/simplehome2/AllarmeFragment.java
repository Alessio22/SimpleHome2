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

import io.alelli.simplehome2.adapters.AllarmiAdapter;
import io.alelli.simplehome2.dao.ProfiloDAO;
import io.alelli.simplehome2.models.Allarme;
import io.alelli.simplehome2.services.AllarmeIntentService;

public class AllarmeFragment extends Fragment {
    private static final String TAG = "AllarmeFragment";
    private static Context context;
    private Intent allarmeService;
    private Long idProfiloAttivo;

    private AbsListView mListView;
    private AllarmiAdapter mAdapter;
    private SwipeRefreshLayout swipeContainer;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive");
            Log.i(TAG, intent.getAction());

            if(AllarmeIntentService.BROADCAST_LIST.equals(intent.getAction())) {
                String errore = intent.getStringExtra(AllarmeIntentService.EXTRA_ERROR);
                Log.d(TAG, "onReceive: " + errore);
                if(errore == null) {
                    String json = intent.getStringExtra(AllarmeIntentService.EXTRA_LIST);
                    Type listType = new TypeToken<ArrayList<Allarme>>() {}.getType();
                    ArrayList<Allarme> listaAllarmi = new Gson().fromJson(json, listType);
                    mAdapter.addAll(listaAllarmi);

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

            if(AllarmeIntentService.BROADCAST_CHANGE.equals(intent.getAction())) {
                String errore = intent.getStringExtra(AllarmeIntentService.EXTRA_ERROR);
                if(errore == null) {
                    boolean result = intent.getBooleanExtra(AllarmeIntentService.EXTRA_CHANGE_RESULT, false);
                    String stato = intent.getStringExtra(AllarmeIntentService.EXTRA_STATO);
                    String nome = intent.getStringExtra(AllarmeIntentService.EXTRA_NOME);

                    String message = getString(R.string.errore_change_stato_luci);
                    if (result) {
                        message = "Allarme " + nome + " " + stato;
                    }
                    if(getView() != null) {
                        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
                    }
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

        mAdapter = new AllarmiAdapter(context);

        allarmeService = new Intent(context, AllarmeIntentService.class);
        allarmeService.setAction(AllarmeIntentService.ACTION_LIST);
        allarmeService.putExtra(AllarmeIntentService.EXTRA_ID_PROFILO, idProfiloAttivo);
        context.startService(allarmeService);

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AllarmeIntentService.BROADCAST_LIST);
        intentFilter.addAction(AllarmeIntentService.BROADCAST_CHANGE);
        context.registerReceiver(receiver, intentFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_luci, container, false);

        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                allarmeService.setAction(AllarmeIntentService.ACTION_LIST);
                allarmeService.putExtra(AllarmeIntentService.EXTRA_ID_PROFILO, idProfiloAttivo);
                context.startService(allarmeService);
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
