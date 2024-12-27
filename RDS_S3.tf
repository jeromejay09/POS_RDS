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

# RDS instance
resource "aws_db_instance" "pos_system_db" {
  allocated_storage    = 20
  storage_type         = "gp2"
  db_instance_class    = "db.t3.micro"
  engine               = "mysql"
  engine_version       = "8.0"
  instance_identifier  = "pos-system-db"
  username             = "admin"
  password             = "yourpassword"
  db_name              = "posdb"
  skip_final_snapshot  = true
  multi_az             = false
  publicly_accessible  = false

  tags = {
    Name = "POS System Database"
  }
}

output "rds_endpoint" {
  value = aws_db_instance.pos_system_db.endpoint
}

output "s3_bucket_name" {
  value = aws_s3_bucket.pos_system_bucket.bucket
}
