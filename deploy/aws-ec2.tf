resource "aws_security_group" "pac_instance_sg" {
  name = "pac-instance-sg"
  description = "SG applied to each proseandconz app server instance"
	vpc_id = "${aws_vpc.default.id}"

  # HTTP access from anywhere
  ingress {
    from_port = 8080
    to_port = 8080
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "pac_elb_sg" {
  name = "pac-elb-sg"
  description = "SG applied to the proseandconz elastic load balancer"
	vpc_id = "${aws_vpc.default.id}"

  # HTTP access from anywhere
  ingress {
    from_port = 80
    to_port = 80
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_instance" "pac1" {
  ami = "${var.pac_ami_id}"
  instance_type = "t1.micro"
  subnet_id = "${aws_subnet.us-east-1b-private.id}"
  security_groups = ["${aws_security_group.pac_instance_sg.id}"]
  key_name = "joe-pac"
  tags {
    Name = "pac-srv"
  }
  
  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_instance" "pac2" {
  ami = "${var.pac_ami_id}"
  instance_type = "t1.micro"
  subnet_id = "${aws_subnet.us-east-1d-private.id}"
  security_groups = ["${aws_security_group.pac_instance_sg.id}"]
  key_name = "joe-pac"
  tags {
    Name = "pac-srv"
  }
  
  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_elb" "pac-elb" {
  name = "${var.pac_elb_name}"
  subnets = ["${aws_subnet.us-east-1b-public.id}", "${aws_subnet.us-east-1d-public.id}"]
  security_groups = ["${aws_security_group.pac_elb_sg.id}"]
  internal = false
  
  # Must manually set the session stickiness for now.  See https://github.com/hashicorp/terraform/issues/656
 
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
    interval = 5
  }
 
  instances = [
    "${aws_instance.pac1.id}",
    "${aws_instance.pac2.id}"
  ]
  
  lifecycle {
    create_before_destroy = true
  }
  
  provisioner "local-exec" {
    command = "./waitFor.sh http://${aws_elb.pac-elb.dns_name} 5 120"
  }
}

