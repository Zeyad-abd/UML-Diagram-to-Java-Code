/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package UMLtoJavaSource;

/**
 *
 * @author Zeyad
 */
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Utility class to convert JSON data into Java class source code.
 */
public class JSONToJavaClassConverter {

    // Set to track generated classes to avoid duplicates
    private static Set<String> generatedClasses = new HashSet<>();

    /**
     * Method to initiate the JSON to Java class conversion.
     * Reads JSON data from a file and generates Java class source code.
     * @param filePath The path of the JSON file
     * @return ArrayList containing arrays with class names and source code
     */
    public static ArrayList<ArrayList<String>> convertJSONToJavaClasses(String filePath) {
        ArrayList<ArrayList<String>> javaClasses = new ArrayList<>();
        try {
            // Read JSON file
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(filePath));

            // Generate Java class source code
            generateClassCode(jsonObject, javaClasses, 0);

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return javaClasses;
    }

    /**
     * Recursively generates Java class source code from JSON data.
     * @param jsonObject The JSON object representing a class.
     * @param javaClasses List to store generated Java class source code and class names.
     * @param indentLevel The current indentation level for formatting.
     */
    private static void generateClassCode(JSONObject jsonObject, ArrayList<ArrayList<String>> javaClasses, int indentLevel) {
        String indent = "\t".repeat(indentLevel);

        // Parse JSON data
        String className = (String) jsonObject.get("name");
        if (generatedClasses.contains(className)) {
            return; // Avoid regenerating the same class
        }
        generatedClasses.add(className);

        StringBuilder javaClassCode = new StringBuilder();
        JSONArray variablesArray = (JSONArray) jsonObject.get("variables");
        JSONArray methodsArray = (JSONArray) jsonObject.get("methods");
        JSONArray hasAArray = (JSONArray) jsonObject.get("has-a");
        JSONObject isAObject = (JSONObject) jsonObject.get("is-a");

        // Generate class header
        javaClassCode.append(indent).append("public class ").append(className);
        if (isAObject != null && !isAObject.isEmpty()) {
            String parentClassName = (String) isAObject.get("name");
            javaClassCode.append(" extends ").append(parentClassName);
        }
        javaClassCode.append(" {\n");

        // Add variables
        for (Object variableObj : variablesArray) {
            JSONArray variable = (JSONArray) variableObj;
            String varName = (String) ((JSONObject) variable.get(0)).get("var_name");
            String type = (String) ((JSONObject) variable.get(1)).get("type");
            String access = (String) ((JSONObject) variable.get(2)).get("acs"); // Access modifier
            javaClassCode.append(indent).append("\t").append(access).append(" ").append(type).append(" ").append(varName).append(";\n");
        }

        // Generate constructor
        javaClassCode.append(indent).append("\n\tpublic ").append(className).append("(");
        boolean hasParentConstructor = isAObject != null && !isAObject.isEmpty();
        if (hasParentConstructor) {
            javaClassCode.append(isAObject.get("name")).append(" parent, ");
        }
        for (int i = 0; i < variablesArray.size(); i++) {
            JSONArray variable = (JSONArray) variablesArray.get(i);
            String varName = (String) ((JSONObject) variable.get(0)).get("var_name");
            String type = (String) ((JSONObject) variable.get(1)).get("type");
            // Append parameter
            javaClassCode.append(type).append(" ").append(varName);
            if (i < variablesArray.size() - 1) {
                javaClassCode.append(", ");
            }
        }
        javaClassCode.append(") {\n");
        // Call parent constructor if applicable
        if (hasParentConstructor) {
            javaClassCode.append(indent).append("\t\tsuper(parent.getAge());\n");
        }
        // Assign parameters to fields
        for (Object variableObj : variablesArray) {
            JSONArray variable = (JSONArray) variableObj;
            String varName = (String) ((JSONObject) variable.get(0)).get("var_name");
            javaClassCode.append(indent).append("\t\tthis.").append(varName).append(" = ").append(varName).append(";\n");
        }
        javaClassCode.append(indent).append("\t}\n");

        // Generate methods
        for (Object methodObj : methodsArray) {
            JSONArray method = (JSONArray) methodObj;
            String methodName = (String) ((JSONObject) method.get(0)).get("method_name");
            String access = (String) ((JSONObject) method.get(1)).get("acs"); // Access modifier
            String returnType = (String) ((JSONObject) method.get(0)).get("return_type");
            javaClassCode.append(indent).append("\n\t").append(access).append(" ").append(returnType).append(" ").append(methodName).append("(");

            // Adding method parameters
            JSONArray methodVars = (JSONArray) ((JSONObject) method.get(2)).get("method_vars");
            for (int i = 0; i < methodVars.size(); i++) {
                JSONArray varObj = (JSONArray) methodVars.get(i);
                String varName = (String) ((JSONObject) varObj.get(0)).get("var_name");
                String type = (String) ((JSONObject) varObj.get(1)).get("type");
                javaClassCode.append(type).append(" ").append(varName);
                if (i < methodVars.size() - 1) {
                    javaClassCode.append(", ");
                }
            }
            javaClassCode.append(") {\n");
            javaClassCode.append(indent).append("\t\t// Implement method\n");
            javaClassCode.append(indent).append("\t}\n");
        }

        // Generate variables with getters and setters
        for (Object variableObj : variablesArray) {
            JSONArray variable = (JSONArray) variableObj;
            String varName = (String) ((JSONObject) variable.get(0)).get("var_name");
            String type = (String) ((JSONObject) variable.get(1)).get("type");

            // Generate getters
            javaClassCode.append(indent).append("\n\tpublic ").append(type).append(" get").append(capitalize(varName)).append("() {\n");
            javaClassCode.append(indent).append("\t\treturn ").append(varName).append(";\n");
            javaClassCode.append(indent).append("\t}\n");

            // Generate setters
            javaClassCode.append(indent).append("\n\tpublic void set").append(capitalize(varName)).append("(").append(type).append(" ").append(varName).append(") {\n");
            javaClassCode.append(indent).append("\t\tthis.").append(varName).append(" = ").append(varName).append(";\n");
            javaClassCode.append(indent).append("\t}\n");
        }

        javaClassCode.append(indent).append("}\n");

        // Store the generated class in the ArrayList
        ArrayList<String> classArray = new ArrayList<>();
        classArray.add(className);
        classArray.add(javaClassCode.toString());
        javaClasses.add(classArray);

        // Handle "has-a" relationships
        if (hasAArray != null) {
            for (Object hasAObj : hasAArray) {
                if (hasAObj != null && !((JSONObject) hasAObj).isEmpty()) {
                    generateClassCode((JSONObject) hasAObj, javaClasses, 0);
                }
            }
        }

        // Handle "is-a" relationship
        if (isAObject != null && !isAObject.isEmpty()) {
            generateClassCode(isAObject, javaClasses, 0);
        }
    }

    /**
     * Capitalizes the first letter of a string.
     * @param str The string to capitalize.
     * @return The capitalized string.
     */
    private static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}
