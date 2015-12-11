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
import io.alelli.simplehome2.models.Allarme;
import io.alelli.simplehome2.models.Profilo;

public class AllarmeIntentService extends IntentService {
    private static final String TAG = "AllarmeIntentService";

    public static final String ACTION_LIST = "io.alelli.simplehome2.services.AllarmeIntentService.action.LIST";
    public static final String ACTION_CHANGE = "io.alelli.simplehome2.services.AllarmeIntentService.action.CHANGE";

    public static final String BROADCAST_LIST = "io.alelli.simplehome2.services.AllarmeIntentService.broadcast.LIST";
    public static final String BROADCAST_CHANGE = "io.alelli.simplehome2.services.AllarmeIntentService.broadcast.CHANGE";

    public static final String EXTRA_ID_PROFILO = "io.alelli.simplehome2.services.AllarmeIntentService.extra.ID_PROFILO";
    public static final String EXTRA_ID = "io.alelli.simplehome2.services.AllarmeIntentService.extra.ID";
    public static final String EXTRA_NOME = "io.alelli.simplehome2.services.AllarmeIntentService.extra.NOME";
    public static final String EXTRA_LIST = "io.alelli.simplehome2.services.AllarmeIntentService.extra.LIST";
    public static final String EXTRA_STATO = "io.alelli.simplehome2.services.AllarmeIntentService.extra.STATO";
    public static final String EXTRA_CHANGE_RESULT = "io.alelli.simplehome2.services.AllarmeIntentService.extra.change.RESULT";
    public static final String EXTRA_ERROR = "io.alelli.simplehome2.services.AllarmeIntentService.extra.ERROR";

    private static Long idProfiloAttivo;
    private static String API = "";
    private static String USERNAME = "";
    private static String PASSWORD = "";

    public AllarmeIntentService() {
        super("AllarmeIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "onHandleIntent");

        final String action = intent.getAction();
        Log.d(TAG, "action: " + action);

        idProfiloAttivo = intent.getLongExtra(EXTRA_ID_PROFILO, 0);
        final String id = intent.getStringExtra(EXTRA_ID);
        final String nome = intent.getStringExtra(EXTRA_NOME);
        final String stato = intent.getStringExtra(EXTRA_STATO);

        ProfiloDAO dao = new ProfiloDAO(getBaseContext());
        Profilo profilo = dao.findById(idProfiloAttivo);
        Log.i(TAG, "getView: " + idProfiloAttivo);

        API = profilo.getUrl();
        USERNAME = profilo.getUsername();
        PASSWORD = profilo.getPassword();

        switch (action) {
            case ACTION_LIST:

                final Intent intentBroadcastList = new Intent(BROADCAST_LIST);
                String jsonList = null;
                try {
                    jsonList = allarmi();
                } catch (MalformedURLException e) {
                    Log.e(TAG, "onHandleIntent: MalformedURLException: " + getString(R.string.errore_host_non_valido));
                    intentBroadcastList.putExtra(EXTRA_ERROR, getString(R.string.errore_host_non_valido));
                } catch (UnknownHostException e) {
                    Log.e(TAG, "onHandleIntent: UnknownHostException: " + getString(R.string.errore_host_non_raggiungibile));
                    intentBroadcastList.putExtra(EXTRA_ERROR, getString(R.string.errore_host_non_raggiungibile));
                } catch (Exception e){
                    Log.e(TAG, "onHandleIntent: Exception: " + getString(R.string.errore_generico));
                    intentBroadcastList.putExtra(EXTRA_ERROR, getString(R.string.errore_generico));
                }
                intentBroadcastList.putExtra(EXTRA_LIST, jsonList);
                sendBroadcast(intentBroadcastList);
                break;
            case ACTION_CHANGE:
                final Intent intentBroadcastChange = new Intent(BROADCAST_CHANGE);
                boolean result = false;
                try {
                    result = changeStatoAllarme(id);
                } catch (MalformedURLException e) {
                    Log.e(TAG, "onHandleIntent: MalformedURLException: " + getString(R.string.errore_host_non_valido));
                    intentBroadcastChange.putExtra(EXTRA_ERROR, getString(R.string.errore_host_non_valido));
                } catch (UnknownHostException e) {
                    Log.e(TAG, "onHandleIntent: UnknownHostException: " + getString(R.string.errore_host_non_raggiungibile));
                    intentBroadcastChange.putExtra(EXTRA_ERROR, getString(R.string.errore_host_non_raggiungibile));
                } catch (Exception e){
                    Log.e(TAG, "onHandleIntent: Exception: " + getString(R.string.errore_generico));
                    intentBroadcastChange.putExtra(EXTRA_ERROR, getString(R.string.errore_generico));
                }
                intentBroadcastChange.putExtra(EXTRA_CHANGE_RESULT, result);
                intentBroadcastChange.putExtra(EXTRA_NOME, nome);
                intentBroadcastChange.putExtra(EXTRA_STATO, stato);
                sendBroadcast(intentBroadcastChange);
                break;
        }
    }

    private String allarmi() throws Exception {
        Log.i(TAG, "allarmi");
        String json = "";
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(API + "user/aree_intr.xml");
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
            result = new Gson().toJson(readAllarmi(parser));
        } finally {
            in.close();
        }
        return result;
    }

    private ArrayList<Allarme> readAllarmi(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<Allarme> allarmi = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "response");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Integer i = allarmi.size() - 1;
            if (name.startsWith("statoAreaP1")) {
                if (parser.next() == XmlPullParser.TEXT) {
                    allarmi.get(i).setStatoP1("1".equals(parser.getText()));
                    parser.nextTag();
                }
            } else if (name.startsWith("statoAreaP2")) {
                if (parser.next() == XmlPullParser.TEXT) {
                    allarmi.get(i).setStatoP2("1".equals(parser.getText()));
                    parser.nextTag();
                }
            } else if(name.startsWith("statoArea")) {
                if (parser.next() == XmlPullParser.TEXT) {
                    Allarme allarme = new Allarme();
                    allarme.setId( allarmi.size() );
                    allarme.setStato( "1".equals(parser.getText()) );
                    allarmi.add(allarme);
                    parser.nextTag();
                }
            } else if (name.startsWith("allarmeArea")) {
                removeByIdAllarme(allarmi, Integer.parseInt(name.substring(11)));
                if (parser.next() == XmlPullParser.TEXT) {
                    parser.nextTag();
                }
            } else {
                skip(parser);
            }
        }
        return allarmi;
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

    private void removeByIdAllarme(ArrayList<Allarme> allarmi, Integer id) {
        for (int i = 0; i < allarmi.size(); i++) {
            if(allarmi.get(i).getId() == id) {
                allarmi.remove(i);
            }
        }
    }

    private boolean changeStatoAllarme(String id) throws Exception {
        Log.i(TAG, "changeStatoAllarme id: " + id);
        boolean result = false;
        HttpURLConnection httpURLConnection = null;
        try {
            Log.i(TAG, "changeStatoAllarme: "+ API + "user/statoAree.cgi?statoArea="+id);
            URL url = new URL(API + "user/statoAree.cgi?statoArea="+id);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            String userCredentials = USERNAME+":"+PASSWORD;
            String basicAuth = "Basic " + Base64.encodeToString(userCredentials.getBytes("UTF-8"), Base64.DEFAULT);
            httpURLConnection.setRequestProperty("Authorization", basicAuth);
            if(httpURLConnection.getResponseCode() == 200) {
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) httpURLConnection.disconnect();
        }
        return result;
    }
}
