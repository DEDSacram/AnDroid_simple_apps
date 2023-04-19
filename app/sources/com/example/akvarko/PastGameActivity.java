package com.example.akvarko;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import okhttp3.OkHttpClient;

public class PastGameActivity extends AppCompatActivity {
    LinearLayout scoreboard;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_pastgame);
        if (getIntent().hasExtra("game_id")) {
            Get(getIntent().getExtras().getString("game_id"));
        }
        this.scoreboard = (LinearLayout) findViewById(R.id.scoreboard);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4 || event.getRepeatCount() != 0) {
            return super.onKeyDown(keyCode, event);
        }
        startActivity(new Intent(this, MainActivity.class));
        finish();
        return true;
    }

    public void Get(String gameid) {
        new Thread(new PastGameActivity$$ExternalSyntheticLambda2(this, gameid, new OkHttpClient())).start();
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x01ab A[SYNTHETIC, Splitter:B:30:0x01ab] */
    /* renamed from: lambda$Get$2$com-example-akvarko-PastGameActivity  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m1954lambda$Get$2$comexampleakvarkoPastGameActivity(java.lang.String r26, okhttp3.OkHttpClient r27) {
        /*
            r25 = this;
            r1 = r25
            okhttp3.Request$Builder r0 = new okhttp3.Request$Builder
            r0.<init>()
            r2 = 1
            java.lang.Object[] r3 = new java.lang.Object[r2]
            r4 = 0
            r3[r4] = r26
            java.lang.String r5 = "http://141.147.10.117/players/%s"
            java.lang.String r3 = java.lang.String.format(r5, r3)
            okhttp3.Request$Builder r0 = r0.url((java.lang.String) r3)
            okhttp3.Request r3 = r0.build()
            r5 = r27
            okhttp3.Call r0 = r5.newCall(r3)     // Catch:{ IOException -> 0x01bb, JSONException -> 0x01b9 }
            okhttp3.Response r0 = r0.execute()     // Catch:{ IOException -> 0x01bb, JSONException -> 0x01b9 }
            r6 = r0
            org.json.JSONArray r0 = new org.json.JSONArray     // Catch:{ all -> 0x01a5 }
            okhttp3.ResponseBody r7 = r6.body()     // Catch:{ all -> 0x01a5 }
            java.lang.String r7 = r7.string()     // Catch:{ all -> 0x01a5 }
            r0.<init>(r7)     // Catch:{ all -> 0x01a5 }
            r7 = 0
        L_0x0034:
            int r8 = r0.length()     // Catch:{ all -> 0x01a5 }
            if (r7 >= r8) goto L_0x019b
            android.widget.LinearLayout r8 = new android.widget.LinearLayout     // Catch:{ all -> 0x01a5 }
            r8.<init>(r1)     // Catch:{ all -> 0x01a5 }
            android.widget.LinearLayout$LayoutParams r9 = new android.widget.LinearLayout$LayoutParams     // Catch:{ all -> 0x01a5 }
            r10 = -1
            r9.<init>(r10, r10)     // Catch:{ all -> 0x01a5 }
            r10 = 10
            r9.setMargins(r4, r4, r4, r10)     // Catch:{ all -> 0x01a5 }
            r8.setLayoutParams(r9)     // Catch:{ all -> 0x01a5 }
            r8.setOrientation(r2)     // Catch:{ all -> 0x01a5 }
            android.widget.LinearLayout r10 = new android.widget.LinearLayout     // Catch:{ all -> 0x01a5 }
            r10.<init>(r1)     // Catch:{ all -> 0x01a5 }
            android.widget.LinearLayout$LayoutParams r11 = new android.widget.LinearLayout$LayoutParams     // Catch:{ all -> 0x01a5 }
            r12 = -2
            r11.<init>(r12, r12)     // Catch:{ all -> 0x01a5 }
            r10.setLayoutParams(r11)     // Catch:{ all -> 0x01a5 }
            r10.setOrientation(r2)     // Catch:{ all -> 0x01a5 }
            org.json.JSONObject r11 = r0.getJSONObject(r7)     // Catch:{ all -> 0x01a5 }
            org.json.JSONArray r13 = new org.json.JSONArray     // Catch:{ all -> 0x01a5 }
            java.lang.String r14 = "grid"
            java.lang.String r14 = r11.getString(r14)     // Catch:{ all -> 0x01a5 }
            r13.<init>(r14)     // Catch:{ all -> 0x01a5 }
            java.lang.String r14 = "nazev"
            java.lang.String r14 = r11.getString(r14)     // Catch:{ all -> 0x01a5 }
            java.lang.String r15 = "skore"
            java.lang.String r15 = r11.getString(r15)     // Catch:{ all -> 0x01a5 }
            com.google.android.material.button.MaterialButton r2 = new com.google.android.material.button.MaterialButton     // Catch:{ all -> 0x01a5 }
            r2.<init>(r1)     // Catch:{ all -> 0x01a5 }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x01a5 }
            r12.<init>()     // Catch:{ all -> 0x01a5 }
            java.lang.StringBuilder r12 = r12.append(r14)     // Catch:{ all -> 0x01a5 }
            java.lang.String r4 = "-"
            java.lang.StringBuilder r4 = r12.append(r4)     // Catch:{ all -> 0x01a5 }
            java.lang.StringBuilder r4 = r4.append(r15)     // Catch:{ all -> 0x01a5 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x01a5 }
            r2.setText(r4)     // Catch:{ all -> 0x01a5 }
            r4 = 1106247680(0x41f00000, float:30.0)
            r2.setTextSize(r4)     // Catch:{ all -> 0x01a5 }
            r4 = 2
            r2.setTextAlignment(r4)     // Catch:{ all -> 0x01a5 }
            r4 = 2131165273(0x7f070059, float:1.7944758E38)
            r12 = 0
            r2.setCompoundDrawablesWithIntrinsicBounds(r4, r12, r12, r12)     // Catch:{ all -> 0x01a5 }
            r4 = 8
            r2.setCompoundDrawablePadding(r4)     // Catch:{ all -> 0x01a5 }
            com.example.akvarko.PastGameActivity$$ExternalSyntheticLambda0 r12 = new com.example.akvarko.PastGameActivity$$ExternalSyntheticLambda0     // Catch:{ all -> 0x01a5 }
            r12.<init>(r10)     // Catch:{ all -> 0x01a5 }
            r2.setOnClickListener(r12)     // Catch:{ all -> 0x01a5 }
            r8.addView(r2)     // Catch:{ all -> 0x01a5 }
            r12 = 0
        L_0x00bc:
            int r4 = r13.length()     // Catch:{ all -> 0x01a5 }
            if (r12 >= r4) goto L_0x0171
            android.widget.LinearLayout r4 = new android.widget.LinearLayout     // Catch:{ all -> 0x01a5 }
            r4.<init>(r1)     // Catch:{ all -> 0x01a5 }
            r17 = r0
            android.widget.LinearLayout$LayoutParams r0 = new android.widget.LinearLayout$LayoutParams     // Catch:{ all -> 0x01a5 }
            r18 = r2
            r2 = -2
            r0.<init>(r2, r2)     // Catch:{ all -> 0x01a5 }
            r4.setLayoutParams(r0)     // Catch:{ all -> 0x01a5 }
            r0 = 0
            r4.setOrientation(r0)     // Catch:{ all -> 0x01a5 }
            org.json.JSONArray r0 = r13.getJSONArray(r12)     // Catch:{ all -> 0x01a5 }
            r2 = 0
        L_0x00dd:
            r19 = r3
            int r3 = r0.length()     // Catch:{ all -> 0x0198 }
            if (r2 >= r3) goto L_0x0154
            android.widget.ImageButton r3 = new android.widget.ImageButton     // Catch:{ all -> 0x0198 }
            r3.<init>(r1)     // Catch:{ all -> 0x0198 }
            android.graphics.drawable.Drawable r20 = r3.getBackground()     // Catch:{ all -> 0x0198 }
            android.graphics.drawable.Drawable r21 = androidx.core.graphics.drawable.DrawableCompat.wrap(r20)     // Catch:{ all -> 0x0198 }
            r22 = r21
            r16 = r9
            r5 = r22
            r9 = 0
            androidx.core.graphics.drawable.DrawableCompat.setTint(r5, r9)     // Catch:{ all -> 0x0198 }
            r3.setBackground(r5)     // Catch:{ all -> 0x0198 }
            android.content.Context r20 = r25.getApplicationContext()     // Catch:{ all -> 0x0198 }
            android.content.res.Resources r9 = r20.getResources()     // Catch:{ all -> 0x0198 }
            r22 = r5
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0198 }
            r5.<init>()     // Catch:{ all -> 0x0198 }
            r23 = r11
            java.lang.String r11 = "drawable/fish_"
            java.lang.StringBuilder r5 = r5.append(r11)     // Catch:{ all -> 0x0198 }
            java.lang.String r11 = r0.getString(r2)     // Catch:{ all -> 0x0198 }
            java.lang.StringBuilder r5 = r5.append(r11)     // Catch:{ all -> 0x0198 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0198 }
            java.lang.String r11 = r20.getPackageName()     // Catch:{ all -> 0x0198 }
            r24 = r0
            r0 = 0
            int r0 = r9.getIdentifier(r5, r0, r11)     // Catch:{ all -> 0x0198 }
            r3.setImageResource(r0)     // Catch:{ all -> 0x0198 }
            android.widget.LinearLayout$LayoutParams r5 = new android.widget.LinearLayout$LayoutParams     // Catch:{ all -> 0x0198 }
            r9 = 1065353216(0x3f800000, float:1.0)
            r11 = -2
            r5.<init>(r11, r11, r9)     // Catch:{ all -> 0x0198 }
            r3.setLayoutParams(r5)     // Catch:{ all -> 0x0198 }
            android.widget.ImageView$ScaleType r9 = android.widget.ImageView.ScaleType.FIT_CENTER     // Catch:{ all -> 0x0198 }
            r3.setScaleType(r9)     // Catch:{ all -> 0x0198 }
            r9 = 1
            r3.setAdjustViewBounds(r9)     // Catch:{ all -> 0x0198 }
            r4.addView(r3)     // Catch:{ all -> 0x0198 }
            int r2 = r2 + 1
            r5 = r27
            r9 = r16
            r3 = r19
            r11 = r23
            r0 = r24
            goto L_0x00dd
        L_0x0154:
            r24 = r0
            r16 = r9
            r23 = r11
            r9 = 1
            r11 = -2
            r10.addView(r4)     // Catch:{ all -> 0x0198 }
            int r12 = r12 + 1
            r5 = r27
            r9 = r16
            r0 = r17
            r2 = r18
            r3 = r19
            r11 = r23
            r4 = 8
            goto L_0x00bc
        L_0x0171:
            r17 = r0
            r18 = r2
            r19 = r3
            r16 = r9
            r23 = r11
            r9 = 1
            r0 = 8
            r10.setVisibility(r0)     // Catch:{ all -> 0x0198 }
            r8.addView(r10)     // Catch:{ all -> 0x0198 }
            com.example.akvarko.PastGameActivity$$ExternalSyntheticLambda1 r0 = new com.example.akvarko.PastGameActivity$$ExternalSyntheticLambda1     // Catch:{ all -> 0x0198 }
            r0.<init>(r1, r8)     // Catch:{ all -> 0x0198 }
            r1.runOnUiThread(r0)     // Catch:{ all -> 0x0198 }
            int r7 = r7 + 1
            r5 = r27
            r2 = r9
            r0 = r17
            r3 = r19
            r4 = 0
            goto L_0x0034
        L_0x0198:
            r0 = move-exception
            r2 = r0
            goto L_0x01a9
        L_0x019b:
            r17 = r0
            r19 = r3
            if (r6 == 0) goto L_0x01a4
            r6.close()     // Catch:{ IOException -> 0x01b7, JSONException -> 0x01b5 }
        L_0x01a4:
            goto L_0x01c1
        L_0x01a5:
            r0 = move-exception
            r19 = r3
            r2 = r0
        L_0x01a9:
            if (r6 == 0) goto L_0x01b4
            r6.close()     // Catch:{ all -> 0x01af }
            goto L_0x01b4
        L_0x01af:
            r0 = move-exception
            r3 = r0
            r2.addSuppressed(r3)     // Catch:{ IOException -> 0x01b7, JSONException -> 0x01b5 }
        L_0x01b4:
            throw r2     // Catch:{ IOException -> 0x01b7, JSONException -> 0x01b5 }
        L_0x01b5:
            r0 = move-exception
            goto L_0x01be
        L_0x01b7:
            r0 = move-exception
            goto L_0x01be
        L_0x01b9:
            r0 = move-exception
            goto L_0x01bc
        L_0x01bb:
            r0 = move-exception
        L_0x01bc:
            r19 = r3
        L_0x01be:
            r0.printStackTrace()
        L_0x01c1:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.example.akvarko.PastGameActivity.m1954lambda$Get$2$comexampleakvarkoPastGameActivity(java.lang.String, okhttp3.OkHttpClient):void");
    }

    static /* synthetic */ void lambda$Get$0(LinearLayout vertical, View view) {
        MaterialButton temp = (MaterialButton) view;
        if (vertical.getVisibility() == 0) {
            temp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_expand_more_18, 0, 0, 0);
            vertical.setVisibility(8);
            return;
        }
        temp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_expand_less_18, 0, 0, 0);
        vertical.setVisibility(0);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$Get$1$com-example-akvarko-PastGameActivity  reason: not valid java name */
    public /* synthetic */ void m1953lambda$Get$1$comexampleakvarkoPastGameActivity(LinearLayout xD) {
        this.scoreboard.addView(xD);
    }
}
