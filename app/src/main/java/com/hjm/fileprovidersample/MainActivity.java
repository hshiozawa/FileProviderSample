package com.hjm.fileprovidersample;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final ComponentName QUICK_PICK_COMPONENT = new ComponentName("com.alensw.PicFolder", "com.alensw.PicFolder.CropActivity");
    private static final ComponentName LINE_CAMERA_COMPONENT = new ComponentName("jp.naver.linecamera.android", "jp.naver.linecamera.android.activity.EditActivity");
    private static final ComponentName GOOGLE_PHOTO_COMPONENT = new ComponentName("com.google.android.apps.photos", "com.google.android.apps.photos.photoeditor.intents.EditActivity");

    private static final String IMAGE_FILE_NAME = "smartphone.png";
    private static final String IMAGE_MIME_TYPE = "image/png";
    private static final String CONTENT_PROVIDER_AUTHORITY = "com.hjm.fileprovidersample.fileprovider";

    private static final int BUFFER_SIZE = 1024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String internalStorage = getFilesDir().getAbsolutePath();
        ensureImageExistence(internalStorage, IMAGE_FILE_NAME);

        String externalStorage = getExternalFilesDir(null).getAbsolutePath();
        ensureImageExistence(externalStorage, IMAGE_FILE_NAME);

        // Internal Storage + Content URI
        {
            final Uri internalContentUri = getContentUri(internalStorage, IMAGE_FILE_NAME);
            Button line = (Button) findViewById(R.id.internalLine);
            line.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startEdit(internalContentUri, LINE_CAMERA_COMPONENT);
                }
            });

            Button quick = (Button) findViewById(R.id.internalQuick);
            quick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startEdit(internalContentUri, QUICK_PICK_COMPONENT);
                }
            });

            Button photos = (Button) findViewById(R.id.internalPhotos);
            photos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startEdit(internalContentUri, GOOGLE_PHOTO_COMPONENT);
                }
            });
        }

        // External Storage + Content URI
        {
            final Uri externalContentUri = getContentUri(externalStorage, IMAGE_FILE_NAME);
            Button line = (Button) findViewById(R.id.externalLine);
            line.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startEdit(externalContentUri, LINE_CAMERA_COMPONENT);
                }
            });

            Button quick = (Button) findViewById(R.id.externalQuick);
            quick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startEdit(externalContentUri, QUICK_PICK_COMPONENT);
                }
            });

            Button photos = (Button) findViewById(R.id.externalPhotos);
            photos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startEdit(externalContentUri, GOOGLE_PHOTO_COMPONENT);
                }
            });
        }

        // Internal Storage + file URI
        {
            final Uri internalFileUri = Uri.fromFile(new File(internalStorage, IMAGE_FILE_NAME));
            Button line = (Button) findViewById(R.id.internalFileLine);
            line.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startEdit(internalFileUri, LINE_CAMERA_COMPONENT);
                }
            });

            Button quick = (Button) findViewById(R.id.internalFileQuick);
            quick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startEdit(internalFileUri, QUICK_PICK_COMPONENT);
                }
            });

            Button photos = (Button) findViewById(R.id.internalFilePhotos);
            photos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startEdit(internalFileUri, GOOGLE_PHOTO_COMPONENT);
                }
            });
        }

        // External Storage + file URI
        {
            final Uri externalFileUri = Uri.fromFile(new File(externalStorage, IMAGE_FILE_NAME));
            Button line = (Button) findViewById(R.id.externalFileLine);
            line.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startEdit(externalFileUri, LINE_CAMERA_COMPONENT);
                }
            });

            Button quick = (Button) findViewById(R.id.externalFileQuick);
            quick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startEdit(externalFileUri, QUICK_PICK_COMPONENT);
                }
            });

            Button photos = (Button) findViewById(R.id.externalFilePhotos);
            photos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startEdit(externalFileUri, GOOGLE_PHOTO_COMPONENT);
                }
            });
        }
    }

    private void startEdit(Uri uri, @NonNull ComponentName targetApp) {
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setComponent(targetApp);
        intent.setDataAndType(uri, IMAGE_MIME_TYPE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        Log.d(TAG, "start activity: uri=" + uri);
        startActivity(intent);
    }

    private Uri getContentUri(String path, String fileName) {
        File file = new File(path, fileName);
        Log.d(TAG, "target file: " + file.getAbsolutePath());
        return FileProvider.getUriForFile(MainActivity.this, CONTENT_PROVIDER_AUTHORITY, file);
    }

    private void ensureImageExistence(String storagePath, String fileName) {
        try {
            if (!exists(storagePath, fileName)) {
                write(storagePath, fileName, R.raw.smartphone);
            }
        } catch (IOException exception) {
            Log.e(TAG, "Exception: " + exception.getMessage());
        }
    }

    private boolean exists(String path, String fileName) {
        File file = new File(path, fileName);
        boolean ret = file.isFile();
        Log.d(TAG, "check exists. path=" + file.getAbsolutePath() + ",ret=" + ret);
        return ret;
    }

    private void write(@NonNull String path, String fileName, @RawRes int rawId) throws IOException {
        // Creating new file
        File file = new File(path, fileName);

        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = getResources().openRawResource(rawId);

            if (path.equals(getFilesDir().getAbsolutePath())) {
                outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            } else if (path.equals(getExternalFilesDir(null).getAbsolutePath())) {
                outputStream = new FileOutputStream(new File(path, fileName));
            } else {
                throw new IllegalArgumentException("argument path is neither internal storage nor external storage");
            }

            byte[] buffer = new byte[BUFFER_SIZE];
            int length = inputStream.read(buffer);
            while (length > 0) {
                outputStream.write(buffer, 0, length);
                length = inputStream.read(buffer);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }
}
