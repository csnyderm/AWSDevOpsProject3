output "cluster_name" {
  value = aws_eks_cluster.project3-cluster.name
}

output "node_group_id" {
  value = aws_eks_node_group.team-cuttlefish-nodegroup.id
}

output "node_group_resources" {
  value = aws_eks_node_group.team-cuttlefish-nodegroup.resources
}