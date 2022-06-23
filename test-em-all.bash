# Sample usage:
#
#   HOST=localhost PORT=7000 ./test-em-all.bash
#
: ${HOST=localhost}
: ${PORT=8080}
: ${BOK_ID_RAT_COM_BTN=2}
: ${BOK_ID_NOT_FOUND=13}
: ${BOK_ID_NO_COM_NO_BTN=114}
: ${BOK_ID_NO_RAT_NO_BTN=214}

function assertCurl() {

  local expectedHttpCode=$1
  local curlCmd="$2 -w \"%{http_code}\""
  local result=$(eval $curlCmd)
  local httpCode="${result:(-3)}"
  RESPONSE='' && (( ${#result} > 3 )) && RESPONSE="${result%???}"

  if [ "$httpCode" = "$expectedHttpCode" ]
  then
    if [ "$httpCode" = "200" ]
    then
      echo "Test OK (HTTP Code: $httpCode)"
    else
      echo "Test OK (HTTP Code: $httpCode, $RESPONSE)"
    fi
  else
      echo  "Test FAILED, EXPECTED HTTP Code: $expectedHttpCode, GOT: $httpCode, WILL ABORT!"
      echo  "- Failing command: $curlCmd"
      echo  "- Response Body: $RESPONSE"
      exit 1
  fi
}

function assertEqual() {

  local expected=$1
  local actual=$2

  if [ "$actual" = "$expected" ]
  then
    echo "Test OK (actual value: $actual)"
  else
    echo "Test FAILED, EXPECTED VALUE: $expected, ACTUAL VALUE: $actual, WILL ABORT"
    exit 1
  fi
}
set -e

echo "HOST=${HOST}"
echo "PORT=${PORT}"

function testUrl() {
    url=$@
    if curl $url -ks -f -o /dev/null
    then
          echo "Ok"
          return 0
    else
          echo -n "not yet"
          return 1
    fi;
}

function waitForService() {
    url=$@
    echo -n "Wait for: $url... "
    n=0
    until testUrl $url
    do
        n=$((n + 1))
        if [[ $n == 100 ]]
        then
            echo " Give up"
            exit 1
        else
            sleep 6
            echo -n ", retry #$n "
        fi
    done
}

function testCompositeCreated() {

    # Expect that the Book Composite for bookId $BOK_ID_RAT_COM_BTN has been created with three ratings, three comments and three book theme nights
    if ! assertCurl 200 "curl $AUTH -k https://$HOST:$PORT/book-composite/$BOK_ID_RAT_COM_BTN -s"
    then
        echo -n "FAIL"
        return 1
    fi

    set +e
    assertEqual "$BOK_ID_RAT_COM_BTN" $(echo $RESPONSE | jq .movieId)
    if [ "$?" -eq "1" ] ; then return 1; fi

    assertEqual 3 $(echo $RESPONSE | jq ".comments | length")
    if [ "$?" -eq "1" ] ; then return 1; fi

    assertEqual 3 $(echo $RESPONSE | jq ".raatings | length")
    if [ "$?" -eq "1" ] ; then return 1; fi
    
    assertEqual 3 $(echo $RESPONSE | jq ".bookThemeNights | length")
    if [ "$?" -eq "1" ] ; then return 1; fi

    set -e
}

function waitForMessageProcessing() {
    echo "Wait for messages to be processed... "

    # Give background processing some time to complete...
    sleep 5

    n=0
    until testCompositeCreated
    do
        n=$((n + 1))
        if [[ $n == 40 ]]
        then
            echo " Give up"
            exit 1
        else
            sleep 3
            echo -n ", retry #$n "
        fi
    done
    echo "All messages are now processed!"
}

function recreateComposite() {
    local bookId=$1
    local composite=$2

    assertCurl 200 "curl -X DELETE http://$HOST:$PORT/book-composite/${bookId} -s"
    curl -X POST http://$HOST:$PORT/book-composite -H "Content-Type: application/json" --data "$composite"
}

function setupTestdata() {

    body=\
'{"bookId":1,"name":"Book 1","releaseDate":"2021-08-12","language":"Language 1", "ratings":[
        {"ratingId":1,"author":"Author 1","rating":1},
        {"ratingId":2,"author":"Author 2","rating":2},
        {"ratingId":3,"author":"Author 3","rating":3},
    ], "comments":[
        {"commentId":1,"author":"Author 1","content":"content 1"},
        {"commentId":2,"author":"Author 2","content":"content 2"},
        {"commentId":3,"author":"Author 3","content":"content 3"}
    ], "bookThemeNights":[
        {"bookThemeNightId":1,"name":"name 1","startDate":"2021-08-12", "location": "Location 1"},
        {"bookThemeNightId":2,"name":"name 2","startDate":"2021-08-12", "location": "Location 2"},
        {"bookThemeNightId":3,"name":"name 3","startDate":"2021-08-12", "location": "Location 3"}
    ]}'
    recreateComposite 1 "$body"


    body="{\"bookId\":$BOK_ID_NO_COM_NO_BTN"
    body+=\
',"name":"Book 1","releaseDate":"2021-08-12","language":"Language 1", "ratings":[
        {"ratingId":1,"author":"Author 1","rating":1},
        {"ratingId":2,"author":"Author 2","rating":2},
        {"ratingId":3,"author":"Author 3","rating":3},
]}'
    recreateComposite "$BOK_ID_NO_COM_NO_BTN" "$body"

    body="{\"bookId\":$BOK_ID_NO_RAT_NO_BTN"
    body+=\
