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
import java.net.URL;
import java.util.ArrayList;

import io.alelli.simplehome2.dao.ProfiloDAO;
import io.alelli.simplehome2.models.Luci;
import io.alelli.simplehome2.models.Profilo;

public class LuciIntentService extends IntentService {
    private static final String TAG = "LuciIntentService";

    public static final String ACTION_LIST = "io.alelli.simplehome2.services.LuciIntentService.action.LIST";
    public static final String ACTION_STATO = "io.alelli.simplehome2.services.LuciIntentService.action.STATO";
    public static final String ACTION_CHANGE = "io.alelli.simplehome2.services.LuciIntentService.action.CHANGE";

    public static final String BROADCAST_LIST = "io.alelli.simplehome2.services.LuciIntentService.broadcast.LIST";
    public static final String BROADCAST_STATO = "io.alelli.simplehome2.services.LuciIntentService.broadcast.STATO";
    public static final String BROADCAST_CHANGE = "io.alelli.simplehome2.services.LuciIntentService.broadcast.CHANGE";

    public static final String EXTRA_ID_PROFILO = "io.alelli.simplehome2.services.LuciIntentService.extra.ID_PROFILO";
    public static final String EXTRA_ID = "io.alelli.simplehome2.services.LuciIntentService.extra.ID";
    public static final String EXTRA_NOME = "io.alelli.simplehome2.services.LuciIntentService.extra.NOME";
    public static final String EXTRA_LIST = "io.alelli.simplehome2.services.LuciIntentService.extra.LIST";
    public static final String EXTRA_STATO = "io.alelli.simplehome2.services.LuciIntentService.extra.STATO";

    private static Long idProfiloAttivo;
    private static String API = "";
    private static String USERNAME = "";
    private static String PASSWORD = "";

    public LuciIntentService() {
        super("LuciIntentService");
        Log.i(TAG, "LuciIntentService()");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "onHandleIntent");

        final String action = intent.getAction();
        Log.d(TAG, "action: " + action);

        idProfiloAttivo = intent.getLongExtra(EXTRA_ID_PROFILO, 0);
        final Integer id = intent.getIntExtra(EXTRA_ID, 0);
        final String nome = intent.getStringExtra(EXTRA_NOME);
        final String stato = intent.getStringExtra(EXTRA_STATO);

        ProfiloDAO dao = new ProfiloDAO(getBaseContext());
        Profilo profilo = dao.findById(idProfiloAttivo);

        String result = null;
        API = profilo.getUrl();
        USERNAME = profilo.getUsername();
        PASSWORD = profilo.getPassword();

        switch (action) {
            case ACTION_LIST:
                result = luciDesc();
                final Intent intentBroadcastList = new Intent(BROADCAST_LIST);
                intentBroadcastList.putExtra(EXTRA_LIST, result);
                sendBroadcast(intentBroadcastList);
                break;
            case ACTION_STATO:
                result = stato();
                final Intent intentBroadcastStato = new Intent(BROADCAST_STATO);
                intentBroadcastStato.putExtra(EXTRA_LIST, result);
                sendBroadcast(intentBroadcastStato);
                break;
            case ACTION_CHANGE:
                changeStatoLuci(id);
                final Intent intentBroadcastChange = new Intent(BROADCAST_CHANGE);
                intentBroadcastChange.putExtra(EXTRA_NOME, nome);
                intentBroadcastChange.putExtra(EXTRA_STATO, stato);
                sendBroadcast(intentBroadcastChange);
                break;
        }
    }

    private String luciDesc() {
        Log.i(TAG, "luciDesc");
        String json = invokeApi(API + "user/luci_desc.xml");
        Log.i(TAG, json);
        return json;
    }
    private String stato() {
        Log.i(TAG, "stato");
        String xml = "";
        // TODO call stato
        return xml;
    }
    private String changeStatoLuci(Integer id) {
        Log.i(TAG, "changeStatoLuci id: " + id);
        String xml = "";
        // TODO call changeStatoLuci
        return xml;
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

        parser.require(XmlPullParser.START_TAG, ns, "response");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.startsWith("desc")) {
                Integer id = Integer.parseInt( name.substring(4) );
                String desc = null;
                if (parser.next() == XmlPullParser.TEXT) {
                    desc = parser.getText();
                    parser.nextTag();
                }
                if(desc != null) {
                    Log.i(TAG, id + ": " + desc);
                    luci.add(new Luci(id, desc, false));
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

    private String invokeApi(String urlStr) {
        String xml = "";
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(urlStr);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            String userCredentials = USERNAME+":"+PASSWORD;
            String basicAuth = "Basic " + Base64.encodeToString(userCredentials.getBytes("UTF-8"), Base64.DEFAULT);
            httpURLConnection.setRequestProperty ("Authorization", basicAuth);
            xml = parse(httpURLConnection.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) httpURLConnection.disconnect();
        }
        return xml;
    }

}
