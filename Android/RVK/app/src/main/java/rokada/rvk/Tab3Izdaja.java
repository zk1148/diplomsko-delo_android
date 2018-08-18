package rokada.rvk;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tab3Izdaja extends Fragment {
    public ActionBar getSupportActionBar() {
        return ((AppCompatActivity)getActivity()).getSupportActionBar();
    }

    private String TAG = Tab1Inventura.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;

    private static String url;


    ArrayList<HashMap<String, String>> postavkeList;
    ListAdapter adapter;
    String vtFil;
    String stevFil;
    String sklFil;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_postavke_izdaja,container,false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        url=((Global) getActivity().getApplication()).getUrl()+"FakBumat/";
        postavkeList = new ArrayList<>();
        lv = (ListView)view.findViewById(R.id.list);


        osvezi(view);

        init(view);

        postavkeList.clear();
        url = ((Global) getActivity().getApplication()).getUrl()+"FakBumat/";
        new Tab3Izdaja.getPostavke().execute();

        return view;
    }


    public void osvezi(View view){
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipelayout);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final String sifraFil;
                //final String sklFil;
                swipeRefreshLayout.setRefreshing(true);

                postavkeList.clear();
                url = ((Global) getActivity().getApplication()).getUrl()+"FakBumat/";
                new Tab3Izdaja.getPostavke().execute();

                swipeRefreshLayout.setRefreshing(false);
            }


        });
    }

    public void osvezi_klic(){
        final String sifraFil;

        postavkeList.clear();
        url = ((Global) getActivity().getApplication()).getUrl()+"FakBumat/";
        new Tab3Izdaja.getPostavke().execute();
    }

    public void init(View view){

        Button btn_zakljuci = (Button) view.findViewById(R.id.btn_zakljuci);
        btn_zakljuci.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent toy = new Intent(getActivity(), IzbiraDelovnegaNaloga.class);
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
            final View view = super.getView(position, convertView, parent);
            final Button btnBrisi = (Button) view.findViewById(R.id.btn_brisi);
            btnBrisi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final TextView tekst = (TextView) ((View) view.getParent()).findViewById(R.id.artikel);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    builder.setCancelable(true);
                    builder.setTitle("OPOZORILO");
                    builder.setMessage("Ste prepričani, da želite izbrisati postavko "+tekst.getText().toString()+"?");

                    builder.setNegativeButton("PREKLIČI", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });

                    builder.setPositiveButton("DA", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String artikel=tekst.getText().toString().split("\\ ")[0];
                            new Tab3Izdaja.izbrisiPolje(artikel).execute();

                        }
                    });
                    builder.show();
                }
            });

            final Button btnUredi = (Button) view.findViewById(R.id.btn_uredi);
            btnUredi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final TextView tekst = (TextView) ((View) view.getParent()).findViewById(R.id.artikel);


                    //zapišemo artikel
                    ((Global) getActivity().getApplication()).setKoncnica(tekst.getText().toString().split("\\ ")[0]);
                    Intent toy2 = new Intent(getActivity(), UrediPostavkoIzdajaActivity.class);
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
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            vtFil=((Global) getActivity().getApplication()).getVrstaDokumenta();
            stevFil=((Global) getActivity().getApplication()).getStevFN();
            sklFil=((Global) getActivity().getApplication()).getSkl();

            url=url+vtFil+"/"+stevFil+"/"+sklFil;
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);
            //Toast.makeText(getApplicationContext(), jsonStr, Toast.LENGTH_LONG).show();


            Log.e(TAG, "Response from url: " + jsonStr);


            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);


                    JSONArray postavke = jsonObj.getJSONArray("FakBumat");

                    // zanka čez vse postavke
                    for (int i = 1; i < postavke.length(); i++) {
                        JSONObject c = postavke.getJSONObject(i);

                        HashMap<String, String> postavka = new HashMap<>();

                        String ARTIKEL = c.getString("ARTI")+" - "+c.getString("NAZIV");
                        String KOL = "Količina: " + c.getString("KOLICINA") + " " + c.getString("NAZIV1");


                        postavka.put("ARTIKEL", ARTIKEL);
                        postavka.put("KOL", KOL);

                        // adding contact to contact list
                        postavkeList.add(postavka);
                    }


                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    //runOnUiThread(new Runnable() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });

                }
            } else {
                /*Log.e(TAG, "Couldn't get json from server.");
                //runOnUiThread(new Runnable() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });*/

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();


            adapter = new Tab3Izdaja.CustomAdapter(
                    //PostavkeActivity.this, postavkeList,
                    getActivity(), postavkeList,
                    R.layout.list_postavka_izdaja, new String[]{"ARTIKEL", "KOL"}, new int[]{R.id.artikel, R.id.kolicina});


            lv.setAdapter(adapter);


        }
    }

    private class izbrisiPolje extends AsyncTask<Void, Void, Void> {
        String vt;
        String stev;
        String skl;
        String artikel;


        izbrisiPolje(String artikel) {
            this.artikel = artikel;
        }

        @Override
        protected Void doInBackground(Void... params) {

            RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());

            vt=((Global) getActivity().getApplication()).getVrstaDokumenta();
            stev=((Global) getActivity().getApplication()).getStevFN();
            skl=((Global) getActivity().getApplication()).getSkl();

            url = ((Global) getActivity().getApplication()).getUrl()+"FakBumat/"+vt+"/"+stev+"/"+skl+"/"+artikel+"/";

            StringRequest dr = new StringRequest(Request.Method.DELETE, url,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {
                            // response
                            url = ((Global) getActivity().getApplication()).getUrl()+"FakBumat/";
                            postavkeList.clear();
                            new Tab3Izdaja.getPostavke().execute();
                            Toast.makeText(getActivity().getApplicationContext(), "Izbrisan je bil artikel "+artikel, Toast.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error.

                        }
                    }
            );
            queue.add(dr);

            return null;
        }
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }
    }
}
