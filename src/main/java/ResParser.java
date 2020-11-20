import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Response;
import model.Result;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ResParser {
    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        List<Result> results = new ArrayList<Result>();
        String postback = "";

        while (results.size() < 100) {
            URL url = new URL(String.format("https://gpsfront.aliexpress.com/getRecommendingResults.do?" +
                    "widget_id=5547572&limit=25&offset=%d&postback=%s", results.size(), postback));

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("accept", "application/json");
            InputStream responseStream = connection.getInputStream();

            Response response = mapper.readValue(responseStream, Response.class);
            postback = response.getPostback();
            results.addAll(response.getResults());
        }

        FileWriter csvWriter = new FileWriter("report.csv");

        csvWriter.append("productId,sellerId,productTitle,minPrice,maxPrice,discount,productPositiveRate,productAverageStar\n");
        for (Result result : results)
            csvWriter.append(result.toCSVRow());
        
        csvWriter.flush();
        csvWriter.close();
    }
}
