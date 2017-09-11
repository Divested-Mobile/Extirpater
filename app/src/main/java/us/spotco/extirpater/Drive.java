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

    private long spaceFreeOrig;
    private long spaceTotal;
    private static final int kilobyte = 1000;
    private static final int megabyte = kilobyte * 1000;
    private static final int megabyte25 = megabyte * 25;
    private static byte[] zeroes;

    private static final String filePrefix = "/Extirpater_Temp-";

    private AsyncTask eraser;
    private boolean running;
    private File tempFile;
    private final Random random = new Random();
    private final SecureRandom secureRandom = new SecureRandom();

    public Drive(File path, TextView txtInfo, ProgressBar prg, Button btnControl, TextView txtStatus) {
        this.path = path;
        this.txtInfo = txtInfo;
        this.prg = prg;
        this.btnControl = btnControl;
        this.txtStatus = txtStatus;

        deleteTempFiles();
        spaceFreeOrig = path.getFreeSpace();
        spaceTotal = path.getTotalSpace();

        this.txtInfo.setText(((spaceTotal - spaceFreeOrig) / megabyte) + "MB  used of " + (spaceTotal / megabyte) + "MB");
        btnControl.setOnClickListener(actionListener);
        this.txtStatus.setText(R.string.lblIdle);
        Log.d(MainActivity.logPrefix, "CREATED DRIVE: Path = " + path + ", Size = " + spaceTotal);

        zeroes = generateByteArray(0xFF, megabyte25);
        prg.setVisibility(View.INVISIBLE);
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
            Log.d(MainActivity.logPrefix, "STARTING");
            btnControl.setText(R.string.lblStop);
            txtStatus.setText(R.string.lblErasing);
            prg.setProgress(0);
            prg.setVisibility(View.VISIBLE);
            running = true;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Log.d(MainActivity.logPrefix, "FILLING FILE TABLE");
                for (int x = 0; x < 20000; x++) {
                    if (!running) {
                        Log.d(MainActivity.logPrefix, "STOPPING");
                        return "Stopped";
                    }

                    new File(path + filePrefix + getRandomString()).createNewFile();
                    if (x % 100 == 0) {
                        publishProgress(x / 200);
                    }
                }
                deleteTempFiles();
                Log.d(MainActivity.logPrefix, "FILLED FILE TABLE");
            } catch (Exception e) {
                e.printStackTrace();
                return "Failed @ Erase File Table";
            }

            tempFile = new File(path + filePrefix + getRandomString());
            try {
                tempFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return "Failed @ Create Temp File";
            }
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(tempFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return "Failed @ Open Temp File";
            }
            Log.d(MainActivity.logPrefix, "CREATED TEMP FILE at " + tempFile);

            final int dataOutput = MainActivity.dataOutput;

            publishProgress(0);

            long fsCache = path.getFreeSpace();
            while (fsCache >= megabyte25) {
                if (!running) {
                    Log.d(MainActivity.logPrefix, "STOPPING");
                    break;
                }
                try {
                    fos.write(getDataArray(dataOutput));
                } catch (IOException e) {
                    break;
                }
                publishProgress((int) (100.0 - ((((double) (fsCache = path.getFreeSpace())) / spaceFreeOrig) * 100.0)));
                //Log.d(MainActivity.logPrefix, "25MB WRITTEN");
            }

            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                return "Failed @ Delete File";
            }
            if (running) {
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
            Log.d(MainActivity.logPrefix, "ENDED");
            deleteTempFiles();
            prg.setProgress(0);
            prg.setVisibility(View.INVISIBLE);
            btnControl.setText(R.string.lblStart);
            txtStatus.setText(result);
            running = false;
        }
    }

    private void deleteTempFiles() {
        try {
            for (File f : path.listFiles()) {
                if (f.isFile()) {
                    if ((f + "").contains(filePrefix)) { //TODO: Verify this
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

    private byte[] getDataArray(int dataOutput) {
        //Log.d(MainActivity.logPrefix, "Generating array using " + MainActivity.dataOutput);
        switch (dataOutput) {
            case 0:
                return zeroes;
            case 1:
                return generateRandomByteArray(false);
            case 2:
                return generateRandomByteArray(true);
            default:
                return zeroes;
        }
    }

    private byte[] generateRandomByteArray(boolean secure) {
        byte[] bytes = new byte[megabyte25];
        if (secure) {
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
