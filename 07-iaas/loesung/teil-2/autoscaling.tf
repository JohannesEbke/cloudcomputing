resource "aws_security_group" "app" {
  name        = "${local.env}-app"
  description = "Security Group for the App-Instances"
  vpc_id      = module.vpc.vpc_id
  tags        = local.standard_tags
}

resource "aws_security_group_rule" "app_ingress_ssh_all" {
  #checkov:skip=CKV_AWS_24:Ensure no security groups allow ingress from 0.0.0.0:0 to port 22

  security_group_id = aws_security_group.app.id
  description       = "Allows SSH from everywhere"
  type              = "ingress"
  from_port         = 22
  to_port           = 22
  protocol          = "tcp"
  cidr_blocks       = ["0.0.0.0/0"]
}

resource "aws_security_group_rule" "app_ingress_http_lb" {
  security_group_id        = aws_security_group.app.id
  description              = "Allows HTTP access from the load balancer"
  type                     = "ingress"
  from_port                = 8080
  to_port                  = 8080
  protocol                 = "tcp"
  source_security_group_id = aws_security_group.lb.id
}

resource "aws_security_group_rule" "app_egress_all" {
  security_group_id = aws_security_group.app.id
  description       = "Allows all outgoing traffic"
  type              = "egress"
  from_port         = 0
  to_port           = 0
  protocol          = "-1"
  cidr_blocks       = ["0.0.0.0/0"]
}

resource "aws_launch_template" "app" {
  #checkov:skip=CKV_AWS_88:EC2 instance should not have public IP

  name                                 = local.env
  image_id                             = "ami-07cb013c9ecc583f0"
  instance_initiated_shutdown_behavior = "terminate"
  update_default_version               = true
  instance_type                        = "t2.micro"

  network_interfaces {
    associate_public_ip_address = true
    security_groups             = [aws_security_group.app.id]
  }

  tag_specifications {
    resource_type = "instance"
    tags          = local.standard_tags
  }

  metadata_options {
    http_endpoint          = "enabled"
    http_tokens            = "required"
    instance_metadata_tags = "enabled"
  }

  user_data = base64encode(
    templatefile(
      "${path.module}/init.sh", { message = local.config.message }
    )
  )
}

resource "aws_autoscaling_group" "app" {
  name                = local.env
  min_size            = 0
  max_size            = 4
  desired_capacity    = 1
  vpc_zone_identifier = module.vpc.public_subnets
  health_check_type   = "ELB"
  target_group_arns   = [aws_lb_target_group.app.arn]

  launch_template {
    id = aws_launch_template.app.id
  }

  dynamic "tag" {
    for_each = local.standard_tags_asg
    content {
      key                 = tag.value.key
      propagate_at_launch = tag.value.propagate_at_launch
      value               = tag.value.value
    }
  }
}
