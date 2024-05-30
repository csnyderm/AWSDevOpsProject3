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

  #! These two are required
  cluster_public = tolist(module.vpc_module.public_subnet_ids)
  cluster_private = tolist(slice(module.vpc_module.private_subnet_ids, 0, 2))
}