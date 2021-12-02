package lab3.com.example.demo;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

public class SMS {
    private static final String ACCOUNT_SID = "AC088384fe5a644619e6991093ed038b2f";
    private static final String AUTH_TOKEN = "382ae831e4f7c67335f0448c172e580b";
    private static final String FROM_NUMBER = "+14088374565";

    public static void sendMessage(String myMessage ,String toPhoneNumber){
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message message = Message.creator(
                        new com.twilio.type.PhoneNumber(toPhoneNumber),   // to number
                        new com.twilio.type.PhoneNumber(FROM_NUMBER), // from number
                        myMessage)                                   // message
                .create();

        System.out.println(message.getSid());
    }
}
