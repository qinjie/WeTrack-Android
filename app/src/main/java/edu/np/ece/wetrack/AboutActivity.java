package edu.np.ece.wetrack;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.webView)
    WebView webView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ButterKnife.bind(this);
        toolbar.setTitle("About");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        webView.loadUrl("file:///android_asset/about.html");

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

//        Bundle c = new Bundle();

        if (detailIntent != null) {
//            Bundle b = detailIntent.getExtras();
            try {
//                if (b != null) {
                String tmp = detailIntent.getStringExtra("fromWhat");
                if (tmp.equals("home")) {
//                        intent.putExtra("isFromDetailActivity", String.valueOf("false"));
//                        c.putString("whatParent", "home");
//                        intent.putExtras(c);
                    intent.putExtra("whatParent", "home");

                }
                if (tmp.equals("detectedList")) {
//                        intent.putExtra("isFromDetailActivity", "true");
//                        c.putString("whatParent", "detectedList");
//                        intent.putExtras(c);
                    intent.putExtra("whatParent", "detectedList");

                }

                if (tmp.equals("relativeList")) {
//                        intent.putExtra("isFromDetailActivity", "true");
//                        c.putString("whatParent", "detectedList");
//                        intent.putExtras(c);
                    intent.putExtra("whatParent", "relativeList");

                }

                startActivity(intent);
//                }
            } catch (Exception e) {
//                intent.putExtra("isFromDetailActivity", "false");
//                c.putString("isFromDetailActivity", "false");
//                intent.putExtras(c);
                intent.putExtra("whatParent", "home");

                startActivity(intent);

            }

        }
        finish();
    }

}
