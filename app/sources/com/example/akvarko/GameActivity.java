package com.example.akvarko;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;

public class GameActivity extends AppCompatActivity {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    DrawerLayout Drawer;
    LinearLayout Drawer_content;
    Button Drawer_open;
    final int PermissionCode = 1;
    HashMap<Integer, Player> Players = new HashMap<>();
    Button addplayer;
    ImageButton chosen_btn;
    int currentplayer = -1;
    Button detect;
    Button endgame;
    SharedPreferences loginPreferences;
    int player_id = 0;
    LinearLayout row_1;
    LinearLayout row_2;
    LinearLayout row_3;
    LinearLayout row_4;
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        public void onActivityResult(ActivityResult result) {
            Bitmap bitmap = BitmapFactory.decodeFile(result.getData().getStringExtra("fpath"));
            GameActivity.this.detect.setText("Počkejte prosím");
            new Thread(new GameActivity$1$$ExternalSyntheticLambda1(this, bitmap)).start();
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$onActivityResult$1$com-example-akvarko-GameActivity$1  reason: not valid java name */
        public /* synthetic */ void m1946lambda$onActivityResult$1$comexampleakvarkoGameActivity$1(Bitmap bitmap) {
            Response response;
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(120, TimeUnit.SECONDS).build();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            try {
                response = client.newCall(new Request.Builder().url(String.format("http://141.147.10.117/detect/%s", new Object[]{GameActivity.this.loginPreferences.getString(NotificationCompat.CATEGORY_PROMO, HttpUrl.FRAGMENT_ENCODE_SET)})).post(RequestBody.create(GameActivity.JSON, new Gson().toJson((Object) new PhotoFile(Base64.encodeToString(baos.toByteArray(), 0))))).build()).execute();
                JSONArray myResponse = new JSONArray(response.body().string());
                for (int p = 0; p < myResponse.length(); p++) {
                    GameActivity.this.Players.get(Integer.valueOf(GameActivity.this.currentplayer)).Grid[(int) Math.floor(new Double((double) p).doubleValue() / 4.0d)][p % 4] = Integer.valueOf(myResponse.getString(p)).intValue();
                }
                GameActivity.this.runOnUiThread(new GameActivity$1$$ExternalSyntheticLambda0(this));
                if (response != null) {
                    response.close();
                    return;
                }
                return;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return;
            } catch (Throwable th) {
                th.addSuppressed(th);
            }
            throw th;
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$onActivityResult$0$com-example-akvarko-GameActivity$1  reason: not valid java name */
        public /* synthetic */ void m1945lambda$onActivityResult$0$comexampleakvarkoGameActivity$1() {
            GameActivity.this.detect.setText("Detekuj");
            GameActivity gameActivity = GameActivity.this;
            gameActivity.CreateGrid(gameActivity.Players.get(Integer.valueOf(GameActivity.this.currentplayer)).Grid);
        }
    });

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_game);
        this.Drawer_open = (Button) findViewById(R.id.Drawer_btn);
        this.addplayer = (Button) findViewById(R.id.addplayer_btn);
        this.endgame = (Button) findViewById(R.id.end_game);
        Button button = (Button) findViewById(R.id.detect);
        this.detect = button;
        button.setText("Detekuj");
        this.detect.setOnClickListener(new GameActivity$$ExternalSyntheticLambda5(this));
        this.Drawer = (DrawerLayout) findViewById(R.id.Drawer_menu);
        this.Drawer_content = (LinearLayout) findViewById(R.id.Drawer_Content);
        this.row_1 = (LinearLayout) findViewById(R.id.row_1);
        this.row_2 = (LinearLayout) findViewById(R.id.row_2);
        this.row_3 = (LinearLayout) findViewById(R.id.row_3);
        this.row_4 = (LinearLayout) findViewById(R.id.row_4);
        this.loginPreferences = getSharedPreferences("loginPrefs", 0);
        ((NavigationView) findViewById(R.id.players)).bringToFront();
        this.endgame.setOnClickListener(new GameActivity$$ExternalSyntheticLambda6(this));
        this.Drawer_open.setOnClickListener(new GameActivity$$ExternalSyntheticLambda7(this));
        this.addplayer.setOnClickListener(new GameActivity$$ExternalSyntheticLambda9(this));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreate$0$com-example-akvarko-GameActivity  reason: not valid java name */
    public /* synthetic */ void m1938lambda$onCreate$0$comexampleakvarkoGameActivity(View view) {
        if (!checkCameraHardware(this)) {
            return;
        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.CAMERA") == 0 && ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0 && ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") == 0) {
            openSomeActivityForResult();
        } else {
            Check();
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreate$2$com-example-akvarko-GameActivity  reason: not valid java name */
    public /* synthetic */ void m1939lambda$onCreate$2$comexampleakvarkoGameActivity(View view) {
        int i = this.currentplayer;
        if (i != -1) {
            SaveLinetoGrid(this.row_1, ((Player) Objects.requireNonNull(this.Players.get(Integer.valueOf(i)))).Grid, 0);
            SaveLinetoGrid(this.row_2, ((Player) Objects.requireNonNull(this.Players.get(Integer.valueOf(this.currentplayer)))).Grid, 1);
            SaveLinetoGrid(this.row_3, ((Player) Objects.requireNonNull(this.Players.get(Integer.valueOf(this.currentplayer)))).Grid, 2);
            SaveLinetoGrid(this.row_4, ((Player) Objects.requireNonNull(this.Players.get(Integer.valueOf(this.currentplayer)))).Grid, 3);
            Player[] j = new Player[this.Players.size()];
            List<String> errorNames = new ArrayList<>();
            int i2 = 0;
            while (i2 < this.Players.size()) {
                Player current = this.Players.get(Integer.valueOf(i2));
                if (current != null) {
                    if (current.Check()) {
                        j[i2] = current;
                    } else {
                        errorNames.add(current.name.toUpperCase());
                    }
                    i2++;
                } else {
                    throw new AssertionError();
                }
            }
            if (errorNames.size() == 0) {
                Post(j);
                return;
            }
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle((CharSequence) "Hra nemá hráče");
        builder.setPositiveButton((CharSequence) "OK", (DialogInterface.OnClickListener) GameActivity$$ExternalSyntheticLambda2.INSTANCE);
        builder.show();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreate$3$com-example-akvarko-GameActivity  reason: not valid java name */
    public /* synthetic */ void m1940lambda$onCreate$3$comexampleakvarkoGameActivity(View view) {
        if (this.Drawer.isOpen()) {
            this.Drawer.close();
        } else {
            this.Drawer.open();
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreate$7$com-example-akvarko-GameActivity  reason: not valid java name */
    public /* synthetic */ void m1943lambda$onCreate$7$comexampleakvarkoGameActivity(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle((CharSequence) "Jméno hráče");
        EditText input = new EditText(this);
        input.setInputType(1);
        builder.setView((View) input);
        builder.setPositiveButton((CharSequence) "Potvrdit", (DialogInterface.OnClickListener) new GameActivity$$ExternalSyntheticLambda0(this, input));
        builder.setNegativeButton((CharSequence) "Zrušit", (DialogInterface.OnClickListener) GameActivity$$ExternalSyntheticLambda3.INSTANCE);
        builder.show();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreate$5$com-example-akvarko-GameActivity  reason: not valid java name */
    public /* synthetic */ void m1942lambda$onCreate$5$comexampleakvarkoGameActivity(EditText input, DialogInterface dialog, int which) {
        MaterialButton testSignIn = new MaterialButton(this.Drawer_content.getContext(), (AttributeSet) null, R.attr.materialButtonOutlinedStyle);
        testSignIn.setText(input.getText().toString());
        testSignIn.setId(this.player_id);
        testSignIn.setOnClickListener(new GameActivity$$ExternalSyntheticLambda8(this));
        this.Drawer_content.addView(testSignIn, 0);
        this.Players.put(Integer.valueOf(this.player_id), new Player(input.getText().toString()));
        this.player_id++;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreate$4$com-example-akvarko-GameActivity  reason: not valid java name */
    public /* synthetic */ void m1941lambda$onCreate$4$comexampleakvarkoGameActivity(View v) {
        int i = this.currentplayer;
        if (i != -1) {
            SaveLinetoGrid(this.row_1, ((Player) Objects.requireNonNull(this.Players.get(Integer.valueOf(i)))).Grid, 0);
            SaveLinetoGrid(this.row_2, ((Player) Objects.requireNonNull(this.Players.get(Integer.valueOf(this.currentplayer)))).Grid, 1);
            SaveLinetoGrid(this.row_3, ((Player) Objects.requireNonNull(this.Players.get(Integer.valueOf(this.currentplayer)))).Grid, 2);
            SaveLinetoGrid(this.row_4, ((Player) Objects.requireNonNull(this.Players.get(Integer.valueOf(this.currentplayer)))).Grid, 3);
        }
        this.currentplayer = v.getId();
        this.detect.setVisibility(0);
        CreateGrid(((Player) Objects.requireNonNull(this.Players.get(Integer.valueOf(v.getId())))).Grid);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length <= 0 || grantResults[0] != 0) {
                    Toast.makeText(this, "Použití kamery zamítnuto", 0).show();
                    return;
                } else {
                    openSomeActivityForResult();
                    return;
                }
            default:
                return;
        }
    }

    public void Check() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.CAMERA") == -1) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == -1) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") == -1) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 1);
        }
    }

    public void openSomeActivityForResult() {
        this.someActivityResultLauncher.launch(new Intent(this, CameraActivity.class));
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature("android.hardware.camera")) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void showCustomDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog);
        for (int i = 0; i < 13; i++) {
            ((ImageButton) dialog.findViewById(R.id.choose_0 + i)).setOnClickListener(new GameActivity$$ExternalSyntheticLambda10(this, dialog));
        }
        dialog.show();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$showCustomDialog$8$com-example-akvarko-GameActivity  reason: not valid java name */
    public /* synthetic */ void m1944lambda$showCustomDialog$8$comexampleakvarkoGameActivity(Dialog dialog, View view) {
        ImageButton button = (ImageButton) view;
        button.getTag();
        Context c = getApplicationContext();
        this.chosen_btn.setImageResource(c.getResources().getIdentifier("drawable/fish_" + button.getTag().toString(), (String) null, c.getPackageName()));
        this.chosen_btn.setTag(button.getTag().toString());
        dialog.dismiss();
    }

    public void Post(Player[] j) {
        new Thread(new GameActivity$$ExternalSyntheticLambda1(this, j)).start();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$Post$9$com-example-akvarko-GameActivity  reason: not valid java name */
    public /* synthetic */ void m1937lambda$Post$9$comexampleakvarkoGameActivity(Player[] j) {
        Response response;
        String id = null;
        try {
            response = new OkHttpClient().newCall(new Request.Builder().url(String.format("http://141.147.10.117/scoregame/%s", new Object[]{this.loginPreferences.getString(NotificationCompat.CATEGORY_PROMO, HttpUrl.FRAGMENT_ENCODE_SET)})).post(RequestBody.create(JSON, new Gson().toJson((Object) j))).build()).execute();
            id = response.body().string();
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        Intent switchActivityIntent = new Intent(this, PastGameActivity.class);
        switchActivityIntent.putExtra("game_id", id);
        startActivity(switchActivityIntent);
        finish();
        return;
        throw th;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4 || event.getRepeatCount() != 0) {
            return super.onKeyDown(keyCode, event);
        }
        startActivity(new Intent(this, MainActivity.class));
        finish();
        return true;
    }

    public void SaveLinetoGrid(LinearLayout row, int[][] Grid, int rownum) {
        int column = 0;
        for (int i = 0; i < row.getChildCount(); i++) {
            if (row.getChildAt(i) instanceof ImageButton) {
                Grid[rownum][column] = Integer.parseInt(((ImageButton) row.getChildAt(i)).getTag().toString());
                column++;
            }
        }
    }

    public void CreateGrid(int[][] Grid) {
        this.row_1.removeAllViews();
        this.row_2.removeAllViews();
        this.row_3.removeAllViews();
        this.row_4.removeAllViews();
        LinearLayout temp = this.row_1;
        for (int row = 0; row < Grid.length; row++) {
            for (int col = 0; col < Grid[row].length; col++) {
                ImageButton s = new ImageButton(this);
                Drawable buttonDrawable = DrawableCompat.wrap(s.getBackground());
                DrawableCompat.setTint(buttonDrawable, 0);
                s.setBackground(buttonDrawable);
                Context c = getApplicationContext();
                s.setImageResource(c.getResources().getIdentifier("drawable/fish_" + Grid[row][col], (String) null, c.getPackageName()));
                s.setTag(Integer.valueOf(Grid[row][col]));
                s.setLayoutParams(new LinearLayout.LayoutParams(-2, -2, 1.0f));
                s.setScaleType(ImageView.ScaleType.FIT_CENTER);
                s.setAdjustViewBounds(true);
                s.setOnClickListener(new GameActivity$$ExternalSyntheticLambda4(this));
                temp.addView(s);
            }
            switch (row + 1) {
                case 1:
                    temp = this.row_2;
                    break;
                case 2:
                    temp = this.row_3;
                    break;
                case 3:
                    temp = this.row_4;
                    break;
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$CreateGrid$10$com-example-akvarko-GameActivity  reason: not valid java name */
    public /* synthetic */ void m1936lambda$CreateGrid$10$comexampleakvarkoGameActivity(View v) {
        this.chosen_btn = (ImageButton) v;
        showCustomDialog();
    }
}
