package com.paymentsave.paymentsave.coreapp.fragments.reports;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.paymentsave.paymentsave.coreapp.fragments.reports.fragments.ReportCustomFormFragment;
import com.paymentsave.paymentsave.coreapp.fragments.reports.fragments.ReportInsightFragment;
import com.paymentsave.paymentsave.coreapp.fragments.reports.fragments.ReportYTDFragment;

class ViewStateAdapter extends FragmentStateAdapter {

    private String supervisorPin;

    public ViewStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle,String supervisorPin) {
        super(fragmentManager, lifecycle);
        this.supervisorPin = supervisorPin;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Hardcoded in this order, you'll want to use lists and make sure the titles match
        if (position == 0) {
            return new ReportInsightFragment(supervisorPin);
        } else if (position == 1){
            return new ReportYTDFragment(supervisorPin);
        } else if (position == 2) {
            return new ReportCustomFormFragment(supervisorPin);
        } else {
            return new ReportInsightFragment(supervisorPin);
        }
    }

    public void replaceFragment(int position, Fragment fragment) {

        createFragment(4);
//        fragmentList.set(position, fragment);
//        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        // Hardcoded, use lists
        return 3;
    }
}
