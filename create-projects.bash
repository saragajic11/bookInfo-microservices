#!/bin/bash

mkdir microservices
cd microservices

spring init \
--boot-version=2.3.0.RELEASE \
--build=gradle \
--java-version=1.8 \
--packaging=jar \
--name=book-service \
--package-name=se.magnus.microservices.core.book \
--groupId=se.magnus.microservices.core.book \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
book-service

spring init \
--boot-version=2.3.0.RELEASE \
--build=gradle \
--java-version=1.8 \
--packaging=jar \
--name=rating-service \
--package-name=se.magnus.microservices.core.rating \
--groupId=se.magnus.microservices.core.rating \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
rating-service

spring init \
--boot-version=2.3.0.RELEASE \
--build=gradle \
--java-version=1.8 \
--packaging=jar \
--name=comment-service \
--package-name=se.magnus.microservices.core.comment \
--groupId=se.magnus.microservices.core.comment \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
comment-service

spring init \
--boot-version=2.3.0.RELEASE \
--build=gradle \
--java-version=1.8 \
--packaging=jar \
--name=book-theme-night-service \
--package-name=se.magnus.microservices.core.book-theme-night \
--groupId=se.magnus.microservices.core.book-theme-night \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
book-theme-night-service

spring init \
--boot-version=2.3.0.RELEASE \
--build=gradle \
--java-version=1.8 \
--packaging=jar \
--name=book-composite-service \
--package-name=se.magnus.microservices.composite.book \
--groupId=se.magnus.microservices.composite.book \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
book-composite-service

cd ..