package com.example.carlos.assignment_one;

import android.os.Bundle;
import android.preference.PreferenceFragment;


/**
 * Created by Carlos on 17/10/3.
 */

public class PrefsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

    }

}
