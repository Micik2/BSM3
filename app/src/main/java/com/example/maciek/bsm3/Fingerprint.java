package com.example.maciek.bsm3;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.fingerprint.FingerprintManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static com.example.maciek.bsm3.FingerprintHandler.fingerprint;
import static com.example.maciek.bsm3.MainActivity.passwordDb;


public class Fingerprint extends AppCompatActivity {
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private TextView errorText;
    private Button submitAuth;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private Cipher cipher;
    private SecretKey secretKey;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintHandler fingerprintHandler;
    private Button hideButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);


        errorText = (TextView) findViewById(R.id.errorText);
        submitAuth = (Button) findViewById(R.id.submitAuth);
        hideButton = (Button) findViewById(R.id.hideButton);
        hideButton.setEnabled(false);
        hideButton.setVisibility(View.GONE);

        SQLiteDatabase.loadLibs(this);

        fingerprintHandler = new FingerprintHandler(hideButton, errorText);

        keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        if (!checkFinger(keyguardManager, fingerprintManager)) {
            finish();
        }
        else {
            try {
                generateKey();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (CertificateException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            }
            try {
                cipher = generateCipher();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnrecoverableKeyException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
            cryptoObject = new FingerprintManager.CryptoObject(cipher);
        }

        submitAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorText.setText("Odciśnij palca");
                fingerprintHandler.doAuth(fingerprintManager, cryptoObject);
            }
        });

        hideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fingerprint) {
                    MyDbHelper myDbHelper = new MyDbHelper(getApplicationContext());
                    SQLiteDatabase database = myDbHelper.getReadableDatabase(passwordDb);
                    Cursor cursor = database.rawQuery("SELECT * from User", null);
                    cursor.moveToFirst();
                    String password = cursor.getString(0);
                    Intent intent = new Intent(getApplicationContext(), PasswordActivity.class);
                    intent.putExtra("password", password);
                    startActivity(intent);
                }
            }
        });

    }

    private void generateKey() throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException, IOException, CertificateException, InvalidAlgorithmParameterException {
        keyStore = KeyStore.getInstance("AndroidKeyStore");
        //AES (Advanced Encryption Standard)
        keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

        // Pusty klucz tworzymy
        keyStore.load(null);
        // Ustawiony dla szyfrowania i odszyfrowywania, tryb wiązania bloków zaszyfrowanych = CBC, jeżeli użytkownik został uwierzytelniony to klucz jest używany, schemat dopełnienia klucza = PKCS7 (próby użycia klucza z jakimkolwiek innym schematem zostaną odrzucone)
        keyGenerator.init(new KeyGenParameterSpec.Builder("my_key", KeyProperties.PURPOSE_DECRYPT | KeyProperties.PURPOSE_ENCRYPT).setBlockModes(KeyProperties.BLOCK_MODE_CBC).setUserAuthenticationRequired(true).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7).build());
        // Generuje sekretny klucz
        keyGenerator.generateKey();
    }

    private Cipher generateCipher() throws NoSuchPaddingException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException, InvalidKeyException {
        cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + KeyProperties.BLOCK_MODE_CBC + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        // Pobiera klucz o danej nazwie
        secretKey = (SecretKey) keyStore.getKey("my_key", null);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher;
    }

    private boolean checkFinger(KeyguardManager keyguardManager, FingerprintManager fingerprintManager) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            errorText.setText("Nie posiadasz uprawnień!");
            return false;
        }
        if (!fingerprintManager.isHardwareDetected()) {
            errorText.setText("Autoryzacja za pomocą odciska palca nie jest wspierana na tym urządzeniu!");
            return false;
        }
        if (!fingerprintManager.hasEnrolledFingerprints()) {
            errorText.setText("Nie zostały dodane żadne odciski palców!");
            return false;
        }
        if (!keyguardManager.isKeyguardSecure()) {
            errorText.setText("Ekran blokady nie został włączony!");
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
