terraform {
  required_providers {
    aws = {
      source = "hashicorp/aws"
      version = "5.20.1"
    }
  }

  required_version = ">= 1.1.0"
}

provider "aws" {
  region = "ap-southeast-1"  # Specify the region here
}

resource "aws_s3_bucket" "pos_bucket" {
  bucket = "pos-system-bucket2"
  acl    = "private"
}

resource "aws_db_instance" "pos_db" {
  allocated_storage    = 20
  engine               = "mysql"
  engine_version       = "8.0"
  instance_class       = "db.t2.micro"
  name                 = "posdb"
  username             = "admin"
  password             = "admin123"
  parameter_group_name = "default.mysql8.0"
  publicly_accessible  = true
}


output "rds_endpoint" {
  value = aws_db_instance.pos_system_db.endpoint
}

output "s3_bucket_name" {
  value = aws_s3_bucket.pos_system_bucket2.bucket
}
