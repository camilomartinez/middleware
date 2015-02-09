class hadoop {

exec { "download_hadoop":
command => "wget -O /tmp/hadoop.tar.gz http://apache.fastbull.org/hadoop/common/hadoop-2.6.0/hadoop-2.6.0.tar.gz",
path => $path,
user => $user,
group => $user,
unless => "ls ${install_dir} | grep hadoop-2.6.0",
require => Package["openjdk-6-jdk"]
}

exec { "unpack_hadoop" :
  command => "tar -zxf /tmp/hadoop.tar.gz -C /tmp",
  path => $path,
  user => $user,
  group => $user,
  creates => "${hadoop_home}-2.6.0",
  unless => "ls ${install_dir} | grep hadoop-2.6.0",
  require => Exec["download_hadoop"]
}

exec { "move_hadoop" :
  command => "mv /tmp/hadoop-2.6.0 ${install_dir}",
  path => $path,
  unless => "ls ${install_dir} | grep hadoop-2.6.0",
  require => Exec["unpack_hadoop"]
}
/*
file {
  "${hadoop_home}-2.6.0/etc/hadoop/slaves":
  content => template('hadoop/slaves'),
  mode => 644,
  owner => $user,
  group => $user,
  require => Exec["move_hadoop"]
 }
 
file {
  "${hadoop_home}-2.6.0/etc/hadoop/masters":
  content => template('hadoop/masters'),
  mode => 644,
  owner => $user,
  group => $user,
  require => Exec["move_hadoop"]
 }

file {
  "${hadoop_home}-2.6.0/etc/hadoop/yarn-site.xml":
  content => template('hadoop/yarn-site.xml'),
  mode => 644,
  owner => $user,
  group => $user,
  require => Exec["move_hadoop"]
}

*/

file {
  "${hadoop_home}-2.6.0/etc/hadoop/mapred-site.xml":
  content => template('hadoop/mapred-site.xml'),
  mode => 644,
  owner => $user,
  group => $user,
  require => Exec["move_hadoop"]
}
 
file {
  "${hadoop_home}-2.6.0/etc/hadoop/core-site.xml":
  content => template('hadoop/core-site.xml'),
  mode => 644,
  owner => $user,
  group => $user,
  require => Exec["move_hadoop"]
}
 
file {
  "${hadoop_home}-2.6.0/etc/hadoop/hdfs-site.xml":
  content => template('hadoop/hdfs-site.xml'),
  mode => 644,
  owner => $user,
  group => $user,
  require => Exec["move_hadoop"]
}

file {
  "${hadoop_home}-2.6.0/etc/hadoop/hadoop-env.sh":
  content => template('hadoop/hadoop-env.sh'),
  mode => 644,
  owner => $user,
  group => $user,
  require => Exec["move_hadoop"]
}

}
