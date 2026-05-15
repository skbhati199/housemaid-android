package com.housemaid.activities.fragments;


import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.housemaid.R;
import com.housemaid.databinding.FragmentUserHomeBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserHomeFragment extends Fragment {

    FragmentUserHomeBinding binding;


    public UserHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_user_home,
                container, false);
        View view = binding.getRoot();

        return view;
    }
}