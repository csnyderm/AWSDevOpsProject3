variable "aws_region" {
  type    = string
  default = "us-east-1"
}

variable "team" {
  type = string
  default = "cuttlefish"
}

variable "vpc_id" {
  type = string
  default = "null"
}
/* 
# Add this to the main in order to get the correct vpc_id
module "eks" {
  source = "./modules/eks"
  
  vpc_id = module.vpc.vpc_id
}
*/

variable "cluster_name" {
  type = string
  default = "team-cuttlefish-cluster"
}

variable "subnet_ids" {
  type = list
  default = [1,2]
}

/* 
# Add this to the main in order to get the correct subnet_id
module "eks" {
  source = "./modules/eks"
  
  vpc_id = module.vpc.public_subnet_id
  # Maybe use private or add instead?
}
*/

variable "cni_version" {
  type = string
  default = "v1.16.0-eksbuild.1"
}

variable "kubeproxy_version" {
  type = string
  default = "v1.29.0-eksbuild.1"
}

variable "podidentity_version" {
  type = string
  default = "v1.12.0-eksbuild.1"
}

variable "coredns_version" {
  type = string
  default = "v1.11.1-eksbuild.4"
}

variable "observability_version" {
  type = string
  default = "v1.6.0-eksbuild.1"
}

variable "nodegroup_name" {
  type = string
  default = "team-cuttlefish-nodegroup"
}

variable "desired_nodes" {
  type = number
  description = "The desired number of nodes to have at any time"
  default = 2
}

variable "max_nodes" {
  type = number
  description = "The max number of nodes to have at any time"
  default = 3
}

variable "min_nodes" {
  type = number
  description = "The minimum number of nodes to have at any time"
  default = 1
}

variable "unavailable_nodes" {
  type = number
  description = "The number of nodes that can be unavailable when updating"
  default = 1
}