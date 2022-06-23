# BookInfo-microservices

BookInfo-microservices is a book information service. It is a microservice architecture, implemented using Spring Boot and Spring Cloud. Business logic is presented through five microservices, where book, comment, rating and book-theme-night microservices represent core microservices, and book-composite represents integration of the core four microservices. Besides previously mentioned microservices, following Spring Cloud components were used:
* Spring Cloud Netflix Eureka - this component was used as Discovery Server. The Discovery Server is the key to the decentralized architecture. This is where routers, brokers and handlers announce themselves, and where you can look them up. 
* Spring Cloud Gateway - this component was used as Edge Server. The Gateway provides entry point to the microservice landscape, and it is used for routing to APIs and providing cross cutting concerns to them such as: security, monitoring/metrics etc.

## Persistence

Data Persistence is implemented using different Database Management Systems. In bookinfo microservice landscape, there are four core microservices which require permanently data storing, and these four microservices use following DBMSs:
*Book - MongoDB
*Comment - MongoDB
*BookThemeNight - MongoDB
*Rating - MySQL

The database schema is attached in the following picture, along with belonging attributes and data types.

The logic used for retrieving data for the book composite microservice is a non-blocking synchronous call to all four core microservices. This reactive approach implies that the requests for data are being sent in parallel to all four core microservices. The logic which is being used in this scenario is called Project Reactor, which uses two types of objects: Flux and Mono. Flux objects are used for streams that have 0 to N elements, while Mono objects are used for streams with 0 to 1 elements. BookInfo Microservices project is implemented in a way in which, if a GET request has been made to a currently unavailable microservice which returns Flux objects (Comment, BookThemeNight and Rating), instead of throwing an exception, it will return empty list. 

While retrieving data is implemented using synchronous communication, data creation and deletion is implemented via event-driven asynchronous approach. Composite microservice will publish CREATE or DELETE event that will go to the respective topic of a certain microservice. The response which is being sent back to the client will be 200 OK, no matter if the CREATE or DELETE operation was successfully executed or not, therefore allowing uninterrupted use. Events stored in respective topic will be consumed by appropriate core microservice. This has been implemented using Spring Cloud Stream, which also allows switching from one message system to another. Messaging systems that have been used in this project are RabbitMQ and Apache Kafka.

## Microservice landscape

Microservice landscape is attached in the following image.

## Prerequisites

* ``Homebrew`` - Homebrew installs packages for macOS or Linux. Installation guide can be found [here](https://docs.brew.sh/Installation) 
* ``Gradle`` - download Gradle using Homebrew and following command: ``brew install gradle``. 
* ``jq`` - download jq using following command: ``brew install jq``.
* ``Docker`` and ``docker-compose`` - download steps for installing Docker Desktop can be found [here](https://docs.docker.com/desktop/)

## Pipeline build/test/deploy
``./gradlew clean build && docker-compose build && docker-compose up -d``

After running command mentioned above, you can run ``bash test-em-all.bash`` command in order to test endpoints and general functionality of the system. Alternatively, you can execute bash script using following command ``bash test-em-all.bash start stop`` which starts the system, and eventually stops it, once all tests are executed. For stopping the system at any time, you can use following command ``docker-compose down``. 