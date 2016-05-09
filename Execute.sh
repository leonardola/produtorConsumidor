#!/usr/bin/env bash
chmod -R 777 StartRmiServer.sh
chmod -R 777 CreateNode.sh
chmod -R 777 Compile.sh

./Compile.sh
#open -a Terminal.app StartRmiServer.sh
sleep 1
open -a Terminal.app CreateNode.sh
open -a Terminal.app CreateNode.sh