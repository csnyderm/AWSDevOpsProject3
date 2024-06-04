#! MARKED FOR DELETION

terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = "us-east-1"
}

resource "aws_security_group" "team_cuttlefish_sg" {
  name   = "team_cuttlefish_sg"
  vpc_id = aws_vpc.vpc.id

  ingress {
    from_port = 443
    to_port   = 443
    protocol  = "tcp"
  }

  ingress {
    from_port = 80
    to_port   = 80
    protocol  = "tcp"
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = 0
    cidr_blocks = ["0.0.0.0/0"]
  }
}


module "documentdb" {
  source         = "./modules/documentdb"
  cluster_name   = "docdb-${local.team}"
  vpc_id         = aws_vpc.vpc.id
  instance_class = "db.r6g.large"
  engine         = "docdb"

  subnet_ids      = [for subnet in aws_subnet.private_subnet : subnet.id]
  security_groups = aws_security_group.team_cuttlefish_sg.id
  tls_enabled     = false
  tags = {
    Name        = local.team
    Environment = local.application
  }
}
