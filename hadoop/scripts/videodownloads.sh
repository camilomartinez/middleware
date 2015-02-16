#!/bin/bash
./bin/hdfs dfs -rm -r "/output/video-downloads"
./bin/hadoop jar /vagrant/eclipse/LogAnalysis/target/LogAnalysis-1.0.0.jar polimi.camilo.hadoop.LogAnalysis.VideoDownloads /input /output/video-downloads