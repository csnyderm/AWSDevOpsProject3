# Data sources to dynamically get the required IDs
data "aws_eks_cluster" "cluster" {
  name = "team-cuttlefish-cluster"
}

data "aws_elb" "alb" {
  name = "my-load-balancer"
}

data "aws_instance" "ec2_instance" {
  filter {
    name   = "tag:Name"
    values = ["your-ec2-instance-tag"]
  }
}

data "aws_docdb_cluster" "docdb_cluster" {
  filter {
    name   = "tag:Name"
    values = ["your-docdb-cluster-tag"]
  }
}

data "aws_cognito_user_pool" "user_pool" {
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