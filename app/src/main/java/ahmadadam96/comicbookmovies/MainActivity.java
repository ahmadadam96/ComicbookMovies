package ahmadadam96.comicbookmovies;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

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
        //Set the colors of the text in the tabLayout
        tabLayout.setTabTextColors(Color.LTGRAY, Color.WHITE);
        //Set the color of the tabLayout
        tabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        //Removes the border under the action bar
        getSupportActionBar().setElevation(0);
    }

    //Adapter for the view pager in use
    private class MyPagerAdapter extends FragmentPagerAdapter {
        SparseArrayCompat<Fragment> registeredFragments = new SparseArrayCompat<>();

        String page1 = "All";
        String page2 = "MCU";
        String page3 = "DC";
        String page4 = "Fox";

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //Setting the ViewPager tab names
            //For all movies
            if (position == 0) {
                return page1;
            }
            //For MCU movies
            if (position == 1) {
                return page2;
            }
            //For DC movies
            if (position == 2) {
                return page3;
            }
            if (position == 3){
                return page4;
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
                    return MainFragment.newInstance(page1);
                //MCU movies
                case 1:
                    return MainFragment.newInstance(page2);
                //DC movies
                case 2:
                    return MainFragment.newInstance(page3);
                case 3:
                    return MainFragment.newInstance(page4);
                default:
                    return null;
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }

        @Override
        //Method to correspond to the number of tabs used
        public int getCount() {
            return 4;
        }
    }
}
