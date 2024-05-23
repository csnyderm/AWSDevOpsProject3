variable "bucket_name" {
  description = "The name of the S3 bucket"
  type        = string
}

variable "index_document" {
  description = "The index document for the website"
  type        = string
  default     = "index.html"
}

variable "error_document" {
  description = "The error document for the website"
  type        = string
  default     = "error.html"
}

variable "cloudfront_origin_access_identity_arn" {
  description = "The ARN of the CloudFront Origin Access Identity"
  type        = string
}