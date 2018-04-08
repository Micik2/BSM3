package com.example.maciek.bsm3;

import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


/**
 * Created by Maciek on 2017-12-10.
 */


public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {
    private TextView textView;
    private Button button;
    private CancellationSignal cancellationSignal;
    public static boolean fingerprint = false;

    public FingerprintHandler(Button b, TextView tV) {
        this.button = b;
        this.textView = tV;
    }

    @Override
    public void onAuthenticationError(int id, CharSequence error) {
        super.onAuthenticationError(id, error);
        textView.setText("Błąd autoryzacji!");
    }

    @Override
    public void onAuthenticationHelp(int id, CharSequence help) {
        super.onAuthenticationHelp(id, help);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult authenticationResult) {
        super.onAuthenticationSucceeded(authenticationResult);
        textView.setText("Autoryzacja pomyślna");
        textView.setTextColor(textView.getContext().getResources().getColor(android.R.color.holo_green_light));
        fingerprint = true;
        button.setEnabled(true);
        button.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
    }

    public void doAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject) throws SecurityException {
        cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

}
