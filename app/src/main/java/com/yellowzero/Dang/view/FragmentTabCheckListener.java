package com.yellowzero.Dang.view;

import android.view.View;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FragmentTabCheckListener implements OnTabCheckListener {

    private FragmentManager fragmentManager;
    private Fragment[] fragments;

    public FragmentTabCheckListener(FragmentManager fragmentManager, @IdRes int containerId, Fragment[] fragments) {
        this.fragmentManager = fragmentManager;
        this.fragments = fragments;
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        for (Fragment fragment : fragments)
            transaction.add(containerId, fragment);
        transaction.commit();
    }

    @Override
    public void onTabSelected(View view, int position) {
        if (position < fragments.length) {
            showFragment(position);
        }
    }

    private void showFragment(int position) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        for (int i = 0; i < fragments.length; i++)
            if (i != position)
                transaction.hide(fragments[i]);
            else
                transaction.show(fragments[i]);
        transaction.commit();
    }
}
