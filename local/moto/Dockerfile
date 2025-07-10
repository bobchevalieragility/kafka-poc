FROM amazon/aws-cli:2.27.12

#RUN yum -y update && \
#    yum -y install gzip wget tar && \
#    wget https://archive.apache.org/dist/kafka/4.0.0/kafka_2.13-4.0.0.tgz && \
#    tar -xzf kafka_2.13-4.0.0.tgz

COPY config /root/.aws/config
#COPY brokernodegroupinfo.json .
COPY entrypoint.sh .

ENTRYPOINT ["./entrypoint.sh"]
