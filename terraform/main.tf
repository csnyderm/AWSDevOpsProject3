provider "aws" {
  region = "us-east-1"
}

module "cloudfront" {
  source              = "./modules/cloudfront"
  origin_bucket       = module.s3_static_website.bucket_name
  acm_certificate_arn = "arn:aws:acm:us-east-1:785169158894:certificate/ddf9372e-77f0-4d27-96a7-b57c44b0f20a"
  aliases             = ["team-cuttlefish.aws-tfbd.com"]
  
}


module "s3_static_website" {
  source = "./modules/s3_bucket"

  bucket_name                           = "teamcuttlefish"
  cloudfront_origin_access_identity_arn = module.cloudfront.origin_access_identity_arn
  
}

output "s3_bucket_name" {
  value = module.s3_static_website.bucket_name
}

output "cloudfront_domain_name" {
  value = module.cloudfront.cloudfront_domain_name
}