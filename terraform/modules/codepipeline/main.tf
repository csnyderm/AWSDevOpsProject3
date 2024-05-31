resource "aws_codepipeline" "pipeline" {
  count           = length(var.pipeline_names)
  name            = var.pipeline_names[count.index]
  role_arn        = var.role_arn
  artifact_store {
    type     = "S3"
    location = var.artifact_bucket
  }
  stage {
    name = "Source"
    action {
      name             = "Source"
      category         = "Source"
      owner            = "AWS"
      provider         = "CodeCommit"
      version          = "1"
      output_artifacts = ["source_output"]
      configuration = {
        RepositoryName = var.pipeline_names[count.index]
        BranchName     = "main"
      }
    }
  }
  stage {
    name = "Build"
    action {
      name             = "Build"
      category         = "Build"
      owner            = "AWS"
      provider         = "CodeBuild"
      input_artifacts  = ["source_output"]
      output_artifacts = ["build_output"]
      version          = "1"
      configuration = {
        ProjectName = var.pipeline_names[count.index]
      }
    }
  }
}
