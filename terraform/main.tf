provider "aws" {
  region = "us-east-1"
}


#? This file will be in order of operation


module "iam" {
  source = "./modules/iam"
}


module "cognito" {
  source = "./modules/cognito"
}


module "cloudfront" {
  source              = "./modules/cloudfront"
  origin_bucket       = module.s3_static_website.bucket_name
  acm_certificate_arn = var.acm_certificate
  aliases             = var.cloudfront_aliases
}


module "s3_static_website" {
  source = "./modules/s3_bucket"

  bucket_name                           = var.s3_bucket_name
  cloudfront_origin_access_identity_arn = module.cloudfront.origin_access_identity_arn
  codebuild_role                        = module.iam.codebuild_role_arn
}

module "vpc_module" {
  source = "./modules/vpc"

}

module "eks_module" {
  source = "./modules/eks"

  # Get the public subnets and the first two private subnets
  cluster_subnet_ids = concat(module.vpc_module.public_subnet_ids, tolist(slice(module.vpc_module.private_subnet_ids, 0, 2)))

  node_subnet_ids = module.vpc_module.public_subnet_ids
  cluster_role    = module.iam.eks_cluster_role_arn

  codebuild_principal = module.iam.codebuild_role_arn

  #! These two are required
  cluster_public  = tolist(module.vpc_module.public_subnet_ids)
  cluster_private = tolist(slice(module.vpc_module.private_subnet_ids, 0, 2))

  nodegroup_role = module.iam.eks_nodegroup_role_arn

  #! For the ALB script
  alb_policy = module.iam.alb_policy_arn
  #? Will this work or do we need to give a different relative path?
  alb_setup_script = "./setup_alb.sh"
}

module "documentdb" {
  source       = "./modules/documentdb"
  vpc_id       = module.vpc_module.vpc_id
  subnet_ids   = [element(module.vpc_module.private_subnet_ids, 2)]
  cluster_name = var.ddb_cluster_name
  ingress_sg   = module.eks_module.cluster_sg
  tags = { # Adjust as needed
    team = var.team
  }
}

module "codecommit" {
  source     = "./modules/codecommit"
  repo_names = var.project_names
}

module "ecr" {
  source     = "./modules/ecr"
  depends_on = [module.codecommit]

  repository_names = var.project_names
  ecr_policy       = module.iam.ecr_policy_arn
}

module "codebuild" {
  source        = "./modules/codebuild"
  project_names = var.project_names
  #service_role  = "" #var.codebuild_service_role_arn # Placeholder, replace
  service_role     = module.iam.codebuild_role_arn
  region           = var.region
  eks_cluster_name = module.eks_module.cluster_name
  ddb_cluster_name = var.ddb_cluster_name
  ddb_pass         = module.documentdb.master_pass
  depends_on       = [module.ecr]
}


module "codepipeline" {
  source         = "./modules/codepipeline"
  pipeline_names = var.project_names
  #role_arn        = "" # var.codepipeline_role_arn # Placeholder, replace
  role_arn        = module.iam.codepipeline_role_arn
  artifact_bucket = var.artifact_bucket

  # Because it depends on CodeBuild, which depends on Commit/ECR, it implicitly relies on them as well
  depends_on = [module.codebuild, module.iam]
}

module "cloudwatch" {
  source = "./modules/cloudwatch"

  # Depends on our big 3. Since CodePipeline needs everything before it, and
  # DocDB/EKS only need VPC, everything should be up once these three are done
  depends_on = [module.codepipeline, module.eks_module, module.documentdb]

  cloudfront_id    = module.cloudfront.distribution_id
  ec2_instance_tag = module.eks_module.node_group_name
  ddb_cluster_id   = module.documentdb.id
  alb_name         = var.alb_name
  #cloudfront_id = "E1V5LUPIOTZWHQ"
  #alb_name = "team-cuttlefish-cluster"
  #ec2_instance_tag = "i-0253faca8004444cd"
  #ddb_cluster_id = "docdb-team-cuttlefish"

}