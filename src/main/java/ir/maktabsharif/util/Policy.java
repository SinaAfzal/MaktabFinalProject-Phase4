package ir.maktabsharif.util;

public class Policy {
    private static final Float finePerHourDelay=1F; // will be subtracted from tradesman's rating.
    private static final Double shareOfTradesManFromPrice=0.7D; //e.g. if the paid amount is 100$, only 70$ will be deposited to the tradesman's account.

    public static Double getShareOfTradesManFromPrice(){
        return shareOfTradesManFromPrice;
    }

    public static Float getFinePerHourDelay(){
        return finePerHourDelay;
    }

}
