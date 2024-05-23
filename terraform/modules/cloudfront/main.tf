resource "aws_cloudfront_origin_access_identity" "origin_access_identity" {
  comment = "OAI for static site"
}

resource "aws_cloudfront_distribution" "cdn" {
  origin {
    domain_name = "${var.origin_bucket}.s3.amazonaws.com"
    origin_id   = "S3-${var.origin_bucket}"

    s3_origin_config {
      origin_access_identity = aws_cloudfront_origin_access_identity.origin_access_identity.cloudfront_access_identity_path
    }
  }

  enabled             = true
  is_ipv6_enabled     = true
  comment             = "Team Cuttlefish's Distro"
  default_root_object = "index.html"

  aliases = var.aliases

  default_cache_behavior {
    target_origin_id       = "S3-${var.origin_bucket}"
    viewer_protocol_policy = "redirect-to-https"

    allowed_methods = ["GET", "HEAD"]
    cached_methods  = ["GET", "HEAD"]

    forwarded_values {
      query_string = false
      cookies {
        forward = "none"
      }
    }

    min_ttl                = 0
    default_ttl            = 3600
    max_ttl                = 86400
  }

  price_class = "PriceClass_100"

  restrictions {
    geo_restriction {
      restriction_type = "none"
    }
  }

  viewer_certificate {
    acm_certificate_arn      = var.acm_certificate_arn
    ssl_support_method       = "sni-only"
    minimum_protocol_version = "TLSv1.2_2021"
  }
}

output "cloudfront_domain_name" {
  value = aws_cloudfront_distribution.cdn.domain_name
}

output "origin_access_identity_arn" {
  value = aws_cloudfront_origin_access_identity.origin_access_identity.iam_arn
}