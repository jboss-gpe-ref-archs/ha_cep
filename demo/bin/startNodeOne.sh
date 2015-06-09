#!/bin/sh
java -Djgroups.tcp.address=127.0.0.1 -Dorg.jboss.logging.provider=slf4j -Drhsummit2014.hornetq.client.id=rhsummit2014-hq-client-1 -jar ~/.m2/repository/org/jboss/ddoyle/rhsummit2014/hacepbrms/RHSummitHaCepApp/1.0.1/RHSummitHaCepApp-1.0.1.jar


