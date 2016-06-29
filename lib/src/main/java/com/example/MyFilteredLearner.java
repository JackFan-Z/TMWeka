package com.example;
/**
 * A Java class that implements a simple text learner, based on WEKA.
 * To be used with MyFilteredClassifier.java.
 * WEKA is available at: http://www.cs.waikato.ac.nz/ml/weka/
 * Copyright (C) 2013 Jose Maria Gomez Hidalgo - http://www.esp.uem.es/jmgomez
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it for any purpose.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

import weka.core.Instances;
//import weka.core.tokenizers.WordTokenizer;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.classifiers.Evaluation;

import java.util.Random;

import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.converters.ArffLoader.ArffReader;

import java.io.*;

/**
 * This class implements a simple text learner in Java using WEKA.
 * It loads a text dataset written in ARFF format, evaluates a classifier on it,
 * and saves the learnt model for further use.
 * @author Jose Maria Gomez Hidalgo - http://www.esp.uem.es/jmgomez
 * @see MyFilteredClassifier
 */
public class MyFilteredLearner {

    public final static String BASE_DIR = "lib/src/main/java/com/example/";
    /**
     * Object that stores training data.
     */
    Instances trainData;
    /**
     * Object that stores the filter
     */
    StringToWordVector filter;
    /**
     * Object that stores the classifier
     */
    FilteredClassifier classifier;

/*    public void setMyWordTokenizer(StringToWordVector filter) {
        //Make a tokenizer
        WordTokenizer wt = new WordTokenizer();
        String delimiters = " \r\t\n.,;:\'\"()?!-><#$\\%&*+/@^_=[]{}|`~0123456789";
        wt.setDelimiters(delimiters);
        filter.setTokenizer(wt);
    }*/
    /**
     * This method loads a dataset in ARFF format. If the file does not exist, or
     * it has a wrong format, the attribute trainData is null.
     * @param fileName The name of the file that stores the dataset.
     */
    public void loadDataset(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            ArffReader arff = new ArffReader(reader);
            trainData = arff.getData();
            System.out.println("===== Loaded dataset: " + fileName + " =====");
            reader.close();
        } catch (IOException e) {
            System.out.println("Problem found when reading: " + fileName);
        }
    }

    /**
     * This method evaluates the classifier. As recommended by WEKA documentation,
     * the classifier is defined but not trained yet. Evaluation of previously
     * trained classifiers can lead to unexpected results.
     */
    public void evaluate() {
        try {
            trainData.setClassIndex(0);
            filter = new StringToWordVector();
            filter.setAttributeIndices("last");
          //  setMyWordTokenizer(filter);
            classifier = new FilteredClassifier();
            classifier.setFilter(filter);
            classifier.setClassifier(new NaiveBayes());
            Evaluation eval = new Evaluation(trainData);
            eval.crossValidateModel(classifier, trainData, 4, new Random(1));
            System.out.println(eval.toSummaryString());
            System.out.println(eval.toClassDetailsString());
            System.out.println("===== Evaluating on filtered (training) dataset done =====");
        } catch (Exception e) {
            System.out.println("Problem found when evaluating");
        }
    }

    /**
     * This method trains the classifier on the loaded dataset.
     */
    public void learn() {
        try {
            trainData.setClassIndex(0);
            filter = new StringToWordVector();
            filter.setAttributeIndices("last");
      //      setMyWordTokenizer(filter);
            classifier = new FilteredClassifier();
            classifier.setFilter(filter);
            classifier.setClassifier(new NaiveBayes());
            classifier.buildClassifier(trainData);
            // Uncomment to see the classifier
            // System.out.println(classifier);
            System.out.println("===== Training on filtered (training) dataset done =====");
        } catch (Exception e) {
            System.out.println("Problem found when training");
        }
    }

    /**
     * This method saves the trained model into a file. This is done by
     * simple serialization of the classifier object.
     * @param fileName The name of the file that will store the trained model.
     */
    public void saveModel(String fileName) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
            out.writeObject(classifier);
            out.close();
            System.out.println("===== Saved model: " + fileName + " =====");
        } catch (IOException e) {
            System.out.println("Problem found when writing: " + fileName);
        }
    }

    /**
     * Main method. It is an example of the usage of this class.
     * @param args Command-line arguments: fileData and fileModel.
     */
    public static void main(String[] args) {

        MyFilteredLearner learner;
        learner = new MyFilteredLearner();
        learner.loadDataset(BASE_DIR + "smsspam.small.arff");
        // Evaluation mus be done before training
        // More info in: http://weka.wikispaces.com/Use+WEKA+in+your+Java+code
        learner.evaluate();
        learner.learn();
        learner.saveModel(BASE_DIR + "my_model");
    }
}	