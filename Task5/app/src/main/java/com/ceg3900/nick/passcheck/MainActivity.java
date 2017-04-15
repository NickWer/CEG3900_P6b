package com.ceg3900.nick.passcheck;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.threetenabp.AndroidThreeTen;

import me.gosimple.nbvcxz.Nbvcxz;
import me.gosimple.nbvcxz.resources.Feedback;
import me.gosimple.nbvcxz.scoring.Result;
import me.gosimple.nbvcxz.scoring.TimeEstimate;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidThreeTen.init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCalculate = (Button)findViewById(R.id.btnCalculate);
        TextView lblStrength = (TextView) findViewById(R.id.lblStrength);
        final EditText txtPassword = (EditText) findViewById(R.id.txtPassword);


        class CalculatePassowrdStrength extends AsyncTask<String, Long, Result> {

            @Override
            protected Result doInBackground(String... params) {
                final String target = params[0].toLowerCase();
                Nbvcxz tester = new Nbvcxz();
                return tester.estimate(target);
            }

            @Override
            protected void onProgressUpdate(Long... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(Result result) {
                String output = "Here's the feedback from the estimation session:\n";

                String timeToCrackOff = TimeEstimate.getTimeToCrackFormatted(result, "OFFLINE_BCRYPT_12");
                String timeToCrackOn = TimeEstimate.getTimeToCrackFormatted(result, "ONLINE_THROTTLED");

                output += "Overall your password is " + (result.isMinimumEntropyMet() ? "" : "not ") + "random enough\n\n";
                output += "Online time to crack: " + timeToCrackOn +"\n";
                output += "Offline time to crack: " + timeToCrackOff +"\n\n";
                Feedback feedback = result.getFeedback();
                if(feedback != null)
                {
                    if (feedback.getWarning() != null)
                        output += "Warning: " + feedback.getWarning() + "\n";
                    for (String suggestion : feedback.getSuggestion())
                    {
                        output += "Suggestion: " + suggestion + "\n";
                    }
                }
                lblStrength.setText(output);
            }
        }


        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalculatePassowrdStrength c = new CalculatePassowrdStrength();
                lblStrength.setText("Calculating... Please wait");
                c.execute(txtPassword.getText().toString());
            }
        });
    }
}
