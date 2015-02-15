#!/bin/bash
./bin/hdfs dfs -rm -r "/output/pageviews"
./bin/hadoop jar /vagrant/eclipse/LogAnalysis/target/LogAnalysis-1.0.0.jar polimi.camilo.hadoop.LogAnalysis.Pageviews /input /output/pageviews