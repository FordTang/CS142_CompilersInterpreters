package crux;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import crux.Token.Kind;

public class Compiler {
    public static String studentName = "Ford Tang";
    public static String studentID = "46564602";
    public static String uciNetID = "FordT1";
	
	public static void main(String[] args) throws IOException
	{
        String sourceFile = args[0];
        Scanner s = null;

        try {
            s = new Scanner(new FileReader(sourceFile));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error accessing the source file: \"" + sourceFile + "\"");
            System.exit(-2);
        }

        Token t = s.next();
        while (t.kind != Kind.EOF) {
                System.out.println(t);
                t = s.next();
        }
        System.out.println(t);
    }
}
