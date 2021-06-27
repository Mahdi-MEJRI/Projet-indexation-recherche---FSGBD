package fr.miage.fsgbd;

import java.io.*;
import java.util.Map;

public class IndexSerializer<Type>
{
    public IndexSerializer (Map index, String path)
    {
        try {
            FileOutputStream fichier = new FileOutputStream(path);
            ObjectOutputStream oos = new ObjectOutputStream(fichier);
            oos.writeObject(index);
            oos.flush();
            oos.close();
        }
        catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}
