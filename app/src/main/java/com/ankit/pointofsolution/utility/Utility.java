package com.ankit.pointofsolution.utility;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.ankit.pointofsolution.Models.OrderDetails;
import com.ankit.pointofsolution.Models.Productdata;
import com.ankit.pointofsolution.config.Constants;
import com.ankit.pointofsolution.storage.Preferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ankit on 08-Sep-16.
 */
public class Utility {
    private ArrayList<Productdata> productdataList;
    private boolean status;
    private Preferences pref;

    public Utility(Preferences pref){
        this.pref = pref;
    }

    public boolean AdditemCatalog(Productdata productdata)
    {
        productdataList = new ArrayList<Productdata>();
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        try {
            JSONArray jsonArray = new JSONArray(pref.getProductData());
            for(int i=0; i<jsonArray.length();i++)
            {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                Productdata productdata1 = new Productdata();
//              System.out.println("jsonObject1: "+jsonObject1);
                productdata1.setProductName(String.valueOf(jsonObject1.get("productName")));
                productdata1.setPrice(String.valueOf(jsonObject1.get("price")));
                productdata1.setBrand(String.valueOf(jsonObject1.get("brand")));
                productdata1.setSku(String.valueOf(jsonObject1.get("sku")));
                productdataList.add(productdata1);
            }
           // System.out.println("productdataList.length(): "+productdataList.size()+" productdata:"+productdata.getSku());
            productdataList.add(jsonArray.length(),productdata);
//            System.out.println("productdataList.length()2: "+productdataList.size());
            String json = gson.toJson(productdataList);
//            System.out.println("json: "+json);
            if(productdataList.size()==jsonArray.length()+1)
            {
                status = true;
                pref.setProductData(json);
            }else status = false;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return status;
    }

    public Productdata getProductDetailsbySku(String skucode)
    {
        Productdata productdata = new Productdata();
        try {
            JSONArray jsonArray = new JSONArray(pref.getProductData());
            for(int i=0; i<jsonArray.length();i++)
            {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                //Productdata productdata = new Productdata();
//                System.out.println("jsonObject1: "+jsonObject1);
                if(String.valueOf(jsonObject1.get("sku")).equals(skucode)) {
//                    System.out.println("i ewrwerew: "+i);
                    productdata.setProductName(String.valueOf(jsonObject1.get("productName")));
                    productdata.setPrice(String.valueOf(jsonObject1.get("price")));
                    productdata.setBrand(String.valueOf(jsonObject1.get("brand")));
                    productdata.setSku(String.valueOf(jsonObject1.get("sku")));
                   // productdata1 = productdata;
                    break;
                 }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    return productdata;
    }

    public String genrateOrderID()
    {
        String orderid = null;
        int oid = 0;
        String imei =  pref.getIMEI();
        String slast5imei = imei.substring(Math.max(imei.length() - 5, 0));
        if(pref.getAutoIncreamentId()==0)//|| pref.getAutoIncreamentId()==Constants.INITIAL_ORDER_ID
        {
            oid = Constants.INITIAL_ORDER_ID;
            pref.setAutoIncreamentId(oid);
        }else
        {
            oid = pref.getAutoIncreamentId();
        }
        orderid = slast5imei + oid;
        if(pref.checkOrderIdExist(orderid)) {
            if (pref.getOrderStatusByOrderId(orderid)!=Constants.ORDER_INITIAL_STATUS) {
                int a = Constants.INITIAL_ORDER_ID + 1;
                pref.setAutoIncreamentId(oid);
                orderid = slast5imei + a;
            }
        }
        return orderid;
    }

    public void makeOrders(String productJson)
    {
        JSONArray ja = new JSONArray();
        JSONObject js = new JSONObject();
        JSONObject js1 = new JSONObject();
        HashMap<String, String> orderdetailsMap = new HashMap<>();
        String orderId = genrateOrderID();
        try {
            //js.put("orderID","001111");
            js.put(Constants.KEY_PRODUCT_DETAILS,productJson);
            js.put(Constants.KEY_ORDER_STATUS, Constants.ORDER_INITIAL_STATUS);
            js.put(Constants.KEY_ORDER_STORAGE_STATUS, Constants.ORDER_STORAGE_STATUS);
            ja.put(js);
            js1.put(orderId,ja);
            orderdetailsMap.put(orderId,js.toString());
            System.out.println("a: "+js1.toString());
            pref.setOrderDetails(orderdetailsMap);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
