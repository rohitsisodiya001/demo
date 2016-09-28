package com.ankit.pointofsolution;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ankit.pointofsolution.Models.Userdata;
import com.ankit.pointofsolution.api.ApiManager;
import com.ankit.pointofsolution.api.ResponseCodes;
import com.ankit.pointofsolution.config.Constants;
import com.ankit.pointofsolution.config.Messages;
import com.ankit.pointofsolution.config.StringUtils;
import com.ankit.pointofsolution.storage.Preferences;
import com.ankit.pointofsolution.utility.NetworkOperations;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Parser;

import java.util.Arrays;

import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_PHONE_STATE;

public class DeviceVerificationActivity extends AppCompatActivity  {

    private CoordinatorLayout coordinatorLayout;
    private NetworkOperations noptn;
    EditText VerificationCode;
    private Button verifyBtn;
    private String verficationcode;
    public ApiManager apiManager;
    private String sResponseCode,sResponseDesc;
    private Activity activity;
    private Context context;
    private ProgressDialog prgLoading = null;
    private Preferences preferences;
    JSONObject jsonObj;
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_PHONE_STATE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_verification);
        noptn = new NetworkOperations(DeviceVerificationActivity.this);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        apiManager = new ApiManager(this);
        activity = this;
        verifyBtn = (Button) findViewById(R.id.Verification_button);
        VerificationCode = (EditText) findViewById(R.id.VerificationCode);
        preferences = new Preferences(activity);

        final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                verficationcode = VerificationCode.getText().toString();
                if (!verficationcode.isEmpty()) {

                        //Checks for Internet connectivity.
                        if (noptn.hasActiveInternetConnection(DeviceVerificationActivity.this)) {
                            //ToDo write your code here to check
                            // Create Inner Thread Class
                            //Show loader
                            showLoading(Messages.PLZ_WAIT_AUTHENTICATE);
                            Thread background = new Thread(new Runnable() {
                                Message msg = new Message();
                                Bundle bndle = new Bundle();

                                @Override
                                public void run() {
                                    ApiManager.Status status = apiManager.processDeviceAuth(verficationcode);
                                    if (status == ApiManager.Status.ERROR) {
                                        sResponseCode = StringUtils.ERROR_CODE;
                                        sResponseDesc = apiManager.getErrorMessage();
                                        bndle.putString(StringUtils.CODE, sResponseCode);
                                        bndle.putString(StringUtils.DESC, sResponseDesc);
                                    } else {
                                        sResponseCode = StringUtils.SUCCESS;
                                        sResponseDesc = apiManager.getResponse();
//                                    System.out.println("sResponse: " + sResponseDesc);
                                    }
                                    msg.setData(bndle);
                                    bndle.putString(StringUtils.DESC, sResponseDesc);
                                    bndle.putString(StringUtils.CODE, sResponseCode);
                                    handler.sendMessage(msg);
                                }

                                // Define the Handler that receives messages from the thread and update the progress
                                private Handler handler = new Handler() {

                                    public void handleMessage(Message msg) {

                                        sResponseCode = msg.getData().getString(StringUtils.CODE);
                                        sResponseDesc = msg.getData().getString(StringUtils.DESC);
                                        if (sResponseCode.equals(StringUtils.SUCCESS)) {
                                            if ((null != sResponseDesc)) {
                                                System.out.println("sResponseDesc:" + sResponseDesc);
                                                // Store data in shared preference
                                                try {
                                                    jsonObj = new JSONObject(sResponseDesc);
                                                    preferences.setUserData(jsonObj.getJSONArray(Constants.USERS).toString());
                                                    preferences.setProductData(jsonObj.getJSONArray(Constants.PRODUCTS).toString());
                                                } catch (Exception e) {
                                                }
                                                preferences.setisDeviceVerified(true);
                                                startActivity(new Intent(DeviceVerificationActivity.this, LoginActivity.class));
                                                finish();
                                            } else {
                                                // ALERT MESSAGE
                                                showSnack(false, Messages.ACCESS_DENIED);
                                            }
                                        } else if (sResponseCode.equals(StringUtils.ERROR_CODE)) {
                                            showSnack(false, apiManager.getErrorMessage());
                                        }
                                        hideLoading();
                                    }
                                };

                            });
                            background.start();
                        } else {
                            showSnack(false, noptn.getErrorMessage());
                        }
                    }
                    else {
                        showSnack(false, Messages.ENTER_VERIFICATION_CODE);
                    }
            }
        });
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_PHONE_STATE)) {
            Snackbar.make(VerificationCode, R.string.permission_phone_status, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_PHONE_STATE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //populateAutoComplete();
            }
        }
    }

    private void showSnack(boolean isConnected,String status)
        {
         Snackbar snackbar = Snackbar.make(coordinatorLayout, status, Snackbar.LENGTH_LONG);
         View sbView = snackbar.getView();
         TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
         textView.setTextColor(Color.RED);
         snackbar.show();
         Toast.makeText(this,status,Toast.LENGTH_LONG).show();
         }
    /**
     * To show the loading dialog with given message
     *
     * @param msg
     *            Message to show when showing dialog.
     */
    public void showLoading(String msg) {

        if (prgLoading != null && prgLoading.isShowing())
            prgLoading.setMessage(msg);
        else
            prgLoading = ProgressDialog.show(activity, "", msg);

    }
    /**
     * To close the loading dialog
     */

    protected void hideLoading() {
        try {
            if (prgLoading != null && prgLoading.isShowing()) {
                prgLoading.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
