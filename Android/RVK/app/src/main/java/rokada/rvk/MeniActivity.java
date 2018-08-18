package rokada.rvk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MeniActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meni);

        setTitle("Meni");
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        init();
    }

    public void init(){
        Button btnVstop = (Button) findViewById(R.id.btn_vstop);
        btnVstop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ((Global) getApplication()).setUrl("http://10.1.1.22/api/");

                //((Global) getApplication()).setUrl("http://192.168.0.26:8080/api/");
                Intent toy = new Intent(MeniActivity.this, IzborActivity.class);
                startActivity(toy);
            }
        });

        Button btnProdukcijska = (Button) findViewById(R.id.btn_produkcijska);
        btnProdukcijska.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((Global) getApplication()).setUrl("http://192.168.0.26:8000/api/");
                Intent toy = new Intent(MeniActivity.this, IzborActivity.class);
                startActivity(toy);
            }
        });


    }
}
