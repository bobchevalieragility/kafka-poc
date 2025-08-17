#!/bin/bash

docker run --volume "$(pwd):/workspace" --workdir /workspace bufbuild/buf breaking --against .git#branch=main,subdir=models/src/main/proto
