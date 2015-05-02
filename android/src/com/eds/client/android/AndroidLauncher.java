package com.eds.client.android;

import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.utils.ObjectMap;
import com.eds.client.MyEdsClient;
import com.eds.client.Platform;

public class AndroidLauncher extends AndroidApplication {

    private ObjectMap<Integer, ActivityResultListener> listeners;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        this.listeners = new ObjectMap<Integer, ActivityResultListener>();
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = false;
        config.useImmersiveMode = false;
        config.hideStatusBar = true;
        config.hideStatusBar = true;
        config.disableAudio = true;
        config.useWakelock = true;
        config.useCompass = false;
		initialize(new MyEdsClient(new AndroidPlatform()), config);
	}

    public void startActivityForResult(Intent intent, int requestCode,
                                       ActivityResultListener l) {
        this.listeners.put(requestCode, l);
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ActivityResultListener listener = this.listeners.get(requestCode);
        if (listener != null) {
            listener.result(resultCode, data);
        }
    }

    public interface ActivityResultListener {

        void result(int resultCode, Intent data);

    }
}
