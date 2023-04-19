package com.example.akvarko;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import com.google.android.material.button.MaterialButton;
import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    OkHttpClient client = new OkHttpClient();
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    Button loginbtn;
    EditText promo;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_login);
        this.loginbtn = (MaterialButton) findViewById(R.id.confirmLogin);
        this.promo = (EditText) findViewById(R.id.loginBox);
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", 0);
        this.loginPreferences = sharedPreferences;
        this.loginPrefsEditor = sharedPreferences.edit();
        this.promo.setText(this.loginPreferences.getString(NotificationCompat.CATEGORY_PROMO, HttpUrl.FRAGMENT_ENCODE_SET));
        this.loginbtn.setOnClickListener(new LoginActivity$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreate$2$com-example-akvarko-LoginActivity  reason: not valid java name */
    public /* synthetic */ void m1949lambda$onCreate$2$comexampleakvarkoLoginActivity(View view) {
        if (TextUtils.isEmpty(this.promo.getText().toString())) {
            Toast.makeText(this, "Vypln pole", 0).show();
        } else {
            new Thread(new LoginActivity$$ExternalSyntheticLambda2(this)).start();
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreate$1$com-example-akvarko-LoginActivity  reason: not valid java name */
    public /* synthetic */ void m1948lambda$onCreate$1$comexampleakvarkoLoginActivity() {
        Response response;
        try {
            response = this.client.newCall(new Request.Builder().url(String.format("http://141.147.10.117/signin/%s", new Object[]{this.promo.getText().toString()})).build()).execute();
            if (response.body().string().equals(String.valueOf(true))) {
                this.loginPrefsEditor.putString(NotificationCompat.CATEGORY_PROMO, this.promo.getText().toString());
                this.loginPrefsEditor.commit();
                switchActivities();
            } else {
                runOnUiThread(new LoginActivity$$ExternalSyntheticLambda1(this));
            }
            if (response != null) {
                response.close();
                return;
            }
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreate$0$com-example-akvarko-LoginActivity  reason: not valid java name */
    public /* synthetic */ void m1947lambda$onCreate$0$comexampleakvarkoLoginActivity() {
        Toast.makeText(getApplicationContext(), "Špatný promokód", 0).show();
    }

    private void switchActivities() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
