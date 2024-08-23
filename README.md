
# UML to Java Source Code Converter

This program converts UML class diagrams in JSON format into Java source code, simplifying the transition from design to implementation.

## Features

- **Import UML JSON Files**: Load JSON files with UML class diagrams.
- **View Class Details**: Select and view classes from the UML diagram.
- **Export Java Code**: Generate and export Java code to your chosen location.

## How to Use

1. **Import JSON**: Click "Import" and select your UML JSON file.
2. **View Classes**: Choose a class from the dropdown to view its structure.
3. **Export Code**: Click "Export" to save the generated Java code.

## JSON Structure

- **Class Name**: `"name": "Person"`
- **Variables**: Defines class fields.
  ```json
  "variables": [
    [{"var_name": "name"}, {"type": "String"}, {"acs": "private"}]
  ]
  ```
- **Methods**: Defines class methods.
  ```json
  "methods": [
    [{"method_name": "sleep", "return_type": "void"}, {"acs": "public"}]
  ]
  ```
- **Relationships**: 
  - `"has-a"`: Composition relationships.
  - `"is-a"`: Inheritance relationships.

## Limitations

- **Annotations & Interfaces**: Not supported.
- **Complex Inheritance**: Only single inheritance is handled.
- **Static Members**: Not supported.
- **Access Modifiers**: All classes are assumed public.

## Authors

- **Zeyad** and **Abdurrahim**
