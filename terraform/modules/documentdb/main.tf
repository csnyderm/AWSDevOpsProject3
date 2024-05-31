resource "aws_security_group" "documentdb_sg" {
    name = "team-cuttlefish-db"
    vpc_id = var.vpc_id

    ingress {
        from_port = 27017
        to_port = 27017
        protocol = "tcp"
        security_groups = [var.security_groups]
    }

    egress {
        from_port = 0
        to_port = 0
        protocol = "-1"
        cidr_blocks = ["0.0.0.0,0"]
    }

    tags = {
        Name = "Team-Cuttlefish Security Group"
    }
}

resource "aws_docdb_cluster" "cuttlefish_db" {
    cluster_identifier = var.cluster_name
    engine = var.engine
    master_username = var.master_username
    master_password = var.master_password
    skip_final_snapshot = true
    db_subnet_group_name = aws_docdb_subnet_group.cuttlefish_sg.name
    vpc_security_group_ids = [aws_security_group.documentdb_sg.id]
    tags = var.tags
}

resource "aws_docdb_subnet_group" "cuttlefish_sg" {
    name = "${var.cluster_name}-subnet-group"
    subnet_ids = var.subnet_ids
    tags = var.tags
}

resource "aws_docdb_cluster_instance" "cuttlefish_instance" {
    count = 2
    identifier = "team-cuttlefish-instance-${count.index +1}"
    cluster_identifier = join("", aws_docdb_cluster.cuttlefish_db.*.id)
    instance_class = var.instance_class
    tags = var.tags
}

resource "aws_docdb_cluster_parameter_group" "cuttlefish_pg" {
    name = "team-cuttlefish-pg-${var.cluster_name}"
    family = var.cluster_family
    parameter {
      name = "tls"
      value = var.tls_enabled
    }
    tags = var.tags
}