package com.example.cdcmessage.cdcmessage;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Eagle on 1/31/2017.
 */

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private static final int TABLE_FRAGMENT_POS = 0;
    private static final int MESSAGE_FRAGMENT_POS = 1;

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        Fragment frag;
        switch (position) {
            case TABLE_FRAGMENT_POS:
                frag = TableFragment.newInstance();
                break;
            case MESSAGE_FRAGMENT_POS:
                frag = MessagesFragment.newInstance();
                break;
            default:
                frag = null;
        }
        return frag;
    }

    public int getCount() {
        // Show 3 total pages.
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case TABLE_FRAGMENT_POS:
                return "Table";
            case MESSAGE_FRAGMENT_POS:
                return "Messages";
        }
        return null;
    }
}
