package io.alelli.simplehome2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import io.alelli.simplehome2.dao.ProfiloDAO;
import io.alelli.simplehome2.models.Profilo;

public class WelcomeActivity extends AppCompatActivity {
    private Context context;
    private ProfiloDAO profiloDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        context = this;
        profiloDAO = new ProfiloDAO(context);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText etichettaEditText = (EditText) findViewById(R.id.dialog_profilo_etichetta);
                final EditText urlEditText = (EditText) findViewById(R.id.dialog_profilo_url);
                final EditText usernameEditText = (EditText) findViewById(R.id.dialog_profilo_username);
                final EditText passwordEditText = (EditText) findViewById(R.id.dialog_profilo_password);

                String etichetta = etichettaEditText.getText().toString();
                String url = urlEditText.getText().toString();
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                Profilo profilo = new Profilo(context, etichetta, url, username, password);
                profiloDAO.insert(profilo);

                final Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
    }

}
