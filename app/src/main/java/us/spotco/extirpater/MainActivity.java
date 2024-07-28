/*
Extirpater: A free space eraser for Android
Copyright (c) 2017-2019 Divested Computing Group

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package us.spotco.extirpater;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;

public class MainActivity extends Activity {

    public static final String logPrefix = "Extirpater";
    public static int dataOutput = 1;
    public static boolean fillFileTable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setTheme(android.R.style.Theme_DeviceDefault_DayNight);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            findViewById(R.id.itmDivider).setBackgroundColor(getResources().getColor(android.R.color.system_accent1_500));
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        Drive primary = new Drive(getCacheDir(), false, findViewById(R.id.txtInfoPrimary),
                findViewById(R.id.prgPrimary), findViewById(R.id.btnPrimary), findViewById(R.id.txtStatusPrimary));

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int ext = getExternalCacheDirs().length;
            Drive secondary = new Drive(getExternalCacheDirs()[ext > 1 ? 1 : 0], true, findViewById(R.id.txtInfoSecondary),
                    findViewById(R.id.prgSecondary), findViewById(R.id.btnSecondary), findViewById(R.id.txtStatusSecondary));
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
                String aboutMessage = "Version: " + BuildConfig.VERSION_NAME + "-" + BuildConfig.VERSION_CODE + "\nLicense: AGPLv3\nCopyright: 2017-2023\nAuthor: Divested Computing Group";
                Dialog creditsDialog;
                AlertDialog.Builder creditsBuilder = new AlertDialog.Builder(this);
                creditsBuilder.setTitle(getString(R.string.lblFullCredits));
                creditsBuilder.setMessage(aboutMessage);
                creditsBuilder.create().show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
