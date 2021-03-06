package io.alelli.simplehome2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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

import java.util.List;

import io.alelli.simplehome2.dao.ProfiloDAO;
import io.alelli.simplehome2.models.Profilo;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static Context context;
    private Drawer drawer;
    private Toolbar toolbar;

    private ProfiloDAO profiloDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        final SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        profiloDAO = new ProfiloDAO(context, prefs);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        List<Profilo> profili = profiloDAO.findAll();
        Profilo profiloAttivo = profiloDAO.findById(profiloDAO.getIdProfileActive());
        if(profili.size() == 0) {
            final Intent intent = new Intent(context, WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        } else if(profiloAttivo == null) {
            profiloDAO.activateProfile(new Long(profili.get(0).getId()));
        }

        IProfile[] profiles = new IProfile[profili.size()];
        for (int i = 0; i < profili.size(); i++) {
            ProfileDrawerItem profile = new ProfileDrawerItem()
                    .withIdentifier(profili.get(i).getId().intValue())
                    .withName(profili.get(i).getEtichetta())
                    .withEmail(profili.get(i).getUrl())
                    .withNameShown(true)
                    .withIcon(getResources().getDrawable(R.drawable.logo));
            profiles[i] = profile;
        }

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.side_nav_bar)
                .addProfiles(profiles)
                //.withProfileImagesVisible(false)
                //.withProfileImagesClickable(false)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        profiloDAO.activateProfile(new Long(profile.getIdentifier()));

                        // HomeFragment
                        openFragment(new HomeFragment(), null);
                        return false;
                    }
                })
                .build();


        Long idProfiloAttivo = profiloDAO.getIdProfileActive();
        headerResult.setActiveProfile(idProfiloAttivo.intValue());;

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
        PrimaryDrawerItem allarme = new PrimaryDrawerItem()
                .withName(R.string.allarme_nav)
                .withIcon(R.drawable.ic_security_black_24px);
        PrimaryDrawerItem interruzioni = new PrimaryDrawerItem()
                .withName(R.string.interruzioni_nav)
                .withIcon(R.drawable.ic_dnd_forwardslash_24dp);
        PrimaryDrawerItem cam = new PrimaryDrawerItem()
                .withName(R.string.cam_nav)
                .withIcon(R.drawable.ic_videocam_black_24dp);
        SecondaryDrawerItem settings = new SecondaryDrawerItem()
                .withName(R.string.settings_nav).withTextColorRes(R.color.secondary_text)
                .withIcon(R.drawable.ic_settings_24dp);
        SecondaryDrawerItem about = new SecondaryDrawerItem()
                .withName(R.string.info_nav).withTextColorRes(R.color.secondary_text)
                .withIcon(R.drawable.ic_info_24dp);

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(home, luci, temperature, allarme, interruzioni, cam, new DividerDrawerItem(), settings, about)
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
                                openFragment(new AllarmeFragment(), getString(R.string.allarme_title_fragment));
                                break;
                            case 5:
                                openFragment(new InterruzioniFragment(), getString(R.string.interruzioni_title_fragment));
                                break;
                            case 6:
                                openFragment(new CamFragment(), getString(R.string.cam_title_fragment));
                                break;
                            case 8:
                                final Intent intent = new Intent(context, SettingsActivity.class);
                                startActivity(intent);
                                break;
                            case 9:
                                openFragment(new InfoFragment(), getString(R.string.info_title_fragment));
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
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        drawer.closeDrawer();
    }

    boolean dblClickToExit = false;
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            if(dblClickToExit) {
                super.onBackPressed();
            }
            dblClickToExit = true;
            Toast.makeText(context, getString(R.string.dbl_back_to_exit), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dblClickToExit = false;
                }
            }, 2000);
        }
    }
}
