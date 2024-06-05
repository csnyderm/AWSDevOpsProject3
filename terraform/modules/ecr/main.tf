resource "aws_ecrpublic_repository" "repositories" {
  for_each        = toset(var.repository_names)
  repository_name = lower(each.value)

  
}

resource "aws_ecrpublic_repository_policy" "policies" {
  
  for_each        = toset(var.repository_names)
  repository_name = aws_ecrpublic_repository.repositories[each.key].repository_name
  policy          = var.ecr_policy
}
