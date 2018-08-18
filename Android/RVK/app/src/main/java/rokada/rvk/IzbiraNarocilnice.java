package rokada.rvk;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class IzbiraNarocilnice extends AppCompatActivity {

    Button btnNadaljuj;
    String url;
    EditText vrdok;
    EditText stev;
    String strVrdok;
    String strStev;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vnos_narocilnice);

        setTitle("PREVZEMNICA");

        vrdok = (EditText) findViewById(R.id.vrdok);
        stev = (EditText) findViewById(R.id.stev);

        init();

    }

    public void onBackPressed()
    {
        Intent intent = new Intent(IzbiraNarocilnice.this, IzborActivity.class);
        startActivity(intent);
    }

    public void init() {
        btnNadaljuj = (Button) findViewById(R.id.btn_nadaljuj);
        btnNadaljuj.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                strVrdok = vrdok.getText().toString();
                strStev = stev.getText().toString();

                Intent toy = new Intent(IzbiraNarocilnice.this, PostavkePrevzemnica.class);
                ((Global) getApplication()).setVrdok(strVrdok);
                ((Global) getApplication()).setStev(strStev);
                new IzbiraNarocilnice.UstvariNarmatTemp().execute();
                startActivity(toy);

            }

        });

    }

    private class UstvariNarmatTemp extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            String URL = ((Global) getApplication()).getUrl()+"NarmatTemp/"+strVrdok+"/"+strStev;


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