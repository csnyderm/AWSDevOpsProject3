output "pipeline_names" {
  value = aws_codepipeline.pipeline[*].name
}
