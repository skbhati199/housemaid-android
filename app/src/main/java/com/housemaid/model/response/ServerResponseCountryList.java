package com.housemaid.model.response;

import com.housemaid.model.CountryListModel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by fluper on 15/5/18.
 */

public class ServerResponseCountryList {
    @SerializedName("response")
    public ArrayList<CountryListModel> countryListModels;
}
