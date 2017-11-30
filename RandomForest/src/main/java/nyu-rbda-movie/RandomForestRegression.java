/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// $example on$

import java.util.HashMap;
import java.util.Map;

import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.evaluation.RegressionEvaluator;
import org.apache.spark.ml.feature.VectorIndexer;
import org.apache.spark.ml.feature.VectorIndexerModel;
import org.apache.spark.ml.regression.RandomForestRegressionModel;

import org.apache.spark.ml.regression.RandomForestRegressor;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.tree.model.DecisionTreeModel;
import org.apache.spark.sql.DataFrame;
import scala.Tuple2;

import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.tree.RandomForest;
import org.apache.spark.mllib.tree.model.RandomForestModel;
import org.apache.spark.mllib.util.MLUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.SQLContext;

// $example off$

public class RandomForestRegression {

    public void randomForestModelTest() {
        // $example on$
        //run in self-contained mode
        SparkConf sparkConf = new SparkConf().setAppName("JavaRandomForestRegressionExample").setMaster("local[*]");
        JavaSparkContext jsc = new JavaSparkContext(sparkConf);

        // Load and parse the data file.
        String datapath = "./input/tmdb_5000_movies.txt";

        JavaRDD<LabeledPoint> data = MLUtils.loadLibSVMFile(jsc.sc(), datapath).toJavaRDD();

        // Split the data into training and test sets (20% held out for testing)
        JavaRDD<LabeledPoint>[] splits = data.randomSplit(new double[]{0.8, 0.2});
        JavaRDD<LabeledPoint> trainingData = splits[0];
        JavaRDD<LabeledPoint> testData = splits[1];

        // Set parameters.
        // Empty categoricalFeaturesInfo indicates all features are continuous.
        Map<Integer, Integer> categoricalFeaturesInfo = new HashMap<>();
        int numTrees = 5000; // Use more in practice.
        String featureSubsetStrategy = "auto"; // Let the algorithm choose.
        String impurity = "variance";
        int maxDepth = 4;
        int maxBins = 32;
        int seed = 12345;
        // Train a RandomForest model.
        final RandomForestModel model = RandomForest.trainRegressor(trainingData,
                categoricalFeaturesInfo, numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins, seed);

        // Evaluate model on test instances and compute test error
        JavaPairRDD<Double, Double> predictionAndLabel =
                testData.mapToPair(new PairFunction<LabeledPoint, Double, Double>() {
                    @Override
                    public Tuple2<Double, Double> call(LabeledPoint p) {
                        return new Tuple2<Double, Double>(model.predict(p.features()), p.label());
                    }
                });
        Double testMSE =
                predictionAndLabel.map(new Function<Tuple2<Double, Double>, Double>() {
                    @Override
                    public Double call(Tuple2<Double, Double> pl) {
                        Double diff = pl._1() - pl._2();
                        return diff * diff;
                    }
                }).reduce(new Function2<Double, Double, Double>() {
                    @Override
                    public Double call(Double a, Double b) {
                        return a + b;
                    }
                }) / testData.count();

        System.out.println("Test Mean Squared Error: " + testMSE);

        //System.out.println("Learned regression forest model:\n" + model.toDebugString());

        System.out.println("Summary of the model: " + model.toString());

        //get the variable imprtance


        //get all the decision trees from the model
        DecisionTreeModel[] trees = model.trees();
        System.out.println("The first decision tree in the forest: " + trees[0].toDebugString());

        //create a movie vector and use model to predict the rating
        Vector windRiver = Vectors.dense(11000000, 21, 40100000, 111);

        System.out.println("Predictive rating of <Wind River>: " + model.predict(windRiver));

        // Save and load
        model.save(jsc.sc(), "target/tmp/myRandomForestRegressionModel");
        RandomForestModel sameModel = RandomForestModel.load(jsc.sc(),
                "target/tmp/myRandomForestRegressionModel");
        // $example off$


        jsc.stop();
    }

    public void randomForestRegressionModel() {

        SparkConf conf = new SparkConf().setAppName("JavaRandomForestRegressorExample").setMaster("local[*]");
        JavaSparkContext jsc = new JavaSparkContext(conf);
        SQLContext sqlContext = new SQLContext(jsc);

        // Load and parse the data file, converting it to a DataFrame.
        DataFrame data = sqlContext.read().format("libsvm").load("./input/tmdb_5000_movies.txt");

        // Automatically identify categorical features, and index them.
        // Set maxCategories so features with > 4 distinct values are treated as continuous.
        VectorIndexerModel featureIndexer = new VectorIndexer()
                .setInputCol("features")
                .setOutputCol("indexedFeatures")
                .setMaxCategories(4)
                .fit(data);

        // Split the data into training and test sets (30% held out for testing)
        DataFrame[] splits = data.randomSplit(new double[]{0.7, 0.3});
        DataFrame trainingData = splits[0];
        DataFrame testData = splits[1];

        // Train a RandomForest model.
        RandomForestRegressor rf = new RandomForestRegressor()
                .setLabelCol("label")
                .setFeaturesCol("indexedFeatures");

        // Chain indexer and forest in a Pipeline
        Pipeline pipeline = new Pipeline()
                .setStages(new PipelineStage[]{featureIndexer, rf});

        // Train model.  This also runs the indexer.
        PipelineModel model = pipeline.fit(trainingData);

        // Make predictions.
        DataFrame predictions = model.transform(testData);

        // Select example rows to display.
        predictions.select("prediction", "label", "features").show(5);

        // Select (prediction, true label) and compute test error
        RegressionEvaluator evaluator = new RegressionEvaluator()
                .setLabelCol("label")
                .setPredictionCol("prediction")
                .setMetricName("rmse");
        double rmse = evaluator.evaluate(predictions);
        System.out.println("Root Mean Squared Error (RMSE) on test data = " + rmse);

        RandomForestRegressionModel rfModel = (RandomForestRegressionModel) (model.stages()[1]);

        System.out.println("Learned regression forest model:\n" + rfModel.toString());

        //get the variable importance from the model
        Vector importances = rfModel.featureImportances();
        System.out.println("The imprtance of each feature: " + importances.toString());


    }

    public static void main(String[] args) {

        RandomForestRegression test = new RandomForestRegression();
        test.randomForestRegressionModel();

    }
}
