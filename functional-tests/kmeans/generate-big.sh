#!/bin/bash

scala -classpath ./target/sparkkmeans-0.0.0.jar GenerateInput /scratch/jmg3/spark-inputs/kmeans 100 100000 100
