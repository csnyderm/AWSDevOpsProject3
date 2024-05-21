# DocumentDB Module

### Introduction
***
The purpose of this module is to deploy a DocumentDB cluster in AWS along with the accompanying `subnet_group`, `cluster_instance`, and `parameter_group`.

Lastly, a DocumentDB Security Group to accept input over 27017 from the front end web server instance security group.

## Usage

> Examples:

```hcl
# Module for DocumentDB
module "documentdb_cluster" {
  source          = "./modules/documentdb"
  cluster_name    = "docdb-${local.team}"
  vpc_id          = aws_vpc.vpc.id
  engine          = "docdb"
  instance_class  = "db.r6g.large"
  subnet_ids      = [for subnet in aws_subnet.private_subnets : subnet.id]
  security_groups = aws_security_group.tfsg.id
  tls_enabled     = false

  tags = {
    Name        = local.team
    Environment = local.application
  }
}
```

<hr />

## Required Inputs

`source`
`cluster_name`
`vpc_id`
`subnet_ids`
`security_groups`
`tags`

## Outputs

`master_username`
`cluster_name`
`instance_identifier`
`arn`
`id`
`endpoint`
`reader_endpoint`
`security_group_id`
`security_group_arn`
`security_group_name`
