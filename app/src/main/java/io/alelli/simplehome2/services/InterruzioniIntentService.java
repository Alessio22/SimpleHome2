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
import io.alelli.simplehome2.models.Interruzione;
import io.alelli.simplehome2.models.Profilo;

public class InterruzioniIntentService extends IntentService {
    private static final String TAG = "Inter..IntentService";

    public static final String ACTION_LIST = "io.alelli.simplehome2.services.InterruzioniIntentService.action.LIST";

    public static final String BROADCAST_LIST = "io.alelli.simplehome2.services.InterruzioniIntentService.broadcast.LIST";

    public static final String EXTRA_ID_PROFILO = "io.alelli.simplehome2.services.InterruzioniIntentService.extra.ID_PROFILO";
    public static final String EXTRA_ID = "io.alelli.simplehome2.services.InterruzioniIntentService.extra.ID";
    public static final String EXTRA_NOME = "io.alelli.simplehome2.services.InterruzioniIntentService.extra.NOME";
    public static final String EXTRA_LIST = "io.alelli.simplehome2.services.InterruzioniIntentService.extra.LIST";
    public static final String EXTRA_STATO = "io.alelli.simplehome2.services.InterruzioniIntentService.extra.STATO";
    public static final String EXTRA_ERROR = "io.alelli.simplehome2.services.InterruzioniIntentService.extra.ERROR";

    private static Long idProfiloAttivo;
    private static String API = "";
    private static String USERNAME = "";
    private static String PASSWORD = "";

    public InterruzioniIntentService() {
        super("InterruzioniIntentService");
        Log.i(TAG, "InterruzioniIntentService()");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "onHandleIntent");

        final String action = intent.getAction();
        Log.d(TAG, "action: " + action);

        idProfiloAttivo = intent.getLongExtra(EXTRA_ID_PROFILO, 0);

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
                    jsonList = interruzioniDesc();
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
        }
    }

    private String interruzioniDesc() throws Exception {
        Log.i(TAG, "interruzioniDesc");
        String json = "";
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(API + "user/zone_intr.xml");
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
            result = new Gson().toJson(readDescInterruzioni(parser));
        } finally {
            in.close();
        }
        return result;
    }
    // TODO da aggiustare
    private ArrayList<Interruzione> readDescInterruzioni(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.i(TAG, "Interruzione");
        ArrayList<Interruzione> interruzioni = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "response");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.startsWith("descF")) {
                Integer id = Integer.parseInt( name.substring(5) );
                String desc = null;
                if (parser.next() == XmlPullParser.TEXT) {
                    desc = parser.getText();
                    parser.nextTag();
                }
                if(desc != null) {
                    interruzioni.add(new Interruzione(id, desc));
                }
            } else if(name.startsWith("inF")) {
                Integer id = Integer.parseInt( name.substring(3) );
                boolean stato = false;
                if (parser.next() == XmlPullParser.TEXT) {
                    stato = !"0".equals(parser.getText());
                    parser.nextTag();
                }
                if(!stato) {
                    interruzioni.remove(id);
                }
            } else {
                skip(parser);
            }
        }
        return interruzioni;
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
