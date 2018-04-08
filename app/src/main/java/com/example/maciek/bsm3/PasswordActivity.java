package com.example.maciek.bsm3;

import android.content.ContentValues;
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

public class PasswordActivity extends AppCompatActivity {
    private EditText newPassword;
    private Button confirm;
    private TextView message;
    private String password;
    private String newPass;
    private MyDbHelper myDbHelper;
    private String text;
    private String intentPassword;
    private Context context;
    private SQLiteDatabase database;
    private Cursor cursor;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        intentPassword = getIntent().getExtras().getString("password");
        context = getApplicationContext();
        SQLiteDatabase.loadLibs(this);
        myDbHelper = new MyDbHelper(context);
        database = myDbHelper.getReadableDatabase(passwordDb);
        String[] tableColumns = new String[] {"Password"};
        String whereClause = "Password = ?";
        String[] whereArgs = new String[] {intentPassword};

        cursor = database.query("User", tableColumns, whereClause, whereArgs, null, null, null);
        cursor.moveToFirst();
        text = cursor.getString(0);
        if (!text.equals(intentPassword)) {
            finish();
            Intent intent1 = new Intent(context, MainActivity.class);
            startActivity(intent1);
            Toast.makeText(getApplicationContext(), "Brak dostępu!", Toast.LENGTH_LONG).show();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_activity);
        myDbHelper = new MyDbHelper(getApplicationContext());

        newPassword = (EditText) findViewById(R.id.password);
        newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        confirm = (Button) findViewById(R.id.confirm);
        message = (TextView) findViewById(R.id.message);
        back = (Button) findViewById(R.id.back);

        password = getIntent().getExtras().getString("password");

        SQLiteDatabase database1 = myDbHelper.getReadableDatabase(passwordDb);
        tableColumns = new String[] {"message"};
        whereClause = "Password = ?";
        whereArgs = new String[] {password};

        Cursor cursor = database1.query("User", tableColumns, whereClause, whereArgs, null, null, null);

        cursor.moveToFirst();
        text = cursor.getString(0);

        message.setText(text);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase database2 = myDbHelper.getWritableDatabase(passwordDb);
                ContentValues value = new ContentValues();
                newPass = hashMD5(newPassword.getText().toString());
                value.put("password", newPass);
                String whereClause = "Password = ?";
                String[] whereArgs = new String[]{password};
                database2.update("User", value, whereClause, whereArgs);
                Toast.makeText(getApplicationContext(), "Uaktualniono hasło", Toast.LENGTH_LONG).show();
                Intent mainIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(mainIntent);
                finish();
            }

        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent2 = new Intent(context, LoginActivity.class);
                startActivity(intent2);
                Toast.makeText(getApplicationContext(), "Poprawnie wylogowano!", Toast.LENGTH_LONG).show();

            }
        });

    }
}
