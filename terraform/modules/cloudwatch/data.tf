# Data sources to dynamically get the required IDs
data "aws_eks_cluster" "cluster" {
  name = var.eks_cluster_name
}

data "aws_lb" "alb" {
  tags = {
    "elbv2.k8s.aws/cluster" = var.alb_name
  }
}

data "aws_instance" "ec2_instance" {
  filter {
    name   = "instance-id"
    values = [var.ec2_instance_tag]
  }
}
/*
data "aws_docdb_cluster" "docdb_cluster" {
  filter {
    name   = "tag:Name"
    values = ["your-docdb-cluster-tag"]
  }
}*/

data "aws_cognito_user_pools" "user_pool" {
  name = "us-east-1_Ne5xAWeWT"
}

data "aws_s3_bucket" "s3_bucket" {
  bucket = "teamcuttlefish"
}

data "aws_cloudfront_distribution" "distribution" {
  id = var.cloudfront_id
  /*filter {
    name   = "tag:Name"
    values = ["your-cloudfront-distribution-tag"]
  }*/
}