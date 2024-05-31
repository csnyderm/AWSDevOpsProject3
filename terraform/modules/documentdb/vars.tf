variable "cluster_name" {
  type = string
  description = "team-cuttlefish-db"
  validation {
    condition = can(regex("^[a-z0-9-]+$", var.cluster_name))
    error_message = "Cluster must only contain lowercase letters, numbers, hyphens"
  }
}

variable "engine" {
    type = string
    default = "docdb"
    description = "team-cuttlefish-db"
}

variable "master_username" {
    type = string
    default = "team-cuttlefish"
}

variable "master_password" {
    type = string
    default = "password"
    sensitive = true
}

variable "backup_retention_period" {
  type = number
  default = 7
}

variable "subnet_ids" {
  type = list(string)
  
}

variable "instance_class" {
  type = string
  default = "db.r6g.large"
}

variable "security_groups" {
  type = string
}

variable "tls_enabled" {
    type = bool
    default = false
}

variable "vpc_id" {
  type = string
}

variable "tags" {
  type = map(string)
}

variable "cluster_family" {
  type = string
  default = "docdb4.0"
}

variable "team" {
  type = string
  default = "cuttlefish"
}