resource "aws_ecrpublic_repository" "account-management" {
  repository_name = "account-management"

  tags = {
    env = "production"
  }
}

resource "aws_ecrpublic_repository_policy" "account-management-policy" {
  repository_name = aws_ecrpublic_repository.account-management.repository_name
  policy          = data.aws_iam_policy_document.team_cuttlefish_ecr_policy.json
}
