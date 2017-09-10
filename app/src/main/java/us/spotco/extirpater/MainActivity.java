package us.spotco.extirpater;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Drive primary = new Drive(getCacheDir(), (TextView) findViewById(R.id.txtInfoPrimary),
                (ProgressBar) findViewById(R.id.prgPrimary), (Button) findViewById(R.id.btnPrimary), (TextView) findViewById(R.id.txtStatusPrimary));

        int ext = getExternalCacheDirs().length;
        Drive secondary = new Drive(getExternalCacheDirs()[ext > 0 ? 1 : 0], (TextView) findViewById(R.id.txtInfoSecondary),
                (ProgressBar) findViewById(R.id.prgSecondary), (Button) findViewById(R.id.btnSecondary), (TextView) findViewById(R.id.txtStatusSecondary));
    }

}
