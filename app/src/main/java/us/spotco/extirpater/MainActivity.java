package us.spotco.extirpater;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static int dataOutput = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Drive primary = new Drive(getCacheDir(), (TextView) findViewById(R.id.txtInfoPrimary),
                (ProgressBar) findViewById(R.id.prgPrimary), (Button) findViewById(R.id.btnPrimary), (TextView) findViewById(R.id.txtStatusPrimary));

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            int ext = getExternalCacheDirs().length;
            Drive secondary = new Drive(getExternalCacheDirs()[ext > 1 ? 1 : 0], (TextView) findViewById(R.id.txtInfoSecondary),
                    (ProgressBar) findViewById(R.id.prgSecondary), (Button) findViewById(R.id.btnSecondary), (TextView) findViewById(R.id.txtStatusSecondary));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnuDataZeroes:
                dataOutput = 0;
                item.setChecked(true);
                Log.d("Extirpater", "Switched to zeroes");
                return true;
            case R.id.mnuDataRandom:
                dataOutput = 1;
                item.setChecked(true);
                Log.d("Extirpater", "Switched to Random");
                return true;
            case R.id.mnuDataSecureRandom:
                dataOutput = 2;
                item.setChecked(true);
                Log.d("Extirpater", "Switched to SecureRandom");
                return true;
            case R.id.mnuAbout:
                //TODO: Change this to a dialog
                String aboutMessage = "Version: " + BuildConfig.VERSION_NAME + ", License: MIT, Copyright: 2017 Spot Communications, Inc.";
                Snackbar about = Snackbar.make(findViewById(R.id.mainCoordinator), aboutMessage, Snackbar.LENGTH_LONG);
                about.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
