/*
Extirpater: A free space eraser for Android
Copyright (c) 2017-2018 Divested Computing Group

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

import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.uncommons.maths.random.CMWC4096RNG;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.SecureRandomSeedGenerator;
import org.uncommons.maths.random.SeedGenerator;
import org.uncommons.maths.random.XORShiftRNG;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Random;

public class Drive {

    private final File path;
    private final boolean substandard;
    private final TextView txtInfo;
    private final ProgressBar prg;
    private final Button btnControl;
    private final TextView txtStatus;

    private final long spaceFreeOrig;
    private final long spaceTotal;
    private static final int kilobyte = 1000;
    private static final int megabyte = kilobyte * 1000;
    private static final int megabyte20 = megabyte * 20;

    private static final String filePrefix = "/Extirpater_Temp-";
    private boolean running;

    private static byte[] zeroes;
    private Random random = null;
    //private final SplittableRandom splittableRandom = new SplittableRandom(); //Only on SDK 24+
    private XORShiftRNG xorShiftRNG = null;
    private MersenneTwisterRNG mersenneTwisterRNG = null;
    private CMWC4096RNG cmwc4096RNG = null;
    //private final XoRoShiRo128PlusRandom xorRandom = new XoRoShiRo128PlusRandom(); //DSIUtils doesn't support Android
    private final SecureRandom secureRandom = new SecureRandom();
    //private final SecureRandom secureRandomStrong = SecureRandom.getInstanceStrong(); //Only on SDK 26+
    //private final AESCounterRNG aesCounterRNG = new AESCounterRNG(); //Doesn't support Android

    public Drive(File path, boolean substandard, TextView txtInfo, ProgressBar prg, Button btnControl, TextView txtStatus) {
        this.path = path;
        this.substandard = substandard;
        this.txtInfo = txtInfo;
        this.prg = prg;
        this.btnControl = btnControl;
        this.txtStatus = txtStatus;

        Utils.deleteFilesByPrefix(path, filePrefix);
        spaceFreeOrig = path.getFreeSpace();
        spaceTotal = path.getTotalSpace();

        try {
            SeedGenerator seedGenerator = new SecureRandomSeedGenerator();
            random = new Random(ByteBuffer.wrap(seedGenerator.generateSeed(16)).getLong());
            xorShiftRNG = new XORShiftRNG(seedGenerator);
            mersenneTwisterRNG = new MersenneTwisterRNG(seedGenerator);
            cmwc4096RNG = new CMWC4096RNG(seedGenerator);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.txtInfo.setText(((spaceTotal - spaceFreeOrig) / megabyte) + "MB  used of " + (spaceTotal / megabyte) + "MB");
        btnControl.setOnClickListener(actionListener);
        this.txtStatus.setText(R.string.lblIdle);
        Log.d(MainActivity.logPrefix, "CREATED DRIVE: Path = " + path + ", Size = " + spaceTotal);

        zeroes = Utils.generateByteArray(0xFF, megabyte20);
        btnControl.setEnabled(true);
    }

    private final View.OnClickListener actionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (running) {
                btnControl.setEnabled(false);
                running = false;
            } else {
                btnControl.setEnabled(false);
                new Eraser().execute("");
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
            btnControl.setEnabled(true);
        }

        @Override
        protected String doInBackground(String... strings) {
            if (MainActivity.fillFileTable) {
                try {
                    Log.d(MainActivity.logPrefix, "FILLING FILE TABLE");
                    for (int x = 0; x < (substandard ? 2000 : 20000); x++) {
                        if (!running) {
                            Log.d(MainActivity.logPrefix, "STOPPING @ FILL FILE TABLE");
                            return "Stopped";
                        }

                        new File(path + filePrefix + Utils.getRandomString(cmwc4096RNG, 16)).createNewFile();
                        if (x % 100 == 0) {
                            publishProgress(x / (substandard ? 20 : 200));
                        }
                    }
                    Utils.deleteFilesByPrefix(path, filePrefix);
                    Log.d(MainActivity.logPrefix, "FILLED FILE TABLE");
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Failed @ Erase File Table";
                }
            }

            publishProgress(0);
            final int dataOutput = MainActivity.dataOutput;
            long fsCache = path.getFreeSpace(); //Compute once to start
            int chunkAmt20MB = (int) (spaceFreeOrig / megabyte20);
            Log.d(MainActivity.logPrefix, "ERASING FREE SPACE! GOING TO CREATE " + chunkAmt20MB + "x 20MB FILES");
            for (int x = 0; x < chunkAmt20MB; x++) {
                if (!running) {
                    Log.d(MainActivity.logPrefix, "STOPPING @ NEW FILE");
                    break;
                }

                if (fsCache >= megabyte20) { //Do we have space for the file?
                    File tempFile = new File(path + filePrefix + Utils.getRandomString(secureRandom, 16));//Create the file
                    try {
                        tempFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return "Failed @ Create Temp File";
                    }
                    FileOutputStream fos;//Open the file
                    try {
                        fos = new FileOutputStream(tempFile);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        return "Failed @ Open Temp File";
                    }
                    //Log.d(MainActivity.logPrefix, "CREATED TEMP FILE at " + tempFile);

                    try {//Write the data
                        fos.write(getDataArray(dataOutput, megabyte20));
                    } catch (IOException e) {
                        break;
                    }
                    publishProgress((int) (100.0 - ((((double) (fsCache = path.getFreeSpace())) / spaceFreeOrig) * 100.0))); //Update progress and cached free space

                    try {//Close the file
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return "Failed @ Close File";
                    }
                }
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
            Utils.deleteFilesByPrefix(path, filePrefix);
            prg.setProgress(0);
            prg.setVisibility(View.INVISIBLE);
            btnControl.setText(R.string.lblStart);
            txtStatus.setText(result);
            running = false;
            btnControl.setEnabled(true);
        }
    }

    private byte[] getDataArray(int dataOutput, int size) {
        //Log.d(MainActivity.logPrefix, "Generating array using " + dataOutput);
        switch (dataOutput) {
            case 0:
                return zeroes; //0ms
            case 1:
                return Utils.generateRandomByteArray(random, size); //~350ms
            case 2:
                return Utils.generateRandomByteArray(xorShiftRNG, size); //~690ms
            case 3:
                return Utils.generateRandomByteArray(mersenneTwisterRNG, size); //~750ms
            case 4:
                return Utils.generateRandomByteArray(cmwc4096RNG, size); //~780ms
            case 5:
                return Utils.generateRandomByteArray(secureRandom, size); //~2880ms
            default:
                return zeroes;
        }
    }

    private void benchmarkGetDataArray() {
        for (int x = 0; x < 6; x++) {
            long preTime = SystemClock.elapsedRealtime();
            for (int c = 0; c < 5; c++) { //Generate 100MB
                getDataArray(x, megabyte20);
            }
            Log.d(MainActivity.logPrefix, "BENCHMARK - RNG: " + x + ", Time Spent: " + (SystemClock.elapsedRealtime() - preTime));
        }
    }

    /*
    benchmarkGetRandomString(random); //~41ms
    benchmarkGetRandomString(xorShiftRNG); //~44ms
    benchmarkGetRandomString(mersenneTwisterRNG); //~45ms
    benchmarkGetRandomString(cmwc4096RNG); //~48ms
    benchmarkGetRandomString(secureRandom); //302ms
    */
    public static void benchmarkGetRandomString(Random rng) {
        long preTime = SystemClock.elapsedRealtime();
        for (int c = 0; c < 1000; c++) {
            Utils.getRandomString(rng, 32);
        }
        Log.d(MainActivity.logPrefix, "BENCHMARK - RNG: " + rng.toString() + ", Time Spent: " + (SystemClock.elapsedRealtime() - preTime));
    }

}
