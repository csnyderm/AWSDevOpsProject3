


resource "aws_sns_topic" "cuttlefish-topic" {
  name = var.sns_topic_name
}

resource "aws_sns_topic_subscription" "team_subscription" {
  topic_arn = aws_sns_topic.cuttlefish-topic.arn
  protocol = "email"
  endpoint = var.team_email
}


resource "aws_cloudwatch_dashboard" "team_cuttlefish_dashboard" {
  dashboard_name = var.dashboard_name
  dashboard_body = jsonencode({
    widgets = [
      {
        type = "metric",
        x = 0,
        y = 0,
        width = 12,
        height = 6,
        properties = {
          metrics = [
            ["AWS/EKS", "CPUUtilization", "ClusterName", data.aws_eks_cluster.cluster.name]
          ],
          view = "timeSeries",
          stacked = false,
          period = 300,
          stat = "Average",
          region = var.aws_region,
          title = "EKS Cluster CPU Utilization"
        }
      },
      {
        type = "metric",
        x = 12,
        y = 0,
        width = 12,
        height = 6,
        properties = {
          metrics = [
            ["AWS/EKS", "MemoryUtilization", "ClusterName", data.aws_eks_cluster.cluster.name]
          ],
          view = "timeSeries",
          stacked = false,
          period = 300,
          stat = "Average",
          region = var.aws_region,
          title = "EKS Cluster Memory Utilization"
        }
      },
      {
        type = "metric",
        x = 0,
        y = 6,
        width = 12,
        height = 6,
        properties = {
          metrics = [
            ["AWS/EKS", "PodCount", "ClusterName", data.aws_eks_cluster.cluster.name]
          ],
          view = "singleValue",
          region = var.aws_region,
          title = "EKS Pod Count"
        }
      },
      {
        type = "metric",
        x = 12,
        y = 6,
        width = 12,
        height = 6,
        properties = {
          metrics = [
            ["AWS/ApplicationELB", "RequestCount", "LoadBalancer", data.aws_elb.alb.name],
            ["AWS/ApplicationELB", "HTTPCode_ELB_4XX_Count", "LoadBalancer", data.aws_elb.alb.name],
            ["AWS/ApplicationELB", "HTTPCode_ELB_5XX_Count", "LoadBalancer", data.aws_elb.alb.name],
            ["AWS/ApplicationELB", "TargetResponseTime", "LoadBalancer", data.aws_elb.alb.name]
          ],
          view = "bar",
          stacked = true,
          region = var.aws_region,
          title = "ALB Metrics"
        }
      },
      {
        type = "metric",
        x = 0,
        y = 12,
        width = 12,
        height = 6,
        properties = {
          metrics = [
            ["AWS/EC2", "CPUUtilization", "InstanceId", data.aws_instance.ec2_instance.id]
          ],
          view = "gauge",
          region = var.aws_region,
          title = "EC2 CPU Utilization"
        }
      },
      {
        type = "metric",
        x = 12,
        y = 12,
        width = 12,
        height = 6,
        properties = {
          metrics = [
            ["AWS/EC2", "NetworkIn", "InstanceId", data.aws_instance.ec2_instance.id],
            ["AWS/EC2", "NetworkOut", "InstanceId", data.aws_instance.ec2_instance.id]
          ],
          view = "line",
          region = var.aws_region,
          title = "EC2 Network Traffic"
        }
      },
      {
        type = "metric",
        x = 0,
        y = 18,
        width = 12,
        height = 6,
        properties = {
          metrics = [
            ["AWS/DocDB", "CPUUtilization", "DBClusterIdentifier", var.ddb_cluster_id],
            ["AWS/DocDB", "FreeableMemory", "DBClusterIdentifier", var.ddb_cluster_id],
            ["AWS/DocDB", "DatabaseConnections", "DBClusterIdentifier", var.ddb_cluster_id]
          ],
          view = "pie",
          region = var.aws_region,
          title = "DocumentDB Metrics"
        }
      },
      {
        type = "metric",
        x = 12,
        y = 18,
        width = 12,
        height = 6,
        properties = {
          metrics = [
            ["AWS/Cognito", "SignInSuccesses", "UserPoolId", data.aws_cognito_user_pools.user_pool.id],
            ["AWS/Cognito", "SignInFailures", "UserPoolId", data.aws_cognito_user_pools.user_pool.id]
          ],
          view = "bar",
          region = var.aws_region,
          title = "Cognito Sign-In Metrics"
        }
      },
      {
        type = "metric",
        x = 0,
        y = 24,
        width = 12,
        height = 6,
        properties = {
          metrics = [
            ["AWS/S3", "BucketSizeBytes", "BucketName", data.aws_s3_bucket.s3_bucket.id],
            ["AWS/S3", "NumberOfObjects", "BucketName", data.aws_s3_bucket.s3_bucket.id]
          ],
          view = "line",
          region = var.aws_region,
          title = "S3 Bucket Metrics"
        }
      },
      {
        type = "metric",
        x = 12,
        y = 24,
        width = 12,
        height = 6,
        properties = {
          metrics = [
            ["AWS/CloudFront", "Requests", "DistributionId", data.aws_cloudfront_distribution.distribution.id],
            ["AWS/CloudFront", "BytesDownloaded", "DistributionId", data.aws_cloudfront_distribution.distribution.id],
            ["AWS/CloudFront", "4xxErrorRate", "DistributionId", data.aws_cloudfront_distribution.distribution.id],
            ["AWS/CloudFront", "5xxErrorRate", "DistributionId", data.aws_cloudfront_distribution.distribution.id]
          ],
          view = "bar",
          region = var.aws_region,
          title = "CloudFront Metrics"
        }
      }
    ]
  })
}

