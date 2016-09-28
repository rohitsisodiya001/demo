package com.ankit.pointofsolution;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ankit.pointofsolution.IntentIntegrator.IntentIntegrator;
import com.ankit.pointofsolution.IntentIntegrator.IntentResult;
import com.ankit.pointofsolution.Models.OrderDetails;
import com.ankit.pointofsolution.Models.Productdata;
import com.ankit.pointofsolution.adapter.CustomAdapter;
import com.ankit.pointofsolution.api.ApiManager;
import com.ankit.pointofsolution.config.Constants;
import com.ankit.pointofsolution.dialog_fragments.AddItemManullyFragment;
import com.ankit.pointofsolution.dialog_fragments.DialogueAlertsFragment;
import com.ankit.pointofsolution.storage.Preferences;
import com.ankit.pointofsolution.utility.GetResponseDialogListener;
import com.ankit.pointofsolution.utility.Utility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import static android.R.layout.simple_spinner_item;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,GetResponseDialogListener {
    ApiManager apiManager;
    Preferences pref;
    private TextView vUseremail;
    public static ListView listView;
    public ArrayList<String> values;
    public static CustomAdapter adapter;

    private Productdata productdata;
    private String scanContent;
    private Gson gson;
    private Utility utility;
    private static OrderDetails orderDetails;
    public static ArrayList<OrderDetails> orderDetailsArrayList;
    public int totalprice;
    public static TextView vTotalconut;
    Intent i;
    String[] spinnerList;
    public static ArrayAdapter<String> dataAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        apiManager = new ApiManager(this);
        pref = new Preferences(this);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        productdata = new Productdata();

       // productdataList = Arrays.asList(gson.fromJson(pref.getProductData(), Productdata[].class));
        utility = new Utility(pref);
        listView = (ListView) findViewById(R.id.list);
        //listView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        // Defined Array values to show in ListView
        orderDetails = new OrderDetails();
        orderDetailsArrayList = new ArrayList<OrderDetails>();
        Resources res = getResources();
        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data
        vTotalconut = (TextView) findViewById(R.id.totalconut);
        spinnerList = new String[Constants.QUANTITY_MAX_VALUE];
        for(int i=0;i<Constants.QUANTITY_MAX_VALUE;i++)
        {
            spinnerList[i]=String.valueOf(i+1);
        }
        // Creating adapter for spinner
       dataAdapter = new ArrayAdapter<String>(this, simple_spinner_item, spinnerList);

        Button paymentoptionsbtn = (Button) findViewById(R.id.paymentoptions);
        paymentoptionsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalprice=0;
                adapter.notifyDataSetChanged();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                       // In that part of code we need to put other details like OrderID, order Status, Offer on item
                        String s = gson.toJson(orderDetailsArrayList, new TypeToken<ArrayList<OrderDetails>>(){}.getType());
                        utility.makeOrders(s);
                        i = new Intent(MainActivity.this, PaymentOptionsActivity.class);
                        startActivity(i);
                    }
                }).start();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);

        vUseremail = (TextView)header.findViewById(R.id.usermail);
        vUseremail.setText(pref.getUsername());

        if(savedInstanceState != null){
            orderDetailsArrayList = savedInstanceState.getParcelableArrayList("d");
            adapter = new CustomAdapter(this,orderDetailsArrayList,res);
            // Assign adapter to ListView
            listView.setAdapter(adapter);

            adapter.notifyDataSetChanged();
        }else {
            adapter = new CustomAdapter(this,orderDetailsArrayList,res);
            // Assign adapter to ListView
            listView.setAdapter(adapter);

            adapter.notifyDataSetChanged();
        }

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            savedInstanceState.putParcelableArrayList("d", orderDetailsArrayList);
            super.onSaveInstanceState(savedInstanceState);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.add_item){
            showAlert(Constants.ADD_ITEM_TYPE_1);;
            return true;
        }
        if(id == R.id.scan_button){
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
            return true;
        }
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_cart) {
            // Handle the camera action
        } else if (id == R.id.nav_return) {

        } else if (id == R.id.nav_reports) {

        } else if (id == R.id.nav_reciept) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_logout) {
            apiManager.logout(pref);
        } else if (id == R.id.nav_help) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            scanContent = scanningResult.getContents();
           // String scanFormat = scanningResult.getFormatName();
//            System.out.println("scanContent: "+scanContent);
            if(scanContent != null){
            /******* Firstly take data in model object ******/
             if(pref.getProductData().contains(scanContent))
                {
                    productdata = utility.getProductDetailsbySku(scanContent);
                    orderDetails = new OrderDetails();
                    orderDetails.setItemPrice(Float.parseFloat(productdata.getPrice()));
                    orderDetails.setItemQty(1);
                    orderDetails.setItemSku(scanContent);
                    orderDetails.setsItemName(productdata.getProductName());
                    orderDetailsArrayList.add(orderDetails);
                    listView.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();

                }
                else
                {
                    pref.setSkuCode(scanContent);
                    showAlert(Constants.ADD_ITEM_TYPE_2);
                }
            /******** Take Model Object in ArrayList **********/
            }
            else{
                Toast toast = Toast.makeText(getApplicationContext(),
                        "No scan data received!", Toast.LENGTH_SHORT);
                toast.show();
            }
            }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    //Show alert to to ask weather he want to add new item or not...
    public void showAlert(String AddType)
    {
        if(AddType.equalsIgnoreCase(Constants.ADD_ITEM_TYPE_1)) {
            AddItemManullyFragment newFragment = new AddItemManullyFragment();
            newFragment.show(this.getFragmentManager().beginTransaction(), "Abd");
        }
        else if(AddType.equalsIgnoreCase(Constants.ADD_ITEM_TYPE_2)) {
            DialogueAlertsFragment newFragment = new DialogueAlertsFragment();
            newFragment.show(this.getFragmentManager().beginTransaction(), "Abd");
        }

    }

    @Override
    public void updateResult(boolean response)
    {
//      System.out.println("response:"+pref.getProductData().contains(scanContent));
        if(response) {
            productdata = utility.getProductDetailsbySku(scanContent);
            orderDetails = new OrderDetails();
            orderDetails.setItemPrice(Float.parseFloat(productdata.getPrice()));
            orderDetails.setItemQty(1);
            orderDetails.setItemSku(scanContent);
            orderDetails.setsItemName(productdata.getProductName());
            orderDetailsArrayList.add(orderDetails);
            listView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }
    @Override
    public void updateResultSkuCode(Productdata productdata)
    {
//      System.out.println("response:"+pref.getProductData().contains(scanContent));
        if(productdata!=null) {
            orderDetails = new OrderDetails();
            orderDetails.setItemPrice(Float.parseFloat(productdata.getPrice()));
            orderDetails.setItemQty(1);
            orderDetails.setItemSku(productdata.getSku());
            orderDetails.setsItemName(productdata.getProductName());
            orderDetailsArrayList.add(orderDetails);
            listView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }
}
