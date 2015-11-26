package io.alelli.simplehome2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import java.net.MalformedURLException;
import java.net.URL;

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
    }

    public void saveProfile(View fab) {
        final EditText etichettaEditText = (EditText) findViewById(R.id.dialog_profilo_etichetta);
        final EditText urlEditText = (EditText) findViewById(R.id.dialog_profilo_url);
        final EditText usernameEditText = (EditText) findViewById(R.id.dialog_profilo_username);
        final EditText passwordEditText = (EditText) findViewById(R.id.dialog_profilo_password);

        Boolean hasError = false;
        String etichetta = etichettaEditText.getText().toString();
        if (etichetta == null || "".equals(etichetta)) {
            etichettaEditText.setError("Etichetta obbligatoria");
            hasError = true;
        }
        String url = urlEditText.getText().toString();
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

        if(!hasError) {
            Profilo profilo = new Profilo(context, etichetta, url, username, password);
            profiloDAO.insert(profilo);

            final Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
    }
}
