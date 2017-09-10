package us.spotco.extirpater;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;

public class Drive {

    private File path;
    private TextView txtInfo;
    private ProgressBar prg;
    private Button btnControl;
    private TextView txtStatus;

    private long spaceFree;
    private long spaceTotal;
    private static final int kilobyte = 1000;
    private static final int megabyte = kilobyte * 1000;
    private static final int megabyte25 = megabyte * 25;
    private static byte[] zeroes;

    private AsyncTask eraser;
    private boolean running;
    private File tempFile;
    private Random random = new Random();
    private SecureRandom secureRandom = new SecureRandom();

    public Drive(File path, TextView txtInfo, ProgressBar prg, Button btnControl, TextView txtStatus) {
        this.path = path;
        this.txtInfo = txtInfo;
        this.prg = prg;
        this.btnControl = btnControl;
        this.txtStatus = txtStatus;

        deleteTempFiles();
        spaceFree = path.getFreeSpace();
        spaceTotal = path.getTotalSpace();

        this.txtInfo.setText(((spaceTotal - spaceFree) / megabyte) + "MB  used of " + (spaceTotal / megabyte) + "MB");
        btnControl.setOnClickListener(actionListener);
        this.txtStatus.setText("Idle");
        Log.d("Extirpater", "CREATED DRIVE: Path = " + path + ", Size = " + spaceTotal);

        zeroes = generateByteArray(0xFF, megabyte * 25);
        btnControl.setEnabled(true);
    }

    private View.OnClickListener actionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (running) {
                running = false;
            } else {
                eraser = new Eraser().execute("");
            }
        }
    };


    private class Eraser extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            Log.d("Extirpater", "STARTING");
            btnControl.setText(R.string.lblStop);
            txtStatus.setText("Erasing");
            prg.setProgress(0);
            prg.setVisibility(View.VISIBLE);
            running = true;
        }

        @Override
        protected String doInBackground(String... strings) {
            tempFile = new File(path + "/Extirpater_Temp-" + getRandomString());
            try {
                tempFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return "Failed";
            }
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(tempFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return "Failed";
            }
            Log.d("Extirpater", "CREATED TEMP FILE at " + tempFile);

            long fsCache = path.getFreeSpace();
            while (fsCache >= megabyte25) {
                if (!running) {
                    Log.d("Extirpater", "STOPPING");
                    break;
                }
                try {
                    fos.write(getDataArray());
                } catch (IOException e) {
                    break;
                }
                publishProgress((int) (100.0 - ((((double) (fsCache = path.getFreeSpace())) / spaceFree) * 100.0)));
                //Log.d("Extirpater", "25MB WRITTEN");
            }

            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                return "Failed";
            }
            if(running) {
                return "Finished";
            } else {
                return "Stopped";
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            prg.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("Extirpater", "ENDED");
            tempFile.delete();
            deleteTempFiles();
            prg.setProgress(0);
            btnControl.setText(R.string.lblStart);
            txtStatus.setText(result);
            running = false;
        }
    }

    private void deleteTempFiles() {
        try {
            for (File f : path.listFiles()) {
                if (f.isFile()) {
                    if ((f + "").contains("Extirpater_Temp-")) {
                        f.delete();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getRandomString() {
        String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int rn = secureRandom.nextInt(base.length());
            temp.append(base.substring(rn, rn + 1));
        }
        return temp.toString();
    }

    private byte[] getDataArray() {
        //Log.d("Extirpater", "Generating array using " + MainActivity.dataOutput);
        switch(MainActivity.dataOutput) {
            case 0:
                return zeroes;
            case 1:
                return getRandomByteArray(false);
            case 2:
                return getRandomByteArray(true);
            default:
                return zeroes;
        }
    }

    private byte[] getRandomByteArray(boolean secure) {
        byte[] bytes = new byte[megabyte25];
        if(secure) {
            secureRandom.nextBytes(bytes);
        } else {
            random.nextBytes(bytes);
        }
        return bytes;
    }

    private byte[] generateByteArray(int b, int length) {
        byte[] bytes = new byte[length];
        for (int x = 0; x < length; x++) {
            bytes[x] = (byte) b;
        }
        return bytes;
    }

}
