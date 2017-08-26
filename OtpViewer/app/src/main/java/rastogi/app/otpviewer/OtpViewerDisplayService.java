package rastogi.app.otpviewer;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;


public class OtpViewerDisplayService extends Service {

    private WindowManager windowManager;
    private LinearLayout OtpMessege;
    private String sender = "";
    private String messege = "";
    private String otp = "";
    WindowManager.LayoutParams params;
    private GestureDetector gestureDetector;
    public OtpViewerDisplayService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        sender = intent.getStringExtra("Sender");
        messege = intent.getStringExtra("Messege");
        otp = intent.getStringExtra("OTP");
        if (OtpMessege != null && OtpMessege.getWindowToken() != null)
            windowManager.removeView(OtpMessege);
        TextView tv=(TextView)OtpMessege.findViewById(R.id.text1);
        tv.setText(otp);
        gestureDetector = new GestureDetector(this, new SingleTapConfirm());
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 250;

        OtpMessege.setOnTouchListener(new View.OnTouchListener() {
            private int initX;
            private int initY;
            private float initTouchX;
            private float initTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    // single tap
                    if (OtpMessege != null && OtpMessege.getWindowToken() != null)
                        windowManager.removeView(OtpMessege);
                    stopSelf();
                    return true;
                } else {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            initX = params.x;
                            initY = params.y;
                            initTouchX = event.getRawX();
                            initTouchY = event.getRawY();
                            return true;
                        case MotionEvent.ACTION_UP:
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            params.x = initX
                                    + (int) (event.getRawX() - initTouchX);
                            params.y = initY
                                    + (int) (event.getRawY() - initTouchY);
                            if (OtpMessege.getWindowToken() != null) {
                                windowManager.updateViewLayout(OtpMessege, params);
                            } else {
                                stopSelf();
                            }
                            return true;
                    }
                    return false;
                }
            }
        });
        windowManager.addView(OtpMessege, params);
        return START_NOT_STICKY;
    }

    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LayoutInflater inflater= (LayoutInflater)getSystemService(this.LAYOUT_INFLATER_SERVICE);
        OtpMessege = (LinearLayout)inflater.inflate(R.layout.otp_messege,null,false);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (OtpMessege != null && OtpMessege.getWindowToken() != null)
            windowManager.removeView(OtpMessege);
    }
}
