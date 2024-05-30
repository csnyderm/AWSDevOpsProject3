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

variable "codebuild_service_role_arn" {
  description = "IAM role ARN for CodeBuild"
  type        = string
}

variable "codepipeline_role_arn" {
  description = "IAM role ARN for CodePipeline"
  type        = string
}

module "codecommit" {
  source     = "./codecommit"
  repo_names = ["frontend", "API", "account-management", "budget-planning", "eureka", "investments", "tax-estimator"]
}

module "codebuild" {
  source        = "./codebuild"
  project_names = ["frontend", "API", "account-management", "budget-planning", "eureka", "investments", "tax-estimator"]
  service_role  = var.codebuild_service_role_arn
  region        = "us-east-1"
}

module "codepipeline" {
  source          = "./codepipeline"
  pipeline_names  = ["frontend", "API", "account-management", "budget-planning", "eureka", "investments", "tax-estimator"]
  role_arn        = var.codepipeline_role_arn
  artifact_bucket = "codepipeline-us-east-1-778398079089"
}