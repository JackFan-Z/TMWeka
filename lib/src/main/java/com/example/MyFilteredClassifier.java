package com.example;
/**
 * A Java class that implements a simple text classifier, based on WEKA.
 * To be used with MyFilteredLearner.java.
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

import com.example.util.MyUtils;
import com.example.vision.VisionUtil;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;

import weka.core.*;
import weka.core.FastVector;
import weka.classifiers.meta.FilteredClassifier;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class implements a simple text classifier in Java using WEKA.
 * It loads a file with the text to classify, and the model that has been
 * learnt with MyFilteredLearner.java.
 *
 * @author Jose Maria Gomez Hidalgo - http://www.esp.uem.es/jmgomez
 * @see MyFilteredLearner
 */
public class MyFilteredClassifier {

    /**
     * String that stores the text to classify
     */
//    String text;
    /**
     * Object that stores the instance.
     */
//    Instances instances;
    /**
     * Object that stores the classifier.
     */
    FilteredClassifier classifier;

    /**
     * This method loads the text to be classified.
     *
     * @param fileName The name of the file that stores the text.
     */
    public ArrayList<String> load(String fileName) {
        ArrayList<String> lines = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            System.out.println("===== Loaded text data: " + fileName + " =====");
            reader.close();
            //System.out.println(text);
        } catch (IOException e) {
            System.out.println("Problem found when reading: " + fileName);
        }
        return lines;
    }

    public ArrayList<String> loadResponseJson(String path) {
        ArrayList<String> lines = new ArrayList<>();
        AnnotateImageResponse annotateImageResponse = VisionUtil.readResponseJson(path);
        if (annotateImageResponse == null) {
            return lines;
        }

        System.out.print(VisionUtil.GetPrintOutString(annotateImageResponse).toString());
        List<EntityAnnotation> labels = annotateImageResponse.getTextAnnotations();
        if (labels != null) {
            for (int i = 0; i < labels.size(); i++ ) {
                EntityAnnotation label = labels.get(i);
                if (i == 0) {
                    //builder.append("Locale: ");
                    //builder.append(label.getLocale());
                    //builder.append("\n");
                }
                String [] arrayLines = label.getDescription().split("\n");
                for (String line : arrayLines) {
                    lines.add(line);
                }
                break;
            }
        }
        return lines;
    }

    /**
     * This method loads the model to be used as classifier.
     *
     * @param fileName The name of the file that stores the text.
     */
    public void loadModel(String fileName) {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
            Object tmp = in.readObject();
            classifier = (FilteredClassifier) tmp;
            in.close();
            System.out.println("===== Loaded model: " + fileName + " =====");
        } catch (Exception e) {
            // Given the cast, a ClassNotFoundException must be caught along with the IOException
            System.out.println("Problem found when reading: " + fileName);
        }
    }

    /**
     * This method creates the instance to be classified, from the text that has been read.
     */
    public Instances makeInstance(String line) {
        // Create the attributes, class and text
        FastVector fvNominalVal = new FastVector(2);
        fvNominalVal.addElement("spam");
        fvNominalVal.addElement("ham");
        Attribute attribute1 = new Attribute("class", fvNominalVal);
        Attribute attribute2 = new Attribute("text", (FastVector) null);
        // Create list of instances with one element
        FastVector fvWekaAttributes = new FastVector(2);
        fvWekaAttributes.addElement(attribute1);
        fvWekaAttributes.addElement(attribute2);
        Instances instances = new Instances("Test relation", fvWekaAttributes, 1);
        // Set class index
        instances.setClassIndex(0);
        // Create and add the instance
        DenseInstance instance = new DenseInstance(2);
        instance.setValue(attribute2, line);
        // Another way to do it:
        // instance.setValue((Attribute)fvWekaAttributes.elementAt(1), text);
        instances.add(instance);
        System.out.println("===== Instance created with reference dataset =====");
        System.out.println(instances);
        return instances;
    }

    /**
     * This method performs the classification of the instance.
     * Output is done at the command-line.
     */
    public void classify(Instances instances) {
        try {
            double pred = classifier.classifyInstance(instances.instance(0));
            System.out.println("===== Classified instance =====");
            System.out.println("Class predicted: " + instances.classAttribute().value((int) pred));
            //
            double [] predictionDistribution = classifier.distributionForInstance(instances.instance(0));
            System.out.printf("[0] %6.3f  [1]%6.3f\n", predictionDistribution[0], predictionDistribution[1]);
        } catch (Exception e) {
            System.out.println("Problem found when classifying the text");
        }
    }

    /**
     * Main method. It is an example of the usage of this class.
     *
     * @param args Command-line arguments: fileData and fileModel.
     */
    public static void main(String[] args) {

        String jsonFolder = "/Users/jackf/Downloads/TestImages/test_cloud_vision_output_tw/";
        ArrayList<String> jsonPaths = MyUtils.GetFileListEx(jsonFolder, new String[]{".json"});
        for (int i = 0; i < jsonPaths.size(); i++) {
            System.out.println(jsonPaths.get(i));
            if (i > 4) {
                break;
            }
        }

        MyFilteredClassifier classifier;
        classifier = new MyFilteredClassifier();
        classifier.loadModel(MyFilteredLearner.BASE_DIR + "my_model");

        //ArrayList<String> lines = classifier.load(MyFilteredLearner.BASE_DIR + "smstest.txt");
        ArrayList<String> lines = classifier.loadResponseJson(jsonPaths.get(0));

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            Instances instances = classifier.makeInstance(line);
            classifier.classify(instances);
        }
    }
}	