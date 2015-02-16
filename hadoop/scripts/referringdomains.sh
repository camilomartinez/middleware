#!/bin/bash
./bin/hdfs dfs -rm -r "/output/referring-domains"
./bin/hadoop jar /vagrant/eclipse/LogAnalysis/target/LogAnalysis-1.0.0.jar polimi.camilo.hadoop.LogAnalysis.ReferringDomains /input /output/referring-domains