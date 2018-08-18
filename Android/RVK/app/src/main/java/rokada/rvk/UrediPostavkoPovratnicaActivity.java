package rokada.rvk;


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

import java.io.UnsupportedEncodingException;

public class UrediPostavkoPovratnicaActivity extends AppCompatActivity {

    Button btnUredi;
    Button btnPreklici;

    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uredivracanjepovratnice);

        final TextView sifArt = (TextView) findViewById(R.id.sifraArtikla);
        final TextView nazArt = (TextView) findViewById(R.id.nazivArtikla);
        final EditText vracam = (EditText) findViewById(R.id.kolicina);
        final TextView izdano = (TextView) findViewById(R.id.izdano);

        String strArtikel=((Global) getApplication()).getSifArt();
        String[] splitArt = strArtikel.split("\\s+");

        String strVracam=((Global) getApplication()).getVracam();
        String[] splitVracam = strVracam.split("\\s+");

        String strIzdano=((Global) getApplication()).getIzdano();
        String[] splitIzdano = strIzdano.split("\\s+");

        sifArt.setText(splitArt[0]);
        nazArt.setText(strArtikel.substring(13));
        vracam.setText(splitVracam[1]);
        izdano.setText(splitIzdano[1]);

        init();

    }

    public void init(){
        btnPreklici = (Button) findViewById(R.id.btn_prekliciurejanje);
        btnPreklici.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent toy = new Intent(UrediPostavkoPovratnicaActivity.this, PostavkePovratnica.class);
                startActivity(toy);
            }
        });

        btnUredi = (Button) findViewById(R.id.btn_uredipostavko);
        btnUredi.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final TextView izdano = (TextView) findViewById(R.id.izdano);
                final EditText vracam = (EditText) findViewById(R.id.kolicina);

                if (vracam.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Vnesite vrnjeno količino!", Toast.LENGTH_LONG).show();
                    return;
                }

                float floatIzdano = Float.parseFloat(izdano.getText().toString());
                float floatVracam = Float.parseFloat(vracam.getText().toString());

                if(floatIzdano<floatVracam){
                    Toast.makeText(getApplicationContext(), "Vrnjena količina ne more biti večja od izdane!", Toast.LENGTH_LONG).show();
                    return;
                }

                new UrediPostavkoPovratnicaActivity.UrediPostavko().execute();
                Intent toy = new Intent(UrediPostavkoPovratnicaActivity.this, PostavkePovratnica.class);
                startActivity(toy);
            }
        });
    }

    private class UrediPostavko extends AsyncTask<Void, Void, Void> {
        EditText vracam=(EditText)findViewById(R.id.kolicina);

        String strVracam=vracam.getText().toString();

        String zst = ((Global) getApplication()).getKoncnica();

        @Override
        protected Void doInBackground(Void... params) {

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            String URL = ((Global) getApplication()).getUrl()+"GibMat/"+zst+"/"+strVracam+"/";

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
