package com.mwin.reward.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.mwin.reward.fragments.LuckyNumberContestAllHistoryFragment;
import com.mwin.reward.fragments.LuckyNumberContestMyHistoryFragment;

import java.util.ArrayList;

public class LuckyNumberGameHistoryTabAdapter extends FragmentPagerAdapter {
    public ArrayList<String> mFragmentItems;
    private LuckyNumberContestMyHistoryFragment fragmentMyContest;
    private LuckyNumberContestAllHistoryFragment fragmentAllContest;

    public LuckyNumberGameHistoryTabAdapter(FragmentManager fm, ArrayList<String> fragmentItems) {
        super(fm);
        this.mFragmentItems = fragmentItems;
        fragmentMyContest = new LuckyNumberContestMyHistoryFragment();
        fragmentAllContest = new LuckyNumberContestAllHistoryFragment();
    }

    @Override
    public Fragment getItem(int i) {
        if (i == 0) {
            return fragmentMyContest;
        } else if (i == 1) {
            return fragmentAllContest;
        }
        return null;
    }

    public LuckyNumberContestMyHistoryFragment getMyContestHistoryFragment() {
        return fragmentMyContest;
    }

    public LuckyNumberContestAllHistoryFragment getAllContestHistoryFragment() {
        return fragmentAllContest;
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
