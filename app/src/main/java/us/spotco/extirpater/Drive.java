package us.spotco.extirpater;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;

public class Drive {

    private File path;
    private TextView txtInfo;
    private ProgressBar prg;
    private Button btnControl;
    private TextView txtStatus;

    private long spaceFree;
    private long spaceTotal;
    private static final int kilobyte = 1000;
    private static final int megabyte = 1000000;
    private static byte[] zeroes;

    private Thread eraser;
    private boolean running;
    private File tempFile;
    private double progress;
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
        prg.setProgress(0);
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
                Log.d("Extirpater", "STOPPING");
                btnControl.setText(R.string.lblStart);
                running = false;
                txtStatus.setText("Stopped");
            } else {
                Log.d("Extirpater", "STARTING");
                eraser = getEraser();
                eraser.start();
                txtStatus.setText("Erasing");
                btnControl.setText(R.string.lblStop);
            }
        }
    };

    private Thread getEraser() {
        return new Thread(() -> {
            try {
                running = true;

                tempFile = new File(path + "/Extirpater_Temp-" + getRandomString());
                tempFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(tempFile);
                Log.d("Extirpater", "CREATED TEMP FILE at " + tempFile);


                while (path.getFreeSpace() / megabyte >= 25) {
                    if (!running) {
                        break;
                    }
                    try {
                        //fos.write(getRandomByteArray(megabyte * 25));
                        fos.write(zeroes);
                    } catch (IOException e) {
                        break;
                    }
                    progress = 100.0 - ((((double) (path.getFreeSpace())) / spaceFree) * 100.0);
                    prg.setProgress((int) progress);
                    //Log.d("Extirpater", "25MB WRITTEN, PROGRESS = " + progress);
                }

                fos.flush();
                fos.close();

                tempFile.delete();
                deleteTempFiles();
                prg.setProgress(0);
                //if(running) txtStatus.setText("Finished");
                running = false;
                Log.d("Extirpater", "ENDED");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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

    private byte[] getRandomByteArray(int length) {
        byte[] bytes = new byte[length];
        secureRandom.nextBytes(bytes);
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
