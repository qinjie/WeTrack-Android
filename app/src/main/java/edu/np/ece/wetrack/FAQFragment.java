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
//
//    @BindView(R.id.expandableLayout6)
//    ExpandableRelativeLayout expandableLayout6;
//
//    @BindView(R.id.expandableLayout7)
//    ExpandableRelativeLayout expandableLayout7;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment_faq, container, false);
        View rootView = inflater.inflate(R.layout.fragment_faq_expand, container, false);
        ButterKnife.bind(this, rootView);

//        webView.loadUrl("file:///android_asset/about.html");
//        jtv.setText(getResources().getString(R.string.FAQ));

        expandableLayout1.toggle();
        expandableLayout2.toggle();
        expandableLayout3.toggle();
        expandableLayout4.toggle();


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

//    @OnClick(R.id.expandableButton5)
//    public void onClick5() {
//        expandableLayout5.toggle();
//
//    }
//
//    @OnClick(R.id.expandableButton6)
//    public void onClick6() {
//        expandableLayout6.toggle();
//
//    }
//    @OnClick(R.id.expandableButton7)
//    public void onClick7() {
//        expandableLayout7.toggle();
//
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
