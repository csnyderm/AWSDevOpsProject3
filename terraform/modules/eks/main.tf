
resource "aws_eks_cluster" "project3-cluster" {
  name = var.cluster_name

  role_arn = var.cluster_role
  vpc_config {
    #subnet_ids = var.subnet_ids
    subnet_ids         = var.cluster_subnet_ids
    security_group_ids = var.cluster_security_group
  }

  access_config {
    authentication_mode = var.cluster_auth_mode
  }
  tags = {
    team = var.team
  }
}

resource "aws_eks_addon" "cni_addon" {
  cluster_name  = aws_eks_cluster.project3-cluster.name
  addon_name    = "vpc-cni" #! Unsure how to move names to data or other
  addon_version = var.cni_version
}

resource "aws_eks_addon" "kubeproxy_addon" {
  cluster_name  = aws_eks_cluster.project3-cluster.name
  addon_name    = "kube-proxy" #! Unsure how to move names to data or other
  addon_version = var.kubeproxy_version
}

resource "aws_eks_addon" "podidentity_addon" {
  cluster_name  = aws_eks_cluster.project3-cluster.name
  addon_name    = "eks-pod-identity-agent" #! Unsure how to move names to data or other
  addon_version = var.podidentity_version
}

resource "aws_eks_addon" "coredns_addon" {
  cluster_name  = aws_eks_cluster.project3-cluster.name
  addon_name    = "coredns" #! Unsure how to move names to data or other
  addon_version = var.coredns_version
}

resource "aws_eks_addon" "observability_addon" {
  cluster_name  = aws_eks_cluster.project3-cluster.name
  addon_name    = "amazon-cloudwatch-observability" #! Unsure how to move names to data or other
  addon_version = var.observability_version
}

resource "aws_eks_access_entry" "eks_access" {
  cluster_name  = aws_eks_cluster.project3-cluster.name
  principal_arn = var.student_principal
  type          = "STANDARD"
}

resource "aws_eks_access_policy_association" "eks_access_association" {
  cluster_name  = aws_eks_cluster.project3-cluster.name
  policy_arn    = var.eks_admin_user_policy
  principal_arn = var.student_principal

  access_scope {
    type = "cluster"
  }
}

resource "aws_eks_access_policy_association" "eks_access_association" {
  cluster_name  = aws_eks_cluster.project3-cluster.name
  policy_arn    = var.eks_cluster_user_policy
  principal_arn = var.student_principal

  access_scope {
    type = "cluster"
  }
}


resource "aws_eks_node_group" "team-cuttlefish-nodegroup" {
  cluster_name    = aws_eks_cluster.project3-cluster.name
  node_group_name = var.nodegroup_name
  node_role_arn   = var.nodegroup_role
  subnet_ids      = var.node_subnet_ids

  scaling_config {
    desired_size = var.desired_nodes
    max_size     = var.max_nodes
    min_size     = var.min_nodes
  }

  update_config {
    max_unavailable = var.unavailable_nodes
  }

  remote_access {
    ec2_ssh_key = var.nodes_ssh_key
  }

  ## Optional but values such as AMI, capacity, etc
  disk_size      = var.node_disk_size
  capacity_type  = var.node_instance_pricing
  instance_types = var.node_instance_types
  #remote_access = something


  # Replace with our own later
  # Ensure that IAM Role permissions are created before and deleted after EKS Node Group handling.
  # Otherwise, EKS will not be able to properly delete EC2 Instances and Elastic Network Interfaces.
  depends_on = [
    aws_eks_cluster.project3-cluster, # Make sure the cluster is created.
  ]

  tags = {
    Name = var.nodegroup_name
    team = var.team
  }
}


# We know that the public subnets are the first two and the private are the next two
# At least in the current configuration
resource "null_resource" "public_tagging_additions" {
  count = length(var.cluster_public)

  provisioner "local-exec" {
    command = "aws ec2 create-tags --resources ${var.cluster_public[count.index]} --tags Key=kubernetes.io/cluster/${var.cluster_name},Value=owned Key=kubernetes.io/role/elb,Value=1"
  }

  depends_on = [aws_eks_cluster.project3-cluster]
}

resource "null_resource" "private_tagging_additions" {
  count = length(var.cluster_private)

  provisioner "local-exec" {
    command = "aws ec2 create-tags --resources ${var.cluster_private[count.index]} --tags Key=kubernetes.io/cluster/${var.cluster_name},Value=owned Key=kubernetes.io/role/internal-elb,Value=1"
  }

  depends_on = [aws_eks_cluster.project3-cluster]
}

resource "null_resource" "setup_alb" {

  depends_on = [                                                                                                                                             # Wait for the node group AND all addons to be up, to make sure that we have the nodes active
    aws_eks_node_group.team-cuttlefish-nodegroup,                                                                                                            # The node group needs to be up
    aws_eks_addon.cni_addon, aws_eks_addon.observability_addon, aws_eks_addon.coredns_addon, aws_eks_addon.podidentity_addon, aws_eks_addon.kubeproxy_addon, # Wait on the addons
  ]

  provisioner "local-exec" {
    command = "bash ${var.alb_setup_script} ${var.cluster_name} ${var.aws_region} ${var.alb_policy}"
  }
}