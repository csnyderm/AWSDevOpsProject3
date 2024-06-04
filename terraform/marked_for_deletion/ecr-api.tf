#! MARKED FOR DELETION
resource "aws_ecrpublic_repository" "api" {
  repository_name = "api"

  tags = {
    env = "production"
  }
}

resource "aws_ecrpublic_repository_policy" "api-policy" {
  repository_name = aws_ecrpublic_repository.api.repository_name
  policy          = data.aws_iam_policy_document.team_cuttlefish_ecr_policy.json
}
