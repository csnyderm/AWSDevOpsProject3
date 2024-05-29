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
  type = list(string)
  default = [1,2]
}

variable "cluster_auth_mode" {
  type = string
  default = "API_AND_CONFIG_MAP"
}

variable "cluster_security_group" {
  type = list(string)
  default = ["sg-0e9f6672dde28a9ea"]
}

/* 
# Add this to the main in order to get the correct subnet_id
module "eks" {
  source = "./modules/eks"
  
  vpc_id = module.vpc.public_subnet_id
  # Maybe use private or add instead?
}
*/

variable "student_principal" {
  type = string
  default = "arn:aws:iam::785169158894:role/aws-reserved/sso.amazonaws.com/AWSReservedSSO_Student_1462e2a20cbcb77f"
}

variable "eks_user_policy" {
  type = string
  default = "arn:aws:eks::aws:cluster-access-policy/AmazonEKSClusterAdminPolicy"
}

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
  default = "v1.2.0-eksbuild.1"
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

variable "node_disk_size" {
  type = string
  description = "How much EBS storage space is needed?"
  default = "20GB"
}

variable "node_instance_pricing" {
  type = string
  description = "The pricing model for the instances. Valid: ON_DEMAND, SPOT"
  default = "SPOT"
}

variable "node_instance_types" {
  type = list(string)
  description = "The instance types to utilize when creating nodes. Perferably, keep this to a single value"
  default = ["m5.large"]
}

variable "nodes_ssh_key" {
  type = string
  description = "The SSH key used to access Nodes"
  default = "csnyder-kp-k8"
}