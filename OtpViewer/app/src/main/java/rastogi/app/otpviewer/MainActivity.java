package rastogi.app.otpviewer;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    @TargetApi(23)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.RECEIVE_SMS) + ContextCompat
                .checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission
                            .RECEIVE_SMS, Manifest.permission.READ_SMS},
                    0);
            return;

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Intent it = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    startActivityForResult(it, 0);
                }
            }
            EnableReceiver();
        }
        setContentView(R.layout.activity_main);
    }
    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if ((grantResults.length > 0) && (grantResults[0] +
                grantResults[1]) == PackageManager.PERMISSION_GRANTED) {
            EnableReceiver();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Intent it = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    startActivityForResult(it, 0);
                }
            }
        } else {
            DisableReceiver();
        }
    }
    @TargetApi(23)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "This permision needed, Please enable it ", Toast.LENGTH_LONG).show();
                DisableReceiver();
                finish();
            }
        }
        if (Settings.canDrawOverlays(this)) {
            EnableReceiver();
        }
    }

    void EnableReceiver() {
        getPackageManager()
                .setComponentEnabledSetting(
                        new ComponentName(MainActivity.this, SmsReader.class),
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);

    }

    void DisableReceiver() {
        getPackageManager()
                .setComponentEnabledSetting(
                        new ComponentName(MainActivity.this, SmsReader.class),
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);

    }
}
