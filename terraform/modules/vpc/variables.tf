variable "aws_region" {
  type    = string
  default = "us-east-1"
}

variable "team" {
  type = string
  default = "cuttlefish"
}

variable "vpc_cidr" {
  type    = string
  default = "10.0.0.0/16"
}

variable "vpc_name" {
  type    = string
  default = "team-cuttlefish-vpc"
}

variable "private_subnets" {
  default = {
    "team-cuttlefish-private1" = 1
    "team-cuttlefish-private2" = 2
    "team-cuttlefish-private3" = 3
  }
}

variable "public_subnets" {
  default = {
    "team-cuttlefish-public1" = 1
    "team-cuttlefish-public2" = 2
  }
}

variable "eks_sg_name" {
  type    = string
  default = "team-cuttlefish-eks-sg"
}

# One option for how to do this. Saving in case I go back to it.
/*
variable "eks_sg_ingress" {
    type = list(object({
      from_port   = number
      to_port     = number
      protocol    = string
      cidr_block  = string
      description = string
    }))
    default     = [
        {
          from_port   = 22
          to_port     = 22
          protocol    = "tcp"
          cidr_block  = "0.0.0.0/0"
          description = "Allow SSH from many"
        },
        {
          from_port   = 80
          to_port     = 80
          protocol    = "tcp"
          cidr_block  = "0.0.0.0.0/0"
          description = "Allow HTTP from any"
        },
    ]
}
*/