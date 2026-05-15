package com.catering.patterns.singleton;

public class CateringManager {
    private static CateringManager instance;
    private CateringManager() {}
    public static CateringManager getInstance() {
        if (instance == null) { instance = new CateringManager(); }
        return instance;
    }
}
