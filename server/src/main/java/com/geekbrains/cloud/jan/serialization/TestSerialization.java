package com.geekbrains.cloud.jan.serialization;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class TestSerialization {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
       /* TransferObject object = new TransferObject("tag", "message");
        System.out.println(object);*/
       /* ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("data/object.txt"));
        out.writeObject(object);*/

        ObjectInputStream in = new ObjectInputStream(new FileInputStream("data/object.txt"));
        TransferObject object = (TransferObject) in.readObject();
        System.out.println(object);
    }
}
