package com.mailbomber.mailbomber;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    EditText sender;
    TextInputLayout textInputLayout_sender;
    EditText to;
    EditText subject;
    EditText body;
    EditText count_var;
    String to_txt;
    String subject_txt;
    String body_txt;
    String count;
    String from;
    int indentify;
    ProgressDialog dialog;
    dataBaseServece dataBaseServece;
    Thread thread;
    Cursor cursor;
    Long time_cal;
    AlertDialog alertDialog;
    public AdView mAdView;

    private static String LOG_TAG = "EXAMPLE";

    NativeExpressAdView mAdView2;
    VideoController mVideoController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sender= (EditText) findViewById(R.id.sender);
        textInputLayout_sender= (TextInputLayout) findViewById(R.id.sender_TextInput);
        mAdView = (AdView) findViewById(R.id.adview);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);




// Locate the NativeExpressAdView.
        mAdView2 = (NativeExpressAdView) findViewById(R.id.adView);

// Set its video options.
        mAdView2.setVideoOptions(new VideoOptions.Builder()
                .setStartMuted(true)
                .build());

// The VideoController can be used to get lifecycle events and info about an ad's video
// asset. One will always be returned by getVideoController, even if the ad has no video
// asset.
        mVideoController = mAdView2.getVideoController();
        mVideoController.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
            @Override
            public void onVideoEnd() {
                Log.d(LOG_TAG, "Video playback is finished.");
                super.onVideoEnd();
            }
        });

// Set an AdListener for the AdView, so the Activity can take action when an ad has finished
// loading.
        mAdView2.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (mVideoController.hasVideoContent()) {
                    Log.d(LOG_TAG, "Received an ad that contains a video asset.");
                } else {
                    Log.d(LOG_TAG, "Received an ad that does not contain a video asset.");
                }
            }
        });

        mAdView2.loadAd(new AdRequest.Builder().build());


        alertDialog=new AlertDialog.Builder(MainActivity.this)
                .setTitle("Please Wait")
                .setCancelable(false).create();


        dataBaseServece=new dataBaseServece(this);
        dataBaseServece.open();
        dataBaseServece.insert(Calendar.getInstance().getTimeInMillis());

        dialog= new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Loading. Sending mails...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);

        to= (EditText) findViewById(R.id.to);
        subject= (EditText) findViewById(R.id.subject);
        body= (EditText) findViewById(R.id.body);
        count_var= (EditText) findViewById(R.id.count);
        indentify=2;


        thread=new Thread(){
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateText();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        thread.start();
    }



    public void updateText(){
        cursor=dataBaseServece.select();
        time_cal=Calendar.getInstance().getTimeInMillis();
        if(time_cal<=Long.valueOf(cursor.getString(1))){
            final Long timer=Long.valueOf(cursor.getString(1))-time_cal;
            alertDialog.setMessage("Wait "+Long.toString(timer/60000)+" Minutes To send mail again");
            if (!alertDialog.isShowing()) {
                alertDialog.show();
            }
        }
        else {
            alertDialog.dismiss();
        }


    }



    public void select(View view){
        Boolean checked=((RadioButton) view).isChecked();
        if (checked){
            if (view.getId()==R.id.random){
                textInputLayout_sender.setVisibility(TextInputLayout.GONE);
                indentify=0;
            }
            else {
                textInputLayout_sender.setVisibility(TextInputLayout.VISIBLE);
                indentify=1;
            }

        }
    }

    // Assynk Task
    public class task extends AsyncTask<Void,Void,Void>{

        RetryPolicy retryPolicy=new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        @Override
        protected Void doInBackground(Void... params) {
            RequestQueue requestQueue= Volley.newRequestQueue(MainActivity.this);
            StringRequest stringRequest=new StringRequest(Request.Method.POST, "http://emailbomb.co/index.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(MainActivity.this,"succefull",Toast.LENGTH_LONG).show();
                    dataBaseServece.update(Calendar.getInstance().getTimeInMillis()+600000);
                    dialog.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(MainActivity.this,"unsuccefull",Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> pram=new HashMap<>();
                    pram.put("email",to_txt);
                    pram.put("count",count);
                    pram.put("from",from);
                    pram.put("subject",subject_txt);
                    pram.put("body",body_txt);
                    return pram;
                }



            };
            stringRequest.setRetryPolicy(retryPolicy);
            requestQueue.add(stringRequest);

            return null;
        }
    }
    //Email Validater

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    public void send(View view){
        to_txt=to.getText().toString();
        body_txt=body.getText().toString();
        subject_txt=subject.getText().toString();
        count=count_var.getText().toString();

        dialog.show();

        if (indentify==1){
            from=sender.getText().toString();
        }
        if (indentify==2){
            TextView new_error= (TextView) findViewById(R.id.new_error);
            new_error.setVisibility(TextView.VISIBLE);
        }
        if (indentify==0){
            from="";
        }

        if (!isEmailValid(to_txt)==true){

            Toast.makeText(getApplicationContext(),"Invalid Email",Toast.LENGTH_LONG).show();
            dialog.dismiss();

        }
        else {
            if (!to_txt.equals("")&&!body_txt.equals("")&&!subject_txt.equals("")&&!count.equals("")&&(indentify==1||indentify==0)){
                if (Integer.valueOf(count)<=50) {
                    TextView error= (TextView) findViewById(R.id.count_error);
                    error.setVisibility(TextView.GONE);
                    TextView new_error= (TextView) findViewById(R.id.new_error);
                    new_error.setVisibility(TextView.GONE);
                    new task().execute();
                }else {
                    TextView error= (TextView) findViewById(R.id.count_error);
                    error.setVisibility(TextView.VISIBLE);
                    dialog.dismiss();
                }
            }
            else {
                Toast.makeText(getApplicationContext(),"Input all above the values",Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        }

    }
}
