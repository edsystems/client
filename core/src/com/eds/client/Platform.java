package com.eds.client;

public interface Platform {

    void chooseImage(FileListener listener);

    public interface FileListener {

        void fileChosen(String path);

    }
}
