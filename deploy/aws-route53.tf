resource "aws_route53_zone" "primary" {
  name = "joescii.com"
}

# Alias records are not currently supported :( https://github.com/hashicorp/terraform/issues/28
#resource "aws_route53_record" "www" {
#  zone_id = "${aws_route53_zone.primary.zone_id}"
#  name = "proseand.co.nz."
#  type = "A"
#  ttl = "300"
#  records = ["${aws_elb.pac-elb.dns_name}"]
#}

resource "aws_route53_record" "cname" {
  zone_id = "${aws_route53_zone.primary.zone_id}"
  name = "www.proseand.co.nz."
  type = "CNAME"
  ttl = "300"
  records = ["proseand.co.nz"]
}

