resource "aws_cognito_user_pool" "team_cuttlefish_pool" {
  name                     = "team-cuttlefish-pool"
  auto_verified_attributes = ["email", "phone_number"]
  username_attributes      = ["email", "phone_number"]

  password_policy {
    minimum_length    = 8
    require_uppercase = true
    require_lowercase = true
    require_numbers   = true
    require_symbols   = true
  }

    account_recovery_setting {
    recovery_mechanism {
      name     = "verified_email"
      priority = 1
    }

    recovery_mechanism {
      name     = "verified_phone_number"
      priority = 2
    }
  }
}

resource "aws_cognito_user_pool_client" "team_cuttlefish_client" {
  name         = "team-cuttlefish-client"
  user_pool_id = aws_cognito_user_pool.team_cuttlefish_pool.id

  explicit_auth_flows = [
    "ALLOW_USER_PASSWORD_AUTH",
    "ALLOW_REFRESH_TOKEN_AUTH",
    "ALLOW_USER_SRP_AUTH"
  ]

  allowed_oauth_flows = ["code", "implicit"]
  allowed_oauth_scopes = [
    "phone",
    "email",
    "openid",
    "profile",
    "aws.cognito.signin.user.admin"
  ]

  supported_identity_providers = ["COGNITO"]

  callback_urls = ["https://team-cuttlefish.aws-tfbd.com"]
  logout_urls   = ["https://team-cuttlefish.aws-tfbd.com"]

  prevent_user_existence_errors = "ENABLED"
}

resource "aws_cognito_user_pool_domain" "team_cuttlefish_domain" {
  domain       = "team-cuttlefish-domain"
  user_pool_id = aws_cognito_user_pool.team_cuttlefish_pool.id
  certificate_arn = aws_acm_certificate.team_cuttlefish_cert.arn
  cloudfront_distribution = "doexvkza8jlwz.cloudfront.net"
  cloudfront_distribution_arn = "arn:aws:cloudfront::785169158894:distribution/E1V5LUPIOTZWHQ"
  cloudfront_distribution_zone_id = "E1V5LUPIOTZWHQ"
}
