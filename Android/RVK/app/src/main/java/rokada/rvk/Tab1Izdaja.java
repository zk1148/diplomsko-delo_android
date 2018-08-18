package rokada.rvk;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class Tab1Izdaja extends Fragment {

    private ProgressDialog pDialog;
    private String url;
    private String sifrDelNaloga;
    private String TAG = Tab1Izdaja.class.getSimpleName();

    String delnalog;
    String kupec;
    View superview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.activity_glava_izdaja,container,false);
        superview=view;
        url = ((Global) getActivity().getApplication()).getUrl()+"GlavaIzdaja/";

        new Tab1Izdaja.GetGlavaIzdaja().execute();

        TextView dnTV = (TextView) view.findViewById(R.id.delnalog);
        dnTV.setText(((Global) getActivity().getApplication()).getSifrDelNaloga());

        TextView sklTV = (TextView) view.findViewById(R.id.skl);
        sklTV.setText(((Global) getActivity().getApplication()).getSklNaziv());

        TextView stevTV = (TextView) view.findViewById(R.id.stev);
        stevTV.setText(((Global) getActivity().getApplication()).getStevFN());

        TextView vtTV = (TextView) view.findViewById(R.id.vt);
        vtTV.setText(((Global) getActivity().getApplication()).getVtNaziv());

        SimpleDateFormat sdf = new SimpleDateFormat("dd. MM. yyyy");
        String currentDateandTime = sdf.format(new Date());
        TextView datumTV = (TextView) view.findViewById(R.id.datum);
        datumTV.setText(currentDateandTime);

        return view;

    }


    private class GetGlavaIzdaja extends AsyncTask<Object, Object, HashMap<String, String>> {

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
            String sifrDelNaloga=((Global) getActivity().getApplication()).getSifrDelNaloga();

            // Making a request to url and getting response
            url=((Global) getActivity().getApplication()).getUrl()+"GlavaIzdaja/"+sifrDelNaloga;
            String jsonStr = sh.makeServiceCall(url);


            Log.e(TAG, "Response from url: " + jsonStr);


            HashMap<String, String> postavka = new HashMap<>();

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray nalogi = jsonObj.getJSONArray("GlavaIzdaje");
                    JSONObject cc = nalogi.getJSONObject(1);

                    delnalog = cc.getString("stevilka") + " - " + cc.getString("opis");
                    kupec = cc.getString("kupec") + " - " + cc.getString("NAZIV1");


                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });

                }
            } else {
                /*Log.e(TAG, "Couldn't get json from server.");
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

            return postavka;
        }

        @Override
        protected void onPostExecute(HashMap<String, String> result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            TextView kupecTV = (TextView) superview.findViewById(R.id.kupec);
            kupecTV.setText(kupec);

            TextView delnalTV = (TextView) superview.findViewById(R.id.delnalog);
            delnalTV.setText(delnalog);


        }
    }


}
