variable "team" {
  type    = string
  default = "cuttlefish"
}

variable "region" {
  type = string
  default = "us-east-1"
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

variable "ddb_cluster_name" {
  type = string
  default = "team-cuttlefish-docdb"
}

variable "project_names" {
  type = list(string)
  default = ["frontend", "API", "account-management", "budget-planning", "eureka", "investments", "tax-estimator"]
}

variable "artifact_bucket" {
  type = string
  default = "codepipeline-us-east-1-778398079089"
}