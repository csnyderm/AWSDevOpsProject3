output "cluster_name" {
  value = aws_eks_cluster.project3-cluster.name
}

output "node_group_name" {
  value = aws_eks_node_group.team-cuttlefish-nodegroup.name
}

output "node_group_resources" {
  value = aws_eks_node_group.team-cuttlefish-nodegroup.resources
}