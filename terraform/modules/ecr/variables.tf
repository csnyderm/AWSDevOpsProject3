variable "repository_names" {
  description = "List of ECR public repository names"
  type        = list(string)
  #default     = ["one", "two", "three", "four", "five", "six", "seven"]
}

variable "team" {
  description = "Team name"
  type        = string
  default     = "cuttlefish"
}

variable "ecr_policy" {
  description = "ECR policy JSON"
  type        = string
}