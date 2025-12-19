package src;

import java.util.*;
import java.io.*;

public class HistoryManager {
    private ArrayList<String> entries = new ArrayList<>();
    private double memory = 0;

    public void add(String e) { entries.add(e); }
    public ArrayList<String> getAll() { return entries; }

    public void saveToFile(String filename) throws Exception {
        PrintWriter out = new PrintWriter(filename);
        for (String s : entries) out.println(s);
        out.close();
    }

    public void loadFromFile(String filename) throws Exception {
        entries.clear();
        Scanner sc = new Scanner(new File(filename));
        while (sc.hasNextLine()) entries.add(sc.nextLine());
        sc.close();
    }
}