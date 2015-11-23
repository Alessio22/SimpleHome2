package io.alelli.simplehome2;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AllarmeFragment extends Fragment {
    public static Fragment newInstance(Context context) {
        TemperatureFragment f = new TemperatureFragment();

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_allarme, null);
        return root;
    }
}
