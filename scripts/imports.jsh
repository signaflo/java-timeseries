import java.io.*
import java.math.*
import java.net.*
import java.nio.file.*
import java.util.*
import java.util.concurrent.*
import java.util.function.*
import java.util.prefs.*
import java.util.regex.*
import java.util.stream.*

import math.stats.distributions.*;
import timeseries.*;
import timeseries.models.*;
import timeseries.models.arima.*;
import timeseries.models.regression.*;

import static data.DoubleFunctions.*;
import static math.operations.Operators.*;
import static data.visualization.Plots.*;
import static timeseries.TestData.*;
import static timeseries.models.arima.Arima.*;
import static timeseries.models.arima.ModelOrder.*;
import static math.stats.Statistics.*;
