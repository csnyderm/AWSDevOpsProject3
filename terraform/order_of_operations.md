#! MARKED FOR DELETION

# Order of Operations

## Foundational

- IAM
  - No dependencies, just need to be created and applied
- VPC
  - No dependencies

## Serverless

- S3 Bucket
  - Requires bucket to exist
  - Dependent on alias
  - ACM Certificate (Could be a data pull?)
- Cloudfront
  - Requires origin bucket
- S3 Website
  - Website is dependent on the bucket
  - Also dependent on the CloudFront origina access identity ARN

`NOTE: A circular dependency does not occur, as the outputs are updated in real time. So the bucket gets created, then used in Cloudfront as needed, and the Cloudfront piece is created and used by S3 as needed in parallel`

`These two should be set`


## Components

- EKS
  - Dependent on VPC
  - Dependent on IAM

- Cognito
  - Unsure?
  - IAM Dependency

- DocumentDB
  - Dependent on VPC
  - Dependent on Subnets
  - Dependent on SG to connect to

- CodeCommit
  - No Dependencies

- CodeBuild
  - Dependent on CodeCommit?
  - Dependent on IAM for role

- ECR
  - All Dependent on IAM
  - Does pipeline ship into here directly? In which case

- CodePipeline
  - Dependent on Codebuild?
  - Dependent on CodeCommit?
  - Might need to add in creating an artifact bucket (Maybe same inside)
  - Dependent on IAM for Role
  - Maybe dependent on ECR