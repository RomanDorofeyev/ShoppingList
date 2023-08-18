package com.shoppinglist.rdproject.shoppinglist;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.ActionBar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

public class SettingsActivity extends AppCompatPreferenceActivity {
    public static final String ADMOB_VIDEO_ID = "ca-app-pub-8462980126781299/7163924058";
    public static final String ADMOB_TEST_VIDEO_ID = "ca-app-pub-3940256099942544/5224354917";
    private static RewardedVideoAd mRewardedVideoAd;
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
                        // if lang is changed show toast
                if (!PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString("language_settings", "1").equals(stringValue)) {
                    Toast.makeText(preference.getContext(),
                            preference.getContext().getResources().getString(R.string.need_restart),
                            Toast.LENGTH_SHORT).show();
                }

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };
    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }
    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.action_settings);
        setupActionBar();
        MobileAds.initialize(this, ADMOB_VIDEO_ID);
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {

            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {
                //Toast.makeText(SettingsActivity.this, "Sorry you must watch full video!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {

            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {

            }

            @Override
            public void onRewardedVideoCompleted() {
                Toast.makeText(SettingsActivity.this, R.string.thank_you, Toast.LENGTH_SHORT).show();
                MainScreen.isAdsfreeForNow = true;
            }
        });
        loadRewardedVideoAd();
        getFragmentManager().beginTransaction().replace(android.R.id.content, new GeneralPreferenceFragment()).commit();
    }

    @Override
    public void onResume() {
        mRewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }

    private static void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd(ADMOB_VIDEO_ID,
                new AdRequest.Builder().build());
    }
    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);

        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
           onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        //SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(findPreference("language_settings"));

            //handle contact preference
            Preference contactPref = findPreference(getResources().getString(R.string.contact_us));
            contactPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    String subject = "App: Shopping List. Feedback from user";
                    String manufacturer = Build.MANUFACTURER;
                    String model = Build.MODEL;
                    int version = Build.VERSION.SDK_INT;
                    String versionRelease = Build.VERSION.RELEASE;

                    String body = "Manufacturer " + manufacturer
                            + " \n Model " + model
                            + " \n Version " + version
                            + " \n VersionRelease " + versionRelease
                            + " \n" + "App version: " + getResources().getString(R.string.app_version);

                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", "rdprojectmail@gmail.com", null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    emailIntent.putExtra(Intent.EXTRA_TEXT, body);
                    startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.send_email)));
                    return true;
                }
            });

            //handle remove ads option
            EditTextPreference removeAdsCompletely = (EditTextPreference) findPreference(getResources().getString(R.string.secret_key));

            if (removeAdsCompletely.getText()!= null && removeAdsCompletely.getText().trim().equalsIgnoreCase(getResources().getString(R.string.secret_key))) {
                removeAdsCompletely.setEnabled(false);
            } else {
                removeAdsCompletely.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        String secretKey = ((String) newValue).trim();
                        String realKey = getResources().getString(R.string.secret_key);
                        if (secretKey.equalsIgnoreCase(realKey)) {
                            preference.setEnabled(false);
                            MainScreen.isAdsfree = true;
                            Snackbar.make(getView(), R.string.banner_not_border, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            return true;
                        }else {
                            Snackbar.make(getView(), R.string.nice_try, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            return false;
                        }

                    }
                });
            }
            //handle remove ads for now
            Preference removeAds24 = findPreference(getResources().getString(R.string.remove_ads));
            removeAds24.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (mRewardedVideoAd.isLoaded()) {
                        mRewardedVideoAd.show();
                    }
                    return true;
                }
            });

        }
    }
}
