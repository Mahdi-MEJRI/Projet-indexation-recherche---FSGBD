package fr.miage.fsgbd;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

import java.util.Map;

public class IndexDeserializer
{
    public Map getIndex(String path)
    {
        Map index = null;
        try {

            FileInputStream fichier = new FileInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(fichier);
            index = (Map) ois.readObject();

        }
        catch (java.io.IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return index;
    }

}