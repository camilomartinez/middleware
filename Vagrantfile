# -*- mode: ruby -*-
# vi: set ft=ruby :

# Setup number of nodes
node_count = 2

Vagrant.configure(2) do |config|
  # Apply to all machines
  config.vm.box = "hashicorp/precise32"
  config.vm.provision :shell, path: "bootstrap.sh"
  # Machine specific
  (1..node_count).each do |i|
    node_name = "mpi-node-#{i}"
    config.vm.define node_name do |node|
      node.vm.provider :virtualbox do |vb|
        # VM name
        vb.name = node_name
      end
      # Networking
      #node.vm.network :private_network, ip: "192.168.33.#{10+i}"
      node.vm.network :public_network, ip: "192.168.1.#{100+i}"
      node.vm.hostname = node_name
    end    
  end
end