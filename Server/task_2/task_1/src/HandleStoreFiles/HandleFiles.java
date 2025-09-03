package HandleStoreFiles;

import Data.Complaint;
import Data.Inquiry;
import Exceptions.InquiryException;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HandleFiles {

   public void saveInquiry(Inquiry inquiry,File f) throws InquiryException {
       try {
           OutputStream file = new FileOutputStream(f, true);
           file.write((inquiry.getData() + "," + inquiry.getCreationDate() + "," + inquiry.getCode()).getBytes());
           if(inquiry instanceof Complaint)
               file.write((","+((Complaint)inquiry).getAssignedBranch()).getBytes());
           file.close();
       } catch (IOException e) {
           throw new InquiryException(inquiry.getCode());
       }
   }

    public void saveFile(ForSaving forSaving)  {
        try {
            File dir = new File(forSaving.getFolderName());
            dir.mkdir();
            File f = new File(getDirectoryPath(forSaving));
            f.createNewFile();
            if(forSaving instanceof Inquiry){
                saveInquiry((Inquiry)forSaving,f);
            }
            else{
            OutputStream file = new FileOutputStream(f);
            file.write(forSaving.getData().getBytes());}
        }catch(IOException | InquiryException e){
            e.printStackTrace();
        }
    }
    public void deleteFile(ForSaving forSaving){
        File path = new File(getDirectoryPath(forSaving));
        path.delete();
    }
    public void updateFile(ForSaving forSaving) throws IOException{
        saveFile(forSaving);
    }
    private String getFileName(ForSaving forSaving){
        return forSaving.getFileName();
    }
    public String getDirectoryPath(ForSaving forSaving){
        return forSaving.getFolderName()+"\\"+forSaving.getFileName()+".csv";
    }
    public void saveFiles(List<ForSaving> forSavingList)throws IOException{
        for(ForSaving f :forSavingList  ){
            saveFile(f);
        }
    }
    public static List<String> readFile(File f)throws IOException {
        List<String> lines = Files.readAllLines(f.toPath());
        List<String> allParts = new ArrayList<>();

        for (String line : lines) {
            String[] parts = line.split(",");
            allParts.addAll(Arrays.asList(parts));
        }
        return allParts;
    }
    public static void saveNextCodeVal(int nextCodeVal,String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(String.valueOf(nextCodeVal));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try (OutputStream f = new FileOutputStream(file)) {
                f.write((String.valueOf(nextCodeVal)).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public int readNextCodeVal(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            return (Integer.parseInt(line));
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
   public String getCSVDataRecursive(Object obj){
       if (obj == null) {return "";}
       StringBuilder all_fields = new StringBuilder();
       Class<?> clazz =obj.getClass();
       while(clazz!=null){
           Field[] fields = clazz.getDeclaredFields();
           for(Field field : fields){
               try {
                   // בדוק אם השדה הוא סטטי
                   if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                       continue; // דלג על שדות סטטיים
                   }
                   field.setAccessible(true); // הגדר את השדה כנגיש
                   // יצירת שם המתודה המתאימה
                   String getterName = "get" + capitalize(field.getName());
                   Method getter = clazz.getMethod(getterName);

                   // קריאה למתודת ה-get
                   Object value = getter.invoke(obj);
                   //field.setAccessible(true);
                   //Object value =field.get(obj);
                  if (value != null) {
                      if (value instanceof LocalDateTime) {
                          //all_fields.append(field.getName()).append(" : ").append(value.toString()).append(",");
                          all_fields.append((value.toString())+",");
                      } else {
                        //  all_fields.append(field.getName()).append(" : ").append(value).append(",");
                          all_fields.append(value+",");
                      }
                       //all_fields.append(field.getName()).append(" : ").append(value).append(",");
                  }
               if (!(value instanceof String) && !field.getType().isPrimitive()){
                   all_fields.append(getCSVDataRecursive(value));
               }
               } catch ( Exception e) {
                   System.err.println("Failed to access field: " + field.getName());
               }
           }
           clazz=clazz.getSuperclass();
       }
       return all_fields.toString();
    }
    public  boolean saveCSV(Object obj , String filePath){
       File file =new File(filePath+".csv");
       try(BufferedWriter writer =new BufferedWriter(new FileWriter(file)))
       {
           writer.write(getCSVDataRecursive(obj));
       } catch (IOException e) {
           throw new RuntimeException(e);
       }
       return true;
    }
   public String readCsv(String filePath){
       StringBuilder all_fields = new StringBuilder();
       String line;
       try(BufferedReader reader = new BufferedReader(new FileReader(filePath))){
           while((line=reader.readLine())!=null){
               String[] values = line.split(",");
               for (String value : values) {
                   all_fields.append(value).append(",");
               }
           }
       }
       catch ( IOException e){
           e.printStackTrace();
       }
       return all_fields.toString();
    }

}
