package com.mwin.reward.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.mwin.reward.fragments.ActiveMilestoneTargetListFragment;
import com.mwin.reward.fragments.FinishedMilestonesFragment;

import java.util.ArrayList;

public class MilestonesTargetTabAdapter extends FragmentPagerAdapter {
    public ArrayList<String> mFragmentItems;
    private ActiveMilestoneTargetListFragment fragmentActive;
    private FinishedMilestonesFragment fragmentCompleted;

    public MilestonesTargetTabAdapter(FragmentManager fm, ArrayList<String> fragmentItems) {
        super(fm);
        this.mFragmentItems = fragmentItems;
        fragmentActive = new ActiveMilestoneTargetListFragment();
        fragmentCompleted = new FinishedMilestonesFragment();
    }

    @Override
    public Fragment getItem(int i) {
        if (i == 0) {
            return fragmentActive;
        } else if (i == 1) {
            return fragmentCompleted;
        }
        return null;
    }

    public ActiveMilestoneTargetListFragment getActiveMilestonesFragment() {
        return fragmentActive;
    }

    public FinishedMilestonesFragment getCompletedMilestonesFragment() {
        return fragmentCompleted;
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
