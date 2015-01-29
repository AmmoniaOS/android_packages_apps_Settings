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


import static android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE;
import static android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
import static android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;

import android.content.ContentResolver;
import android.os.Bundle;
import android.content.Intent;
import android.preference.ListPreference;
import android.preference.SwitchPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.provider.Settings.Global;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class SmarterControlSettings extends SettingsPreferenceFragment 
             implements OnPreferenceChangeListener {

    private static final String TAG = "SmarterControlSettings";

    private static final String SMARTER_BRIGHTNESS = "smarter_brightness";
    private static final String KEY_SMALLHOURS_CHANGES = "smallhours_changes";
    private static final String KEY_MORNINGHOURS_CHANGES = "morninghours_changes";
    private static final String KEY_NOONHOURS_CHANGES = "noonhours_changes";
    private static final String KEY_NIGHTHOURS_CHANGES = "nighthours_changes";
    private static final String KEY_POWER_SAVE_SETTING = "power_save_setting";
    private static final String KEY_TIPS = "tips";
    private static final String KEY_DISPLAY_SMARTER = "display_smarter";

    private SwitchPreference mSmarterBrightness;
    private ListPreference mSmallhours;
    private ListPreference mMorninghours;
    private ListPreference mNoonhours;
    private ListPreference mNighthours;
    private ListPreference mPowerSaveSettings;
    private Preference mTips;
    private PreferenceCategory mDisplaySmarter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.smarter_control_settings);

        ContentResolver resolver = getActivity().getContentResolver();

        mTips = (Preference) findPreference(KEY_TIPS);
        mDisplaySmarter = (PreferenceCategory) findPreference(KEY_DISPLAY_SMARTER);

        mPowerSaveSettings = (ListPreference) findPreference(KEY_POWER_SAVE_SETTING);
        int PowerSaveSettings = Settings.System.getInt(
                resolver, Settings.System.POWER_SAVE_SETTINGS, 0);
        mPowerSaveSettings.setValue(String.valueOf(PowerSaveSettings));
        mPowerSaveSettings.setSummary(mPowerSaveSettings.getEntry());
        mPowerSaveSettings.setOnPreferenceChangeListener(this);

        mSmallhours = (ListPreference) findPreference(KEY_SMALLHOURS_CHANGES);
        int Smallhours = Settings.System.getInt(
                resolver, Settings.System.SMALL_BRIGHTNESS, 0);
        mSmallhours.setValue(String.valueOf(Smallhours));
        mSmallhours.setSummary(mSmallhours.getEntry());
        mSmallhours.setOnPreferenceChangeListener(this);

        mMorninghours = (ListPreference) findPreference(KEY_MORNINGHOURS_CHANGES);
        int Morninghours = Settings.System.getInt(
                resolver, Settings.System.MORNING_BRIGHTNESS, 0);
        mMorninghours.setValue(String.valueOf(Morninghours));
        mMorninghours.setSummary(mMorninghours.getEntry());
        mMorninghours.setOnPreferenceChangeListener(this);

        mNoonhours = (ListPreference) findPreference(KEY_NOONHOURS_CHANGES);
        int Noonhours = Settings.System.getInt(
                resolver, Settings.System.NOON_BRIGHTNESS, 0);
        mNoonhours.setValue(String.valueOf(Noonhours));
        mNoonhours.setSummary(mNoonhours.getEntry());
        mNoonhours.setOnPreferenceChangeListener(this);

        mNighthours = (ListPreference) findPreference(KEY_NIGHTHOURS_CHANGES);
        int Nighthours = Settings.System.getInt(
                resolver, Settings.System.NIGHT_BRIGHTNESS, 0);
        mNighthours.setValue(String.valueOf(Nighthours));
        mNighthours.setSummary(mNighthours.getEntry());
        mNighthours.setOnPreferenceChangeListener(this);

        mSmarterBrightness = (SwitchPreference) findPreference(SMARTER_BRIGHTNESS);
        mSmarterBrightness.setChecked((Settings.System.getInt(resolver,
                 Settings.System.SMARTER_BRIGHTNESS, 0) == 1));
        mSmarterBrightness.setOnPreferenceChangeListener(this);
        boolean mAutomaticBrightnessState = Settings.System.getInt(resolver,
                 SCREEN_BRIGHTNESS_MODE, SCREEN_BRIGHTNESS_MODE_MANUAL)
                 == SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        if (mAutomaticBrightnessState) {
            getPreferenceScreen().removePreference(mDisplaySmarter);
            Settings.System.putInt(resolver,
                 Settings.System.SMARTER_BRIGHTNESS, 0);
            mTips.setSummary(R.string.tips_title);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        Intent i = new Intent();
        i.setAction(Intent.ACTION_SCREENUI_SWITCHED);
        if (preference == mSmarterBrightness) {
             boolean value = (Boolean) newValue;
             Settings.System.putInt(resolver, Settings.System.SMARTER_BRIGHTNESS, value ? 1 : 0);
             return true;
         } else if (preference == mPowerSaveSettings) {
            int PowerSaveSettings = Integer.valueOf((String) newValue);
            int index = mPowerSaveSettings.findIndexOfValue((String) newValue);
            Settings.System.putInt(
                    resolver, Settings.System.POWER_SAVE_SETTINGS, PowerSaveSettings);
            mPowerSaveSettings.setSummary(mPowerSaveSettings.getEntries()[index]);
            Settings.Global.putInt(resolver,
                Settings.Global.LOW_POWER_MODE_TRIGGER_LEVEL, 0);
            return true;
        } else if (preference == mSmallhours) {
            int Smallhours = Integer.valueOf((String) newValue);
            int index = mSmallhours.findIndexOfValue((String) newValue);
            Settings.System.putInt(
                    resolver, Settings.System.SMALL_BRIGHTNESS, Smallhours);
            mSmallhours.setSummary(mSmallhours.getEntries()[index]);
            getActivity().sendBroadcast(i);
            return true;
        } else if (preference == mMorninghours) {
            int Morninghours = Integer.valueOf((String) newValue);
            int index = mMorninghours.findIndexOfValue((String) newValue);
            Settings.System.putInt(
                    resolver, Settings.System.MORNING_BRIGHTNESS, Morninghours);
            mMorninghours.setSummary(mMorninghours.getEntries()[index]);
            getActivity().sendBroadcast(i);
            return true;
        } else if (preference == mNoonhours) {
            int Noonhours = Integer.valueOf((String) newValue);
            int index = mNoonhours.findIndexOfValue((String) newValue);
            Settings.System.putInt(
                    resolver, Settings.System.NOON_BRIGHTNESS, Noonhours);
            mNoonhours.setSummary(mNoonhours.getEntries()[index]);
            getActivity().sendBroadcast(i);
            return true;
        } else if (preference == mNighthours) {
            int Nighthours = Integer.valueOf((String) newValue);
            int index = mNighthours.findIndexOfValue((String) newValue);
            Settings.System.putInt(
                    resolver, Settings.System.NIGHT_BRIGHTNESS, Nighthours);
            mNighthours.setSummary(mNighthours.getEntries()[index]);
            getActivity().sendBroadcast(i);
            return true;
        }
        return false;
    }

}
