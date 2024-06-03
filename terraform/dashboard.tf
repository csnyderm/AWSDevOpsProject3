provider "aws" {
  region = "us-east-1"
}

resource "aws_cloudwatch_dashboard" "team_cuttlefish_dashboard" {
  dashboard_name = "Team-Cuttlefish"
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
            ["AWS/EKS", "CPUUtilization", "ClusterName", "your-cluster-name"]
          ],
          view = "timeSeries",
          stacked = false,
          period = 300,
          stat = "Average",
          region = "us-east-1",
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
            ["AWS/EKS", "MemoryUtilization", "ClusterName", "your-cluster-name"]
          ],
          view = "timeSeries",
          stacked = false,
          period = 300,
          stat = "Average",
          region = "us-east-1",
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
            ["AWS/EKS", "PodCount", "ClusterName", "your-cluster-name"]
          ],
          view = "singleValue",
          region = "us-east-1",
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
            ["AWS/ApplicationELB", "RequestCount", "LoadBalancer", "app/my-load-balancer/50dc6c495c0c9188"],
            ["AWS/ApplicationELB", "HTTPCode_ELB_4XX_Count", "LoadBalancer", "app/my-load-balancer/50dc6c495c0c9188"],
            ["AWS/ApplicationELB", "HTTPCode_ELB_5XX_Count", "LoadBalancer", "app/my-load-balancer/50dc6c495c0c9188"],
            ["AWS/ApplicationELB", "TargetResponseTime", "LoadBalancer", "app/my-load-balancer/50dc6c495c0c9188"]
          ],
          view = "bar",
          stacked = true,
          region = "us-east-1",
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
            ["AWS/EC2", "CPUUtilization", "InstanceId", "i-1234567890abcdef0"]
          ],
          view = "gauge",
          region = "us-east-1",
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
            ["AWS/EC2", "NetworkIn", "InstanceId", "i-1234567890abcdef0"],
            ["AWS/EC2", "NetworkOut", "InstanceId", "i-1234567890abcdef0"]
          ],
          view = "line",
          region = "us-east-1",
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
            ["AWS/DocDB", "CPUUtilization", "DBClusterIdentifier", "your-docdb-cluster-id"],
            ["AWS/DocDB", "FreeableMemory", "DBClusterIdentifier", "your-docdb-cluster-id"],
            ["AWS/DocDB", "DatabaseConnections", "DBClusterIdentifier", "your-docdb-cluster-id"]
          ],
          view = "pie",
          region = "us-east-1",
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
            ["AWS/Cognito", "SignInSuccesses", "UserPoolId", "your-cognito-user-pool-id"],
            ["AWS/Cognito", "SignInFailures", "UserPoolId", "your-cognito-user-pool-id"]
          ],
          view = "bar",
          region = "us-east-1",
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
            ["AWS/S3", "BucketSizeBytes", "BucketName", "your-bucket-name"],
            ["AWS/S3", "NumberOfObjects", "BucketName", "your-bucket-name"]
          ],
          view = "line",
          region = "us-east-1",
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
            ["AWS/CloudFront", "Requests", "DistributionId", "E1V5LUPIOTZWHQ"],
            ["AWS/CloudFront", "BytesDownloaded", "DistributionId", "E1V5LUPIOTZWHQ"],
            ["AWS/CloudFront", "4xxErrorRate", "DistributionId", "E1V5LUPIOTZWHQ"],
            ["AWS/CloudFront", "5xxErrorRate", "DistributionId", "E1V5LUPIOTZWHQ"]
          ],
          view = "bar",
          region = "us-east-1",
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
  alarm_actions       = ["arn:aws:sns:us-east-1:123456789012:MyTopic"]
  dimensions = {
    InstanceId = "i-1234567890abcdef0"
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
  alarm_actions       = ["arn:aws:sns:us-east-1:123456789012:MyTopic"]
  dimensions = {
    LoadBalancer = "app/my-load-balancer/50dc6c495c0c9188"
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
  alarm_actions       = ["arn:aws:sns:us-east-1:123456789012:MyTopic"]
  dimensions = {
    ClusterName = "your-cluster-name"
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
  alarm_actions       = ["arn:aws:sns:us-east-1:123456789012:MyTopic"]
  dimensions = {
    DBClusterIdentifier = "your-docdb-cluster-id"
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
  alarm_actions       = ["arn:aws:sns:us-east-1:123456789012:MyTopic"]
  dimensions = {
    UserPoolId = "your-cognito-user-pool-id"
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
  alarm_actions       = ["arn:aws:sns:us-east-1:123456789012:MyTopic"]
  dimensions = {
    DBClusterIdentifier = "your-docdb-cluster-id"
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
  alarm_actions       = ["arn:aws:sns:us-east-1:123456789012:MyTopic"]
  dimensions = {
    BucketName = "your-bucket-name"
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
  alarm_actions       = ["arn:aws:sns:us-east-1:123456789012:MyTopic"]
  dimensions = {
    DistributionId = "E1V5LUPIOTZWHQ"
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
  alarm_actions       = ["arn:aws:sns:us-east-1:123456789012:MyTopic"]
  dimensions = {
    DistributionId = "E1V5LUPIOTZWHQ"
  }
}
