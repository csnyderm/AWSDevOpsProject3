#! MARKED FOR DELETION
resource "aws_ecrpublic_repository" "investments" {
  repository_name = "investments"

  tags = {
    env = "production"
  }
}

resource "aws_ecrpublic_repository_policy" "investments-policy" {
  repository_name = aws_ecrpublic_repository.investments.repository_name
  policy          = data.aws_iam_policy_document.team_cuttlefish_ecr_policy.json
}
