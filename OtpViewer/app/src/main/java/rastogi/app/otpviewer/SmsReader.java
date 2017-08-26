package rastogi.app.otpviewer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReader extends BroadcastReceiver {


    boolean isOtpMessege(String s) {
        s = s.toUpperCase();
        if (s.contains("OTP") ||
                s.contains("PIN") ||
                s.contains("ONE TIME PASSWORD") ||
                (s.contains("CODE") && !(s.contains("%") || s.contains("ONLINE"))))
            return true;
        return false;
    }

    String getOtp(String s) {
        //to do check for only number and if occurence is 1 then no need to go further

        Matcher makeMatch1 = Pattern.compile("is\\s*\\d+").matcher(s);// otp is 3456
        Matcher makeMatch2 = Pattern.compile("\\d+\\s*is").matcher(s);// 3456 is your otp
        Matcher makeMatch3 = Pattern.compile("is\\s*:\\s*\\d+").matcher(s);// otp is:2345
        Matcher makeMatch4 = Pattern.compile("\\d+\\s*as").matcher(s);// 123456 as your otp
        Matcher makeMatch = Pattern.compile("\\d+").matcher(s);// 123456 as your otp
        makeMatch.find();
        String otp = makeMatch.group();
        if (makeMatch1.find()) {
            otp = makeMatch1.group();
        } else if (makeMatch2.find()) {
            otp = makeMatch2.group();
        } else if (makeMatch3.find()) {
            otp = makeMatch3.group();
        } else if (makeMatch4.find()) {
            otp = makeMatch4.group();
        }
        Matcher makeMatch9 = Pattern.compile("\\d+").matcher(otp);
        if (makeMatch9.find())
            otp = makeMatch9.group();
        return otp;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage NewMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = NewMessage.getDisplayOriginatingAddress();
                    String message = NewMessage.getDisplayMessageBody();
                    if (isOtpMessege(message)) {
                        String otp = "demo";
                        otp = getOtp(message);
                        Intent intent1 = new Intent(context, OtpViewerDisplayService.class);
                        intent1.putExtra("Sender", phoneNumber);
                        intent1.putExtra("Messege", message);
                        intent1.putExtra("OTP", otp);
                        context.startService(intent1);
                    }
                }
            }
        } catch (Exception e) {

        }
    }
}
