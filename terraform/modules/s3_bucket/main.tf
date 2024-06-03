resource "aws_s3_bucket" "static_site" {
  bucket = var.bucket_name

  website {
    index_document = var.index_document
    error_document = var.error_document
  }

  tags = var.tags
}


data "aws_prefix_list" "cloudfront_origin_facing" {
  prefix_list_id = var.cloudfront_prefix_list
}

resource "aws_s3_bucket_policy" "static_site_policy" {

  depends_on = [aws_s3_bucket.static_site]

  bucket = aws_s3_bucket.static_site.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid : "AllowCloudFrontServicePrincipal",
        Effect = "Allow"
        Principal = {
          "AWS" = var.cloudfront_origin_access_identity_arn
        }
        Action   = "s3:GetObject"
        Resource = "${aws_s3_bucket.static_site.arn}/*"
      },
      {
        Sid : "AllowCloudFrontOriginFacingIPRanges",
        Effect    = "Allow",
        Principal = "*",
        Action    = "s3:GetObject",
        Resource  = "${aws_s3_bucket.static_site.arn}/*",
        Condition = {
          IpAddress = {
            "aws:SourceIp" = "${data.aws_prefix_list.cloudfront_origin_facing.cidr_blocks}"
          }
        }
      },
      {
        Sid : "AllowCodeBuildFrontendRole",
        Effect = "Allow",
        Principal = {
          AWS = "${aws_iam_role.codebuild_role.arn}"
        },
        Action = [
          "s3:GetObject",
          "s3:PutObject"
        ],
        Resource = "${aws_s3_bucket.static_site.arn}/*"
      }
    ]
  })
}