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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
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


public class Tab1Inventura extends Fragment {
    public ActionBar getSupportActionBar() {
        return ((AppCompatActivity)getActivity()).getSupportActionBar();
    }

    private String TAG = Tab1Inventura.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;

    private static String url;
    private static String urlSkl;

    ArrayList<HashMap<String, String>> postavkeList;
    ListAdapter adapter;
    EditText fil_skladisce;
    EditText fil_sifra;
    Spinner spinner_skl;
    List<String> vsaSkladisca = new ArrayList<String>();
    String skladisceSifra;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_postavke,container,false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        url = ((Global) getActivity().getApplication()).getUrl();
        urlSkl = ((Global) getActivity().getApplication()).getUrl()+"SiSklad/";

        postavkeList = new ArrayList<>();
        lv = (ListView)view.findViewById(R.id.list);
        fil_skladisce = (EditText) view.findViewById(R.id.fil_skladisce);
        fil_sifra = (EditText) view.findViewById(R.id.fil_sifra);

        spinner_skl= (Spinner) view.findViewById(R.id.spinner_fil);

        fil_skladisce.setText(((Global) getActivity().getApplication()).getFilterSkl());
        fil_sifra.setText(((Global) getActivity().getApplication()).getFilterSif());

        final String sifraFil;
        final String sklFil;
        final EditText sklET = (EditText) view.findViewById(R.id.fil_skladisce);
        final EditText sifraET = (EditText) view.findViewById(R.id.fil_sifra);
        if(sklET.getText().toString().matches("") && sifraET.getText().toString().matches("")){
            url = ((Global) getActivity().getApplication()).getUrl()+"InventurDnevnik/";
        }
        else if(sklET.getText().toString().matches("")){
            sifraFil=sifraET.getText().toString();
            url=((Global) getActivity().getApplication()).getUrl()+"InventurDnevnikFilter/0/"+sifraFil+"/1/";
        }
        else if(sifraET.getText().toString().matches("")){
            sklFil=sklET.getText().toString();
            url=((Global) getActivity().getApplication()).getUrl()+"InventurDnevnikFilter/1/1/"+sklFil+"/";
        }
        else {
            sifraFil=sifraET.getText().toString();
            sklFil=sklET.getText().toString();
            url=((Global) getActivity().getApplication()).getUrl()+"InventurDnevnikFilter/2/"+sifraFil+"/"+sklFil+"/";
        }

        vsaSkladisca.clear();
        spinner_skl.setAdapter(null);
        postavkeList.clear();

        new Tab1Inventura.getInventura().execute();

        int pozicijaSpinner = spinner_skl.getSelectedItemPosition();
        osvezi(view);
        spinner_skl.setSelection(pozicijaSpinner);

