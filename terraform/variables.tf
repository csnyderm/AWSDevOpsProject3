variable "team" {
  type    = string
  default = "cuttlefish"
}

variable "acm_certificate" {
  type = string
  default = "arn:aws:acm:us-east-1:785169158894:certificate/ddf9372e-77f0-4d27-96a7-b57c44b0f20a"
}

variable "cloudfront_aliases" {
  type = list(string)
  default = ["frontend.aws-tfbd.com"] #! Changed to frontend
}

variable "s3_bucket_name" {
  type = string
  default = "teamcuttlefish"
}

variable "repository_names" {
  description = "List of ECR public repository names"
  type        = list(string)
  default     = ["frontend", "API", "account-management", "budget-planning", "eureka", "investments", "tax-estimator"]
}

variable "team" {
  description = "Team name"
  type        = string
}

variable "ecr_policy" {
  description = "ECR policy JSON"
  type        = string
}