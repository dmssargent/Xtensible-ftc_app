package org.ftccommunity.bindings;

import android.view.View;

import java.util.HashMap;

public class DataBinder {
    private static DataBinder instance;
    private static HashMap<String, Integer> integerBindings;
    private static HashMap<String, View> viewBindings;
    private static HashMap<String, String> stringBindings;
    private static HashMap<String, Object> objectBindings;

    public DataBinder() {
        integerBindings = new HashMap<>();
        viewBindings = new HashMap<>();
        stringBindings = new HashMap<>();
        objectBindings = new HashMap<>();

        instance = this;
    }

    public static DataBinder getInstance() {
        if (instance == null) {
            instance = new DataBinder();
        }

        return instance;
    }

    public HashMap<String, Integer> getIntegers() {
        return integerBindings;
    }

    public HashMap<String, View> getViews() {
        return viewBindings;
    }

    public HashMap<String, String> getStrings() {
        return stringBindings;
    }

    public HashMap<String, Object> getObjects() {
        return objectBindings;
    }


}
