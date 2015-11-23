package io.alelli.simplehome2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import java.util.ArrayList;

import io.alelli.simplehome2.adapters.LuciAdapter;
import io.alelli.simplehome2.models.Luci;
import io.alelli.simplehome2.services.LuciIntentService;

public class LuciFragment extends Fragment {
    private static final String TAG = "LuciFragment";
    private static Context context;
    private Intent luciService;

    private AbsListView mListView;
    private LuciAdapter mAdapter;
    private SwipeRefreshLayout swipeContainer;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive");
            Log.i(TAG, intent.getAction());

            if(LuciIntentService.BROADCAST_LIST.equals(intent.getAction())) {
                String xml = intent.getStringExtra(LuciIntentService.EXTRA_LIST);
                Log.i(TAG, xml);
                ArrayList<Luci> listaLuci = xmlToList(xml);
                for (Luci luce: listaLuci) {
                    mAdapter.add(luce);
                }
                mAdapter.add(new Luci((mAdapter.getCount() + 1), "Stanza " + (mAdapter.getCount() + 1), false));
                luciService.setAction(LuciIntentService.ACTION_STATO);
                context.startService(luciService);
            }

            if(LuciIntentService.BROADCAST_STATO.equals(intent.getAction())) {
                String xml = intent.getStringExtra(LuciIntentService.EXTRA_LIST);
                Log.i(TAG, xml);

                String message = "Aggiornamento completato";
                if(getView() != null) {
                    Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
                }
                swipeContainer.setRefreshing(false);
            }

            if(LuciIntentService.BROADCAST_CHANGE.equals(intent.getAction())) {
                String stato = intent.getStringExtra(LuciIntentService.EXTRA_STATO);
                String nome = intent.getStringExtra(LuciIntentService.EXTRA_NOME);

                String message = "Luce " + nome + " " + stato;
                if(getView() != null) {
                    Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
                }
            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        context = getContext();

        mAdapter = new LuciAdapter(context);

        luciService = new Intent(context, LuciIntentService.class);
        luciService.setAction(LuciIntentService.ACTION_LIST);
        context.startService(luciService);

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LuciIntentService.BROADCAST_LIST);
        intentFilter.addAction(LuciIntentService.BROADCAST_STATO);
        intentFilter.addAction(LuciIntentService.BROADCAST_CHANGE);
        context.registerReceiver(receiver, intentFilter);

        // TODO da rimuovere
        mAdapter.add(new Luci(1, "Cucina", false));
        mAdapter.add(new Luci(2, "Ingresso",false));
        mAdapter.add(new Luci(3, "Salone",true));
        mAdapter.add(new Luci(4, "Balcone",false));
        mAdapter.add(new Luci(5, "Camera", true));
        mAdapter.add(new Luci(6, "Bagno", false));
        mAdapter.add(new Luci(7, "Scale", false));
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
                luciService.setAction(LuciIntentService.ACTION_STATO);
                context.startService(luciService);

                final IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(LuciIntentService.BROADCAST_STATO);
                context.registerReceiver(receiver, intentFilter);
            }
        });
        swipeContainer.setColorSchemeResources(R.color.primary);

        return view;
    }

    @Override
    public void onDetach() {
        Log.i(TAG, "onDetach");
        super.onDetach();
        context.unregisterReceiver(receiver);
    }

    public void setEmptyText(CharSequence emptyText) {
        Log.i(TAG, "setEmptyText");
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    private ArrayList<Luci> xmlToList(String xml) {
        ArrayList<Luci> listaLuci = new ArrayList<>();

        return listaLuci;
    }
}
