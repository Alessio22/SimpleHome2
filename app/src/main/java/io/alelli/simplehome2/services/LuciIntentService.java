package io.alelli.simplehome2.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class LuciIntentService extends IntentService {
    private static final String TAG = "LuciIntentService";

    public static final String ACTION_LIST = "io.alelli.simplehome2.services.LuciIntentService.action.LIST";
    public static final String ACTION_STATO = "io.alelli.simplehome2.services.LuciIntentService.action.STATO";
    public static final String ACTION_CHANGE = "io.alelli.simplehome2.services.LuciIntentService.action.CHANGE";

    public static final String BROADCAST_LIST = "io.alelli.simplehome2.services.LuciIntentService.broadcast.LIST";
    public static final String BROADCAST_STATO = "io.alelli.simplehome2.services.LuciIntentService.broadcast.STATO";
    public static final String BROADCAST_CHANGE = "io.alelli.simplehome2.services.LuciIntentService.broadcast.CHANGE";

    public static final String EXTRA_ID = "io.alelli.simplehome2.services.LuciIntentService.extra.ID";
    public static final String EXTRA_NOME = "io.alelli.simplehome2.services.LuciIntentService.extra.NOME";
    public static final String EXTRA_LIST = "io.alelli.simplehome2.services.LuciIntentService.extra.LIST";
    public static final String EXTRA_STATO = "io.alelli.simplehome2.services.LuciIntentService.extra.STATO";

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

        final Integer id = intent.getIntExtra(EXTRA_ID, 0);
        final String nome = intent.getStringExtra(EXTRA_NOME);
        final String stato = intent.getStringExtra(EXTRA_STATO);

        // TODO prenderla dal profilo attivo nel DB
        String result = null;
        API = "http://example.com/";
        USERNAME = "username";
        PASSWORD = "password";

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
        String xml = "";

        return xml;
    }
    private String stato() {
        Log.i(TAG, "stato");
        String xml = "";

        return xml;
    }
    private String changeStatoLuci(Integer id) {
        Log.i(TAG, "changeStatoLuci id: " + id);
        String xml = "";

        return xml;
    }



}
