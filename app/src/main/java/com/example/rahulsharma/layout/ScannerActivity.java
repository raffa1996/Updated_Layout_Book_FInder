package com.example.rahulsharma.layout;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;

import info.androidhive.barcode.BarcodeReader;


public class ScannerActivity extends AppCompatActivity implements BarcodeReader.BarcodeReaderListener {

    private static final String LOG_TAG = ScannerActivity.class.getSimpleName();
    private BarcodeReader mBarcodeReader;
    public static final String BarcodeObject = "String";
    private boolean isPaused = false;
    private static final int RC_BAR_CAPTURE = 9005;
    private boolean flashLightStatus = false;
    private static final int DISMISS_BARCODE = 9000;
    ImageView flash;
    private static final int CAMERA_REQUEST = 50;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);


        mBarcodeReader = (BarcodeReader) getSupportFragmentManager().findFragmentById(
                R.id.barcode_fragment);

        flash = findViewById(R.id.flash);


        final boolean hasCameraFlash = getPackageManager().
                hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        boolean isEnabled = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;

        flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasCameraFlash) {
                    if (flashLightStatus)
                        flashLightOff();
                    else
                        flashLightOn();
                } else {
                    Toast.makeText(ScannerActivity.this, "No flash available on your device",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void flashLightOn() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, true);
            flashLightStatus = true;
            flash.setImageResource(R.drawable.flash_on);
        } catch (CameraAccessException e) {
        }
    }

    private void flashLightOff() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, false);
            flashLightStatus = false;
            flash.setImageResource(R.drawable.flash_off);
        } catch (CameraAccessException e) {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    flash.setEnabled(true);
                } else {
                    Toast.makeText(ScannerActivity.this, "Permission Denied for the Camera", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    /*@Override
    public void onScanned(Barcode barcode) {
        // Playing barcode reader beep sound
        mBarcodeReader.playBeep();
        // Add to the BlockChain
        // TODO: Implement BlockChain
        String previousHash = "0";
        if (!mBlockChain.isEmpty()) {
            previousHash = mBlockChain.get(mBlockChain.size() - 1).getPreviousHash();
        }
        Log.v(LOG_TAG, "Adding New Block......");
        mBlockChain.add(new Block(previousHash, barcode.rawValue));
        Log.v(LOG_TAG, "Mining Block " + mBlockChain.size() + "...");
        mBlockChain.get(mBlockChain.size() - 1).mineBlock(DIFFICULTY);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notifyId = 1;
        Notification notification = new Notification.Builder(this)
                .setContentTitle("IS CHAIN VALID?")
                .setContentText(String.valueOf(isChainValid()))
                .setSmallIcon(R.drawable.ic_launcher_background)
                .build();
        notificationManager.notify(notifyId, notification);
    }
    */

    /* Helper method to check the validity of the chain
    public static boolean isChainValid() {
        Block mCurrentBlock;
        Block mPreviousBlock;
        String hashTarget = new String(new char[DIFFICULTY]).replace('\0', '0');
        // Loop through the blockchain:
        for (int i = 1; i < mBlockChain.size(); i++) {
            mCurrentBlock = mBlockChain.get(i);
            mPreviousBlock = mBlockChain.get(i - 1);
            // Compare registered hash and calculated hash:
            if (!mCurrentBlock.getBlockHash().equals(mCurrentBlock.calculateHash())) {
                Log.e(LOG_TAG, "Current Hash Invalid");
                return false;
            }
            // Compare previous hash and previous registered hash:
            if (!mPreviousBlock.getBlockHash().equals(mCurrentBlock.getPreviousHash())) {
                Log.e(LOG_TAG, "Previous Hash Invalid");
                return false;
            }
            // Check if the hash was solved with the given difficulty:
            if (!mCurrentBlock.getBlockHash().substring(0, DIFFICULTY).equals(hashTarget)) {
                Log.e(LOG_TAG, "Block Not Yet Mined");
                return false;
            }
        }
        return true;
    } */

    @Override
    protected void onResume() {
        super.onResume();
        mBarcodeReader.resumeScanning();
    }

    @Override
    public void onScanned(final Barcode barcode) {
        mBarcodeReader.playBeep();
        Log.v(LOG_TAG, "Barcode Data Read: " + barcode.displayValue);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (barcode.rawValue != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ScannerActivity.this);
                    LayoutInflater inflater = LayoutInflater.from(ScannerActivity.this);
                    View view = inflater.inflate(R.layout.layout_barcode, (ViewGroup) findViewById(R.id.root_barcode));
                    builder.setView(view);
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    TextView isbnum = view.findViewById(R.id.isbn);
                    TextView title = view.findViewById(R.id.title);
                    Button Search = view.findViewById(R.id.search);
                    Button Rescan = view.findViewById(R.id.rescan);
                    Button Dismiss = view.findViewById(R.id.dismiss);
                    builder.setTitle(R.string.error);
                    isbnum.setText(barcode.rawValue);
                    alertDialog.show();
                    Rescan.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mBarcodeReader.resumeScanning();
                            alertDialog.dismiss();
                        }

                    });

                    Dismiss.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivityForResult(intent, DISMISS_BARCODE);
                        }

                    });




                }


            }
        });
        mBarcodeReader.pauseScanning();

    }

    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String errorMessage) {
        Log.e(LOG_TAG, errorMessage);
    }

    @Override
    public void onCameraPermissionDenied() {

    }
}

