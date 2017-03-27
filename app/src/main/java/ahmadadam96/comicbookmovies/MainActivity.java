package ahmadadam96.comicbookmovies;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Got the reference to the view pager
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        //Set the adapter for the view pager
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        //Got the reference for the tabLayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        //Set the tabLayout to belong to the view pager
        tabLayout.setupWithViewPager(viewPager);
        //Maximise the tabs to fill the tabLayout
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }

    //Adapter for the view pager in use
    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //Setting the ViewPager tab names
            //For all movies
            if (position == 0) {
                return "All";
            }
            //For MCU movies
            if (position == 1) {
                return "MCU";
            }
            //For DC movies
            if (position == 2) {
                return "DC";
            }
            //if it is an unknown page (should not happen) then set the title to unknown
            else return "Unknown";
        }

        @Override
        public Fragment getItem(int position) {
            //Passes the input of each fragment in order to filter the movies
            switch (position) {
                //All movies
                case 0:
                    return MainFragment.newInstance("All");
                //MCU movies
                case 1:
                    return MainFragment.newInstance("MCU");
                //DC movies
                case 2:
                    return MainFragment.newInstance("DC");
                default:
                    return null;
            }
        }

        @Override
        //Method to correspond to the number of tabs used
        public int getCount() {
            return 3;
        }
    }
}
