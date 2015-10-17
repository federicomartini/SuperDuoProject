package it.jaschke.alexandria.BarCodeReader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class BarCodeScannerActivity extends Activity implements ZBarScannerView.ResultHandler {

    private static final String LOG_TAG = "BarCodeScannerActivity";
    private ZBarScannerView mScannerView;
    int mRequestCode;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZBarScannerView(this);    // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Context context = getApplicationContext();
        CharSequence text = "Barcode captured!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        Intent intent = new Intent();
        intent.putExtra("SCAN_RESULT_INTENT", rawResult.getContents());
        intent.putExtra("BAR_CODE_INTENT", rawResult.getBarcodeFormat().getName());
        setResult(RESULT_OK, intent);

        Log.v(LOG_TAG, rawResult.getContents()); // Prints scan results
        Log.v(LOG_TAG, rawResult.getBarcodeFormat().getName()); // Prints the scan format (qrcode, pdf417 etc.)
        finish();
    }
}
