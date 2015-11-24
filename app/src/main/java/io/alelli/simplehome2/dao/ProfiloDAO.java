package io.alelli.simplehome2.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.ArrayList;

import io.alelli.simplehome2.R;
import io.alelli.simplehome2.models.Profilo;

/**
 * Created by Alessio on 23/11/2015.
 */
public class ProfiloDAO extends SQLiteOpenHelper {
    private static final String TAG = "ProfiloDAO";

    public static final String ACTIVE_PROFILE = "active.profile";

    static final String DBNAME = "simpleHomeDB";
    static final String TABLE_PROFILO="profilo";
    static final String TABLE_PROFILO_ID="id";
    static final String TABLE_PROFILO_ETICHETTA="etichetta";
    static final String TABLE_PROFILO_URL="url";
    static final String TABLE_PROFILO_USERNAME="username";
    static final String TABLE_PROFILO_PASSWORD="password";
    static final String TABLE_PROFILO_IMG="img";

    final static String[] COLUMNS = {
                                TABLE_PROFILO_ID,
                                TABLE_PROFILO_ETICHETTA,
                                TABLE_PROFILO_URL,
                                TABLE_PROFILO_USERNAME,
                                TABLE_PROFILO_PASSWORD
                            };

    private Context context;

    public ProfiloDAO(Context context) {
        super(context, DBNAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO aggiungere immagine
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
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_PROFILO);
        onCreate(db);
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
            // TODO prenderla dal db
            profilo.setImg(BitmapFactory.decodeResource(context.getResources(), R.raw.profile6));
            profili.add(profilo);
        }
        return profili;
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
            // TODO salvare anche l'immagine
            Long result = db.insert(TABLE_PROFILO, TABLE_PROFILO_ID, cv);
            Log.i(TAG, "result: " + result);
            db.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean delete(Profilo profilo) {
        try {
            SQLiteDatabase db=this.getWritableDatabase();
            db.delete(TABLE_PROFILO, TABLE_PROFILO_ID + "=?", new String [] { String.valueOf(profilo.getId()) });
            db.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
