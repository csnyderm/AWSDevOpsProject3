#!/bin/bash

echo "What is the name of your cluster? "
read cluster_name

echo "What is the AWS Region you are working in? "
read aws_region

echo "Please provide a service account name for the load balancer: "
read service_account_name

echo "Please provide the policy ARN to attach: "
read policy_arn

# Start by switching into the context, just to be safe. Also, ensure the iam oidc is associated, to be sure
aws eks update-kubeconfig --region $aws_region --name $cluster_name
eksctl utils associate-iam-oidc-provide --region $aws_region --cluster $cluster_name --approve

# Next, create the IAM service account

