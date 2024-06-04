#! MARKED FOR DELETION

resource "aws_iam_user" "codecommit_user" {
  name = "team-cuttlefish-codecommit-user"

  tags = {
    team = "cuttlefish"
  }
}

resource "aws_iam_user_policy" "codecommit_policy" {
  name = "team-cuttlefish-codecommit-policy"
  user = aws_iam_user.codecommit_user.name

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "codecommit:GitPull",
          "codecommit:GitPush",
          "codecommit:ListRepositories",
          "codecommit:BatchGetRepositories"
        ],
        Resource = "*"
      }
    ]
  })
}

resource "aws_iam_user_ssh_key" "codecommit_ssh_key" {
  username   = aws_iam_user.codecommit_user.name
  public_key = file("~/.ssh/id_rsa.pub")
  encoding   = "SSH"
}