output "project_names" {
  value = aws_codebuild_project.project[*].name
}
