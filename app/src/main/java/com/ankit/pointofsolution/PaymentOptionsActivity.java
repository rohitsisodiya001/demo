package com.ankit.pointofsolution;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ankit.pointofsolution.IntentIntegrator.IntentIntegrator;
import com.ankit.pointofsolution.Models.OrderDetails;
import com.ankit.pointofsolution.api.ApiManager;
import com.ankit.pointofsolution.config.Constants;
import com.ankit.pointofsolution.storage.Preferences;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PaymentOptionsActivity extends MainActivity {

    Button b1, b2, b3;
    Map<String, String> orderDetailses;
    Preferences pref;
    ApiManager apiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_options);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pref = new Preferences(this);
        apiManager = new ApiManager(this);
        b1 = (Button) findViewById(R.id.btncash);
        b2 = (Button) findViewById(R.id.btncc);
        b3 = (Button) findViewById(R.id.btndc);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderDetailses = pref.getOrderDetails();
                for(int i=0; i<orderDetailses.size();i++)
                {
                    System.out.println("orderDetailses:"+ orderDetailses);
                    Toast.makeText(PaymentOptionsActivity.this,orderDetailses.toString(),Toast.LENGTH_LONG).show();

                }
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.customer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_settings) {
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            apiManager.logout(pref);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
