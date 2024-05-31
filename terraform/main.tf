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

module "vpc_module" {
  source = "./modules/vpc"

}

module "eks_module" {
  source = "./modules/eks"

  # Get the public subnets and the first two private subnets
  cluster_subnet_ids = concat(module.vpc_module.public_subnet_ids, tolist(slice(module.vpc_module.private_subnet_ids, 0, 2)))
  #! NOTE: IF YOU CHANGE THIS VALUE, PUBLIC AND PRIVATE TAGGING IN EKS MAIN MAY NEED TO BE EDITED
  #! These can be found at the bottom of the eks main.tf document
  
  node_subnet_ids = module.vpc_module.public_subnet_ids
  cluster_security_group = [module.vpc_module.cluster_security_group]
  node_group_security_group = [module.vpc_module.cluster_security_group]

  #! These two are required
  cluster_public = tolist(module.vpc_module.public_subnet_ids)
  cluster_private = tolist(slice(module.vpc_module.private_subnet_ids, 0, 2))

  #! For the ALB script
  alb_policy = "arn:aws:iam::785169158894:policy/AWSLoadBalancerControllerIAMPolicy"
  #alb_setup_script = 
}

#! We shouldn't have outputs and variables in main.
#? To do: Figure out where this came from and move it to where it needs to go
/*
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
*/
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

  depends_on = [ module.codebuild ]
}



#? Elton's modules here
#? This includes DocumentDB, ECR, and Cognito

module "documentdb" {
  source = "./modules/documentdb"
  vpc_id = module.vpc_module.vpc_id
  subnet_ids = [module.vpc_module.private_subnet_ids[-1]]
  cluster_name = "team-cuttlefish-docdb"
  security_groups = []
  tags = {
    team = var.team
  }
}