import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static HashMap<String, String> userCredentials = new HashMap<>();
    static boolean loggedIn = false;
    static String currentUser = "";

    static int storageChoice = -1;

    public static void main(String[] args) {
        System.out.println("Welcome to the Weather App!");

        // Prompt user to choose storage type
        System.out.println("Please choose a storage type:");
        System.out.println("1. Text File");
        System.out.println("2. SQLite Database");
        System.out.print("Enter your choice (1 or 2): ");
        storageChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (storageChoice) {
            case 1:
                useTextFileStorage();
                break;
            case 2:
                useSQLiteDatabase();
                break;
            default:
                System.out.println("Invalid choice. Exiting program.");
                System.exit(1);
        }

        if (!loggedIn) {
            showLoginMenu();
        } else {
            showMainMenu();
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    //////////////////////////////INIT//////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    static void useTextFileStorage() {
        System.out.println("Using Text File Storage...");
        loadUserDataFromFile();
    }

    static void useSQLiteDatabase() {
        System.out.println("Using SQLite Database Storage...");
        initializeDatabase();
        loadUserDataFromDatabase();
    }

    static void initializeDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:user_data.db");
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS users (email TEXT PRIMARY KEY, password TEXT)");
            connection.close();
        } catch (SQLException e) {
            System.out.println("Error initializing SQLite database: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }



        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:weather_data.db");
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS weather_data (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "city_name TEXT," +
                    "country_code TEXT," +
                    "time_zone INTEGER," +
                    "sunrise_time INTEGER," +
                    "sunset_time INTEGER," +
                    "description TEXT," +
                    "feels_like REAL," +
                    "fetching_time INTEGER)");
            connection.close();
        } catch (SQLException e) {
            System.out.println("Error initializing SQLite database: " + e.getMessage());
        }
    }

    static void loadUserDataFromFile() {
        try {
            File file = new File("user_data.txt");
            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(",");
                // Assuming email is unique
                if (parts.length == 2) {
                    addUserCredentials(parts[0], parts[1]);
                }
            }
            fileScanner.close();
        } catch (IOException e) {
            System.out.println("Error loading user data from file: " + e.getMessage());
        }
    }

    static void loadUserDataFromDatabase() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:user_data.db");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users");
            while (resultSet.next()) {
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                addUserCredentials(email, password);
            }
            connection.close();
        } catch (SQLException e) {
            System.out.println("Error loading user data from database: " + e.getMessage());
        }
    }

    static void addUserCredentials(String email, String password) {
        // Add user credentials to memory
        // You may want to modify this method to store credentials based on storage type
        Main.userCredentials.put(email, password);
    }

    static void showLoginMenu() {
        System.out.println("Welcome to the Weather App!");

        while (!loggedIn) {
            System.out.println("\nLogin/Register Menu:");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.print("Please enter your choice (1-2): ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    register();
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1 for Login or 2 for Register.");
            }
        }
        showMainMenu();
    }

    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////AUTH////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    static void login() {
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        if (userCredentials.containsKey(email) && userCredentials.get(email).equals(password)) {
            System.out.println("Login successful!");
            loggedIn = true;
            currentUser = email;
        } else {
            System.out.println("Invalid email or password. Please try again.");
        }
    }

    static void register() {
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        if (userCredentials.containsKey(email)) {
            System.out.println("Email already exists. Please choose another email.");
            return;
        }
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();
        userCredentials.put(email, password);
        loggedIn = true;
        currentUser = email;
        System.out.println("Registration successful!");
    }

    static void showMainMenu() {
        System.out.println("Welcome to the Weather App!");

        while (true) {
            System.out.println("\nMain Menu:");
            System.out.println("1. View Weather by Long/Lat");
            System.out.println("2. View Weather by City/Country");
            System.out.println("3. Show Current Weather");
            System.out.println("4. Show 5 Days Forecast");
            System.out.println("5. Check Air Quality");
            System.out.println("6. Add Location");
            System.out.println("7. Exit");
            System.out.print("Please enter your choice (1-7): ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewWeatherByLongLat();
                    break;
                case 2:
                    viewWeatherByCityCountry();
                    break;
                case 3:
                    showCurrentWeather();
                    break;
                case 4:
                    showFiveDaysForecast();
                    break;
                case 5:
                    checkAirQuality();
                    break;
                case 6:
                    addLocation();
                    break;
                case 7:
                    if(storageChoice == 1){
                        saveUserDataToFile();
                    }
                    else{
                        saveUserDataToDatabase();
                    }
                    System.out.println("Thank you for using the Weather App. Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 7.");
            }
        }
    }


    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    //////////////////////////////VIEW BY LAT LONG//////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    static void viewWeatherByLongLat() {
        System.out.println("View Weather by Long/Lat:");
        System.out.println("1. View an already saved location");
        System.out.println("2. View a new location");
        System.out.print("Enter your choice (1 or 2): ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                viewSavedLocation();
                break;
            case 2:
                viewNewLocation();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }
    static void viewSavedLocation() {
        System.out.println();
        System.out.println("Save a location first");
        System.out.println();
    }
    static void viewNewLocation() {
        System.out.print("Enter latitude: ");
        double latitude = scanner.nextDouble();
        System.out.print("Enter longitude: ");
        double longitude = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        // Display weather details for the new location
        displayWeatherDetails(latitude, longitude);

        // Ask if the user wants to save the viewed location
        System.out.print("Do you want to save this location? (yes/no): ");
        String saveChoice = scanner.nextLine().toLowerCase();

        if (saveChoice.equals("yes")) {
            // Save the viewed location to the preferred storage
            saveLocationToStorage(latitude, longitude);
            System.out.println("Location saved successfully.");
        }
    }
    static void saveLocationToStorage(double latitude, double longitude) {
        if (storageChoice == 1) {
            saveToFile(latitude, longitude);
        } else if (storageChoice == 2) {
            saveToDatabase(latitude, longitude);
        } else {
            System.out.println("Invalid storage choice.");
        }
    }
    static void saveToFile(double latitude, double longitude) {
        try (FileWriter writer = new FileWriter("weather_data.txt", true)) {
            writer.write(latitude + "," + longitude + "\n");
        } catch (IOException e) {
            System.out.println("Error saving location to file: " + e.getMessage());
        }
    }
    static void saveToDatabase(double latitude, double longitude) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:weather_data.db");
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO saved_locations (latitude, longitude) VALUES (?, ?)");
            preparedStatement.setDouble(1, latitude);
            preparedStatement.setDouble(2, longitude);
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            System.out.println("Error saving location to database: " + e.getMessage());
        }
    }
    static void displayWeatherDetails(double latitude, double longitude) {
        try {
            URL url = new URL("https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=fb2e59cfc6024b27f5cd9bd935de4f5d");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse JSON response
                JSONObject jsonObject = new JSONObject(response.toString());
                String cityName = jsonObject.getString("name");
                String countryCode = jsonObject.getJSONObject("sys").getString("country");
                double temperature = jsonObject.getJSONObject("main").getDouble("temp");
                double feelsLike = jsonObject.getJSONObject("main").getDouble("feels_like");
                String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");

                // Display weather details
                System.out.println("Location: " + cityName + ", " + countryCode);
                System.out.println("Temperature: " + temperature + " K");
                System.out.println("Feels Like: " + feelsLike + " K");
                System.out.println("Description: " + description);
            } else {
                System.out.println("Error fetching weather data. Response code: " + responseCode);
            }
        } catch (IOException | JSONException e) {
            System.out.println("Error fetching weather data: " + e.getMessage());
        }
    }

    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    //////////////////////////////VIEW BY CITY-COUNTRY//////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    static void viewWeatherByCityCountry() {
        System.out.println("View Weather by City/Country:");
        System.out.println("1. Search by City");
        System.out.println("2. Search by Country Code");
        System.out.print("Enter your choice (1 or 2): ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                searchByCity();
                break;
            case 2:
                searchByCountryCode();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    static void searchByCity() {
        System.out.print("Enter city name: ");
        String cityName = scanner.nextLine();

        // Retrieve weather data from preferred storage for the given city
        List<WeatherData> weatherDataList = retrieveWeatherDataByCity(cityName);

        // Display weather data for the city
        displayWeatherData(weatherDataList);
    }

    static void searchByCountryCode() {
        System.out.print("Enter country code: ");
        String countryCode = scanner.nextLine();

        // Retrieve weather data from preferred storage for the given country code
        List<WeatherData> weatherDataList = retrieveWeatherDataByCountry(countryCode);

        // Display weather data for all cities in the country
        displayWeatherData(weatherDataList);
    }

    static List<WeatherData> retrieveWeatherDataByCity(String cityName) {
        List<WeatherData> weatherDataList = new ArrayList<>();
        if (storageChoice == 1) {
            weatherDataList = retrieveFromTextFile(cityName);
        } else if (storageChoice == 2) {
            weatherDataList = retrieveFromDatabase(cityName);
        } else {
            System.out.println("Invalid storage choice.");
        }
        return weatherDataList;
    }

    static List<WeatherData> retrieveWeatherDataByCountry(String countryCode) {
        List<WeatherData> weatherDataList = new ArrayList<>();
        if (storageChoice == 1) {
            weatherDataList = retrieveFromTextFile(countryCode);
        } else if (storageChoice == 2) {
            weatherDataList = retrieveFromDatabase(countryCode);
        } else {
            System.out.println("Invalid storage choice.");
        }
        return weatherDataList;
    }

    static List<WeatherData> retrieveFromTextFile(String searchKey) {
        List<WeatherData> weatherDataList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("weather_data.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 7) {
                    String cityName = parts[0];
                    String countryCode = parts[1];
                    int timeZone = Integer.parseInt(parts[2]);
                    long sunriseTime = Long.parseLong(parts[3]);
                    long sunsetTime = Long.parseLong(parts[4]);
                    String description = parts[5];
                    double feelsLike = Double.parseDouble(parts[6]);
                    long fetchingTime = Long.parseLong(parts[7]);
                    if (cityName.equals(searchKey) || countryCode.equals(searchKey)) {
                        WeatherData weatherData = new WeatherData(cityName, countryCode, timeZone, sunriseTime, sunsetTime, description, feelsLike, fetchingTime);
                        weatherDataList.add(weatherData);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading weather data from file: " + e.getMessage());
        }
        return weatherDataList;
    }

    static List<WeatherData> retrieveFromDatabase(String searchKey) {
        List<WeatherData> weatherDataList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:weather_data.db")) {
            String sql = "SELECT * FROM weather_data WHERE city_name = ? OR country_code = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, searchKey);
            statement.setString(2, searchKey);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String cityName = resultSet.getString("city_name");
                String countryCode = resultSet.getString("country_code");
                int timeZone = resultSet.getInt("time_zone");
                long sunriseTime = resultSet.getLong("sunrise_time");
                long sunsetTime = resultSet.getLong("sunset_time");
                String description = resultSet.getString("description");
                double feelsLike = resultSet.getDouble("feels_like");
                long fetchingTime = resultSet.getLong("fetching_time");
                WeatherData weatherData = new WeatherData(cityName, countryCode, timeZone, sunriseTime, sunsetTime, description, feelsLike, fetchingTime);
                weatherDataList.add(weatherData);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving weather data from database: " + e.getMessage());
        }
        return weatherDataList;
    }

    static void displayWeatherData(List<WeatherData> weatherDataList) {
        if (weatherDataList.isEmpty()) {
            System.out.println("No weather data found.");
            return;
        }
        for (WeatherData weatherData : weatherDataList) {
            System.out.println("City: " + weatherData.getCityName());
            System.out.println("Country Code: " + weatherData.getCountryCode());
            System.out.println("Time Zone: " + weatherData.getTimeZone());
            System.out.println("Sunrise Time: " + weatherData.getSunriseTime());
            System.out.println("Sunset Time: " + weatherData.getSunsetTime());
            System.out.println("Description: " + weatherData.getDescription());
            System.out.println("Feels Like: " + weatherData.getFeelsLike());
            System.out.println("Fetching Time: " + weatherData.getFetchingTime());
            System.out.println();
        }
    }

    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////


    static void showCurrentWeather() {
        System.out.println("To Check > Go To Check By Lat/Long > Check New Location...");
    }

    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////5-Day Weather Forecast//////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    static void showFiveDaysForecast() {
//        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter latitude: ");
            double lat = scanner.nextDouble();

            System.out.print("Enter longitude: ");
            double lon = scanner.nextDouble();

//            scanner.close();

            String apiKey = "10b4e9d130091b96f0775edc59e7ee11";
            URL url = new URL("https://api.openweathermap.org/data/2.5/forecast?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey);

            Scanner apiScanner = new Scanner(url.openStream());
            StringBuilder jsonBuilder = new StringBuilder();

            while (apiScanner.hasNext()) {
                jsonBuilder.append(apiScanner.nextLine());
            }

//            apiScanner.close();

            JSONObject json = new JSONObject(jsonBuilder.toString());
            JSONArray forecastList = json.getJSONArray("list");

            System.out.println("Showing 5 days forecast for latitude: " + lat + ", longitude: " + lon);
            System.out.println("------------------------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-20s%-20s%-20s%-30s%-20s%-20s%n", "Date/Time", "Temperature (C)", "Weather", "Description", "Wind Speed (m/s)", "Clouds (%)");
            System.out.println("------------------------------------------------------------------------------------------------------------------------------------");

            for (int i = 0; i < forecastList.length(); i++) {
                JSONObject forecast = forecastList.getJSONObject(i);
                String dateTime = forecast.getString("dt_txt");
                JSONObject main = forecast.getJSONObject("main");
                double temperature = main.getDouble("temp") - 273.15; // Convert Kelvin to Celsius
                JSONArray weatherArray = forecast.getJSONArray("weather");
                JSONObject weather = weatherArray.getJSONObject(0);
                String description = weather.getString("description");
                String mainWeather = weather.getString("main");
                double windSpeed = forecast.getJSONObject("wind").getDouble("speed");
                int cloudsPercentage = forecast.getJSONObject("clouds").getInt("all");

                System.out.printf("%-20s%-20.2f%-20s%-30s%-20.2f%-20d%n", dateTime, temperature, mainWeather, description, windSpeed, cloudsPercentage);
            }
        } catch (IOException | JSONException e) {
            System.out.println("Error fetching data: " + e.getMessage());
        }
    }

    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////Check Air Quality////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    static void checkAirQuality() {
        BufferedReader reader = null; // Initialize BufferedReader outside try block
        try {
            String line;
            StringBuilder responseContent = new StringBuilder();

            System.out.print("Please enter latitude:");
            String latitude = scanner.nextLine(); // Use nextLine() instead of readLine()

            System.out.print("Please enter longitude:");
            String longitude = scanner.nextLine(); // Use nextLine() instead of readLine()


            String apiKey = "7c1e8c40d3131a3931336096d8e2059f";
            String urlString = "http://api.openweathermap.org/data/2.5/air_pollution?lat=" + latitude + "&lon=" + longitude + "&appid=" + apiKey;

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Request setup
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int status = connection.getResponseCode();

            if (status > 299) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
            }

            // Close reader after reading response
            reader.close();

            // Parsing JSON response
            JSONObject jsonObject = new JSONObject(responseContent.toString());
            JSONArray list = jsonObject.getJSONArray("list");
            JSONObject airData = list.getJSONObject(0);
            JSONObject components = airData.getJSONObject("components");
            int aqi = airData.getJSONObject("main").getInt("aqi");

            // Displaying air quality information
            System.out.println("Air Quality Index (AQI): " + aqi);
            System.out.println("Pollutant concentrations:");

            System.out.println("Carbon Monoxide (CO): " + components.getDouble("co") + " µg/m³");
            System.out.println("Nitrogen Monoxide (NO): " + components.getDouble("no") + " µg/m³");
            System.out.println("Nitrogen Dioxide (NO2): " + components.getDouble("no2") + " µg/m³");
            System.out.println("Ozone (O3): " + components.getDouble("o3") + " µg/m³");
            System.out.println("Sulfur Dioxide (SO2): " + components.getDouble("so2") + " µg/m³");
            System.out.println("Fine Particulate Matter (PM2.5): " + components.getDouble("pm2_5") + " µg/m³");
            System.out.println("Coarse Particulate Matter (PM10): " + components.getDouble("pm10") + " µg/m³");
            System.out.println("Ammonia (NH3): " + components.getDouble("nh3") + " µg/m³");

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            // Ensure to close reader in finally block
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    //////////////////////////////ADD LOCATION//////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    static void addLocation() {

        System.out.print("Enter latitude: ");
        double latitude = scanner.nextDouble();

        System.out.print("Enter longitude: ");
        double longitude = scanner.nextDouble();

        scanner.nextLine();

        System.out.print("Fetching HTTPS Response: ");
        try {
            URL url = new URL("https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=fb2e59cfc6024b27f5cd9bd935de4f5d");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonObject = new JSONObject(response.toString());
                String cityName = jsonObject.getString("name");
                String countryCode = jsonObject.getJSONObject("sys").getString("country");
                long timeZone = jsonObject.getLong("timezone");
                long sunriseTime = jsonObject.getJSONObject("sys").getLong("sunrise");
                long sunsetTime = jsonObject.getJSONObject("sys").getLong("sunset");
                String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                double feelsLike = jsonObject.getJSONObject("main").getDouble("feels_like");
                long fetchingTime = System.currentTimeMillis();

                // Save data
                saveWeatherData(cityName, countryCode, timeZone, sunriseTime, sunsetTime, description, feelsLike, fetchingTime);
                System.out.println("Location added successfully.");
            } else {
                System.out.println("Error fetching weather data. Response code: " + responseCode);
            }
        } catch (IOException | JSONException e) {
            System.out.println("Error adding location: " + e.getMessage());
        }
    }
    static void saveWeatherData(String cityName, String countryCode, long timeZone, long sunriseTime, long sunsetTime, String description, double feelsLike, long fetchingTime) {
        if (storageChoice == 1) {
            saveToFile(cityName, countryCode, timeZone, sunriseTime, sunsetTime, description, feelsLike, fetchingTime);
        } else if (storageChoice == 2) {
            saveToDatabase(cityName, countryCode, timeZone, sunriseTime, sunsetTime, description, feelsLike, fetchingTime);
        } else {
            System.out.println("Invalid storage choice.");
        }
    }
    static void saveToFile(String cityName, String countryCode, long timeZone, long sunriseTime, long sunsetTime, String description, double feelsLike, long fetchingTime) {
        try (FileWriter writer = new FileWriter("weather_data.txt", true)) {
            writer.write(cityName + "," + countryCode + "," + timeZone + "," + sunriseTime + "," + sunsetTime + "," + description + "," + feelsLike + "," + fetchingTime + "\n");
        } catch (IOException e) {
            System.out.println("Error saving weather data to file: " + e.getMessage());
        }
    }
    static void saveToDatabase(String cityName, String countryCode, long timeZone, long sunriseTime, long sunsetTime, String description, double feelsLike, long fetchingTime) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:weather_data.db");
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO weather_data (city_name, country_code, time_zone, sunrise_time, sunset_time, description, feels_like, fetching_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            preparedStatement.setString(1, cityName);
            preparedStatement.setString(2, countryCode);
            preparedStatement.setLong(3, timeZone);
            preparedStatement.setLong(4, sunriseTime);
            preparedStatement.setLong(5, sunsetTime);
            preparedStatement.setString(6, description);
            preparedStatement.setDouble(7, feelsLike);
            preparedStatement.setLong(8, fetchingTime);
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            System.out.println("Error saving weather data to database: " + e.getMessage());
        }
    }


    static void saveUserDataToFile() {
        // Save user data to text file
        try {
            FileWriter writer = new FileWriter("user_data.txt");
            for (String email : userCredentials.keySet()) {
                writer.write(email + "," + userCredentials.get(email) + "\n");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving user data to file: " + e.getMessage());
        }
    }
    static void saveUserDataToDatabase() {
        // Save user data to SQLite database
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:user_data.db");
            Statement statement = connection.createStatement();
            statement.execute("DELETE FROM users"); // Clear existing data
            for (String email : userCredentials.keySet()) {
                String password = userCredentials.get(email);
                String query = "INSERT INTO users (email, password) VALUES ('" + email + "', '" + password + "')";
                statement.executeUpdate(query);
            }
            connection.close();
        } catch (SQLException e) {
            System.out.println("Error saving user data to database: " + e.getMessage());
        }
    }

    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
}

