package ir.maktabsharif.util;

public class Policy {
    private static final Float finePerHourDelay = 1F; // will be subtracted from tradesman's rating.
    private static final Double shareOfTradesManFromPrice = 0.7D; //e.g. if the paid amount is 100$, only 70$ will be deposited to the tradesman's account.
    private static final Long emailConfirmationTokenValidationTimeInMinutes = 15L;
    private static final String verificationEmailsAreSentFrom="testformaktabproject@gmail.com"; //password: Maktab123456

    private static final String serverAddress="http://localhost:8080/";
    public static Double getShareOfTradesManFromPrice() {
        return shareOfTradesManFromPrice;
    }

    public static Float getFinePerHourDelay() {
        return finePerHourDelay;
    }
    public static Long getEmailConfirmationTokenValidationTimeInMinutes(){
        return emailConfirmationTokenValidationTimeInMinutes;
    }

    public static String getVerificationEmailsAreSentFrom(){
        return verificationEmailsAreSentFrom;
    }
    public static String getServerAddress(){
        return serverAddress;
    }

}
