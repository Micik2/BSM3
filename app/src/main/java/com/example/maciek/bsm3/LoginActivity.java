package com.example.maciek.bsm3;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.maciek.bsm3.MainActivity.hashMD5;
import static com.example.maciek.bsm3.MainActivity.passwordDb;

/**
 * Created by Maciek on 2017-10-10.
 */

public class LoginActivity extends AppCompatActivity {
    private EditText password;
    private Button submit;
    private TextView information;
    private String pass;
    private Context context;
    private String text;
    private Cursor cursor;
    private Button submitFinger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = getApplicationContext();
        SQLiteDatabase.loadLibs(this);

        information = (TextView) findViewById(R.id.information);
        information.setText("Wpisz hasło, aby uzyskać dostęp do wiadomości");

        password = (EditText) findViewById(R.id.password);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        submit = (Button) findViewById(R.id.submit);
        submitFinger = (Button) findViewById(R.id.submitFinger);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pass = hashMD5(password.getText().toString());
                MyDbHelper myDbHelper = new MyDbHelper(context);
                SQLiteDatabase database = myDbHelper.getReadableDatabase(passwordDb);
                String[] tableColumns = new String[] {"Password"};
                String whereClause = "Password = ?";
                String[] whereArgs = new String[] {pass};

                cursor = database.query("User", tableColumns, whereClause, whereArgs, null, null, null);

                if (cursor.moveToFirst()) {
                    text = cursor.getString(0);
                    if (text.equals(pass)) {
                        Intent messageIntent = new Intent(context, PasswordActivity.class);
                        messageIntent.putExtra("password", pass);
                        startActivity(messageIntent);
                    }
                    else
                        Toast.makeText(getApplicationContext(), "Wpisano błędne hasło!", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getApplicationContext(), "Wpisano błędne hasło!", Toast.LENGTH_SHORT).show();
            }
        });

        submitFinger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, Fingerprint.class));
            }
        });

    }


}



