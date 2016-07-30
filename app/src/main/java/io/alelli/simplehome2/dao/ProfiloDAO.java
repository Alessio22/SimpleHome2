package io.alelli.simplehome2.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import io.alelli.simplehome2.models.Profilo;

public class ProfiloDAO extends SQLiteOpenHelper {
    private static final String TAG = "ProfiloDAO";
    private static final int version = 2;

    public static final String ACTIVE_PROFILE = "active.profile";
    private SharedPreferences prefs;

    static final String DBNAME = "simpleHomeDB";
    static final String TABLE_PROFILO="profilo";
    static final String TABLE_PROFILO_ID="id";
    static final String TABLE_PROFILO_ETICHETTA="etichetta";
    static final String TABLE_PROFILO_URL="url";
    static final String TABLE_PROFILO_USERNAME="username";
    static final String TABLE_PROFILO_PASSWORD="password";
    static final String TABLE_PROFILO_URL_CAM="urlCam";
    static final String TABLE_PROFILO_USERNAME_CAM="usernameCam";
    static final String TABLE_PROFILO_PASSWORD_CAM="passwordCam";

    final static String[] COLUMNS = {
            TABLE_PROFILO_ID,
            TABLE_PROFILO_ETICHETTA,
            TABLE_PROFILO_URL,
            TABLE_PROFILO_USERNAME,
            TABLE_PROFILO_PASSWORD,
            TABLE_PROFILO_URL_CAM,
            TABLE_PROFILO_USERNAME_CAM,
            TABLE_PROFILO_PASSWORD_CAM
    };

    private Context context;

    public ProfiloDAO(Context context) {
        super(context, DBNAME, null, version);
        this.context = context;
    }

    public ProfiloDAO(Context context, SharedPreferences prefs) {
        super(context, DBNAME, null, version);
        this.context = context;
        this.prefs = prefs;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PROFILO + " " +
                "(" + TABLE_PROFILO_ID + " INTEGER PRIMARY KEY , " +
                TABLE_PROFILO_ETICHETTA + " TEXT," +
                TABLE_PROFILO_URL + " TEXT," +
                TABLE_PROFILO_USERNAME + " TEXT," +
                TABLE_PROFILO_PASSWORD + " TEXT" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion == 1 && newVersion == 2) {
            db.execSQL("ALTER TABLE " + TABLE_PROFILO + " ADD " +
                TABLE_PROFILO_URL_CAM + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_PROFILO + " ADD " +
                    TABLE_PROFILO_USERNAME_CAM + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_PROFILO + " ADD " +
                    TABLE_PROFILO_PASSWORD_CAM + " TEXT");
        }else {
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_PROFILO);
            onCreate(db);
        }
    }

    public ArrayList<Profilo> findAll() {
        ArrayList<Profilo> profili = new ArrayList<>();
        Cursor cur = this.getReadableDatabase().query(TABLE_PROFILO, COLUMNS, null, new String[]{}, null, null, null);
        while (cur.moveToNext()) {
            Profilo profilo = new Profilo();
            profilo.setId(cur.getLong(cur.getColumnIndex(TABLE_PROFILO_ID)));
            profilo.setEtichetta(cur.getString(cur.getColumnIndex(TABLE_PROFILO_ETICHETTA)));
            profilo.setUrl(cur.getString(cur.getColumnIndex(TABLE_PROFILO_URL)));
            profilo.setUsername(cur.getString(cur.getColumnIndex(TABLE_PROFILO_USERNAME)));
            profilo.setPassword(cur.getString(cur.getColumnIndex(TABLE_PROFILO_PASSWORD)));
            profilo.setUrlCam(cur.getString(cur.getColumnIndex(TABLE_PROFILO_URL_CAM)));
            profilo.setUsernameCam(cur.getString(cur.getColumnIndex(TABLE_PROFILO_USERNAME_CAM)));
            profilo.setPasswordCam(cur.getString(cur.getColumnIndex(TABLE_PROFILO_PASSWORD_CAM)));
            profili.add(profilo);
        }
        return profili;
    }

    public Profilo findById(Long id) {
        Cursor cur = this.getReadableDatabase().query(TABLE_PROFILO, COLUMNS, TABLE_PROFILO_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        Profilo profilo = null;
        if(cur.moveToFirst()) {
            profilo = new Profilo();
            profilo.setId(cur.getLong(cur.getColumnIndex(TABLE_PROFILO_ID)));
            profilo.setEtichetta(cur.getString(cur.getColumnIndex(TABLE_PROFILO_ETICHETTA)));
            profilo.setUrl(cur.getString(cur.getColumnIndex(TABLE_PROFILO_URL)));
            profilo.setUsername(cur.getString(cur.getColumnIndex(TABLE_PROFILO_USERNAME)));
            profilo.setPassword(cur.getString(cur.getColumnIndex(TABLE_PROFILO_PASSWORD)));
            profilo.setUrlCam(cur.getString(cur.getColumnIndex(TABLE_PROFILO_URL_CAM)));
            profilo.setUsernameCam(cur.getString(cur.getColumnIndex(TABLE_PROFILO_USERNAME_CAM)));
            profilo.setPasswordCam(cur.getString(cur.getColumnIndex(TABLE_PROFILO_PASSWORD_CAM)));
        }
        return profilo;
    }

    public boolean insert(Profilo profilo) {
        try {
            SQLiteDatabase db=this.getWritableDatabase();
            ContentValues cv=new ContentValues();
            cv.put(TABLE_PROFILO_ID, profilo.getId());
            cv.put(TABLE_PROFILO_ETICHETTA, profilo.getEtichetta());
            cv.put(TABLE_PROFILO_URL, profilo.getUrl());
            cv.put(TABLE_PROFILO_USERNAME, profilo.getUsername());
            cv.put(TABLE_PROFILO_PASSWORD, profilo.getPassword());
            cv.put(TABLE_PROFILO_URL_CAM, profilo.getUrlCam());
            cv.put(TABLE_PROFILO_USERNAME_CAM, profilo.getUsernameCam());
            cv.put(TABLE_PROFILO_PASSWORD_CAM, profilo.getPasswordCam());
            Long result = db.insert(TABLE_PROFILO, TABLE_PROFILO_ID, cv);
            Log.d(TAG, "result: " + result);
            db.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean delete(Long id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_PROFILO, TABLE_PROFILO_ID + "=?", new String[]{String.valueOf(id)});
            db.close();
        } catch (Exception e) {
            return false;
        }
        return true;
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
