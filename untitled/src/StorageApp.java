import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StorageApp extends JFrame {
    private JComboBox<String> storageOptions;
    private JPanel mainPanel, sidebarPanel, loginPanel, loggedInPanel, addLocationPanel, checkAirQualityPanel, viewWeatherByLongLatPanel, viewWeatherByCityCountryPanel, showForecastPanel;
    private JTextField latitudeField, longitudeField, cityOrCountryField;
    private JTextField latitudeFieldForecast, longitudeFieldForecast;
    private JTextField usernameField;
    private JTextField latitudeFieldAQ, longitudeFieldAQ;
    private JTextField latitudeFieldWL, longitudeFieldWL;

    private JPasswordField passwordField;
    private JButton loginButton, registerButton, addLocationButton, checkAirQualityButton, viewWeatherByLongLatButton, viewWeatherButton, showForecastButton;
    private JLabel loggedInLabel;
    private String selectedStorage;

    public StorageApp() {
        setTitle("Storage App");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Storage options dropdown
        String[] storageOptionsArray = {"Text File Storage", "SQLite Storage"};
        storageOptions = new JComboBox<>(storageOptionsArray);
        storageOptions.setSelectedIndex(0);
        selectedStorage = storageOptionsArray[0]; // Initialize selectedStorage
        JPanel topPanel = new JPanel();
        JButton homeButton = new JButton("Home");
        topPanel.add(homeButton);
        topPanel.add(storageOptions);
        add(topPanel, BorderLayout.NORTH);

        // Login/Register panel
        loginPanel = new JPanel(new GridLayout(3, 2));
        usernameField = new JTextField();
        usernameField.setColumns(20);
        passwordField = new JPasswordField();
        passwordField.setColumns(20);
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        latitudeFieldAQ = new JTextField();
        longitudeFieldAQ = new JTextField();

        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);
        loginPanel.add(registerButton);


        addLocationPanel = new JPanel(new GridLayout(4, 2));
        latitudeField = new JTextField();
        longitudeField = new JTextField();
        longitudeField.setColumns(20);
        latitudeField.setColumns(20);
        addLocationButton = new JButton("Add Location");
        viewWeatherByLongLatButton = new JButton("View Weather");

        addLocationPanel.add(new JLabel("Latitude:"));
        addLocationPanel.add(latitudeField);
        addLocationPanel.add(new JLabel("Longitude:"));
        addLocationPanel.add(longitudeField);
        addLocationPanel.add(addLocationButton);

        checkAirQualityPanel = new JPanel(new GridLayout(3, 2));
        latitudeFieldAQ = new JTextField();
        longitudeFieldAQ = new JTextField();
        checkAirQualityButton = new JButton("Check Air Quality");

        checkAirQualityPanel.add(new JLabel("Latitude:"));
        checkAirQualityPanel.add(latitudeFieldAQ);
        checkAirQualityPanel.add(new JLabel("Longitude:"));
        checkAirQualityPanel.add(longitudeFieldAQ);
        checkAirQualityPanel.add(checkAirQualityButton);

        viewWeatherByLongLatPanel = new JPanel(new GridLayout(2, 2));
        latitudeFieldWL = new JTextField();
        longitudeFieldWL = new JTextField();
        viewWeatherByLongLatButton = new JButton("View Weather");


        viewWeatherByLongLatPanel = new JPanel(new GridLayout(2, 2));
        viewWeatherByLongLatPanel.add(new JLabel("Latitude:"));
        viewWeatherByLongLatPanel.add(latitudeFieldWL);
        viewWeatherByLongLatPanel.add(new JLabel("Longitude:"));
        viewWeatherByLongLatPanel.add(longitudeFieldWL);
        viewWeatherByLongLatPanel.add(viewWeatherByLongLatButton);

        viewWeatherByCityCountryPanel = new JPanel(new GridLayout(3, 2));
        cityOrCountryField = new JTextField();
        viewWeatherButton = new JButton("View Weather");

        viewWeatherByCityCountryPanel.add(new JLabel("Enter City/Country:"));
        viewWeatherByCityCountryPanel.add(cityOrCountryField);
        viewWeatherByCityCountryPanel.add(viewWeatherButton);

        showForecastPanel = new JPanel(new GridLayout(3, 2));
        latitudeFieldForecast = new JTextField();
        longitudeFieldForecast = new JTextField();
        showForecastButton = new JButton("Show Forecast");

        showForecastPanel.add(new JLabel("Latitude:"));
        showForecastPanel.add(latitudeFieldForecast);
        showForecastPanel.add(new JLabel("Longitude:"));
        showForecastPanel.add(longitudeFieldForecast);
        showForecastPanel.add(showForecastButton);

        // Logged-in panel
        loggedInPanel = new JPanel(new BorderLayout());
        sidebarPanel = new JPanel(new GridLayout(6, 1));
        JButton[] sidebarButtons = {
                new JButton("View Weather by Long/Lat"),
                new JButton("View Weather by City/Country"),
                new JButton("Show Current Weather"),
                new JButton("Show 5 Days Forecast"),
                new JButton("Check Air Quality"),
                new JButton("Add Location")
        };
        for (JButton button : sidebarButtons) {
            sidebarPanel.add(button);
        }
        loggedInLabel = new JLabel("Logged in as: ");
        loggedInPanel.add(loggedInLabel, BorderLayout.NORTH);
        loggedInPanel.add(sidebarPanel, BorderLayout.WEST);

        // Main panel
        mainPanel = new JPanel();
        mainPanel.add(loginPanel);

        addLocationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double latitude = Double.parseDouble(latitudeField.getText());
                double longitude = Double.parseDouble(longitudeField.getText());
                addLocation(latitude, longitude);
            }
        });

        checkAirQualityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double latitude = Double.parseDouble(latitudeFieldAQ.getText());
                double longitude = Double.parseDouble(longitudeFieldAQ.getText());
                checkAirQuality(latitude, longitude);
            }
        });

        viewWeatherByLongLatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double latitude = Double.parseDouble(latitudeFieldWL.getText());
                double longitude = Double.parseDouble(longitudeFieldWL.getText());
                viewWeatherByLongLat(latitude, longitude);
            }
        });

        viewWeatherButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cityOrCountry = cityOrCountryField.getText();
                viewWeatherByCityCountry(cityOrCountry);
            }
        });

        showForecastButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double latitude = Double.parseDouble(latitudeFieldForecast.getText());
                double longitude = Double.parseDouble(longitudeFieldForecast.getText());
                showForecast(latitude, longitude);
            }
        });

        for (JButton button : sidebarButtons) {
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String actionCommand = e.getActionCommand();
                    switch (actionCommand) {
                        case "Add Location":
                            mainPanel.removeAll();
                            mainPanel.add(addLocationPanel);
                            mainPanel.revalidate();
                            mainPanel.repaint();
                            break;
                        case "Check Air Quality":
                            mainPanel.removeAll();
                            mainPanel.add(checkAirQualityPanel);
                            mainPanel.revalidate();
                            mainPanel.repaint();
                            break;
                        case "View Weather by Long/Lat":
                            mainPanel.removeAll();
                            mainPanel.add(viewWeatherByLongLatPanel);
                            mainPanel.revalidate();
                            mainPanel.repaint();
                            break;
                        case "View Weather by City/Country":
                            mainPanel.removeAll();
                            mainPanel.add(viewWeatherByCityCountryPanel);
                            mainPanel.revalidate();
                            mainPanel.repaint();
                            break;
                        case "Show 5 Days Forecast":
                            mainPanel.removeAll();
                            mainPanel.add(showForecastPanel);
                            mainPanel.revalidate();
                            mainPanel.repaint();
                            break;
                        case "Show Current Weather":
                            JOptionPane.showMessageDialog(null, "To View Current Weather, Go To View By Lat/Long> New Location");
                            break;

                        default:
                            // Perform other actions based on the clicked button
                            break;
                    }
                }
            });
        }

        add(mainPanel, BorderLayout.CENTER);

        // Action Listeners
        storageOptions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedStorage = (String) storageOptions.getSelectedItem();
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (selectedStorage.equals("Text File Storage")) {
                    if (loginFromFile(username, password)) {
                        loggedInLabel.setText("Logged in as: " + username);
                        mainPanel.removeAll();
                        mainPanel.add(loggedInPanel);
                        mainPanel.revalidate();
                        mainPanel.repaint();
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid username or password.");
                    }
                } else if (selectedStorage.equals("SQLite Storage")) {
                    if (loginFromSQLite(username, password)) {
                        loggedInLabel.setText("Logged in as: " + username);
                        mainPanel.removeAll();
                        mainPanel.add(loggedInPanel);
                        mainPanel.revalidate();
                        mainPanel.repaint();
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid username or password.");
                    }
                }
            }
        });

        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (loggedInLabel.getText().contains("Logged In")) {
                    mainPanel.removeAll();
                    mainPanel.add(loggedInPanel);
                    mainPanel.revalidate();
                    mainPanel.repaint();
                } else {
                    mainPanel.removeAll();
                    mainPanel.add(loginPanel);
                    mainPanel.revalidate();
                    mainPanel.repaint();
                }
            }
        });


        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (selectedStorage.equals("Text File Storage")) {
                    registerToFile(username, password);
                } else if (selectedStorage.equals("SQLite Storage")) {
                    registerToSQLite(username, password);
                }
            }
        });
    }

    private boolean loginFromFile(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader("user_data.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(username) && parts[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private boolean loginFromSQLite(String username, String password) {
        String url = "jdbc:sqlite:user_data.db";
        String query = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private void registerToFile(String username, String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("user_data.txt", true))) {
            writer.write(username + "," + password);
            writer.newLine();
            writer.flush();
            JOptionPane.showMessageDialog(null, "Registration successful!");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void registerToSQLite(String username, String password) {
        String url = "jdbc:sqlite:user_data.db";
        String query = "INSERT INTO users (email, password) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Registration successful!");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void addLocation(double latitude, double longitude) {
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
                JOptionPane.showMessageDialog(null, "Location added successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "Error fetching weather data. Response code: " + responseCode);
            }
        } catch (IOException | JSONException ex) {
            JOptionPane.showMessageDialog(null, "Error adding location: " + ex.getMessage());
        }
    }

    private void saveWeatherData(String cityName, String countryCode, long timeZone, long sunriseTime, long sunsetTime, String description, double feelsLike, long fetchingTime) {
        if (selectedStorage.equals("Text File Storage")) {
            saveToFile(cityName, countryCode, timeZone, sunriseTime, sunsetTime, description, feelsLike, fetchingTime);
        } else if (selectedStorage.equals("SQLite Storage")) {
            saveToDatabase(cityName, countryCode, timeZone, sunriseTime, sunsetTime, description, feelsLike, fetchingTime);
        } else {
            JOptionPane.showMessageDialog(null, "Invalid storage choice.");
        }
    }

    private void saveToFile(String cityName, String countryCode, long timeZone, long sunriseTime, long sunsetTime, String description, double feelsLike, long fetchingTime) {
        try (FileWriter writer = new FileWriter("weather_data.txt", true)) {
            writer.write(cityName + "," + countryCode + "," + timeZone + "," + sunriseTime + "," + sunsetTime + "," + description + "," + feelsLike + "," + fetchingTime + "\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving weather data to file: " + e.getMessage());
        }
    }

    private void saveToDatabase(String cityName, String countryCode, long timeZone, long sunriseTime, long sunsetTime, String description, double feelsLike, long fetchingTime) {
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
            JOptionPane.showMessageDialog(null, "Error saving weather data to database: " + e.getMessage());
        }
    }

    private void checkAirQuality(double latitude, double longitude) {
        try {
            String apiKey = "7c1e8c40d3131a3931336096d8e2059f";
            String urlString = "http://api.openweathermap.org/data/2.5/air_pollution?lat=" + latitude + "&lon=" + longitude + "&appid=" + apiKey;

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Request setup
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int status = connection.getResponseCode();

            BufferedReader reader;
            if (status > 299) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }

            // Read response content
            StringBuilder responseContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseContent.append(line);
            }
            reader.close();

            // Parsing JSON response
            JSONObject jsonObject = new JSONObject(responseContent.toString());
            JSONArray list = jsonObject.getJSONArray("list");
            JSONObject airData = list.getJSONObject(0);
            JSONObject components = airData.getJSONObject("components");
            int aqi = airData.getJSONObject("main").getInt("aqi");

            // Displaying air quality information
            String message = "Air Quality Index (AQI): " + aqi + "\n";
            message += "Pollutant concentrations:\n";
            message += "Carbon Monoxide (CO): " + components.getDouble("co") + " µg/m³\n";
            message += "Nitrogen Monoxide (NO): " + components.getDouble("no") + " µg/m³\n";
            message += "Nitrogen Dioxide (NO2): " + components.getDouble("no2") + " µg/m³\n";
            message += "Ozone (O3): " + components.getDouble("o3") + " µg/m³\n";
            message += "Sulfur Dioxide (SO2): " + components.getDouble("so2") + " µg/m³\n";
            message += "Fine Particulate Matter (PM2.5): " + components.getDouble("pm2_5") + " µg/m³\n";
            message += "Coarse Particulate Matter (PM10): " + components.getDouble("pm10") + " µg/m³\n";
            message += "Ammonia (NH3): " + components.getDouble("nh3") + " µg/m³";

            JOptionPane.showMessageDialog(null, message);

        } catch (IOException | JSONException e) {
            JOptionPane.showMessageDialog(null, "Error checking air quality: " + e.getMessage());
        }
    }

    private void viewWeatherByLongLat(double latitude, double longitude) {
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
                long visibility = jsonObject.getLong("visibility");
                long sunriseTime = jsonObject.getJSONObject("sys").getLong("sunrise");
                long sunsetTime = jsonObject.getJSONObject("sys").getLong("sunset");
                long timeZone = jsonObject.getLong("timezone");
                double pressure = jsonObject.getJSONObject("main").getDouble("pressure");
                double humidity = jsonObject.getJSONObject("main").getDouble("humidity");
                double windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");
                double windDegree = jsonObject.getJSONObject("wind").getDouble("deg");
                double clouds = jsonObject.getJSONObject("clouds").getDouble("all");

                // Format sunrise and sunset time
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                java.util.Date sunriseDate = new java.util.Date(sunriseTime * 1000);
                java.util.Date sunsetDate = new java.util.Date(sunsetTime * 1000);
                String formattedSunriseTime = sdf.format(sunriseDate);
                String formattedSunsetTime = sdf.format(sunsetDate);

                // Display weather details
                String message = "Location: " + cityName + ", " + countryCode + "\n";
                message += "Temperature: " + temperature + " K\n";
                message += "Feels Like: " + feelsLike + " K\n";
                message += "Description: " + description + "\n";
                message += "Visibility: " + visibility + " meters\n";
                message += "Sunrise Time: " + formattedSunriseTime + "\n";
                message += "Sunset Time: " + formattedSunsetTime + "\n";
                message += "Timezone: " + timeZone + "\n";
                message += "Pressure: " + pressure + " hPa\n";
                message += "Humidity: " + humidity + "%\n";
                message += "Wind Speed: " + windSpeed + " m/s\n";
                message += "Wind Degree: " + windDegree + "°\n";
                message += "Cloudiness: " + clouds + "%";

                JOptionPane.showMessageDialog(null, message);
            } else {
                JOptionPane.showMessageDialog(null, "Error fetching weather data. Response code: " + responseCode);
            }
        } catch (IOException | JSONException e) {
            JOptionPane.showMessageDialog(null, "Error fetching weather data: " + e.getMessage());
        }
    }


    private void viewWeatherByCityCountry(String cityOrCountry) {
        List<WeatherData> weatherDataList = retrieveWeatherDataByCityCountry(cityOrCountry);
        displayWeatherData(weatherDataList);
    }

    private List<WeatherData> retrieveWeatherDataByCityCountry(String cityOrCountry) {
        List<WeatherData> weatherDataList = new ArrayList<>();
        if (selectedStorage.equals("Text File Storage")) {
            weatherDataList = retrieveFromTextFile(cityOrCountry);
        } else if (selectedStorage.equals("SQLite Storage")) {
            weatherDataList = retrieveFromDatabase(cityOrCountry);
        } else {
            JOptionPane.showMessageDialog(null, "Invalid storage choice.");
        }
        return weatherDataList;
    }

    // Implement the method to retrieve weather data from text file
    private List<WeatherData> retrieveFromTextFile(String searchKey) {
        List<WeatherData> weatherDataList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("weather_data.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 8) {
                    String cityName = parts[0];
                    String countryCode = parts[1];
                    int timeZone = Integer.parseInt(parts[2]);
                    long sunriseTime = Long.parseLong(parts[3]);
                    long sunsetTime = Long.parseLong(parts[4]);
                    String description = parts[5];
                    double feelsLike = Double.parseDouble(parts[6]);
                    long fetchingTime = Long.parseLong(parts[7]);
                    if (cityName.equalsIgnoreCase(searchKey) || countryCode.equalsIgnoreCase(searchKey)) {
                        WeatherData weatherData = new WeatherData(cityName, countryCode, timeZone, sunriseTime, sunsetTime, description, feelsLike, fetchingTime);
                        weatherDataList.add(weatherData);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return weatherDataList;
    }

    // Retrieve weather data from database
    private List<WeatherData> retrieveFromDatabase(String searchKey) {
        List<WeatherData> weatherDataList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:weather_data.db")) {
            String sql = "SELECT * FROM weather_data WHERE city_name LIKE ? OR country_code LIKE ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, "%" + searchKey + "%");
            statement.setString(2, "%" + searchKey + "%");
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
            e.printStackTrace();
        }
        return weatherDataList;
    }

    // Display weather data in a dialog
    private void displayWeatherData(List<WeatherData> weatherDataList) {
        StringBuilder message = new StringBuilder();
        if (weatherDataList.isEmpty()) {
            message.append("No weather data found.");
        } else {
            for (WeatherData weatherData : weatherDataList) {
                message.append("City: ").append(weatherData.getCityName()).append("\n");
                message.append("Country Code: ").append(weatherData.getCountryCode()).append("\n");
                message.append("Time Zone: ").append(weatherData.getTimeZone()).append("\n");
                message.append("Sunrise Time: ").append(weatherData.getSunriseTime()).append("\n");
                message.append("Sunset Time: ").append(weatherData.getSunsetTime()).append("\n");
                message.append("Description: ").append(weatherData.getDescription()).append("\n");
                message.append("Feels Like: ").append(weatherData.getFeelsLike()).append("\n");
                message.append("Fetching Time: ").append(weatherData.getFetchingTime()).append("\n\n");
            }
        }
        JOptionPane.showMessageDialog(null, message.toString());
    }

    private static class WeatherData {
        private final String cityName;
        private final String countryCode;
        private final int timeZone;
        private final long sunriseTime;
        private final long sunsetTime;
        private final String description;
        private final double feelsLike;
        private final long fetchingTime;

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

        public String getCityName() {
            return cityName;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public int getTimeZone() {
            return timeZone;
        }

        public String getSunriseTime() {
            return formatTime(sunriseTime);
        }

        public String getSunsetTime() {
            return formatTime(sunsetTime);
        }

        public String getDescription() {
            return description;
        }

        public double getFeelsLike() {
            return feelsLike;
        }

        public long getFetchingTime() {
            return fetchingTime;
        }

        private String formatTime(long timestamp) {
            Date date = new Date(timestamp * 1000L);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            return sdf.format(date);
        }
    }


    private void showForecast(double latitude, double longitude) {
        try {
            String apiKey = "10b4e9d130091b96f0775edc59e7ee11";
            URL url = new URL("https://api.openweathermap.org/data/2.5/forecast?lat=" + latitude + "&lon=" + longitude + "&appid=" + apiKey);

            Scanner apiScanner = new Scanner(url.openStream());
            StringBuilder jsonBuilder = new StringBuilder();

            while (apiScanner.hasNext()) {
                jsonBuilder.append(apiScanner.nextLine());
            }
            apiScanner.close();

            JSONObject json = new JSONObject(jsonBuilder.toString());
            JSONArray forecastList = json.getJSONArray("list");
            String placeName = json.getJSONObject("city").getString("name");

            // Create a JTextArea to display the forecast information
            JTextArea forecastTextArea = new JTextArea();
            forecastTextArea.setEditable(false);
            forecastTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

            // Append forecast information to the JTextArea
            forecastTextArea.append("Showing 5 days forecast for " + placeName + " (Latitude: " + latitude + ", Longitude: " + longitude + ")\n");
            forecastTextArea.append("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
            forecastTextArea.append(String.format("%-20s%-20s%-20s%-30s%-20s%-20s%n", "Date/Time", "Temperature (C)", "Weather", "Wind Speed (m/s)", "Clouds (%)", "Description"));
            forecastTextArea.append("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");

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

                forecastTextArea.append(String.format("%-20s%-20.2f%-20s%-20.2f%-20d%-30s%n", dateTime, temperature, mainWeather, windSpeed, cloudsPercentage, description));
            }

            // Create a JScrollPane to contain the JTextArea
            JScrollPane scrollPane = new JScrollPane(forecastTextArea);

            // Show the forecast information in a JOptionPane with a scrollable JTextArea
            JOptionPane.showMessageDialog(null, scrollPane, "5-Day Forecast", JOptionPane.PLAIN_MESSAGE);

        } catch (IOException | JSONException e) {
            JOptionPane.showMessageDialog(null, "Error fetching data: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new StorageApp().setVisible(true);
            }
        });
    }
}
