

resource "aws_vpc" "team-cuttlefish_vpc" {
  cidr_block = var.vpc_cidr

  tags = {
    Name = var.vpc_name
    team = var.team
  }
}

resource "aws_subnet" "private_subnets" {
  for_each          = var.private_subnets
  vpc_id            = aws_vpc.team-cuttlefish_vpc.id
  cidr_block        = cidrsubnet(var.vpc_cidr, 8, each.value)
  availability_zone = tolist(data.aws_availability_zones.available.names)[each.value]

  tags = {
    Name = each.key
    team = var.team
  }
}

resource "aws_subnet" "public_subnets" {
  for_each   = var.public_subnets
  vpc_id     = aws_vpc.team-cuttlefish_vpc.id
  cidr_block = cidrsubnet(var.vpc_cidr, 8, each.value + 100)
  ## 100 is skipping (reserving) the first 100 IPs
  availability_zone       = tolist(data.aws_availability_zones.available.names)[each.value]
  map_public_ip_on_launch = true
  tags = {
    Name = each.key
    team = var.team
  }
}

resource "aws_internet_gateway" "team-cuttlefish_igw" {
  vpc_id = aws_vpc.team-cuttlefish_vpc.id

  tags = {
    Name = var.igw_name
    team = var.team
  }
}

resource "aws_route_table" "public_route_table" {

  depends_on = [aws_internet_gateway.team-cuttlefish_igw]

  vpc_id = aws_vpc.team-cuttlefish_vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.team-cuttlefish_igw.id
  }
  tags = {
    Name = var.public_rt_name # Maybe variable
    team = var.team
  }
}


#? Going to leave these two, but I really should have made it a loop
resource "aws_route_table_association" "public1_association" {

  depends_on = [aws_route_table.public_route_table, aws_subnet.public_subnets]

  subnet_id      = aws_subnet.public_subnets["team-cuttlefish-public1"].id
  route_table_id = aws_route_table.public_route_table.id
}

resource "aws_route_table_association" "public2_association" {

  depends_on = [aws_route_table.public_route_table, aws_subnet.public_subnets]

  subnet_id      = aws_subnet.public_subnets["team-cuttlefish-public2"].id
  route_table_id = aws_route_table.public_route_table.id
}

resource "aws_eip" "nat_gateway_eip" {
  tags = {
    Name = var.eip_name
    team = var.team
  }
}


resource "aws_nat_gateway" "team-cuttle_nat" {
  depends_on = [aws_subnet.public_subnets, aws_eip.nat_gateway_eip]

  allocation_id = aws_eip.nat_gateway_eip.id
  subnet_id     = aws_subnet.public_subnets["team-cuttlefish-public1"].id

  tags = {
    Name = var.nat_name
    team = var.team
  }
}

resource "aws_route_table" "private_route_table" {

  depends_on = [aws_vpc.team-cuttlefish_vpc, aws_nat_gateway.team-cuttle_nat]

  vpc_id = aws_vpc.team-cuttlefish_vpc.id
  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.team-cuttle_nat.id
  }
  tags = {
    Name = var.private_rt_name
    team = var.team
  }
}

#? Going to leave these but it should be a loop
resource "aws_route_table_association" "private1_association" {

  depends_on = [aws_subnet.private_subnets, aws_route_table.private_route_table]

  subnet_id      = aws_subnet.private_subnets["team-cuttlefish-private1"].id
  route_table_id = aws_route_table.private_route_table.id
}

resource "aws_route_table_association" "private2_association" {

  depends_on = [aws_subnet.private_subnets, aws_route_table.private_route_table]

  subnet_id      = aws_subnet.private_subnets["team-cuttlefish-private2"].id
  route_table_id = aws_route_table.private_route_table.id
}

resource "aws_route_table_association" "private3_association" {

  depends_on = [aws_subnet.private_subnets, aws_route_table.private_route_table]

  subnet_id      = aws_subnet.private_subnets["team-cuttlefish-private3"].id
  route_table_id = aws_route_table.private_route_table.id
}


/*
resource "aws_security_group" "eks_sg" {
  name        = var.eks_sg_name
  description = "Allows traffic and connection to EKS cluster from approved sources"
  vpc_id      = aws_vpc.team-cuttlefish_vpc.id

  tags = {
    Name = var.eks_sg_name
    team = var.team
  }
}
*/
/*
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
*/

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