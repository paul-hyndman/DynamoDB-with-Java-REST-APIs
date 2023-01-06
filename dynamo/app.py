#!/usr/bin/env python3
import os

from aws_cdk import core as cdk
from resource_stacks.dynamodb import DynamoDBStack

app = cdk.App()
dynamoDBStack = DynamoDBStack(app, "DynamoDBStackapp")
app.synth()
