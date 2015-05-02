package com.eds.client.android;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.StreamUtils;
import com.eds.client.MyEdsClient;
import com.eds.client.Platform;
import com.kotcrab.vis.ui.widget.file.FileChooserListener;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

public class AndroidPlatform implements Platform {

    private static final int PICK_IMAGE = 0;

    @Override
    public void chooseImage(FileListener listener) {
        String pathColumn = MediaStore.Images.Media.DATA;
        Intent intent = new Intent(android.content.Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        selectImage(listener, intent);
    }

    private void selectImage(FileListener listener, Intent intent) {

        AndroidLauncher activity = (AndroidLauncher) Gdx.app;
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(intent, PICK_IMAGE,
                    new FileResultListener(listener));
        } else {
            listener.fileChosen(null);
        }
    }


    private static class FileResultListener implements AndroidLauncher.ActivityResultListener {

        private FileListener listener;

        public FileResultListener(FileListener listener) {
            this.listener = listener;
        }

        @Override
        public void result(int resultCode, final Intent data) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Gdx.app.postRunnable(new Runnable() {

                        @Override
                        public void run() {
                            AndroidLauncher activity = (AndroidLauncher) Gdx.app;
                            String path = getPathFromIntent(activity, data);
                            if (path == null) {

                                if (!isConnected()) {
                                    showToast("The file cannot be downloaded because you are not connected to the internet.");
                                } else {
                                    String url = data.getDataString();
                                    try {
                                        Context context = activity
                                                .getContext();
                                        InputStream input = context
                                                .getContentResolver()
                                                .openInputStream(Uri.parse(url));
                                        FileHandle destFile = FileHandle.tempFile("image");
                                        download(input, destFile.write(false));
                                        listener.fileChosen(destFile.file().getAbsolutePath());
                                    } catch (FileNotFoundException e) {
                                        showToast("Image not found.");
                                    }
                                }
                            } else {
                                listener.fileChosen(path);
                            }
                        }
                    });
                } else {
                    showToast("Image not found.");
                }
            } else {
                showToast("Image not found.");
            }
        }

        private void showToast(String message) {
            AndroidLauncher activity = (AndroidLauncher) Gdx.app;
            Toast.makeText(activity.getApplicationContext(), message,
                    Toast.LENGTH_LONG).show();
        }

        private boolean isConnected() {
            Context context = ((AndroidLauncher) Gdx.app).getContext();
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo state = connectivity.getActiveNetworkInfo();

            return state != null && state.isConnected();
        }

        private String getPathFromIntent(Context context, Intent data) {

            Uri uri = data.getData();
            if ("content".equalsIgnoreCase(uri.getScheme())) {

                // Return the remote address
                if (isGooglePhotosUri(uri))
                    return null;

                return getDataColumn(context, uri, null, null);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }

            return null;
        }

        /**
         * @param uri
         *            The Uri to check.
         * @return Whether the Uri authority is Google Photos.
         */
        private boolean isGooglePhotosUri(Uri uri) {
            return "com.google.android.apps.photos.content".equals(uri
                    .getAuthority());
        }

        /**
         * Get the value of the data column for this Uri. This is useful for
         * MediaStore Uris, and other file-based ContentProviders.
         *
         * @param context
         *            The context.
         * @param uri
         *            The Uri to query.
         * @param selection
         *            (Optional) Filter used in the query.
         * @param selectionArgs
         *            (Optional) Selection arguments used in the query.
         * @return The value of the _data column, which is typically a file path.
         */
        private String getDataColumn(Context context, Uri uri, String selection,
                                     String[] selectionArgs) {

            Cursor cursor = null;
            String column = "_data";
            String[] projection = { column };

            try {
                cursor = context.getContentResolver().query(uri, projection,
                        selection, selectionArgs, null);
                if (cursor != null && cursor.moveToFirst()) {

                    int column_index = cursor.getColumnIndexOrThrow(column);
                    return cursor.getString(column_index);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            return null;
        }

        private void download(InputStream input, OutputStream output) {
            byte[] data = new byte[2048];
            while(input != null) {
                try {
                    int count;
                    count = input.read(data);
                    if (count != -1) {
                        output.write(data, 0, count);
                    } else {
                        output.flush();
                        StreamUtils.closeQuietly(output);
                        StreamUtils.closeQuietly(input);
                        input = null;
                    }
                } catch (Exception e) {
                    Gdx.app.error(MyEdsClient.LOG_TAG, "Exception while downloading file.", e);
                    StreamUtils.closeQuietly(output);
                    StreamUtils.closeQuietly(input);
                    input = null;
                }
            }
        }
    }
}
