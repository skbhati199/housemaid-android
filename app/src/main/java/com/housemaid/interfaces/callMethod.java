package com.housemaid.interfaces;

/**
 * Created by fluper on 23/6/18.
 */

public interface callMethod {
        void makeFavourite(int key,int jobListId);
        void makeUnfavourite(int key,int jobListId, int position);
        void removeMaid(int position);
}

