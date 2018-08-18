package rokada.rvk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class IzborActivity extends Activity {

    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_izbor);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTitle("Meni");

        lv = (ListView) findViewById(R.id.list);

        // Instanciating an array list (you don't need to do this,
        // you already have yours).
        List<String> your_array_list = new ArrayList<String>();
        your_array_list.add("INVENTURA                       ");
        your_array_list.add("IZDAJA NA DELOVNI NALOG       ");
        your_array_list.add("POVRATNICA NA DELOVNI NALOG");
        your_array_list.add("PREVZEMI                        ");

        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                R.layout.activity_menu_listview,
                your_array_list );

        lv.setAdapter(arrayAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (position==0){
                    ((Global) getApplication()).setTabPozicija(0);
                    Intent toy = new Intent(IzborActivity.this, VnosSkladiscaActivity.class);
                    startActivity(toy);
                }

                if (position==1){
                    Intent toy = new Intent(IzborActivity.this, IzbiraDelovnegaNaloga.class);
                    startActivity(toy);
                }

                if (position==2){
                    Intent toy = new Intent(IzborActivity.this, IzbiraDelovnegaNalogaPovratnice.class);
                    startActivity(toy);
                }
                if (position==3){
                    Intent toy = new Intent(IzborActivity.this, IzbiraNarocilnice.class);
                    startActivity(toy);
                }

            }
        });
    }

    public void onBackPressed()
    {
        Intent intent = new Intent(IzborActivity.this, MeniActivity.class);
        startActivity(intent);
    }
}
