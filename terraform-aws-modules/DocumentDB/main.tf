# DocumentDB Security Group
resource "aws_security_group" "documentdb_sg" {
  name        = "documentdb-sg"
  description = "Allow access to DocumentDB from the web server"
  vpc_id      = var.vpc_id

  ingress {
    description     = "Allow 27017 from the web server"
    from_port       = 27017
    to_port         = 27017
    protocol        = "tcp"
    security_groups = [var.security_groups] # Allow traffic from the web server security group
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "DocumentDB Security Group"
  }
}

resource "aws_docdb_cluster" "example" {
  cluster_identifier     = var.cluster_name
  engine                 = var.engine
  master_username        = var.master_username
  master_password        = var.master_password
  skip_final_snapshot    = true
  db_subnet_group_name   = aws_docdb_subnet_group.example.name
  vpc_security_group_ids = [aws_security_group.documentdb_sg.id]
  tags                   = var.tags
}

resource "aws_docdb_subnet_group" "example" {
  name       = "${var.cluster_name}-subnet-group"
  subnet_ids = var.subnet_ids
  tags       = var.tags
}

resource "aws_docdb_cluster_instance" "example" {
  count              = 2 # Adjust the instance count
  identifier         = "docdb-instance-${count.index + 1}"
  cluster_identifier = join("", aws_docdb_cluster.example.*.id)
  instance_class     = var.instance_class
  tags               = var.tags
}

resource "aws_docdb_cluster_parameter_group" "this" {
  name        = "parameter-group-${var.cluster_name}"
  description = "DB cluster parameter group."
  family      = var.cluster_family
  parameter {
    name  = "tls"
    value = var.tls_enabled ? "enabled" : "disabled"
  }
  tags = var.tags
}
