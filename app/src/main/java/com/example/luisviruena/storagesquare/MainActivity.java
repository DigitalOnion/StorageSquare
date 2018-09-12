package com.example.luisviruena.storagesquare;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.CharBuffer;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    public static final int READ_REQUEST_CODE = 3;

    private File selectedFile = null;
    private Uri uri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickTakePicture(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    public void onClickProcessPicture(View view) {
        if(selectedFile == null) {
            Toast.makeText(this, R.string.please_select_file, Toast.LENGTH_LONG).show();
            return;
        }

        try {
            ParcelFileDescriptor descriptor =
                    getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = descriptor.getFileDescriptor();
            FileInputStream stream = new FileInputStream(fileDescriptor);

            LinkedList<byte[]> listByteArray = new LinkedList<>();
            int total = 0;
            int available = stream.available();
            while(available > 0) {
                Log.d("LUIS", "available = " + available);
                byte[] readBytes = new byte[available];
                stream.read(readBytes);
                listByteArray.add(readBytes);
                total += readBytes.length;
                available = stream.available();
            }

            StringBuilder sb = new StringBuilder();
            for(byte[] bytes : listByteArray) {
                for(byte b: bytes) {
                    sb.append( (b >= 0 && b<=32) ? '.' : (char)b );
                }
            }
            TextView textProcessed = findViewById(R.id.processed_file);
            textProcessed.setText(sb.toString());

            /* this does not work...
            String pathDcim;
            pathDcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera";
            File dcimDirectory = new File(pathDcim);
            File[] allFiles = dcimDirectory.listFiles();
            for(File file : allFiles) {
                Log.d("LUIS", "file:" + file.toString());
            }
            */

            /* This does not work. The selectedFile seems to be useless
            byte[] fileBytes = new byte[(int) selectedFile.length()];
            FileInputStream stream = new FileInputStream(selectedFile.getPath());
            stream.read(fileBytes);
            String message = new String(fileBytes);
            TextView textProcessed = findViewById(R.id.processed_file);
            textProcessed.setText(message);
            */
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent dataIntent) {
        if(requestCode == READ_REQUEST_CODE && resultCode == RESULT_OK) {
            uri = null;
            if(dataIntent != null) {
                uri = dataIntent.getData();
                selectedFile = new File(uri.getPath());     // THIS FILE CANNOT BE OPENED...
            }
        }
    }
}
