resource "aws_ecrpublic_repository" "frontend" {
  repository_name = "frontend"

  tags = {
    env = "production"
  }
}

resource "aws_ecrpublic_repository_policy" "frontend-policy" {
  repository_name = aws_ecrpublic_repository.frontend.repository_name
  policy          = data.aws_iam_policy_document.team_cuttlefish_ecr_policy.json
}
