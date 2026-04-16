package myfood.persistence;

import myfood.model.*;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.*;

public class PersistenceManager {

    private static final String DATA_FILE = "myfood_data.xml";

    public static void save(SistemaData data) {
        try (XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(DATA_FILE)))) {
            encoder.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar dados: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static SistemaData load() {
        File f = new File(DATA_FILE);
        if (!f.exists()) return new SistemaData();
        try (XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(DATA_FILE)))) {
            Object obj = decoder.readObject();
            if (obj instanceof SistemaData) return (SistemaData) obj;
            return new SistemaData();
        } catch (Exception e) {
            return new SistemaData();
        }
    }

    public static void delete() {
        File f = new File(DATA_FILE);
        if (f.exists()) f.delete();
    }
}
