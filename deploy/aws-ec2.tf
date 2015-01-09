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

resource "aws_launch_configuration" "pac_as_conf" {
  name = "pac_auto_scale"
  image_id = "${var.pac_ami_id}"
  instance_type = "t1.micro"
  key_name = "joe-pac"
  security_groups = ["${aws_security_group.pac_instance_sg.id}"]
}

resource "aws_autoscaling_group" "pac_as" {
  availability_zones = ["us-east-1b", "us-east-1d"]
  vpc_zone_identifier = ["${aws_subnet.us-east-1b-private.id}", "${aws_subnet.us-east-1d-private.id}"]
  name = "pac-autoscaling-group"
  max_size = 2
  min_size = 2
  health_check_grace_period = 300
  health_check_type = "ELB"
  desired_capacity = 2
  force_delete = true
  launch_configuration = "${aws_launch_configuration.pac_as_conf.id}"
  load_balancers = ["${aws_elb.pac-elb.name}"]

  lifecycle {
    create_before_destroy = true
  }
  
  provisioner "local-exec" {
    command = "./waitFor.sh http://${aws_elb.pac-elb.dns_name} 5 120"
  }
  provisioner "local-exec" {
    command = "./route53.sh ${aws_route53_zone.primary.zone_id} ${aws_elb.pac-elb.name}"
  }
}

resource "aws_elb" "pac-elb" {
  name = "pac-elb"
  subnets = ["${aws_subnet.us-east-1b-public.id}", "${aws_subnet.us-east-1d-public.id}"]
  security_groups = ["${aws_security_group.pac_elb_sg.id}"]
  internal = false
  cross_zone_load_balancing = true
  
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
 
  lifecycle {
    create_before_destroy = true
  }
  
#  provisioner "local-exec" {
#    command = "./elb-stickiness.sh ${aws_elb.pac-elb.name}"
#  }
}

