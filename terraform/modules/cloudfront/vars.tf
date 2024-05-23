variable "origin_bucket" {
  description = "The S3 bucket origin for CloudFront"
  type        = string
}

variable "acm_certificate_arn" {
  description = "The ARN of the ACM certificate"
  type        = string
}

variable "aliases" {
  description = "A list of aliases for the CloudFront distribution"
  type        = list(string)
  default     = []
}

variable "aws_region" {
  description = "The AWS region where the S3 bucket is hosted"
  type        = string
}