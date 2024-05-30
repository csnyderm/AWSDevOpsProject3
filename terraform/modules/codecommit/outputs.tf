output "repo_arns" {
  value = aws_codecommit_repository.repo[*].arn
}