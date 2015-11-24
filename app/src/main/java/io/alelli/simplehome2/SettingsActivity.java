package io.alelli.simplehome2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import io.alelli.simplehome2.adapters.ProfiliAdapter;
import io.alelli.simplehome2.dao.ProfiloDAO;
import io.alelli.simplehome2.models.Profilo;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";
    private static Context context;

    private AbsListView mListView;
    private ProfiliAdapter mAdapter;

    private ProfiloDAO profiloDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        context = this;
        profiloDAO = new ProfiloDAO(context);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAdapter = new ProfiliAdapter(context);

        mListView = (AbsListView) findViewById(R.id.profili_listView);
        mListView.setAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.dialog_new_profile, (ViewGroup) findViewById(R.id.content_layout));

                final EditText etichettaEditText = (EditText) layout.findViewById(R.id.dialog_profilo_etichetta);
                final EditText urlEditText = (EditText) layout.findViewById(R.id.dialog_profilo_url);
                final EditText usernameEditText = (EditText) layout.findViewById(R.id.dialog_profilo_username);
                final EditText passwordEditText = (EditText) layout.findViewById(R.id.dialog_profilo_password);

                builder.setView(layout);

                builder.setTitle(R.string.dialog_new_profile_title);
                builder.setPositiveButton(R.string.dialog_new_profile_save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i(TAG, "Dialog Ok");
                        String etichetta = etichettaEditText.getText().toString();
                        String url = urlEditText.getText().toString();
                        String username = usernameEditText.getText().toString();
                        String password = passwordEditText.getText().toString();

                        Profilo profilo = new Profilo(context, etichetta, url, username, password);
                        profiloDAO.insert(profilo);
                        mAdapter.add(profilo);
                    }
                });

                builder.setNegativeButton(R.string.dialog_new_profile_close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i(TAG, "Dialog close");
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        List<Profilo> profili = profiloDAO.findAll();
        Log.i(TAG, "size profile: " + profili.size());
        mAdapter.addAll(profili);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
                final int pos = position;
                final Profilo profilo = mAdapter.getItem(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                // TODO layout di info profilo
                View layout = inflater.inflate(R.layout.dialog_info_profile, (ViewGroup) findViewById(R.id.content_layout));
                // TODO textView
                final TextView etichettaTextView = (TextView) layout.findViewById(R.id.dialog_info_profilo_etichetta);
                etichettaTextView.setText(profilo.getEtichetta());
                final TextView urlTextView = (TextView) layout.findViewById(R.id.dialog_info_profilo_url);
                urlTextView.setText(profilo.getUrl());
                final TextView usernameTextView = (TextView) layout.findViewById(R.id.dialog_info_profilo_username);
                usernameTextView.setText(profilo.getUsername());

                builder.setView(layout);

                builder.setTitle(R.string.dialog_info_profile_title);
                builder.setPositiveButton(R.string.dialog_info_profile_delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        profiloDAO.delete(profilo.getId());
                        mAdapter.remove(pos);
                    }
                });
                builder.setNegativeButton(R.string.dialog_info_profile_close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

}
