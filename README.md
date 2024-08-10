# Forecaster

### How to run
- The project is set up with sbt. You can do "sbt run" in the main directory to start up the api.

### Endpoints
The main endpoint is `/forecast/<lat>/<long>` which will give you an output like:
```json
{
  "classification":"moderate",
  "shortForecast":"Mostly Cloudy"
}
```

Notes:
- Latitude and Longetitude must be kept to 4 digits past decimal.