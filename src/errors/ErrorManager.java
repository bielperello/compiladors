package errors;

import java.util.ArrayList;
import java.util.List;

public class ErrorManager {
    private static final List<CompilerError> errors = new ArrayList<CompilerError>();

    public static void add(CompilerError error) {
        errors.add(error);
    }

    public static boolean hasErrors() {
        return !errors.isEmpty();
    }

    public static List<CompilerError> getErrors() {
        return errors;
    }

    public static void clear() {
        errors.clear();
    }

    public static void printErrors() {
        for(CompilerError error : errors) {
            System.out.println(error.toString());
        }
    }

    public static void writeToFile(String filename) {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(filename)) {
            for (CompilerError error : errors) {
                writer.println(error.toString());
            }
        } catch (Exception e) {
            System.err.println("No s'ha pogut escriure el fitxer d'errors: " + e.getMessage());
        }
    }

}
