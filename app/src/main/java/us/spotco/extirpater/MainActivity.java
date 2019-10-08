/*
Extirpater: A free space eraser for Android
Copyright (c) 2017-2019 Divested Computing Group

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package us.spotco.extirpater;

import android.os.Bundle;
import android.os.Environment;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static final String logPrefix = "Extirpater";
    public static int dataOutput = 1;
    public static boolean fillFileTable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        Drive primary = new Drive(getCacheDir(), false, (TextView) findViewById(R.id.txtInfoPrimary),
                (ProgressBar) findViewById(R.id.prgPrimary), (Button) findViewById(R.id.btnPrimary), (TextView) findViewById(R.id.txtStatusPrimary));

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            int ext = getExternalCacheDirs().length;
            Drive secondary = new Drive(getExternalCacheDirs()[ext > 1 ? 1 : 0], true, (TextView) findViewById(R.id.txtInfoSecondary),
                    (ProgressBar) findViewById(R.id.prgSecondary), (Button) findViewById(R.id.btnSecondary), (TextView) findViewById(R.id.txtStatusSecondary));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.mnuFillFileTable).setChecked(fillFileTable);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnuFillFileTable:
                fillFileTable = !item.isChecked();
                item.setChecked(fillFileTable);
                return true;

            //START OF DATA OUTPUT GROUP
            case R.id.mnuDataZeroes:
                dataOutput = 0;
                item.setChecked(true);
                Log.d(logPrefix, "Switched to zeroes");
                return true;
            case R.id.mnuDataRandom:
                dataOutput = 1;
                item.setChecked(true);
                Log.d(logPrefix, "Switched to Random");
                return true;
            case R.id.mnuDataXORShiftRNG:
                dataOutput = 2;
                item.setChecked(true);
                Log.d(logPrefix, "Switched to XORShiftRNG");
                return true;
            case R.id.mnuDataMersenneTwisterRNG:
                dataOutput = 3;
                item.setChecked(true);
                Log.d(logPrefix, "Switched to MersenneTwisterRNG");
                return true;
            case R.id.mnuDataCMWC4096RNG:
                dataOutput = 4;
                item.setChecked(true);
                Log.d(logPrefix, "Switched to CMWC4096RNG");
                return true;
            case R.id.mnuDataSecureRandom:
                dataOutput = 5;
                item.setChecked(true);
                Log.d(logPrefix, "Switched to SecureRandom");
                return true;
            //END OF DATA OUTPUT GROUP

            case R.id.mnuAbout:
                //TODO: Change this to a dialog
                String aboutMessage = "Version: " + BuildConfig.VERSION_NAME + ", License: GPLv3, Copyright: 2017-2019 Divested Computing Group";
                Snackbar about = Snackbar.make(findViewById(R.id.mainCoordinator), aboutMessage, Snackbar.LENGTH_LONG);
                about.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
