package com.example.currencyconversionprovider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "cpp", value = "/cpp")
public class CPP extends HttpServlet {

    public static JSONObject makeRequestAboutConversionRate(String symbols, String baseCurrency) throws JSONException {

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

    public void init() {

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        // List of currencies that will be available
        List<String> availableCurrencies = Arrays.asList("USD", "EUR", "JPY", "GBP", "AUD", "CAD", "CHF", "HKD", "NZD", "SEK");

        // Hello
        PrintWriter out = response.getWriter();
        out.println("<html><style type=\"text/css\"> body { font-family: Arial,Helvetica,sans-serif; } table { border: outset 1pt; border-spacing: 0pt; } td,th { border: inset 1pt; } td { text-align: right; font-family: monospace; } .warn { color: blue; } .err { font-weight: bold; color: red; } </style><body>");

            out.println("<h1>Currency conversion provider</h1>");

            out.println("<form action=\"\">");

                out.println("<br><label for=\"input_currency\">Enter currency you want to convert:</label>");

                out.println("<select id=\"input_currency\" name=\"input_currency\">");
                    for (String temp : availableCurrencies) {
                        out.println("<option value=\"" + temp + "\">" + temp + "</option>");
                    }
                out.println("</select>");

                out.println("<br><br><label for=\"input_amount\">Enter amount you want to convert:</label>");

                out.println("<input type=\"number\" step=\"0.01\" id=\"input_amount\" name=\"input_amount\" value=100>");

                out.println("<br><br><label for=\"input_currency\">Enter currency you want to convert:</label>");

                out.println("<select id=\"receive_currency\" name=\"receive_currency\">");
                for (String temp : availableCurrencies) {
                    out.println("<option value=\"" + temp + "\">" + temp + "</option>");
                }
                out.println("</select>");

                out.println("<br><br><input type=\"submit\">");

            out.println("</form>");

        String input_currency = request.getParameter("input_currency");
        double input_amount = Double.parseDouble(request.getParameter("input_amount"));
        String receive_currency = request.getParameter("receive_currency");

        if (!input_currency.equals(receive_currency)){

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

            // Get conversion rates about user input currency to intermediary currency
            JSONObject inputToIntermediary;
            try {
                inputToIntermediary = makeRequestAboutConversionRate(stringBuilder.toString(), input_currency);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            // Table headers to be shown to user
            List<String> tableHeaderList = Arrays.asList("Intermediary currency", input_currency + " to Intermediary currency", "Intermediary currency to " + receive_currency, "PROFIT!");

            // Show visually table headers to user
            out.println("<table><tr>");
                for (String temp : tableHeaderList){
                    out.println("<th>" + temp + "</th>");
                }
            out.println("</tr>");

            // Variables to find best of category
            String bestForCustomerString = null;
            Double bestForCustomerValue = 0.0;
            String bestForServiceProviderString = null;
            Double bestForServiceProviderValue = 0.0;

            // Loop through available currency
            for (String temp : availableCurrencies) {

                // Not interested in user input currencies
                if (!temp.equals(receive_currency) && !temp.equals(input_currency)){

                    // Temporary currency variable to save found data
                    Currency tempCurrency = new Currency(temp);

                    // User input currency to intermediary with rate times amount
                    try {
                        tempCurrency.setUserToInter(inputToIntermediary.getJSONObject("rates").getDouble(temp) * input_amount);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    // Intermediary currency to user desired currency with rate times intermediary currency
                    try {
                        tempCurrency.setInterToUser(makeRequestAboutConversionRate(receive_currency, temp).getJSONObject("rates").getDouble(receive_currency) * tempCurrency.getUserToInter());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    //  Straight conversion form user input to receive minus intermediary currency
                    try {
                        tempCurrency.setProfit(makeRequestAboutConversionRate(receive_currency, input_currency).getJSONObject("rates").getDouble(receive_currency) * input_amount - tempCurrency.getInterToUser());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    // Show visually to the user calculated numbers
                    out.println("<tr>");
                    for (Object tmp : tempCurrency.list().toArray()){
                        out.println("<td>" + tmp + "</td>");
                    }
                    out.println("</tr>");

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

            // End table
            out.println("</table>");

            // Show visually best result for customer
            out.println("<br><br>Best for customer: " + bestForCustomerString);

            if (bestForServiceProviderValue <= 0){
                out.println("<br><br>Best for service provider: " + "no profit can be made from intermediary currency");
            } else {
                out.println("<br><br>Best for service provider: " + bestForServiceProviderString);
            }

        } else {
            out.println("<h1><strong>Input and output currencies can not be the same!</strong></h1>");
        }


        out.println("");
        out.println("");
        out.println("");

        out.println("</body></html>");
    }

    public void destroy() {
    }
}