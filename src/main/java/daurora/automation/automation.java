package daurora.automation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class automation {
    public static List<ArrayList> parsedAttributes = new ArrayList<>(6);

    public static int databaseReader(String query) {
        parsedAttributes.clear();
        for (int i = 0; i < 6; i++) {
            parsedAttributes.add(new ArrayList());
        }
        try{

            //TODO: Update database connection info
            Connection con = DriverManager.getConnection(
                    "INSERT YOUR DATABASE CONNECTION INFORMATION HERE");
            Statement stmt=con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            ResultSetMetaData metadata = rs.getMetaData();
            while (rs.next()) {
                for (int i = 1; i <= metadata.getColumnCount(); i++) {
                    String columnValue = rs.getString(i);

                    if (i == 3 && !columnValue.equals("ie"))
                    {
                        columnValue = columnValue.substring(0,1).toUpperCase() + columnValue.substring(1);
                    }

                    if (columnValue.equals("ie"))
                    {
                        columnValue = columnValue.toUpperCase();

                    }


                    parsedAttributes.get(i-1).add(columnValue);
                }
            }


        }catch(SQLException ex){         System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return parsedAttributes.get(0).size();

    }


    public static String testGenerator(int numParallel, String buildName, int queueSize, int timeoutTime) {

        int totalProcesses = numParallel + queueSize;

        //TODO: Update BrowserStack Credentials
        final String USERNAME = "INSERT YOUR BROWSERSTACK USERNAME HERE";
        final String AUTOMATE_KEY = "INSERT YOUR BROWSERSTACK ACCESS KEY HERE";

        if (parsedAttributes.get(0).size() != 0)
        {
            StringBuilder testFile = new StringBuilder( "import java.net.MalformedURLException;\n" +
                    "import java.net.URL;\n" +
                    "import java.util.Hashtable;\n" +
                    "import java.util.Iterator;\n" +
                    "import java.util.Set;\n" +
                    "import java.util.concurrent.TimeUnit;\n" +
                    "import org.openqa.selenium.By;\n" +
                    "import org.openqa.selenium.JavascriptExecutor;\n" +
                    "import org.openqa.selenium.WebDriver;\n" +
                    "import org.openqa.selenium.WebElement;\n" +
                    "import org.openqa.selenium.remote.DesiredCapabilities;\n" +
                    "import org.openqa.selenium.remote.RemoteWebDriver;\n" +
                    "import org.openqa.selenium.support.ui.ExpectedConditions;\n" +
                    "import org.openqa.selenium.support.ui.WebDriverWait;\n");

            for (int i = 0; i < parsedAttributes.get(0).size(); i++)
            {
                testFile.append("class ");
                for (int j = 0; j < parsedAttributes.size(); j++)
                {
                    if(!parsedAttributes.get(j).get(i).toString().contains("null")) {
                        String temp = (String) parsedAttributes.get(j).get(i).toString().replace(" " , "_").replace(".", "_");
                        testFile.append(temp).append("_");
                    }
                }
                testFile.deleteCharAt(testFile.length()-1);
                testFile.append(" implements Runnable {\n" +
                        "\tpublic void run() {\n" +
                        "\t\tHashtable<String, String> capsHashtable = new Hashtable<String, String>();\n\t\t");

                if(parsedAttributes.get(0).get(i).toString().contains("Windows") || parsedAttributes.get(0).get(i).toString().contains("OS X")) {
                    testFile.append("capsHashtable.put(" + '"' + "os" + '"' + ", " + '"' + parsedAttributes.get(0).get(i) + '"' + ");\n\t\t" );
                    testFile.append("capsHashtable.put(" + '"' + "browser" + '"' + ", " + '"' + parsedAttributes.get(2).get(i) + '"' + ");\n\t\t" );
                    testFile.append("capsHashtable.put(" + '"' + "browser_version" + '"' + ", " + '"' + parsedAttributes.get(4).get(i) + '"' + ");\n\t\t" );



                }
                if(parsedAttributes.get(0).get(i).toString().contains("android") || parsedAttributes.get(0).get(i).toString().contains("ios")) {
                    testFile.append("capsHashtable.put(" + '"' + "browserName" + '"' + ", " + '"' + parsedAttributes.get(0).get(i) + '"' + ");\n\t\t" );
                    testFile.append("capsHashtable.put(" + '"' + "device" + '"' + ", " + '"' + parsedAttributes.get(3).get(i) + '"' + ");\n\t\t" );
                    testFile.append("capsHashtable.put(" + '"' + "real_mobile" + '"' + ", " + '"' + parsedAttributes.get(5).get(i) + '"' + ");\n\t\t" );


                }

                testFile.append("capsHashtable.put(" + '"' + "os_version" + '"' + ", " + '"' + parsedAttributes.get(1).get(i) + '"' + ");\n\t\t" );
                testFile.append("capsHashtable.put(" + '"' + "build" + '"' + ", " + '"' + buildName + '"' + ");\n\t\t" );


                int threadNumber = numParallel - (i % numParallel);
                testFile.append("capsHashtable.put(" + '"' + "name" + '"' + ", " + '"' + "Thread " + threadNumber + '"' + ");\n\t\t" );
                testFile.append("mainTestClass r" + threadNumber + " = new mainTestClass();\n\t\t" + "r" + threadNumber + ".executeTest(capsHashtable);\n" +
                        "  \t}\n" +
                        "}\n");

            }




            testFile.append("public class mainTestClass {\n" +
                    "\tpublic static final String USERNAME =" + USERNAME + ";\n" +
                    "\tpublic static final String AUTOMATE_KEY =" + AUTOMATE_KEY +  ";\n" +
                    "\tpublic static final String URL = \"https://\" + USERNAME + \":\" + AUTOMATE_KEY + \"@hub-cloud.browserstack.com/wd/hub\";\n" +
                    "\tpublic static void main(String[] args) throws Exception {\n");

            for (int i = 0; i < parsedAttributes.get(0).size(); i++) {
                int threadObjectNumber = i + 1;
                testFile.append("Thread object" + threadObjectNumber + " = new Thread(new ");
                for (int j = 0; j < parsedAttributes.size(); j++)
                {
                    if(!parsedAttributes.get(j).get(i).toString().contains("null")) {
                        String temp = parsedAttributes.get(j).get(i).toString().replace(" " , "_").replace(".", "_");
                        testFile.append(temp).append("_");
                    }
                }
                testFile.deleteCharAt(testFile.length()-1);
                testFile.append("());\n" +
                        "\t\tobject");
                testFile.append(threadObjectNumber + ".start();\n" +
                        "\t\t");

                if (i % totalProcesses - 1 == 1) {
                    testFile.append("Thread.sleep(" + timeoutTime*totalProcesses + ");\n\t\t");
                }
            }
            testFile.append("}\n" +
                    "  \tpublic void executeTest(Hashtable<String, String> capsHashtable) {\n" +
                    "\t\tString key;\n" +
                    "\t\tDesiredCapabilities caps = new DesiredCapabilities();\n" +
                    "\t\t// Iterate over the hashtable and set the capabilities\n" +
                    "\t\tSet<String> keys = capsHashtable.keySet();\n" +
                    "    \tIterator<String> itr = keys.iterator();\n" +
                    "    \twhile (itr.hasNext()) {\n" +
                    "       \t\tkey = itr.next();\n" +
                    "        \tcaps.setCapability(key, capsHashtable.get(key));\n" +
                    "    \t}\n" +
                    "    \tWebDriver driver;\n" +
                    "\t\ttry {\n\n\n} catch (MalformedURLException e) {\n" +
                    "\t\t\te.printStackTrace();\n" +
                    "\t\t}\n" +
                    "\t}\n" +
                    "}");
            return testFile.toString();
        }
    else
        {
            StringBuilder testFile = new StringBuilder("Your request yielded no results");
            return testFile.toString();
        }
    }

}

