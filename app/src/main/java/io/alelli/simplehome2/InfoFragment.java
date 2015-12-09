package io.alelli.simplehome2;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicInteger;

public class InfoFragment extends Fragment {

    private AtomicInteger mCounter = new AtomicInteger();
    private Handler handler = new Handler();
    private Runnable mRunnable = new Runnable(){
        @Override
        public void run(){
            mCounter = new AtomicInteger();
        }
    };

    public InfoFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_info, container, false);

        TextView version = (TextView) view.findViewById(R.id.version);
        version.setText("v" + BuildConfig.VERSION_NAME);

        final ImageView logo = (ImageView) view.findViewById(R.id.imageView_logo);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(mRunnable);
                handler.postDelayed(mRunnable, 1000);
                if (mCounter.incrementAndGet() == 3) {
                    logo.setImageDrawable(view.getContext().getDrawable(R.drawable.logo_alt));
                }
            }
        });
        return view;
    }

}
