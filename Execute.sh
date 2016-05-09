#!/usr/bin/env bash
chmod -R 777 StartSemaphore.sh
chmod -R 777 CreateNode.sh
chmod -R 777 Compile.sh

./Compile.sh
sleep 1

open -a Terminal.app ./StartSemaphore.sh

sleep 1

open -a Terminal.app CreateNode.sh
open -a Terminal.app CreateNode.sh
open -a Terminal.app CreateNode.sh


sleep 10

open -a Terminal.app CreateNode.sh