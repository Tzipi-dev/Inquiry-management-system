package HandleStoreFiles;

import Data.Inquiry;

import java.io.File;
import java.util.List;

public interface ForSaving {

    public String getFolderName();
    public String getFileName();
    public String getData();
    public void parseFromFile();
    public List<String> readFile(File f);
}
