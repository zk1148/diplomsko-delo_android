package rokada.rvk;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class IzdajaActivity extends AppCompatActivity {

    private static final String TAG = "IzdajaActivity";

    private SectionsPageAdapter mSectionsPageAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_izdaja);
        Log.d(TAG, "onCreate: Starting.");

        setTitle("Izdaja: "+((Global) this.getApplication()).getSifrDelNaloga());
        //setTitle("SKL:"+((Global) getApplication()).getSkl()+"/VT:"+((Global) getApplication()).getVrstaDokumenta()+"STEV:"+((Global) getApplication()).getVrstaDokumenta()+"DN:"+((Global) getApplication()).getSifrDelNaloga());

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        int tabPozicija = ((Global) this.getApplication()).getTabPozicija();
        mViewPager.setCurrentItem(1);



    }

    public void onBackPressed()
    {
        Intent intent = new Intent(IzdajaActivity.this, IzbiraDelovnegaNaloga.class);
        startActivity(intent);
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new Tab1Izdaja(), "GLAVA");
        adapter.addFragment(new Tab2Izdaja(), "ARTIKEL");
        adapter.addFragment(new Tab3Izdaja(), "POSTAVKE");
        viewPager.setAdapter(adapter);
    }

}
