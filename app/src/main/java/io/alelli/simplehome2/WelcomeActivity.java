package io.alelli.simplehome2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

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
        final RadioButton metodoRadioHTTP = (RadioButton) findViewById(R.id.dialog_profilo_metodo_http);
        final RadioButton metodoRadioHTTPS = (RadioButton) findViewById(R.id.dialog_profilo_metodo_https);
        final EditText urlEditText = (EditText) findViewById(R.id.dialog_profilo_url);
        final EditText usernameEditText = (EditText) findViewById(R.id.dialog_profilo_username);
        final EditText passwordEditText = (EditText) findViewById(R.id.dialog_profilo_password);
        // CAM
        final RadioButton metodoCamRadioHTTP = (RadioButton) findViewById(R.id.dialog_profilo_cam_metodo_http);
        final RadioButton metodoCamRadioHTTPS = (RadioButton) findViewById(R.id.dialog_profilo_cam_metodo_https);
        final EditText urlCamEditText = (EditText) findViewById(R.id.dialog_profilo_cam_url);
        final EditText usernameCamEditText = (EditText) findViewById(R.id.dialog_profilo_cam_username);
        final EditText passwordCamEditText = (EditText) findViewById(R.id.dialog_profilo_cam_password);

        Boolean hasError = false;
        String etichetta = etichettaEditText.getText().toString();
        if ("".equals(etichetta)) {
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
        if ("".equals(password)) {
            passwordEditText.setError("Password obbligatoria");
            hasError = true;
        }
        // CAM
        String urlCam = "";
        if (metodoCamRadioHTTP.isChecked()) {
            urlCam = String.valueOf(metodoCamRadioHTTP.getText());
        } else if (metodoCamRadioHTTPS.isChecked()) {
            urlCam = String.valueOf(metodoCamRadioHTTPS.getText());
        }
        urlCam += urlCamEditText.getText().toString();
        try {
            new URL(urlCam);
        } catch (MalformedURLException malformedURLException) {
            urlCamEditText.setError("URL non valido");
            hasError = true;
        }
        String usernameCam = usernameCamEditText.getText().toString();
        String passwordCam = passwordCamEditText.getText().toString();
        if ("".equals(passwordCam)) {
            passwordEditText.setError("Password obbligatoria");
            hasError = true;
        }

        if(!hasError) {
            Profilo profilo = new Profilo(etichetta, url, username, password, urlCam, usernameCam, passwordCam);
            profiloDAO.insert(profilo);

            final Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
    }
}
