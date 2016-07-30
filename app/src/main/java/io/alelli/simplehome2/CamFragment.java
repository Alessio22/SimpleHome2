package io.alelli.simplehome2;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

import io.alelli.simplehome2.dao.ProfiloDAO;
import io.alelli.simplehome2.models.Profilo;
import io.alelli.simplehome2.services.LuciIntentService;

public class CamFragment extends Fragment {
    private static final String TAG = "CamFragment";
    private static Context context;
    private IntentFilter intentFilterReceiver = new IntentFilter();
    private Intent luciService;
    private Long idProfiloAttivo;

    private String url;
    private String userCredentials;
    private final Integer delay = 100;
    private Timer timer = new Timer();
    private ImageView camPic;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        context = getContext();
        final SharedPreferences prefs = this.getActivity().getPreferences(Context.MODE_PRIVATE);
        ProfiloDAO profiloDAO = new ProfiloDAO(context, prefs);
        idProfiloAttivo = profiloDAO.getIdProfileActive();
        Log.d(TAG, "idProfiloAttivo: " + idProfiloAttivo);

        Profilo profilo = profiloDAO.findById(idProfiloAttivo);
        userCredentials = profilo.getUsernameCam()+":"+profilo.getPasswordCam();
        Log.d(TAG, "userCredentials: " + userCredentials);
        url = profilo.getUrlCam();
        Log.d(TAG, "url: " + url);

        intentFilterReceiver.addAction(LuciIntentService.BROADCAST_CHANGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cam, container, false);

        this.camPic = (ImageView) view.findViewById(R.id.cam_pic);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Integer idLuce = 1;
                String nomeLuce = "";
                Log.d(TAG, "idLuce: " + idLuce + "toggle");

                luciService = new Intent(context, LuciIntentService.class);
                luciService.setAction(LuciIntentService.ACTION_CHANGE);
                luciService.putExtra(LuciIntentService.EXTRA_ID_PROFILO, idProfiloAttivo);
                luciService.putExtra(LuciIntentService.EXTRA_ID, idLuce);
                luciService.putExtra(LuciIntentService.EXTRA_NOME, nomeLuce);

                luciService.putExtra(LuciIntentService.EXTRA_STATO, "");
                context.startService(luciService);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        callAsynchronousTask();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        timer.cancel();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");
        super.onDetach();
        timer.cancel();
    }

    private void callAsynchronousTask() {
        Log.d(TAG, "callAsynchronousTask: " + url);
        timer = new Timer();
        final Handler handler = new Handler();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {

                        new CamPicTask().execute(url);

                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, delay);
    }

    private class CamPicTask extends AsyncTask<String, Void, Bitmap> {
        private static final String TAG = "LuciFragment";

        protected Bitmap doInBackground(String... urls) {
            Log.d(TAG, "doInBackground: ");
            return download_Image(urls[0]);
        }

        protected void onPostExecute(Bitmap result) {
            Log.d(TAG, "onPostExecute: ");
            camPic.setImageBitmap(result);
        }

        private Bitmap download_Image(String url) {
            Log.d(TAG, "download_Image: " + url);
            Bitmap bm = null;
            try {
                String basicAuth = "Basic " + Base64.encodeToString(userCredentials.getBytes("UTF-8"), Base64.DEFAULT);

                URL aURL = new URL(url);
                URLConnection conn = aURL.openConnection();
                conn.setRequestProperty("Authorization", basicAuth);
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
            } catch (IOException e) {
                Log.e("Hub","Error getting the image from server : " + e.getMessage().toString());
            }
            return bm;
        }

    }

}
