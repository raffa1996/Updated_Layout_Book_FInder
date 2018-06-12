package com.example.rahulsharma.layout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button Barcode, Ocr;
    private static final int RC_BAR_CAPTURE = 9085;
    private static final int RC_OCR_CAPTURE = 9018;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Barcode = findViewById(R.id.start_barcode);
        Ocr = findViewById(R.id.start_ocr);

        Barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ScannerActivity.class);
                startActivityForResult(intent, RC_BAR_CAPTURE);
            }
        });

        Ocr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OcrCaptureActivity.class);
                startActivityForResult(intent, RC_OCR_CAPTURE);
            }
        });


    }
}
