package com.teamsolo.swear.structure.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.MenuItem;

import com.teamsolo.base.util.BuildUtility;
import com.teamsolo.base.util.DisplayUtility;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.WebLink;
import com.teamsolo.swear.foundation.constant.NetConst;
import com.teamsolo.swear.foundation.constant.SpConst;
import com.teamsolo.swear.foundation.ui.AppCompatPreferenceActivity;
import com.teamsolo.swear.structure.ui.about.AboutActivity;
import com.teamsolo.swear.structure.ui.common.WebLinkActivity;

import java.util.List;

/**
 * description: settings page
 * author: Melody
 * date: 2016/6/23
 * version: 0.0.0.1
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener =
            (preference, value) -> {
                String stringValue = value.toString();

                if (preference instanceof ListPreference) {
                    ListPreference listPreference = (ListPreference) preference;
                    int index = listPreference.findIndexOfValue(stringValue);

                    preference.setSummary(index >= 0
                            ? listPreference.getEntries()[index]
                            : null);
                } else if (preference instanceof RingtonePreference) {
                    if (TextUtils.isEmpty(stringValue))
                        preference.setSummary(R.string.pref_ringtone_silent);
                    else {
                        Ringtone ringtone = RingtoneManager.getRingtone(preference.getContext(), Uri.parse(stringValue));

                        if (ringtone == null) preference.setSummary(null);
                        else {
                            String name = ringtone.getTitle(preference.getContext());
                            preference.setSummary(name);
                        }
                    }
                } else preference.setSummary(stringValue);

                return true;
            };

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager.getDefaultSharedPreferences(preference.getContext()).
                        getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onIsMultiPane() {
        return DisplayUtility.isXLargeTablet(this);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<PreferenceActivity.Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);

        for (int i = 0; i < target.size(); i++) {
            PreferenceActivity.Header header = target.get(i);
            if (header.id == R.id.setting_feedback) {
                // TODO:
                WebLink webLink = new WebLink();
                webLink.title = getString(R.string.app_name);
                webLink.forwardUrl = "http://wenxue.test.xweisoft.com/pc_secureproxy/resource/page/info_detail.html?newsUuid=14b18c5421d44dccb63e57e992203dae";

                Intent intent = new Intent(this, WebLinkActivity.class);
                intent.putExtra("link", webLink);
                header.intent = intent;
            } else if (header.id == R.id.setting_help) {
                WebLink webLink = new WebLink();
                webLink.title = getString(R.string.web_help_center);
                webLink.forwardUrl = NetConst.HTTP + NetConst.getBaseHttpUrl() + NetConst.HELP_CENTER;

                Intent intent = new Intent(this, WebLinkActivity.class);
                intent.putExtra("link", webLink);

                header.intent = intent;
            } else if (header.id == R.id.setting_share)
                header.intent = new Intent(this, AboutActivity.class);
        }
    }

    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || DataSyncPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (BuildUtility.isRequired(Build.VERSION_CODES.LOLLIPOP)) finishAfterTransition();
            else finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(findPreference(SpConst.SETTING_NOTICE_RINGTONE));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }

            return super.onOptionsItemSelected(item);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(findPreference(SpConst.LOAD_COVER_FREQUENCY));

            Preference preferenceCache = findPreference("clear_cache");
            if (preferenceCache != null)
                preferenceCache.setOnPreferenceClickListener(
                        preference -> {
                            // TODO: clear cache
                            return true;
                        });

            final Preference preferenceUpgrade = findPreference("upgrade");
            if (preferenceUpgrade != null)
                preferenceUpgrade.setOnPreferenceClickListener(
                        preference -> {
                            // TODO: upgrade
                            new AlertDialog.Builder(preferenceUpgrade.getContext())
                                    .setTitle(R.string.prompt)
                                    .setMessage(R.string.coming_soon)
                                    .setPositiveButton(R.string.ok,
                                            (dialog, which) -> {

                                            })
                                    .create()
                                    .show();
                            return true;
                        });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }

            return super.onOptionsItemSelected(item);
        }
    }
}
