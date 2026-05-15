package com.housemaid.activities.maid.fromHome.fragment;


import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.housemaid.R;
import com.housemaid.databinding.FragmentMaidHomeBinding;


/**
 * A simple {@link Fragment} subclass.
 */
public class MaidHomeFragment extends Fragment {

    FragmentMaidHomeBinding binding;

    public MaidHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_maid_home,
                container, false);
        View view = binding.getRoot();

        return view;
    }

}
