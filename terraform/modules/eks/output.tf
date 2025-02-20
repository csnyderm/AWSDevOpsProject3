output "cluster_name" {
  value = aws_eks_cluster.project3-cluster.name
}

output "cluster_sg" {
  value = aws_eks_cluster.project3-cluster.vpc_config[0].cluster_security_group_id
}

output "node_group_id" {
  value = aws_eks_node_group.team-cuttlefish-nodegroup.id
}

output "node_group_resources" {
  value = aws_eks_node_group.team-cuttlefish-nodegroup.resources
}

output "node_group_name" {
  value = aws_eks_node_group.team-cuttlefish-nodegroup.node_group_name
}