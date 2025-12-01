FROM debian:latest
LABEL authors="ryu"

RUN apt update && \
    apt install -y git, python3, pip3 && \
    apt clean && \
    rm -rf /var/lib/apt/lists/* \

RUN git clone https://github.com/gomtangmoru/bookstore.git && \
    cd bookstore && \
    pip install -r requirements.txt \ &&


ENTRYPOINT gunicorn -w 4 -b ${SERVERPORT}:${SERVERHOSTPORT} app:app