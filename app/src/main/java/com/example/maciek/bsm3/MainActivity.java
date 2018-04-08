package com.example.maciek.bsm3;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import net.sqlcipher.database.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Maciek on 2017-10-10.
 */

public class MainActivity extends AppCompatActivity {
    private Button confirm;
    private Context context;
    private EditText password;
    private EditText message;
    private String pass;
    private String mess;
    protected final static String passwordDb = "mF0auVP092n8u0932m-2938f2Nm0jjs90";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SQLiteDatabase.loadLibs(this);

        context = getApplicationContext();
        confirm = (Button) findViewById(R.id.confirm);
        password = (EditText) findViewById(R.id.password);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        message = (EditText) findViewById(R.id.message);

        context.deleteDatabase("bsm.db");
        MyDbHelper myDbHelper = new MyDbHelper(context);
        final SQLiteDatabase db = myDbHelper.getWritableDatabase(passwordDb);
        db.execSQL("DELETE FROM User");

        final ContentValues values = new ContentValues();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pass = password.getText().toString();
                mess = message.getText().toString();
                pass = hashMD5(pass);
                values.put("password", pass);
                values.put("message", mess);
                db.insert("User", null, values);
                Intent loginIntent = new Intent(context, LoginActivity.class);
                startActivity(loginIntent);
            }
        });
    }

    public final static String hashMD5(String s) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
