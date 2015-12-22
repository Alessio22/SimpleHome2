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
import io.alelli.simplehome2.models.Luci;
import io.alelli.simplehome2.models.Profilo;

public class LuciIntentService extends IntentService {
    private static final String TAG = "LuciIntentService";

    public static final String ACTION_LIST = "io.alelli.simplehome2.services.LuciIntentService.action.LIST";
    public static final String ACTION_CHANGE = "io.alelli.simplehome2.services.LuciIntentService.action.CHANGE";

    public static final String BROADCAST_LIST = "io.alelli.simplehome2.services.LuciIntentService.broadcast.LIST";
    public static final String BROADCAST_CHANGE = "io.alelli.simplehome2.services.LuciIntentService.broadcast.CHANGE";

    public static final String EXTRA_ID_PROFILO = "io.alelli.simplehome2.services.LuciIntentService.extra.ID_PROFILO";
    public static final String EXTRA_ID = "io.alelli.simplehome2.services.LuciIntentService.extra.ID";
    public static final String EXTRA_NOME = "io.alelli.simplehome2.services.LuciIntentService.extra.NOME";
    public static final String EXTRA_LIST = "io.alelli.simplehome2.services.LuciIntentService.extra.LIST";
    public static final String EXTRA_STATO = "io.alelli.simplehome2.services.LuciIntentService.extra.STATO";
    public static final String EXTRA_CHANGE_RESULT = "io.alelli.simplehome2.services.LuciIntentService.extra.change.RESULT";
    public static final String EXTRA_ERROR = "io.alelli.simplehome2.services.LuciIntentService.extra.ERROR";

    private static Long idProfiloAttivo;
    private static String API = "";
    private static String USERNAME = "";
    private static String PASSWORD = "";

    public LuciIntentService() {
        super("LuciIntentService");
        Log.d(TAG, "LuciIntentService()");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");

        final String action = intent.getAction();
        Log.d(TAG, "action: " + action);

        idProfiloAttivo = intent.getLongExtra(EXTRA_ID_PROFILO, 0);
        final Integer id = intent.getIntExtra(EXTRA_ID, 0);
        final String nome = intent.getStringExtra(EXTRA_NOME);
        final String stato = intent.getStringExtra(EXTRA_STATO);

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
                    jsonList = luciDesc();
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
                    result = changeStatoLuci(id);
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

    private String luciDesc() throws Exception {
        Log.d(TAG, "luciDesc");
        String json = "";
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(API + "user/luci_desc.xml");
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
            result = new Gson().toJson(readDescLuci(parser));
        } finally {
            in.close();
        }
        return result;
    }

    private ArrayList<Luci> readDescLuci(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<Luci> luci = new ArrayList();
        char[] charArray = null;

        parser.require(XmlPullParser.START_TAG, ns, "response");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.startsWith("stato")) {
                if (parser.next() == XmlPullParser.TEXT) {
                    charArray = parser.getText().toCharArray();
                    parser.nextTag();
                }
            } else if (name.startsWith("desc")) {
                Integer id = Integer.parseInt( name.substring(4) );
                String desc = null;
                if (parser.next() == XmlPullParser.TEXT) {
                    desc = parser.getText();
                    parser.nextTag();
                }
                if(desc != null) {
                    boolean accesa = false;
                    if(charArray != null) {
                        accesa = charArray[id] != '0' ? true : false;
                    }
                    luci.add(new Luci(id, desc, accesa));
                }
            } else {
                skip(parser);
            }
        }
        return luci;
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

    private boolean changeStatoLuci(Integer id) throws Exception {
        Log.d(TAG, "changeStatoLuci id: " + id);
        boolean result = false;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(API + "user/luci.cgi?luce="+id);
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
