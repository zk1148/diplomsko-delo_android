package rokada.rvk;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VnosSkladiscaActivity extends AppCompatActivity {
    Button btnNadaljuj;
    private ProgressDialog pDialog;
    private String TAG = VnosSkladiscaActivity.class.getSimpleName();
    String skladisceSifra;

    List<String> vsaSkladisca = new ArrayList<String>();
    Spinner spinner_skl;

    private static String url;

    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vnosskladisca);
        setTitle("INVENTURA");
        url=((Global) getApplication()).getUrl()+"SiSklad/";
        spinner_skl= (Spinner) findViewById(R.id.spinner_skl);

        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        String address = info.getMacAddress();

        TextView mac = (TextView) findViewById(R.id.mac);
        //mac.setText(address);
        mac.setText("");
        ((Global) getApplication()).setUporabnik(address);

        new GetSkladisca().execute();
        init();
    }

    public void onBackPressed()
    {
        Intent intent = new Intent(VnosSkladiscaActivity.this, IzborActivity.class);
        startActivity(intent);
    }

    public void init(){
        btnNadaljuj = (Button) findViewById(R.id.btn_nadaljuj);
        btnNadaljuj.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                skladisceSifra = spinner_skl.getSelectedItem().toString();
                skladisceSifra=skladisceSifra.split("\\ ")[0];
                ((Global) getApplication()).setTabPozicija(0);
                ((Global) getApplication()).setSkl(skladisceSifra);
                Intent toy = new Intent(VnosSkladiscaActivity.this, InventuraActivity.class);
                startActivity(toy);
            }
        });

    }

    private class GetSkladisca extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(VnosSkladiscaActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

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

            spinner_skl.setAdapter(new ArrayAdapter<String>(VnosSkladiscaActivity.this,android.R.layout.simple_spinner_dropdown_item,  vsaSkladisca));

        }
    }
}
