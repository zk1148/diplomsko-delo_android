package rokada.rvk;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostavkePovratnica  extends AppCompatActivity {

    public ActionBar getSupportActionBar() {
        return super.getSupportActionBar();
    }

    private String TAG = PostavkePovratnica.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;

    private static String url;

    ArrayList<HashMap<String, String>> postavkeList;
    ListAdapter adapter;

    Button btnKreirajPovratnico;

    String zadnji_ZST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postavke_povratnica);

        setTitle("POVRATNICA - Postavke");
        url=((Global) getApplication()).getUrl()+"GibMat/";

        postavkeList = new ArrayList<>();

        lv = (ListView) findViewById(R.id.list);

        String delNal=((Global) getApplication()).getSifrDelNaloga();
        url=((Global) getApplication()).getUrl()+"GibMat/";

        postavkeList.clear();
        new getPostavke().execute();

        osvezi();

        init();

    }

    public void osvezi(){
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipelayout);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                url=((Global) getApplication()).getUrl()+"GibMat/";

                postavkeList.clear();
                new getPostavke().execute();

                swipeRefreshLayout.setRefreshing(false);
            }


        });
    }

    public void init(){
        btnKreirajPovratnico = (Button) findViewById(R.id.btn_kreiraj_povratnico);
        btnKreirajPovratnico.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new PostavkePovratnica.KreirajPovratnico().execute();
                String delnal = ((Global) getApplication()).getSifrDelNaloga();
                Toast.makeText(getApplicationContext(), "Kreirana je bila povratnica na delovnem nalogu "+delnal+"!", Toast.LENGTH_LONG).show();
                Intent toy = new Intent(PostavkePovratnica.this, IzbiraDelovnegaNalogaPovratnice.class);
                startActivity(toy);
            }
        });
    }

    public class CustomAdapter extends SimpleAdapter {
        public CustomAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final View view=super.getView(position, convertView, parent);

            final Button btnUredi= (Button) view.findViewById(R.id.btn_uredi);
            btnUredi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final TextView tekst = (TextView) ((View) view.getParent()).findViewById(R.id.zst);
                    final TextView sifArt = (TextView) ((View) view.getParent()).findViewById(R.id.artikel);
                    final TextView nazArt = (TextView) ((View) view.getParent()).findViewById(R.id.artikel);
                    final TextView vracam = (TextView) ((View) view.getParent()).findViewById(R.id.vracam);
                    final TextView izdano = (TextView) ((View) view.getParent()).findViewById(R.id.kolicina);

                    ((Global) getApplication()).setKoncnica(tekst.getText().toString());
                    ((Global) getApplication()).setSifArt(sifArt.getText().toString());
                    ((Global) getApplication()).setNazArt(nazArt.getText().toString());
                    ((Global) getApplication()).setVracam(vracam.getText().toString());
                    ((Global) getApplication()).setIzdano(izdano.getText().toString());
                    Intent toy2 = new Intent(PostavkePovratnica.this, UrediPostavkoPovratnicaActivity.class);
                    startActivity(toy2);

                }
            });


            return view;

        }
    }

    private class getPostavke extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(PostavkePovratnica.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);
            //Toast.makeText(getApplicationContext(), jsonStr, Toast.LENGTH_LONG).show();


            Log.e(TAG, "Response from url: " + jsonStr);


            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);


                    JSONArray postavke = jsonObj.getJSONArray("GibMat_TEMP");

                    // zanka čez vse postavke
                    for (int i = 1; i < postavke.length(); i++) {
                        JSONObject c = postavke.getJSONObject(i);

                        String ZST = c.getString("ZST");
                        zadnji_ZST=ZST;
                        String ARTIKEL = c.getString("SIFART")+" - "+c.getString("NAZIV");
                        String KOLICINA = "Izdano: "+c.getString("IZDANO")+" "+c.getString("NAZIV1");
                        String VRACAM = "Vračam: "+c.getString("VRACAM")+" "+c.getString("NAZIV1");


                        // tmp hash map for single contact
                        HashMap<String, String> postavka = new HashMap<>();

                        postavka.put("ZST", ZST);
                        postavka.put("ARTIKEL", ARTIKEL);
                        postavka.put("KOLICINA", KOLICINA);
                        postavka.put("VRACAM", VRACAM);

                        // adding contact to contact list
                        postavkeList.add(postavka);
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


            adapter = new PostavkePovratnica.CustomAdapter(
                    PostavkePovratnica.this, postavkeList,
                    R.layout.list_postavka_povratnica, new String[]{"ZST", "ARTIKEL", "KOLICINA", "VRACAM"}, new int[]{R.id.zst, R.id.artikel, R.id.kolicina, R.id.vracam});


            lv.setAdapter(adapter);

        }
    }

    private class KreirajPovratnico extends AsyncTask<Void, Void, Void> {


        String delnal = ((Global) getApplication()).getSifrDelNaloga();

        @Override
        protected Void doInBackground(Void... params) {

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            String URL = ((Global) getApplication()).getUrl()+"PovratnicaDokument/"+zadnji_ZST+"/"+delnal+"/";

            final String mRequestBody = "";

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

            return null;
        }
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }
    }
}
