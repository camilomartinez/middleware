vagrant-hadoop-cluster
======================

Deploying hadoop in a virtualized cluster in simple steps

These are the files that support the blogpost http://cscarioni.blogspot.co.uk/2012/09/setting-up-hadoop-virtual-cluster-with.html

For using them.

Simply clone the repository, then

`gem install vagrant `

`vagrant box add lucid64 http://files.vagrantup.com/lucid64.box`


Maybe generate your own ssh-keygen pair of keys.. and replace them in the files id_rsa and id_rsa.pub in the modules/hadoop/files directory. Or for testing copy the provided `id_rsa` and `id_rsa.pub` into your `.ssh` directory.


`vagrant up`

then 

`vagrant ssh master`

`sudo su ubuntu -`

`cd /opt/hadoop-2.6.0`

`./bin/hdfs namenode -format`

`./sbin/start-dfs.sh`

verify namenode is working at http://localhost:50070

copy desired jar

execute it with

'./bin/hadoop jar customjars/LogAnalysis-1.0.0.jar polimi.camilo.hadoop.LogAnalysis.WordCount input output'
