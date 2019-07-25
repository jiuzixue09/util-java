package com.dave.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SerializeUtil {
	public static void serialize(String fileName, Object object) throws IOException {
		try(FileOutputStream fos = new FileOutputStream(fileName); ObjectOutputStream oos = new ObjectOutputStream(fos)){
			oos.writeObject(object);
			oos.flush();
		}
	}
	
	public static Object deserialize(String fileName) throws IOException, ClassNotFoundException{
		try(FileInputStream fis = new FileInputStream(fileName); ObjectInputStream ois = new ObjectInputStream(fis)){
			return ois.readObject();
		}
	}
	
	 /**  
     * 对象转数组  
     * @param obj  
     * @return  
	 * @throws IOException 
     */  
    public static byte[] toByteArray(Object obj) throws IOException {
        byte[] bytes;
        try(ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);        
            oos.flush();         
            bytes = bos.toByteArray();
        }    
        return bytes;    
    }   
       
    /**  
     * 数组转对象  
     * @param bytes  
     * @return  
     * @throws IOException 
     * @throws ClassNotFoundException 
     */  
    public static Object toObject(byte[] bytes) throws ClassNotFoundException, IOException {
        try(ByteArrayInputStream bis = new ByteArrayInputStream(bytes);ObjectInputStream ois = new ObjectInputStream(bis)) {
            return ois.readObject();
        }
    }
    
    


    public static <M> List<M> toListObject(byte[] in) throws IOException, ClassNotFoundException {  
        List<M> list = new ArrayList<>();

        Objects.requireNonNull(in,"Input value can't be null!!!");
        try(ByteArrayInputStream bis = new ByteArrayInputStream(in);ObjectInputStream ois = new ObjectInputStream(bis)) {
            while (bis.available() > 0) {
                M m = (M)ois.readObject();
                if (m == null) break;
                list.add(m);
            }
        }
        return  list;
    }  


    public static <M> byte[] toByteArrayList(Object value) throws IOException {
        Objects.requireNonNull(value,"Input value can't be null!!!");
          
        List<M> values = (List<M>) value;  
        byte[] results;
          
        try(ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(bos)) {  
            for (M m : values) {  
                oos.writeObject(m);  
            }  
            results = bos.toByteArray();  
        } 
          
        return results;  
    }  
    
    public static byte[] LongToBytes(long values) {  
        byte[] buffer = new byte[8]; 
        for (int i = 0; i < 8; i++) {   
             int offset = 64 - (i + 1) * 8;    
             buffer[i] = (byte) ((values >> offset) & 0xff); 
         }
        return buffer;  
   }

    public static long BytesToLong(byte[] buffer) {   
       long  values = 0;   
       for (int i = 0; i < 8; i++) {    
           values <<= 8; values|= (buffer[i] & 0xff);   
       }   
       return values;  
    } 
}
