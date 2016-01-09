package io.alelli.simplehome2;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
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

import io.alelli.simplehome2.adapters.LuciAdapter;
import io.alelli.simplehome2.dao.ProfiloDAO;
import io.alelli.simplehome2.models.Luci;
import io.alelli.simplehome2.services.LuciIntentService;

public class LuciFragment extends Fragment {
    private static final String TAG = "LuciFragment";
    private static Context context;
    private Intent luciService;
    private Long idProfiloAttivo;

    private AbsListView mListView;
    private LuciAdapter mAdapter;
    private SwipeRefreshLayout swipeContainer;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive");
            Log.d(TAG, intent.getAction());

            if(LuciIntentService.BROADCAST_LIST.equals(intent.getAction())) {
                String errore = intent.getStringExtra(LuciIntentService.EXTRA_ERROR);
                Log.d(TAG, "onReceive: " + errore);
                if(errore == null) {
                    String json = intent.getStringExtra(LuciIntentService.EXTRA_LIST);
                    Type listType = new TypeToken<ArrayList<Luci>>() {}.getType();
                    ArrayList<Luci> listaLuci = new Gson().fromJson(json, listType);
                    mAdapter.addAll(listaLuci);

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

            if(LuciIntentService.BROADCAST_CHANGE.equals(intent.getAction())) {
                String errore = intent.getStringExtra(LuciIntentService.EXTRA_ERROR);
                if(errore == null) {
                    boolean result = intent.getBooleanExtra(LuciIntentService.EXTRA_CHANGE_RESULT, false);
                    String stato = intent.getStringExtra(LuciIntentService.EXTRA_STATO);
                    String nome = intent.getStringExtra(LuciIntentService.EXTRA_NOME);

                    String message = getString(R.string.errore_change_stato_luci);
                    if (result) {
                        message = "Luce " + nome + " " + stato;
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
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        context = getContext();
        final SharedPreferences prefs = this.getActivity().getPreferences(Context.MODE_PRIVATE);
        ProfiloDAO profiloDAO = new ProfiloDAO(context, prefs);
        idProfiloAttivo = profiloDAO.getIdProfileActive();
        Log.d(TAG, "onCreate: " + idProfiloAttivo);

        mAdapter = new LuciAdapter(context);

        luciService = new Intent(context, LuciIntentService.class);
        luciService.setAction(LuciIntentService.ACTION_LIST);
        luciService.putExtra(LuciIntentService.EXTRA_ID_PROFILO, idProfiloAttivo);
        context.startService(luciService);

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LuciIntentService.BROADCAST_LIST);
        intentFilter.addAction(LuciIntentService.BROADCAST_CHANGE);
        context.registerReceiver(receiver, intentFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_luci_list, container, false);

        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                luciService.setAction(LuciIntentService.ACTION_LIST);
                luciService.putExtra(LuciIntentService.EXTRA_ID_PROFILO, idProfiloAttivo);
                context.startService(luciService);
            }
        });
        swipeContainer.setColorSchemeResources(R.color.primary);
        swipeContainer.setRefreshing(true);

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            Activity a = getActivity();
            if(a != null) a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        }
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");
        super.onDetach();
        context.stopService(luciService);
        context.unregisterReceiver(receiver);
    }

}
