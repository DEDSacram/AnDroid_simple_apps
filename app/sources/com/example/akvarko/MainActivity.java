package com.example.akvarko;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import com.google.android.material.button.MaterialButton;
import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {
    OkHttpClient client = new OkHttpClient();
    SharedPreferences loginPreferences;
    String promo;
    Button startgame;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_main);
        Button button = (Button) findViewById(R.id.newGame);
        this.startgame = button;
        button.setOnClickListener(new MainActivity$$ExternalSyntheticLambda0(this));
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", 0);
        this.loginPreferences = sharedPreferences;
        this.promo = sharedPreferences.getString(NotificationCompat.CATEGORY_PROMO, HttpUrl.FRAGMENT_ENCODE_SET);
        new Thread(new MainActivity$$ExternalSyntheticLambda3(this, (LinearLayout) findViewById(R.id.maincontainer))).start();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreate$0$com-example-akvarko-MainActivity  reason: not valid java name */
    public /* synthetic */ void m1950lambda$onCreate$0$comexampleakvarkoMainActivity(View view) {
        switchActivities();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreate$3$com-example-akvarko-MainActivity  reason: not valid java name */
    public /* synthetic */ void m1952lambda$onCreate$3$comexampleakvarkoMainActivity(LinearLayout layout) {
        Response response;
        try {
            response = this.client.newCall(new Request.Builder().url(String.format("http://141.147.10.117/games/%s", new Object[]{this.promo})).build()).execute();
            JSONArray myResponse = new JSONArray(response.body().string());
            for (int i = 0; i < myResponse.length(); i++) {
                JSONArray entry = myResponse.getJSONArray(i);
                MaterialButton testSignIn = new MaterialButton(this, (AttributeSet) null, R.attr.materialButtonStyle);
                testSignIn.setCornerRadius(20);
                testSignIn.setBackgroundColor(Color.argb(80, 98, 0, 238));
                testSignIn.setPadding(0, 10, 0, 10);
                testSignIn.setText(entry.getString(1));
                testSignIn.setTag(Integer.valueOf(entry.getInt(0)));
                testSignIn.setOnClickListener(new MainActivity$$ExternalSyntheticLambda1(this));
                runOnUiThread(new MainActivity$$ExternalSyntheticLambda2(layout, testSignIn));
            }
            if (response != null) {
                response.close();
                return;
            }
            return;
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        } catch (IOException e2) {
            e2.printStackTrace();
            return;
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreate$1$com-example-akvarko-MainActivity  reason: not valid java name */
    public /* synthetic */ void m1951lambda$onCreate$1$comexampleakvarkoMainActivity(View v) {
        Intent switchActivityIntent = new Intent(this, PastGameActivity.class);
        switchActivityIntent.putExtra("game_id", v.getTag().toString());
        startActivity(switchActivityIntent);
        finish();
    }

    private void switchActivities() {
        startActivity(new Intent(this, GameActivity.class));
        finish();
    }
}