        return view;
    }

    public void osvezi(View view){
        final EditText sklET = (EditText) view.findViewById(R.id.fil_skladisce);
        final EditText sifraET = (EditText) view.findViewById(R.id.fil_sifra);
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipelayout);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final String sifraFil;
                //final String sklFil;
                swipeRefreshLayout.setRefreshing(true);

                skladisceSifra = spinner_skl.getSelectedItem().toString();
                skladisceSifra=skladisceSifra.split("\\ ")[0];

                if(skladisceSifra.matches("VSA") && sifraET.getText().toString().matches("")){
                    url = ((Global) getActivity().getApplication()).getUrl()+"InventurDnevnik/";
                }
                else if(skladisceSifra.matches("VSA")){
                    sifraFil=sifraET.getText().toString();
                    url=((Global) getActivity().getApplication()).getUrl()+"InventurDnevnikFilter/0/"+sifraFil+"/1/";
                }
                else if(sifraET.getText().toString().matches("")){
                    //sklFil=sklET.getText().toString();
                    url=((Global) getActivity().getApplication()).getUrl()+"InventurDnevnikFilter/1/1/"+skladisceSifra+"/";
                }
                else {
                    sifraFil=sifraET.getText().toString();
                    //sklFil=sklET.getText().toString();
                    url=((Global) getActivity().getApplication()).getUrl()+"InventurDnevnikFilter/2/"+sifraFil+"/"+skladisceSifra+"/";
                }

                vsaSkladisca.clear();
                spinner_skl.setAdapter(null);
                postavkeList.clear();
                new getInventura().execute();

                swipeRefreshLayout.setRefreshing(false);
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
            final Button btnBrisi= (Button) view.findViewById(R.id.btn_brisi);
            btnBrisi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final TextView tekst = (TextView) ((View) view.getParent()).findViewById(R.id.zst);
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
                            new izbrisiPolje(tekst.getText().toString()).execute();
                        }
                    });
                    builder.show();


                }
            });

            final Button btnUredi= (Button) view.findViewById(R.id.btn_uredi);
            btnUredi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final TextView tekst = (TextView) ((View) view.getParent()).findViewById(R.id.zst);


                    ((Global) getActivity().getApplication()).setKoncnica(tekst.getText().toString());
                    ((Global) getActivity().getApplication()).setFilterSkl(fil_skladisce.getText().toString());
                    ((Global) getActivity().getApplication()).setFilterSif(fil_sifra.getText().toString());
                    Intent toy2 = new Intent(getActivity(), UrediPostavkoActivity.class);
                    startActivity(toy2);

                }
            });


            return view;

        }


        @Override
        public int getCount() {
            return super.getCount();
        }
    }

    private class izbrisiPolje extends AsyncTask<Void, Void, Void> {
        String sifra;

        izbrisiPolje(String sifra) {
            this.sifra = sifra;
        }

        @Override
        protected Void doInBackground(Void... params) {

            RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());

            url = ((Global) getActivity().getApplication()).getUrl()+"InventurDnevnik/"+sifra;

            StringRequest dr = new StringRequest(Request.Method.DELETE, url,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {
                            // response
                            url = ((Global) getActivity().getApplication()).getUrl()+"InventurDnevnik/";
                            vsaSkladisca.clear();
                            spinner_skl.setAdapter(null);
                            postavkeList.clear();
                            new getInventura().execute();
                            Toast.makeText(getActivity().getApplicationContext(), "Izbrisana je bila postavka "+sifra, Toast.LENGTH_LONG).show();
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

    private class getInventura extends AsyncTask<Void, Void, Void> {

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

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);
            //Toast.makeText(getApplicationContext(), jsonStr, Toast.LENGTH_LONG).show();


            Log.e(TAG, "Response from url: " + jsonStr);


            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);


                    JSONArray postavke = jsonObj.getJSONArray("InventurDnevnik");

                    // zanka čez vse postavke
                    for (int i = 1; i < postavke.length(); i++) {
                        JSONObject c = postavke.getJSONObject(i);

                        String ZST = c.getString("ZST");
                        String SIFRA = c.getString("SIFART");
                        String NAZIV = SIFRA + " - " + c.getString("NAZIV");
                        String SKL = c.getString("SKL");
                        String KOL = "Skladišče "+SKL + ":     " + c.getString("INVKOLI")+" "+c.getString("NAZIV_EM");


                        // tmp hash map for single contact
                        HashMap<String, String> postavka = new HashMap<>();

                        postavka.put("ZST", ZST);
                        postavka.put("SIFRA", SIFRA);
                        postavka.put("NAZIV", NAZIV);
                        postavka.put("POZ", SKL);
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
                Log.e(TAG, "Couldn't get json from server.");
                //runOnUiThread(new Runnable() {
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


            sh = new HttpHandler();

            // Making a request to url and getting response
            jsonStr = sh.makeServiceCall(urlSkl);
            //Toast.makeText(getApplicationContext(), jsonStr, Toast.LENGTH_LONG).show();


            Log.e(TAG, "Response from url: " + jsonStr);


            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);


                    JSONArray skladisca = jsonObj.getJSONArray("SiSklad");

                    vsaSkladisca.add("VSA SKLADIŠČA");
                    for (int i = 1; i < skladisca.length(); i++) {

                        JSONObject actor = skladisca.getJSONObject(i);
                        String sifra_skl = actor.getString("SIFRA")+" - "+actor.getString("NAZIV");

                        vsaSkladisca.add(sifra_skl);
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
                Log.e(TAG, "Couldn't get json from server.");
                //runOnUiThread(new Runnable() {
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

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();


            adapter = new Tab1Inventura.CustomAdapter(
                    //PostavkeActivity.this, postavkeList,
                    getActivity(), postavkeList,
                    R.layout.list_item, new String[]{"ZST","NAZIV", "KOL", "SIFRA"}, new int[]{R.id.zst, R.id.naziv, R.id.kolicina, R.id.sifra});


            lv.setAdapter(adapter);

            spinner_skl.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,  vsaSkladisca));

        }
    }
}
