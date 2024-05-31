output "cloudfront_domain_name" {
  description = "The domain name of the CloudFront distribution"
  value       = aws_cloudfront_distribution.cdn.domain_name
}

output "origin_access_identity_arn" {
  description = "The ARN of the CloudFront Origin Access Identity"
  value       = aws_cloudfront_origin_access_identity.origin_access_identity.iam_arn
}

output "cache_policy_id" {
  description = "The ID of the CloudFront cache policy"
  value       = aws_cloudfront_cache_policy.cache_policy.id
}

output "origin_request_policy_id" {
  description = "The ID of the CloudFront origin request policy"
  value       = aws_cloudfront_origin_request_policy.origin_request_policy.id
}

output "response_headers_policy_id" {
  description = "The ID of the CloudFront response headers policy"
  value       = aws_cloudfront_response_headers_policy.response_headers_policy.id
}
