package edu.np.ece.wetrack;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutUsActivity extends AppCompatActivity {

    @BindView(R.id.credit)
    RelativeLayout credit;

    @BindView(R.id.creditArea)
    TextView creditArea;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.mScrollView)
    NestedScrollView scrollView;

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

//        scrollView.setFocusable(false);
//        scrollView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                return true;
//            }
//        });
////        setOverScrollMode(View.OVER_SCROLL_NEVER);
//        credit.setVisibility(View.GONE);

//        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/homestead.TTF");
//        creditArea.setTypeface(custom_font);

//        Animation fadeOut = new AlphaAnimation(1, 0);
//        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
//        fadeOut.setStartOffset(0);
//        fadeOut.setDuration(3000);
//        creditArea.startAnimation(fadeOut);


//        mHandler = new Handler();
//
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                creditArea.setVisibility(View.GONE);
//                credit.setVisibility(View.VISIBLE);
//                animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.credit);
//                startRepeatingTask();
//
//            }
//        }, 3000);


    }


    @Override
    protected void onResume() {
        super.onResume();

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


//    public void didTapButton(View view) {

//        Animation fadeOut = new AlphaAnimation(1, 0);
//        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
//        fadeOut.setStartOffset(0);
//        fadeOut.setDuration(1000);
//
//        creditArea.startAnimation(fadeOut);
//        button.startAnimation(fadeOut);
//        creditArea.setVisibility(View.GONE);
//        button.setVisibility(View.GONE);


//        credit.setVisibility(View.VISIBLE);
//        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.credit);
//
//        mHandler = new Handler();
//        startRepeatingTask();



//    }

//    void startRepeatingTask() {
//        mStatusChecker.run();
//    }
//
//    private int mInterval = 20000;
//    Runnable mStatusChecker = new Runnable() {
//        @Override
//        public void run() {
//            try {
//                credit.startAnimation(animation);
//                mHandler.postDelayed(mStatusChecker, mInterval);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//
//        }
//    };
}
