package io.alelli.simplehome2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class HomeFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Button btnLuci = (Button) view.findViewById(R.id.button_luci);
        btnLuci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new LuciFragment();
                openFragment(fragment);
            }
        });

        Button btnTemperature = (Button) view.findViewById(R.id.button_temperature);
        btnTemperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new TemperatureFragment();
                openFragment(fragment);
            }
        });

        Button btnAllarme = (Button) view.findViewById(R.id.button_allarme);
        btnAllarme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new AllarmeFragment();
                openFragment(fragment);
            }
        });

        Button btnInterruzioni = (Button) view.findViewById(R.id.button_interruzioni);
        btnInterruzioni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new InterruzioniFragment();
                openFragment(fragment);
            }
        });

        Button btnCam = (Button) view.findViewById(R.id.button_cam);
        btnCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new CamFragment();
                openFragment(fragment);
            }
        });

        Button btnSettings = (Button) view.findViewById(R.id.button_settings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void openFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.replace(R.id.content_frame, fragment);
            ft.addToBackStack(null);
            ft.commit();
        }
    }


}
