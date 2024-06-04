output "route53_role_arn" {
  value = aws_iam_role.route53_role.arn
}

output "cognito_role_arn" {
  value = aws_iam_role.cognito_role.arn
}

output "s3_role_arn" {
  value = aws_iam_role.s3_role.arn
}

output "eks_cluster_role_arn" {
  value = aws_iam_role.eks_role.arn
}

output "eks_nodegroup_role_arn" {
  value = aws_iam_role.eks_nodegroup_role.arn
}

output "alb_policy_arn" {
  value = aws_iam_policy.alb_policy.arn
}

output "codecommit_user_arn" {
  value = aws_iam_user.codecommit_user.arn
}

#! Add ECR Outputs here
#! Name of output: "ecr_policy_arn"


output "codebuild_role_arn" {
  value = aws_iam_role.codebuild_role.arn
}

output "codepipeline_role_arn" {
  value = aws_iam_role.codepipeline_role.arn
}