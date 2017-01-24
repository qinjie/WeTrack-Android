package com.example.hoanglong.wetrack;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.biubiubiu.justifytext.library.JustifyTextView;

/**
 * Created by hoanglong on 20-Jan-17.
 */

public class FAQFragment  extends Fragment {

    public static FAQFragment newInstance(String title) {
        Bundle args = new Bundle();
        args.putString("title", title);
        FAQFragment fragment = new FAQFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.text)
    JustifyTextView jtv;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.bind(this, rootView);
        jtv.setText(getResources().getString(R.string.FAQ));

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
