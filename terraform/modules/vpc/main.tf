provider "aws" {
  # Configuration options
  region = "us-east-1"
}


resource "aws_vpc" "project3_vpc" {
  cidr_block = var.vpc_cidr

  tags = {
    Name   = var.vpc_name
    region = var.aws_region
  }
}

resource "aws_subnet" "private_subnets" {
  for_each          = var.private_subnets
  vpc_id            = aws_vpc.project3_vpc.id
  cidr_block        = cidrsubnet(var.vpc_cidr, 8, each.value)
  availability_zone = tolist(data.aws_availability_zones.available.names)[each.value]

  tags = {
    Name = each.key
  }
}

resource "aws_subnet" "public_subnets" {
  for_each   = var.public_subnets
  vpc_id     = aws_vpc.project3_vpc.id
  cidr_block = cidrsubnet(var.vpc_cidr, 8, each.value + 100)
  ## 100 is skipping (reserving) the first 100 IPs
  availability_zone = tolist(data.aws_availability_zones.available.names)[each.value]
  map_public_ip_on_launch = true

  tags = {
    Name = each.key
  }
}

resource "aws_internet_gateway" "team-cuttlefish_igw" {
  vpc_id = aws_vpc.project3_vpc.id

  tags = {
    Name = "team-cuttlefish_igw"
  }
}

resource "aws_route_table" "public_route_table" {
  vpc_id = aws_vpc.project3_vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.team-cuttlefish_igw.id
  }

  tags = {
    Name = "public_rtb" # Maybe variable
  }
}


resource "aws_security_group" "eks_sg" {
  name        = var.eks_sg_name
  description = "Allows traffic and connection to EKS cluster from approved sources"
  vpc_id      = aws_vpc.project3_vpc.id

  tags = {
    Name = var.eks_sg_name
  }
}

resource "aws_vpc_security_group_ingress_rule" "allow_ssh" {
  security_group_id = aws_security_group.eks_sg.id
  cidr_ipv4         = "0.0.0.0/0" # Narrow later
  from_port         = 22
  to_port           = 22
  ip_protocol       = "tcp"
}

resource "aws_vpc_security_group_ingress_rule" "allow_http" {
  security_group_id = aws_security_group.eks_sg.id
  cidr_ipv4         = "0.0.0.0/0" # Narrow later
  from_port         = 80
  to_port           = 80
  ip_protocol       = "tcp"
}


# One method on how to do this. Saving in case I go back to it later.
/*
resource "aws_security_group_rule" "eks_sg_ingress" {
  count = length(var.eks_sg_ingress)

  type              = "ingress"
  from_port         = var.eks_sg_ingress[count.index].from_port
  to_port           = var.eks_sg_ingress[count.index].to_port
  protocol          = var.eks_sg_ingress[count.index].protocol
  cidr_blocks       = [var.eks_sg_ingress[count.index].cidr_block]
  description       = var.eks_sg_ingress[count.index].description
  security_group_id = aws_security_group.eks_sg.id
}
*/