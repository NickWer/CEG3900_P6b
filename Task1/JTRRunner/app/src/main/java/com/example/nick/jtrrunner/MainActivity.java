package com.example.nick.jtrrunner;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnGo = (Button) findViewById(R.id.button);
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new runSsh()).execute("ec2-34-209-162-103.us-west-2.compute.amazonaws.com", R.id.txtOutput1);
                (new runSsh()).execute("ec2-34-209-228-210.us-west-2.compute.amazonaws.com", R.id.txtOutput2);
                (new runSsh()).execute("ec2-34-209-93-48.us-west-2.compute.amazonaws.com", R.id.txtOutput3);
                (new runSsh()).execute("ec2-52-26-55-136.us-west-2.compute.amazonaws.com", R.id.txtOutput4);
            }
        });
    }

    private class runSsh extends AsyncTask<Object, Void, Void> {
        String stdio = "Connected via ssh";
        EditText textfield = null;

        /**
         * Gets a File for the private key. Busts the space saving methods where
         * assets are meant to be read from the apk directly.
         * http://stackoverflow.com/questions/8474821
         * @return a File object referencing the private key
         */
        private File getPrivKey(){
            File f = new File(getCacheDir()+"/jtrapp.pem");
            if (!f.exists()) try {

                InputStream is = getAssets().open("jtrapp.pem");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();


                FileOutputStream fos = new FileOutputStream(f);
                fos.write(buffer);
                fos.close();
            } catch (Exception e) { throw new RuntimeException(e); }

            return f;
        }

        private String buildCommand(String server){
            String out = "ssh -i ";
            out += getPrivKey().getPath() + " ";
            out += "ubuntu@" + server;
            out += "john etc-shadow-2009.txt";
            return out;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            textfield.setText(stdio);
        }

        @Override
        protected Void doInBackground(Object... params) {
            Process p = null;
            try {
                p = Runtime.getRuntime().exec(buildCommand((String)params[0]));
                PrintStream out = new PrintStream(p.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                textfield = (EditText) findViewById((int)params[1]);

                //out.println("ls -l /home/me");
                while (in.ready()) {
                    stdio += "\n" + in.readLine();
                    publishProgress();
                }
                out.println("exit");

                p.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
