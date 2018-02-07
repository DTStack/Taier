#!/bin/bash

# auto install nginx
if [ -s "/usr/sbin/nginx" ]; then
    echo "Nginx have installed."
else
    echo "Auto install nginx."
    yum install nginx
fi

order=$1

# ============ do order ============ #
# Rewrite nginx conf
site_conf="dt-rdos-offline.conf"
nginx_conf="/etc/nginx/conf.d/$site_conf"

rm $nginx_conf
cp "./$site_conf" /etc/nginx/conf.d

echo "The site configuration of nginx have installed."

systemctl start nginx;

echo "Nginx have started."
