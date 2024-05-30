resource "aws_codebuild_project" "project" {
  count            = length(var.project_names)
  name             = var.project_names[count.index]
  service_role     = var.service_role
  artifacts {
    type = "NO_ARTIFACTS"
  }
  environment {
    compute_type    = "BUILD_GENERAL1_SMALL"
    image           = "aws/codebuild/standard:4.0"
    type            = "LINUX_CONTAINER"
    privileged_mode = false
  }
  source {
    type      = "CODECOMMIT"
    location  = "https://git-codecommit.${var.region}.amazonaws.com/v1/repos/${var.project_names[count.index]}"
  }
}
