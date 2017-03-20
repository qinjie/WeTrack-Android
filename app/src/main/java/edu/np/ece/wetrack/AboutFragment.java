package edu.np.ece.wetrack;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;


import butterknife.BindView;
import butterknife.ButterKnife;
import edu.np.ece.wetrack.R;


/**
 * Created by hoanglong on 06-Dec-16.
 */

public class AboutFragment extends Fragment {

    public static AboutFragment newInstance(String title) {
        Bundle args = new Bundle();
        args.putString("title", title);
        AboutFragment fragment = new AboutFragment();
        fragment.setArguments(args);
        return fragment;
    }

//    @BindView(R.id.text)
//    JustifyTextView jtv;

    @BindView(R.id.webView)
    WebView webView;

//    @BindView(R.id.webView)
//    TextView webView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        View rootView = inflater.inflate(R.layout.fragment_about_webview, container, false);

        ButterKnife.bind(this, rootView);

//        webView.setVerticalScrollBarEnabled(true);
//        webView.setHorizontalScrollBarEnabled(true);

//        jtv.setText(getResources().getString(R.string.about));

        webView.loadUrl("file:///android_assets/about.html");

//        webView.setText(Html.fromHtml("file:///android_asset/about.html\n"));

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