resource "aws_cloudwatch_metric_alarm" "high_cpu_utilization" {
  alarm_name          = "HighCPUUtilization"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = "1"
  metric_name         = "CPUUtilization"
  namespace           = "AWS/EC2"
  period              = "300"
  statistic           = "Average"
  threshold           = "80"
  alarm_description   = "Alarm when CPU utilization exceeds 80%"
  #alarm_actions       = ["arn:aws:sns:us-east-1:123456789012:MyTopic"]
  alarm_actions = [aws_sns_topic.cuttlefish-topic.arn]
  dimensions = {
    InstanceId = data.aws_instance.ec2_instance.id
  }
}

resource "aws_cloudwatch_metric_alarm" "high_error_rate_5xx" {
  alarm_name          = "High5xxErrorRate"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = "1"
  metric_name         = "HTTPCode_ELB_5XX_Count"
  namespace           = "AWS/ApplicationELB"
  period              = "300"
  statistic           = "Sum"
  threshold           = "5"
  alarm_description   = "Alarm when 5xx error rate exceeds 5%"
  #alarm_actions       = ["arn:aws:sns:us-east-1:123456789012:MyTopic"]
  alarm_actions = [aws_sns_topic.cuttlefish-topic.arn]
  dimensions = {
    LoadBalancer = data.aws_elb.alb.name
  }
}

resource "aws_cloudwatch_metric_alarm" "high_memory_utilization" {
  alarm_name          = "HighMemoryUtilization"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = "1"
  metric_name         = "MemoryUtilization"
  namespace           = "AWS/EKS"
  period              = "300"
  statistic           = "Average"
  threshold           = "80"
  alarm_description   = "Alarm when memory utilization exceeds 80%"
  #alarm_actions       = ["arn:aws:sns:us-east-1:123456789012:MyTopic"]
  alarm_actions = [aws_sns_topic.cuttlefish-topic.arn]
  dimensions = {
    ClusterName = data.aws_eks_cluster.cluster.name
  }
}

