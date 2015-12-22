package io.alelli.simplehome2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
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
            public void onClick(final View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.dialog_new_profile, (ViewGroup) findViewById(R.id.content_layout));

                final EditText etichettaEditText = (EditText) layout.findViewById(R.id.dialog_profilo_etichetta);
                final RadioButton metodoRadioHTTP = (RadioButton) layout.findViewById(R.id.dialog_profilo_metodo_http);
                final RadioButton metodoRadioHTTPS = (RadioButton) layout.findViewById(R.id.dialog_profilo_metodo_https);
                final EditText urlEditText = (EditText) layout.findViewById(R.id.dialog_profilo_url);
                final EditText usernameEditText = (EditText) layout.findViewById(R.id.dialog_profilo_username);
                final EditText passwordEditText = (EditText) layout.findViewById(R.id.dialog_profilo_password);

                builder.setView(layout);

                builder.setTitle(R.string.dialog_new_profile_title);
                builder.setPositiveButton(R.string.dialog_new_profile_save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, "Dialog Ok");
                        Boolean hasError = false;
                        String etichetta = etichettaEditText.getText().toString();
                        if (etichetta == null || "".equals(etichetta)) {
                            etichettaEditText.setError("Etichetta obbligatoria");
                            hasError = true;
                        }

                        String url = "";
                        if (metodoRadioHTTP.isChecked()) {
                            url = String.valueOf(metodoRadioHTTP.getText());
                        } else if (metodoRadioHTTPS.isChecked()) {
                            url = String.valueOf(metodoRadioHTTPS.getText());
                        }
                        url += urlEditText.getText().toString();
                        if (url.charAt(url.length()-1) != '/') {
                            url += "/";
                        }
                        try {
                            new URL(url);
                        } catch (MalformedURLException malformedURLException) {
                            urlEditText.setError("URL non valido");
                            hasError = true;
                        }

                        String username = usernameEditText.getText().toString();
                        String password = passwordEditText.getText().toString();
                        if (password == null || "".equals(password)) {
                            passwordEditText.setError("Password obbligatoria");
                            hasError = true;
                        }

                        String message;
                        if (!hasError) {
                            Profilo profilo = new Profilo(etichetta, url, username, password);
                            profiloDAO.insert(profilo);
                            mAdapter.add(profilo);
                            message = "Profilo aggiunto";
                        } else {
                            message = "Profilo non aggiunto";
                        }

                        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
                    }
                });

                builder.setNegativeButton(R.string.dialog_new_profile_close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, "Dialog close");
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        List<Profilo> profili = profiloDAO.findAll();
        Log.d(TAG, "size profile: " + profili.size());
        mAdapter.addAll(profili);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
                final int pos = position;
                final Profilo profilo = mAdapter.getItem(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
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
