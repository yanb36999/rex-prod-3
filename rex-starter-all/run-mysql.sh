#!/usr/bin/env bash
docker run -d --name rex-mysql \
-e TZ="Asia/Shanghai" \
-e MYSQL_ROOT_PASSWORD="ZMCSoft.rex#Mysql" \
-v /etc/localtime:/etc/localtime \
-v /opt/rex_mysql_data/:/var/lib/mysql/ \
-p 3306:3306 \
alisql/alisql

# CREATE DATABASE IF NOT EXISTS rex default charset utf8 COLLATE utf8_general_ci;