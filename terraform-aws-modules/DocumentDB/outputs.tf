output "master_username" {
  value       = join("", aws_docdb_cluster.example.*.master_username)
  description = "Username for the master DB user"
}

output "cluster_name" {
  value       = join("", aws_docdb_cluster.example.*.cluster_identifier)
  description = "Cluster Identifier"
}

output "instance_identifier" {
  value = aws_docdb_cluster_instance.example.*.identifier
}

output "arn" {
  value       = join("", aws_docdb_cluster.example.*.arn)
  description = "Amazon Resource Name (ARN) of the cluster"
}

output "id" {
  value       = join("", aws_docdb_cluster.example.*.id)
  description = "DocumentDB Cluster Resource ID"
}

output "endpoint" {
  value       = join("", aws_docdb_cluster.example.*.endpoint)
  description = "Endpoint of the DocumentDB cluster"
}

output "reader_endpoint" {
  value       = join("", aws_docdb_cluster.example.*.reader_endpoint)
  description = "A read-only endpoint of the DocumentDB cluster, automatically load-balanced across replicas"
}

output "security_group_id" {
  description = "ID of the DocumentDB cluster Security Group"
  value       = try(aws_security_group.documentdb_sg.id, "")
}

output "security_group_arn" {
  description = "ARN of the DocumentDB cluster Security Group"
  value       = try(aws_security_group.documentdb_sg.arn, "")
}

output "security_group_name" {
  description = "Name of the DocumentDB cluster Security Group"
  value       = try(aws_security_group.documentdb_sg.name, "")
}