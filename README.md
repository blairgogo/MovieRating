# RBDA-17fall-Movie
Predictive analysis for movie ratings.

![MovieRating](https://nycdatascience.com/blog/wp-content/uploads/2016/08/Screen-Shot-2016-08-21-at-11.54.05-PM-1200x480.png)

## Data source
- [Kaggle TMDB 5000](https://www.kaggle.com/tmdb/tmdb-movie-metadata/data)
- [Douban Movie Short Comments](https://www.kaggle.com/utmhikari/doubanmovieshortcomments)
- MovieLens

## Dumbo HPC Environment:
**Java** 1.7.0_79

**Apache Spark MLlib** 1.6.0

**Apache Maven** 3.2.1

---

## Clean and Format Data

Run the DataETL MapReduce code on input data from [Kaggle TMDB 5000](https://www.kaggle.com/tmdb/tmdb-movie-metadata/data), the output is in [LIBSVM](https://www.csie.ntu.edu.tw/~cjlin/libsvmtools/datasets/) format. Rename the output file as "tmdb_5000_movies.txt".

e.g.

> 6.6	 1:29000000 2:19.237254 3:48902953 4:110 

6.6 is the rating the this movie, the value of 1 is budget of this movie in dollars, value of 2 is the popularity variable of the movie, value of 3 is the revenue of the movie in dollars, value of 4 is the duration of the movie in minutes. Any variable can be empty.

## Load data to HDFS

Go to folder ./RandomForest/input:

```language=bash
hdfs dfs -mkdir input
hdfs dfs -mv tmdb_5000_movies.txt input
```

## Run random forest regression

To create the Maven project package (the project already exists, no need to create new one): 
> /opt/maven/bin/mvn archetype:generate 

To build the Maven project, go to folder ./RandomForest: 

> /opt/maven/bin/mvn package 

To run the task on Spark, go to folder ./RandomForest/target: 

> spark-submit --class "RandomForestRegression" rbda-movie-1.0-SNAPSHOT.jar

Remember to put the input TXT file in HDFS under 'input' folder

