import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.activations.Activation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class TextClassification {
    public static void main(String[] args) {
        // Define the neural network configuration
        MultiLayerNetwork model = new MultiLayerNetwork(new NeuralNetConfiguration.Builder()
                .updater(new Adam(0.001)) // Adam optimizer with learning rate of 0.001
                .list()
                .layer(0, new DenseLayer.Builder().nIn(2).nOut(128)
                        .activation(Activation.RELU) // Use Activation.RELU instead of "relu"
                        .build())
                .layer(1, new OutputLayer.Builder().nIn(128).nOut(1)
                        .lossFunction(LossFunctions.LossFunction.XENT) // Binary cross-entropy loss
                        .activation(Activation.SIGMOID) // Use Activation.SIGMOID for binary output
                        .build())
                .build());
        
        model.init();
        model.setListeners(new ScoreIterationListener(1)); // Shows score every 1 iteration

        // Create input and labels (2 samples)
        INDArray input = Nd4j.create(new double[][]{{1, 2}, {3, 4}});
        INDArray labels = Nd4j.create(new double[][]{{1}, {0}});
        DataSet dataSet = new DataSet(input, labels);
        
        // Normalize data
        DataNormalization normalizer = new NormalizerMinMaxScaler(0, 1);
        normalizer.fit(dataSet); // Fit to calculate scaling parameters
        normalizer.transform(dataSet); // Transform the input data to be between 0 and 1

        // Train the model
        ListDataSetIterator<DataSet> iterator = new ListDataSetIterator<>(Arrays.asList(dataSet), 1); // Batch size = 1
        model.fit(iterator, 10); // Train the model for 10 epochs

        // Evaluate the model on new data
        INDArray newInput = Nd4j.create(new double[][]{{5, 6}}); // New input data
        normalizer.transform(newInput); // Normalize new input data

        INDArray output = model.output(newInput); // Predict on new input

        // Save prediction to a file
        try (FileWriter writer = new FileWriter("prediction_output.txt")) { 
            writer.write("Prediction: " + output);
            System.out.println("Prediction saved to file: prediction_output.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
