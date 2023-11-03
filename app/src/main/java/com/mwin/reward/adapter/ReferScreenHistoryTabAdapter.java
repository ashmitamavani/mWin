package com.mwin.reward.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.mwin.reward.fragments.ReferPointHistoryFragment;
import com.mwin.reward.fragments.ReferUserHistoryFragment;

import java.util.ArrayList;

public class ReferScreenHistoryTabAdapter extends FragmentPagerAdapter {
    public ArrayList<String> mFragmentItems;
    private ReferPointHistoryFragment fragmentPointHistory;
    private ReferUserHistoryFragment fragmentUserHistory;

    public ReferScreenHistoryTabAdapter(FragmentManager fm, ArrayList<String> fragmentItems) {
        super(fm);
        this.mFragmentItems = fragmentItems;
        fragmentPointHistory = new ReferPointHistoryFragment();
        fragmentUserHistory = new ReferUserHistoryFragment();
    }

    @Override
    public Fragment getItem(int i) {
        if (i == 0) {
            return fragmentPointHistory;
        } else if (i == 1) {
            return fragmentUserHistory;
        }
        return null;
    }

    public ReferPointHistoryFragment getReferPointHistoryFragment() {
        return fragmentPointHistory;
    }

    public ReferUserHistoryFragment getReferUserHistoryFragment() {
        return fragmentUserHistory;
    }

    @Override
    public int getCount() {
        return mFragmentItems.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentItems.get(position);
    }
}
