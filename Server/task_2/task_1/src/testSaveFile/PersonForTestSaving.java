package testSaveFile;

import HandleStoreFiles.ForSaving;

import java.io.File;
import java.util.List;

public class PersonForTestSaving implements ForSaving {
    String id;
    String name;
    MySallary mySallary;
    public PersonForTestSaving(String id,String name,int mySallary){
        this.id=id;
        this.name=name;
        this.mySallary=new MySallary(mySallary);
    }
    public PersonForTestSaving(String id,String name0){
        this.id=id;
        this.name=name;
        this.mySallary=new MySallary(0);
    }


    public String getFolderName() {
        return getClass().getPackageName();
    }

    public String getFileName() {
        return getClass().getSimpleName()+id;
    }


    public String getData() {
        return id+","+name;
    }

    @Override
    public List<String> readFile(File f) {
        return List.of();
    }

    @Override
    public void parseFromFile() {

    }
}