class WeatherData {
    private String cityName;
    private String countryCode;
    private int timeZone;
    private long sunriseTime;
    private long sunsetTime;
    private String description;
    private double feelsLike;
    private long fetchingTime;

    public WeatherData(String cityName, String countryCode, int timeZone, long sunriseTime, long sunsetTime, String description, double feelsLike, long fetchingTime) {
        this.cityName = cityName;
        this.countryCode = countryCode;
        this.timeZone = timeZone;
        this.sunriseTime = sunriseTime;
        this.sunsetTime = sunsetTime;
        this.description = description;
        this.feelsLike = feelsLike;
        this.fetchingTime = fetchingTime;
    }

    // Getters and setters
    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public int getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(int timeZone) {
        this.timeZone = timeZone;
    }

    public String getSunriseTime() {
        return formatTime(sunriseTime);
    }

    public void setSunriseTime(long sunriseTime) {
        this.sunriseTime = sunriseTime;
    }

    public String getSunsetTime() {
        return formatTime(sunsetTime);
    }

    public void setSunsetTime(long sunsetTime) {
        this.sunsetTime = sunsetTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getFeelsLike() {
        return feelsLike;
    }

    public void setFeelsLike(double feelsLike) {
        this.feelsLike = feelsLike;
    }

    public String getFetchingTime() {
        return formatTime(fetchingTime);
    }

    public void setFetchingTime(long fetchingTime) {
        this.fetchingTime = fetchingTime;
    }

    private String formatTime(long timestamp) {
        Date date = new Date(timestamp * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return sdf.format(date);
    }
}