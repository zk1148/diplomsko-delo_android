package rokada.rvk;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import java.util.HashMap;

public class UrediPostavkoIzdajaActivity extends AppCompatActivity {
    Button btnUredi;
    Button btnPreklici;

    private String TAG = UrediPostavkoActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private String url;
    String SIFART;
    String KOLICINA;
    String NAZIV;
    String NAZIV_EM;
    String ZALOGA;


    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uredipostavko_izdaja);
        url = ((Global) getApplication()).getUrl()+"FakBumat/";

        String vt = ((Global) this.getApplication()).getVrstaDokumenta();
        String stev = ((Global) this.getApplication()).getStevFN();
        String skl = ((Global) this.getApplication()).getSkl();
        String koncnica = ((Global) this.getApplication()).getKoncnica();

        url = ((Global) getApplication()).getUrl()+"FakBumat/"+vt+"/"+stev+"/"+skl+"/"+koncnica+"/";

        setTitle("Urejanje postavke");

        new UrediPostavkoIzdajaActivity.GetPostavke().execute();

        init();

    }

    public void init(){
        btnPreklici = (Button) findViewById(R.id.btn_prekliciurejanje);
        btnPreklici.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((Global) getApplication()).setTabPozicija(2);
                Intent toy = new Intent(UrediPostavkoIzdajaActivity.this, IzdajaActivity.class);
                startActivity(toy);
            }
        });

        btnUredi = (Button) findViewById(R.id.btn_uredipostavko);
        btnUredi.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new UrediPostavkoIzdajaActivity.UrediPostavko().execute();
                ((Global) getApplication()).setTabPozicija(2);

                EditText kolicinaET = (EditText) findViewById(R.id.kolicina);
                TextView zalogaTV = (TextView) findViewById(R.id.zaloga);
                if (Integer.parseInt(zalogaTV.getText().toString())<Integer.parseInt(kolicinaET.getText().toString())){
                    Toast.makeText(getApplicationContext(),
                            "Količina ne more biti večja od zaloge!",
                            Toast.LENGTH_LONG)
                            .show();
                    return;
                }

                Intent toy = new Intent(UrediPostavkoIzdajaActivity.this, IzdajaActivity.class);
                startActivity(toy);
            }
        });
    }

    private class GetPostavke extends AsyncTask<Object, Object, HashMap<String, String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(UrediPostavkoIzdajaActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected HashMap<String, String> doInBackground(Object... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);


            Log.e(TAG, "Response from url: " + jsonStr);


            HashMap<String, String> postavka = new HashMap<>();

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray nalogi = jsonObj.getJSONArray("FakBumat");
                    JSONObject cc = nalogi.getJSONObject(1);

                    SIFART = cc.getString("ARTI");
                    KOLICINA = cc.getString("KOLICINA");
                    NAZIV = cc.getString("NAZIV");
                    NAZIV_EM = cc.getString("NAZIV1");
                    ZALOGA = cc.getString("ZALOGA");


                    postavka.put("SIFART", SIFART);
                    postavka.put("KOLICINA", KOLICINA);
                    postavka.put("NAZIV", NAZIV);
                    postavka.put("NAZIV_EM", NAZIV_EM);
                    postavka.put("ZALOGA", ZALOGA);



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

            return postavka;
        }

        @Override
        protected void onPostExecute(HashMap<String, String> result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            TextView sifra = (TextView) findViewById(R.id.sifraArtikla);
            sifra.setText(result.get("SIFART"));

            EditText kolicina = (EditText) findViewById(R.id.kolicina);
            kolicina.setText(result.get("KOLICINA"));

            TextView naziv = (TextView) findViewById(R.id.nazivArtikla);
            naziv.setText(result.get("NAZIV"));

            TextView naziv_em = (TextView) findViewById(R.id.em);
            naziv_em.setText(result.get("NAZIV_EM"));

            TextView zaloga = (TextView) findViewById(R.id.zaloga);
            zaloga.setText(result.get("ZALOGA"));

        }
    }

    private class UrediPostavko extends AsyncTask<Void, Void, Void> {
        TextView sifra=(TextView)findViewById(R.id.sifraArtikla);
        EditText kolicina=(EditText)findViewById(R.id.kolicina);

        String strSifra=sifra.getText().toString();
        String strKolicina=kolicina.getText().toString();

        String vtUredi = ((Global) getApplication()).getVrstaDokumenta();
        String stevUredi = ((Global) getApplication()).getStevFN();
        String sklUredi = ((Global) getApplication()).getSkl();

        @Override
        protected Void doInBackground(Void... params) {

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            String URL = ((Global) getApplication()).getUrl()+"FakBumat/"+vtUredi+"/"+stevUredi+"/"+sklUredi+"/"+strSifra+"/"+strKolicina+"/";

            final String mRequestBody = "";

            StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
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

            return null;
        }
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }
    }
}
