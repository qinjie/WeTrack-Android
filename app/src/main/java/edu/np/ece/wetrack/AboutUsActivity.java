package edu.np.ece.wetrack;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.np.ece.wetrack.tasks.MyBounceInterpolator;

public class AboutUsActivity extends AppCompatActivity {

    @BindView(R.id.credit)
    RelativeLayout credit;

    @BindView(R.id.creditArea)
    ImageView creditArea;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.button)
    Button button;


//    @BindView(R.id.webView)
//    WebView webView;

    Animation animation;

    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        toolbar.setTitle("About us");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        credit.setVisibility(View.GONE);

        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);

        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        button.startAnimation(myAnim);

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


    public void didTapButton(View view) {

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setStartOffset(0);
        fadeOut.setDuration(1000);

        creditArea.startAnimation(fadeOut);
        button.startAnimation(fadeOut);
        creditArea.setVisibility(View.GONE);
        button.setVisibility(View.GONE);


        credit.setVisibility(View.VISIBLE);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.credit);

        mHandler = new Handler();
        startRepeatingTask();


//        button.setText("Product Manager \n Zhang \"Mark\" Qinjie \nDevelopers \n Long Pham \n long.phamlp94@gmail.com \n Hoa Nguyen \n phuonghoatink22@gmail.com \n");
//        button.setTextSize(16);


    }

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    private int mInterval = 18000;
    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {

            credit.startAnimation(animation);

            mHandler.postDelayed(mStatusChecker, mInterval);

        }
    };
}
