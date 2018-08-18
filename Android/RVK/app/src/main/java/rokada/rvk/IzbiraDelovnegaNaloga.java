package rokada.rvk;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class IzbiraDelovnegaNaloga extends AppCompatActivity {

    Button btnNadaljuj;
    String stevilo_zapisov="";
    String status_delNaloga="";
    private ProgressDialog pDialog;
    private ProgressDialog pDialog_vr;
    private String TAG = VnosSkladiscaActivity.class.getSimpleName();
    String strDelNal;
    String skladisceSifra;
    String vrstaSifra;

    EditText delNal;

    String url;
    private static String url_skl;
    private static String url_vrs;

    List<String> vsaSkladisca = new ArrayList<String>();
    Spinner spinner_skl;

    List<String> vseVrste = new ArrayList<String>();
    Spinner spinner_vrs;

    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_izbordelovnegamaloga);

        setTitle("IZDAJA NA DELOVNI NALOG");

        url=((Global) getApplication()).getUrl()+"PreveriDelNalog/";
        url_skl = ((Global) getApplication()).getUrl()+"SiSklad/";
        url_vrs = ((Global) getApplication()).getUrl()+"SiVrtrans/";

        spinner_skl= (Spinner) findViewById(R.id.spinner);
        spinner_vrs= (Spinner) findViewById(R.id.spinner_vrsta);

        delNal = (EditText) findViewById(R.id.delnal);


        new GetVrtrans().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new GetSkladisca().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        init();
    }

    public void onBackPressed()
    {
        Intent intent = new Intent(IzbiraDelovnegaNaloga.this, IzborActivity.class);
        startActivity(intent);
    }


    public void init(){
        btnNadaljuj = (Button) findViewById(R.id.btn_nadaljuj);
        btnNadaljuj.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                strDelNal = delNal.getText().toString();

                if (strDelNal.equals("")){
                    Toast.makeText(getApplicationContext(), "Vnesite dolovni nalog!", Toast.LENGTH_LONG).show();
                    return;
                }

                new IzbiraDelovnegaNaloga.GetNalog().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                try {
                    synchronized(this){
                        wait(1000);
                    }
                }
                catch(InterruptedException ex){
                }

                if (stevilo_zapisov.equals("0")){
                    Toast.makeText(getApplicationContext(), "Delovnega naloga ni v bazi", Toast.LENGTH_LONG).show();
                    return;
                }
                else if(status_delNaloga.equals("1")){
                    Toast.makeText(getApplicationContext(),
                            "Delovni nalog ni odprt!",
                            Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                else{
                    skladisceSifra = spinner_skl.getSelectedItem().toString();
                    ((Global) getApplication()).setSklNaziv(skladisceSifra);
                    skladisceSifra=skladisceSifra.split("\\ ")[0];
                    ((Global) getApplication()).setSkl(skladisceSifra);

                    vrstaSifra = spinner_vrs.getSelectedItem().toString();
                    ((Global) getApplication()).setVtNaziv(vrstaSifra);
                    vrstaSifra=vrstaSifra.split("\\ ")[0];
                    ((Global) getApplication()).setVrstaDokumenta(vrstaSifra);
                    ((Global) getApplication()).setSifrDelNaloga(strDelNal);

                    new IzbiraDelovnegaNaloga.GetFNStev().execute();
                    new IzbiraDelovnegaNaloga.DodajVFakNumat().execute();

                    /*try {
                        synchronized(this){
                            wait(1000);
                        }
                    }
                    catch(InterruptedException ex){
                    }*/



                    Intent toy = new Intent(IzbiraDelovnegaNaloga.this, IzdajaActivity.class);

                    startActivity(toy);
                }
            }
        });

    }

    private class GetSkladisca extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(IzbiraDelovnegaNaloga.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url_skl);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);


                    JSONArray skladisca = jsonObj.getJSONArray("SiSklad");

                    for (int i = 1; i < skladisca.length(); i++) {

                        JSONObject actor = skladisca.getJSONObject(i);
                        String sifra_skl = actor.getString("SIFRA")+" - "+actor.getString("NAZIV");

                        vsaSkladisca.add(sifra_skl);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            spinner_skl.setAdapter(new ArrayAdapter<String>(IzbiraDelovnegaNaloga.this,android.R.layout.simple_spinner_dropdown_item,  vsaSkladisca));

        }
    }

    private class GetVrtrans extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog_vr = new ProgressDialog(IzbiraDelovnegaNaloga.this);
            pDialog_vr.setMessage("Please wait...");
            pDialog_vr.setCancelable(false);
            pDialog_vr.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url_vrs);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);


                    JSONArray vrste = jsonObj.getJSONArray("SiVrtrans");

                    for (int i = 1; i < vrste.length(); i++) {

                        JSONObject actor = vrste.getJSONObject(i);
                        String sifra_vrs = actor.getString("SIFRA")+" - "+actor.getString("NAZIV");

                        vseVrste.add(sifra_vrs);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog_vr.isShowing())
                pDialog_vr.dismiss();

            spinner_vrs.setAdapter(new ArrayAdapter<String>(IzbiraDelovnegaNaloga.this,android.R.layout.simple_spinner_dropdown_item,  vseVrste));
            spinner_vrs.setSelection(14);

        }
    }

    private class GetNalog extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(IzbiraDelovnegaNaloga.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();


            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url+strDelNal);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {

                    JSONObject jsonObj = new JSONObject(jsonStr);

                    JSONArray nalog = jsonObj.getJSONArray("PreveriDelNalog");

                    JSONObject actor = nalog.getJSONObject(1);

                    stevilo_zapisov = actor.getString("STEVILO");
                    if (stevilo_zapisov.equals("0")){
                        status_delNaloga = "0";
                    }
                    else{
                        status_delNaloga = actor.getString("STATUS");
                    }

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            stevilo_zapisov = stevilo_zapisov;
            status_delNaloga = status_delNaloga;

        }
    }

    private class DodajVFakNumat extends AsyncTask<Void, Void, Void> {

        String SKL = ((Global) getApplication()).getSkl();
        String VT = ((Global) getApplication()).getVrstaDokumenta();
        String DELNALOG = ((Global) getApplication()).getSifrDelNaloga();
        String uporabnik=((Global) getApplication()).getUporabnik();

        @Override
        protected Void doInBackground(Void... params) {

            try {
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                String URL = ((Global) getApplication()).getUrl()+"FakNumat";
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("SKL", SKL);
                jsonBody.put("VT", VT);
                jsonBody.put("DELNALOG", DELNALOG);
                jsonBody.put("UPORABNIK", uporabnik);
                final String mRequestBody = jsonBody.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("LOG_VOLLEY", response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("LOG_VOLLEY", error.toString());
                    }
                }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        try {
                            return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                            return null;
                        }
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        String responseString = "";
                        if (response != null) {

                            responseString = String.valueOf(response.statusCode);

                        }
                        return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                    }
                };

                requestQueue.add(stringRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }
    }

    private class GetFNStev extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(IzbiraDelovnegaNaloga.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(((Global) getApplication()).getUrl()+"FakNumat");

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);


                    JSONArray skladisca = jsonObj.getJSONArray("FakNumat");

                    JSONObject actor = skladisca.getJSONObject(1);
                    String STEV = actor.getString("STEV");

                    int stevInt=0;
                    try {
                        stevInt = Integer.parseInt(STEV);
                    } catch(NumberFormatException nfe) {}

                    stevInt++;

                    ((Global) getApplication()).setStevFN(String.valueOf(stevInt));

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();


        }
    }
}
