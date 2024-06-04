resource "aws_security_group" "documentdb_sg" {
  name   = var.documentdb_sg_name
  vpc_id = var.vpc_id

  ingress {
    from_port       = 27017
    to_port         = 27017
    protocol        = "tcp"
    security_groups = [var.ingress_sg]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  description = "Team-Cuttlefish DocumentDB Security Group"
  tags = {
    Name = var.documentdb_sg_name
    team = var.team
  }
}

resource "aws_security_group_rule" "Allow ingress from db" {
  security_group_id = var.ingress_sg
  type = "ingress"
  from_port = 0
  to_port = 65535
  protocol = "tcp"
  source_security_group_id = aws_security_group.documentdb_sg.id
}


resource "aws_docdb_subnet_group" "cuttlefish_subnetgrp" {
  name       = "${var.cluster_name}-subnet-group"
  subnet_ids = var.subnet_ids
  tags       = var.tags
}

resource "aws_docdb_cluster" "cuttlefish_db" {

  depends_on = [aws_docdb_subnet_group.cuttlefish_subnetgrp]

  cluster_identifier     = var.cluster_name
  engine                 = var.engine
  master_username        = var.master_username
  master_password        = var.master_password
  skip_final_snapshot    = true
  db_subnet_group_name   = aws_docdb_subnet_group.cuttlefish_subnetgrp.name
  vpc_security_group_ids = [aws_security_group.documentdb_sg.id]
  tags                   = var.tags
  db_cluster_parameter_group_name = aws_docdb_cluster_parameter_group.cuttlefish_pg.name
}



resource "aws_docdb_cluster_instance" "cuttlefish_instance" {

  depends_on = [aws_docdb_cluster.cuttlefish_db]

  count              = 2
  identifier         = "team-cuttlefish-instance-${count.index + 1}"
  cluster_identifier = join("", aws_docdb_cluster.cuttlefish_db.*.id)
  instance_class     = var.instance_class
  tags               = var.tags
}

resource "aws_docdb_cluster_parameter_group" "cuttlefish_pg" {
  name   = "team-cuttlefish-pg-${var.cluster_name}"
  family = var.cluster_family
  parameter {
    name  = "tls"
    value = var.tls_enabled
  }
  tags = var.tags
}