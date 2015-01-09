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

resource "aws_route53_record" "ns" {
  zone_id = "${aws_route53_zone.primary.zone_id}"
  name = "joescii.com."
  type = "NS"
  ttl = "172800"
  records = [
    "ns-1778.awsdns-30.co.uk.",
    "ns-1454.awsdns-53.org.",
    "ns-770.awsdns-32.net.",
    "ns-5.awsdns-00.com."
  ]
}

resource "aws_route53_record" "soa" {
  zone_id = "${aws_route53_zone.primary.zone_id}"
  name = "joescii.com."
  type = "SOA"
  ttl = "900"
  records = ["ns-1530.awsdns-63.org. awsdns-hostmaster.amazon.com. 1 7200 900 1209600 86400"]
}

resource "aws_route53_record" "cname" {
  zone_id = "${aws_route53_zone.primary.zone_id}"
  name = "www.joescii.com."
  type = "CNAME"
  ttl = "300"
  records = ["joescii.com"]
}

