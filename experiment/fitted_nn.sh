#!/bin/bash
cd ../
java -cp Playmaker8:../QNeuralNets/bin:../QNeuralNets/lib/* EnvModel.PredatorModel.PredatorTester -m fitted_nn_conf.pbtxt
