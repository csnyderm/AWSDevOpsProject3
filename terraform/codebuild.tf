resource "aws_codebuild_project" "project" {
  count = 7

  name          = element(["frontend", "API", "account-management", "budget-planning", "eureka", "investments", "tax-estimator"], count.index)
  description   = "${element(["frontend", "API", "account-management", "budget-planning", "eureka", "investments", "tax-estimator"], count.index)} CodeBuild Project"
  build_timeout = 5

  service_role = aws_iam_role.codebuild_role.arn

  artifacts {
    type = "NO_ARTIFACTS"
  }

  environment {
    compute_type    = "BUILD_GENERAL1_SMALL"
    image           = "aws/codebuild/amazonlinux2-x86_64-standard:4.0"
    type            = "LINUX_CONTAINER"
    privileged_mode = false
  }

  source {
    type            = "CODECOMMIT"
    location        = element([aws_codecommit_repository.frontend.repository_name, aws_codecommit_repository.api.repository_name, aws_codecommit_repository.account_management.repository_name, aws_codecommit_repository.budget_planning.repository_name, aws_codecommit_repository.eureka.repository_name, aws_codecommit_repository.investments.repository_name, aws_codecommit_repository.tax_estimator.repository_name], count.index)
    buildspec       = "buildspec.yml"
    git_clone_depth = 1
  }

  cache {
    type = "NO_CACHE"
  }
}