variable "pipeline_names" {
  description = "List of CodePipeline names"
  type        = list(string)
}

variable "role_arn" {
  description = "IAM role ARN for CodePipeline"
  type        = string
}

variable "artifact_bucket" {
  description = "S3 bucket for CodePipeline artifacts"
  type        = string
}
