#!/bin/bash
# Ferry Mall 一键启动脚本
# Usage: ./start.sh [backend|admin|miniapp|all]

set -e

# 颜色
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"

print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[OK]${NC} $1"
}

print_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

check_java() {
    if ! command -v java &> /dev/null; then
        print_error "未找到 Java，请先安装 JDK 21+"
        exit 1
    fi
    JAVA_VERSION=$(java -version 2>&1 | head -n1 | cut -d'"' -f2)
    print_info "Java 版本: $JAVA_VERSION"
}

check_maven() {
    if ! command -v mvn &> /dev/null; then
        print_error "未找到 Maven，请先安装 Maven 3.9+"
        exit 1
    fi
    MVN_VERSION=$(mvn -version 2>&1 | head -n1)
    print_info "Maven: $MVN_VERSION"
}

check_node() {
    if ! command -v node &> /dev/null; then
        print_error "未找到 Node.js，请先安装 Node.js 18+"
        exit 1
    fi
    NODE_VERSION=$(node --version)
    print_info "Node.js 版本: $NODE_VERSION"
}

check_redis() {
    if command -v redis-cli &> /dev/null && redis-cli ping &> /dev/null; then
        print_success "Redis 已运行"
        return 0
    fi
    if docker ps --format '{{.Names}}' | grep -q 'ferry-redis'; then
        print_success "Redis Docker 容器已运行"
        return 0
    fi
    print_warn "Redis 未运行，尝试启动 Docker Redis..."
    if command -v docker &> /dev/null; then
        docker run -d -p 6379:6379 --name ferry-redis redis:7-alpine &> /dev/null || true
        sleep 2
        if docker ps --format '{{.Names}}' | grep -q 'ferry-redis'; then
            print_success "Redis Docker 容器已启动"
            return 0
        fi
    fi
    print_error "无法启动 Redis，请手动安装 Redis 或使用 Docker"
    return 1
}

start_backend() {
    print_info "启动后端服务..."
    cd "$PROJECT_ROOT/ferry-mall-server"

    # 检查是否已编译
    if [ ! -d "ferry-server/target/classes" ]; then
        print_info "首次编译，请稍等..."
        mvn compile -q -pl ferry-server -am
    fi

    print_info "后端服务启动中（端口 48080，按 Ctrl+C 停止）..."
    SPRING_PROFILES_ACTIVE=local mvn spring-boot:run -pl ferry-server -q &
    BACKEND_PID=$!

    # 等待启动
    for i in {1..60}; do
        if curl -s http://localhost:48080/actuator/health &> /dev/null; then
            print_success "后端服务已启动: http://localhost:48080"
            print_info "Swagger 文档: http://localhost:48080/doc.html"
            print_info "H2 Console: http://localhost:48080/h2-console"
            return 0
        fi
        sleep 1
    done

    print_error "后端服务启动超时"
    return 1
}

start_admin() {
    print_info "启动 Admin 管理后台..."
    cd "$PROJECT_ROOT/ferry-mall-admin"

    if [ ! -d "node_modules" ]; then
        print_info "安装依赖..."
        npm install
    fi

    print_info "Admin 启动中（端口 5173）..."
    npm run dev &
    ADMIN_PID=$!

    for i in {1..30}; do
        if curl -s http://localhost:5173 &> /dev/null; then
            print_success "Admin 已启动: http://localhost:5173"
            print_info "默认账号: admin / admin123"
            return 0
        fi
        sleep 1
    done

    print_warn "Admin 可能需要更长时间启动，请稍后访问 http://localhost:5173"
    return 0
}

build_miniapp() {
    print_info "编译小程序..."
    cd "$PROJECT_ROOT/ferry-mall-miniapp"

    if [ ! -d "node_modules" ]; then
        print_info "安装依赖..."
        npm install
    fi

    print_info "编译微信小程序到 dist 目录..."
    npm run build:weapp

    print_success "小程序编译完成"
    print_info "请用微信开发者工具导入: $PROJECT_ROOT/ferry-mall-miniapp/dist"
}

show_help() {
    cat << EOF
Ferry Mall 一键启动脚本

用法: ./start.sh [命令]

命令:
  all       启动全部服务（后端 + Admin + Redis）
  backend   仅启动后端服务
  admin     仅启动 Admin 管理后台
  miniapp   仅编译小程序
  help      显示帮助

示例:
  ./start.sh all       # 启动后端 + Admin
  ./start.sh backend   # 仅启动后端
  ./start.sh miniapp   # 编译小程序

EOF
}

# 主逻辑
CMD=${1:-all}

case $CMD in
    help|--help|-h)
        show_help
        exit 0
        ;;
    backend)
        check_java
        check_maven
        check_redis
        start_backend
        print_info "后端 PID: $BACKEND_PID"
        wait $BACKEND_PID
        ;;
    admin)
        check_node
        start_admin
        print_info "Admin PID: $ADMIN_PID"
        wait $ADMIN_PID
        ;;
    miniapp)
        check_node
        build_miniapp
        ;;
    all)
        check_java
        check_maven
        check_node
        check_redis

        start_backend
        start_admin

        echo ""
        print_success "=== 所有服务已启动 ==="
        echo ""
        echo "  后端 API:     http://localhost:48080"
        echo "  Swagger 文档: http://localhost:48080/doc.html"
        echo "  Admin 后台:   http://localhost:5173  (admin/admin123)"
        echo "  H2 Console:   http://localhost:48080/h2-console"
        echo ""
        echo "  按 Ctrl+C 停止所有服务"
        echo ""

        wait
        ;;
    *)
        print_error "未知命令: $CMD"
        show_help
        exit 1
        ;;
esac
