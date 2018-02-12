package com.willbrom.forismaticquotes;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.willbrom.forismaticquotes.data.Quote;
import com.willbrom.forismaticquotes.fragments.FavoriteFragment;
import com.willbrom.forismaticquotes.fragments.MainFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivityFavorite extends AppCompatActivity {

    @BindView(R.id.toolBar)
    Toolbar toolBar;
    @BindView(R.id.tab)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);

        setSupportActionBar(toolBar);

        setupViewHolder(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewHolder(ViewPager viewPager) {
        ViewpagerAdapter adapter = new ViewpagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MainFragment(), "One");
        adapter.addFragment(new FavoriteFragment(), "Two");
        viewPager.setAdapter(adapter);
    }

    class ViewpagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentListTitle = new ArrayList<>();

        public ViewpagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentListTitle.get(position);
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentListTitle.add(title);
        }
    }
}
