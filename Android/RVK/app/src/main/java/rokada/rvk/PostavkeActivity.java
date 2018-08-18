package rokada.rvk;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostavkeActivity extends AppCompatActivity {


    public ActionBar getSupportActionBar() {
        return super.getSupportActionBar();
    }

    private String TAG = PostavkeActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;

    private static String url;

    ArrayList<HashMap<String, String>> postavkeList;
    ListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postavke);

        url=((Global) getApplication()).getUrl()+"InventurDnevnik/";
        postavkeList = new ArrayList<>();
        setTitle("INVENTURA - Postavke");

        lv = (ListView) findViewById(R.id.list);


        url = ((Global) getApplication()).getUrl()+"InventurDnevnik/";
        postavkeList.clear();

        new getInventura().execute();
    }

    public class CustomAdapter extends SimpleAdapter {
        public CustomAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);

        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final View view=super.getView(position, convertView, parent);
            /*final Button button= (Button) view.findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView tekst = (TextView) ((View) view.getParent()).findViewById(R.id.DELNALOG);

                    new izbrisiPolje(tekst.getText().toString()).execute();
                    //Toast.makeText(getApplicationContext(), delnal, Toast.LENGTH_LONG).show();

                }
            });

            final Button btnUredi= (Button) view.findViewById(R.id.button_uredi);
            btnUredi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent toy2 = new Intent(MainActivity.this, UrediActivity.class);
                    startActivity(toy2);

                }
            });*/


            return view;

        }


        @Override
        public int getCount() {
            return super.getCount();
        }
    }

    private class getInventura extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(PostavkeActivity.this);
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

                        String SIFRA = c.getString("SIFART");
                        String NAZIV = SIFRA + " - " + c.getString("NAZIV");
                        String SKL = c.getString("SKL");
                        String KOL = "Skladišče "+SKL + ":     " + c.getString("INVKOLI");


                        // tmp hash map for single contact
                        HashMap<String, String> postavka = new HashMap<>();

                        postavka.put("SIFRA", SIFRA);
                        postavka.put("NAZIV", NAZIV);
                        postavka.put("POZ", SKL);
                        postavka.put("KOL", KOL);

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


            adapter = new CustomAdapter(
                    PostavkeActivity.this, postavkeList,
                    R.layout.list_item, new String[]{"NAZIV", "KOL"}, new int[]{R.id.naziv, R.id.kolicina});


            lv.setAdapter(adapter);

        }
    }
}
