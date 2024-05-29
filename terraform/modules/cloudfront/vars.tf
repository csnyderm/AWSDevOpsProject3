variable "origin_bucket" {
  description = "The S3 bucket to be used as the origin for CloudFront"
  type        = string
}

variable "aliases" {
  description = "A list of aliases (CNAMEs) for the CloudFront distribution"
  type        = list(string)
  default     = []
}

variable "acm_certificate_arn" {
  description = "The ARN of the ACM certificate to use for SSL"
  type        = string
}

variable "cache_policy_name" {
  description = "Name for the CloudFront cache policy"
  type        = string
  default     = "TeamCuttlefishCachePolicy"
}

variable "origin_request_policy_name" {
  description = "Name for the CloudFront origin request policy"
  type        = string
  default     = "TeamCuttlefishOriginRequestPolicy"
}

variable "response_headers_policy_name" {
  description = "Name for the CloudFront response headers policy"
  type        = string
  default     = "TeamCuttlefishResponseHeadersPolicy"
}

variable "tags" {
  description = "A map of tags to apply to all resources"
  type        = map(string)
  default     = {
    team = "cuttlefish"
  }
}

