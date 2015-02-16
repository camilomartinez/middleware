#!/bin/bash
./bin/hdfs dfs -rm -r "/output/referrals"
./bin/hadoop jar /vagrant/eclipse/LogAnalysis/target/LogAnalysis-1.0.0.jar polimi.camilo.hadoop.LogAnalysis.Referrals /input /output/referrals