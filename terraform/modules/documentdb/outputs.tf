output "master_username" {
  value = join("", aws_docdb_cluster.cuttlefish_db.*.master_username)
}

output "cluster_name" {
  value = join("", aws_docdb_cluster.cuttlefish_db.*.cluster_identifier)
}

output "instance_identifier" {
  value = aws_docdb_cluster_instance.cuttlefish_instance.*.identifier
}

output "arn" {
  value = join("", aws_docdb_cluster.cuttlefish_db.*.arn)
}

output "id" {
  value = join("", aws_docdb_cluster.cuttlefish_db.*.id)
}

output "endpoint" {
  value = join("", aws_docdb_cluster.cuttlefish_db.*.endpoint)
}

output "reader_endpoint" {
  value = join("", aws_docdb_cluster.cuttlefish_db.*.reader_endpoint)
}

output "security_group_id" {
  value = try(aws_security_group.documentdb_sg.id, "")
}

output "security_group_arn" {
  value = try(aws_security_group.documentdb_sg.arn, "")
}

output "security_group_name" {
  value = try(aws_security_group.documentdb_sg.name, "")
}
