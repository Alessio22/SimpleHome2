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

import io.alelli.simplehome2.adapters.InterruzioniAdapter;
import io.alelli.simplehome2.dao.ProfiloDAO;
import io.alelli.simplehome2.models.Interruzione;
import io.alelli.simplehome2.services.InterruzioniIntentService;

public class InterruzioniFragment extends Fragment {
    private static final String TAG = "InterruzioniFragment";
    private static Context context;
    private Intent interruzioniService;
    private Long idProfiloAttivo;

    private AbsListView mListView;
    private InterruzioniAdapter mAdapter;
    private SwipeRefreshLayout swipeContainer;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive");
            Log.i(TAG, intent.getAction());

            if(InterruzioniIntentService.BROADCAST_LIST.equals(intent.getAction())) {
                String errore = intent.getStringExtra(InterruzioniIntentService.EXTRA_ERROR);
                Log.d(TAG, "onReceive: " + errore);
                if(errore == null) {
                    String json = intent.getStringExtra(InterruzioniIntentService.EXTRA_LIST);
                    Type listType = new TypeToken<ArrayList<Interruzione>>() {}.getType();
                    ArrayList<Interruzione> interruzioniList = new Gson().fromJson(json, listType);
                    mAdapter.addAll(interruzioniList);

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

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        context = getContext();
        final SharedPreferences prefs = this.getActivity().getPreferences(Context.MODE_PRIVATE);
        ProfiloDAO profiloDAO = new ProfiloDAO(context, prefs);
        idProfiloAttivo = profiloDAO.getIdProfileActive();
        Log.i(TAG, "onCreate: " + idProfiloAttivo);

        mAdapter = new InterruzioniAdapter(context);

        interruzioniService = new Intent(context, InterruzioniIntentService.class);
        interruzioniService.setAction(InterruzioniIntentService.ACTION_LIST);
        interruzioniService.putExtra(InterruzioniIntentService.EXTRA_ID_PROFILO, idProfiloAttivo);
        context.startService(interruzioniService);

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(InterruzioniIntentService.BROADCAST_LIST);
        context.registerReceiver(receiver, intentFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_interruzioni_list, container, false);

        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                interruzioniService.setAction(InterruzioniIntentService.ACTION_LIST);
                interruzioniService.putExtra(InterruzioniIntentService.EXTRA_ID_PROFILO, idProfiloAttivo);
                context.startService(interruzioniService);
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
