resource "aws_codecommit_repository" "frontend" {
  repository_name = "frontend"
  description     = "Frontend CodeCommit repository"
}

resource "aws_codecommit_repository" "api" {
  repository_name = "API"
  description     = "API CodeCommit repository"
}

resource "aws_codecommit_repository" "account_management" {
  repository_name = "account-management"
  description     = "Account Management CodeCommit repository"
}

resource "aws_codecommit_repository" "budget_planning" {
  repository_name = "budget-planning"
  description     = "Budget Planning CodeCommit repository"
}

resource "aws_codecommit_repository" "eureka" {
  repository_name = "eureka"
  description     = "Eureka CodeCommit repository"
}

resource "aws_codecommit_repository" "investments" {
  repository_name = "investments"
  description     = "Investments CodeCommit repository"
}

resource "aws_codecommit_repository" "tax_estimator" {
  repository_name = "tax-estimator"
  description     = "Tax Estimator CodeCommit repository"
}


