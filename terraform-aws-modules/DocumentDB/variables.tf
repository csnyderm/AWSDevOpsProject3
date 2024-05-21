variable "cluster_name" {
  type        = string
  description = "The name for the DocumentDB cluster. Must contain only lowercase letters, numbers, and hyphens."
  validation { # Used for error handling and standardizing
    condition     = can(regex("^[a-z0-9-]+$", var.cluster_name)) /* The can() function
    Wraps the regex filter and cluster_name and evaluates to returna boolean value
    Ideal for error handling, but can be subbed for a try function
    https://developer.hashicorp.com/terraform/language/functions/can
    The regex expression identifies if the cluster_name in var is only of lowercase letters, numbers and hyphens */
    error_message = "Cluster name must contain only lowercase letters, numbers, and hyphens."
  }
}

variable "engine" {
  type        = string
  default     = "docdb" # DocumentDB is the default engine
  description = "The name of the database engine to be used for this cluster. Valid values: docdb"
}

variable "master_username" {
  type        = string
  description = "The name of the master user for the DocumentDB cluster."
  default     = "username"
}

variable "master_password" {
  type        = string
  description = "The password for the master database user. This must be a strong password."
  default     = "password"
  sensitive   = true # Mark this as a sensitive value for security
}

variable "backup_retention_period" {
  type        = number
  default     = 7
  description = "The number of days for which automated backups are retained. Valid values are 0 to 35"
}

variable "subnet_ids" {
  type        = list(string)
  description = "A list of subnet IDs (typically private subnets) where DocumentDB instances will be created."
}

variable "instance_class" {
  type        = string
  description = "The instance class for the DocumentDB cluster instance"
  default     = "db.r6g.large"
}

variable "security_groups" {
  type = string
  # description = "A list of security group IDs attached to DocumentDB"
}

variable "tls_enabled" {
  type        = bool
  default     = false
  description = "When true than cluster using TLS for communication."
}

variable "vpc_id" {
  description = "ID of the VPC to deploy database into."
  type        = string
}

variable "tags" {
  type = map(string)
}

variable "cluster_family" {
  type        = string
  default     = "docdb4.0"
  description = "The family of the DocumentDB cluster parameter group. For more details, see https://docs.aws.amazon.com/documentdb/latest/developerguide/db-cluster-parameter-group-create.html ."
}

