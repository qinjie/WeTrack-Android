package edu.np.ece.wetrack;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutUsActivity extends AppCompatActivity {

//    @BindView(R.id.credit)
//    TextView credit;

    @BindView(R.id.creditArea)
    ImageView creditArea;

    @BindView(R.id.toolbar)
    Toolbar toolbar;


//    @BindView(R.id.webView)
//    WebView webView;

    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        toolbar.setTitle("About us");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


//        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.credit);
//        credit.setText("Long pham - student\n" + "Long pham - student\n" + "Long pham - student\n" + "Long pham - student\n" + "Long pham - student\n" + "Long pham - student\n" + "Long pham - student\n"+"Long pham - student\n" + "Long pham - student\n" + "Long pham - student\n" + "Long pham - student\n" + "Long pham - student\n" + "Long pham - student\n" + "Long pham - student\n"+"Long pham - student\n" + "Long pham - student\n" + "Long pham - student\n" + "Long pham - student\n" + "Long pham - student\n" + "Long pham - student\n" + "Long pham - student\n"+"Long pham - student\n" + "Long pham - student\n" + "Long pham - student\n" + "Long pham - student\n" + "Long pham - student\n" + "Long pham - student\n" + "Long pham - student\n");

//        webView.loadUrl("file:///android_asset/about.html");

//        creditArea.startAnimation(animation);
    }


    @Override
    protected void onResume() {
        super.onResume();
//        creditArea.startAnimation(animation);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        Intent detailIntent = getIntent();

        Intent intent = new Intent(this, MainActivity.class);

        if (detailIntent != null) {
            try {
                String tmp = detailIntent.getStringExtra("fromWhat");
                if (tmp.equals("home")) {
                    intent.putExtra("whatParent", "home");
                }

                if (tmp.equals("detectedList")) {
                    intent.putExtra("whatParent", "detectedList");
                }

                if (tmp.equals("relativeList")) {
                    intent.putExtra("whatParent", "relativeList");
                }

                startActivity(intent);

            } catch (Exception e) {
                intent.putExtra("whatParent", "home");
                startActivity(intent);
            }

        }
        finish();
    }
}
