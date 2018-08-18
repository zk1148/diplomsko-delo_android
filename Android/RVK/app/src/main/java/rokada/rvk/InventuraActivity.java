package rokada.rvk;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class InventuraActivity extends AppCompatActivity {

    private static final String TAG = "InventuraActivity";

    private SectionsPageAdapter mSectionsPageAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventura);
        Log.d(TAG, "onCreate: Starting.");

        setTitle("INVENTURA");

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        int tabPozicija = ((Global) this.getApplication()).getTabPozicija();
        mViewPager.setCurrentItem(tabPozicija);

    }

    public void onBackPressed()
    {
        Intent intent = new Intent(InventuraActivity.this, VnosSkladiscaActivity.class);
        startActivity(intent);
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new Tab2Inventura(), "VNOS");
        adapter.addFragment(new Tab1Inventura(), "PREGLED");
        viewPager.setAdapter(adapter);
    }

}
