resource "aws_route53_zone" "primary" {
   name = "joescii.com"
}

# Alias records are not currently supported :( https://github.com/hashicorp/terraform/issues/28
resource "aws_route53_record" "www" {
   zone_id = "${aws_route53_zone.primary.zone_id}"
   name = "joescii.com."
   type = "CNAME"
   ttl = "60"
   records = ["${aws_elb.pac-elb.dns_name}"]
}

resource "aws_route53_record" "ns" {
   zone_id = "${aws_route53_zone.primary.zone_id}"
   name = "joescii.com."
   type = "NS"
   ttl = "172800"
   records = ["ns-763.awsdns-31.net.", 
     "ns-1530.awsdns-63.org.",
     "ns-1566.awsdns-03.co.uk.",
     "ns-340.awsdns-42.com."
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

