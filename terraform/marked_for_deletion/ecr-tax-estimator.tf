#! MARKED FOR DELETION
resource "aws_ecrpublic_repository" "tax-estimator" {
  repository_name = "tax-estimator"

  tags = {
    env = "production"
  }
}

resource "aws_ecrpublic_repository_policy" "tax-estimator-policy" {
  repository_name = aws_ecrpublic_repository.tax-estimator.repository_name
  policy          = data.aws_iam_policy_document.team_cuttlefish_ecr_policy.json
}
