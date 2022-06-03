import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class Main {

    public static String askStringInputFormUser(String question, List currencies, String unwantedCurrency){

        // Scanner class object
        Scanner scanner = new Scanner(System.in);

        // Variable for saving the result
        String result;

        // Loop until desired result
        while (true) {

            // Show question visually
            System.out.println("\n" + question);

            // If scanner has detected input
            if (scanner.hasNext()) {

                // Save result with uppercase letters
                result = scanner.next().toUpperCase();

                // Check if currency is available
                if (currencies.contains(result)){

                    if (unwantedCurrency == null || !unwantedCurrency.equals(result)){

                        // Return the result
                        return result;

                    }
                }

                // Continue loop if input is not valid
                System.out.println("error: try again\n");
            }
        }
    }

    public static float askFloatInputFormUser(String question){

        // Scanner class object
        Scanner scanner = new Scanner(System.in);

        // Loop until desired result
        while (true) {

            // Show question visually
            System.out.println("\n" + question);

            // If scanner has detected input
            if (scanner.hasNextFloat()) {

                    // Return the result
                    return scanner.nextFloat();

            } else {

                // Clean scanner result
                scanner.next();

                // Continue loop if input is not valid
                System.out.println("error: try again\n");
            }
        }
    }

    public static JSONObject makeRequestAboutConversionRate(String symbols, String baseCurrency){

        // API URL with parameters
        URL url;
        try {
            url = new URL("https://api.apilayer.com/fixer/latest?symbols="+symbols+"&base="+baseCurrency);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        // Make single request
        HttpURLConnection http;
        try {
            http = (HttpURLConnection)url.openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Set headers
        http.setRequestProperty("apikey", "7f8APLdlFUVlE2avfv772hqtm2vjRS5I");
        http.setRequestProperty("Cache-Control", "must-revalidate");
        http.setRequestProperty("Pragma", "no-cache");
        http.setRequestProperty("Expires", "0");

        // Save http request input stream
        InputStreamReader inputStreamReader;
        try {
            inputStreamReader = new InputStreamReader(http.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Save input stream reader to buffered reader
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        // Necessary to append string to save request response
        StringBuilder stringBuffer = new StringBuilder();

        // Necessary to save readLine function value temporarily
        String tempLine;

        // Loop until all the necessary lines have been saved
        while (true){
            try {
                if ((tempLine = bufferedReader.readLine()) == null) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            stringBuffer.append(tempLine);
        }

        // Close connection
        http.disconnect();

        // Return the result
        return new JSONObject(stringBuffer.toString());

    }

    public static void main(String[] args) {

        // List of currencies that will be available
        List<String> availableCurrencies = Arrays.asList("USD", "EUR", "JPY", "GBP", "AUD", "CAD", "CHF", "HKD", "NZD", "SEK");

        // Show visually to the user
        System.out.println("Available currencies are:");
        System.out.println(availableCurrencies);

        // Ask the user about input currency
        String input_currency = askStringInputFormUser("Enter currency you want to convert:", availableCurrencies, null);

        // Ask the user about input amount
        float input_amount = askFloatInputFormUser("Enter amount you want to convert:");

        // Ask the user about 'receive' currency
        String receive_currency = askStringInputFormUser("Enter currency you want to receive:", availableCurrencies, input_currency);

        // Intermediary currencies have to be added to one string
        StringBuilder stringBuilder = new StringBuilder();

        // Loop through available currencies
        for (String temp : availableCurrencies) {

            // If the current currency does not equal any user inputs move on
            if (!temp.equals(receive_currency) && !temp.equals(input_currency)){

                // Append with string and separator
                stringBuilder.append(temp).append("%2C");
            }
        }

        // Delete 3 last characters as they are extra from for loop
        stringBuilder.delete(stringBuilder.length()-3, stringBuilder.length());

        // Inform user about the API request
        System.out.println("\nPlease wait for the API request results...");

        // Get conversion rates about user input currency to intermediary currency
        JSONObject inputToIntermediary = makeRequestAboutConversionRate(stringBuilder.toString(), input_currency);

        // Table headers to be shown to user
        List<String> tableHeaderList = Arrays.asList("Intermediary currency", input_currency + " to Intermediary currency", "Intermediary currency to " + receive_currency, "PROFIT!");

        // Formatter to separate values
        Formatter formatter = new Formatter();

        // Format setup
        formatter.format("%n%-40S%-40S%-40S%-40S", tableHeaderList.toArray());

        // Show visually table headers to user
        System.out.println(formatter);

        // Variables to find best of category
        String bestForCustomerString = null;
        float bestForCustomerValue = 0;
        String bestForServiceProviderString = null;
        float bestForServiceProviderValue = 0;

        // Loop through available currency
        for (String temp : availableCurrencies) {

            // Not interested in user input currencies
            if (!temp.equals(receive_currency) && !temp.equals(input_currency)){

                // Temporary currency variable to save found data
                Currency tempCurrency = new Currency(temp);

                // User input currency to intermediary with rate times amount
                tempCurrency.setUserToInter(inputToIntermediary.getJSONObject("rates").getFloat(temp) * input_amount);

                // Intermediary currency to user desired currency with rate times intermediary currency
                tempCurrency.setInterToUser(makeRequestAboutConversionRate(receive_currency, temp).getJSONObject("rates").getFloat(receive_currency) * tempCurrency.getUserToInter());

                //  Straight conversion form user input to receive minus intermediary currency
                tempCurrency.setProfit(makeRequestAboutConversionRate(receive_currency, input_currency).getJSONObject("rates").getFloat(receive_currency) * input_amount - tempCurrency.getInterToUser());

                // Show visually to the user calculated numbers
                Formatter tempFormatter = new Formatter();
                tempFormatter.format("%-40S%-40S%-40S%-40S", tempCurrency.list().toArray());
                System.out.println(tempFormatter);

                // Save results about a currency that is the best for the customer
                if (bestForCustomerString == null || bestForCustomerValue < tempCurrency.getInterToUser()){
                    bestForCustomerValue = tempCurrency.getInterToUser();
                    bestForCustomerString = tempCurrency.getName();

                }

                // Save results about a currency that is the best for the service provider
                if (bestForServiceProviderString == null || bestForServiceProviderValue < tempCurrency.getProfit()){
                    bestForServiceProviderValue = tempCurrency.getProfit();
                    bestForServiceProviderString = tempCurrency.getName();
                }
            }
        }

        // Show visually best result for customer
        System.out.println("\nBest for customer: " + bestForCustomerString);

        if (bestForServiceProviderValue <= 0){
            System.out.println("Best for service provider: " + "no profit can be made from intermediary currency");
        } else {
            System.out.println("Best for service provider: " + bestForServiceProviderString);
        }

    }
}
