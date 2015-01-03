provider "aws" {
    access_key = "${var.access_key}"
    secret_key = "${var.secret_key}"
    region = "${var.region}"
}

resource "aws_security_group" "pac_instance_sg" {
    name = "pac-instance-sg"
    description = "SG applied to each proseandconz app server instance"

    # SSH access from anywhere
    ingress {
        from_port = 22
        to_port = 22
        protocol = "tcp"
        cidr_blocks = ["0.0.0.0/0"]
    }

    # HTTP access from anywhere
    ingress {
        from_port = 80
        to_port = 80
        protocol = "tcp"
        cidr_blocks = ["0.0.0.0/0"]
    }
}

resource "aws_elb" "pac-elb" {
  name = "pac-elb"
  subnets = ["subnet-48432a72"]
  security_groups = ["${aws_security_group.pac_instance_sg.id}"]
 
  listener {
    instance_port = 8080
    instance_protocol = "http"
    lb_port = 80
    lb_protocol = "http" 
  }
 
  health_check {
    healthy_threshold = 2
    unhealthy_threshold = 2
    timeout = 3
    target = "HTTP:8080/"
    interval = 30
  }
 
  # instances = ["${aws_instance.api1.id}", "${aws_instance.api2.id}"]
}
 