package rokada.rvk;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class Tab2Inventura extends Fragment {
    public Button btnNalozi;
    public Button btnDodaj;
    private String TAG = Tab2Inventura.class.getSimpleName();
    private String TAG2 = Tab2Inventura.class.getSimpleName();
    private ProgressDialog pDialog;
    private ProgressDialog pDialog2;
    private String url;
    private String sifraArtikla;

    String naziv;
    String naziv_skl;
    String naziv_em;
    String zaloga;
    View superview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.activity_dodajpostavko,container,false);

        url=((Global) getActivity().getApplication()).getUrl()+"InventurDnevnik/";

        TextView naziv_sklTV = (TextView) view.findViewById(R.id.skladisce);
        btnNalozi = (Button) view.findViewById(R.id.btn_nalozi);

        init(view);

        EditText etSifra = (EditText) view.findViewById(R.id.sifra);
        EditText kolicinaET = (EditText) view.findViewById(R.id.kolicina);
        kolicinaET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    btnNalozi.performClick();

                } else {

                }
            }
        });

        return view;

    }

    public void init(final View view){
        superview=view;
        btnNalozi = (Button) view.findViewById(R.id.btn_nalozi);
        btnNalozi.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText sifraET = (EditText) view.findViewById(R.id.sifra);
                sifraArtikla=sifraET.getText().toString();

                //new GetPostavke().execute();
                new GetPostavke().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                try {
                    synchronized(this){
                        wait(1000);
                    }
                }
                catch(InterruptedException ex){
                }

                TextView nazivTV = (TextView) view.findViewById(R.id.artikel);
                nazivTV.setText(naziv);

                TextView naziv_sklTV = (TextView) view.findViewById(R.id.skladisce);
                naziv_sklTV.setText(naziv_skl);

                TextView naziv_emTV = (TextView) view.findViewById(R.id.em);
                naziv_emTV.setText(naziv_em);

                TextView zalogaTV = (TextView) view.findViewById(R.id.zaloga);
                zalogaTV.setText(zaloga);

            }
        });



        btnDodaj = (Button) view.findViewById(R.id.btn_dodaj);
        btnDodaj.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText sifraET = (EditText) view.findViewById(R.id.sifra);
                EditText kolicinaET = (EditText) view.findViewById(R.id.kolicina);



                if (sifraET.getText().toString().matches("") || kolicinaET.getText().toString().matches("")){
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Za vnašanje nove postavke, morata biti vnešena šifra in količina!",
                            Toast.LENGTH_LONG)
                            .show();
                    return;
                }

                new dodajPolje().execute();

                sifraET.setText("");
                kolicinaET.setText("");

                TextView nazivTV = (TextView) view.findViewById(R.id.artikel);
                nazivTV.setText("");

                TextView naziv_sklTV = (TextView) view.findViewById(R.id.skladisce);
                naziv_sklTV.setText("");

                TextView naziv_emTV = (TextView) view.findViewById(R.id.em);
                naziv_emTV.setText("");

                TextView zalogaTV = (TextView) view.findViewById(R.id.zaloga);
                zalogaTV.setText("");

                sifraET.requestFocus();
            }

        });
    }


    private class GetPostavke extends AsyncTask<Object, Object, HashMap<String, String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected HashMap<String, String> doInBackground(Object... arg0) {
            HttpHandler sh = new HttpHandler();
            String skl=((Global) getActivity().getApplication()).getSkl();

            // Making a request to url and getting response
            url=((Global) getActivity().getApplication()).getUrl()+"Siartdt_Inventur/"+sifraArtikla+"/"+skl+"/";
            String jsonStr = sh.makeServiceCall(url);


            Log.e(TAG, "Response from url: " + jsonStr);


            HashMap<String, String> postavka = new HashMap<>();

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray nalogi = jsonObj.getJSONArray("InventurDnevnik");
                    JSONObject cc = nalogi.getJSONObject(1);

                    naziv = cc.getString("NAZIV");
                    naziv_skl = cc.getString("NAZIV_SKL");
                    naziv_em = cc.getString("NAZIV_EM");
                    zaloga = cc.getString("ZALOGA");


                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(),
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

        }
    }

    private class GetPostavke2 extends AsyncTask<Object, Object, HashMap<String, String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog2 = new ProgressDialog(getActivity());
            pDialog2.setMessage("Please wait...");
            pDialog2.setCancelable(false);
            pDialog2.show();

        }

        @Override
        protected HashMap<String, String> doInBackground(Object... arg0) {
            HttpHandler sh2 = new HttpHandler();
            String skl2=((Global) getActivity().getApplication()).getSkl();

            // Making a request to url and getting response
            url=((Global) getActivity().getApplication()).getUrl()+"Siartdt_Inventur/"+sifraArtikla+"/"+skl2+"/";
            String jsonStr2 = sh2.makeServiceCall(url);


            Log.e(TAG2, "Response from url: " + jsonStr2);


            HashMap<String, String> postavka2 = new HashMap<>();


            if (jsonStr2 != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr2);
                    JSONArray nalogi = jsonObj.getJSONArray("InventurDnevnik");
                    JSONObject cc = nalogi.getJSONObject(1);

                    naziv = cc.getString("NAZIV");
                    naziv_skl = cc.getString("NAZIV_SKL");
                    naziv_em = cc.getString("NAZIV_EM");
                    zaloga = cc.getString("ZALOGA");




                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return postavka2;
        }

        @Override
        protected void onPostExecute(HashMap<String, String> result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog2.isShowing())
                pDialog2.dismiss();

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private class dodajPolje extends AsyncTask<Void, Void, Void> {
        EditText sifra=(EditText)superview.findViewById(R.id.sifra);
        EditText kolicina=(EditText)superview.findViewById(R.id.kolicina);

        String strSifra = sifra.getText().toString();
        String strKolicina = kolicina.getText().toString();

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());


        @Override
        protected Void doInBackground(Void... params) {

            String uporabnik=((Global) getActivity().getApplication()).getUporabnik();

            try {
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
                String URL = ((Global) getActivity().getApplication()).getUrl()+"InventurDnevnik/";
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("SIFART", strSifra);
                jsonBody.put("INVKOLI", strKolicina);
                jsonBody.put("SKL", "10");
                jsonBody.put("VT", "99");
                jsonBody.put("STDOK", "999");
                jsonBody.put("DATUM", formattedDate);
                jsonBody.put("UPORABNIK", uporabnik);
                final String mRequestBody = jsonBody.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("LOG_VOLLEY", response);
                        Toast.makeText(getActivity().getApplicationContext(), "Dodana je bila postavka "+strSifra, Toast.LENGTH_LONG).show();
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

}
