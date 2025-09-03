package testSaveFile;

import HandleStoreFiles.ForSaving;
import HandleStoreFiles.HandleFiles;

import java.io.IOException;
import java.util.Arrays;

public class TestingHandleFiles  {

    public static void main(String[] args) throws IOException {

        PersonForTestSaving p1 = new PersonForTestSaving("1234","aaa",8);
        PersonForTestSaving p2 = new PersonForTestSaving("5432","bbb",78);
        PersonForTestSaving p3 = new PersonForTestSaving("9999","ccc",654);
        PersonForTestSaving p4 = new PersonForTestSaving("0090","ccdc",446545);

        HandleFiles handleFiles = new HandleFiles();
        handleFiles.saveFile(p3);
        handleFiles.saveFiles(Arrays.asList(p1,p2,p3,p4));
        System.out.println(handleFiles.getCSVDataRecursive(p3));
        handleFiles.deleteFile(p2);
        PersonForTestSaving p5 = new PersonForTestSaving("123456789","sucess BH!",4335353);
        handleFiles.saveCSV(p5, "id");
        PersonForTestSaving readP5 = new PersonForTestSaving("123456789","sucess BH!",4545);
        handleFiles.saveCSV(readP5, "123456789");
        String read = handleFiles.readCsv ("123456789.csv");
        System.out.println(read);


    }
}
