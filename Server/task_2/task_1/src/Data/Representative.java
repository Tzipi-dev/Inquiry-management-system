package Data;

import HandleStoreFiles.HandleFiles;

import java.io.Serializable;

public class Representative implements Serializable {
    private static int counter = 0;
    private String name;
    private int tz;
    private int representative_code;

    public Representative(String name, int tz) {
        Representative.setCounter(counter+1);
        this.representative_code = counter;
        this.name = name;
        this.tz = tz;
        HandleFiles.saveNextCodeVal(counter,"nextCodeVal-representatives.txt");
    }

    public static int getCounter() {
        return counter;
    }

    public static void setCounter(int counter) {
        Representative.counter = counter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTz() {
        return tz;
    }

    public void setTz(int tz) {
        this.tz = tz;
    }

    public int getRepresentative_code() {
        return representative_code;
    }

    public void setRepresentative_code(int representative_code) {
        this.representative_code = representative_code;
    }
}
