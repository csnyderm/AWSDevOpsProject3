variable "aws_region" {
  type    = string
  default = "us-east-1"
}

variable "team" {
  type    = string
  default = "cuttlefish"
}

variable "vpc_id" {
  type    = string
  default = "null"
}

variable "cluster_name" {
  type    = string
  default = "team-cuttlefish-cluster"
}

#? Required value
variable "cluster_subnet_ids" {
  type = list(string)
}


variable "cluster_auth_mode" {
  type    = string
  default = "API_AND_CONFIG_MAP"
}
/*
variable "cluster_security_group" {
  type = list(string)
}*/

variable "cluster_role" {
  type = string
}

variable "cni_version" {
  type    = string
  default = "v1.16.0-eksbuild.1"
}

variable "kubeproxy_version" {
  type    = string
  default = "v1.29.0-eksbuild.1"
}

variable "podidentity_version" {
  type    = string
  default = "v1.2.0-eksbuild.1"
}

variable "coredns_version" {
  type    = string
  default = "v1.11.1-eksbuild.4"
}

variable "observability_version" {
  type    = string
  default = "v1.6.0-eksbuild.1"
}

variable "student_principal" {
  type    = string
  default = "arn:aws:iam::785169158894:role/aws-reserved/sso.amazonaws.com/AWSReservedSSO_Student_1462e2a20cbcb77f"
}

variable "codebuild_principal" {
  type = string
}

variable "eks_admin_user_policy" {
  type    = string
  default = "arn:aws:eks::aws:cluster-access-policy/AmazonEKSClusterAdminPolicy"
}

variable "eks_cluster_user_policy" {
  type    = string
  default = "arn:aws:eks::aws:cluster-access-policy/AmazonEKSAdminPolicy"
}

variable "nodegroup_name" {
  type    = string
  default = "team-cuttlefish-nodegroup"
}

variable "nodegroup_role" {
  type = string
}

#? Required value
variable "node_subnet_ids" {
  type = list(string)
}

variable "desired_nodes" {
  type        = number
  description = "The desired number of nodes to have at any time"
  default     = 2
}

variable "max_nodes" {
  type        = number
  description = "The max number of nodes to have at any time"
  default     = 3
}

variable "min_nodes" {
  type        = number
  description = "The minimum number of nodes to have at any time"
  default     = 1
}

variable "unavailable_nodes" {
  type        = number
  description = "The number of nodes that can be unavailable when updating"
  default     = 1
}

variable "node_disk_size" {
  type        = string
  description = "How much EBS storage space is needed?"
  default     = "20"
}

#! Consider ON_DEMAND
variable "node_instance_pricing" {
  type        = string
  description = "The pricing model for the instances. Valid: ON_DEMAND, SPOT"
  default     = "ON_DEMAND"
}

variable "node_instance_types" {
  type        = list(string)
  description = "The instance types to utilize when creating nodes. Perferably, keep this to a single value"
  default     = ["t3.medium"]
}

#! Consider removing
variable "nodes_ssh_key" {
  type        = string
  description = "The SSH key used to access Nodes"
  default     = "csnyder-kp-k8"
}


#? Required value
variable "cluster_public" {
  type        = list(string)
  description = "The public subnets of the cluster"
}

#? Required value
variable "cluster_private" {
  type        = list(string)
  description = "The private subnets of the cluster"
}

/*
#? Required value
#! Update this either adding the SG in EKS or in VPC.
#! This needs to allow port 80 and 43, I believe
#! The other needs to allow 8215 from all, I believe
variable "node_group_security_group" {
  type        = list(string)
  description = "The security group to use for the node group"
}*/

#! Necessary from IAM
variable "alb_policy" {
  type        = string
  description = "The ARN of the policy to attach to the ALB service account"
}

variable "alb_setup_script" {
  type        = string
  description = "The path, absolute or relative, to the ALB setup script"
  default = "./setup_alb"
}