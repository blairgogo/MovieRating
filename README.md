# RBDA-17fall-Movie

Data source: [Kaggle TMDB 5000](https://www.kaggle.com/tmdb/tmdb-movie-metadata/data)

## Dumbo HPC Environment:
**Java** 1.7.0_79

**Apache Spark MLlib** 1.6.0

**Apache Maven** 3.2.1

---

## Load data to HDFS

```language=bash
hdfs dfs -mkdir hiveInput
hdfs dfs -mv tmdb_5000_movies.csv hiveInput
```

---

## Run random forest regression

To create the Maven project package: 
> mvn archetype:generate 

To build the Maven project: 

> mvn package 

To run the task on Spark: 

> spark-submit --class "RandomForestRegression" rbda-movie-1.0-SNAPSHOT.jar

Remember to put the input TXT file in HDFS under 'input' folder

The input file should be in [LIBSVM](https://www.csie.ntu.edu.tw/~cjlin/libsvmtools/datasets/) format

