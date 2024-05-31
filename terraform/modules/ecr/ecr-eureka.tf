resource "aws_ecrpublic_repository" "eureka" {
  repository_name = "eureka"

  tags = {
    env = "production"
  }
}

resource "aws_ecrpublic_repository_policy" "eureka-policy" {
  repository_name = aws_ecrpublic_repository.eureka.repository_name
  policy          = data.aws_iam_policy_document.team_cuttlefish_ecr_policy.json
}
