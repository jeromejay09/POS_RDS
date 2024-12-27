provider "aws" {
  region = "ap-southeast-1" # Update to your desired region
}

# S3 Bucket
resource "aws_s3_bucket" "pos_bucket" {
  bucket = "pos-system-bucket2"

  tags = {
    Name = "POS System Bucket"
  }
}

resource "aws_s3_bucket_acl" "pos_bucket_acl" {
  bucket = aws_s3_bucket.pos_bucket.id
  acl    = "private"
}

resource "aws_db_instance" "pos_system_db" {
  allocated_storage    = 20
  storage_type         = "gp2"
  instance_class       = "db.t3.micro"  # Correct parameter name is 'instance_class'
  engine               = "mysql"
  engine_version       = "8.0"
  identifier           = "pos-system-db"
  username             = "admin"
  password             = "admin123"
  db_name              = "posdb"
  skip_final_snapshot  = true
  multi_az             = false
  publicly_accessible  = true

  tags = {
    Name = "POS System Database"
  }
}

# Outputs
output "rds_endpoint" {
  value = aws_db_instance.pos_db.endpoint
}

output "s3_bucket_name" {
  value = aws_s3_bucket.pos_bucket.bucket
}
