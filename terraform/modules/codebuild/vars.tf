variable "project_names" {
  description = "List of CodeBuild project names"
  type        = list(string)
}

variable "service_role" {
  description = "IAM role ARN for CodeBuild"
  type        = string
}

variable "region" {
  description = "AWS region"
  type        = string
  default     = "us-east-1"
}
