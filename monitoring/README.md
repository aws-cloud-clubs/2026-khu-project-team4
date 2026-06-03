# 모니터링 설정 가이드 (Prometheus + Grafana)

## 구조

```
monitoring/
├── docker-compose.yml                          # Prometheus + Grafana 실행
├── prometheus.yml                              # Prometheus 스크래핑 설정
└── grafana/
    ├── provisioning/
    │   ├── datasources/prometheus.yml          # Prometheus 데이터소스 자동 등록
    │   └── dashboards/dashboard.yml            # 대시보드 자동 로드 설정
    └── dashboards/
        └── spring-boot-overview.json           # 사전 구성 대시보드
```

## 동작 원리

```
Spring Boot 앱
  └─ /actuator/prometheus (메트릭 노출)
        ↑ 15초마다 스크래핑 (Pull)
Prometheus
  └─ 시계열 데이터 저장
        ↑ PromQL 쿼리
Grafana
  └─ 대시보드 시각화
```

## EC2 배포 방법

### 1. prometheus.yml에서 Spring 앱 IP 수정

```yaml
# monitoring/prometheus.yml
static_configs:
  - targets:
      - '172.31.xx.xx:8080'  # EC2 private IP로 변경
```

### 2. Docker Compose 실행

```bash
cd monitoring
docker-compose up -d
```

### 3. 접속

| 서비스 | URL | 계정 |
|--------|-----|------|
| Grafana | http://{EC2_IP}:3000 | admin / admin |
| Prometheus | http://{EC2_IP}:9091 | - |

> EC2 보안 그룹에서 3000, 9091 포트를 열어야 합니다.  
> **운영 환경에서는 초기 비밀번호(admin)를 반드시 변경하세요.**

### 4. Grafana 대시보드 확인

Grafana 로그인 후 `Dashboards > Khunghap > Khunghap - Spring Boot 모니터링` 으로 이동하면 사전 구성된 대시보드가 자동으로 표시됩니다.

---

## 대시보드 패널 구성

| 패널 | 설명 |
|------|------|
| JVM CPU 사용률 | 앱 프로세스의 CPU 점유율 |
| JVM 힙 메모리 사용량 | 현재 힙 메모리 사용량 |
| DB 커넥션 풀 활성 연결 수 | HikariCP 활성 연결 수 |
| 5xx 에러 수 | 최근 1분간 서버 에러 발생 수 |
| HTTP 요청 처리량 | 초당 요청 수 (엔드포인트별) |
| HTTP 응답시간 | p50 / p95 / p99 레이턴시 |
| JVM 메모리 사용량 | 힙/논힙 시계열 |
| HikariCP 커넥션 풀 상태 | Active / Idle / Pending / Max |

---

## 추가 권장 설정

### Node Exporter (EC2 시스템 메트릭)
EC2 서버의 CPU, 메모리, 디스크, 네트워크 메트릭을 수집하려면:

```bash
# EC2에서 실행
docker run -d \
  --name node-exporter \
  --restart unless-stopped \
  -p 9100:9100 \
  prom/node-exporter
```

그 후 `prometheus.yml`에서 node-exporter 섹션 주석 해제

### 추천 Grafana 대시보드 (Import)
- **Spring Boot 2.1+**: ID `12900`
- **JVM Micrometer**: ID `4701`
- **Node Exporter Full**: ID `1860`

Grafana UI → Dashboards → Import → ID 입력
