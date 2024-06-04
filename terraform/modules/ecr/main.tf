resource "aws_ecrpublic_repository" "repositories" {
  for_each        = toset(var.repository_names)
  repository_name = lower(each.value)

  
}

resource "aws_ecrpublic_repository_policy" "policies" {
  provisioner "local-exec" {
    command = "echo ${aws_ecrpublic_repository.repositories}"
  }
  for_each        = toset([for s in var.repository_names : lower(s)])
  repository_name = aws_ecrpublic_repository.repositories[lower(each.key)].repository_name
  policy          = var.ecr_policy
}
