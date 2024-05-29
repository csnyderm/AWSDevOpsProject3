provider "aws" {
  region = var.aws_region
}

resource "aws_iam_role" "codepipeline_role" {
  name = "CodePipelineRole"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Principal = {
          Service = "codepipeline.amazonaws.com"
        },
        Action = "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_iam_policy" "codepipeline_policy" {
  name        = "CodePipelinePolicy"
  description = "Policy for CodePipeline access"
  
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect   = "Allow",
        Action   = [
          "codebuild:StartBuild",
          "s3:GetObject",
          "cloudwatch:PutMetricData",
          "eks:DescribeCluster"
        ],
        Resource = "*"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "codepipeline_policy_attach" {
  role       = aws_iam_role.codepipeline_role.name
  policy_arn = aws_iam_policy.codepipeline_policy.arn
}

resource "aws_iam_role" "codebuild_role" {
  name = "CodeBuildRole"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Principal = {
          Service = "codebuild.amazonaws.com"
        },
        Action = "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_iam_policy" "codebuild_policy" {
  name        = "CodeBuildPolicy"
  description = "Policy for CodeBuild access"
  
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect   = "Allow",
        Action   = [
          "s3:GetObject",
          "ecr:GetDownloadUrlForLayer",
          "cloudwatch:PutMetricData",
          "documentdb:DescribeDBClusters"
        ],
        Resource = "*"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "codebuild_policy_attach" {
  role       = aws_iam_role.codebuild_role.name
  policy_arn = aws_iam_policy.codebuild_policy.arn
}

resource "aws_iam_role" "eks_role" {
  name = "EKSClusterRole"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Principal = {
          Service = "eks.amazonaws.com"
        },
        Action = "sts:AssumeRole"
      }
    ]
  })
}
resource "aws_iam_policy" "eks_cluster_policy" {
  name        = "EKSClusterPolicy"
  description = "Policy for EKS Cluster Role"
  
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect   = "Allow",
        Action   = [
          "ec2:DescribeInstances",
          "ec2:DescribeNetworkInterfaces",
          "ec2:DescribeSecurityGroups",
          "ec2:DescribeSubnets",
          "ec2:DescribeVpcs",
          "elasticloadbalancing:DescribeLoadBalancers",
          "elasticloadbalancing:DescribeTargetGroups",
          "elasticloadbalancing:DescribeListeners",
          "cloudwatch:PutMetricData",
          "eks:DescribeCluster",
          "eks:CreateCluster",
          "eks:ListClusters",
          "eks:UpdateClusterConfig",
          "eks:DeleteCluster",
          "eks:DescribeNodegroup",
          "eks:CreateNodegroup",
          "eks:UpdateNodegroupConfig",
          "eks:DeleteNodegroup",
          "autoscaling:DescribeAutoScalingGroups",
          "autoscaling:DescribeLaunchConfigurations",
          "autoscaling:DescribeScalingActivities",
          "autoscaling:DescribeTags",
          "autoscaling:CreateOrUpdateTags",
          "autoscaling:UpdateAutoScalingGroup",
          "autoscaling:CreateAutoScalingGroup",
          "autoscaling:DeleteAutoScalingGroup",
          "autoscaling:AttachInstances",
          "autoscaling:DetachInstances",
          "autoscaling:TerminateInstanceInAutoScalingGroup",
          "autoscaling:SetDesiredCapacity"
        ],
        Resource = "*"
      }
    ]
  })
}
resource "aws_iam_role" "eks_nodegroup_role" {
  name = "AmazonEKSNodeRole"
  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Principal = {
          Service = "ec2.amazonaws.com"
        },
        Action = "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "eks_cni_policy" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKS_CNI_Policy"
  role       = aws_iam_role.eks_nodegroup_role.name
}

resource "aws_iam_role_policy_attachment" "eks_worker_node_policy" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSWorkerNodePolicy"
  role       = aws_iam_role.eks_nodegroup_role.name
}

resource "aws_iam_role_policy_attachment" "cloudwatch_agent_server_policy" {
  policy_arn = "arn:aws:iam::aws:policy/CloudWatchAgentServerPolicy"
  role       = aws_iam_role.eks_nodegroup_role.name
}

resource "aws_iam_role_policy_attachment" "ec2_container_registry_read_only" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryReadOnly"
  role       = aws_iam_role.eks_nodegroup_role.name
}

resource "aws_iam_role" "s3_role" {
  name = "S3Role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Principal = {
          Service = "s3.amazonaws.com"
        },
        Action = "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_iam_policy" "s3_policy" {
  name        = "S3Policy"
  description = "Policy for S3 access"
  
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect   = "Allow",
        Action   = [
          "s3:PutObject",
          "cloudfront:CreateInvalidation"
        ],
        Resource = "*"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "s3_policy_attach" {
  role       = aws_iam_role.s3_role.name
  policy_arn = aws_iam_policy.s3_policy.arn
}

resource "aws_iam_role" "cognito_role" {
  name = "CognitoRole"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Principal = {
          Service = "cognito-idp.amazonaws.com"
        },
        Action = "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_iam_policy" "cognito_policy" {
  name        = "CognitoPolicy"
  description = "Policy for Cognito access"
  
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect   = "Allow",
        Action   = [
          "cognito-idp:ListUsers",
          "cloudwatch:PutMetricData"
        ],
        Resource = "*"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "cognito_policy_attach" {
  role       = aws_iam_role.cognito_role.name
  policy_arn = aws_iam_policy.cognito_policy.arn
}

resource "aws_iam_role" "route53_role" {
  name = "Route53Role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Principal = {
          Service = "route53.amazonaws.com"
        },
        Action = "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_iam_policy" "route53_policy" {
  name        = "Route53Policy"
  description = "Policy for Route53 access"
  
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect   = "Allow",
        Action   = [
          "route53:ChangeResourceRecordSets"
        ],
        Resource = "*"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "route53_policy_attach" {
  role       = aws_iam_role.route53_role.name
  policy_arn = aws_iam_policy.route53_policy.arn
}
