package edu.np.ece.wetrack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;

/**
 * Created by hoanglong on 17-Nov-16.
 */

public class LoadingActivity extends AppCompatActivity {
    @BindView(R.id.textView3)
    TextView textView;

    @BindView(R.id.ivLoading)
    ImageView imageView;

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Intent intent = new Intent();
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String userID = sharedPref.getString("userID-WeTrack", "");
            if (userID.equals("")) {
                intent.setClass(LoadingActivity.this, LoginActivity.class);

            } else {
                intent.setClass(LoadingActivity.this, MainActivity.class);

            }
            startActivity(intent);
            LoadingActivity.this.finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);


        mHandler.sendEmptyMessageDelayed(1, 2000);
    }
}

