#? We should probably be looping, but I'll leave it for now.

resource "aws_ecrpublic_repository" "account-management" {
  repository_name = "account-management"

  tags = {
    env = "production"
    team = var.team
  }
}

resource "aws_ecrpublic_repository_policy" "account-management-policy" {
  repository_name = aws_ecrpublic_repository.account-management.repository_name
  policy          = var.ecr_policy
}


resource "aws_ecrpublic_repository" "api" {
  repository_name = "api"

  tags = {
    env = "production"
  }
}

resource "aws_ecrpublic_repository_policy" "api-policy" {
  repository_name = aws_ecrpublic_repository.api.repository_name
  policy          = var.ecr_policy
}


resource "aws_ecrpublic_repository" "budget-planning" {
  repository_name = "budget-planning"

  tags = {
    env = "production"
  }
}

resource "aws_ecrpublic_repository_policy" "budget-planning-policy" {
  repository_name = aws_ecrpublic_repository.budget-planning.repository_name
  policy          = var.ecr_policy
}

resource "aws_ecrpublic_repository" "eureka" {
  repository_name = "eureka"

  tags = {
    env = "production"
  }
}

resource "aws_ecrpublic_repository_policy" "eureka-policy" {
  repository_name = aws_ecrpublic_repository.eureka.repository_name
  policy          = var.ecr_policy
}


resource "aws_ecrpublic_repository" "frontend" {
  repository_name = "frontend"

  tags = {
    env = "production"
  }
}

resource "aws_ecrpublic_repository_policy" "frontend-policy" {
  repository_name = aws_ecrpublic_repository.frontend.repository_name
  policy          = var.ecr_policy
}


resource "aws_ecrpublic_repository" "investments" {
  repository_name = "investments"

  tags = {
    env = "production"
  }
}

resource "aws_ecrpublic_repository_policy" "investments-policy" {
  repository_name = aws_ecrpublic_repository.investments.repository_name
  policy          = var.ecr_policy
}


resource "aws_ecrpublic_repository" "tax-estimator" {
  repository_name = "tax-estimator"

  tags = {
    env = "production"
  }
}

resource "aws_ecrpublic_repository_policy" "tax-estimator-policy" {
  repository_name = aws_ecrpublic_repository.tax-estimator.repository_name
  policy          = var.ecr_policy
}
