package ir.maktabsharif.util;

import java.util.concurrent.Semaphore;

public class SemaphoreUtil {

    private SemaphoreUtil() {
    }

    private static final Semaphore newUserSemaphore = new Semaphore(1);
    private static final Semaphore newCategorySemaphore = new Semaphore(1);


    public static void acquireNewUserSemaphore() throws InterruptedException {
        newUserSemaphore.acquire();
    }

    public static void releaseNewUserSemaphore() {
        newUserSemaphore.release();
    }

    public static void acquireNewCategorySemaphore() throws InterruptedException {
        newCategorySemaphore.acquire();
    }
    public static void releaseNewCategorySemaphore(){
        newCategorySemaphore.release();
    }

}
