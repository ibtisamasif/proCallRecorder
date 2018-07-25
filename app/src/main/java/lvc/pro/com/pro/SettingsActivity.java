package lvc.pro.com.pro;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.callrecorder.pro.R;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;
import net.rdrei.android.dirchooser.DirectoryChooserConfig;

import lvc.pro.com.pro.constants.Constants;
import lvc.pro.com.pro.service.ShakeDetectionService;
import lvc.pro.com.pro.utility.SharedPreferenceUtility;

public class SettingsActivity extends AppCompatPreferenceActivity {
    static Context ctx;
    public static boolean mIsDestroying=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        EditTextPreference editTextPreference;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            SwitchPreference sp = (SwitchPreference) findPreference("LOCK");
            sp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    SharedPreferences SP1 = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.ctx);
                    boolean lockstatus = SP1.getBoolean("LOCK", false);
                    final SharedPreferences sharedPreferences = SettingsActivity.ctx.getSharedPreferences("LOCK", MODE_PRIVATE);
                    String pin = sharedPreferences.getString("PIN", "");
                    if (!lockstatus && (pin.isEmpty())) {
                        Intent intent = new Intent(SettingsActivity.ctx, NewPinLock.class);
                        intent.putExtra(Constants.sKEY_FOR_ONLY_SET_PIN, true);
                        startActivity(intent);
                    }
                    return true;
                }
            });

            Preference button = findPreference("DIRECTORY");
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent chooserIntent = new Intent(ctx, DirectoryChooserActivity.class);
                    DirectoryChooserConfig config = DirectoryChooserConfig.builder().newDirectoryName("CallRecorder")
                            .allowNewDirectoryNameModification(true)
                            .build();
                    chooserIntent.putExtra(DirectoryChooserActivity.EXTRA_CONFIG, config);
                    startActivityForResult(chooserIntent, 1001);
                    return true;
                }
            });
            final Preference savingOptions = (ListPreference) findPreference(getString(R.string.shared_pref_saving_pref_key));
            savingOptions.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    if (o.toString().equals("3")) {
                        Intent intent = new Intent(ctx, ShakeDetectionService.class);
                        ctx.startService(intent);
                        MainActivity.mMainActivityInstance.setRecorderState(true);
                    } else {
                        Intent intent = new Intent(ctx, ShakeDetectionService.class);
                        ctx.stopService(intent);
                        if (o.toString().equals("1")) {
                            MainActivity.mMainActivityInstance.setRecorderState(false);
                        } else {
                            MainActivity.mMainActivityInstance.setRecorderState(true);
                        }
                    }
                    return true;
                }
            });

        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 1001) {
                if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
                    SharedPreferences filepreference = ctx.getSharedPreferences("DIRECTORY", MODE_PRIVATE);
                    SharedPreferences.Editor editor = filepreference.edit();
                    editor.putString("DIR", data.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR));
                    editor.apply();
                    Intent intent = new Intent(ctx, MainActivity.class);
                    startActivity(intent);
                } else {
                    // Nothing selected
                }
            }
        }
    }
//pre
   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                // SharedPreferenceUtility.restoreDefaults(getApplicationContext());
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }*/
@Override
protected void onPause() {
    super.onPause();
    mIsDestroying=true;
    SharedPreferenceUtility.setBackgroundStatus(getApplicationContext(), true);
}

    @Override
    protected void onResume() {
        super.onResume();
        if (SharedPreferenceUtility.getLockActivatedStatus(getApplicationContext())) {
            if ((SharedPreferenceUtility.getBackgroundStatus(getApplicationContext())) && (!(Constants.sIS_FROM_ANOTHER_ACTIVITY))) {
                Constants.sIS_FROM_BACKGROUND = true;
                Intent intent = new Intent(SettingsActivity.this, NewPinLock.class);
                startActivity(intent);
            }
        }
        Constants.sIS_FROM_ANOTHER_ACTIVITY = false;
        mIsDestroying=false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constants.sIS_FROM_ANOTHER_ACTIVITY = true;
        SharedPreferenceUtility.setBackgroundStatus(getApplicationContext(), false);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                Constants.sIS_FROM_ANOTHER_ACTIVITY = true;
                SharedPreferenceUtility.setBackgroundStatus(getApplicationContext(), false);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Constants.sIS_FROM_ANOTHER_ACTIVITY = true;
        SharedPreferenceUtility.setBackgroundStatus(getApplicationContext(), false);
    }

}
