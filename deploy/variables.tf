variable "access_key" {}
variable "secret_key" {}
variable "region" {
  default = "us-east-1"
}
variable "subnet_b" {
  default = "subnet-80e32cab"
}
variable "subnet_c" {
  default = "subnet-bce063cb"
}
variable "subnet_d" {
  default = "subnet-c00ba899"
}
variable "subnet_e" {
  default = "subnet-48432a72"
}

variable "pac_ami_id" {}
variable "pac_elb_name" {}