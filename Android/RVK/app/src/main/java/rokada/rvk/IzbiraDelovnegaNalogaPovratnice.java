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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class IzbiraDelovnegaNalogaPovratnice extends AppCompatActivity {

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

    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vnosnaloga_povratnice);

        url=((Global) getApplication()).getUrl()+"PreveriDelNalog/";

        setTitle("POVRATNICA NA DELOVNI NALOG");

        delNal = (EditText) findViewById(R.id.delnal);

        init();
    }

    public void onBackPressed()
    {
        Intent intent = new Intent(IzbiraDelovnegaNalogaPovratnice.this, IzborActivity.class);
        startActivity(intent);
    }

    public void init() {
        btnNadaljuj = (Button) findViewById(R.id.btn_nadaljuj);
        btnNadaljuj.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                strDelNal = delNal.getText().toString();

                if (strDelNal.equals("")){
                    Toast.makeText(getApplicationContext(), "Vnesite dolovni nalog!", Toast.LENGTH_LONG).show();
                    return;
                }

                new IzbiraDelovnegaNalogaPovratnice.GetNalog().execute();

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
                    Intent toy = new Intent(IzbiraDelovnegaNalogaPovratnice.this, PostavkePovratnica.class);
                    ((Global) getApplication()).setSifrDelNaloga(strDelNal);
                    new IzbiraDelovnegaNalogaPovratnice.UstvariGibMatTemp().execute();
                    startActivity(toy);
                }
            }

        });
    }

    private class GetNalog extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(IzbiraDelovnegaNalogaPovratnice.this);
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

    private class UstvariGibMatTemp extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            String URL = ((Global) getApplication()).getUrl()+"Gibmat/"+strDelNal;


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
                    return null;
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