',"bookId":1,"name":"Book 1","releaseDate":"2021-08-12","language":"Language 1", "comments":[
        {"commentId":1,"author":"Author 1","content":"content 1"},
        {"commentId":2,"author":"Author 2","content":"content 2"},
        {"commentId":3,"author":"Author 3","content":"content 3"}
    ]}'
    recreateComposite "$BOK_ID_NO_RAT_NO_BTN" "$body"
    
    body="{\"bookId\":$BOK_ID_RAT_COM_BTN"
    body+=\
',"name":"Book 1","releaseDate":"2021-08-12","language":"Language 1", "ratings":[
        {"ratingId":1,"author":"Author 1","rating":1},
        {"ratingId":2,"author":"Author 2","rating":2},
        {"ratingId":3,"author":"Author 3","rating":3},
    ], "comments":[
        {"commentId":1,"author":"Author 1","content":"content 1"},
        {"commentId":2,"author":"Author 2","content":"content 2"},
        {"commentId":3,"author":"Author 3","content":"content 3"}
    ], "bookThemeNights":[
        {"bookThemeNightId":1,"name":"name 1","startDate":"2021-08-12", "location": "Location 1"},
        {"bookThemeNightId":2,"name":"name 2","startDate":"2021-08-12", "location": "Location 2"},
        {"bookThemeNightId":3,"name":"name 3","startDate":"2021-08-12", "location": "Location 3"}
    ]}'
    recreateComposite "$BOK_ID_RAT_COM_BTN" "$body"
}

set -e

echo "Start:" `date`

echo "HOST=${HOST}"
echo "PORT=${PORT}"

if [[ $@ == *"start"* ]]
then
    echo "Restarting the test environment..."
    echo "$ docker-compose down"
    docker-compose down
    echo "$ docker-compose up -d"
    docker-compose up -d
fi

waitForService curl -k http://$HOST:$PORT/actuator/health

setupTestdata

waitForMessageProcessing

# Verify that a normal request works
assertCurl 200 "curl http://$HOST:$PORT/book-composite/$BOK_ID_RAT_COM_BTN -s"
assertEqual $BOK_ID_RAT_COM_BTN $(echo $RESPONSE | jq .bookId)
assertEqual 3 $(echo $RESPONSE | jq ".comments | length")
assertEqual 3 $(echo $RESPONSE | jq ".ratings | length")
assertEqual 3 $(echo $RESPONSE | jq ".bookThemeNights | length")

# Verify that a 404 (Not Found) error is returned for a non existing insuranceCompanyId 13
assertCurl 404 "curl http://$HOST:$PORT/book-composite/$BOK_ID_NOT_FOUND -s"

# Verify that no comments are returned for insuranceCompanyId 113
assertCurl 200 "curl http://$HOST:$PORT/book-composite/$BOK_ID_NO_COM_NO_BTN -s"
assertEqual $BOK_ID_NO_COM_NO_BTN $(echo $RESPONSE | jq .bookId)
assertEqual 0 $(echo $RESPONSE | jq ".comments | length")
assertEqual 3 $(echo $RESPONSE | jq ".ratings | length")
assertEqual 0 $(echo $RESPONSE | jq ".bookThemeNights | length")

# Verify that no ratings are returned for insuranceCompanyId 213
assertCurl 200 "curl http://$HOST:$PORT/book-composite/$BOK_ID_NO_RAT_NO_BTN -s"
assertEqual $BOK_ID_NO_RAT_NO_BTN $(echo $RESPONSE | jq .insuranceCompanyId)
assertEqual 3 $(echo $RESPONSE | jq ".comments | length")
assertEqual 0 $(echo $RESPONSE | jq ".ratings | length")
assertEqual 0 $(echo $RESPONSE | jq ".bookThemeNights | length")

# Verify that a 422 (Unprocessable Entity) error is returned for a bookId that is out of range (-1)
assertCurl 422 "curl http://$HOST:$PORT/book-composite/-1 -s"
assertEqual "\"Invalid book id: -1\"" "$(echo $RESPONSE | jq .message)"

# Verify that a 400 (Bad Request) error error is returned for a bookId that is not a number, i.e. invalid format
assertCurl 400 "curl http://$HOST:$PORT/book-composite/invalidBookId -s"
assertEqual "\"Type mismatch.\"" "$(echo $RESPONSE | jq .message)"


if [[ $@ == *"stop"* ]]
then
    echo "We are done, stopping the test environment..."
    echo "$ docker-compose down"
    docker-compose down
fi

echo "End:" `date`
