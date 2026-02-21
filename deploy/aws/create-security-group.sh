#!/usr/bin/env bash
set -euo pipefail

: "${AWS_REGION:?Set AWS_REGION}"
: "${VPC_ID:?Set VPC_ID}"
: "${MY_IP:?Set MY_IP (e.g. 203.0.113.10/32)}"

SG_NAME=${SG_NAME:-ecommerce-microservices}
SG_DESC=${SG_DESC:-"Security group for ecommerce microservices"}
PUBLIC_PORTS=${PUBLIC_PORTS:-"8082"}
OPEN_INTERNAL_PORTS=${OPEN_INTERNAL_PORTS:-"false"}
VPC_CIDR=${VPC_CIDR:-""}

SG_ID=$(aws ec2 create-security-group \
  --region "$AWS_REGION" \
  --group-name "$SG_NAME" \
  --description "$SG_DESC" \
  --vpc-id "$VPC_ID" \
  --query 'GroupId' \
  --output text)

echo "Created security group: $SG_ID"

aws ec2 authorize-security-group-ingress \
  --region "$AWS_REGION" \
  --group-id "$SG_ID" \
  --protocol tcp \
  --port 22 \
  --cidr "$MY_IP"

echo "Opened SSH (22) for $MY_IP"

IFS=',' read -ra PORTS <<< "$PUBLIC_PORTS"
for port in "${PORTS[@]}"; do
  port=$(echo "$port" | xargs)
  if [[ -n "$port" ]]; then
    aws ec2 authorize-security-group-ingress \
      --region "$AWS_REGION" \
      --group-id "$SG_ID" \
      --protocol tcp \
      --port "$port" \
      --cidr "0.0.0.0/0"
    echo "Opened public TCP $port"
  fi
done

if [[ "$OPEN_INTERNAL_PORTS" == "true" ]]; then
  if [[ -z "$VPC_CIDR" ]]; then
    echo "Set VPC_CIDR to open internal ports safely (e.g. 10.0.0.0/16)." >&2
    exit 1
  fi
  for port in 8761 8080 8081 9898 8083; do
    aws ec2 authorize-security-group-ingress \
      --region "$AWS_REGION" \
      --group-id "$SG_ID" \
      --protocol tcp \
      --port "$port" \
      --cidr "$VPC_CIDR"
    echo "Opened internal TCP $port for $VPC_CIDR"
  done
fi

echo "Security group ready: $SG_ID"
