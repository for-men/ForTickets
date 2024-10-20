services=("eureka" "concert-service" "order-service" "user-service" "gateway-service")
commit_hash=$(git rev-parse --short HEAD)

for service in "${services[@]}"
do
  imageName="$ECR_REGISTRY/$ECR_NAMESPACE/$service"

  # 커밋 해시 기반 태그 추가
  docker tag "$imageName:latest" "$imageName:$commit_hash"

  # ECR에 푸시
  docker push "$imageName:latest"
  docker push "$imageName:$commit_hash"

  echo "$service image tagged and pushed to ECR"
done

echo "All Docker images have been built and pushed."
