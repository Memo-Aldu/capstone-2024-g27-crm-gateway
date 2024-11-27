@echo off
set env_file=./.env

docker-compose --env-file %env_file% up --build
pause
