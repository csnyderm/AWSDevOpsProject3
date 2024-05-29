resource "aws_codepipeline" "pipeline" {
  count = 7

  name     = element(["frontend", "API", "account-management", "budget-planning", "eureka", "investments", "tax-estimator"], count.index)
  role_arn = aws_iam_role.codepipeline_role.arn

  artifact_store {
    type     = "S3"
    location = data.aws_s3_bucket.codepipeline_bucket.bucket
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
        RepositoryName = element([aws_codecommit_repository.frontend.repository_name, aws_codecommit_repository.API.repository_name, aws_codecommit_repository.account_management.repository_name, aws_codecommit_repository.budget_planning.repository_name, aws_codecommit_repository.eureka.repository_name, aws_codecommit_repository.investments.repository_name, aws_codecommit_repository.tax_estimator.repository_name], count.index)
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
      version          = "1"
      input_artifacts  = ["source_output"]
      output_artifacts = ["build_output"]

      configuration = {
        ProjectName = element([aws_codebuild_project.project[0].name, aws_codebuild_project.project[1].name, aws_codebuild_project.project[2].name, aws_codebuild_project.project[3].name, aws_codebuild_project.project[4].name, aws_codebuild_project.project[5].name, aws_codebuild_project.project[6].name], count.index)
      }
    }
  }
}
