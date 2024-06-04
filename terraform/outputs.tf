locals {
  team        = "team-cuttlefish"
  application = "tfbdaws"
}

output "cluster_name" {
  value = module.documentdb.cluster_name
}

#! MARKED FOR DELETION ?