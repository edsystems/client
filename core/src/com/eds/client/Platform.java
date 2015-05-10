package com.eds.client;

public interface Platform {

    void chooseImage(FileListener listener);

    void showImage(String path);

    public interface FileListener {

        void fileChosen(String path);
    }
}
