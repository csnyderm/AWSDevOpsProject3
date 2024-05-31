data "aws_route53_zone" "hosted_zone" {
  name = "aws-tfbd.com"  
}

resource "aws_route53_record" "hosted_zone_id" {
  zone_id = data.aws_route53_zone.hosted_zone.zone_id
  name    = "team-cuttlefish.aws-tfbd.com"  
  type    = "A"

  alias {
    name                   = module.cloudfront.cloudfront_domain_name
    zone_id                = "Z2FDTNDATAQYW2"  # CloudFront hosted zone ID (global)
    evaluate_target_health = false
  }
}

resource "aws_cloudfront_origin_access_identity" "origin_access_identity" {
  comment = "OAI for static site"
}

resource "aws_cloudfront_cache_policy" "cache_policy" {
  name = var.cache_policy_name

  parameters_in_cache_key_and_forwarded_to_origin {
    headers_config {
      header_behavior = "whitelist"
      headers {
        items = ["Origin", "Content-Type"]
      }
    }

    cookies_config {
      cookie_behavior = "none"
    }

    query_strings_config {
      query_string_behavior = "none"
    }

    enable_accept_encoding_gzip = true
    enable_accept_encoding_brotli = true
  }

  default_ttl = 3600 # 1 hour
  max_ttl     = 86400 # 1 day
  min_ttl     = 0
}

resource "aws_cloudfront_origin_request_policy" "origin_request_policy" {
  name = var.origin_request_policy_name

  headers_config {
    header_behavior = "whitelist"
    headers {
      items = [
        "Origin", 
        "Access-Control-Allow-Origin", 
        "Access-Control-Request-Headers", 
        "Access-Control-Request-Method", 
        "Content-Type", 
        "Accept",
        "Viewer-Protocol",
        "CloudFront-Viewer-TLS"
      ]
    }
  }

  cookies_config {
    cookie_behavior = "none"
  }

  query_strings_config {
    query_string_behavior = "none"
  }
}

resource "aws_cloudfront_response_headers_policy" "response_headers_policy" {
  name = var.response_headers_policy_name

  cors_config {
    access_control_allow_credentials = true

    access_control_allow_headers {
      items = ["Authorization", "Content-Type"]
    }

    access_control_allow_methods {
      items = ["GET", "HEAD", "OPTIONS", "PUT", "POST", "PATCH", "DELETE"]
    }

    access_control_allow_origins {
      items = ["*"]
    }

    access_control_expose_headers {
      items = ["Authorization", "Content-Type"]
    }

    access_control_max_age_sec = 3000
    origin_override = true
  }

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

    allowed_methods = ["GET", "HEAD", "OPTIONS", "PUT", "POST", "PATCH", "DELETE"]
    cached_methods  = ["GET", "HEAD"]

    cache_policy_id            = aws_cloudfront_cache_policy.cache_policy.id
    origin_request_policy_id   = aws_cloudfront_origin_request_policy.origin_request_policy.id
    response_headers_policy_id = aws_cloudfront_response_headers_policy.response_headers_policy.id

    min_ttl     = 0
    default_ttl = 3600
    max_ttl     = 86400
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

  tags = var.tags
}