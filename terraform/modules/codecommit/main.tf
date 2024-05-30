resource "aws_codecommit_repository" "repo" {
  count            = length(var.repo_names)
  repository_name  = var.repo_names[count.index]
}