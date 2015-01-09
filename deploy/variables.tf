variable "access_key" {}
variable "secret_key" {}
variable "aws_key_name" {
  default = "joe-pac"
}
variable "aws_nat_ami" {
	default = "ami-2e1bc047"
}
variable "aws_ubuntu_ami" {
	default = "ami-b227efda"
}
variable "region" {
  default = "us-east-1"
}

variable "pac_ami_id" {}
