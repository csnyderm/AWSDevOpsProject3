provider "aws" {
  # Configuration options
  region = "us-east-1"
}

resource "aws_eks_cluster" "project3-cluster" {
  name = var.cluster_name

  role_arn = aws_iam_role.example.arn
  vpc_config {
    subnet_ids = var.subnet_ids
  }
  depends_on = [
    aws_iam_role_policy_attachment.example-AmazonEKSClusterPolicy,
    aws_iam_role_policy_attachment.example-AmazonEKSVPCResourceController,
  ]
}