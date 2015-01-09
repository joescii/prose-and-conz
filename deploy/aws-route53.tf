resource "aws_route53_zone" "primary" {
  name = "joescii.com"
}

# Alias records are not currently supported :( https://github.com/hashicorp/terraform/issues/28
#resource "aws_route53_record" "www" {
#  zone_id = "${aws_route53_zone.primary.zone_id}"
#  name = "joescii.com."
#  type = "A"
#  ttl = "300"
#  records = ["${aws_elb.pac-elb.dns_name}"]
#}

resource "aws_route53_record" "cname" {
  zone_id = "${aws_route53_zone.primary.zone_id}"
  name = "www.joescii.com."
  type = "CNAME"
  ttl = "300"
  records = ["joescii.com"]
}

