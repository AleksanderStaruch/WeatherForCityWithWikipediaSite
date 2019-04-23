import org.json.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;

public class Service {
    private String country;
    String currency;

    public Service(String country) {
        this.country = country;
        try {
            URL oracle = new URL("https://fxtop.com/en/countries-currencies.php");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(oracle.openStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null){
                if(inputLine.contains(">"+country)){
                    inputLine = in.readLine();
                    this.currency=inputLine.split("> USD / ")[1].split("<")[0];
                }
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String get(String s) {
        StringBuilder respbuffer = new StringBuilder();
        try {
            URL url = new URL(s);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader respon = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String odpstring ;
            while ((odpstring = respon.readLine()) != null) {
                respbuffer.append(odpstring + "\n");
            }
            respon.close();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return respbuffer.toString();
    }

    public String getWeather(String city){
        String s = get("http://api.openweathermap.org/data/2.5/weather?q=" + city + "," + country + "&appid=b3ca5aedcc61e4af8f7f30b301dc50c8");

        JSONObject json = new JSONObject(s);
        JSONObject weather = (JSONObject) json.getJSONArray("weather").get(0);

        Double temp = Double.parseDouble(json.getJSONObject("main").get("temp").toString());

        return weather.get("main").toString() + " \n\r" + Math.floor((temp - 273.15) * 100) / 100 + " C";
    }

    public Double getRateFor(String kod_waluty){
        String s;
        if(kod_waluty.equals("EUR")){
            s = get("https://api.exchangeratesapi.io/latest?symbols="+currency+","+currency);
        }else{
            if(currency.equals("EUR")){
                s = get("https://api.exchangeratesapi.io/latest?symbols="+kod_waluty+","+kod_waluty);
            }else {
                s = get("https://api.exchangeratesapi.io/latest?symbols="+kod_waluty+","+currency);
            }
        }

        JSONObject json = new JSONObject(s);
        JSONObject rates = json.getJSONObject("rates");

        if(kod_waluty.equals("EUR") ){
            Double rate2=(Double)rates.get(currency);
            return 1/rate2;
        }else{
            if(currency.equals("EUR") ){
                Double rate1=(Double)rates.get(kod_waluty);
                return rate1;
            }else {
                Double rate1=(Double)rates.get(kod_waluty);
                Double rate2=(Double)rates.get(currency);
                return rate2/rate1;
            }
        }

    }

    public Double getNBPRate(){
        double NBP=0;
        if(country.equals("Poland")){
            NBP= 1.0;
        }else{
            try {
                URL oracle = new URL("http://www.nbp.pl/kursy/kursya.html");
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(oracle.openStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null){
                    if(inputLine.contains(currency)){
                        inputLine = in.readLine();
                        NBP= Double.parseDouble(inputLine.split(">")[1].split("<")[0].replace(',','.'));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return NBP;
    }

}
