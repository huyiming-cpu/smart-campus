# 智慧校园服务平台

## 技术栈
- **后端**: Spring Boot 4.1.0 + JDK 17 + JPA
- **前端**: Vue 3.5 + Vite 8 + Element Plus
- **数据库**: MySQL 8.4.5
- **AI**: DeepSeek

## 环境要求
| 工具 | 版本 |
|------|------|
| JDK | 17+ |
| Maven | 3.9+（或用 `./mvnw`） |
| Node.js | 18+ |
| MySQL | 8.0+ |

## 快速启动

### 1. 克隆仓库
```bash
git clone https://github.com/huyiming-cpu/smart-campus.git
cd smart-campus
```

### 2. 导入数据库
```bash
# 先创建数据库
mysql -uroot -p -P3306 -e "CREATE DATABASE IF NOT EXISTS smart_campus DEFAULT CHARACTER SET utf8mb4"

# 导入数据
mysql -uroot -p -P3306 smart_campus < smart_campus.sql
```

### 3. 配置数据库连接
修改 `smartcampus-back/src/main/resources/application.yaml` 中的数据库用户名和密码。

### 4. 启动后端
```bash
cd smartcampus-back
./mvnw spring-boot:run
# 或在 IDEA 中运行 SmartcampusApplication
```
后端运行在 `http://localhost:8080`

### 5. 启动前端
```bash
cd smartcampus-front
npm install
npm run dev
```
前端运行在 `http://localhost:5173`

## 测试账号（密码都是 123456）

| 角色 | 姓名 | 账号 |
|------|------|------|
| 学生 | 虎一铭 | 2023112588 |
| 教师 | 赵美婷 | 2023112618 |
| 领导 | 徐娅 | 2023112580 |
| 校长 | Ariana | 2023112589 |
| 管理员 | admin | admin |

## 项目结构
```
smart-campus/
├── smartcampus-back/     # Spring Boot 后端
│   └── src/main/java/com/smartcampus/smartcampus/
│       ├── controller/   # 接口层
│       ├── service/      # 业务层
│       ├── entity/       # 实体类（38张表）
│       ├── repository/   # 数据访问层
│       ├── security/     # JWT认证
│       ├── config/       # 配置
│       ├── common/       # 公共类
│       └── dto/          # 数据传输对象
├── smartcampus-front/    # Vue 3 前端
│   └── src/
│       ├── views/        # 页面（学生/教师/领导/管理员）
│       ├── router/       # 路由
│       ├── api/          # API 封装
│       ├── store/        # Pinia 状态管理
│       └── utils/        # 工具函数
├── smart_campus.sql      # 数据库脚本
└── README.md
```

## 功能模块（36个功能）
- **学生端**(9): 学习中心、考试中心、一卡通缴费、实验室、毕业设计、校园服务、公告新闻、个人信息、AI助手
- **教师端**(10): 教学工作台、科研管理、毕业设计、公文审批、工作计划、一卡通、通讯录、奖助贷、个人信息、AI助手
- **领导端**(8): 数据统计、公文审批、奖助贷审批、科研总览、工作计划、通讯录、个人信息、AI助手
- **管理员端**(9): 用户管理、课程管理、考试管理、一卡通缴费、固定资产、实验室、内容管理、系统配置、AI助手
