package io.alelli.simplehome2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.ArrayList;

import io.alelli.simplehome2.dao.ProfiloDAO;
import io.alelli.simplehome2.models.Profilo;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static Context context;
    private Drawer drawer;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        final SharedPreferences prefs = getPreferences(MODE_PRIVATE);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ArrayList<Profilo> profili = new ProfiloDAO(context).findAll();
        if(profili.size() == 0) {
            // TODO start WelcomeActivity
        }
        IProfile[] profiles = new IProfile[profili.size()];
        for (int i = 0; i < profili.size(); i++) {
            ProfileDrawerItem profile = new ProfileDrawerItem()
                    .withIdentifier(profili.get(i).getId().intValue())
                    .withName(profili.get(i).getEtichetta())
                    .withEmail(profili.get(i).getUrl())
                    .withIcon(getResources().getResourceName(R.raw.profile6));
            profiles[i] = profile;
        }

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.side_nav_bar)
                .addProfiles(profiles)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        Log.i(TAG, "onProfileChanged id: " + profile.getIdentifier());
                        // TODO set profilo attivo
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt(ProfiloDAO.ACTIVE_PROFILE, profile.getIdentifier());
                        editor.commit();
                        // HomeFragment
                        openFragment(new HomeFragment(), null);
                        return false;
                    }
                })
                .build();

        // TODO query db per recuperare l'id del profilo attivo
        Integer idProfiloAttivo = prefs.getInt(ProfiloDAO.ACTIVE_PROFILE, 0);
        Log.i(TAG, "idProfiloAttivo: " + idProfiloAttivo);
        headerResult.setActiveProfile(idProfiloAttivo);

        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem home = new PrimaryDrawerItem()
                .withName(R.string.home_nav)
                .withIcon(R.drawable.ic_home_24dp);
        PrimaryDrawerItem luci = new PrimaryDrawerItem()
                .withName(R.string.luci_nav)
                .withIcon(R.drawable.ic_wb_incandescent_24dp);
        PrimaryDrawerItem temperature = new PrimaryDrawerItem()
                .withName(R.string.temperature_nav)
                .withIcon(R.drawable.ic_ac_unit_black_24px);
        PrimaryDrawerItem allarme = new PrimaryDrawerItem().withSelectable(false)
                .withName(R.string.allarme_nav)
                .withIcon(R.drawable.ic_security_black_24px);
        SecondaryDrawerItem settings = new SecondaryDrawerItem()
                .withName(R.string.settings_nav).withTextColorRes(R.color.secondary_text)
                .withIcon(R.drawable.ic_settings_24dp);
        SecondaryDrawerItem about = new SecondaryDrawerItem()
                .withName(R.string.about_nav).withTextColorRes(R.color.secondary_text)
                .withIcon(R.drawable.ic_info_24dp);

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(home, luci, temperature, allarme, new DividerDrawerItem(), settings, about)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position) {
                            case 1:
                                openFragment(new HomeFragment(), null);
                                break;
                            case 2:
                                openFragment(new LuciFragment(), getString(R.string.luci_title_fragment));
                                break;
                            case 3:
                                openFragment(new TemperatureFragment(), getString(R.string.temperature_title_fragment));
                                break;
                            case 4:
                                //openFragment(new AllarmeFragment(), getString(R.string.allarme_title_fragment));
                                break;
                            case 6:
                                final Intent intent = new Intent(context, SettingsActivity.class);
                                startActivity(intent);
                                break;
                            case 7:
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle(R.string.about_dialog_title);
                                builder.setPositiveButton(R.string.about_dialog_close, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Log.i(TAG, "Dialog Ok");
                                    }
                                });

                                AlertDialog dialog = builder.create();
                                dialog.show();
                                break;
                        }
                        return true;
                    }
                })
                .build();

        // HomeFragment
        openFragment(new HomeFragment(), null);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            final Intent intent = new Intent(context, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openFragment(Fragment fragment, String title) {
        if (title == null || "".equals(title)) {
            title = getString(R.string.app_name);
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        drawer.closeDrawer();
    }

}
