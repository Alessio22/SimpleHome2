package io.alelli.simplehome2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;

import io.alelli.simplehome2.adapters.ProfiliAdapter;
import io.alelli.simplehome2.models.Profilo;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";
    private static Context context;

    private AbsListView mListView;
    private ProfiliAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        context = this;

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

                        Profilo profilo = new Profilo();
                        profilo.setId(new Long(mAdapter.getCount() + 1));
                        // TODO prenderla dal dialog
                        profilo.setImg(BitmapFactory.decodeResource(context.getResources(), R.drawable.profile6));
                        profilo.setEtichetta(etichettaEditText.getText().toString());
                        profilo.setUrl(urlEditText.getText().toString());
                        profilo.setUsername(usernameEditText.getText().toString());
                        profilo.setPassword(passwordEditText.getText().toString());

                        // TODO salvare sul db
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

        // TODO prenderli dal db
        Profilo profilo = new Profilo();
        profilo.setId(1l);
        profilo.setImg(BitmapFactory.decodeResource(context.getResources(), R.drawable.profile6));
        profilo.setEtichetta("Profilo " + profilo.getId());
        profilo.setUrl("http://" + profilo.getEtichetta().trim() + "/");
        mAdapter.add(profilo);
    }

}
