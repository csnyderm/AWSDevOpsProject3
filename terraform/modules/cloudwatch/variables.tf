variable "team" {
  type = string
  default = "cuttlefish"
}

variable "aws_region" {
  type = string
  default = "us-east-1"
}

variable "cloudfront_id" {
  type = string
}

variable "dashboard_name" {
  type = string
  default = "Team-Cuttlefish"
}

variable "sns_topic_name" {
  type = string
  default = "team-cuttlefish-topic"
}

variable "team_email" {
  type = string
  default = "nlaws@skillstorm.com"
}

variable "eks_cluster_name" {
  type = string
  default = "team-cuttlefish-cluster"
}

variable "alb_name" {
  type = string
}

variable "ec2_instance_tag" {
  type = string 
}

variable "ddb_cluster_id" {
  type = string
}