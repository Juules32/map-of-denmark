package Utils;

import java.io.Serializable;

public class Timer implements Serializable {
    long start = System.currentTimeMillis();

    public void printTimePassed(String message) {
        System.out.println(message);
        printTimePassed();
    }

    public void printTimePassed() {
        int secondsSinceStart = (int) (System.currentTimeMillis() - start)/1000;
        String minutesSinceStart = secondsSinceStart >= 60 ? 
        (secondsSinceStart/60) + " minutes and " : "";
        System.out.println("Action took: " + minutesSinceStart + (secondsSinceStart % 60) + " seconds.\n");
    }
}
