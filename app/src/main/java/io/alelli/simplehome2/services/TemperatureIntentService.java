package io.alelli.simplehome2.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;
import android.util.Xml;

import com.google.gson.Gson;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

import io.alelli.simplehome2.R;
import io.alelli.simplehome2.dao.ProfiloDAO;
import io.alelli.simplehome2.models.Profilo;
import io.alelli.simplehome2.models.Temperature;

public class TemperatureIntentService extends IntentService {
    private static final String TAG = "TemperatureService";

    public static final String ACTION_LIST = "io.alelli.simplehome2.services.TemperatureIntentService.action.LIST";
    public static final String ACTION_UP = "io.alelli.simplehome2.services.TemperatureIntentService.action.UP";
    public static final String ACTION_DOWN = "io.alelli.simplehome2.services.TemperatureIntentService.action.DOWN";

    public static final String BROADCAST_LIST = "io.alelli.simplehome2.services.TemperatureIntentService.broadcast.LIST";
    public static final String BROADCAST_UP= "io.alelli.simplehome2.services.TemperatureIntentService.broadcast.UP";
    public static final String BROADCAST_DOWN= "io.alelli.simplehome2.services.TemperatureIntentService.broadcast.DOWN";

    public static final String EXTRA_ID_PROFILO = "io.alelli.simplehome2.services.TemperatureIntentService.extra.ID_PROFILO";
    public static final String EXTRA_ID = "io.alelli.simplehome2.services.TemperatureIntentService.extra.ID";
    public static final String EXTRA_NOME = "io.alelli.simplehome2.services.TemperatureIntentService.extra.NOME";
    public static final String EXTRA_LIST = "io.alelli.simplehome2.services.TemperatureIntentService.extra.LIST";
    public static final String EXTRA_CHANGE_RESULT = "io.alelli.simplehome2.services.TemperatureIntentService.extra.change.RESULT";
    public static final String EXTRA_ERROR = "io.alelli.simplehome2.services.TemperatureIntentService.extra.ERROR";

    private static Long idProfiloAttivo;
    private static String API = "";
    private static String USERNAME = "";
    private static String PASSWORD = "";

    public TemperatureIntentService() {
        super("TemperatureIntentService");
        Log.i(TAG, "TemperatureIntentService()");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "onHandleIntent");

        final String action = intent.getAction();
        Log.d(TAG, "action: " + action);

        idProfiloAttivo = intent.getLongExtra(EXTRA_ID_PROFILO, 0);
        final Integer id = intent.getIntExtra(EXTRA_ID, 0);
        final String nome = intent.getStringExtra(EXTRA_NOME);

        ProfiloDAO dao = new ProfiloDAO(getBaseContext());
        Profilo profilo = dao.findById(idProfiloAttivo);

        API = profilo.getUrl();
        USERNAME = profilo.getUsername();
        PASSWORD = profilo.getPassword();

        switch (action) {
            case ACTION_LIST:
                final Intent intentBroadcastList = new Intent(BROADCAST_LIST);
                String jsonList = null;
                try {
                    jsonList = tempDesc();
                } catch (MalformedURLException e) {
                    Log.e(TAG, "onHandleIntent: MalformedURLException: " + getString(R.string.errore_host_non_valido));
                    intentBroadcastList.putExtra(EXTRA_ERROR, getString(R.string.errore_host_non_valido));
                } catch (UnknownHostException e) {
                    Log.e(TAG, "onHandleIntent: UnknownHostException: " + getString(R.string.errore_host_non_raggiungibile));
                    intentBroadcastList.putExtra(EXTRA_ERROR, getString(R.string.errore_host_non_raggiungibile));
                } catch (Exception e){
                    e.printStackTrace();
                    Log.e(TAG, "onHandleIntent: Exception: " + getString(R.string.errore_generico));
                    intentBroadcastList.putExtra(EXTRA_ERROR, getString(R.string.errore_generico));
                }
                intentBroadcastList.putExtra(EXTRA_LIST, jsonList);
                sendBroadcast(intentBroadcastList);
                break;
            case ACTION_UP:
                // TODO chiamata aumenta temperatura
                break;
            case ACTION_DOWN:
                // TODO chiamata diminuisci temperatura
                break;
        }
    }

    private String tempDesc() throws XmlPullParserException, IOException {
        Log.i(TAG, "tempDesc");
        String json = "";
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(API + "user/termo.xml");
            httpURLConnection = (HttpURLConnection) url.openConnection();
            String userCredentials = USERNAME+":"+PASSWORD;
            String basicAuth = "Basic " + Base64.encodeToString(userCredentials.getBytes("UTF-8"), Base64.DEFAULT);
            httpURLConnection.setRequestProperty("Authorization", basicAuth);
            json = parse(httpURLConnection.getInputStream());
        } finally {
            if (httpURLConnection != null) httpURLConnection.disconnect();
        }
        return json;
    }

    private static final String ns = null;
    public String parse(InputStream in) throws XmlPullParserException, IOException {
        String result = "";
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            result = new Gson().toJson(readDescTemp(parser));
        } finally {
            in.close();
        }
        return result;
    }

    private ArrayList<Temperature> readDescTemp(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<Temperature> temperature = new ArrayList();
        for (int i = 0; i<24; i++) {
            temperature.add(new Temperature());
        }

        parser.require(XmlPullParser.START_TAG, ns, "response");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if(name.startsWith("temp")) {
                Integer pos = Integer.parseInt( name.substring(4) );
                if (parser.next() == XmlPullParser.TEXT) {
                    temperature.get(pos).setTemperatura(parser.getText());
                    parser.nextTag();
                }
            } else if (name.startsWith("setpoint")) {
                Integer pos = Integer.parseInt( name.substring(8) );
                if (parser.next() == XmlPullParser.TEXT) {
                    temperature.get(pos).setSetPoint(parser.getText());
                    parser.nextTag();
                }
            } else if (name.startsWith("txttemp")) {
                Integer pos = Integer.parseInt( name.substring(7) );
                if (parser.next() == XmlPullParser.TEXT) {
                    temperature.get(pos).setTxtTemp(parser.getText());
                    parser.nextTag();
                }
            } else {
                skip(parser);
            }
        }
        ArrayList<Temperature> result = new ArrayList();
        for (Temperature t : temperature) {
            if(t.getTxtTemp() != null) {
                result.add(t);
            }
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