resource "aws_cloudwatch_metric_alarm" "low_freeable_memory_docdb" {
  alarm_name          = "LowFreeableMemoryDocDB"
  comparison_operator = "LessThanThreshold"
  evaluation_periods  = "1"
  metric_name         = "FreeableMemory"
  namespace           = "AWS/DocDB"
  period              = "300"
  statistic           = "Average"
  threshold           = "100000000"
  alarm_description   = "Alarm when freeable memory in DocumentDB is less than 100MB"
  #alarm_actions       = ["arn:aws:sns:us-east-1:123456789012:MyTopic"]
  alarm_actions = [aws_sns_topic.cuttlefish-topic.arn]
  dimensions = {
    DBClusterIdentifier = var.ddb_cluster_id
  }
}

resource "aws_cloudwatch_metric_alarm" "high_sign_in_failures_cognito" {
  alarm_name          = "HighSignInFailuresCognito"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = "1"
  metric_name         = "SignInFailures"
  namespace           = "AWS/Cognito"
  period              = "300"
  statistic           = "Sum"
  threshold           = "10"
  alarm_description   = "Alarm when the number of failed sign-ins in Cognito exceeds 10"
  #alarm_actions       = ["arn:aws:sns:us-east-1:123456789012:MyTopic"]
  alarm_actions = [aws_sns_topic.cuttlefish-topic.arn]
  dimensions = {
    UserPoolId = data.aws_cognito_user_pools.user_pool.id
  }
}

resource "aws_cloudwatch_metric_alarm" "high_database_connections_docdb" {
  alarm_name          = "HighDatabaseConnectionsDocDB"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = "1"
  metric_name         = "DatabaseConnections"
  namespace           = "AWS/DocDB"
  period              = "300"
  statistic           = "Average"
  threshold           = "100"
  alarm_description   = "Alarm when the number of database connections in DocumentDB exceeds 100"
  #alarm_actions       = ["arn:aws:sns:us-east-1:123456789012:MyTopic"]
  alarm_actions = [aws_sns_topic.cuttlefish-topic.arn]
  dimensions = {
    DBClusterIdentifier = var.ddb_cluster_id
  }
}

resource "aws_cloudwatch_metric_alarm" "s3_bucket_size" {
  alarm_name          = "S3BucketSize"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = "1"
  metric_name         = "BucketSizeBytes"
  namespace           = "AWS/S3"
  period              = "86400"
  statistic           = "Average"
  threshold           = "1000000000000"
  alarm_description   = "Alarm when S3 bucket size exceeds 1TB"
  #alarm_actions       = ["arn:aws:sns:us-east-1:123456789012:MyTopic"]
  alarm_actions = [aws_sns_topic.cuttlefish-topic.arn]
  dimensions = {
    BucketName = data.aws_s3_bucket.s3_bucket.bucket
  }
}

resource "aws_cloudwatch_metric_alarm" "high_4xx_error_rate_cloudfront" {
  alarm_name          = "High4xxErrorRateCloudFront"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = "1"
  metric_name         = "4xxErrorRate"
  namespace           = "AWS/CloudFront"
  period              = "300"
  statistic           = "Sum"
  threshold           = "5"
  alarm_description   = "Alarm when 4xx error rate in CloudFront exceeds 5%"
  #alarm_actions       = ["arn:aws:sns:us-east-1:123456789012:MyTopic"]
  alarm_actions = [aws_sns_topic.cuttlefish-topic.arn]
  dimensions = {
    DistributionId = data.aws_cloudfront_distribution.distribution.id
  }
}

resource "aws_cloudwatch_metric_alarm" "high_5xx_error_rate_cloudfront" {
  alarm_name          = "High5xxErrorRateCloudFront"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = "1"
  metric_name         = "5xxErrorRate"
  namespace           = "AWS/CloudFront"
  period              = "300"
  statistic           = "Sum"
  threshold           = "5"
  alarm_description   = "Alarm when 5xx error rate in CloudFront exceeds 5%"
  #alarm_actions       = ["arn:aws:sns:us-east-1:123456789012:MyTopic"]
  alarm_actions = [aws_sns_topic.cuttlefish-topic.arn]
  dimensions = {
    DistributionId = data.aws_cloudfront_distribution.distribution.id
  }
}
