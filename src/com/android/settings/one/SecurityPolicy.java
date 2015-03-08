/*
 * Copyright (C) 2015 The OneUI Open Source Project
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

import android.app.AlertDialog;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.SwitchPreference;
import android.preference.PreferenceScreen;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.provider.Settings.Global;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class SecurityPolicy extends SettingsPreferenceFragment 
             implements OnPreferenceChangeListener {

    private static final String TAG = "SecurityPolicy";

    private static final String SHOW_PASSWORD = "show_password";
    private static final String CUSTOM_PASSWORD_LABEL = "custom_password_label";

    private SwitchPreference mShowPasswordDialog;
    private PreferenceScreen mCustomPasswordLabel;

    private String mCustomPasswordLabelText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.security_policy_settings);

        ContentResolver resolver = getActivity().getContentResolver();

        mShowPasswordDialog = (SwitchPreference) findPreference(SHOW_PASSWORD);
        mShowPasswordDialog.setChecked((Settings.System.getInt(resolver,
                 Settings.System.SHOW_PASSWORD_DIALOG, 0) == 1));
        mShowPasswordDialog.setOnPreferenceChangeListener(this);

        mCustomPasswordLabel = (PreferenceScreen) findPreference(CUSTOM_PASSWORD_LABEL);
        updateSummary();
    }
    
    private void updateSummary() {
        mCustomPasswordLabelText = Settings.System.getString(
            getActivity().getContentResolver(), Settings.System.CUSTOM_PASSWORD_DIALOG_LABEL);

        if (TextUtils.isEmpty(mCustomPasswordLabelText)) {
            mCustomPasswordLabel.setSummary(R.string.password_not_set);
        } else {
            mCustomPasswordLabel.setSummary(R.string.password_has_been_set);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mShowPasswordDialog) {
             boolean value = (Boolean) newValue;
             Settings.System.putInt(resolver, Settings.System.SHOW_PASSWORD_DIALOG, value ? 1 : 0);
             return true;
         }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            final Preference preference) {
        final ContentResolver resolver = getActivity().getContentResolver();
        if (preference.getKey().equals(CUSTOM_PASSWORD_LABEL)) {
            final boolean are = TextUtils.isEmpty(mCustomPasswordLabelText);
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle(R.string.custom_password_label_title);
            alert.setMessage(are ? R.string.custom_password_label_msg : R.string.original_password_msg);

            // Set an EditText view to get user input
            final EditText input = new EditText(getActivity());
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            alert.setView(input);
            alert.setPositiveButton(getString(android.R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String value = ((Spannable) input.getText()).toString().trim();
                            if (!are) {
                                if (mCustomPasswordLabelText.equals(value)) {
                                    Settings.System.putString(resolver,
                                        Settings.System.CUSTOM_PASSWORD_DIALOG_LABEL, null);
                                    Toast.makeText(getActivity(), getString(
                                       R.string.password_successfully_reset), Toast.LENGTH_SHORT).show();
                                    updateSummary();
                                } else {
                                    Toast.makeText(getActivity(), getString(
                                       R.string.password_failed_reset), Toast.LENGTH_SHORT).show();
                                }
                                return;
                            }
                            Settings.System.putString(resolver,
                                Settings.System.CUSTOM_PASSWORD_DIALOG_LABEL, value);
                            updateSummary();
                }
            });
            alert.setNegativeButton(getString(android.R.string.cancel), null);
            alert.show();
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
