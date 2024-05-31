#!/bin/bash

if [[ $# -lt 3 ]]; then
  echo "Missing script input. This script requires the following format: <script> <cluster-name> <aws-region> <policy-arn>"
  echo "User will not be prompted for input instead. If you meant run with commandline arguments, please utilize the formatting above in your code and exit here"

  echo "What is the name of your cluster? "
  read cluster_name

  echo "What is the AWS Region you are working in? "
  read aws_region

  #echo "Please provide a service account name for the load balancer: "
  #read service_account_name

  echo "Please provide the policy ARN to attach: "
  read policy_arn

  # Start by switching into the context, just to be safe. Also, ensure the iam oidc is associated, to be sure
  aws eks update-kubeconfig --region $aws_region --name $cluster_name
  eksctl utils associate-iam-oidc-provider --region $aws_region --cluster $cluster_name --approve
else
  cluster_name=$1
  aws_region=$2
  policy_arn=$3
fi

# Next, create the IAM service account - Use this block if we are using the default
#curl -o iam-policy.json https://raw.githubusercontent.com/kubernetes-sigs/aws-load-balancer-controller/v2.7.2/docs/install/iam_policy.json
#policy_arn=$(aws iam create-policy --policy-name AWSLoadBalancerControllerIAMPolicy --policy-document file://iam-policy.json | jq .Policy.Arn)
#rm iam-policy.json # Clean up the file after

# Next, create the IAM service account - Use this block if we are getting input from policy_arn

eksctl create iamserviceaccount --cluster=$cluster_name --namespace=kube-system --name=aws-load-balancer-controller --attach-policy-arn=$policy_arn --override-existing-serviceaccounts --region $aws_region --approve


# Add EKS Chart Repo to Helm
helm repo add eks https://aws.github.io/eks-charts
helm repo update eks

# Finally, install the load balancer
helm install aws-load-balancer-controller eks/aws-load-balancer-controller -n kube-system --set clusterName=$cluster_name --set serviceAccount.create=false --set serviceAccount.name=aws-load-balancer-controller


echo "Please verify that the load balancer was installed in following output: "
echo $(kubectl get deployments -n kube-system)


