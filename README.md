# Project uses AWS CDK to create AWS DynamoDB and implements a Java-based CRUD API
# Project also illustrates correct usage of checked exceptions in Java indicating recoverable errors and semantics

# Project creates AWS artifacts:
#  - DynamoDB
#  - A sample Java-based REST API

# When deployed on desktop , issue requests to URLs:
#    POST - http://localhost:1025/order
#    PUT - http://localhost:1025/order
#    GET - http://localhost:1025/order/{order_id}
#    GET - http://localhost:1025/orders/customer/{customer_id}
#    DELETE - http://localhost:1025/order/{order_id}

An example payload for POST/PUT is:
{<br>
    "orderId" : "6",<br>
    "customerId" : "555",<br>
    "sku" : "110-rrgt555",<br>
    "quantity" : 5<br>
}

Requirements:
 - A command shell such as Git Bash
 - Python
 - CDK
 - Node JS/NPM for miscellaneous package installs
 - Supply your AWS account values in file application.yml for REST API
