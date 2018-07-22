#!/bin/sh

# Sample script to generate self-signed keystore, certificate and truststore files
# By Thanh Nguyen <btnguyen2k@gmail.com>

echo Generating server.keystore file...
keytool -genkey -noprompt -trustcacerts -keyalg RSA -alias ddthrecipes -dname "CN=localhost, OU=com.github.btnguyen2k, O=DDTH, L=HCM, ST=HCM, C=VN" -keypass s3cr3t -keystore server.keystore -storepass s3cr3t

echo Exporting server.cer from server.keystore file...
keytool -export -alias ddthrecipes -storepass s3cr3t -file server-temp.cer -keystore server.keystore

echo Generate client.truststore file...
echo yes | keytool -import -v -trustcacerts -alias ddthrecipes -file server-temp.cer -keystore client.truststore -keypass s3cr3t -storepass s3cr3t
rm server-temp.cer

echo Generating X509 server-grpc.key and server-grpc.key...
openssl req -x509 -newkey rsa:4096 -keyout server-grpc-nodes.key -out server-grpc.cer -days 365 -nodes -subj '/CN=localhost'

echo Generating server.p12, server.cer and server.key/server-nodes.key
keytool -importkeystore -srckeystore server.keystore -srcalias ddthrecipes -srcstorepass s3cr3t -destkeystore server.p12 -deststoretype PKCS12 -deststorepass s3cr3t -destkeypass s3cr3t
openssl pkcs12 -in server.p12 -nodes -nocerts -out server-nodes.key -password pass:s3cr3t
openssl pkcs12 -in server.p12 -nocerts -out server.key -password pass:s3cr3t -passout pass:s3cr3t
openssl pkcs12 -in server.p12 -nokeys -out server.cer -password pass:s3cr3t
#openssl pkcs12 -in server.p12 -nokeys -clcerts -out client.cer -password pass:s3cr3t
