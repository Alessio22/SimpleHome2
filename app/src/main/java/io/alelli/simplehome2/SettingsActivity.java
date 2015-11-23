package io.alelli.simplehome2;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import io.alelli.simplehome2.adapters.ProfiliAdapter;
import io.alelli.simplehome2.models.Profilo;

public class SettingsActivity extends AppCompatActivity {
    private static Context context;
    private ProfiliAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        context = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAdapter = new ProfiliAdapter(context);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {Profilo profilo = new Profilo();
                profilo.setId(new Long(mAdapter.getCount()+1));
                profilo.setEtichetta("Profilo " + profilo.getId());
                profilo.setUrl("http://" + profilo.getEtichetta().trim() + "/");
                mAdapter.add(profilo);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Profilo profilo = new Profilo();
        profilo.setId(1l);
        profilo.setEtichetta("Profilo " + profilo.getId());
        profilo.setUrl("http://" + profilo.getEtichetta().trim() + "/");
        mAdapter.add(profilo);
    }

}
