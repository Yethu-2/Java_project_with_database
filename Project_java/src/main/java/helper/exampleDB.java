package helper;

import java.io.File;
import java.sql.DriverManager;
import java.util.HashSet;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.Statement;

public class exampleDB {
    private static final String DATABASE = "jdbc:sqlite:dataset-Climate/population.db";
    private static final String CSV_FILE = "dataset-Climate/Population.csv";

    public static void main(String[] args) {
        try {
            Scanner lineScanner = new Scanner(new File(CSV_FILE));
            Connection connection = DriverManager.getConnection(DATABASE);

            String firstLine = lineScanner.nextLine();
            String[] originalColumnNames = firstLine.split(",");

            StringBuilder createTableQuery = new StringBuilder("CREATE TABLE IF NOT EXISTS Population(");
            HashSet<String> uniqueColumnNames = new HashSet<>();
            String[] modifiedColumnNames = new String[originalColumnNames.length];

            for (int j = 0; j < originalColumnNames.length; j++) {
                String columnName = originalColumnNames[j];

                // Handle column names that start with numbers
                String validColumnName = columnName;
                if (Character.isDigit(columnName.charAt(0))) {
                    validColumnName = "c" + columnName;
                }

                // Handle duplicate column names
                String uniqueColumnName = validColumnName;
                int i = 1;
                while (uniqueColumnNames.contains(uniqueColumnName)) {
                    uniqueColumnName = validColumnName + i;
                    i++;
                }
                uniqueColumnNames.add(uniqueColumnName);
                modifiedColumnNames[j] = uniqueColumnName;

                createTableQuery.append(uniqueColumnName).append(" TEXT, ");
            }

            // Remove the last comma before appending the closing parenthesis
            int lastCommaIndex = createTableQuery.lastIndexOf(",");
            if (lastCommaIndex >= 0) {
                createTableQuery.deleteCharAt(lastCommaIndex);
            }

            createTableQuery.append(")");

            Statement createTableStatement = connection.createStatement();
            createTableStatement.execute(createTableQuery.toString());

            while (lineScanner.hasNextLine()) {
                String line = lineScanner.nextLine();
                try (Scanner rowScanner = new Scanner(line)) {
                    rowScanner.useDelimiter(",");

                    StringBuilder query = new StringBuilder("INSERT into Population(");
                    for (String columnName : modifiedColumnNames) {
                        query.append("\"").append(columnName).append("\",");
                    }
                    query.setLength(query.length() - 1);
                    query.append(") VALUES (");

                    // ...

while (rowScanner.hasNext()){
    String data = rowScanner.next();
    // Replace single quotes in data with two single quotes to escape them
    String escapedData = data.replace("'", "''");
    query.append("'").append(escapedData).append("',");
}

// ...
                    query.setLength(query.length() - 1);
                    query.append(")");
                    Statement statement = connection.createStatement();
                    statement.execute(query.toString());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}