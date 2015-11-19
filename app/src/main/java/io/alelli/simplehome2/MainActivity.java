package io.alelli.simplehome2;

import android.content.Context;
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
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Context context;

    private Drawer drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // TODO elenco profili dal DB
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.side_nav_bar)
                .addProfiles(
                        new ProfileDrawerItem().withName("Casa").withEmail("http://example.com/casa").withIcon(getResources()
                                .getDrawable(R.drawable.profile6))
                ).addProfiles(
                        new ProfileDrawerItem().withName("Fuoricasa").withEmail("http://example.com/fuoricasa").withIcon(getResources()
                                .getDrawable(R.drawable.profile3))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        Log.i(TAG, "onProfileChanged");
                        // TODO set default profile

                        return false;
                    }
                })
                .build();

        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem home = new PrimaryDrawerItem()
                .withName(R.string.home_nav).withIcon(R.drawable.ic_home_24dp);
        PrimaryDrawerItem luci = new PrimaryDrawerItem()
                .withName(R.string.luci_nav).withIcon(R.drawable.ic_wb_incandescent_24dp);
        PrimaryDrawerItem temperature = new PrimaryDrawerItem()
                .withName(R.string.temperature_nav).withIcon(R.drawable.ic_ac_unit_black_24px);
        PrimaryDrawerItem allarme = new PrimaryDrawerItem()
                .withName(R.string.allarme_nav).withIcon(R.drawable.ic_security_black_24px)
                .withSelectable(false);
        PrimaryDrawerItem settings = new PrimaryDrawerItem()
                .withName(R.string.settings_nav).withIcon(R.drawable.ic_settings_24dp);

        //create the drawer and remember the `Drawer` result object
        drawer = new DrawerBuilder()
            .withActivity(this)
            .withToolbar(toolbar)
            .withAccountHeader(headerResult)
            .addDrawerItems(home, luci, temperature, allarme, settings)
            .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                @Override
                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                    Fragment fragment = null;
                    String title = getString(R.string.app_name);

                    switch (position) {
                        case 1:
                            fragment = new HomeFragment();
                            title = getString(R.string.home_title_frangment);
                            break;
                        case 2:
                            fragment = new LuciFragment();
                            title = getString(R.string.luci_title_fragment);
                            break;
                        case 3:
                            fragment = new TemperatureFragment();
                            title = getString(R.string.temperature_title_fragment);
                            break;
                        case 4:
                            fragment = new AllarmeFragment();
                            title  = getString(R.string.allarme_title_fragment);
                            break;
                        case 5:
                            fragment = new SettingsFragment();
                            title = getString(R.string.settings_title_fragment);
                            break;
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
                    return true;
                }
            })
            .build();

        // HomeFragment
        Fragment fragment = new HomeFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //if (drawer.isDrawerOpen(GravityCompat.START)) {
            //drawer.closeDrawer(GravityCompat.START);
        if(drawer.isDrawerOpen()) {
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
            openFragment(new SettingsFragment());

            // set the toolbar title
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(getString(R.string.settings_title_fragment));
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
    }

}
