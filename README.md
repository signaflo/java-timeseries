Java Time Series
===============
Time series analysis in Java.

##NOTICE##
-------
Major API changes have been made with release 0.3. The first and most obvious
is that the single java-timeseries project has been split up into two
seperate modules -- math and timeseries. The timeseries module contains
the time series data types, models, and forecasts. The math module
contains much of the lower-level supporting structure and is likely to
be of less interest to library users.

The arima package has undergone a lot of refactoring and some structural
changes. One critical change worth noting is that the arima classes now
have the concept of a drift term, and differentiate between the mean
and the drift in certain special cases. If clarification is needed, don't
hesitate to create an issue for discussion.

For this release, time series linear regression model and forecast classes
have been added. However, these are unlikely to be very useful on their
own at this time, at least for forecasting purposes. The best you can
get out of them currently is a point forecast. For the next release,
the time series linear regression forecast class will implement the Forecast
interface, which will make it much more useful. 
 
Features
-------
* Seasonal ARIMA models.
* ARIMA forecasting and simulation.
* Random walk simulation and modeling.
* Time series statistics, moving averages, and aggregation.
* Simple, intuitive, and powerful time modeling.
* Autocorrelation function and ACF plot.

Using
------
#### Maven

```groovy
<dependency>
    <groupId>com.github.jrachiele</groupId>
    <artifactId>timeseries</artifactId>
    <version>0.3.0</version>
</dependency>
<dependency>
  <groupId>com.github.jrachiele</groupId>
  <artifactId>math</artifactId>
  <version>0.3.0></version>
```

#### Gradle
```groovy
compile 'com.github.jrachiele:timeseries:0.3.0'
compile 'com.github.jrachiele:math:0.3.0'
```

Credits
------
| Library | Category | License |
| ------- | -------- | ------- |
| [XChart](https://github.com/timmolter/XChart) | Graphing | [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0) |
| [Smile](https://github.com/haifengl/smile) | Distributions | [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0) |
