package edu.np.ece.wetrack;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by hoanglong on 20-Jan-17.
 */

public class FAQFragment extends Fragment {

    public static FAQFragment newInstance(String title) {
        Bundle args = new Bundle();
        args.putString("title", title);
        FAQFragment fragment = new FAQFragment();
        fragment.setArguments(args);
        return fragment;
    }

//    @BindView(R.id.text)
//    JustifyTextView jtv;

//    @BindView(R.id.webView)
//    WebView webView;

    @BindView(R.id.expandableLayout1)
    ExpandableRelativeLayout expandableLayout1;

    @BindView(R.id.expandableLayout2)
    ExpandableRelativeLayout expandableLayout2;

    @BindView(R.id.expandableLayout3)
    ExpandableRelativeLayout expandableLayout3;

    @BindView(R.id.expandableLayout4)
    ExpandableRelativeLayout expandableLayout4;

//    @BindView(R.id.expandableLayout5)
//    ExpandableRelativeLayout expandableLayout5;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment_faq, container, false);
        View rootView = inflater.inflate(R.layout.fragment_faq_expand, container, false);
        ButterKnife.bind(this, rootView);

//        webView.loadUrl("file:///android_asset/about.html");
//        jtv.setText(getResources().getString(R.string.FAQ));

        expandableLayout1.expand();
        expandableLayout2.collapse();
        expandableLayout3.collapse();
        expandableLayout4.collapse();


        return rootView;
    }

    @OnClick(R.id.expandableButton1)
    public void onClick1() {
        expandableLayout1.toggle();

    }

    @OnClick(R.id.expandableButton2)
    public void onClick2() {
        expandableLayout2.toggle();

    }

    @OnClick(R.id.expandableButton3)
    public void onClick3() {
        expandableLayout3.toggle();

    }

    @OnClick(R.id.expandableButton4)
    public void onClick4() {
        expandableLayout4.toggle();

    }
//    public void expandableButton1(View view) {
////        expandableLayout1 = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout1);
//         // toggle expand and collapse
//    }
//
//    public void expandableButton2(View view) {
////        expandableLayout2 = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout2);
//        expandableLayout2.toggle(); // toggle expand and collapse
//    }
//
//    public void expandableButton3(View view) {
////        expandableLayout3 = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout3);
//        expandableLayout3.toggle(); // toggle expand and collapse
//    }
//
//    public void expandableButton4(View view) {
////        expandableLayout4 = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout4);
//        expandableLayout4.toggle(); // toggle expand and collapse
//    }

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
