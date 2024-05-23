variable "aws_region" {
  type    = string
  default = "us-east-1"
}

variable "vpc_id" {
  type = string
  default = "null"
}
/* 
# Add this to the main in order to get the correct vpc_id
module "eks" {
  source = "./modules/eks"
  
  vpc_id = module.vpc.vpc_id
}
*/

variable "cluster_name" {
  type = string
  default = "team-cuttlefish-cluster"
}

variable "subnet_ids" {
  type = list
  default = [1,2]
}

/* 
# Add this to the main in order to get the correct subnet_id
module "eks" {
  source = "./modules/eks"
  
  vpc_id = module.vpc.public_subnet_id
  # Maybe use private or add instead?
}
*/