provider "aws" {
  # Configuration options
  region = "us-east-1"
}

resource "aws_eks_cluster" "project3-cluster" {
  name = var.cluster_name

  role_arn = aws_iam_role.example.arn # Might need to pull from the policies section as output
  vpc_config {
    subnet_ids = var.subnet_ids
  }
  depends_on = [
    aws_iam_role_policy_attachment.example-AmazonEKSClusterPolicy,
    aws_iam_role_policy_attachment.example-AmazonEKSVPCResourceController,
  ]

  tags = {
    team = var.team
  }
}

resource "aws_eks_addon" "cni_addon" {
  cluster_name = aws_eks_cluster.project3-cluster.name
  addon_name = "vpc-cni" # Maybe move the names to data?
  addon_version = var.cni_version
}

resource "aws_eks_addon" "kubeproxy_addon" {
  cluster_name = aws_eks_cluster.project3-cluster.name
  addon_name = "kube-proxy" # Maybe move the names to data?
  addon_version = var.kubeproxy_version
}

resource "aws_eks_addon" "podidentity_addon" {
  cluster_name = aws_eks_cluster.project3-cluster.name
  addon_name = "eks-pod-identity-agent" # Maybe move the names to data?
  addon_version = var.podidentity_version
}

resource "aws_eks_addon" "coredns_addon" {
  cluster_name = aws_eks_cluster.project3-cluster.name
  addon_name = "coredns" # Maybe move the names to data?
  addon_version = var.coredns_version
}

resource "aws_eks_addon" "observability_addon" {
  cluster_name = aws_eks_cluster.project3-cluster.name
  addon_name = "amazon-cloudwatch-observability" # Maybe move the names to data?
  addon_version = var.observability_version
}

resource "aws_eks_node_group" "team-cuttlefish-nodegroup" {
  cluster_name    = aws_eks_cluster.project3-cluster.name
  node_group_name = var.nodegroup_name
  node_role_arn   = aws_iam_role.example.arn # Replace later with other
  subnet_ids      = aws_subnet.example[*].id # Replace later with subnets

  scaling_config {
    desired_size = var.desired_nodes
    max_size     = var.max_nodes
    min_size     = var.min_nodes
  }

  update_config {
    max_unavailable = var.unavailable_nodes
  }

  ## Optional but values such as AMI, capacity, etc
  #disk_size = var.node_disk_size
  #capacity_type = var.node_instance_pricing
  #instance_types = var.node_instance_type
  #remote_access = something

  # Replace with our own later
  # Ensure that IAM Role permissions are created before and deleted after EKS Node Group handling.
  # Otherwise, EKS will not be able to properly delete EC2 Instances and Elastic Network Interfaces.
  depends_on = [
    aws_iam_role_policy_attachment.example-AmazonEKSWorkerNodePolicy,
    aws_iam_role_policy_attachment.example-AmazonEKS_CNI_Policy,
    aws_iam_role_policy_attachment.example-AmazonEC2ContainerRegistryReadOnly,
    aws_eks_cluster.project3-cluster, # Make sure the cluster is created. Maybe wait on addons?
    aws_eks_addon.cni_addon,
    aws_eks_addon.coredns_addon,
    aws_eks_addon.kubeproxy_addon,
    aws_eks_addon.observability_addon,
    aws_eks_addon.podidentity_addon
  ]

  tags = {
    team = var.team
  }
}