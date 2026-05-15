package com.housemaid.activities.fragments;


import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.housemaid.R;
import com.housemaid.databinding.FragmentEarnCreditBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class EarnCreditFragment extends Fragment {

    FragmentEarnCreditBinding binding;


    public EarnCreditFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_earn_credit, container, false);
        View view = binding.getRoot();

        return view;
    }
}