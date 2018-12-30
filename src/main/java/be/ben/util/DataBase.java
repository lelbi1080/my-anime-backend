package be.ben.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class DataBase {


    private static String INSTRUCTIONS = new String();
    @Autowired
    private DataSource dataSource;


    public void resetDatabase(String fileName) throws SQLException {
        String s = new String();
        StringBuffer sb = new StringBuffer();

        try {
            //Get file from resources folder
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource(fileName).getFile());
            FileReader fr = new FileReader(file);
            // be sure to not have line starting with "--" or "/*" or any other non aplhabetical character

            BufferedReader br = new BufferedReader(fr);

            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
            br.close();

            // here is our splitter ! We use ";" as a delimiter for each request
            // then we are sure to have well formed statements
            String[] inst = sb.toString().split(";");
            Statement statement = dataSource.getConnection().createStatement();


            for (int i = 0; i < inst.length; i++) {
                // we ensure that there is no spaces before or after the request string
                // in order to not execute empty statements
                if (!inst[i].trim().equals("")) {
                    statement.executeUpdate(inst[i]);
                    System.out.println(">>" + inst[i]);
                }
            }

        } catch (Exception e) {
            System.out.println("*** Error : " + e.toString());
            System.out.println("*** ");
            System.out.println("*** Error : ");
            e.printStackTrace();
            System.out.println("################################################");
            System.out.println(sb.toString());
        }

    }


}