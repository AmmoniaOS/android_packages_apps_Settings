/*
 * Copyright (C) 2015 The New One Android
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.one;

import android.content.ContentResolver;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.SwitchPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.provider.Settings.Global;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class SmarterControlSettings extends SettingsPreferenceFragment 
             implements OnPreferenceChangeListener {

    private static final String TAG = "SmarterControlSettings";

    private static final String SMARTER_BRIGHTNESS = "smarter_brightness";

    private SwitchPreference mSmarterBrightness;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.smarter_control_settings);

        ContentResolver resolver = getActivity().getContentResolver();

        mSmarterBrightness = (SwitchPreference) findPreference(SMARTER_BRIGHTNESS);
        mSmarterBrightness.setChecked((Settings.System.getInt(resolver,
                 Settings.System.SMARTER_BRIGHTNESS, 0) == 1));
        mSmarterBrightness.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mSmarterBrightness) {
             boolean value = (Boolean) newValue;
             Settings.System.putInt(resolver, Settings.System.SMARTER_BRIGHTNESS, value ? 1 : 0);
             return true;
         }
        return false;
    }

}
