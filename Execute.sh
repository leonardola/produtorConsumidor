#!/usr/bin/env bash
chmod -R 777 StartRmiServer.sh
chmod -R 777 CreateNode.sh
chmod -R 777 Compile.sh

./Compile.sh
open -a Terminal.app StartRmiServer.sh
open -a Terminal.app CreateNode.sh