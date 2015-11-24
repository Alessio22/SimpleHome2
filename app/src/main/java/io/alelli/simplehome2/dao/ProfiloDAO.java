package io.alelli.simplehome2.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.List;

import io.alelli.simplehome2.models.Profilo;

/**
 * Created by Alessio on 23/11/2015.
 */
public class ProfiloDAO {
    private static final String TAG = "ProfiloDAO";
    private static final String ACTIVE_PROFILE = "active.profile";

    private Context context;
    private SharedPreferences prefs;

    public ProfiloDAO(Context context) {
        this.context = context;
    }

    public ProfiloDAO(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    public List<Profilo> findAll() {
        return Profilo.listAll(Profilo.class);
    }

    public void insert(Profilo profilo) {
        profilo.save();
        Log.i(TAG, "Profilo.count( " + Profilo.count(Profilo.class, null, null));
    }

    public void delete(Long id) {
        Profilo profilo = Profilo.findById(Profilo.class, id);
        profilo.delete();
    }

    public void activateProfile(Long id) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(ProfiloDAO.ACTIVE_PROFILE, id);
        editor.commit();
    }

    public Long getIdProfileActive() {
        return prefs.getLong(ProfiloDAO.ACTIVE_PROFILE, 0);
    }
}
