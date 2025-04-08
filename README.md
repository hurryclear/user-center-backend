# Introduction

This is Backend App for user center.

# Stack

- Java
- Spring: Framework for dependencies insert, help to manage Java objects
- SpringMVC: Web Framework, provides API access, RESTful API
- MyBatis: Framework to help java operate database
- MyBatis-plus: MyBatis strengthen, no need SQL for CRUD
- SpringBoot: quick start (boot), don't need to manage spring dependencies/setup

# Setup

- initial spring project with Spring Initializer in IDEA
  - #what is Maven?
- How to use MyBatis-plus? [see documentation](https://baomidou.com/getting-started/install/) 
  - MyBatis-plus dependency
  - follow quick-start in documentation
  - what is Mapper?
    - for MyBatis you define the CRUD in mapper
- application.properties --> application.yml
  - .yml is eaiser to write when there are more layers
  - add config of data base
  - add port of this backend
  - add config of mabatis-plus
- 

# Database

```sql
-- auto-generated definition
create table user
(
    id           bigint auto_increment
        primary key,
    username     varchar(256)      null,
    userAccount  varchar(256)      null,
    avatarUrl    varchar(1024)     null,
    gender       tinyint           null,
    userPassword varchar(512)      not null,
    phone        varchar(128)      null,
    email        varchar(512)      null,
    userStatus   int     default 0 not null,
    createTime   datetime          null,
    updateTime   datetime          null,
    isDelete     tinyint default 0 not null,
    userRole     int     default 0 null
);
```

- isDelete: logic delete, in the real-world company normally don't really delete user data, but just set to 0



## create Table

1. using DDL
2. using right-click table --> new table

## aws mysql databse

- How to connect aws mysql in idea:

  - make sure public access

    <img src="README.assets/image-20250407131103629.png" alt="image-20250407131103629" style="zoom:67%;" />

  - allow connect port 3306

    <img src="README.assets/image-20250407131236241.png" alt="image-20250407131236241" style="zoom:40%;" />

  - connect database in idea

    <img src="README.assets/image-20250407130809623.png" alt="image-20250407130809623" style="zoom:80%;" />



# Model <--> Database

- Layers: controller, service, utils, mapper, model

- connect database table to model objects and generate objects automatically

  - MyBatisX-Generator Plugin --> what will be generated?
    - domain (objects)
    - mapper (operate objects in database)
    - mapper.xml (connection between mapper objects and database, SQL operations here)
    - service (logical business, CRUD)
    - serviceImpl (implements service)

- let's test service: UserServiceTest

  - new User
  - setter, getter
  - test user.save( )
  - #got problem with lombok and solve and wrote here: https://www.codefather.cn/post/1864728138141466625 
  

# Service

## Registration

1. user register from frontend with userAccount, userPassword, checkPassword, planetCode
2. check userAccount, userPassword, checkPassword, planetCode
   1. not null
   2. account not shorter than 4 digits
   3. password not shorter than 8 digits
   4. unique account, planet code
   5. no special character in account
   6. userPassword and checkPassword are same
3. encrypt password!!!
4. save in database

## Login

1. check userAccount, userPassword --> same with registration 
   1. not null
   2. account not shorter than 4 digits
   3. password not shorter than 8 digits
   4. no special character in account
2. is userPassword correct? same with encryptPassword in database --> query in database
3. What is user is deleted logically?
4. hide sensitive information of user (password)
   1. return a new/safe user
   2. new user instance and set parameters
5. record session of user and store it in server (SpringBoot tomcat; frontend cookie)
   1. session (backend) and cookie (frontend)
   2. How to know which user login? 
6. return user without sensitive information

# Controller

- Provoide API to Frontend

- Communication between Frontend and Backend

  - user input their account, password from Frontend

  - these information will be packed into json file (common use)

  - json file will be passed to Backend

  - Backend will need json file to "connect" with objects in backend

  - for example: Login

    - user gives account and password

    - account and password will be packed in json file

      ```json
      {
        "userAccount": hurryclear,
        "userPassword": 111111111
      }
      ```

    - Backend connect this json file with `UserLoginRequest` through `@RequestBody` and translate json object to object in backend

  - Why do we need `UserLoginRequest` and `UserRegisterRequest`? And why implements with `Serializable`? #todo

- Java Interface and Class

  - we import UserService in Controller instead of UserServiceImp, why?#todo

- API test tool: generated-requests.http in IDEA

  - for example: login

  - ```http
    POST http://localhost:8080/api/user/login
    Content-Type: application/json
    
    {
      "userAccount":"hurryclear",
      "userPassword": 123456789
    }
    ```

    - suppose we got json file from Frontend: {...}
    - we pass it to Backend with .../user/login API
    - if it works good, will return User in json file

- 

## User Manage

- check role
  - two kinds for now
    - default: user --> 0
    - admin --> 1
  - "role" add into user table in database
    - int
    - default value: 0
  - "role" add into `User` Class
- search and delete
  - `@PostMapping("/search")` and `@PostMapping("/delete")`
  - using methods in `userService`, there are methods which are implemented from `IRepository` (MyBatis-plus)

## Login-state

https://blog.csdn.net/LAM1006_csdn/article/details/120440394

https://www.cnblogs.com/88223100/p/One-article-to-understand-the-common-login-schemes-of-the-front-and-rear-end.html

### Login state/authentication state/登录态

- the system will remember you after your login, so that the users don't can switch between different pages

### Session-based login state

<img src="README.assets/image-20250324205800776.png" alt="image-20250324205800776" style="zoom:80%;" />

- session: server create and manage
- cookie: client (browser) manage and store
- Steps:
  - After logging in, the server saves user information in a **session**.
  - A session ID is sent to the client as a **cookie**.
  - When the client sends the session ID with each request, the server knows which user is logged in.
- Cons: server needs storage for session

### Token-based login state

<img src="README.assets/image-20250324210219733.png" alt="image-20250324210219733" style="zoom:80%;" />

- Traditional token
  - The server generates a **token** (JWT) after login and sends it to the client.
  - The client must include this token in the Authorization header for future requests.
  - server validates the token without storing session data (stateless).
  - <img src="README.assets/image-20250324210730128.png" alt="image-20250324210730128" style="zoom:80%;" />
  - everytime has to query databse to know the validity of the token --> consums the performance
- JWT
  - Json Web Token
  - 3 parts
    - header
    - payload
    - signature
    - <img src="README.assets/image-20250324220407110.png" alt="image-20250324220407110" style="zoom:80%;" />
  - 

## get current user

```java
@GetMapping("/current")
public User getCurrentUser(HttpServletRequest request) {
  Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
  User currentUser = (User) userObj;
  if (currentUser == null) {
    return null;
  }
  long userId = currentUser.getId();
  User user = userService.getById(userId);
  return userService.getSafetyUser(user);
}
```

## Logout

- Backend: remove user attribute which saved in session when user login
- Frontend: 

# Communication between Backend and Frontend



## Proxy

- forward proxy and reverse proxy

- Nginx server or Node.js server

- Ant Design Pro has `proxy.ts` for us to set proxy (forward proxy)

  ```typescript
  // localhost:8000/api/** -> https://preview.pro.ant.design/api/**
      '/api': {
        // 要代理的地址
        target: 'http://localhost:8080/',
        // 配置了这个可以从 http 代理到 https
        // 依赖 origin 的功能可能需要这个，比如 cookie
        changeOrigin: true,
      },
  ```

- 

# Backend Optimization

## 通用返回对象

- http also has state code, but that's not accurate enough
- we can define our own state code which returns to frontend, so frontend can know the state in details

```json
{
  "name":
}
  
{
  "code": 00 // state code
  "data": {
	  "name": 
	},
	"message": "ok"
}
```

## 封装全局异常处理

<img src="README.assets/image-20250327145843109.png" alt="image-20250327145843109" style="zoom:67%;" />

## 全局请求日志和登录校验

# Frontend Optimization

<img src="README.assets/image-20250327163201426.png" alt="image-20250327163201426" style="zoom:67%;" />

# Frontend

- using Ant Design Pro as the framework and adjust the code accordingly
- 

## ProComponents

### Pro Table

- https://procomponents.ant.design/en-US/components/table?tab=api&current=1&pageSize=5
- Find the component you need, copy the src code and add into your code



# Multi-environment

## Classification

1. local env
2. dev env: remote, everyone connect to one same server
3. test env
4. pre env: must be exactly same as prod env
5. prod env
6. sandbox env: for experiment



## frontend - umi

- request address

  - local env: localhost:8000
  - prod env: hurryclear.user-center.de

- project conifguration

  - it depends on the project
  - umi uses config files

- umi framework will help to recognize "which env"

  - `"analyze": "cross-env ANALYZE=1 max build"`
  - `"start": "cross-env UMI_ENV=dev max dev"`
  - process.env.NODE_ENV is a constant which stores the different envs:
    `const isDev = process.env.NODE_ENV === 'development';`

- build

  - dev env: `npm run start`
  - prod env: `npm run build` (project build and package)
  - dirctory: `./dist` to store the files which produced after build
  - use tool: "serve" to test the built project in `.\dist` 

- change request address accordingly

  ```typescript
  /**
   * 配置request请求时的默认参数
   */
  const request = extend({
    credentials: 'include', // 默认请求是否带上cookie
    prefix: process.env.NODE_ENV === 'production' ? 'http://user-backend.code-nav.cn' : undefined
    // requestType: 'form',
  });
  ```

- 页面静态化 in umijs: `exportStatic: {}`

  - if we use `exportStatic: {}`, we will get `???.html` for every single page, so every page will be accessible
  - but if we don't use it, then there will be only one `index.html` and how can we get access to the other pages?
    - the answer is: dynamic page
    - this need to be set through Nginx or other web services

## backend - springboot

> - Multi-environment in backend (Spring Boot) is implemented through config file `application.yml` and `application-prod.yml` , `application-{env}.yml` in general
> - start the project by default, then the `application.yml` will be used as the config file
> - or start he project with other `.yml` file / different config, e.g. `java -jar ./user-center-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod`

- `application-prod.yml` file
  - different setting, e.g. online database
  - <img src="README.assets/image-20250407151902143.png" alt="image-20250407151902143" style="zoom:80%;" />
- package the project with maven tool
  - Lifecycle: package
  - after package will get a `jar` file
- start the project with this command: `java -jar ./user-center-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod` 
- 



# Deploy

- nginx: web-server and reverse proxy server

- what can nginx do?

  | **Function**            | **Description**                                              |
  | ----------------------- | ------------------------------------------------------------ |
  | **Web server**          | Serves static content like HTML, CSS, JS, images, etc.       |
  | **Reverse proxy**       | Forwards client requests to backend apps (like Spring Boot, Node.js, etc.) |
  | **Load balancer**       | Distributes traffic across multiple backend servers          |
  | **SSL/TLS termination** | Handles HTTPS encryption (via SSL certificates)              |
  | **Caching**             | Speeds up repeated requests by storing results               |
  | **Compression**         | Reduces size of content using gzip, improving speed          |

## original

> login your own cloud server (I use aws EC2 for free - 12 months)

- 

## docker

### frontend

- nginx as web-server need to be installed first on the server (remote/cloud..)

- add nginx configuration in the project directory: `./docker/nginx.conf` 

  ```nginx
  server {
      listen 80;
  
      # gzip config
      gzip on;
      gzip_min_length 1k;
      gzip_comp_level 9;
      gzip_types text/plain text/css text/javascript application/json application/javascript application/x-javascript application/xml;
      gzip_vary on;
      gzip_disable "MSIE [1-6]\.";
  
      root /usr/share/nginx/html;
      include /etc/nginx/mime.types;
  
      location / {
          try_files $uri /index.html;
      }
  
  }
  ```

- add Dockerfile

  ```dockerfile
  FROM nginx
  
  WORKDIR /usr/share/nginx/html/
  USER root
  
  COPY ./docker/nginx.conf /etc/nginx/conf.d/default.conf
  
  COPY ./dist /usr/share/nginx/html/
  
  EXPOSE 80
  
  CMD ["nginx", "-g", "daemon off;"]
  ```

- build docker image

  ```bash
  docker buildx create --use
  
  docker buildx build --platform=linux/amd64 -t user-center-frontend:v0.0.1 .
  ```

  - the remote server i used is aws ec2 free tier, so my docker image has to be built in same machine "linux/amd64", otherwise will get error

- test docker image locally

  ```bash
  docker run -p 80:80 -d user-center-frontend:v0.0.1
  ```

- push docker image in docker hub

  - tag the local image for docker hub

    ```bash
    docker tag user-center-frontend:v0.0.1 hurryclear/my-web:v0.0.1
    ```

  - push the image to docker hub

    ```bash
    docker push hurryclear/my-web:v0.0.1
    ```

- login remote server and pull docker image from docker hub

  - aws ec2 

  - install dokcer: `sudo yum install -y docker`

  - start docker `sudo systemctl start docker`

  - pull docker image

    ```bash
    docker pull hurryclear/my-web:v0.0.1

- run docker image

  ```bash
  docker run -p 80:80 -d user-center-frontend:v0.0.1
  ```

### backend

