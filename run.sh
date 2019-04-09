#!/bin/bash
DIR=
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )"  >/dev/null 2>&1 &&  pwd )" || {
    echo "Failed to get current working directory."
    exit 1
}


if [ "$1" == "" ]; then
echo "Usage: run.sh jar_file_path [args]"
  exit 1
fi

jar_file="$1"
shift

config_file="${DIR}/hello-world.yml"

set -e

ADD_MODULES=""
if [ "$(java -version 2>&1 | head -1 | grep '\"1\.[78].\+\"')" = "" ]; then
  ADD_MODULES="--add-modules=java.xml.bind"
fi

container_name="jaegertracing/all-in-one"
container_version="1.11"
container="${container_name}:${container_version}"
container_id=
container_id="$(docker ps --filter "ancestor=${container}" --format='{{.ID}}')"

[[ -z "${container_id}" ]] && {
	${DIR}/start-container.sh "${container}" || {
		echo "ERROR: failed to start the docker container ${container}."
		exit 1
    }
	container_id="$(docker ps --filter "ancestor=${container}" --format='{{.ID}}')"
}

echo "INFO: Docker container ${container} (id: ${container_id}) is running."

container_ip="$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' ${container_id})"

export JAEGER_SAMPLER_TYPE=const 
export JAEGER_SAMPLER_PARAM=1 
export JAEGER_SAMPLER_MANAGER_HOST_PORT=${container_ip}:5778 
export JAEGER_REPORTER_LOG_SPANS=true 
export JAEGER_AGENT_HOST=${container_ip}
export JAEGER_AGENT_PORT=6831 
export JAEGER_REPORTER_FLUSH_INTERVAL=1000 
export JAEGER_REPORTER_MAX_QUEUE_SIZE=100 
export JAEGER_ENDPOINT=http://${JAEGER_AGENT_HOST}:14268/api/traces

echo "INFO: The Jaeger endpoint is ${JAEGER_ENDPOINT}."

java $ADD_MODULES -jar "${jar_file}" server "${config_file}" || {
	echo "ERROR: ${jar_file} failed."
	exit 1
}
echo "${jar_file} ran OK."
