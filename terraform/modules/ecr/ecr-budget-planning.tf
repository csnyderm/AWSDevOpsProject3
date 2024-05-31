resource "aws_ecrpublic_repository" "budget-planning" {
  repository_name = "budget-planning"

  tags = {
    env = "production"
  }
}

resource "aws_ecrpublic_repository_policy" "budget-planning-policy" {
  repository_name = aws_ecrpublic_repository.budget-planning.repository_name
  policy          = data.aws_iam_policy_document.team_cuttlefish_ecr_policy.json
}
