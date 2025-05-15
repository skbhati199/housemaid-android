package com.housemaid.utils;

import com.housemaid.adapter.CountryAdapter;
import com.housemaid.model.CountryListModel;

import java.util.ArrayList;

public class FilterListUtils {
    public static void filter(String newText, CountryAdapter adapter, ArrayList<CountryListModel> list) {
        newText = newText.toLowerCase();
        ArrayList<CountryListModel> newList = new ArrayList<>();
        for (CountryListModel itemList : list) {

            String countryName = itemList.getName().toLowerCase();
            if (countryName.contains(newText))
                newList.add(itemList);
        }
        adapter.setfilter(newList);

    }
}
